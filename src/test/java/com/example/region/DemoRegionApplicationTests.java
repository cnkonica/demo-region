package com.example.region;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoRegionApplicationTests {

    @Autowired
    private RegionService regionService;

    @Test
    void contextLoadsAndHasRegion() {
        RegionDescriptor descriptor = regionService.getActiveRegion();
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.code()).isNotBlank();
    }
}
