package com.example.region.controller;

import com.example.region.config.RegionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RegionController {
    @Autowired
    private RegionProperties regionProperties;

    @GetMapping("/region")
    public ResponseEntity<RegionProperties> activeRegion() {
        return ResponseEntity.ok(regionProperties);
    }
}
