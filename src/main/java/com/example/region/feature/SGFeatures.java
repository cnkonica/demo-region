package com.example.region.feature;

import org.togglz.core.Feature;
import org.togglz.core.annotation.FeatureGroup;
import org.togglz.core.annotation.Label;
@FeatureGroup("SG Feature")
public enum SGFeatures implements Feature {
    @Label("SG | Dual Currency")
    FEATURE_DUAL_CURRENCY_SG,


}
