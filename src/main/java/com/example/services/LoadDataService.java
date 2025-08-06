package com.example.services;

import com.example.models.ElementData;
import com.example.models.Measurement;
import com.example.utils.DatabaseConnection;
import com.example.utils.PeriodicTable;
import java.sql.*;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

import static com.example.services.UpdateDataService.updateFullDataForMeasurement;
import static com.example.utils.Formatter.formatDateTime;

/**
 * Coordinates reading measurements and per-measurement element data.
 */
public class LoadDataService {


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
    public static void loadFullDataForMeasurement(Measurement measurement,
                                                  Label ceLabel,
                                                  ObservableList<ElementData> elementsData,
                                                  TableColumn<ElementData, String> alloy1Column,
                                                  TableColumn<ElementData, String> alloy2Column,
                                                  TableColumn<ElementData, String> alloy3Column
    ) throws SQLException, NullPointerException, SecurityException {
        updateFullDataForMeasurement(measurement);

        elementsData.clear();
        elementsData.addAll(measurement.getElementsData());

        loadCELabel(measurement, ceLabel);

        // Rename column headers
        loadAlloyNamesColumns(measurement.getAlloyNames(), alloy1Column, alloy2Column, alloy3Column);
    }

    /**
     * Updates the M1–M3 column headers to the first three alloy names
     * from the given list.
     */
    public static void loadAlloyNamesColumns(List<String> names,
                                      TableColumn<ElementData, String> alloy1Column,
                                      TableColumn<ElementData, String> alloy2Column,
                                      TableColumn<ElementData, String> alloy3Column) {
        // Pick off up to the first three names, or M... if missing
        String name1 = names.size() >= 1 ? names.get(0) : "M1";
        String name2 = names.size() >= 2 ? names.get(1) : "M2";
        String name3 = names.size() >= 3 ? names.get(2) : "M3";

        // Assuming your fx:id fields are these:
        alloy1Column.setText(name1);
        alloy2Column.setText(name2);
        alloy3Column.setText(name3);
    }

    public static void loadCELabel(Measurement measurement, Label ceLabel ) {
        if (measurement.getCE() == null){
            ceLabel.setText("Данные об углеродом эквиваленте отсутствуют.");
        } else {
            ceLabel.setText("Углеродный эквивалент (CE): " + measurement.getCE().toString());
        }
    }

}
