package group37.preference.lp;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.Value;
import genius.core.uncertainty.BidRanking;
import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.constraints.LinearConstraint;
import scpsolver.constraints.LinearEqualsConstraint;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class LinearPMWeightsBuilder {

    private int numIssues;
    private int numSlackVar;
    private double[][] deltaU;
    private HashMap<Value, Double> valuesUtility;

    public LinearPMWeightsBuilder(Domain domain, BidRanking bidRank, HashMap<Value, Double> valuesUtility){
        numIssues = domain.getIssues().size();
        numSlackVar = bidRank.getBidOrder().size() - 1;
        this.deltaU = generateDeltaUWeight(bidRank.getBidOrder(), valuesUtility);
    }

    public double[] getObjective(){
        double[] objective = new double[numIssues + numSlackVar];
        for(int i = 0; i < numIssues + numSlackVar; i++){
            if(i < numIssues) objective[i] = 0.0;
            else objective[i] = 1.0;
        }
        return objective;
    }

    public List<LinearConstraint> getConstraints(){
        List<LinearConstraint> constraints = new LinkedList<>();
        int length = numIssues + numSlackVar;
        double[] emptyConstraint = new double[length];
        Arrays.fill(emptyConstraint, 0.0);
        // z >= 0
        for(int i = 0; i < numSlackVar; i++){
            double[] c = emptyConstraint.clone();
            c[numIssues + i] = 1.0;
            constraints.add(new LinearBiggerThanEqualsConstraint(c, 0.0, "Z" + i));
        }

        // w >= 0
        for(int i = 0; i < numIssues; i++){
            double[] c = emptyConstraint.clone();
            c[i] = 1.0;
            constraints.add(new LinearBiggerThanEqualsConstraint(c, 0.0, "W" + i));
        }

        // deltaU + z >= 0
        for(int i = 0; i < numSlackVar; i++){
            double[] c = emptyConstraint.clone();
            double[] delta = deltaU[i];
            System.arraycopy(delta, 0, c, 0, numIssues);
            c[numIssues + i] = 1.0;
            constraints.add(new LinearBiggerThanEqualsConstraint(c, 0.0, "DELTA" + i));
        }

        // w1 + w2 + ... + wn = 1.0
        double[] cWeightSum = emptyConstraint.clone();
        for(int i = 0; i < numIssues; i++){
            cWeightSum[i] = 1.0;
        }
        constraints.add(new LinearEqualsConstraint(cWeightSum, 1.0, "TOTAL_W"));

        return constraints;
    }

    private double[][] generateDeltaUWeight(List<Bid> bidOrder, HashMap<Value, Double> valuesUtility){
        double[][] delta = new double[bidOrder.size() - 1][numIssues];
        for(int i = bidOrder.size() - 1 , j = 0; i > 0; i--, j++){
            Bid highBid = bidOrder.get(i);
            Bid lowBid = bidOrder.get(i - 1);
            for(Issue issue : highBid.getIssues()){
                delta[j][issue.getNumber() - 1] =  valuesUtility.get(highBid.getValue(issue.getNumber())) -  valuesUtility.get(lowBid.getValue(issue.getNumber()));
            }
        }
        return delta;
    }
}
