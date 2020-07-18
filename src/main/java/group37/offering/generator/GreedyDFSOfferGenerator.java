package group37.offering.generator;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.EvaluatorDiscrete;
import java.util.*;

public class GreedyDFSOfferGenerator extends AbstractOfferGenerator{

    protected List<Issue> issueOrder;
    protected Map<Issue, List<ValueDiscrete>> valuesOrder;
    protected Stack<HashMap<Integer, Value>> fringe;

    public GreedyDFSOfferGenerator(Domain domain, AbstractUtilitySpace utilitySpace) {
        super(domain, utilitySpace);
        AdditiveUtilitySpace additiveUtilitySpace = (AdditiveUtilitySpace) utilitySpace;
        issueOrder = new LinkedList<>();
        valuesOrder = new HashMap<>();
        for(Issue i: domain.getIssues()){
            issueOrder.add(i);
            List<ValueDiscrete> values = new ArrayList<>(((IssueDiscrete)i).getValues());
            EvaluatorDiscrete e = (EvaluatorDiscrete) additiveUtilitySpace.getEvaluator(i);
            values.sort(Comparator.comparing(v -> e.getValue(v)));
            valuesOrder.put(i, values);
        }
        issueOrder.sort(Comparator.comparing(i -> additiveUtilitySpace.getWeight(i), Comparator.reverseOrder()));
    }

    @Override
    public List<Bid> generateOffers(double minimumUtility, long maxBidNumber) {
        List<Bid> offers = new LinkedList<>();
        fringe = new Stack<>();
        fringe.add(new HashMap<>());
        do{
            HashMap<Integer, Value> node = fringe.pop();
            if(node.size() == issueOrder.size()){
                Bid bid = new Bid(domain, node);
                if(utilitySpace.getUtility(bid) >= minimumUtility) offers.add(bid);
            }else{
                expand(node);
            }
        }while(offers.size() < maxBidNumber && !fringe.isEmpty());
        offers.sort(Comparator.comparing(o -> utilitySpace.getUtility(o), Comparator.reverseOrder()));
        return offers;
    }

    protected void expand(HashMap<Integer, Value> node){
        int nextIssueIndex = node.size();
        Issue nextIssue = issueOrder.get(nextIssueIndex);
        for(ValueDiscrete v : valuesOrder.get(nextIssue)){
            HashMap<Integer, Value> newNode = new HashMap<>(node);
            newNode.put(nextIssue.getNumber(), v);
            fringe.push(newNode);
        }
    }
}
