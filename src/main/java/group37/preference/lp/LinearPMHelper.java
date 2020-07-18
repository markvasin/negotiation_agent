package group37.preference.lp;

import org.apache.commons.math3.optimization.linear.LinearConstraint;
import org.apache.commons.math3.optimization.linear.Relationship;
import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.uncertainty.BidRanking;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class LinearPMHelper {

    private int numValues;
    private HashMap<Value, Integer> valueIndices;
    private double[][] deltaU;
    private int numSlackVar;
    private BidRanking bidRank;

    public LinearPMHelper(Domain domain, BidRanking bidRank){
        List<Bid> bidOrder = bidRank.getBidOrder();
        valueIndices = new HashMap<>();
        List<Value> allValues = new LinkedList<>();

        for (Issue i : domain.getIssues()){
            allValues.addAll(((IssueDiscrete)i).getValues());
        }
        numValues = allValues.size();
        for(int i = 0; i < allValues.size(); i++){
            valueIndices.put(allValues.get(i), i);
        }

        if(bidOrder.size() > 0) deltaU = generateDeltaU(bidOrder);
        numSlackVar = bidOrder.size() - 1;
        this.bidRank = bidRank;
    }

    public double[] getObjective(){
        double[] objective = new double[numValues + numSlackVar];
        for(int i = 0; i < numValues + numSlackVar; i++){
            if(i < numValues) objective[i] = 0.0;
            else objective[i] = 1.0;
        }
        return objective;
    }

    public List<LinearConstraint> getConstraints(){
        List<LinearConstraint> constraints = new LinkedList<>();
        int length = numValues + numSlackVar;
        double[] emptyConstraint = new double[length];
        Arrays.fill(emptyConstraint, 0.0);
        // z >= 0
        for(int i = 0; i < numSlackVar; i++){
            double[] c = emptyConstraint.clone();
            c[numValues + i] = 1.0;
            constraints.add(new LinearConstraint(c, Relationship.GEQ, 0.0));
        }
        // deltaU + z >= 0
        for(int i = 0; i < numSlackVar; i++){
            double[] c = emptyConstraint.clone();
            double[] delta = deltaU[i];
            System.arraycopy(delta, 0, c, 0, numValues);
            c[numValues + i] = 1.0;
            constraints.add(new LinearConstraint(c, Relationship.GEQ, 0.0));
        }

        // u(x) >= 0
        for(int i = 0; i < numValues; i++){
            double[] c = emptyConstraint.clone();
            c[i] = 1.0;
            constraints.add(new LinearConstraint(c, Relationship.GEQ, 0.0));
        }

        // U(maxBid) = bidRank.getHighUtility
        List<Bid> bidOrder = bidRank.getBidOrder();
        Bid highBid = bidOrder.get(bidOrder.size() - 1);
        double[] cHighBid = emptyConstraint.clone();
        for(Issue i: highBid.getIssues()){
            Value v = highBid.getValue(i);
            cHighBid[valueIndices.get(v)] = 1.0;
        }
        constraints.add(new LinearConstraint(cHighBid, Relationship.EQ, bidRank.getHighUtility()));

        // U(minBid) = bidRank.getLowUtility
        Bid lowBid = bidOrder.get(0);
        double[] cLowBid = emptyConstraint.clone();
        for(Issue i: lowBid.getIssues()){
            Value v = lowBid.getValue(i);
            cLowBid[valueIndices.get(v)] = 1.0;
        }
        constraints.add(new LinearConstraint(cLowBid, Relationship.EQ, bidRank.getLowUtility()));

        return constraints;
    }

    public int getValueIndex(Value v){
        return valueIndices.get(v);
    }

    public double[][] getDeltaU(){ return deltaU; }

    private double[][] generateDeltaU(List<Bid> bidOrder){
        deltaU = new double[bidOrder.size() - 1][numValues];
        for(int i = bidOrder.size() - 1, j = 0; i > 0; i--, j++){
            deltaU[j] = getDeltaCoefficients(bidOrder.get(i), bidOrder.get(i - 1));
        }
        return deltaU;
    }

    public double[] getDeltaCoefficients(Bid a, Bid b){
        double[] aCoef = getBidCoefficients(a);
        double[] bCoef = getBidCoefficients(b);
        double[] delta = new double[aCoef.length];
        Arrays.setAll(delta, n -> aCoef[n] - bCoef[n]);
        return delta;
    }

    public double[] getBidCoefficients(Bid bid){
        double[] coefficients = new double[numValues];
        for (Issue i : bid.getIssues()){
            Value v = bid.getValue(i);
            coefficients[valueIndices.get(v)] = 1.0;
        }
        return coefficients;
    }

}
