package com.oslash.googledrivescrapperplugin.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
public class FileLoggingUtility {

     synchronized public static void write(Path path, String data) {

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.writeString(path, data, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Error writing Log", e);
        }
    }
}
