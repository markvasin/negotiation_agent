package group37.offering.generator;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.utility.AbstractUtilitySpace;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class RandomOfferGenerator extends AbstractOfferGenerator{

    private static int MAX_ITERATION = 100;

    public RandomOfferGenerator(Domain domain, AbstractUtilitySpace utilitySpace) {
        super(domain, utilitySpace);
    }

    @Override
    public List<Bid> generateOffers(double minimumUtility, long maxBidNumber) {
        List<Bid> offers = new LinkedList<>();
        for(int i = 0; i < maxBidNumber; i++){
            int round = 0;
            Bid bid;
            do{
                bid = domain.getRandomBid(random);
                round++;
            }while(utilitySpace.getUtility(bid) < minimumUtility && round < MAX_ITERATION);
            offers.add(bid);
        }
        offers.sort(Comparator.comparing(o -> utilitySpace.getUtility(o), Comparator.reverseOrder()));
        return offers;
    }
}
