package com.example.hometheater.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class DesktopRepository {

    private final String filePath = "src/main/java/com/example/hometheater/repository/settings.conf";

    // Correct constructor
    public DesktopRepository() {}

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
                    line = "folderPath=\"" + newFolderPath + "\"";
                    updated = true;
                }
                updatedContent.append(line).append(System.lineSeparator());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

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
                    line = "userName=\"" + name + "\"";
                    updated = true;
                }
                updatedContent.append(line).append(System.lineSeparator());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try (PrintWriter writer = new PrintWriter(configFile)) {
            writer.print(updatedContent.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return updated;
    }
    public String getIPv4Address() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr instanceof java.net.Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

}

