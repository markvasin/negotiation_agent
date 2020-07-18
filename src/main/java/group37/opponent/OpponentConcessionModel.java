package group37.opponent;

import agents.Jama.Matrix;
import agents.uk.ac.soton.ecs.gp4j.bmc.BasicPrior;
import agents.uk.ac.soton.ecs.gp4j.bmc.GaussianProcessMixture;
import agents.uk.ac.soton.ecs.gp4j.bmc.GaussianProcessMixturePrediction;
import agents.uk.ac.soton.ecs.gp4j.bmc.GaussianProcessRegressionBMC;
import agents.uk.ac.soton.ecs.gp4j.gp.covariancefunctions.CovarianceFunction;
import agents.uk.ac.soton.ecs.gp4j.gp.covariancefunctions.Matern3CovarianceFunction;
import agents.uk.ac.soton.ecs.gp4j.gp.covariancefunctions.NoiseCovarianceFunction;
import agents.uk.ac.soton.ecs.gp4j.gp.covariancefunctions.SumCovarianceFunction;
import group37.util.Pair;

import java.util.Arrays;
import java.util.List;

public class OpponentConcessionModel {

    private GaussianProcessRegressionBMC regression;
    private GaussianProcessMixture predictor;
    private int timeSlots;
    private int currentTimeSlot;
    private double maxUtilityInTimeSlot;
    private Matrix timePeriod;
    private Matrix currentMeans;
    private Matrix currentVariance;

    public OpponentConcessionModel(int timeSlots) {

        List<BasicPrior> basicPriors = Arrays.asList(
                new BasicPrior(11, 0.75, 0.5),
                new BasicPrior(11, 0.85, 0.5),
                new BasicPrior(1, .01, 1.0));

        CovarianceFunction covarianceFunction = new SumCovarianceFunction(
                Matern3CovarianceFunction.getInstance(),
                NoiseCovarianceFunction.getInstance());

        regression = new GaussianProcessRegressionBMC(covarianceFunction, basicPriors);
        predictor = regression.calculateRegression(new double[]{}, new double[]{});
        this.timeSlots = timeSlots;
        this.currentTimeSlot = 1;
        timePeriod = makeTimePeriod(10);

    }

    public void updateModel(double time, double opponentUtility) {
        if (opponentUtility > maxUtilityInTimeSlot)
            maxUtilityInTimeSlot = opponentUtility;

        if (time > (double) currentTimeSlot / timeSlots) {
            predictor = regression.updateRegression(
                    new Matrix(new double[]{time}, 1),
                    new Matrix(new double[]{Math.max(opponentUtility, maxUtilityInTimeSlot)}, 1));

            predictAll();
            // Prepare for next time slot
            currentTimeSlot++;
            maxUtilityInTimeSlot = 0;
        }
    }

    public Pair<Double, Double> predict(double time) {
        GaussianProcessMixturePrediction prediction = predictor.calculatePrediction(new Matrix(new double[]{time}, 1));
        double mean = prediction.getMean().get(0, 0);
        double variance = prediction.getVariance().get(0, 0);
        return new Pair<>(mean, variance);
    }

    public void predictAll() {
        GaussianProcessMixturePrediction prediction = predictor.calculatePrediction(timePeriod.transpose());
        currentMeans = prediction.getMean();
        currentVariance = prediction.getVariance();
    }

    public boolean isOpponentConcess() {
        if (currentMeans == null || currentVariance == null)
            return false;

        double[] means = currentMeans.transpose().getArray()[0];
        double[] variances = currentVariance.transpose().getArray()[0];

        int concessionCount = 0;
        int total = 0;

        for (int i = 1; i < means.length; i++) {
            if (get95ConfidenceIntervall(variances[i]) < 0.2) {
                total++;
                if (means[i] > means[i - 1])
                    concessionCount++;
            }
        }

        if (total > 3)
            return (double) concessionCount > (double) total / 2;
        else
            return false;
    }

    private Matrix makeTimePeriod(int n) {
        double[] timeSamplesArray = new double[n + 1];

        for (int i = 0; i < timeSamplesArray.length; i++) {
            timeSamplesArray[i] = ((double) i) / ((double) n);
        }

        return new Matrix(timeSamplesArray, 1);
    }

    private double get95ConfidenceIntervall(double variance) {
        return 1.96 * Math.sqrt(variance);
    }

}
