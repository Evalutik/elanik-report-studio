package com.example;

import java.io.File;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Window;


public class MainController {

    @FXML private MenuItem closeDatabaseMenuItem;

    @FXML private TableView<Measurement> measurementsTableView;

    @FXML private TableColumn<Measurement, Boolean> selectColumn;
    @FXML private TableColumn<Measurement, Integer> idColumn;
    @FXML private TableColumn<Measurement, String> dateTimeColumn;
    @FXML private TableColumn<Measurement, Integer> pointsNumColumn;
    @FXML private TableColumn<Measurement, Integer> baseIdColumn;
    @FXML private TableColumn<Measurement, String> commentColumn;

    private final ObservableList<Measurement> measurements = FXCollections.observableArrayList();

    @FXML private TableView<ElementData> oneMeasurementTableView;
    @FXML private TableColumn<ElementData, String> elementNameColumn;
    @FXML private TableColumn<ElementData, Float> concentrationColumn;
    @FXML private TableColumn<ElementData, Float> deviationColumn;
    @FXML private TableColumn<ElementData, String> mark1Column;
    @FXML private TableColumn<ElementData, String> mark2Column;
    @FXML private TableColumn<ElementData, String> mark3Column;

    private final ObservableList<ElementData> elementsData = FXCollections.observableArrayList();

    public void initialize() { // Called automatically at the start
        setupMeasurementsTableView();
        setupOneMeasurementTableView();

        // Bind the disable state: true if measurements list is empty
        closeDatabaseMenuItem.disableProperty().bind(
                Bindings.isEmpty(measurements)
        );


    }

    private void setupMeasurementsTableView() {
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        dateTimeColumn.setCellValueFactory(cellData -> cellData.getValue().dateTimeProperty());
        pointsNumColumn.setCellValueFactory(cellData -> cellData.getValue().pointsNumProperty().asObject());
        baseIdColumn.setCellValueFactory(cellData -> cellData.getValue().baseIdProperty().asObject());
        commentColumn.setCellValueFactory(cellData -> cellData.getValue().commentProperty());

        measurementsTableView.setEditable(true); // Allow edits on the table as a whole
        selectColumn.setEditable(true); // Allow edits on the checkbox column

        measurementsTableView.setItems(measurements);
    }

    private void setupOneMeasurementTableView() {
        elementNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        concentrationColumn.setCellValueFactory(cellData -> cellData.getValue().concentrationProperty().asObject());
        deviationColumn.setCellValueFactory(cellData -> cellData.getValue().deviationProperty().asObject());
        mark1Column.setCellValueFactory(cellData -> cellData.getValue().mark1Property());
        mark2Column.setCellValueFactory(cellData -> cellData.getValue().mark2Property());
        mark3Column.setCellValueFactory(cellData -> cellData.getValue().mark3Property());

        oneMeasurementTableView.setItems(elementsData);
    }

    @FXML
    void onAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Elanik Report Tool");
        alert.setContentText("This application generates PDF reports from Elanik measurement data.");
        alert.showAndWait();
    }

    @FXML
    void onExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void onOpenDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open SQLite Database");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SQLite Database Files", "*.sqlite3", "*.db")
        );
        Window window = measurementsTableView.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            loadMeasurementsFromDatabase(selectedFile);
        }
    }

    @FXML
    private void onCloseDatabase() {
        measurements.clear();
    }

    private void loadMeasurementsFromDatabase(File dbFile) {
        measurements.clear();
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        String query = "SELECT Id, DataTime, AvaragingsNum, BaseId, Comment, AlloyType FROM Results";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            DateTimeFormatter inputFormat = DateTimeFormatter.ISO_DATE_TIME;
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

            while (rs.next()) {
                System.out.println("Row: ID=" + rs.getInt("Id") + " " + rs.getInt("BaseId"));
                int id = rs.getInt("Id");
                String rawDate = rs.getString("DataTime");
                String formattedDate = rawDate;

                try {
                    LocalDateTime parsedDate = LocalDateTime.parse(rawDate, inputFormat);
                    formattedDate = parsedDate.format(outputFormat);
                } catch (Exception e) {
                    // Leave rawDate if parsing fails
                }

                Measurement m = new Measurement(
                        id,
                        formattedDate,
                        rs.getInt("AvaragingsNum"),
                        rs.getInt("BaseId"),
                        rs.getString("Comment"),
                        rs.getString("AlloyType")
                );

                measurements.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database Error", "Unable to load data from the selected file.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
