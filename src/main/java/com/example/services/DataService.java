package com.example.services;

import com.example.models.ElementData;
import com.example.models.ElementPercentageFitStore;
import com.example.models.Measurement;
import com.example.models.TypeAlloyMatch;
import com.example.utils.DatabaseConnection;
import com.example.utils.PeriodicTable;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;

import static com.example.domain.BlobDecoder.decodeElements;
import static com.example.domain.BlobDecoder.decodeMatches;
import static com.example.domain.ElementDataMapFactory.getElementDataMap;

/**
 * Coordinates reading measurements and per-measurement element data.
 */
public class DataService {


    public static void loadMeasurementsFromDatabase(ObservableList<Measurement> measurements) throws SQLException, NullPointerException, SecurityException {
        measurements.clear();

        String query = "SELECT Id, DataTime, AvaragingsNum, BaseId, Comment, AlloyType FROM Results";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String dateTime = formatDateTime(rs.getString("DataTime"));
                String baseElementName = PeriodicTable.getElementName(rs.getInt("BaseId"));

                Measurement m = new Measurement(
                        rs.getInt("Id"),
                        dateTime,
                        rs.getInt("AvaragingsNum"),
                        baseElementName,
                        rs.getString("AlloyType"),
                        rs.getString("Comment")
                );

                measurements.add(m);
            }
        }
    }

    /**
     * Called whenever the user selects a row in the left table.
     * Rebuilds `elementsData`.
     */
    public static void loadElementsForMeasurement(Measurement measurement, ObservableList<ElementData> elementsData) throws SQLException, NullPointerException, SecurityException {
        elementsData.clear();
        int resultId = measurement.getId();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1) fetch the two blobs
            byte[] blobMatch, blobEl;
            String sql = "SELECT MatchAlloys, MatchAlloysElements FROM Results WHERE Id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, resultId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return;  // no data
                    blobMatch = rs.getBytes(1);
                    blobEl    = rs.getBytes(2);
                }
            }

            // 2) decode into Java objects
            List<TypeAlloyMatch> matches = decodeMatches(blobMatch);
            List<ElementPercentageFitStore> elements = decodeElements(blobEl);

            updateAlloyNames(measurement, matches);

            Map<Integer, ElementData> byIndex = getElementDataMap(matches, elements, resultId);
            // 3) push into the observable list
            elementsData.addAll(byIndex.values());
        }
    }

    private static void updateAlloyNames(Measurement measurement, List<TypeAlloyMatch> matches) throws SQLException, SecurityException {
        measurement.getAlloyNames().clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT Name FROM MatchAlloyNames WHERE Id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (TypeAlloyMatch match : matches) {
                    ps.setInt(1, match.nameId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            measurement.getAlloyNames().add(rs.getString("Name"));
                        } else {
                            measurement.getAlloyNames().add("Unknown alloy"); // no data
                        }
                    }
                }
            }

        }
    }

    private static String formatDateTime(String rawDate) {

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
