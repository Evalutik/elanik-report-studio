package com.example.services;

import com.example.models.ElementData;
import com.example.models.ElementPercentageFitStore;
import com.example.models.Measurement;
import com.example.models.TypeAlloyMatch;
import com.example.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.example.domain.BlobDecoder.decodeElements;
import static com.example.domain.BlobDecoder.decodeMatches;
import static com.example.domain.ElementDataMapFactory.getElementDataMap;

public class UpdateDataService {
    public static void updateFullDataForMeasurement(Measurement measurement) throws SQLException, NullPointerException, SecurityException {
        updateElementsForMeasurement(measurement);
        //updates anything else
    }


    private static void updateElementsForMeasurement(Measurement measurement) throws SQLException, NullPointerException, SecurityException {
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

            // reoderd

            measurement.getElementsData().clear();
            measurement.getElementsData().addAll(byIndex.values());
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
}
