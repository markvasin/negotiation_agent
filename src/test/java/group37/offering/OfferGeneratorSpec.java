package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.DomainImpl;
import genius.core.utility.AdditiveUtilitySpace;
import group37.offering.generator.GreedyDFSOfferGenerator;
import group37.offering.generator.OfferGenerator;
import group37.offering.generator.RandomOfferGenerator;
import group37.offering.generator.SimulatedAnnealingOfferGenerator;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class OfferGeneratorSpec {

    private static String config = "src/test/resources/test_domain.xml";
    private static Domain domain;
    private static AdditiveUtilitySpace utilitySpace;

    @BeforeClass
    public static void init(){
        try{
            domain = new DomainImpl(config);
            utilitySpace = new AdditiveUtilitySpace(domain, config);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor(){
        OfferGenerator generator = new GreedyDFSOfferGenerator(domain, utilitySpace);
    }

    @Test
    public void testGreedyDFS(){
        OfferGenerator generator = new GreedyDFSOfferGenerator(domain, utilitySpace);
        double minimumUtility = 0.6;
        int maxOfferNumber = 5;
        List<Bid> bids = generator.generateOffers(minimumUtility, maxOfferNumber);
        assertTrue(bids.size() <= maxOfferNumber);
        assertTrue(utilitySpace.getUtility(bids.get(0)) >= utilitySpace.getUtility(bids.get(1)));
        assertTrue(utilitySpace.getUtility(bids.get(1)) >= utilitySpace.getUtility(bids.get(2)));
        assertTrue(utilitySpace.getUtility(bids.get(2)) >= utilitySpace.getUtility(bids.get(3)));
        assertTrue(utilitySpace.getUtility(bids.get(3)) >= utilitySpace.getUtility(bids.get(4)));
        for(Bid bid : bids){
            assertTrue(utilitySpace.getUtility(bid) >= minimumUtility);
        }

        double minimumUtility2 = 0.8;
        int maxOfferNumber2 = 10;
        List<Bid> bids2 = generator.generateOffers(minimumUtility2, maxOfferNumber2);
        assertTrue(bids2.size() <= maxOfferNumber2);
        assertEquals(utilitySpace.getUtility(bids2.get(0)), 1.0, 0.01); // Must have the optimal offer
        for(Bid bid : bids2){
            assertTrue(utilitySpace.getUtility(bid) >= minimumUtility2);
        }
    }

    @Test
    public void testSimulatedAnnealing(){
        OfferGenerator generator = new SimulatedAnnealingOfferGenerator(domain, utilitySpace);
        double minimumUtility = 0.6;
        int maxOfferNumber = 7;
        List<Bid> bids = generator.generateOffers(minimumUtility, maxOfferNumber);
        assertTrue(bids.size() <= maxOfferNumber);
        assertTrue(utilitySpace.getUtility(bids.get(0)) >= utilitySpace.getUtility(bids.get(1)));
        assertTrue(utilitySpace.getUtility(bids.get(1)) >= utilitySpace.getUtility(bids.get(2)));
        assertTrue(utilitySpace.getUtility(bids.get(2)) >= utilitySpace.getUtility(bids.get(3)));
        assertTrue(utilitySpace.getUtility(bids.get(3)) >= utilitySpace.getUtility(bids.get(4)));
        for(Bid bid : bids){
            assertTrue(utilitySpace.getUtility(bid) >= minimumUtility);
        }

        double minimumUtility2 = 0.9;
        int maxOfferNumber2 = 5;
        List<Bid> bids2 = generator.generateOffers(minimumUtility2, maxOfferNumber2);
        assertTrue(bids2.size() <= maxOfferNumber2);
        for(Bid bid : bids2){
            assertTrue(utilitySpace.getUtility(bid) >= minimumUtility2);
        }
    }

    @Test
    public void testRandomOfferGenerator(){
        OfferGenerator generator = new RandomOfferGenerator(domain, utilitySpace);
        double minimumUtility = 0.6;
        int maxOfferNumber = 7;
        List<Bid> bids = generator.generateOffers(minimumUtility, maxOfferNumber);
        assertTrue(bids.size() <= maxOfferNumber);
        assertTrue(utilitySpace.getUtility(bids.get(0)) >= utilitySpace.getUtility(bids.get(1)));
        assertTrue(utilitySpace.getUtility(bids.get(1)) >= utilitySpace.getUtility(bids.get(2)));
        assertTrue(utilitySpace.getUtility(bids.get(2)) >= utilitySpace.getUtility(bids.get(3)));
        assertTrue(utilitySpace.getUtility(bids.get(3)) >= utilitySpace.getUtility(bids.get(4)));
        for(Bid bid : bids){
            assertTrue(utilitySpace.getUtility(bid) >= minimumUtility);
        }

        double minimumUtility2 = 1.1;
        int maxOfferNumber2 = 5;
        List<Bid> bids2 = generator.generateOffers(minimumUtility2, maxOfferNumber2);
        assertTrue(bids2.size() <= maxOfferNumber2);
        assertEquals(maxOfferNumber2, bids2.size());
        for(Bid bid : bids2){
            assertNotNull(bid);
        }
    }

}
