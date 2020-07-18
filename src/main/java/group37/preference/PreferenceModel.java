package group37.preference;

import genius.core.Bid;
import genius.core.utility.AbstractUtilitySpace;

public interface PreferenceModel {
    AbstractUtilitySpace estimateUtilitySpace();

    void updateModel(Bid bid);
}
