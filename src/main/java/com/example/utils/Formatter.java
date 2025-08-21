package com.example.utils;

import com.example.models.ReportOptions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Formatter {
    public static String formatDateTime(String rawDate) {

        DateTimeFormatter inputFormat = DateTimeFormatter.ISO_DATE_TIME;
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        String formattedDate = rawDate;
        try {
            LocalDateTime parsedDate = LocalDateTime.parse(rawDate, inputFormat);
            formattedDate = parsedDate.format(outputFormat);
        } catch (Exception e) {
            // Leave rawDate if parsing fails
        }
        return formattedDate;
    }

    public static String formatToReportName(LocalDateTime date, ReportOptions.Format fmt) {
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = date.format(FMT);
        String extension = fmt == ReportOptions.Format.PDF ? ".pdf" : ".html";
        return "report_" + timestamp + extension;
    }

    public static String getPrefixFromFit(float fit) {
        String prefix = "";
        if (fit == 0f) {
            prefix = "";
        } else if (fit == 1.4f){
            prefix = "(✓)";
        } else if (fit == 2.8f){
            prefix = "(!✓)";
        } else if (fit == 4.2f){
            prefix = "(✕)";
        }
        return prefix;
    }

}
