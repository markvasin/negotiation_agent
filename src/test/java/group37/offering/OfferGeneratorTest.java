package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.DomainImpl;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.EvaluatorDiscrete;
import group37.offering.generator.GreedyDFSOfferGenerator;
import group37.offering.generator.OfferGenerator;
import group37.offering.generator.SimulatedAnnealingOfferGenerator;
import group37.opponent.FrequencyTable;
import org.junit.Test;
import java.util.List;

public class OfferGeneratorTest {

    private double OFFER_GENERATE_PERCENT = 0.2;
    private double TARGET_UTILITY = 0.75;

    String[] configs = new String[]{
            "src/test/resources/party1_utility.xml",
            "src/test/resources/energy_consumer.xml",
            "src/test/resources/energy_distributor.xml",
            "src/test/resources/SmartEnergyGrid_util1.xml",
            "src/test/resources/SmartGridDomain_util1.xml"
    };

    @Test
    public void testOfferGenerator(){
        for(String file : configs){
            try{
                Domain domain = new DomainImpl(file);
                AdditiveUtilitySpace utilitySpace = new AdditiveUtilitySpace(domain, file);
                OfferGenerator greedyOfferGenerator = new GreedyDFSOfferGenerator(domain, utilitySpace);
                OfferGenerator simulatedAnnealingOfferGenerator = new SimulatedAnnealingOfferGenerator(domain, utilitySpace);

                long NUM_OFFER_GENERATE = Math.min(200, (long)(OFFER_GENERATE_PERCENT * domain.getNumberOfPossibleBids()));
                List<Bid> bids1 = greedyOfferGenerator.generateOffers(TARGET_UTILITY, NUM_OFFER_GENERATE);
                List<Bid> bids2 = simulatedAnnealingOfferGenerator.generateOffers(TARGET_UTILITY, NUM_OFFER_GENERATE);

                FrequencyTable frequencyTable1 = new FrequencyTable(domain);
                FrequencyTable frequencyTable2 = new FrequencyTable(domain);

                for(Bid b: bids1){
                    frequencyTable1.updateFrequency(b);
                }
                for(Bid b: bids2){
                    frequencyTable2.updateFrequency(b);
                }
                System.out.println(domain.getName() + "\n");
                System.out.println("Num offer generate : " + NUM_OFFER_GENERATE);
                System.out.println("Values Frequency");
                for(Issue i: domain.getIssues()){
                    System.out.println(String.format("Issue %s(%.2f)", i, utilitySpace.getWeight(i)));

                    System.out.println(String.format("%11.11s : %3s %3s", "", "Greedy", "SimulatedAnnealing"));
                    for(Value v: ((IssueDiscrete)i).getValues()){
                        EvaluatorDiscrete e = (EvaluatorDiscrete) utilitySpace.getEvaluator(i);
                        System.out.println(String.format("%5.5s(%.2f) : %5d %5d", v, e.getEvaluation((ValueDiscrete) v),frequencyTable1.getFrequency(i,v), frequencyTable2.getFrequency(i,v)));
                    }
                    System.out.println();
                }

                System.out.println("Bid Utility");
                System.out.println(String.format("%11.11s : %3s %3s", "", "Greedy", "SimulatedAnnealing"));
                double averageUtil1 = 0.0;
                double averageUtil2 = 0.0;
                double maxUtil1 = 0.0;
                double maxUtil2 = 0.0;
                double minUtil1 = 1.0;
                double minUtil2 = 1.0;
                for(int i = 0 ; i < bids1.size(); i++){
                    double util1 =  utilitySpace.getUtility(bids1.get(i));
                    double util2 = utilitySpace.getUtility(bids2.get(i));
                    averageUtil1 += util1 / (double)NUM_OFFER_GENERATE;
                    averageUtil2 += util2 / (double)NUM_OFFER_GENERATE;
                    if(util1 > maxUtil1) maxUtil1 = util1;
                    if(util2 > maxUtil2) maxUtil2 = util2;
                    if(util1 < minUtil1) minUtil1 = util1;
                    if(util2 < minUtil2) minUtil2 = util2;
                }
                System.out.println(String.format("%-11s : %3.2f   %3.2f", "Average", averageUtil1, averageUtil2));
                System.out.println(String.format("%-11s : %3.2f   %3.2f", "Max", maxUtil1, maxUtil2));
                System.out.println(String.format("%-11s : %3.2f   %3.2f", "Min", minUtil1, minUtil2));

                System.out.println("_________________________________________________________________________________");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
