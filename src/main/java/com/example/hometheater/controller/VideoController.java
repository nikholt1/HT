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
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

        // Validate file path
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
        headers.setContentLength(contentLength);
        headers.add("Accept-Ranges", "bytes");

        if (rangeHeader != null) {
            headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
            headers.add("Connection", "keep-alive");

            RandomAccessFile raf = new RandomAccessFile(videoPath.toFile(), "r");
            raf.seek(start);
            InputStream inputStream = new InputStream() {
                private long bytesRemaining = contentLength;

                @Override
                public int read() throws IOException {
                    if (bytesRemaining <= 0) return -1;
                    int b = raf.read();
                    if (b != -1) bytesRemaining--;
                    return b;
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    if (bytesRemaining <= 0) return -1;
                    len = (int) Math.min(len, bytesRemaining);
                    int read = raf.read(b, off, len);
                    if (read != -1) bytesRemaining -= read;
                    return read;
                }

                @Override
                public void close() throws IOException {
                    raf.close();
                    super.close();
                }
            };

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
        } else {
            InputStream inputStream = Files.newInputStream(videoPath);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
        }
    }



    @PostMapping("/setFolder")
    public ResponseEntity<String> setFolder(@RequestParam String folderPath) {
        Path path = Paths.get(folderPath);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return ResponseEntity.badRequest().body("Folder does not exist or is not a directory.");
        }
        this.videoFolder = folderPath;
        return ResponseEntity.ok("Folder updated to: " + folderPath);
    }

    @GetMapping("/settings")
    public String settingsMenu(Model model) {
        String username = videoService.getUserName();

        model.addAttribute("videoPath", videoFolder);
        model.addAttribute("userName", username);

        return "settings";
    }



    @PostMapping("/updateFolder")
    public String updateFolder(@RequestParam String folderPath) {
        videoService.updateFolderPath(folderPath);
        boolean success = videoService.updateFolderPath(folderPath);
        if (success) {
            return "redirect:/videos/settings";
        } else {
            return "redirect:/videos/browser?error=true";
        }
    }
    @PostMapping("/updateUserName")
    public String updateUserName(@RequestParam("Username") String userName) {
        boolean success = videoService.updateUserName(userName);
        // Redirect back to the main browser page (adjust URL as needed)
        if (success) {
            return "redirect:/videos/settings";
        } else {
            return "redirect:/videos/browser?error=true";
        }
    }

    @GetMapping("/search")
    @ResponseBody
    public List<Map<String, String>> search(@RequestParam String query) throws IOException {
        List<Map<String, String>> results = new ArrayList<>();
        Path rootFolder = Paths.get(videoFolder);
        Files.walk(rootFolder)
                .filter(p -> p.getFileName().toString().toLowerCase().contains(query.toLowerCase()))
                .forEach(p -> {
                    Map<String, String> item = new HashMap<>();
                    item.put("name", p.getFileName().toString());
                    item.put("path", rootFolder.relativize(p).toString());
                    item.put("type", Files.isDirectory(p) ? "folder" : "video");
                    results.add(item);
                });

        return results;
    }





}
