package nxcs.testbed;


import nxcs.ActionPareto;
import nxcs.NXCSParameters;
import nxcs.Qvector;
import nxcs.common.MazeBase;
import nxcs.common.MazeParameters;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class MazeRunner {

    public static void main(String[] args) throws IOException {

        MazeParameters mp = new MazeParameters();
        NXCSParameters np = new NXCSParameters();

        mp.totalTrailCount = 1;
        mp.finalStateUpperBound = 5000;
        mp.resultInterval = 4999;
        mp.logFolder = "log/maze1/csv/";
        mp.rewardFile= "rewards/maze6.json";
        mp.mazeFile= "data/maze6.txt";




        np.N = 6000;
        np.stateLength = 24;
        np.numActions = 4;
        np.rho0 = 1000;
        np.pHash = 0.0;
        np.gamma = 0.85;
        np.crossoverRate = 0.8;
        np.mutationRate = 0.04;
        np.thetaMNA = 4;
        np.thetaGA = 500;
        // np.thetaGA = 0;
        // np.e0 = 0.05;
        np.e0 = 0.05;
        np.thetaDel = 200;
        np.doActionSetSubsumption = false;
        np.doGASubsumption = false;

        //initialize weights
        np.weights = new ArrayList<Point>();
//            np.weights.add(new Point(0, 10));
//            np.weights.add(new Point(1, 9));
//            np.weights.add(new Point(2, 8));
//            np.weights.add(new Point(3, 7));
//            np.weights.add(new Point(4, 6));
        np.weights.add(new Point(5, 5));
//            np.weights.add(new Point(6, 4));
//            np.weights.add(new Point(7, 3));
//            np.weights.add(new Point(8, 2));
//            np.weights.add(new Point(9, 1));
//            np.weights.add(new Point(10, 0));

        //initialize reward
//            np.obj1 = new int[]{10, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
        np.obj1 = new int[]{100};


        MazeBase maze = new maze6_weighted_sum(mp.mazeFile);
        ClassLoader classLoader = new MazeRunner().getClass().getClassLoader();
        File file = new File(classLoader.getResource(mp.rewardFile).getFile());

        JSONParser parser = new JSONParser();
        Iterator<JSONObject> iterator = null;
        try {
            Object obj = parser.parse(new FileReader(file));

            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);
            System.out.println(jsonObject.get("name"));
            // loop array
            JSONArray msg = (JSONArray) jsonObject.get("rewards");
            iterator = msg.iterator();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        maze.initialize(mp, np, parseReward(iterator) ).run();
    }


    private static Hashtable<Point, ActionPareto> parseReward(Iterator<JSONObject> rewards)
    {
        Hashtable<Point, ActionPareto> ret = new Hashtable<Point, ActionPareto>();
        while (rewards.hasNext()) {
            JSONObject t = rewards.next();

            String[] loc =((String) t.get("location")).split("\\|");
            String[] reward =((String) t.get("reward")).split("\\|");

            Point p = new Point(Integer.parseInt(loc[0]), Integer.parseInt(loc[1]));
            ActionPareto qreward = new ActionPareto(new Qvector(Double.parseDouble(reward[0]), Double.parseDouble(reward[1])), 0);
            ret.put(p,qreward);
        }

        return ret;
    }
}