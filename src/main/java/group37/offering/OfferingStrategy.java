package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.*;
import genius.core.parties.NegotiationInfo;
import group37.offering.generator.OfferGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public abstract class OfferingStrategy {


    protected Domain domain;

    public OfferingStrategy(Domain domain){
        this.domain = domain;
    }

    public abstract Bid generateBid(double targetUtility, List<Bid> offerSpace, Bid opponentBestOffer, double time);

}
