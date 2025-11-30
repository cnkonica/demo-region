package com.example.region.feature;

import org.togglz.core.Feature;
import org.togglz.core.annotation.FeatureGroup;
import org.togglz.core.annotation.Label;

@FeatureGroup("HK Feature")
public enum HKFeatures implements Feature {
    @Label("HK | Audit logging")
    FEATURE_AUDIT_LOGGING_HK,

}
