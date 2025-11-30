package com.example.region;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DemoRegionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoRegionApplication.class, args);
    }
}
