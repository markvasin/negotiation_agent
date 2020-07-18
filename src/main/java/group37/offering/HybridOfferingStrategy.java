package group37.offering;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.utility.AbstractUtilitySpace;
import group37.opponent.OpponentModel;

import java.util.List;
import java.util.Random;

/**
 * Return the bid higher than target utility and has max opponent utility or the best bid from opponent
 * with some randomness to prevent opponent to model our agent.
 */
public class HybridOfferingStrategy extends OfferingStrategy {

    private OpponentModel opponentModel;
    private AbstractUtilitySpace userspace;
    private RandomOfferingStrategy randomOfferingStrategy;

    public HybridOfferingStrategy(Domain domain, AbstractUtilitySpace userModel, OpponentModel opponentModel) {
        super(domain);
        this.opponentModel = opponentModel;
        this.userspace = userModel;
        this.randomOfferingStrategy = new RandomOfferingStrategy(domain, userspace);
    }

    @Override
    public Bid generateBid(double targetUtility, List<Bid> offerSpace, Bid opponentBestOffer, double time) {
        Bid highestOpponentBid = null;
        Bid chosenBid = null;
        for (Bid bid : offerSpace) {
            if (highestOpponentBid == null || opponentModel.getUtility(bid) > opponentModel.getUtility(highestOpponentBid)) {
                highestOpponentBid = bid;
            }
        }

        if (time <= 0.5) {
            Bid randomBid = randomOfferingStrategy.generateBid(targetUtility, offerSpace, opponentBestOffer, time);
            Bid[] bidChoices = new Bid[]{highestOpponentBid, randomBid};
            chosenBid = bidChoices[new Random().nextInt(bidChoices.length)];
        } else {
            chosenBid = highestOpponentBid;
        }

        if (chosenBid == null)
            return opponentBestOffer;
        else if (userspace.getUtility(chosenBid) > userspace.getUtility(opponentBestOffer))
            return chosenBid;
        else
            return opponentBestOffer;
    }
}
