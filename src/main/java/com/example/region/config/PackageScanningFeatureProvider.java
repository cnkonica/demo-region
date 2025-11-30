package com.example.region.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.togglz.core.Feature;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.spi.FeatureProvider;

/**
 * Scans the given package for {@link Feature} enums and registers them so Togglz can expose all
 * feature toggles without manually listing enum classes.
 */
public class PackageScanningFeatureProvider implements FeatureProvider {

    private final Map<Feature, FeatureProvider> delegates = new LinkedHashMap<>();

    public PackageScanningFeatureProvider(String basePackage) {
        if (!StringUtils.hasText(basePackage)) {
            throw new IllegalArgumentException("Feature base package must not be blank");
        }
        scanAndRegister(basePackage);
    }

    @Override
    public Set<Feature> getFeatures() {
        return delegates.keySet();
    }

    @Override
    public FeatureMetaData getMetaData(Feature feature) {
        FeatureProvider delegate = delegates.get(feature);
        if (delegate == null) {
            throw new IllegalArgumentException("Unknown feature: " + feature);
        }
        return delegate.getMetaData(feature);
    }

    private void scanAndRegister(String basePackage) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Feature.class));

        for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
            registerFeatureClass(candidate.getBeanClassName());
        }
    }

    @SuppressWarnings("unchecked")
    private void registerFeatureClass(String className) {
        try {
            Class<?> candidateClass =
                    ClassUtils.forName(className, PackageScanningFeatureProvider.class.getClassLoader());
            if (candidateClass.isEnum() && Feature.class.isAssignableFrom(candidateClass)) {
                Class<? extends Feature> featureEnumClass = (Class<? extends Feature>) candidateClass;
                EnumBasedFeatureProvider provider = new EnumBasedFeatureProvider(featureEnumClass);
                provider.getFeatures().forEach(feature -> delegates.put(feature, provider));
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Could not load feature class: " + className, ex);
        }
    }
}
