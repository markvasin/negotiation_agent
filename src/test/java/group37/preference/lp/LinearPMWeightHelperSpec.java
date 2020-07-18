package group37.preference.lp;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.uncertainty.BidRanking;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.apache.commons.math3.optimization.linear.LinearConstraint;
import org.apache.commons.math3.optimization.linear.Relationship;

public class LinearPMWeightHelperSpec {

    Domain mockDomain = Mockito.mock(Domain.class);
    Issue issue1 = new IssueDiscrete("issue1", 1, new String[]{"1", "2", "3"});
    Issue issue2 = new IssueDiscrete("issue2", 2, new String[]{"A", "B"});
    List<Issue> issues = Arrays.asList(new Issue[]{issue1, issue2});

    Bid bid1 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
        put(1, new ValueDiscrete("1"));
        put(2, new ValueDiscrete("A"));
    }});
    Bid bid2 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
        put(1, new ValueDiscrete("2"));
        put(2, new ValueDiscrete("A"));
    }});
    Bid bid3 = new Bid(mockDomain, new HashMap<Integer, Value>() {{
        put(1, new ValueDiscrete("3"));
        put(2, new ValueDiscrete("B"));
    }});

    List<Bid> bidOrder = new ArrayList<Bid>(){{
        add(bid3);
        add(bid2);
        add(bid1);
    }};

    HashMap<Value, Double> valuesUtility = new HashMap<Value, Double>(){{
        put(new ValueDiscrete("1"), 0.60);
        put(new ValueDiscrete("2"), 0.25);
        put(new ValueDiscrete("3"), 0.15);
        put(new ValueDiscrete("A"), 0.66);
        put(new ValueDiscrete("B"), 0.34);
    }};

    @Test
    public void testConstructor(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        LinearPMWeightHelper s = new LinearPMWeightHelper(mockDomain, new BidRanking(bidOrder, 0.0, 1.0), valuesUtility);
    }

    @Test
    public void testGetObjective(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        LinearPMWeightHelper s = new LinearPMWeightHelper(mockDomain, new BidRanking(bidOrder, 0.0, 1.0), valuesUtility);
        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 1.0, 1.0}, s.getObjective()));
    }

    @Test
    public void testGetConstraints(){
        Mockito.when(mockDomain.getIssues()).thenReturn(issues);
        LinearPMWeightHelper s = new LinearPMWeightHelper(mockDomain, new BidRanking(bidOrder, 0.0, 1.0), valuesUtility);
        List<LinearConstraint> constraints = s.getConstraints();
        assertEquals(Relationship.GEQ, constraints.get(0).getRelationship());
        assertEquals(0.0, constraints.get(0).getValue(), 0.0);
        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 1.0, 0.0}, constraints.get(0).getCoefficients().toArray()));
        assertEquals(Relationship.GEQ, constraints.get(1).getRelationship());
        assertEquals(0.0, constraints.get(1).getValue(), 0.0);
        assertTrue(Arrays.equals(new double[]{0.0, 0.0, 0.0, 1.0}, constraints.get(1).getCoefficients().toArray()));
        assertEquals(Relationship.GEQ, constraints.get(2).getRelationship());
        assertEquals(0.0, constraints.get(2).getValue(), 0.0);
        assertTrue(Arrays.equals(new double[]{1.0, 0.0, 0.0, 0.0}, constraints.get(2).getCoefficients().toArray()));
        assertEquals(Relationship.GEQ, constraints.get(3).getRelationship());
        assertEquals(0.0, constraints.get(3).getValue(), 0.0);
        assertTrue(Arrays.equals(new double[]{0.0, 1.0, 0.0, 0.0}, constraints.get(3).getCoefficients().toArray()));
        assertEquals(Relationship.GEQ, constraints.get(4).getRelationship());
        assertEquals(0.0, constraints.get(4).getValue(), 0.0);
        assertTrue(Arrays.equals(new double[]{0.35, 0.0, 1.0, 0.0}, constraints.get(4).getCoefficients().toArray()));
        assertEquals(Relationship.GEQ, constraints.get(5).getRelationship());
        assertEquals(0.0, constraints.get(5).getValue(), 0.0);
        assertTrue(Arrays.equals(new double[]{0.1, 0.32, 0.0, 1.0}, constraints.get(5).getCoefficients().toArray()));
        assertEquals(Relationship.EQ, constraints.get(6).getRelationship());
        assertEquals(1.0, constraints.get(6).getValue(), 0.0);
        assertTrue(Arrays.equals(new double[]{1.0, 1.0, 0.0, 0.0}, constraints.get(6).getCoefficients().toArray()));
    }
}
