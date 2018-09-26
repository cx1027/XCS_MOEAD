package nxcs.moead;

import nxcs.Classifier;
import nxcs.Environment;
import nxcs.NXCS;
import nxcs.NXCSParameters;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MOEAD {
    public int TotalItrNum;

    protected double[] idealpoint;

    public int numObjectives = 2;

//	public MoPopulation mainpop;


    public List<double[]> weights;
    protected List<int[]> neighbourTable;

    // parameters.
    public int popsize;

    public int neighboursize;

    private double F = 0.5;
    private double CR = 1;

    private Environment maze;

    public MOEAD(Environment maze) throws IOException {
        this.maze = maze;
    }
//	protected MoChromosome GeneticOPDE(int i) {
//		int k, l, m;
//		do
//			k = neighbourTable.get(i)[this.randomData.nextInt(0,
//					neighboursize - 1)];
//		while (k == i);
//		do
//			l = neighbourTable.get(i)[this.randomData.nextInt(0,
//					neighboursize - 1)];
//		while (l == k || l == i);
//		do
//			m = neighbourTable.get(i)[this.randomData.nextInt(0,
//					neighboursize - 1)];
//		while (m == l || m == k || m == i);
//
//		CMoChromosome chromosome1 = (CMoChromosome) this.mainpop
//				.getChromosome(k);
//		CMoChromosome chromosome2 = (CMoChromosome) this.mainpop
//				.getChromosome(l);
//		CMoChromosome chromosome3 = (CMoChromosome) this.mainpop
//				.getChromosome(m);
//
//		// generic operation crossover and mutation.
//		CMoChromosome offSpring = (CMoChromosome) this.createChromosome();
//		CMoChromosome current = (CMoChromosome) this.mainpop.getChromosome(i);
//		int D = offSpring.parDimension;
//		double jrandom = Math.floor(Math.random() * D);
//
//		for (int index = 0; index < D; index++) {
//			double value = 0;
//			if (Math.random() < CR || index == jrandom)
//				value = chromosome1.realGenes[index]
//						+ F
//						* (chromosome2.realGenes[index] - chromosome3.realGenes[index]);
//			else
//				value = current.realGenes[index];
//			// REPAIR.
//
//			double high = 1;
//			double low = 0;
//			if (value > high)
//				value = high;
//			else if (value < low)
//				value = low;
//
//			offSpring.realGenes[index] = value;
//		}
//
//		offSpring
//				.mutate(this.getRandomGenerator(), 1d / offSpring.parDimension);
//		return offSpring;
//	}
//
//	 protected MoChromosome GeneticOP(int i) {
//	 int k = neighbourTable.get(i)[this.randomData.nextInt(0,
//	 neighboursize - 1)];
//	 int l = neighbourTable.get(i)[this.randomData.nextInt(0,
//	 neighboursize - 1)];
//
//	 MoChromosome chromosome1 = this.mainpop.getChromosome(k);
//	 MoChromosome chromosome2 = this.mainpop.getChromosome(l);
//
//	 // generic operation crossover and mutation.
//	 MoChromosome offSpring1 = this.createChromosome();
//	 MoChromosome offSpring = this.createChromosome();
//
//	 GeneticOperators.realCrossover2(chromosome1, chromosome2, offSpring1,
//	 offSpring, this.getMultiObjectiveProblem().getDomain());
//
//	 GeneticOperators.realMutation2(offSpring, 1d / this
//	 .getMultiObjectiveProblem().getParameterSpaceDimension(), this
//	 .getMultiObjectiveProblem().getDomain());
//
//	 // update the archive;
//	 // updateArchive(offSpring);
//	 this.destroyChromosome(offSpring1);
//	 return offSpring;
//	 }

    protected void updateNeighbours(Classifier clcurrent, List<Classifier> moead_actionSet, double[] moeadWeight) {
        for (Classifier cl : moead_actionSet) {
              if(techScalarObj(moeadWeight, clcurrent)<techScalarObj(moeadWeight, cl)){
                  cl.prediction[0] = clcurrent.prediction[0];
                cl.prediction[1] = clcurrent.prediction[1];
              }

//            if (clcurrent.prediction[0] < cl.prediction[0] && clcurrent.prediction[1] > cl.prediction[1]) {
//                cl.prediction[0] = clcurrent.prediction[0];
//                cl.prediction[1] = clcurrent.prediction[1];
//            }
        }
    }

//	protected double updateCretia(int problemIndex, MoChromosome chrom) {
//		double[] ds = this.weights.get(problemIndex);
//		// return this.techScalarObj(ds, chrom);
//		return this.wsScalarObj(ds, chrom);
//	}

    protected void updateReference(Classifier cl) {
        //norm cl.prediciton
        double[] prediciton_norm= new double[]{stepNor(cl.prediction[0],100),rewardNor(cl.prediction[1],1000,0)};


        // update the idealpoint.
//        if (cl.prediction[0] < idealpoint[0])
//            idealpoint[0] = cl.prediction[0];
//        if (cl.prediction[1] > idealpoint[1])
//            idealpoint[1] = cl.prediction[1];


		for (int j = 0; j < numObjectives; j++)
			if (prediciton_norm[j] > idealpoint[j])
				idealpoint[j] = prediciton_norm[j];
    }

	protected double techScalarObj(double[] weight, Classifier cl) {
		double max_fun = -1 * Double.MAX_VALUE;
		double[] prediciton_norm= new double[]{stepNor(cl.prediction[0],100),rewardNor(cl.prediction[1],1000,0)};
		for (int n = 0; n < numObjectives; n++) {
			double diff = Math.abs(cl.prediction[n] - idealpoint[n]);
			double feval;
			if (weight[n] == 0)
				feval = 0.00001 * diff;
			else
				feval = diff * weight[n];
			if (feval > max_fun)
				max_fun = feval;
		}
		return max_fun;
	}

    // normalisation about steps
    public double stepNor(double q, double max) {
        return Math.abs((max + 1 - q) / max);
    }

    // normalisaton for final reward
    public double rewardNor(double q, double max, double min) {
        return (q - min) / (max - min);
    }

//	protected double wsScalarObj(double[] namda, MoChromosome var) {
//		double sum = 0;
//		for (int n = 0; n < numObjectives; n++) {
//			sum += (namda[n]) * var.objectivesValue[n];
//		}
//		return sum;
//	}
//
//	protected double pbiScalarObj(double[] namda, MoChromosome var) {
//		return 0;
//	}
//
//	protected void improve(int i, MoChromosome offSpring) {
//	}
//
//	protected boolean terminated() {
//		// condition on the iteration.
//		return (this.ItrCounter > this.TotalItrNum);
//
//		// condition on the evaluation.
//		// return (this.evaluationCounter > this.TotalEvaluation);
//	}

    public void initialize(List<Point> openLocations, NXCSParameters params, NXCS nxcs) {

        // loadConfiguration();
        idealpoint = new double[numObjectives];
        for (int i = 0; i < numObjectives; i++) {
            // min_indiv[i] = this.createChromosome();
            idealpoint[i] = 50;
            // evaluate(min_indiv[i]);
        }

        // initialize the weights;
        initWeight(this.popsize);

        //TODO: initialze cls for each state
//        nxcs.generateCoveringClassifierbyWeight(openLocations, weights, params, nxcs);
        // put the cls into a map
        initNeighbour();


    }





//	protected void moreInitialize() {
//		mainpop = this.createPopulation(popsize);
//		evaluate(mainpop);
//		for (int i = 0; i < mainpop.size(); i++)
//			updateReference(mainpop.getChromosome(i));
//	};

    // protected void loadConfiguration() {
    // MoeaConfiguration configuration = this.getConfiguration();
    // this.popsize = configuration.getIntegerParameter(
    // MoeaConfiguration.POPULATION_SIZE, 100);
    // this.neighboursize = configuration.getIntegerParameter(
    // TSMOEAConfiguration.NEIGHBOUR_SIZE, 20);
    // this.TotalItrNum = configuration.getIntegerParameter(
    // MoeaConfiguration.GENERATION_NUMBER, 500);
    // }

    protected void initNeighbour() {
        neighbourTable = new ArrayList<int[]>(popsize);

        double[][] distancematrix = new double[popsize][popsize];
        for (int i = 0; i < popsize; i++) {
            distancematrix[i][i] = 0;
            for (int j = i + 1; j < popsize; j++) {
                distancematrix[i][j] = Sorting.distance(weights.get(i), weights.get(j));
                distancematrix[j][i] = distancematrix[i][j];
            }
        }

        for (int i = 0; i < popsize; i++) {
            int[] index = Sorting.sorting(distancematrix[i]);
            int[] array = new int[neighboursize];
            System.arraycopy(index, 0, array, 0, neighboursize);
            neighbourTable.add(array);
        }
    }

    protected void initWeight(int m) {
        this.weights = new ArrayList<double[]>();
        for (int i = 0; i <= m; i++) {
            if (numObjectives == 2) {
                double[] weight = new double[2];
                weight[0] = i / (double) m;
                weight[1] = (m - i) / (double) m;
                this.weights.add(weight);
            } else if (numObjectives == 3) {
                for (int j = 0; j <= m; j++) {
                    if (i + j <= m) {
                        int k = m - i - j;
                        double[] weight = new double[3];
                        weight[0] = i / (double) m;
                        weight[1] = j / (double) m;
                        weight[2] = k / (double) m;
                        this.weights.add(weight);
                    }
                }
            }
        }
//        this.weights.add(new double[]{0.52,0.48});
//        this.weights.add(new double[]{0.54,0.46});
//        this.weights.add(new double[]{0.56,0.44});
//        this.weights.add(new double[]{0.58,0.42});
//        this.weights.add(new double[]{0.6,0.4});
//        this.weights.add(new double[]{0.62,0.38});
//        this.weights.add(new double[]{0.64,0.36});
//        this.weights.add(new double[]{0.66,0.34});
//        this.weights.add(new double[]{0.68,0.32});
//        this.weights.add(new double[]{0.7,0.3});
//        this.weights.add(new double[]{0.72,0.28});
//        this.weights.add(new double[]{0.74,0.26});
//        this.weights.add(new double[]{0.76,0.24});
//        this.weights.add(new double[]{0.78,0.22});
//        this.weights.add(new double[]{0.8,0.2});
//        this.weights.add(new double[]{0.82,0.18});
//        this.weights.add(new double[]{0.84,0.16});
//        this.weights.add(new double[]{0.86,0.14});
//        this.weights.add(new double[]{0.88,0.12});
//        this.weights.add(new double[]{0.9,0.1});
//        this.weights.add(new double[]{0.92,0.08});
//        this.weights.add(new double[]{0.94,0.06});
//        this.weights.add(new double[]{0.96,0.04});
//        this.weights.add(new double[]{0.98,0.02});
//        this.weights.add(new double[]{1,0});
//        this.weights.add(new double[]{0.52,0.48});
//        this.weights.add(new double[]{0.53,0.47});
//        this.weights.add(new double[]{0.54,0.46});
//        this.weights.add(new double[]{0.55,0.45});
//        this.weights.add(new double[]{0.56,0.44});
//        this.weights.add(new double[]{0.57,0.43});
//        this.weights.add(new double[]{0.58,0.42});
//        this.weights.add(new double[]{0.59,0.41});
//        this.weights.add(new double[]{0.6,0.4});
//        this.weights.add(new double[]{0.61,0.39});
//        this.weights.add(new double[]{0.62,0.38});
//        this.weights.add(new double[]{0.63,0.37});
//        this.weights.add(new double[]{0.64,0.36});
//        this.weights.add(new double[]{0.65,0.35});
//        this.weights.add(new double[]{0.66,0.34});
//        this.weights.add(new double[]{0.67,0.33});
//        this.weights.add(new double[]{0.68,0.32});
//        this.weights.add(new double[]{0.69,0.31});
//        this.weights.add(new double[]{0.7,0.3});
//        this.weights.add(new double[]{0.71,0.29});
//        this.weights.add(new double[]{0.72,0.28});
//        this.weights.add(new double[]{0.73,0.27});
//        this.weights.add(new double[]{0.74,0.26});
//        this.weights.add(new double[]{0.75,0.25});
//        this.weights.add(new double[]{0.76,0.24});
//        this.weights.add(new double[]{0.77,0.23});
//        this.weights.add(new double[]{0.78,0.22});

        this.popsize = this.weights.size();
    }

    public List<double[]> getWeights() {
        return weights;
    }

//	protected void doSolve() {
//		initialize();
//		while (!terminated()) {
//			for (int i = 0; i < popsize; i++) {
//				evolveNewInd(i);
//			}
//			ItrCounter++;
//			System.out.println("---------------Iteration " + ItrCounter+
//					"-----------------");
//		}
//	}

//	public void reset() {
//	}

//	protected void evolveNewInd(int i) {
//		//MoChromosome offSpring = GeneticOPDE(i);
//		MoChromosome offSpring = GeneticOP(i);
//		improve(i, offSpring);
//
//		this.evaluate(offSpring);
//
//		// update neighbours.
//		updateNeighbours(i, offSpring);
//		updateReference(offSpring);
//
//		// Always remember to destory the chromosome.
//		this.destroyChromosome(offSpring);
//	}


    public void evaluateAndUpdate(Classifier cl, List<Classifier> moead_actionSet, double[] moeadWeight) {

//		double[] cl_prediciton = this.evaluate(cl);

        // update neighbours
        updateReference(cl);
//        updateNeighbours(cl, moead_actionSet, moeadWeight);

        // TODO:Always remember to destory the chromosome.
//		this.destroyChromosome(offSpring);
    }

//	public double[] evaluate(Classifier cl) {
//		return cl.prediction;
//	};
//
//	public void evaluate(MoPopulation pop) {
//		int size = pop.size();
//		for (int i = 0; i < size; i++) {
//			MoChromosome chromosome = pop.getChromosome(i);
//			evaluate(chromosome);
//		}
//	}


//	public static void main(String[] args) {
//		System.out.println("Test Created");
//		MOEAD impl = new MOEAD();
//
//		MultiObjectiveProblem problem = ZDT1.getInstance(30);
//		System.out.println("Test Solving Started for: " + problem.getName());
//
//		impl.popsize = 100;
//		impl.neighboursize = 30;
//		impl.TotalItrNum = 250;
//
//		// impl.TotalItrNum = 10;
//		impl.solve(problem);
//
//		String filename = "D:/experiments/moead_" + problem.getName() + ".txt";
//		impl.mainpop.writeToFile(filename);
//		System.out.println("Test Solving End");
//	}
}
