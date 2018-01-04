package nxcs.testbed;

import nxcs.*;
import nxcs.common.MazeBase;

import java.awt.*;
import java.io.File;
import java.io.IOException;

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
    public ActionPareto getReward(String state, int action, double first_reward) {
        stepCount++;
        ActionPareto reward = new ActionPareto(new Qvector(-1, 0), 1);

        try {
            this.move(action);

            if (x == 1 && y == 6) {
                reward.setPareto(new Qvector(-1, 30));
                // resetPosition();
            }

            if (x == 6 && y == 1) {
                reward.setPareto(new Qvector(-1, 100));
                // resetPosition();
            }

            if (stepCount > 100) {
//                logger.info("stepCount>100:");
//                printOpenLocationClassifiers(0, this, null, null, first_reward);
                resetPosition();
//				action = -1;
//                logger.info("reset:" + "x:" + x + " y:" + y);
                reward.setPareto(new Qvector(-1, 0));//
            }
            if (this.isEndOfProblem(this.getState()))
                reward = this.positionRewards.get(new Point(this.x, this.y));
        } catch (Exception e) {
            logger.info(String.format("%s  %d", state, action));
            throw e;
        }

        return reward;
    }
}
