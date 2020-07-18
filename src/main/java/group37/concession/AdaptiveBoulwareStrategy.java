package group37.concession;

public class AdaptiveBoulwareStrategy implements ConcessionStrategy {

    private double minUtility;
    private double maxUtility;
    private double initialConcession;
    private double concessionRate;

    public AdaptiveBoulwareStrategy(double maxTargetUtility, double minTargetUtility, double initialConcession, double concessionValue) {
        this.minUtility = minTargetUtility;
        this.maxUtility = maxTargetUtility;
        this.initialConcession = initialConcession;
        this.concessionRate = concessionValue;
    }

    @Override
    public double getTargetUtility(double time) {
        double timeFunction = initialConcession + ((1 - initialConcession) * Math.pow(time, 1 / concessionRate));
        return minUtility + (1 - timeFunction) * (maxUtility - minUtility);
    }

    @Override
    public void adjustRate(double concessionRate) {
        this.concessionRate = concessionRate;
    }
}
