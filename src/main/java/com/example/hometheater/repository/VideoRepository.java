package com.example.hometheater.repository;

import com.example.hometheater.utils.DatabaseUtils;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Scanner;


@Repository
public class VideoRepository {


    private final DatabaseUtils databaseUtils;
    public String filePath = "src/main/java/com/example/hometheater/repository/settings.conf";

    public VideoRepository(DatabaseUtils databaseUtils) {
        this.databaseUtils = databaseUtils;
    }


    public String getFolderPath() {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith("#")) continue;


                if (line.startsWith("folderPath=")) {

                    String value = line.substring("folderPath=".length()).trim();
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    return value;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean updateFolderPath(String newFolderPath) {
        File configFile = new File(filePath);
        StringBuilder updatedContent = new StringBuilder();
        boolean updated = false;

        try (Scanner scanner = new Scanner(configFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("folderPath=")) {
                    // Replace the old folderPath with the new one
                    line = "folderPath=\"" + newFolderPath + "\"";
                    updated = true;
                }
                updatedContent.append(line).append(System.lineSeparator());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        // Write updated content back to the file
        try (PrintWriter writer = new PrintWriter(configFile)) {
            writer.print(updatedContent.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return updated;
    }


    public String getUserName() {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith("#")) continue;


                if (line.startsWith("userName=")) {

                    String value = line.substring("userName=".length()).trim();
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    return value;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserName(String name) {
        File configFile = new File(filePath);
        StringBuilder updatedContent = new StringBuilder();
        boolean updated = false;

        try (Scanner scanner = new Scanner(configFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("userName=")) {
                    // Replace the old folderPath with the new one
                    line = "userName=\"" + name + "\"";
                    updated = true;
                }
                updatedContent.append(line).append(System.lineSeparator());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        // Write updated content back to the file
        try (PrintWriter writer = new PrintWriter(configFile)) {
            writer.print(updatedContent.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return updated;
    }
    public void deleteUser(String username) throws SQLException {
        databaseUtils.deleteUser(username);
    }

}
