package com.example.hometheater.controller;

import com.example.hometheater.service.VideoService;
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


    public VideoController(VideoService videoService) {
        this.videoService = videoService;
        this.videoFolder = videoService.getFolderPath();
    }

    @GetMapping("/browser")
    public String browseVideos(@RequestParam(required = false) String path, Model model) throws IOException {
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



    @GetMapping("/stream")
    public ResponseEntity<Resource> streamVideo(
            @RequestParam String filePath,
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

        Path videoPath = Paths.get(videoFolder).resolve(filePath).normalize();


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

//    @PostMapping("/setFolder")
//    public ResponseEntity<String> setFolder(@RequestParam String folderPath) {
//        Path path = Paths.get(folderPath).normalize().toAbsolutePath();
//
//        // Must exist and be a directory
//        if (!Files.exists(path) || !Files.isDirectory(path)) {
//            return ResponseEntity.badRequest().body("Folder does not exist or is not a directory.");
//        }
//
//        // Block known system folders
//        for (Path forbidden : FORBIDDEN_PATHS) {
//            if (path.startsWith(forbidden)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body("Access to system directories is not allowed.");
//            }
//        }
//
//        this.videoFolder = path.toString();
//        return ResponseEntity.ok("Folder updated to: " + path);
//    }


    @GetMapping("/settings")
    public String settingsMenu(Model model) {
        String username = videoService.getUserName();

        model.addAttribute("videoPath", videoService.getFolderPath());
        model.addAttribute("userName", videoService.getUserName());

        return "settings";
    }



    @PostMapping("/updateFolder")
    public String updateFolder(@RequestParam String folderPath, Model model) {
        boolean success = videoService.updateFolderPath(folderPath);
        model.addAttribute("userName", videoService.getUserName());
        model.addAttribute("videoPath", videoService.getFolderPath());
        if (!success) {
            model.addAttribute("errorMessage", "Folder is invalid or forbidden");
            model.addAttribute("folderPath", folderPath);
            return "settings";
        }
        return "redirect:/videos/settings"; // success
    }
    @PostMapping("/updateUserName")
    public String updateUserName(@RequestParam("Username") String userName) {
        boolean success = videoService.updateUserName(userName);

        if (success) {
            return "redirect:/videos/settings";
        } else {
            return "redirect:/videos/browser?error=true";
        }
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> search(@RequestParam String query) {
        List<Map<String, String>> results = new ArrayList<>();
        Path rootFolder = Paths.get(videoFolder);

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





}
