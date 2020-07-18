package group37.preference.lp;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.uncertainty.AdditiveUtilitySpaceFactory;
import genius.core.uncertainty.BidRanking;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;
import genius.core.utility.AdditiveUtilitySpace;
import group37.preference.PreferenceModel;
import scpsolver.constraints.LinearConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.lpsolver.SolverFactory2;
import scpsolver.problems.LinearProgram;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class LinearProgrammingPM implements PreferenceModel {

    private Domain domain;
    private User user;
    private UserModel userModel;
    private List<Issue> allIssues;
    private List<Value> allValues;

    public LinearProgrammingPM(Domain domain, User user, UserModel userModel){
        this.domain = domain;
        this.user = user;
        this.userModel = userModel;
        this.allIssues = domain.getIssues();
        this.allValues = new LinkedList<>();
        for (Issue i : domain.getIssues()){
            allValues.addAll(((IssueDiscrete)i).getValues());
        }
    }

    @Override
    public AdditiveUtilitySpace estimateUtilitySpace(){
        HashMap<Value, Double> utilities = estimateUtilities(userModel.getBidRanking());
        HashMap<Issue, Double> weights = estimateWeight(userModel.getBidRanking(), utilities);

        AdditiveUtilitySpaceFactory additiveUtilitySpaceFactory = new AdditiveUtilitySpaceFactory(domain);
        for(Issue i : allIssues){
            additiveUtilitySpaceFactory.setWeight(i, weights.get(i));
            for(Value v : ((IssueDiscrete)i).getValues()){
                additiveUtilitySpaceFactory.setUtility(i, (ValueDiscrete) v, utilities.get(v));
            }
        }
        additiveUtilitySpaceFactory.normalizeWeights();
        return additiveUtilitySpaceFactory.getUtilitySpace();
    }

    @Override
    public void updateModel(Bid bid) {
        if(!userModel.getBidRanking().getBidOrder().contains(bid)){
            userModel = user.elicitRank(bid, userModel);
        }
    }

    private HashMap<Value, Double> estimateUtilities(BidRanking bidRank){
        HashMap<Value, Double> utilities = new HashMap<>();
        LinearPMBuilder builder = new LinearPMBuilder(domain, bidRank);

        double[] objective = builder.getObjective();
        LinearProgram lp = new LinearProgram(objective);
        List<LinearConstraint> constraints = builder.getConstraints();
        for(LinearConstraint c : constraints){
            lp.addConstraint(c);
        }
        lp.setMinProblem(true);
        LinearProgramSolver solver  = SolverFactory2.newDefault();
        double[] solution = solver.solve(lp);
        for(int i = 0; i < allValues.size(); i++){
            Value v = allValues.get(i);
            utilities.put(v, solution[builder.getValueIndex(v)]);
        }
        return utilities;
    }

    private HashMap<Issue, Double> estimateWeight(BidRanking bidRank, HashMap<Value, Double> valuesUtility){
        HashMap<Issue, Double> weights = new HashMap<>();
        LinearPMWeightsBuilder builder = new LinearPMWeightsBuilder(domain, bidRank, valuesUtility);

        double[] objective = builder.getObjective();
        LinearProgram lp = new LinearProgram(objective);
        List<LinearConstraint> constraints = builder.getConstraints();
        for(LinearConstraint c : constraints){
            lp.addConstraint(c);
        }
        lp.setMinProblem(true);
        LinearProgramSolver solver  = SolverFactory2.newDefault();
        double[] solution = solver.solve(lp);

        for(Issue i: allIssues){
            weights.put(i, solution[i.getNumber() - 1]);
        }
        return weights;
    }
}
