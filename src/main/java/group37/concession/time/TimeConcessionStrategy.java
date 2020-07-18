package group37.concession.time;

import group37.concession.ConcessionStrategy;

import java.util.function.Function;

/**
 * Concede overtime from target utility to minimum target utility with the scale of delta function(could be linear, quadratic, cubic etc.)
 */
public class TimeConcessionStrategy implements ConcessionStrategy {

    private double targetUtility;
    private double minTargetUtility;
    private Function<Double, Double> deltaFunc;

    public TimeConcessionStrategy(double targetUtility, double minTargetUtility, Function<Double, Double> deltaFunc){
        if(targetUtility < minTargetUtility){
            throw new IllegalArgumentException("Target utility cannot be lower than minimum target utility");
        }
        this.targetUtility = targetUtility;
        this.minTargetUtility = minTargetUtility;
        this.deltaFunc = deltaFunc;
    }

    @Override
    public double getTargetUtility(double time) {
        if(time > 1.0){
            throw new IllegalArgumentException("Time must not exceed 1.0.");
        }
        double delta = this.deltaFunc.apply(time);
        return targetUtility - delta;
    }

    @Override
    public void adjustRate(double concessionRate) {

    }
}
