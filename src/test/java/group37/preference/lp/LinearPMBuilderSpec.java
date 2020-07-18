//package group37.preference.lp;
//
//import genius.core.Bid;
//import genius.core.Domain;
//import genius.core.issue.Issue;
//import genius.core.issue.IssueDiscrete;
//import genius.core.issue.Value;
//import genius.core.issue.ValueDiscrete;
//import genius.core.uncertainty.BidRanking;
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
//public class LinearPMBuilderSpec {
//
//    Domain mockDomain = Mockito.mock(Domain.class);
//    Issue issue1 = (Issue)(new IssueDiscrete("issue1", 1, new String[]{"1", "2", "3"}));
//    Issue issue2 = (Issue)(new IssueDiscrete("issue2", 2, new String[]{"A", "B"}));
//    List<Issue> issues = Arrays.asList(new Issue[]{issue1, issue2});
//
//    Bid bid1 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
//        put(1, new ValueDiscrete("1"));
//        put(2, new ValueDiscrete("A"));
//    }});
//    Bid bid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
//        put(1, new ValueDiscrete("3"));
//        put(2, new ValueDiscrete("B"));
//    }});
//    Bid bid3 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
//        put(1, new ValueDiscrete("2"));
//        put(2, new ValueDiscrete("A"));
//    }});
//
//    List<Bid> bidOrder = new ArrayList<Bid>(){{
//        add(bid3);
//        add(bid2);
//        add(bid1);
//    }};
//
//    @Test
//    public void testConstructorValueIndices(){
//        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
//        LinearPMBuilder s = new LinearPMBuilder(mockDomain, new BidRanking(bidOrder, 0.0, 1.0));
//        assertEquals(0, s.getValueIndex(new ValueDiscrete("1")));
//        assertEquals(1, s.getValueIndex(new ValueDiscrete("2")));
//        assertEquals(2, s.getValueIndex(new ValueDiscrete("3")));
//        assertEquals(3, s.getValueIndex(new ValueDiscrete("A")));
//        assertEquals(4, s.getValueIndex(new ValueDiscrete("B")));
//    }
//
//    @Test
//    public void testGetBidCoefficients(){
//        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
//        LinearPMBuilder s = new LinearPMBuilder(mockDomain, new BidRanking(bidOrder, 0.0, 1.0));
//        assertTrue(Arrays.equals(new double[]{1.0, 0.0, 0.0, 1.0, 0.0}, s.getBidCoefficients(bid1)));
//        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 1.0, 0.0, 1.0}, s.getBidCoefficients(bid2)));
//        assertTrue(Arrays.equals(new double[]{0.0, 1.0, 0.0, 1.0, 0.0}, s.getBidCoefficients(bid3)));
//    }
//
//    @Test
//    public void testGenerateDeltaCoefficients(){
//        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
//        LinearPMBuilder s = new LinearPMBuilder(mockDomain, new BidRanking(bidOrder, 0.0, 1.0));
//        assertTrue(Arrays.equals(new double[]{1.0, 0.0, -1.0, 1.0, -1.0}, s.getDeltaCoefficients(bid1, bid2)));
//        assertTrue(Arrays.equals(new double[]{1.0, -1.0, 0.0, 0.0, 0.0}, s.getDeltaCoefficients(bid1, bid3)));
//        assertTrue(Arrays.equals(new double[]{0.0, -1.0, 1.0, -1.0, 1.0}, s.getDeltaCoefficients(bid2, bid3)));
//        assertTrue(Arrays.equals(new double[]{-1.0, 1.0, 0.0, 0.0, 0.0}, s.getDeltaCoefficients(bid3, bid1)));
//    }
//
//    @Test
//    public void testConstructorGenerateDeltaU(){
//        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
//        LinearPMBuilder s = new LinearPMBuilder(mockDomain, new BidRanking(bidOrder, 0.0, 1.0));
//        double[][] deltaU = s.getDeltaU();
//        assertEquals(2, deltaU.length);
//        assertTrue(Arrays.equals(new double[]{1.0, 0.0, -1.0, 1.0, -1.0}, deltaU[0]));
//        assertTrue(Arrays.equals(new double[]{0.0, -1.0, 1.0, -1.0, 1.0}, deltaU[1]));
//    }
//
//    @Test
//    public void testGetObjective(){
//        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
//        LinearPMBuilder s = new LinearPMBuilder(mockDomain, new BidRanking(bidOrder, 0.0, 1.0));
//        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0}, s.getObjective()));
//    }
//
//    @Test
//    public void testGetConstraints(){
//        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
//        LinearPMBuilder s = new LinearPMBuilder(mockDomain, new BidRanking(bidOrder, 0.3, 1.0));
//        List<LinearConstraint> constraints = s.getConstraints();
//        assertEquals(11, s.getConstraints().size());
//        assertTrue(constraints.get(0) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(0).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0}, constraints.get(0).getC()));
//        assertTrue(constraints.get(1) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(1).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0}, constraints.get(1).getC()));
//        assertTrue(constraints.get(2) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(2).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{1.0, 0.0, -1.0, 1.0, -1.0, 1.0, 0.0}, constraints.get(2).getC()));
//        assertTrue(constraints.get(3) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(3).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.0, -1.0, 1.0, -1.0, 1.0, 0.0, 1.0}, constraints.get(3).getC()));
//        assertTrue(constraints.get(4) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(4).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, constraints.get(4).getC()));
//        assertTrue(constraints.get(5) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(5).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0}, constraints.get(5).getC()));
//        assertTrue(constraints.get(6) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(6).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0}, constraints.get(6).getC()));
//        assertTrue(constraints.get(7) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(7).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0}, constraints.get(7).getC()));
//        assertTrue(constraints.get(8) instanceof LinearBiggerThanEqualsConstraint);
//        assertEquals(0.0, constraints.get(8).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0}, constraints.get(8).getC()));
//        assertTrue(constraints.get(9) instanceof LinearEqualsConstraint);
//        assertEquals(1.0, constraints.get(9).getT(), 0.0);
//        assertTrue(Arrays.equals(new double[]{1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0}, constraints.get(9).getC()));
//        assertTrue(constraints.get(10) instanceof LinearEqualsConstraint);
//        assertEquals(0.3, constraints.get(10).getT(), 0.00);
//        assertTrue(Arrays.equals(new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0}, constraints.get(10).getC()));
//    }
//
//}
