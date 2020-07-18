package group37.offering.generator;

import genius.core.Domain;
import genius.core.issue.*;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.Evaluator;
import group37.offering.generator.OfferGenerator;

import java.util.Map;
import java.util.Random;

public abstract class AbstractOfferGenerator implements OfferGenerator {

    protected Domain domain;
    protected AbstractUtilitySpace utilitySpace;
    protected Random random;

    public AbstractOfferGenerator(Domain domain, AbstractUtilitySpace utilitySpace){
        this.domain = domain;
        this.utilitySpace = utilitySpace;
        this.random = new Random();
    }
}
