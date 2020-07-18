package group37.preference;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.UtilitySpace;
import genius.core.xml.SimpleElement;

import java.io.IOException;
import java.util.List;

public class RankDependentPM implements PreferenceModel {

    private Domain domain;
    private User user;
    private UserModel userModel;
    private int minBidOrderSize;
    private double unknownUtility;

    public RankDependentPM(Domain domain, User user, UserModel userModel, double unknownUtility, int minBidOrderSize) {
        this.domain = domain;
        this.user = user;
        this.userModel = userModel;
        this.minBidOrderSize = minBidOrderSize;
        this.unknownUtility = unknownUtility;
    }

    @Override
    public AbstractUtilitySpace estimateUtilitySpace() {
        AbstractUtilitySpace utilitySpace = new AbstractUtilitySpace(domain) {
            @Override
            public double getUtility(Bid bid) {
                List<Bid> bidOrder = userModel.getBidRanking().getBidOrder();
                if (bidOrder.size() >= minBidOrderSize)
                    return (double) (bidOrder.indexOf(bid) + 1) / (double) bidOrder.size();
                else
                    return unknownUtility;
            }

            @Override
            public UtilitySpace copy() {
                return null;
            }

            @Override
            public String isComplete() {
                return null;
            }

            @Override
            public SimpleElement toXML() throws IOException {
                return null;
            }
        };
        return utilitySpace;
    }

    @Override
    public void updateModel(Bid bid) {
        if (!userModel.getBidRanking().getBidOrder().contains(bid)) {
            UserModel newUserModel = user.elicitRank(bid, userModel);
            userModel = newUserModel;
        }
    }
}
