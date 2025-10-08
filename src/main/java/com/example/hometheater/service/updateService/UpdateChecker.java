package com.example.hometheater.service.updateService;

import com.example.hometheater.config.AppInfo;
import org.springframework.stereotype.Component;

@Component
public class UpdateChecker {

    private final AppInfo appInfo;

    public UpdateChecker(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public boolean isUpdateAvailable() {
        // Example: check latest GitHub release via REST API
        String latestVersion = fetchLatestVersionFromGitHub();
        return !appInfo.getVersion().equals(latestVersion);
    }

    private String fetchLatestVersionFromGitHub() {
//        // Minimal example: use GitHub API to get latest release tag
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "https://api.github.com/repos/YOUR_USER/YOUR_REPO/releases/latest";
//        var response = restTemplate.getForObject(url, GitHubRelease.class);
//        return response != null ? response.getTagName() : appInfo.getVersion();
        return "2.2.2";
    }

    static class GitHubRelease {
        private String tag_name;

        public String getTagName() { return tag_name; }
        public void setTagName(String tag_name) { this.tag_name = tag_name; }
    }


}
