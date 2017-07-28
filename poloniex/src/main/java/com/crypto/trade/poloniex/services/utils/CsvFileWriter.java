package com.crypto.trade.poloniex.services.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class CsvFileWriter {

    private static String OS = System.getProperty("os.name").toLowerCase();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void write(String name, StringBuilder data) {
        write(name, data, false);
    }

    public void write(String name, StringBuilder data, boolean append) {
        BufferedWriter writer = null;
        String str = OS.contains("win") ? data.toString().replaceAll("\\,", "\\;").replaceAll("\\.", "\\,") : data.toString();
        try {
            writer = new BufferedWriter(new FileWriter(name + "_" + LocalDateTime.now().format(formatter) + ".csv", append));
            writer.write(str);
        } catch (IOException ioe) {
            log.error("Failed to write csv", ioe);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ioe) {
                log.error("Failed to close writer.", ioe);
            }
        }
    }
}
