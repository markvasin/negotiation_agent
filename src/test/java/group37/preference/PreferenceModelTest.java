package group37.preference;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.DomainImpl;
import genius.core.issue.*;
import genius.core.uncertainty.BidRanking;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.EvaluatorDiscrete;
import group37.preference.lp.LinearPreferenceModel;
import group37.preference.lp.LinearProgrammingPM;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.*;

public class PreferenceModelTest {

    private int NUM_BID_ORDER = 100;
    private Random random = new Random();

    String[] configs = new String[]{
            "src/test/resources/party1_utility.xml",
            "src/test/resources/energy_consumer.xml",
            "src/test/resources/energy_distributor.xml",
            "src/test/resources/SmartEnergyGrid_util1.xml",
            "src/test/resources/SmartGridDomain_util1.xml"
    };

    @Test
    public void testPreferenceModel(){

        for(String file: configs){
            try {
                Domain domain = new DomainImpl(file);
                AdditiveUtilitySpace utilitySpace = new AdditiveUtilitySpace(domain, file);

                System.out.println(domain.getName()+"\n");
                System.out.println("Actual UtilitySpace");
                for(Issue i : domain.getIssues()){
                    System.out.print(String.format("%-20s(%.2f) : ", i.getName(),utilitySpace.getWeight(i)));
                    for(Value v : ((IssueDiscrete)i).getValues()){
                        EvaluatorDiscrete e = (EvaluatorDiscrete)utilitySpace.getEvaluator(i);
                        System.out.print(String.format("%s(%.2f), ", v, e.getEvaluation((ValueDiscrete) v)));
                    }
                    System.out.println();
                }

                List<Bid> bidOrder = new ArrayList<>();
                for(int i = 0; i < NUM_BID_ORDER; i++){
                    bidOrder.add(generateRandomBid(domain));
                }
                bidOrder.sort((Bid a, Bid b) -> utilitySpace.getUtility(a) < utilitySpace.getUtility(b) ? -1 : 1);

                BidRanking bidRank = new BidRanking(bidOrder, utilitySpace.getUtility(bidOrder.get(0)), utilitySpace.getUtility(bidOrder.get(NUM_BID_ORDER - 1)));
                UserModel userModel = new UserModel(bidRank);
                User user = Mockito.mock(User.class);

//                PreferenceModel pm = new LinearProgrammingPM(domain, user, userModel);
                PreferenceModel pm = new LinearProgrammingPM(domain, user, userModel);
                AdditiveUtilitySpace estimatedUtilitySpace = (AdditiveUtilitySpace) pm.estimateUtilitySpace();

                System.out.println("\nEstimated UtilitySpace");
                for(Issue i : domain.getIssues()){
                    System.out.print(String.format("%-20s(%.2f) : ", i.getName(),estimatedUtilitySpace.getWeight(i)));
                    for(Value v : ((IssueDiscrete)i).getValues()){
                        EvaluatorDiscrete e = (EvaluatorDiscrete)estimatedUtilitySpace.getEvaluator(i);
                        System.out.print(String.format("%s(%.2f), ", v, e.getEvaluation((ValueDiscrete) v)));
                    }
                    System.out.println();
                }

                double totalDeltaU = 0.0;
                for(Bid b : bidOrder){
                    totalDeltaU += Math.abs(estimatedUtilitySpace.getUtility(b) - utilitySpace.getUtility(b));
                }

                System.out.println();
                System.out.println(String.format("Bid order size : %d", NUM_BID_ORDER));
                System.out.println(String.format("Average utility error : %.2f", totalDeltaU / bidOrder.size()));

                System.out.println("\nSample bids utility");
                for(int i = 0; i < 10; i++){
                    Bid b = bidOrder.get(random.nextInt(NUM_BID_ORDER));
                    System.out.println(String.format("%-150s\t=\t%.2f(Actual)\t%.2f(Estimated)", b, utilitySpace.getUtility(b), estimatedUtilitySpace.getUtility(b)));
                }

                System.out.println("\n_______________________________________________________________________________________________________________________________________\n");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private Bid generateRandomBid(Domain domain) {
        try {
            HashMap<Integer, Value> values = new HashMap<Integer, Value>();
            for (Issue currentIssue : domain.getIssues()) {
                values.put(currentIssue.getNumber(), getRandomValue(currentIssue));
            }
            return new Bid(domain, values);

        } catch (Exception e) {
            return new Bid(domain);
        }
    }

    private Value getRandomValue(Issue currentIssue) throws Exception {
        Value currentValue;
        int index;
        switch (currentIssue.getType()) {
            case DISCRETE:
                IssueDiscrete discreteIssue = (IssueDiscrete) currentIssue;
                index = (random.nextInt(discreteIssue.getNumberOfValues()));
                currentValue = discreteIssue.getValue(index);
                break;
            default:
                throw new Exception("issue type " + currentIssue.getType() + " not supported");
        }
        return currentValue;
    }
}
