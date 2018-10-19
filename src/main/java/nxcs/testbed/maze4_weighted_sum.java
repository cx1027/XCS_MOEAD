package nxcs.testbed;

import nxcs.*;
import nxcs.common.MazeBase;
import nxcs.stats.StepSnapshot;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class maze4_weighted_sum extends MazeBase {

    /**
     * Loads a maze from the given maze file
     *
     * @param mazeFile The filename of the maze to load
     * @throws IOException On standard IO problems
     */
    public maze4_weighted_sum(String mazeFile) throws IOException {
        super(new File(mazeFile));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    /* return reward and action */
    public ActionPareto getReward(String state, int action) {
        stepCount++;
        ActionPareto reward = new ActionPareto(new Qvector(-1, 0), 1);
        try {
            this.move(action);

            if (this.isEndOfProblem(this.getState()))
                reward = this.currentPositionReward.get(new Point(this.x, this.y));
        } catch (Exception e) {
            logger.info(String.format("%s  %d", state, action));
            throw e;
        }

        return reward;
    }

    public void move(int action)
    {
        super.move(action);

        if (stepCount > 100) {
            Point p = this.getCurrentLocation();
            this.resetPosition();
            logger.info(String.format("Cannot go to final state from: %s after 100 steps, reset to random position:%s", p, this.getCurrentLocation()));

        }
    }




    public Hashtable<String, Boolean> getOpenLocationExpectPaths(){
        Hashtable<String, Boolean> ht = new Hashtable<String, Boolean>();
        ht.put("(1-1)-(1-6)-7", true);
        ht.put("(1-1)-(6-1)-11", true);
        ht.put("(1-4)-(1-6)-4", true);
        ht.put("(2-1)-(1-6)-6", true);
        ht.put("(2-1)-(6-1)-10", true);
        ht.put("(2-2)-(1-6)-5", true);
        ht.put("(2-2)-(6-1)-9", true);
        ht.put("(2-3)-(1-6)-4", true);
        ht.put("(2-3)-(6-1)-8", true);
        ht.put("(2-4)-(1-6)-3", true);
        ht.put("(2-4)-(6-1)-7", true);
        ht.put("(2-5)-(1-6)-2", true);
        ht.put("(2-5)-(6-1)-8", true);
        ht.put("(2-6)-(1-6)-1", true);
        ht.put("(2-6)-(6-1)-9", true);
        ht.put("(3-2)-(1-6)-8", true);
        ht.put("(3-2)-(6-1)-6", true);
        ht.put("(3-4)-(1-6)-4", true);
        ht.put("(3-4)-(6-1)-6", true);
        ht.put("(3-6)-(1-6)-2", true);
        ht.put("(3-6)-(6-1)-8", true);
        ht.put("(4-1)-(1-6)-10", true);
        ht.put("(4-1)-(6-1)-2", true);
        ht.put("(4-3)-(1-6)-6", true);
        ht.put("(4-3)-(6-1)-4", true);
        ht.put("(4-4)-(1-6)-5", true);
        ht.put("(4-4)-(6-1)-5", true);
        ht.put("(4-5)-(1-6)-4", true);
        ht.put("(4-5)-(6-1)-6", true);
        ht.put("(4-6)-(1-6)-3", true);
        ht.put("(4-6)-(6-1)-7", true);
        ht.put("(5-1)-(1-6)-9", true);
        ht.put("(5-1)-(6-1)-1", true);
        ht.put("(5-2)-(1-6)-8", true);
        ht.put("(5-2)-(6-1)-2", true);
        ht.put("(1-4)-(6-1)-8", true);
        ht.put("(5-3)-(1-6)-7", true);
        ht.put("(5-3)-(6-1)-3", true);
        ht.put("(5-4)-(1-6)-6", true);
        ht.put("(5-4)-(6-1)-4", true);
        ht.put("(5-5)-(1-6)-5", true);
        ht.put("(5-5)-(6-1)-5", true);
        ht.put("(6-2)-(1-6)-9", true);
        ht.put("(6-2)-(6-1)-1", true);
        ht.put("(6-4)-(1-6)-7", true);
        ht.put("(6-4)-(6-1)-5", true);
        ht.put("(6-5)-(1-6)-6", true);
        ht.put("(6-5)-(6-1)-6", true);
        ht.put("(6-6)-(1-6)-7", true);
        ht.put("(6-6)-(6-1)-7", true);
        ht.put("(3-2)-(6-1)-10", true);
        ht.put("(3-2)-(1-6)-6", true);


        return ht;
    }


    @Override
    public List<double[]> getTraceWeight(List<double[]> traceWeights) {
        List<double[]> ret = new ArrayList<double[]>();
        ret.add(traceWeights.get(0));
        ret.add(traceWeights.get(1));
        ret.add(traceWeights.get(traceWeights.size()-1));
        return traceWeights;
    }




    @Override
    public boolean isTraceConditionMeet() {
        return (this.finalStateCount % this.mp.resultInterval == 0)
                || (this.mp.logLowerFinalState && ((this.finalStateCount < 5)
                        || (this.finalStateCount < 20 && this.finalStateCount % 5 == 0)
                        || (this.finalStateCount < 100 && this.finalStateCount % 10 == 0)))
                ;
    }


}
