package com.example.ui;

import com.example.domain.MessageFactory;
import com.example.models.ElementData;
import com.example.models.Measurement;
import com.example.models.Report;
import com.example.services.ReportService;
import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
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
import static com.example.services.UpdateDataService.getSerial;
import static com.example.services.UpdateDataService.updateFullDataForMeasurement;
import static com.example.utils.Formatter.formatToReportName;


public class MainController {

    private static File currentDbFile = null;
    public Label measurementCE;

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
                        loadFullDataForMeasurement(newSel, measurementCE, elementsData, alloy1Column, alloy2Column, alloy3Column);
                    } catch (SQLException | NullPointerException e) {
                        e.printStackTrace();
                        showError("error.db.title", "error.db.message");
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        showError("error.noPermission.title", "error.noPermission.message");
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
                showError("error.db.title", "error.db.message");
            } catch (SecurityException e) {
                e.printStackTrace();
                showError("error.noPermission.title", "error.noPermission.message");
            }
        }
    }

    @FXML
    private void onCloseDatabase() {
        setCurrentDbFile(null);
        measurements.clear();
        elementsData.clear();
        loadAlloyNamesColumns(List.of(), alloy1Column, alloy2Column, alloy3Column); // Reset column headers to M1, M2, M3
        measurementCE.setText("No measurement chosen");
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


    public static File getCurrentDbFile() { return currentDbFile; }
    public void setCurrentDbFile(File dbFile) { currentDbFile = dbFile; }

    public void onGenerateReport(ActionEvent actionEvent) {
        LocalDateTime creationDateTime = LocalDateTime.now();
        List<Measurement> selectedMeasurements = measurements.filtered(Measurement::isSelected);

        if (selectedMeasurements.isEmpty()) {
            showError(MessageFactory.get("error.noDataSelected.title"), MessageFactory.get( "error.noDataSelected.message"));
            return;
        }
        try{
            for (Measurement measurement : selectedMeasurements) {
                updateFullDataForMeasurement(measurement);
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save PDF Report");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            chooser.setInitialFileName(formatToReportName(creationDateTime)); // Prefill the file name
            Window window = measurementsTableView.getScene().getWindow();
            File outputFile = chooser.showSaveDialog(window);
            if (outputFile == null) { return; }

            try {
                Report report = new Report(selectedMeasurements, outputFile, getSerial(), creationDateTime);
                ReportService.generate(report);
                showInfo("Report Generated", "Your PDF report was saved to:\n" + outputFile.getAbsolutePath());
            }
            catch (Exception e) {
                e.printStackTrace();
                showError("Report Error", "Failed to generate or save the report:\n" + e.getMessage());
            }

        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            showError("error.db.title", "error.db.message");
        } catch (SecurityException e) {
            e.printStackTrace();
            showError("error.noPermission.title", "error.noPermission.message");
        }
    }

    private void showInfo(String title, String message) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle(title);
        info.setHeaderText(null);
        info.setContentText(message);
        info.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
