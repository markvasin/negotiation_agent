package group37.offering.generator;

import genius.core.Bid;
import java.util.List;

public interface OfferGenerator {
    public List<Bid> generateOffers(double minimumUtility, long maxBidNumber);
}
