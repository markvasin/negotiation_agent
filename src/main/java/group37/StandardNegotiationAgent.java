package group37;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.*;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import group37.concession.AdaptiveBoulwareStrategy;
import group37.concession.ConcessionStrategy;
import group37.offering.OfferingStrategy;
import group37.opponent.AdaptiveFrequencyOM;
import group37.opponent.OpponentConcessionModel;
import group37.opponent.OpponentModel;
import group37.preference.PreferenceModel;

import java.util.List;
import java.util.Random;

public class StandardNegotiationAgent extends AbstractNegotiationParty {

    protected double DEFAULT_TARGET_UTILITY = 0.9;
    protected double DEFAULT_MIN_TARGET_UTILITY = 0.5;
    protected int MINIMUM_BID_ORDER_SIZE = 10;

    protected double discountFactor;

    protected Bid lastOffer;
    protected double targetUtility;
    protected double maxUtility;
    protected double minUtility;

    protected OpponentModel opponentModel;
    protected OpponentConcessionModel opponentConcessionModel;
    protected PreferenceModel preferenceModel;
    protected OfferingStrategy offeringStrategy;
    protected ConcessionStrategy concessionStrategy;

    @Override
    public void init(NegotiationInfo info) {
        super.init(info);

        System.out.println("Initialize agent");

        /* Set target utility */
        Bid highestBid;
        Bid lowestBid;

        try {
            highestBid = utilitySpace.getMaxUtilityBid();
            lowestBid = utilitySpace.getMinUtilityBid();
            System.out.println("Highest bid : " + highestBid);
            System.out.println("Lowest bid : " + lowestBid);
            maxUtility = Math.max(utilitySpace.getUtility(highestBid), DEFAULT_TARGET_UTILITY);
            minUtility = Math.max(utilitySpace.getUtility(lowestBid), DEFAULT_MIN_TARGET_UTILITY);
        } catch (Exception e) {
            maxUtility = DEFAULT_TARGET_UTILITY;
            minUtility = DEFAULT_MIN_TARGET_UTILITY;
        }
        discountFactor = utilitySpace.getDiscountFactor();
        minUtility = Math.max(utilitySpace.getReservationValue(), minUtility);

        opponentModel = new AdaptiveFrequencyOM(getDomain(), 1000, 0.9); //new JonnyBlackOM(getDomain(), 0.5, 10);
        opponentConcessionModel = new OpponentConcessionModel(100);
        concessionStrategy = new AdaptiveBoulwareStrategy(maxUtility, minUtility, 0.05, 0.1);

        System.out.println("Max utility : " + maxUtility);
        System.out.println("Minimum target utility : " + minUtility);
        System.out.println("Discount factor : " + discountFactor);
        System.out.println("Reserved value : " + utilitySpace.getReservationValue());
    }

    @Override
    public void receiveMessage(AgentID sender, Action action) {
        if (action instanceof Offer) {
            lastOffer = ((Offer) action).getBid();
            /* Update user and opponent models */
            if (hasPreferenceUncertainty()) {
                /* Update user and opponent models */
                preferenceModel.updateModel(lastOffer);
            }
            opponentModel.updateModel(lastOffer);
            opponentConcessionModel.updateModel(timeline.getTime(), getUtility(lastOffer));
        }
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> possibleActions) {
        System.out.println("________________________________________________________");
        System.out.println("Time : " +  timeline.getTime());
//        System.out.println("Last offer receive : " + lastOffer);

        Action action;
        if (lastOffer != null) {
            double time = timeline.getTime();
            double targetUtility = concessionStrategy.getTargetUtility(time);
            double utility = getUtility(lastOffer);
            double opponentUtility = opponentModel.getUtility(lastOffer);

            System.out.println("Estimated last offer utility : " + utility);
            System.out.println("Estimated last offer opponent utility : " + opponentUtility);
            System.out.println("Target utility : " + targetUtility);
            System.out.println("Opponent Concession : " + opponentConcessionModel.isOpponentConcess());

            if (timeline.getTime() >= 0.99) {
                if (utility >= DEFAULT_MIN_TARGET_UTILITY) {
                    action = new Accept(getPartyId(), lastOffer);
                } else {
                    action = new EndNegotiation(getPartyId());
                }
            } else {
                if (utility >= targetUtility) {
                    action = new Accept(getPartyId(), lastOffer);
                } else {
//                    action = new Offer(getPartyId(), offeringStrategy.generateBid(targetUtility));
                    action = new Offer(getPartyId(), lastOffer);
                }
            }
        } else {
//            action = new Offer(getPartyId(), offeringStrategy.generateBid(maxUtility));
            action = new Offer(getPartyId(), getDomain().getRandomBid(new Random()));
        }

//        System.out.println("Action taken : " + action);
        if (action instanceof DefaultActionWithBid) {
            DefaultActionWithBid lastBid = (DefaultActionWithBid) action;
            System.out.println("Counter offer utility : " + getUtility(lastBid.getBid()));
            System.out.println("Counter offer opponent utility : " + opponentModel.getUtility(lastBid.getBid()));
        }
        return action;
    }

    /**
     * Update utility space
     * By default, using default estimator
     */
    @Override
    public AbstractUtilitySpace estimateUtilitySpace() {
//        System.out.println("Max Utility : " + userModel.getBidRanking().getHighUtility());
//        System.out.println("Min Utility : " + userModel.getBidRanking().getLowUtility());
//        preferenceModel = new RankDependentPM(getDomain(), user, userModel, minUtility - 0.1, 10);
//        return preferenceModel.estimateUtilitySpace();
        return super.estimateUtilitySpace();
    }

    @Override
    public String getDescription() {
        return "StandardNegotiationAgent";
    }
}
