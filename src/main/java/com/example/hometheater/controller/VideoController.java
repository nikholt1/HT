package com.example.hometheater.controller;

import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.service.updateService.UpdateChecker;
import com.example.hometheater.service.VideoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaTypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@Controller
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;
    private String videoFolder;
    private UpdateChecker updateChecker;


    public VideoController(VideoService videoService, UpdateChecker updateChecker) {
        this.videoService = videoService;
        this.videoFolder = videoService.getFolderPath();
        this.updateChecker = updateChecker;
        updateChecker.checkForUpdate();

    }

    @ModelAttribute("currentUser")
    public ProfileUser addCurrentUserToModel(HttpSession session) {
        return (ProfileUser) session.getAttribute("selectedUser");
    }

    @GetMapping("/browser")
    public String browseVideos(@RequestParam(required = false) String path, Model model, HttpSession session) throws IOException {
        ProfileUser currentUser = (ProfileUser) session.getAttribute("selectedUser");

        if (currentUser == null) {
            return "redirect:/";
        }

        model.addAttribute("currentUser", currentUser);

        System.out.println("[SYSTEM] User " + currentUser.getUsername() + " Navigated to endpoint /browser");

        Path folderPath = Paths.get(videoFolder);
        if (path != null && !path.isBlank()) {
            folderPath = folderPath.resolve(path).normalize();
        }

        if (!folderPath.startsWith(Paths.get(videoFolder)) || !Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
            model.addAttribute("error", "Folder does not exist or invalid path");
            model.addAttribute("items", List.of());
            model.addAttribute("paths", Map.of());
            model.addAttribute("currentPath", "");
            return "browser";
        }

        // List folder items
        List<String> items;
        Map<String, String> paths = new LinkedHashMap<>();
        try (var files = Files.list(folderPath)) {
            System.out.println("[SYSTEM] folderPath = " + folderPath);
            items = files.map(p -> {
                String name = p.getFileName().toString();
                boolean isDir = Files.isDirectory(p);
                String displayName = isDir ? name + "/" : name;

                String newPath = (path == null || path.isBlank()) ? displayName : path + "/" + displayName;
                paths.put(displayName, newPath);

                return displayName;
            }).toList();
        }

        model.addAttribute("items", items);
        model.addAttribute("paths", paths);
        model.addAttribute("currentPath", path == null ? "" : path);

        String userName = videoService.getUserName();
        model.addAttribute("userName", userName);

        // ======= Random video preview =======
        List<Path> videoFiles;
        try (var files = Files.list(folderPath)) {
            videoFiles = files.filter(Files::isRegularFile)
                    .filter(f -> {
                        String name = f.getFileName().toString().toLowerCase();
                        return name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi");
                    })
                    .toList();
        }

        if (!videoFiles.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(videoFiles.size());
            Path randomVideo = videoFiles.get(randomIndex);

            // Make path relative for /stream
            String randomVideoPath = (path == null || path.isBlank() ? "" : path + "/") + randomVideo.getFileName().toString();
            model.addAttribute("previewVideoPath", randomVideoPath);
            model.addAttribute("previewVideoName", randomVideo.getFileName().toString());
        } else {
            model.addAttribute("previewVideoPath", null);
            model.addAttribute("previewVideoName", null);
        }
        model.addAttribute("videoFolderPath", videoFolder);
        model.addAttribute("userName", videoService.getUserName());
        return "browser";
    }

    @PostMapping("/updateProgress")
    @ResponseBody
    public ResponseEntity<Void> updateProgress(@RequestParam String filePath,
                                               @RequestParam int seconds,
                                               HttpSession session) {
        ProfileUser currentUser = (ProfileUser) session.getAttribute("selectedUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

//        videoService.saveProgress(currentUser.getUserId(), filePath, seconds);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/stream")
    public ResponseEntity<Resource> streamVideo(
            @RequestParam String filePath,
            @RequestHeader(value = "Range", required = false) String rangeHeader, HttpSession session) throws IOException {

        Path videoPath = Paths.get(videoFolder).resolve(filePath).normalize();
        ProfileUser currentUser = (ProfileUser) session.getAttribute("selectedUser");
        System.out.println("[SYSTEM] User " + currentUser.getUsername() + " Started streaming " + filePath);

        if (!videoPath.startsWith(Paths.get(videoFolder)) || !Files.exists(videoPath) || !Files.isRegularFile(videoPath)) {
            return ResponseEntity.notFound().build();
        }

        long fileSize = Files.size(videoPath);
        long start = 0;
        long end = fileSize - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.replace("bytes=", "").split("-");
            try {
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                start = 0;
                end = fileSize - 1;
            }
            if (end > fileSize - 1) end = fileSize - 1;
        }

        long contentLength = end - start + 1;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaTypeFactory.getMediaType(videoPath.toString())
                .orElse(MediaType.APPLICATION_OCTET_STREAM));
        headers.add("Accept-Ranges", "bytes");


        InputStream inputStream = Files.newInputStream(videoPath);
        inputStream.skip(start);

        if (rangeHeader != null) {
            headers.setContentLength(contentLength);
            headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
        } else {
            headers.setContentLength(fileSize);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
        }
    }



    private static final List<Path> FORBIDDEN_PATHS = List.of(
            Paths.get("/etc"),
            Paths.get("/bin"),
            Paths.get("/sbin"),
            Paths.get("/usr"),
            Paths.get("/var"),
            Paths.get("/proc"),
            Paths.get("/sys"),
            Paths.get("/dev"),
            Paths.get("C:\\Windows"),
            Paths.get("C:\\Program Files"),
            Paths.get("C:\\Program Files (x86)"),
            Paths.get("C:\\ProgramData")
    );


    @GetMapping("/settings")
    public String settingsMenu(Model model, HttpSession session) {
        ProfileUser currentUser = (ProfileUser) session.getAttribute("selectedUser");
        if (currentUser == null) {
            return "redirect:/profiles"; // No user selected → go back to profile selection
        }
        System.out.println("[SYSTEM] User " + currentUser.getUsername() + " Navigated to endpoint /settings");

        // Add user info to model
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userName", currentUser.getUsername()); // Optional alias for convenience
        model.addAttribute("videoPath", videoService.getFolderPath());

        return "settings";
    }




    @PostMapping("/updateFolder")
    public String updateFolder(@RequestParam String folderPath, Model model, HttpSession session) {
        ProfileUser currentUser = (ProfileUser) session.getAttribute("selectedUser");

        boolean success = videoService.updateFolderPath(folderPath);
        model.addAttribute("userName", videoService.getUserName());
        model.addAttribute("videoPath", videoService.getFolderPath());
        if (!success) {
            model.addAttribute("errorMessage", "Folder is invalid or forbidden");
            model.addAttribute("folderPath", folderPath);
            System.out.println("[SYSTEM] User " + currentUser + " Failed to update folderpath");
            return "settings";
        }
        System.out.println("[SYSTEM] User " + currentUser.getUsername() + " Updated folderpath to " + folderPath);
        return "redirect:/videos/settings"; // success
    }
    @PostMapping("/updateUserName")
    public String updateUserName(@RequestParam("Username") String newUserName, HttpSession session) {
        ProfileUser currentUser = (ProfileUser) session.getAttribute("selectedUser");
        if (currentUser == null) {
            System.out.println("Error No user found in session");

        }
        System.out.println("[SYSTEM] Trying to update" + currentUser.getUsername() + " to " + newUserName);
        boolean success = videoService.updateUserName(currentUser.getUserId(), newUserName);

        if (success) {
            System.out.println("[SYSTEM] Username successfully updated to " + currentUser.getUsername());
            return "redirect:/videos/settings";
        } else {
            System.out.println("[SYSTEM] Error while updating username");
            return "redirect:/videos/browser?error=true";
        }
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> search(@RequestParam String query, HttpSession session) {
        List<Map<String, String>> results = new ArrayList<>();
        Path rootFolder = Paths.get(videoFolder);
        ProfileUser currentUser = (ProfileUser) session.getAttribute("selectedUser");

        System.out.println("[SYSTEM] User " + currentUser.getUsername() + " Navigated to search for String query " + query);
        try (Stream<Path> paths = Files.walk(rootFolder)) {
            paths.filter(p -> p.getFileName().toString().toLowerCase().contains(query.toLowerCase()))
                    .forEach(p -> {
                        Map<String, String> item = new HashMap<>();
                        item.put("name", p.getFileName().toString());
                        item.put("path", rootFolder.relativize(p).toString());
                        item.put("type", Files.isDirectory(p) ? "folder" : "video");
                        results.add(item);
                    });
            return ResponseEntity.ok(results);
        } catch (IOException e) {
            // log properly with a logger instead of System.out
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }


    @GetMapping("{errorMessage}/error")
    public String error(@PathVariable String errorMessage, Model model) {
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }


    @PostMapping("/videos/deleteUser")
    public String deleteUser(@RequestParam("user") String user) {

        return "redirect:/";
    }



}
