package group37.opponent;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.EvaluatorDiscrete;

import java.util.*;

public class AdaptiveFrequencyOM implements OpponentModel {

    public AdditiveUtilitySpace opponentUtilitySpace;
    private FrequencyTable frequencyTable;
    private double bidSize;
    private double exponentialFilter;
    private LinkedList<Bid> opponentBids = new LinkedList<>();

    public AdaptiveFrequencyOM(Domain domain, int size, double expFilter) {
        opponentUtilitySpace = new AdditiveUtilitySpace(domain);
        frequencyTable = new FrequencyTable(domain);
        bidSize = size;
        exponentialFilter = expFilter;
        calculateOpponentUtility();
    }

    @Override
    public double getUtility(Bid opponentBid) {
        return getOpponentUtility(opponentBid);
    }

    @Override
    public void updateModel(Bid opponentBid) {
        opponentBids.add(opponentBid);
        frequencyTable.updateFrequency(opponentBid);
        if (opponentBids.size() == bidSize) {
            Bid removedBid = opponentBids.remove(0);
            frequencyTable.decreaseFrequency(removedBid);
        }
        calculateOpponentUtility();
    }

    private void calculateOpponentUtility() {
        double totalFrequency = frequencyTable.getTotalFrequency();
        double maxFrequency = frequencyTable.getMaxFrequency();
        List<Issue> issues = opponentUtilitySpace.getDomain().getIssues();
        double[] weights = new double[issues.size()];
        for (Issue issue : issues) {
            IssueDiscrete issueDiscrete = (IssueDiscrete) issue;
            HashMap<ValueDiscrete, Double> evaluator = new HashMap<>();
            List<ValueDiscrete> values = issueDiscrete.getValues();
            for (ValueDiscrete value : values) {
                evaluator.put(value, getValue(issue, value, maxFrequency));
                if (totalFrequency != 0)
                    weights[issue.getNumber() - 1] += Math.pow(frequencyTable.getFrequency(issue, value) / totalFrequency, 2);
                else
                    weights[issue.getNumber() - 1] += 1 / (double) values.size();
            }
            opponentUtilitySpace.addEvaluator(issue, new EvaluatorDiscrete(evaluator));
        }

        double totalWeight = Arrays.stream(weights).sum();
        // normalize weights
        for (int i = 0; i < weights.length; i++) {
            weights[i] /= totalWeight;
        }
        opponentUtilitySpace.setWeights(issues, weights);
    }

    private double getValue(Issue issue, Value option, double maxFreq) {
        int optionFrequency = frequencyTable.getFrequency(issue, option);
        // Laplace smoothing
        return Math.pow(1 + optionFrequency, exponentialFilter) / Math.pow(1 + maxFreq, exponentialFilter);
    }

    private double getOpponentUtility(Bid bid) {
        try {
            // throws exception if bid incomplete or not in utility space
            return bid == null ? 0 : opponentUtilitySpace.getUtility(bid);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
