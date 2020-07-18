package group37.concession.time;

import java.util.function.Function;

public class TimeConcessionStrategies {

    public static TimeConcessionStrategy LinearTimeConcessionStrategy(double targetUtility, double minTargetUtility) {
        Function<Double, Double> deltaFunc = t -> t * (targetUtility - minTargetUtility);
        return new TimeConcessionStrategy(targetUtility, minTargetUtility, deltaFunc);
    }

    public static TimeConcessionStrategy QuadraticTimeConcessionStrategy(double targetUtility, double minTargetUtility) {
        Function<Double, Double> deltaFunc = t -> Math.pow(t, 2) * (targetUtility - minTargetUtility);
        return new TimeConcessionStrategy(targetUtility, minTargetUtility, deltaFunc);
    }

    public static TimeConcessionStrategy CubicTimeConcessionStrategy(double targetUtility, double minTargetUtility) {
        Function<Double, Double> deltaFunc = t -> Math.pow(t, 3) * (targetUtility - minTargetUtility);
        return new TimeConcessionStrategy(targetUtility, minTargetUtility, deltaFunc);
    }

}
