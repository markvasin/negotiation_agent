package group37.opponent;

import genius.core.Bid;

public interface OpponentModel {
    double getUtility(Bid bid);
    void updateModel(Bid bid);
}
