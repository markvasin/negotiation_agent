package group37.concession.time;

import group37.concession.ConcessionStrategy;
import org.junit.Test;
import java.util.function.Function;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TimeConcessionStrategySpec {

    @Test(expected = IllegalArgumentException.class)
    public void testTargetLowerThanMin(){
        Function<Double, Double> deltaFunc = a -> a;
        new TimeConcessionStrategy(0.5, 0.8, deltaFunc);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTime(){
        Function<Double, Double> deltaFunc = a -> a;
        ConcessionStrategy c = new TimeConcessionStrategy(0.5, 0.8, deltaFunc);
        c.getTargetUtility(1.1);
    }

    @Test
    public void testLinearTimeConcession(){
        ConcessionStrategy c = TimeConcessionStrategies.LinearTimeConcessionStrategy(0.9, 0.5);
        assertEquals(0.86, c.getTargetUtility(0.1), 0.00);
        assertEquals(0.7, c.getTargetUtility(0.5), 0.00);
        assertEquals(0.54, c.getTargetUtility(0.9), 0.00);
    }

    @Test
    public void testQuadraticTimeConcession() {
        ConcessionStrategy c = TimeConcessionStrategies.QuadraticTimeConcessionStrategy(0.9, 0.5);
        assertEquals(0.896, c.getTargetUtility(0.1), 0.00);
        assertEquals(0.8, c.getTargetUtility(0.5), 0.00);
        assertEquals(0.576, c.getTargetUtility(0.9), 0.00);
    }

    @Test
    public void testCubicTimeConcession() {
        ConcessionStrategy c = TimeConcessionStrategies.CubicTimeConcessionStrategy(0.9, 0.5);
        assertEquals(0.8996, c.getTargetUtility(0.1), 0.00001);
        assertEquals(0.85, c.getTargetUtility(0.5), 0.00);
    }

}
