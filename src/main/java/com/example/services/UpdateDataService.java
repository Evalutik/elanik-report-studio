package com.example.services;

import com.example.models.*;
import com.example.utils.Calculator;
import com.example.utils.DatabaseConnection;
import java.sql.*;
import java.util.List;
import java.util.Map;

import static com.example.domain.BlobDecoder.decodeElements;
import static com.example.domain.BlobDecoder.decodeMatches;
import static com.example.domain.ElementDataMapFactory.getElementDataMap;
import static com.example.services.SortDataService.sortElementsData;
import static com.example.utils.Formatter.getPrefixFromFit;

public class UpdateDataService {

    public static void updateFullDataForMeasurement(Measurement measurement) throws SQLException, NullPointerException, SecurityException {
        updateElementsForMeasurement(measurement);
        updateCEForMeasurement(measurement);
        // update anything else
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

            Map<Integer, ElementData> indexedElementsData = getElementDataMap(matches, elements, resultId);
            List<ElementData> sortedElementsData = sortElementsData(indexedElementsData, measurement.getBaseElementName());

            measurement.getElementsData().clear();
            measurement.getElementsData().addAll(sortedElementsData);
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
                            String prefix = getPrefixFromFit(match.fit());
                            measurement.getAlloyNames().add(prefix + " " + rs.getString("Name"));
                        } else {
                            measurement.getAlloyNames().add("Unknown alloy"); // no data
                        }
                    }
                }
            }

        }
    }

    private static void updateCEForMeasurement(Measurement measurement) throws SQLException, NullPointerException {
        int resultId = measurement.getId();
        try (Connection conn = DatabaseConnection.getConnection()){
            String query = "SELECT Min, Max FROM CE WHERE ResultId = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, resultId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        float min = rs.getFloat(1);
                        float max = rs.getFloat(2);
                        measurement.setCE(new CarbonEquivalentData(
                                Calculator.round(Calculator.concentration(min, max), 3),
                                Calculator.round(Calculator.deviation(min, max), 3)
                        ));
                    } else { // Data not found in the CE table
                        measurement.setCE(null);
                    }
                }

            }
        }
    }

    public static String getSerial() throws SQLException, NullPointerException {
        String query = "SELECT \"Value\" FROM Descriptor WHERE Identifier = 'Serial'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)
        ) {
            if (rs.next()) {
                return rs.getString(1);
            } else { // Data not found in the Descriptor table
                return "Not data";
            }
        }
    }
}
