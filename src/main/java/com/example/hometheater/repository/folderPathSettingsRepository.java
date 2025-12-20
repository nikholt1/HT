package com.example.hometheater.repository;

import org.springframework.beans.factory.annotation.Value;

public class folderPathSettingsRepository {


    @Value("${app.folder-path}")
    private String folderPath;
    public String getFolderPath() {
//        try (Scanner scanner = new Scanner(new File(filePath))) {
//            while (scanner.hasNextLine()) {
//                String line = scanner.nextLine().trim();
//                if (line.isEmpty() || line.startsWith("#")) continue;
//
//                if (line.startsWith("folderPath=")) {
//                    String value = line.substring("folderPath=".length()).trim();
//                    if (value.startsWith("\"") && value.endsWith("\"")) {
//                        value = value.substring(1, value.length() - 1);
//                    }
//                    return value;
//                }
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
        return folderPath;
    }

    public boolean updateFolderPath(String newFolderPath) {
//        File configFile = new File(filePath);
//        StringBuilder updatedContent = new StringBuilder();
//        boolean updated = false;
//
//        try (Scanner scanner = new Scanner(configFile)) {
//            while (scanner.hasNextLine()) {
//                String line = scanner.nextLine();
//                if (line.startsWith("folderPath=")) {
//                    line = "folderPath=\"" + newFolderPath + "\"";
//                    updated = true;
//                }
//                updatedContent.append(line).append(System.lineSeparator());
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        }
//
//        try (PrintWriter writer = new PrintWriter(configFile)) {
//            writer.print(updatedContent.toString());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        }
//
//        return updated;
        try {
            this.folderPath = newFolderPath;
            return true;
        } catch (Exception ex) {
            return false;
        }

    }
}
