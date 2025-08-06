package com.example.ui;

import com.example.models.ElementData;
import com.example.models.Measurement;
import java.io.File;
import java.sql.*;
import java.util.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import static com.example.services.LoadDataService.*;


public class MainController {

    @FXML private MenuItem closeDatabaseMenuItem;

    @FXML private TableView<Measurement> measurementsTableView;

    @FXML private TableColumn<Measurement, Boolean> selectColumn;
    @FXML private TableColumn<Measurement, Integer> idColumn;
    @FXML private TableColumn<Measurement, String> dateTimeColumn;
    @FXML private TableColumn<Measurement, Integer> pointsNumColumn;
    @FXML private TableColumn<Measurement, String> baseElementNameColumn;
    @FXML private TableColumn<Measurement, String> alloyTypeColumn;
    @FXML private TableColumn<Measurement, String> commentColumn;

    private final ObservableList<Measurement> measurements = FXCollections.observableArrayList();

    @FXML private TableView<ElementData> oneMeasurementTableView;
    @FXML private TableColumn<ElementData, String> elementNameColumn;
    @FXML private TableColumn<ElementData, Float> concentrationColumn;
    @FXML private TableColumn<ElementData, Float> deviationColumn;
    @FXML private TableColumn<ElementData, String> alloy1Column;
    @FXML private TableColumn<ElementData, String> alloy2Column;
    @FXML private TableColumn<ElementData, String> alloy3Column;

    private final ObservableList<ElementData> elementsData = FXCollections.observableArrayList();

    private static File currentDbFile = null;

    public void initialize() { // Called automatically at the start
        setupMeasurementsTableView();
        setupOneMeasurementTableView();
        setupSelectionListener();

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
        baseElementNameColumn.setCellValueFactory(cellData -> cellData.getValue().baseElementNameProperty());
        alloyTypeColumn.setCellValueFactory(cellData -> cellData.getValue().alloyTypeProperty());
        commentColumn.setCellValueFactory(cellData -> cellData.getValue().commentProperty());

        measurementsTableView.setEditable(true); // Allow edits on the table as a whole
        selectColumn.setEditable(true); // Allow edits on the checkbox column

        measurementsTableView.setItems(measurements);
    }

    private void setupOneMeasurementTableView() {
        elementNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        concentrationColumn.setCellValueFactory(cellData -> cellData.getValue().concentrationProperty().asObject());
        deviationColumn.setCellValueFactory(cellData -> cellData.getValue().deviationProperty().asObject());
        alloy1Column.setCellValueFactory(cellData -> cellData.getValue().alloy1Property());
        alloy2Column.setCellValueFactory(cellData -> cellData.getValue().alloy2Property());
        alloy3Column.setCellValueFactory(cellData -> cellData.getValue().alloy3Property());

        oneMeasurementTableView.setItems(elementsData);
    }

    private void setupSelectionListener() {
        measurementsTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        try {
                            loadFullDataForMeasurement(newSel, elementsData, alloy1Column, alloy2Column, alloy3Column);
                        } catch (SQLException | NullPointerException e) {
                            e.printStackTrace();
                            showError("Database Error", "Unable to load data from the selected file.");
                        } catch (SecurityException e) {
                            e.printStackTrace();
                            showError("Permission Error", "Unable to load open the selected file. Try to run the program as administrator.");
                        }
                    }
                });
    }

    @FXML
    private void onOpenDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open SQLite Database");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SQLite Database Files", "*.sqlite3", "*.db")
        );
        Window window = measurementsTableView.getScene().getWindow();
        setCurrentDbFile(fileChooser.showOpenDialog(window));

        if (getCurrentDbFile() != null) {
            try {
                loadMeasurementsFromDatabase(measurements);
            } catch (SQLException | NullPointerException e) {
                e.printStackTrace();
                showError("Database Error", "Unable to load data from the selected file.");
            } catch (SecurityException e) {
                e.printStackTrace();
                showError("Permission Error", "Unable to load open the selected file. Try to run the program as administrator.");
            }
        }
    }

    @FXML
    private void onCloseDatabase() {
        setCurrentDbFile(null);
        measurements.clear();
        elementsData.clear();
        loadAlloyNamesColumns(List.of(), alloy1Column, alloy2Column, alloy3Column); // Reset column headers to M1, M2, M3
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



    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static File getCurrentDbFile() { return currentDbFile; }
    public void setCurrentDbFile(File dbFile) { currentDbFile = dbFile; }
}
