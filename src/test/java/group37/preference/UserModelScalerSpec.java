package group37.preference;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.DomainImpl;
import genius.core.uncertainty.BidRanking;
import genius.core.uncertainty.UserModel;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.AdditiveUtilitySpace;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserModelScalerSpec {

    private static Domain domain;
    private static AbstractUtilitySpace utilitySpace;
    private static Random random;

    @BeforeClass
    public static void init(){
        try{
            domain = new DomainImpl("src/test/resources/test_domain.xml");
            utilitySpace = new AdditiveUtilitySpace(domain, "src/test/resources/test_domain.xml");
            random = new Random();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Test
    public void testScaleDownUserModelByHalf(){
        int originalBidOrderSize = 20;
        List<Bid> originalBidOrder = new LinkedList<>();
        double maxUtility = 0.0;
        double minUtility = 1.0;
        for(int i = 0; i < originalBidOrderSize; i++){
            Bid bid = domain.getRandomBid(random);
            double utility = utilitySpace.getUtility(bid);
            originalBidOrder.add(bid);
            if(utility < minUtility) minUtility = utility;
            if(utility > maxUtility) maxUtility = utility;
        }
        originalBidOrder.sort(Comparator.comparing(b -> utilitySpace.getUtility(b)));
        BidRanking originalBidRank = new BidRanking(originalBidOrder, minUtility, maxUtility);
        UserModel originalUserMode = new UserModel(originalBidRank);

        int newBidOrderSize = 10;
        UserModel newUserModel = UserModelScaler.scaleUserModel(originalUserMode, newBidOrderSize);
        List<Bid> newBidOrder = newUserModel.getBidRanking().getBidOrder();
        assertEquals(newBidOrderSize, newBidOrder.size());
        assertEquals(originalBidOrder.get(0), newBidOrder.get(0));
        assertEquals(originalBidOrder.get(2), newBidOrder.get(1));
        assertEquals(originalBidOrder.get(6), newBidOrder.get(3));
        assertEquals(originalBidOrder.get(8), newBidOrder.get(4));
        assertEquals(0.0, newUserModel.getBidRanking().getLowUtility(), 0.0);
        assertEquals(1.0 * ((double)((newBidOrderSize * 2) - 1) / originalBidOrderSize), newUserModel.getBidRanking().getHighUtility(), 0.0);
    }

    @Test
    public void testScaleDownUserModelByLessThanHalf(){
        int originalBidOrderSize = 17;
        List<Bid> originalBidOrder = new LinkedList<>();
        double maxUtility = 0.0;
        double minUtility = 1.0;
        for(int i = 0; i < originalBidOrderSize; i++){
            Bid bid = domain.getRandomBid(random);
            double utility = utilitySpace.getUtility(bid);
            originalBidOrder.add(bid);
            if(utility < minUtility) minUtility = utility;
            if(utility > maxUtility) maxUtility = utility;
        }
        originalBidOrder.sort(Comparator.comparing(b -> utilitySpace.getUtility(b)));
        BidRanking originalBidRank = new BidRanking(originalBidOrder, minUtility, maxUtility);
        UserModel originalUserMode = new UserModel(originalBidRank);

        int newBidOrderSize = 15;
        UserModel newUserModel = UserModelScaler.scaleUserModel(originalUserMode, newBidOrderSize);
        List<Bid> newBidOrder = newUserModel.getBidRanking().getBidOrder();
        assertEquals(newBidOrderSize, newBidOrder.size());
        assertEquals(originalBidOrder.get(0), newBidOrder.get(0));
        assertEquals(originalBidOrder.get(1), newBidOrder.get(1));
        assertEquals(originalBidOrder.get(3), newBidOrder.get(3));
        assertEquals(originalBidOrder.get(7), newBidOrder.get(7));
        assertEquals(originalBidOrder.get(11), newBidOrder.get(11));
        assertEquals(originalBidOrder.get(14), newBidOrder.get(14));
        assertEquals(0.0, newUserModel.getBidRanking().getLowUtility(), 0.0);
        assertEquals(1.0 * ((double)newBidOrderSize / (double)originalBidOrderSize), newUserModel.getBidRanking().getHighUtility(), 0.0);
    }

    @Test
    public void testSameSizeUserModel(){
        int originalBidOrderSize = 10;
        List<Bid> originalBidOrder = new LinkedList<>();
        double maxUtility = 0.0;
        double minUtility = 1.0;
        for(int i = 0; i < originalBidOrderSize; i++){
            Bid bid = domain.getRandomBid(random);
            double utility = utilitySpace.getUtility(bid);
            originalBidOrder.add(bid);
            if(utility < minUtility) minUtility = utility;
            if(utility > maxUtility) maxUtility = utility;
        }
        originalBidOrder.sort(Comparator.comparing(b -> utilitySpace.getUtility(b)));
        BidRanking originalBidRank = new BidRanking(originalBidOrder, minUtility, maxUtility);
        UserModel originalUserMode = new UserModel(originalBidRank);

        int newBidOrderSize = 10;
        UserModel scaledUserModel = UserModelScaler.scaleUserModel(originalUserMode, newBidOrderSize);
        List<Bid> newBidOrder = scaledUserModel.getBidRanking().getBidOrder();
        assertEquals(newBidOrderSize, newBidOrder.size());
        assertEquals(originalBidOrder.get(0), newBidOrder.get(0));
        assertEquals(originalBidOrder.get(1), newBidOrder.get(1));
        assertEquals(originalBidOrder.get(3), newBidOrder.get(3));
        assertEquals(originalBidOrder.get(5), newBidOrder.get(5));
        assertEquals(originalBidOrder.get(9), newBidOrder.get(9));
    }

    @Test
    public void testBiggerUserModel(){
        int originalBidOrderSize = 10;
        List<Bid> originalBidOrder = new LinkedList<>();
        double maxUtility = 0.0;
        double minUtility = 1.0;
        for(int i = 0; i < originalBidOrderSize; i++){
            Bid bid = domain.getRandomBid(random);
            double utility = utilitySpace.getUtility(bid);
            originalBidOrder.add(bid);
            if(utility < minUtility) minUtility = utility;
            if(utility > maxUtility) maxUtility = utility;
        }
        originalBidOrder.sort(Comparator.comparing(b -> utilitySpace.getUtility(b)));
        BidRanking originalBidRank = new BidRanking(originalBidOrder, minUtility, maxUtility);
        UserModel originalUserMode = new UserModel(originalBidRank);

        int newBidOrderSize = 15;
        UserModel scaledUserModel = UserModelScaler.scaleUserModel(originalUserMode, newBidOrderSize);
        List<Bid> newBidOrder = scaledUserModel.getBidRanking().getBidOrder();
        assertEquals(originalBidOrderSize, newBidOrder.size());
        assertEquals(originalBidOrder.get(0), newBidOrder.get(0));
        assertEquals(originalBidOrder.get(1), newBidOrder.get(1));
        assertEquals(originalBidOrder.get(3), newBidOrder.get(3));
        assertEquals(originalBidOrder.get(5), newBidOrder.get(5));
        assertEquals(originalBidOrder.get(9), newBidOrder.get(9));
    }

}
