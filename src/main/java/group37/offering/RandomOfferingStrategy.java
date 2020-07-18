package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.utility.AbstractUtilitySpace;

import java.util.List;
import java.util.Random;

/**
 * Randomly pick an offer from a set of acceptable offers
 */
public class RandomOfferingStrategy extends OfferingStrategy {

    private static Random random = new Random();
    private AbstractUtilitySpace utilitySpace;

    public RandomOfferingStrategy(Domain domain, AbstractUtilitySpace utilitySpace) {
        super(domain);
        this.utilitySpace = utilitySpace;
    }

    public Bid generateBid(double targetUtility, List<Bid> offerSpace, Bid opponentBestOffer, double time) {
        Bid bid = offerSpace.get(0);
        while (utilitySpace.getUtility(bid) < targetUtility) {
            bid = offerSpace.get(random.nextInt(offerSpace.size()));
        }
        return bid;
    }
}
