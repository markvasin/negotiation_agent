package group37.preference;

import genius.core.Bid;
import genius.core.uncertainty.BidRanking;
import genius.core.uncertainty.UserModel;
import genius.core.utility.AbstractUtilitySpace;
import java.util.LinkedList;
import java.util.List;

public class UserModelScaler{

    public static UserModel scaleUserModel(UserModel userModel, int maxNumBid){
        List<Bid> originalBidOrder = userModel.getBidRanking().getBidOrder();
        List<Bid> sampledBidOrder = new LinkedList<>();
        int sampledOrderSize = Math.min(maxNumBid, originalBidOrder.size());
        int sampleRate = Math.max(originalBidOrder.size() / maxNumBid, 1);
        int lastOrginalIndex = 0;
        for(int i = 0; i < sampledOrderSize; i++){
            sampledBidOrder.add(originalBidOrder.get(i * sampleRate));
            lastOrginalIndex = i * sampleRate;
        }
        BidRanking bidRank = new BidRanking(sampledBidOrder, 0.0, (((double)lastOrginalIndex + 1) / (double)originalBidOrder.size()) *1.0) ;
        return new UserModel(bidRank);
    }

}
