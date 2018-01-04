package nxcs.common;

import com.google.gson.Gson;
import nxcs.*;
import nxcs.moead.MOEAD;
import nxcs.stats.Snapshot;
import nxcs.stats.StepSnapshot;
import nxcs.stats.StepStatsLogger;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a maze problem which is loaded in from a file. Such a file should
 * contain a rectangular array of 'O', 'T' and 'F' characters. A sample is given
 * in Mazes/Woods1.txt
 */
public abstract class MazeBase implements Environment {

    private MazeParameters mp;
    private NXCSParameters np;

    public File mazeFile;
    protected int finalStateCount;

    private final static List<Snapshot> stats = new ArrayList<Snapshot>();
    public static List<Integer> act = new ArrayList<Integer>();
    /**
     * The current position of the agent in the maze
     */
    public int x, y;
    /**
     * The raw characters in the maze
     */
    private char[][] mazeTiles;
    /**
     * The map from characters to their binary encodings used in states and
     * conditions
     */
    private Map<Character, String> encodingTable;
    /**
     * A list of points representing locations we can safely move the agent to
     */
    private ArrayList<Point> openLocations;
    /**
     * A list of points representing the final states in the environment
     */
    public List<Point> finalStates;
    private ArrayList<Reward> rewardGrid;
    /**
     * A list which maps the indices to (delta x, delta y) pairs for moving the
     * agent around the environment
     */
    protected List<Point> actions;
    /**
     * The number of timesteps since the agent last discovered a final state
     */
    protected int stepCount;

    public Hashtable<Point, ActionPareto> positionRewards;

    protected final static Logger logger = Logger.getLogger(MazeBase.class);

    private Gson gson = new Gson();

    /**
     * Loads a maze from the given maze file
     *
     * @param mazeFile The filename of the maze to load
     * @throws IOException On standard IO problems
     */
    public MazeBase(String mazeFile) throws IOException {
        this(new File(mazeFile));
    }

    /**
     * Loads a maze from the given maze file
     *
     * @param f The file of the maze to load
     * @throws IOException On standard IO problems
     */
    public MazeBase(File f) throws IOException {
        this.mazeFile = f;
    }

    public void run() throws IOException {

        try {


//            NXCSParameters this.np = new NXCSParameters();
//            // Another set of parameters Woods1, Woods101
//
//            this.np.N = 6000;
//            this.np.stateLength = 24;
//            this.np.numActions = 4;
//            this.np.rho0 = 1000;
//            this.np.pHash = 0.0;
//            this.np.gamma = 0.85;
//            this.np.crossoverRate = 0.8;
//            this.np.mutationRate = 0.04;
//            this.np.thetaMNA = 4;
//            this.np.thetaGA = 500;
//            // this.np.thetaGA = 0;
//            // this.np.e0 = 0.05;
//            this.np.e0 = 0.05;
//            this.np.thetaDel = 200;
//            this.np.doActionSetSubsumption = false;
//            this.np.doGASubsumption = false;
//
//            //initialize weights
//            this.np.weights = new ArrayList<Point>();
////            this.np.weights.add(new Point(0, 10));
////            this.np.weights.add(new Point(1, 9));
////            this.np.weights.add(new Point(2, 8));
////            this.np.weights.add(new Point(3, 7));
////            this.np.weights.add(new Point(4, 6));
//            this.np.weights.add(new Point(5, 5));
////            this.np.weights.add(new Point(6, 4));
////            this.np.weights.add(new Point(7, 3));
////            this.np.weights.add(new Point(8, 2));
////            this.np.weights.add(new Point(9, 1));
////            this.np.weights.add(new Point(10, 0));
//
//            //initialize reward
////            this.np.obj1 = new int[]{10, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
//            this.np.obj1 = new int[]{100};


            //TODO:initialise and associate with lamda

//
//            //initalize for result output
//            ArrayList<Point> reward_CSV = new ArrayList<Point>();
//            for (int i = 0; i < this.np.obj1.length; i++) {
//                reward_CSV.add(new Point(this.np.obj1[i], 1000 - this.np.obj1[i]));
//            }

//             maze = new ("data/maze5.txt");


//            int finalStateCount = 1;
            boolean logged = false;
//            int resultInterval = 2499;
//            int numOfChartBars = 20;
            // ArrayList<Point> traceWeights = new ArrayList<Point>();
            // traceWeights.add(new Point(10, 90));
            // traceWeights.add(new Point(95, 5));

            // picture: finalStateUpperBound / 20) / 10 * 10 should be 20
            int chartXInterval = ((this.mp.finalStateUpperBound / this.mp.numOfChartBars) > 10)
                    ? (this.mp.finalStateUpperBound / this.mp.numOfChartBars) / 10 * 10 : 10;

            //Loop weights
            for (Point pweight : this.np.weights) {
                double[] weight = new double[]{pweight.getX(), pweight.getY()};

                //Loop:diff final reward for obj1
                for (int obj_num = 0; obj_num < this.np.obj1.length; obj_num++) {

                    //set reward for each round
                    double first_Freward = this.np.obj1[obj_num];
                    double second_Freward = 1000 - this.np.obj1[obj_num];

                    //how many times a same setting run, then to avg for the result
                    //totalCalcTimes:how many runs want to avg, here set 1 to ignor this loop
                    for (int trailIndex = 0; trailIndex < this.mp.totalTrailCount; trailIndex++) {
                        NXCS nxcs = new NXCS(this, this.np);

                        //initialize MOEAD
                        MOEAD moeadObj = new MOEAD(this);
                        moeadObj.popsize = 25;
                        moeadObj.neighboursize = 3;
                        moeadObj.TotalItrNum = 250;
                        moeadObj.initialize(this.openLocations, this.np, nxcs);
                        nxcs.setMoead(moeadObj);

                        nxcs.generateCoveringClassifierbyWeight(this.openLocations, moeadObj.weights, this.np);

                        int stepi = 1;
                        this.resetPosition();

                        this.finalStateCount = 1;

                        // clear stats
                        stats.clear();

                        StepStatsLogger stepLogger = new StepStatsLogger(chartXInterval, 0);
                        StepStatsLogger stepLogger_test = new StepStatsLogger(chartXInterval, 0);

                        logger.info(String.format("######### begin to run of: Weight:%s - first reward:%s - Trail#: %s ",
                                        weight, this.np.obj1[obj_num], trailIndex));


                        while (this.finalStateCount < this.mp.finalStateUpperBound) {

//                                logger.info("final State Count:" + finalStateCount);

                            nxcs.runIteration(finalStateCount, this.getState(), weight, stepi, this.np.obj1[obj_num], moeadObj.getWeights());


                            stepi++;


                            if (finalStateCount > 2497) {
                                //logger.info("print classifiers at finalstatecount: " + finalStateCount);
                                this.printOpenLocationClassifiers(finalStateCount, nxcs, weight, this.np.obj1[obj_num]);
                            }

                            if (this.isEndOfProblem(this.getState())) {
//                                    this.resetPosition();
//								logger.info("goalstate*************************");
//                                this.resetToSamePosition(new Point(1, 1));
                                this.resetPosition();
                                finalStateCount++;
                                logged = false;
                                // logger.info(finalStateCount);
                            }

                            // analyst results
                            // if (((finalStateCount % resultInterval ==
                            // 0)||(finalStateCount<100)) && !logged) {
                            if ((finalStateCount % this.mp.resultInterval == 0) && !logged) {
                                // test algorithem
                                logger.info("testing process: Trained on " + finalStateCount + " final states");

                                int[] actionSelect = null;

                                for (double[] test_weight : moeadObj.weights) {

                                    Integer testStepCount = 0;
                                    int totalTestStepCount = 0;

                                    logger.info(String.format("Test on  weight: %f, %f ", test_weight[0], test_weight[1]));


                                    //testing process for 4 open states from (2,1)
                                    actionSelect = new int[this.openLocations.size()];
                                    int logFlag = 0;
                                    int resetPoint = 0;

                                    this.resetToSamePosition(this.openLocations.get(testStepCount));
                                    while (testStepCount < this.openLocations.size()) {
                                        String state = this.getState();
                                        logger.info(String.format("@1 Test:%d, Steps:%d, state:%s", resetPoint, logFlag, this.getxy()));
                                        int action = nxcs.classify(state, test_weight);

                                        if (logFlag == 0) {
                                            actionSelect[resetPoint] = action;
                                        }
                                        // logger.info("choose action");
                                        logFlag++;
                                        //TODO:return the PA1[action]
//                                        logger.info(String.format("@2 Timestamp:%d, test:%d, resetPoint:%d, logFlag:%d, state:%s", timestamp, test, resetPoint, logFlag, this.getxy()));

                                        double selectedPA_reward = nxcs.getSelectPA(action, state);

                                        ActionPareto r = this.getReward(state, action, first_Freward);

                                        // logger.info("take testing:");
                                        if (this.isEndOfProblem(this.getState())) {
                                            logger.info(String.format("@3 Test:%d, Steps:%d, state:%s", resetPoint, logFlag, this.getxy()));
                                            testStepCount++;
                                            if (testStepCount < this.openLocations.size()) {
                                                Point testPoint = this.openLocations.get(testStepCount); //this.getTestLocation(test, testLocations);
                                                resetPoint++;

                                                this.resetToSamePosition(testPoint);
                                                logger.info(String.format("Reset to Test:%d, resetPoint:%d, testLocation:%s", testStepCount, testStepCount, testPoint));
                                                logFlag = 0;
                                            }
                                        }
                                        totalTestStepCount++;
                                    }


                                    //TODO:write first_selected_PA in CSV
                                    /*************
                                     * stepLogger for testing
                                     * stepLogger.add(this.traceOpenLocations(finalStateCount, trace, nxcs, this.np));
                                     *****/
                                    ArrayList<StepSnapshot> testStats = new ArrayList<StepSnapshot>();


                                    testStats.addAll(this.GetTestingPAResultInCSV(trailIndex, finalStateCount, nxcs, weight, first_Freward, actionSelect));

                                    stepLogger_test.add(testStats);


                                    finalStateCount++;

                                    logger.info("avg steps:" + ((double) (totalTestStepCount)) / testStepCount);


                                    //TODO:print reward_l(PA[1]),reward_r(PA[2]),deltaReward and maxReward
//                                    this.printOpenLocationClassifiers(finalStateCount, nxcs, weight, this.np.obj1[obj_num]);


                                    /*************
                                     * stepLogger for training
                                     * stepLogger.add(this.traceOpenLocations(finalStateCount, trace, nxcs, this.np));
                                     *****/
                                    ArrayList<StepSnapshot> trailStats = new ArrayList<StepSnapshot>();


                                    trailStats.addAll(this.GetTrainingPAResultInCSV(trailIndex, finalStateCount, nxcs, weight, first_Freward));
                                    stepLogger.add(trailStats);

                                    logger.info(String.format("End of %d/%d,  weight: %f, %f", finalStateCount, this.mp.finalStateUpperBound, test_weight[0], test_weight[1]));
                                }//loop test weight

                                logged = true;

                            }//for log
                        } // endof z loop

                        //write result to csv
                        stepLogger.writeLogAndCSVFiles(
//                                    String.format("log/%s/%s/%s - %s - Trial %d - <TRIAL_NUM> - %d.csv", "MOXCS",
//                                    "MAZE4", weight, this.np.obj1[obj_num], trailIndex, this.np.N),
                                String.format("log/%s/%s - %s - Trial %d - TRIAL_NUM - %d.csv", "MOXCS",
                                        "Train", this.mp.fileTimestampFormat, trailIndex, this.np.N),
                                String.format("log/datadump/%s - %s - Trail %d-<TIMESTEP_NUM> - %d.log", "MOXCS",
                                        this.np.obj1[obj_num], trailIndex, this.np.N));
                        stepLogger_test.writeLogAndCSVFiles_TESTING(
                                String.format("log/%s/%s - %s - Trial %d - TRIAL_NUM - %d - TEST.csv", "MOXCS",
                                        "Train", this.mp.fileTimestampFormat, trailIndex, this.np.N),
                                String.format("log/datadump/%s - %s - Trail %d-<TIMESTEP_NUM> - %d.log", "MOXCS",
                                        this.np.obj1[obj_num], trailIndex, this.np.N));
                        logger.info("End of trail:" + trailIndex);
                    } // totalTrailCount loop

                    logger.info(String.format("End of %d/%d, objective: %d, %d", finalStateCount, this.mp.finalStateUpperBound, obj_num, this.np.obj1[obj_num]));
                } // action selection loop

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                //writer.close();
            } catch (Exception e) {
            }
        } // endof try
    }

    public List<Reward> getRewardGrid() {
        return rewardGrid;
    }

    /**
     * Resets the agent to a random open position in the environment
     */
    public void resetPosition() {
        Point randomOpenPoint = XienceMath.choice(openLocations);
        x = randomOpenPoint.x;
        y = randomOpenPoint.y;
        stepCount = 0;
        // x = 1;
        // y = 1;
        // stepCount = 0;
    }

    public void resetToSamePosition(Point xy) {
        x = xy.x;
        y = xy.y;
        stepCount = 0;
        // x = 1;
        // y = 1;
        // stepCount = 0;
    }


    /**
     * Returns the two-bit encoding for the given position in the maze
     *
     * @param x The x position in the maze to get
     * @param y The y position in the maze to get
     * @return The two-bit encoding of the given position
     */
    private String getEncoding(int x, int y) {
        if (x < 0 || y < 0 || y >= mazeTiles.length || x >= mazeTiles[0].length) {
            return encodingTable.get(null);
        } else {
            return encodingTable.get(mazeTiles[y][x]);

        }
    }

    /**
     * Calculates the 16-bit state for the given position, from the 8 positions
     * around it
     *
     * @param x The x position of the state to get the encoding for
     * @param y The y position of the state to get the encoding for
     * @return The binary representation of the given state
     */
    public String getStringForState(int x, int y) {
        StringBuilder build = new StringBuilder();
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0)
                    continue;
                build.append(getEncoding(x + dx, y + dy));
            }
        }
        return build.toString();
    }

    /**
     * Checks whether the given position is a valid position that the agent can
     * be in in this this. A position is valid if it is inside the bounds of the
     * maze and is not a tree (T)
     *
     * @param x The x position to check
     * @param y The y position to check.
     * @return True if the given (x, y) position is a valid position in the maze
     */
    protected boolean isValidPosition(int x, int y) {
        return !(x < 0 || y < 0 || y >= mazeTiles.length || x >= mazeTiles[0].length) && mazeTiles[y][x] != 'T' && mazeTiles[y][x] != 'N';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
//        logger.info(String.format("x,y:%d %d", x, y));
        return getStringForState(x, y);
    }

    public Point getxy() {
        return new Point(x, y);
    }

    // public Qvector getReward(String state, int action) {
    // stepCount++;
    // Qvector reward = new Qvector(-1, 0);
    //
    // Point movement = actions.get(action);
    // if (isValidPosition(x + movement.x, y + movement.y)) {
    // x += movement.x;
    // y += movement.y;
    // }
    //
    // if (x == 1 && y == 1) {
    // reward.setQvalue(-1, 1);
    // // resetPosition();
    // }
    //
    // if (x == 1 && y == 6) {
    // reward.setQvalue(-1, 10);
    // // resetPosition();
    // }
    //
    // if (stepCount > 100) {
    // resetPosition();
    // action=-1;//???
    // reward.setQvalue(-1, 0);
    // }
    //
    // return reward;
    // }


    public abstract ActionPareto getReward(String state, int action, double first_reward);

    public boolean isEndOfProblem(String state) {
        for (Point finalState : finalStates) {
            if (getStringForState(finalState.x, finalState.y).equals(state)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<ArrayList<StepSnapshot>> traceReward(int exp_repeat, int timeStamp, NXCS nxcs,
                                                           NXCSParameters params, double[] weight, double first_reward) {
        // stats variables
        ArrayList<ArrayList<StepSnapshot>> locStats = new ArrayList<ArrayList<StepSnapshot>>();
        for (Point p : this.openLocations) {
            this.resetToSamePosition(p);
            String startState = this.getState();
            ArrayList<StepSnapshot> trc = GetTrainingPAResultInCSV(exp_repeat, timeStamp, nxcs, weight, first_reward);
            // ArrayList<StepSnapshot> trc = trace.traceStart(startState, nxcs);
            trc.stream().forEach(x -> x.setTimestamp(timeStamp));
            locStats.add(trc);
        }
        return locStats;
    }

    public void printOpenLocationClassifiers(int timestamp, NXCS nxcs, double[] weight, double obj_r1) {
        logger.info("R1 is:" + obj_r1 + " R2 is:" + (1000 - obj_r1));

        for (Point p : this.openLocations) {
            logger.info(String.format("%d\t location:%d,%d", timestamp, (int) p.getX(), (int) p.getY()));


            List<Classifier> C = nxcs.generateMatchSet(this.getStringForState(p.x, p.y));

            double[] PA1 = nxcs.generatePredictions(C, 0);
            for (int i = 0; i < PA1.length; i++) {
                logger.info("PA1[" + i + "]:" + PA1[i]);
            }
            double[] PA2 = nxcs.generatePredictions(C, 1);
            for (int i = 0; i < PA2.length; i++) {
                logger.info("PA2[" + i + "]:" + PA2[i]);
            }
            double[] PA = nxcs.generateTotalPredictions_Norm(C, weight);
            for (int i = 0; i < PA.length; i++) {
                logger.info("PAt[" + i + "]:" + PA[i]);
            }

            //Q_finalreward
//            double Q_finalreward_left = PA1[1];
//            double Q_finalreward_right = PA1[2];
//            double Q_finalreward_delta = PA1[1] - PA1[2];
//            double Q_finalreward_max = 0;
//            if (PA1[1] > PA1[2]) {
//                Q_finalreward_max = PA1[1];
//            } else {
//                Q_finalreward_max = PA1[2];
//            }
//
//            //Q_steps
//            double Q_steps_left = PA2[1];
//            double Q_steps_right = PA2[2];
//            double Q_steps_delta = PA2[1] - PA2[2];
//            double Q_steps_min = 0;
//            if (PA2[1] > PA2[2]) {
//                Q_steps_min = PA2[2];
//            } else {
//                Q_steps_min = PA2[1];
//            }

            //Q_weighted sum value for different weights
            //TODO:Q_weighted sum value for different weights


            for (int action : act) {

                List<Classifier> A = C.stream().filter(b -> b.action == action && b.weight_moead[1] == 0).collect(Collectors.toList());
                Collections.sort(A, new Comparator<Classifier>() {
                    @Override
                    public int compare(Classifier o1, Classifier o2) {
                        return o1.fitness == o2.fitness ? 0 : (o1.fitness > o2.fitness ? 1 : -1);
                    }
                });

                logger.info("action:" + action);
                // TODO:
                // 1.why not print fitness of cl???????
                // 2.print PA for each state to see if PA correct
                logger.info(A);

            }

        } // open locations
    }


    public void generateCoveringClassifierbyWeight(List<Point> openLocations, List<double[]> weights, NXCSParameters params) {
//		assert (state != null && matchSet != null) : "Invalid parameters";
//		assert (state.length() == this.np.stateLength) : "Invalid state length";

        for (Point location : openLocations) {

            String state = getStringForState(location.x, location.y);

            for (int act = 0; act < 4; act++) {
                for (int w = 0; w < weights.size(); w++) {
                    Classifier clas = new Classifier(this.np, state);
//				Set<Integer> usedActions = matchSet.stream().map(c -> c.action).distinct().collect(Collectors.toSet());
//				Set<Integer> unusedActions = IntStream.range(0, this.np.numActions).filter(i -> !usedActions.contains(i)).boxed()
//						.collect(Collectors.toSet());
                    clas.action = act;
                    clas.timestamp = 0;//TODO: timestamp;
                    clas.setWeight_moead(weights.get(w));
                }
            }
        }

    }

    public ArrayList<StepSnapshot> GetTestingPAResultInCSV(int experiment_num, int timestamp, NXCS nxcs, double[] weight, double obj_r1, int[] ActionSelect) {

        ArrayList<StepSnapshot> PAresult = new ArrayList<StepSnapshot>();


        for (int p = 0; p < this.openLocations.size(); p++) {

            Point point = new Point(p + 2, 1);

            List<Classifier> C = nxcs.generateMatchSet(this.getStringForState(this.openLocations.get(p).x, this.openLocations.get(p).y));
            double[] PA1 = nxcs.generatePredictions(C, 0);

            double[] PA2 = nxcs.generatePredictions(C, 1);

            double[] PA1_nor = new double[4];
            double[] PA2_nor = new double[4];

            //normalisation
            for (int i = 0; i < PA1.length; i++) {
                PA1_nor[i] = nxcs.stepNor(PA1[i], 100);
            }
            for (int i = 0; i < PA2.length; i++) {
                PA2_nor[i] = nxcs.rewardNor(PA2[i], 1000, 0);
            }

            double[] PAt = nxcs.getTotalPrediciton(weight, PA1_nor, PA2_nor);

            //Q_finalreward
            double Q_finalreward_left = PA1[1];
            double Q_finalreward_right = PA1[2];
            double Q_finalreward_delta = PA1[1] - PA1[2];
            double Q_finalreward_max = 0;
            if (PA1[1] > PA1[2]) {
                Q_finalreward_max = PA1[1];
            } else {
                Q_finalreward_max = PA1[2];
            }


            //Q_steps
            double Q_steps_left = PA2[1];
            double Q_steps_right = PA2[2];
            double Q_steps_delta = PA2[1] - PA2[2];
            double Q_steps_min = 0;
            if (PA2[1] > PA2[2]) {
                Q_steps_min = PA2[2];
            } else {
                Q_steps_min = PA2[1];
            }

            double Q_total_left = PAt[1];
            double Q_total_right = PAt[2];


            double Q_finalreward_select = PA1[ActionSelect[p]];
            double Q_steps_select = PA2[ActionSelect[p]];
            double Q_total_select = PAt[ActionSelect[p]];

            //int exp_repeat, int finalCount, Point openState, double Q_finalreward_left, double Q_finalreward_right,double Q_finalreward_delta,double Q_finalreward_max, double Q_steps_left, double Q_steps_right,double Q_steps_delta,double Q_steps_max,ArrayList<Point> path) {

            StepSnapshot result_row = new StepSnapshot(experiment_num, timestamp, weight, obj_r1, point, Q_finalreward_left, Q_finalreward_right, Q_finalreward_delta, Q_finalreward_max, Q_steps_left, Q_steps_right, Q_steps_delta, Q_steps_min, Q_total_left, Q_total_right, Q_finalreward_select, Q_steps_select, Q_total_select);

            PAresult.add(result_row);
            //Q_weighted sum value for different weights
            //TODO:Q_weighted sum value for different weights


        } // open locations
        return PAresult;
    }

    public ArrayList<StepSnapshot> GetTrainingPAResultInCSV(int experiment_num, int timestamp, NXCS nxcs, double[] weight, double obj_r1) {

        ArrayList<StepSnapshot> PAresult = new ArrayList<StepSnapshot>();


        for (Point p : this.openLocations) {

            List<Classifier> C = nxcs.generateMatchSet(this.getStringForState(p.x, p.y));
            double[] PA1 = nxcs.generatePredictions(C, 0);

            double[] PA2 = nxcs.generatePredictions(C, 1);

            //Q_finalreward
            double Q_finalreward_left = PA1[1];
            double Q_finalreward_right = PA1[2];
            double Q_finalreward_delta = PA1[1] - PA1[2];
            double Q_finalreward_max = 0;
            if (PA1[1] > PA1[2]) {
                Q_finalreward_max = PA1[1];
            } else {
                Q_finalreward_max = PA1[2];
            }

            //Q_steps
            double Q_steps_left = PA2[1];
            double Q_steps_right = PA2[2];
            double Q_steps_delta = PA2[1] - PA2[2];
            double Q_steps_max = 0;
            if (PA2[1] > PA2[2]) {
                Q_steps_max = PA2[1];
            } else {
                Q_steps_max = PA2[2];
            }

            //int exp_repeat, int finalCount, Point openState, double Q_finalreward_left, double Q_finalreward_right,double Q_finalreward_delta,double Q_finalreward_max, double Q_steps_left, double Q_steps_right,double Q_steps_delta,double Q_steps_max,ArrayList<Point> path) {

            StepSnapshot result_row = new StepSnapshot(experiment_num, timestamp, weight, obj_r1, p, Q_finalreward_left, Q_finalreward_right, Q_finalreward_delta, Q_finalreward_max, Q_steps_left, Q_steps_right, Q_steps_delta, Q_steps_max);

            PAresult.add(result_row);
            //Q_weighted sum value for different weights
            //TODO:Q_weighted sum value for different weights


        } // open locations
        return PAresult;
    }


    public ArrayList<ArrayList<StepSnapshot>> getOpenLocationExpectPaths() {
        ArrayList<ArrayList<StepSnapshot>> expect = new ArrayList<ArrayList<StepSnapshot>>();
        ArrayList<StepSnapshot> e21 = new ArrayList<StepSnapshot>();
        e21.add(new StepSnapshot(new Point(2, 1), new Point(1, 1), 1));
        e21.add(new StepSnapshot(new Point(2, 1), new Point(6, 1), 4));
        expect.add(e21);
        ArrayList<StepSnapshot> e31 = new ArrayList<StepSnapshot>();
        e31.add(new StepSnapshot(new Point(3, 1), new Point(1, 1), 2));
        e31.add(new StepSnapshot(new Point(3, 1), new Point(6, 1), 3));
        expect.add(e31);
        ArrayList<StepSnapshot> e41 = new ArrayList<StepSnapshot>();
        e41.add(new StepSnapshot(new Point(4, 1), new Point(6, 1), 2));
        expect.add(e41);
        ArrayList<StepSnapshot> e51 = new ArrayList<StepSnapshot>();
        e51.add(new StepSnapshot(new Point(5, 1), new Point(6, 1), 1));
        expect.add(e51);

        return expect;
    }


    private HashMap<Integer, Point> getTestLocation() {
        HashMap<Integer, Point> ret = new HashMap<Integer, Point>();
        ret.put(0, new Point(2, 1));
        ret.put(1, new Point(3, 1));
        ret.put(2, new Point(4, 1));
        ret.put(3, new Point(5, 1));
        return ret;
    }

    private Point getTestLocation(Integer test, HashMap<Integer, Point> locations) {
        if (locations.containsKey(test))
            return locations.get(test);
        else
            return null;
    }


    public MazeBase initialize(MazeParameters mp, NXCSParameters np, Hashtable<Point, ActionPareto> positionRewards) throws IOException {
        logger.info("\n\n=================================================================");

        this.mp = mp;
        this.np = np;
        this.positionRewards = positionRewards;

        // Set up the encoding table FOR DST
        encodingTable = new HashMap<Character, String>();
        encodingTable.put('O', "000");
        encodingTable.put('T', "110");
        encodingTable.put(null, "100");// For out of the maze positions
        encodingTable.put('F', "111");
        encodingTable.put('N', "011");

        // encodingTable.put('1', "001");
        // encodingTable.put('3', "011");
        // encodingTable.put('5', "101");
        // encodingTable.put('8', "010");

        openLocations = new ArrayList<Point>();
        finalStates = new ArrayList<Point>();
        rewardGrid = new ArrayList<Reward>();

        act = new ArrayList<>();
        act.add(0);
        act.add(1);
        act.add(2);
        act.add(3);

        actions = new ArrayList<Point>();
        actions.add(new Point(0, -1));// Up
        actions.add(new Point(-1, 0));// Left
        actions.add(new Point(1, 0));// Right
        actions.add(new Point(0, 1));// Down
        // actions.add(new Point(-1, -1));// Up, Left
        // actions.add(new Point(1, -1));// Up, Right
        // actions.add(new Point(-1, 1));// Down, Left
        // actions.add(new Point(1, 1));// Down, Right

        // Load the maze into a char array
        List<String> mazeLines = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(this.mazeFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                mazeLines.add(line);
            }
        } catch (Exception e) {
            throw new IOException(String.format("Failed to load file:%s", this.mazeFile));
        }
        mazeTiles = new char[mazeLines.size()][];
        for (int i = 0; i < mazeLines.size(); i++) {
            mazeTiles[i] = mazeLines.get(i).toCharArray();
            if (i > 0 && mazeTiles[i].length != mazeTiles[1].length) {
                throw new IllegalArgumentException(
                        String.format("Line %d in file %s is of different length than the others", i + 1, this.mazeFile.getName()));
            }

            for (int j = 0; j < mazeTiles[i].length; j++) {
                char c = mazeTiles[i][j];
                if (!encodingTable.containsKey(c)) {
                    throw new IllegalArgumentException(
                            String.format("Line %d in file %s has an invalid character %c", i + 1, this.mazeFile.getName(), c));
                }

                if (c == 'O') {
                    openLocations.add(new Point(j, i));
                    rewardGrid.add(new Reward(new Point(j, i), -1, 0));
//				} else if (c != 'O' && c != 'T') {
                } else if (c == 'F') {
                    finalStates.add(new Point(j, i));
                    rewardGrid.add(new Reward(new Point(j, i), -1, Character.getNumericValue(c)));
                }
            }

        }

//        logger.debug("rewardGrid:" + rewardGrid);
//        logger.info("rewards:" + this.positionRewards);
        logger.info("===========Maze Parameters============\t" + gson.toJson(this.mp));
        logger.info("===========NXCS Parameters============\t" + gson.toJson(this.np));
        logger.info("===========Open Locations=============\t:" + openLocations);
        logger.info("===========Final States===============\t:" + finalStates);
        logger.info("===========Position Rewards===========\t" + gson.toJson(this.positionRewards));

        return this;
    }

    public Point getCurrentLocation() {
        return new Point(x, y);
    }

    public ArrayList<Point> getOpenLocations() {
        return this.openLocations;
    }

    public void setPositionRewards(Hashtable<Point, ActionPareto> pr) {
        this.positionRewards = pr;
    }

    /***
     * move to next valid location
     * @param action
     */
    public void move(int action) {
        Point movement = actions.get(action);
        if (isValidPosition(x + movement.x, y + movement.y)) {
            x += movement.x;
            y += movement.y;
            stepCount++;
        }
    }
}
