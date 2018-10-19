
package nxcs.testbed;

import nxcs.ActionPareto;
import nxcs.Qvector;
import nxcs.common.MazeBase;
import nxcs.stats.StepSnapshot;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

//import nxcs.Trace;


/**
 * Represents a maze problem which is loaded in from a file. Such a file should
 * contain a rectangular array of 'O', 'T' and 'F' characters. A sample is given
 * in Mazes/Woods1.txt
 */

public class mmaze_weighted_sum extends MazeBase {


    /**
     * Loads a maze from the given maze file
     *
     * @param mazeFile The filename of the maze to load
     * @throws IOException On standard IO problems
     */

    public mmaze_weighted_sum(String mazeFile) throws IOException {
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

    @Override
    public boolean isTraceConditionMeet() {
        return (this.finalStateCount % this.mp.resultInterval == 0)
                || (this.mp.logLowerFinalState
                && ((this.finalStateCount < 20 && this.finalStateCount % 5 == 0)||(this.finalStateCount < 100 && this.finalStateCount % 10 == 0)))
                ;
    }

    public Hashtable<String, Boolean> getOpenLocationExpectPaths(){
        Hashtable<String, Boolean> ht = new Hashtable<String, Boolean>();
        ht.put("(1-1)-(5-1)-6", true);
        ht.put("(1-1)-(1-4)-3", true);
        ht.put("(1-2)-(5-1)-5", true);
        ht.put("(1-2)-(1-4)-2", true);
        ht.put("(1-3)-(1-4)-1", true);
        ht.put("(1-3)-(5-1)-6", true);
        ht.put("(10-15)-(13-15)-3", true);
        ht.put("(11-15)-(13-15)-2", true);
        ht.put("(11-16)-(13-15)-3", true);
        ht.put("(11-17)-(13-15)-4", true);
        ht.put("(11-18)-(13-15)-5", true);
        ht.put("(12-15)-(13-15)-1", true);
        ht.put("(12-16)-(13-15)-2", true);
        ht.put("(13-16)-(13-15)-1", true);
        ht.put("(13-17)-(13-15)-2", true);
        ht.put("(13-18)-(13-15)-3", true);
        ht.put("(13-19)-(13-15)-4", true);
        ht.put("(13-20)-(13-15)-5", true);
        ht.put("(2-1)-(5-1)-5", true);
        ht.put("(3-1)-(5-1)-4", true);
        ht.put("(3-1)-(1-4)-5", true);
        ht.put("(3-2)-(5-1)-3", true);
        ht.put("(3-3)-(5-1)-4", true);
        ht.put("(3-4)-(5-1)-5", true);
        ht.put("(4-1)-(5-1)-1", true);
        ht.put("(4-2)-(5-1)-2", true);
        ht.put("(5-10)-(5-11)-1", true);
        ht.put("(5-2)-(5-1)-1", true);
        ht.put("(5-3)-(5-1)-2", true);
        ht.put("(5-4)-(5-1)-3", true);
        ht.put("(5-5)-(5-1)-4", true);
        ht.put("(5-6)-(5-1)-5", true);
        ht.put("(5-8)-(9-8)-4", true);
        ht.put("(5-8)-(5-11)-3", true);
        ht.put("(5-9)-(9-8)-5", true);
        ht.put("(5-9)-(5-11)-2", true);
        ht.put("(6-8)-(9-8)-3", true);
        ht.put("(6-8)-(5-11)-4", true);
        ht.put("(7-10)-(9-8)-4", true);
        ht.put("(7-11)-(9-8)-5", true);
        ht.put("(7-8)-(9-8)-2", true);
        ht.put("(7-9)-(9-8)-3", true);
        ht.put("(8-8)-(9-8)-1", true);
        ht.put("(8-9)-(9-8)-2", true);
        ht.put("(9-10)-(9-8)-2", true);
        ht.put("(9-11)-(9-8)-3", true);
        ht.put("(9-12)-(9-8)-4", true);
        ht.put("(9-13)-(9-8)-5", true);
        ht.put("(9-15)-(13-15)-4", true);
        ht.put("(9-15)-(9-18)-3", true);
        ht.put("(9-16)-(13-15)-5", true);
        ht.put("(9-16)-(9-18)-2", true);
        ht.put("(9-17)-(9-18)-1", true);
        ht.put("(9-17)-(13-15)-6", true);
        ht.put("(9-9)-(9-8)-1", true);
        ht.put("(5-8)-(9-8)-6", true);
        ht.put("(6-8)-(9-8)-5", true);
        ht.put("(7-8)-(9-8)-4", true);
        ht.put("(9-15)-(13-15)-6", true);
        ht.put("(10-15)-(13-15)-5", true);
        ht.put("(10-15)-(9-18)-4", true);
        ht.put("(11-15)-(13-15)-4", true);
        ht.put("(2-1)-(1-4)-4", true);
        ht.put("(1-2)-(5-1)-7", true);
        ht.put("(5-9)-(9-8)-7", true);
        ht.put("(9-16)-(13-15)-7", true);

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


}