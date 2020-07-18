package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.DomainImpl;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.AdditiveUtilitySpace;
import group37.offering.generator.GreedyDFSOfferGenerator;
import group37.offering.generator.OfferGenerator;
import group37.offering.generator.RandomOfferGenerator;
import group37.preference.PreferenceModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.*;

public class RandomOfferingStrategySpec {

    private static Domain domain;
    private static AbstractUtilitySpace utilitySpace;

    @BeforeClass
    public static void init(){
        try {
            domain = new DomainImpl("src/test/resources/test_domain.xml");
            utilitySpace = new AdditiveUtilitySpace(domain, "src/test/resources/test_domain.xml");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor(){
        new RandomOfferingStrategy(domain, utilitySpace);
    }

    @Test
    public void testGenerateBid(){
        OfferGenerator generator = new RandomOfferGenerator(domain, utilitySpace);
        OfferingStrategy o = new RandomOfferingStrategy(domain, utilitySpace);
        List<Bid> offerSpace = generateOfferSpace(0.8, utilitySpace);
        Bid bid1 = o.generateBid(0.9, offerSpace, null, 0.6);
        Bid bid2 = o.generateBid(0.8, offerSpace, null, 0.6);
        Bid bid3 = o.generateBid(0.95, offerSpace, null, 0.6);
        assertTrue(utilitySpace.getUtility(bid1) >= 0.9);
        assertTrue(utilitySpace.getUtility(bid2) >= 0.8);
        assertTrue(utilitySpace.getUtility(bid3) >= 0.95);
    }

    private List<Bid> generateOfferSpace(double minimumUtility, AbstractUtilitySpace utilitySpace){
        List<Bid> bids = new LinkedList<>();
        Random random = new Random();
        for(int i = 0; i < 10; i++){
            Bid bid = domain.getRandomBid(random);
            while(utilitySpace.getUtility(bid) < minimumUtility){
                bid = domain.getRandomBid(random);
            }
            bids.add(bid);
        }
        return bids;
    }

}
