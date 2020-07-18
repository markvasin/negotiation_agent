package group37.concession;

public interface ConcessionStrategy {
    double getTargetUtility(double time);

    void adjustRate(double concessionRate);
}
