package group37.preference.lp;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.DomainImpl;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.uncertainty.BidRanking;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.AdditiveUtilitySpace;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import java.io.IOException;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LinearPreferenceModelSpec {

    User mockUser = Mockito.mock(User.class);

    private static Domain domain;
    private static Bid bid1;
    private static Bid bid2;
    private static Bid bid3;
    private static Bid bid4;

    @BeforeClass
    public static void init(){
        try {
            Random random = new Random();
            domain = new DomainImpl("src/test/resources/test_domain.xml");
            List<Issue> issues = domain.getIssues();
            Issue issue1 = issues.get(0);
            Issue issue2 = issues.get(1);
            Issue issue3 = issues.get(2);
            bid1 = new Bid(domain, new HashMap<Integer, Value>() {{
                put(issue1.getNumber(), ((IssueDiscrete)issue1).getValues().get(0));
                put(issue2.getNumber(), ((IssueDiscrete)issue2).getValues().get(0));
                put(issue3.getNumber(), ((IssueDiscrete)issue3).getValues().get(0));
            }});
            bid2 = new Bid(domain, new HashMap<Integer, Value>() {{
                put(issue1.getNumber(), ((IssueDiscrete)issue1).getValues().get(2));
                put(issue2.getNumber(), ((IssueDiscrete)issue2).getValues().get(1));
                put(issue3.getNumber(), ((IssueDiscrete)issue3).getValues().get(1));
            }});
            bid3 = new Bid(domain, new HashMap<Integer, Value>() {{
                put(issue1.getNumber(), ((IssueDiscrete)issue1).getValues().get(1));
                put(issue2.getNumber(), ((IssueDiscrete)issue2).getValues().get(0));
                put(issue3.getNumber(), ((IssueDiscrete)issue3).getValues().get(1));
            }});
            bid4 = new Bid(domain, new HashMap<Integer, Value>() {{
                put(issue1.getNumber(), ((IssueDiscrete)issue1).getValues().get(2));
                put(issue2.getNumber(), ((IssueDiscrete)issue2).getValues().get(0));
                put(issue3.getNumber(), ((IssueDiscrete)issue3).getValues().get(1));
            }});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor(){
        BidRanking bidRank = new BidRanking(new ArrayList<Bid>(){{
            add(bid3);
            add(bid2);
            add(bid1);
        }}, 0.0, 1.0);
        UserModel userModel = new UserModel(bidRank);
        new LinearPreferenceModel(domain, mockUser, userModel);
    }

    @Test
    public void testGetUtilityBasic(){
        BidRanking bidRank = new BidRanking(new ArrayList<Bid>(){{
            add(bid3);
            add(bid2);
            add(bid1);
        }}, 0.0, 1.0);
        UserModel userModel = new UserModel(bidRank);
        LinearPreferenceModel pm = new LinearPreferenceModel(domain, mockUser, userModel);
        AdditiveUtilitySpace utilitySpace = pm.estimateUtilitySpace();
        assertEquals(1.0, utilitySpace.getUtility(bid1), 0.00);
    }

    @Test
    public void testGetUtilities(){
        BidRanking bidRank = new BidRanking(new ArrayList<Bid>(){{
            add(bid3);
            add(bid2);
            add(bid1);
        }}, 0.3, 1.0);
        UserModel userModel = new UserModel(bidRank);
        LinearPreferenceModel pm = new LinearPreferenceModel(domain, mockUser, userModel);
        AbstractUtilitySpace utilitySpace = pm.estimateUtilitySpace();
        assertEquals(1.0, utilitySpace.getUtility(bid1), 0.00);
        assertEquals(0.3, utilitySpace.getUtility(bid3), 0.00);
        assertTrue(utilitySpace.getUtility(bid1) >= utilitySpace.getUtility(bid2));
        assertTrue(utilitySpace.getUtility(bid2) >= utilitySpace.getUtility(bid3));
    }

    @Test
    public void testUpdateModel(){
        BidRanking bidRank = new BidRanking(new ArrayList<Bid>(){{
            add(bid3);
            add(bid2);
            add(bid1);
        }}, 0.0, 1.0);
        UserModel userModel = new UserModel(bidRank);
        LinearPreferenceModel pm = new LinearPreferenceModel(domain, mockUser, userModel);

        AbstractUtilitySpace utilitySpace1 = pm.estimateUtilitySpace();
        assertEquals(1.0, utilitySpace1.getUtility(bid1), 0.00);
        assertEquals(0.0, utilitySpace1.getUtility(bid3), 0.00);

        UserModel newUserModel = new UserModel(
                new BidRanking(new ArrayList<Bid>(){{
                    add(bid3);
                    add(bid2);
                    add(bid1);
                    add(bid4);
                }}, 0.25, 1.0)
        );
        Mockito.when(mockUser.elicitRank(bid4, userModel)).thenReturn(newUserModel);
        pm.updateModel(bid4);

        AbstractUtilitySpace utilitySpace2 = pm.estimateUtilitySpace();
        assertTrue(utilitySpace2.getUtility(bid4) >= utilitySpace2.getUtility(bid1));
        assertTrue(utilitySpace2.getUtility(bid1) >= utilitySpace2.getUtility(bid2));
        assertTrue(utilitySpace2.getUtility(bid3) >= utilitySpace2.getUtility(bid3));
    }
}
