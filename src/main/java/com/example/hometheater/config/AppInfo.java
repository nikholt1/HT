package com.example.hometheater.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppInfo {
    @Value("${app.version}")
    private String version;

    public String getVersion() {
        System.out.println("[SYSTEM] RUNNING VERSION: " + version);
        return version;
    }
}

