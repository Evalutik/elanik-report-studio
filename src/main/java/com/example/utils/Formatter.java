package com.example.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Formatter {
    public static String formatDateTime(String rawDate) {

        DateTimeFormatter inputFormat = DateTimeFormatter.ISO_DATE_TIME;
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

        String formattedDate = rawDate;
        try {
            LocalDateTime parsedDate = LocalDateTime.parse(rawDate, inputFormat);
            formattedDate = parsedDate.format(outputFormat);
        } catch (Exception e) {
            // Leave rawDate if parsing fails
        }
        return formattedDate;
    }
}
