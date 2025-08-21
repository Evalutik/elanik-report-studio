package com.example.models;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public record Report(List<Measurement> measurements, File outputFile, String serial, LocalDateTime creationDateTime, ReportOptions options) {
}
