package group37.opponent.jonnyblack;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import group37.opponent.FrequencyTable;
import group37.opponent.OpponentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JonnyBlackOM implements OpponentModel {

    private HashMap<Issue, List<Value>> issueValues;
    private FrequencyTable ft;
    private double initUtility;
    private int minimumFrequency;

    public JonnyBlackOM(Domain domain, double initUtility, int minimumFrequency) {
        ft = new FrequencyTable(domain);
        this.initUtility = initUtility;
        this.minimumFrequency = minimumFrequency;
        issueValues = new HashMap<>();
        for (Issue i : domain.getIssues()) {
            IssueDiscrete issueDiscrete = (IssueDiscrete) i;
            List<Value> values = new ArrayList<>();
            for (Value v : issueDiscrete.getValues()) {
                values.add(v);
            }
            issueValues.put(i, values);
        }
    }

    public void updateModel(Bid opponentBid) {
        ft.updateFrequency(opponentBid);
    }

    public double getUtility(Bid opponentBid) {
        if (ft.getTotalFrequency() > minimumFrequency) {
            Map<Issue, Double> weights = issueValues.keySet().stream()
                    .collect(Collectors.toMap(Function.identity(), i -> getWeight(i)));

            double totalWeight = weights.values().stream().mapToDouble(w -> w).sum();

            Map<Issue, Double> normalizedWeights = issueValues.keySet().stream()
                    .collect(Collectors.toMap(Function.identity(), i -> weights.get(i) / totalWeight));

            return opponentBid.getIssues().stream()
                    .mapToDouble(issue -> normalizedWeights.get(issue) * getValue(issue, opponentBid.getValue(issue)))
                    .sum();
        } else {
            return initUtility;
        }
    }

    private double getValue(Issue issue, Value v) {
        int rank = ft.getValueRank(issue, v);
        int numOptions = issueValues.get(issue).size();
        return ((double) (numOptions - rank + 1) / (double) numOptions);
    }

    private double getWeight(Issue issue) {
        Integer totalFrequency = ft.getTotalFrequency();
        if (totalFrequency == 0) return 1.0 / (double) issueValues.keySet().size();
        return issueValues.get(issue).stream()
                .mapToDouble(v -> Math.pow((double) ft.getFrequency(issue, v) / (double) totalFrequency, 2))
                .sum();
    }
}
