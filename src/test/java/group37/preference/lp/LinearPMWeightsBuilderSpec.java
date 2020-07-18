//package group37.preference.lp;
//
//import genius.core.Bid;
//import genius.core.Domain;
//import genius.core.issue.Issue;
//import genius.core.issue.IssueDiscrete;
//import genius.core.issue.Value;
//import genius.core.issue.ValueDiscrete;
//import genius.core.uncertainty.BidRanking;
//import genius.core.uncertainty.User;
//import genius.core.uncertainty.UserModel;
//import org.junit.Test;
//import org.mockito.Mockito;
//import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
//import scpsolver.constraints.LinearConstraint;
//import scpsolver.constraints.LinearEqualsConstraint;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//public class LinearPMWeightsBuilderSpec {
//
//    Domain mockDomain = Mockito.mock(Domain.class);
//    Issue issue1 = new IssueDiscrete("issue1", 1, new String[]{"1", "2", "3"});
//    Issue issue2 = new IssueDiscrete("issue2", 2, new String[]{"A", "B"});
//    List<Issue> issues = Arrays.asList(new Issue[]{issue1, issue2});
//
//    User mockUser = Mockito.mock(User.class);
//    UserModel mockUserModel = Mockito.mock(UserModel.class);
//    BidRanking mockBidRank = Mockito.mock(BidRanking.class);
//
//    Bid bid1 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
//        put(1, new ValueDiscrete("1"));
//        put(2, new ValueDiscrete("A"));
//    }});
//    Bid bid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
//        put(1, new ValueDiscrete("2"));
//        put(2, new ValueDiscrete("A"));
//    }});
//    Bid bid3 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
//        put(1, new ValueDiscrete("3"));
//        put(2, new ValueDiscrete("B"));
//    }});
//
//    List<Bid> bidOrder = new ArrayList<Bid>(){{
//        add(bid3);
//        add(bid2);
//        add(bid1);
//    }};
//
//    HashMap<Value, Double> valuesUtility = new HashMap<Value, Double>(){{
//        put(new ValueDiscrete("1"), 0.60);
//        put(new ValueDiscrete("2"), 0.25);
//        put(new ValueDiscrete("3"), 0.15);
//        put(new ValueDiscrete("A"), 0.66);
//        put(new ValueDiscrete("B"), 0.34);
//    }};
//
//    @Test
//    public void testConstructor(){
//        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
//        LinearPMWeightsBuilder s = new LinearPMWeightsBuilder(mockDomain, new BidRanking(bidOrder, 0.0, 1.0), valuesUtility);
//    }
//
//    @Test
//    public void testGetObjective(){
//        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
//        LinearPMWeightsBuilder s = new LinearPMWeightsBuilder(mockDomain, new BidRanking(bidOrder, 0.0, 1.0), valuesUtility);
//        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 1.0, 1.0}, s.getObjective()));
//    }
//
//    @Test
//    public void testGetConstraints(){
//        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
//        LinearPMWeightsBuilder s = new LinearPMWeightsBuilder(mockDomain, new BidRanking(bidOrder, 0.0, 1.0), valuesUtility);
//        List<LinearConstraint> constraints = s.getConstraints();
//        assertTrue(constraints.get(0) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(0).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 1.0, 0.0}, constraints.get(0).getC()));
//        assertTrue(constraints.get(1) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(1).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 0.0, 1.0}, constraints.get(1).getC()));
//        assertTrue(constraints.get(2) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(2).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{1.0, 0.0, 0.0, 0.0}, constraints.get(2).getC()));
//        assertTrue(constraints.get(3) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(3).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.0, 1.0, 0.0, 0.0}, constraints.get(3).getC()));
//        assertTrue(constraints.get(4) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(4).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.35, 0.0, 1.0, 0.0}, constraints.get(4).getC()));
//        assertTrue(constraints.get(5) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(5).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.1, 0.32, 0.0, 1.0}, constraints.get(5).getC()));
//        assertTrue(constraints.get(6) instanceof LinearEqualsConstraint);
//        assertEquals(1.0, constraints.get(6).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{1.0, 1.0, 0.0, 0.0}, constraints.get(6).getC()));
//    }
//
//}
