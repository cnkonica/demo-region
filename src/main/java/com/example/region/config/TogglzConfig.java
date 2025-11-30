package com.example.region.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.spi.FeatureProvider;

@Configuration
public class TogglzConfig {

    @Value("${togglz.feature-base-package:com.example.region.feature}")
    private String featureBasePackage;

    @Value("${togglz.features-file}")
    private String featuresFilePath;

    @Bean
    public FeatureProvider featureProvider() {
        return new PackageScanningFeatureProvider(featureBasePackage);
    }

    @Bean
    public StateRepository stateRepository(ResourceLoader resourceLoader) throws IOException {
        Resource resource = resourceLoader.getResource(featuresFilePath);
        File file = resource.getFile();
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        seedFromClasspathIfPresent(resourceLoader, file);
        return new FileBasedStateRepository(file);
    }

    private void seedFromClasspathIfPresent(ResourceLoader resourceLoader, File file) throws IOException {
        if (file.exists()) {
            return;
        }
        String seedLocation = "classpath:" + file.getName();
        Resource seed = resourceLoader.getResource(seedLocation);
        if (seed.exists()) {
            Files.copy(seed.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else if (!file.createNewFile()) {
            throw new IOException("Unable to create Togglz state file at " + file.getAbsolutePath());
        }
    }
}
