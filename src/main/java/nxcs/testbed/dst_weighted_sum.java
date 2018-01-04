/*
package nxcs.testbed;

import nxcs.*;
import nxcs.common.MazeBase;
import nxcs.moead.MOEAD;
import nxcs.stats.Snapshot;
import nxcs.stats.StepSnapshot;
import nxcs.stats.StepStatsLogger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

//import nxcs.Trace;

*/
/**
 * Represents a maze problem which is loaded in from a file. Such a file should
 * contain a rectangular array of 'O', 'T' and 'F' characters. A sample is given
 * in Mazes/Woods1.txt
 *//*

public class dst_weighted_sum extends MazeBase {

    */
/**
     * Loads a maze from the given maze file
     *
     * @param mazeFile The filename of the maze to load
     * @throws IOException On standard IO problems
     *//*

    public dst_weighted_sum(String mazeFile) throws IOException {
        super(new File(mazeFile));
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    */
/* return reward and action *//*


    public ActionPareto getReward(String state, int action, double first_reward) {
        ActionPareto reward = new ActionPareto(new Qvector(-1, 0), 1);

        try {
            this.move(action);

            if (x == 1 && y == 2) {
                reward.setPareto(new Qvector(-1, 10));
                // resetPosition();
            }

            if (x == 2 && y == 3) {
                reward.setPareto(new Qvector(-1, 50));
                // resetPosition();
            }
            if (x == 3 && y == 4) {
                reward.setPareto(new Qvector(-1, 200));
                // resetPosition();
            }
//
//            if (count > 100) {
////                logger.debug("count>100:");
//
//
////                printOpenLocationClassifiers(0, this, null, null, first_reward);
//
//
//                resetPosition();
////				action = -1;
////                logger.debug("reset:" + "x:" + x + " y:" + y);
//                reward.setPareto(new Qvector(-1, 0));//
//                // correct??????
//            }
        } catch (Exception e) {
            logger.debug(String.format("%s  %d", state, action));
            throw e;
        }

        return reward;
    }

}
*/
