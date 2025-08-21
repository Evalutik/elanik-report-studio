package com.example.ui;

import com.example.domain.MessageFactory;
import com.example.models.ElementData;
import com.example.models.Measurement;
import com.example.models.Report;
import com.example.models.ReportOptions;
import com.example.services.ReportService;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
                        showError(MessageFactory.get( "error.db.title"), MessageFactory.get( "error.db.message"));
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        showError(MessageFactory.get("error.noPermission.title"), MessageFactory.get( "error.noPermission.message"));
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
                showError(MessageFactory.get("error.db.title"), MessageFactory.get("error.db.message"));
            } catch (SecurityException e) {
                e.printStackTrace();
                showError( MessageFactory.get("error.noPermission.title"), MessageFactory.get("error.noPermission.message"));
            }
        }
    }

    @FXML
    private void onCloseDatabase() {
        setCurrentDbFile(null);
        measurements.clear();
        elementsData.clear();
        loadAlloyNamesColumns(List.of(), alloy1Column, alloy2Column, alloy3Column); // Reset column headers to M1, M2, M3
        measurementCE.setText(MessageFactory.get("ui.noMeasurementChosen.message"));
    }

    @FXML
    void onAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(MessageFactory.get( "ui.aboutProgram.title"));
        alert.setContentText(MessageFactory.get( "ui.aboutProgram.message"));
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

        Optional<ReportOptions> opt = showReportOptionsDialogFXML();
        if (opt.isEmpty()) return; // user cancelled

        ReportOptions options = opt.get();


        try {
            for (Measurement measurement : selectedMeasurements) {
                updateFullDataForMeasurement(measurement);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            showError(MessageFactory.get("error.db.title"), MessageFactory.get("error.db.message"));
            return;
        }

        File outputFile = chooseOutputFile(options.getFormat(), creationDateTime);
        if (outputFile == null) return;

        try {
            Report report = new Report(selectedMeasurements, outputFile, getSerial(), creationDateTime, options);
            ReportService.generate(report);
            showInfo(MessageFactory.get("info.reportGenerated.title"), MessageFactory.get("info.reportGenerated.message", outputFile.getAbsolutePath()));
        }
        catch (Exception e) {
            e.printStackTrace();
            showError(MessageFactory.get("error.reportGeneration.title"), MessageFactory.get("error.reportGeneration.message", e.getMessage()));
        }
    }

    private Optional<ReportOptions> showReportOptionsDialogFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/report-options.fxml"));
            Parent root = loader.load();

            ReportOptionsController ctrl = loader.getController();
            // you can pre-set defaults if desired:
            ctrl.setInitialFormat(ReportOptions.Format.PDF);
            ctrl.setInitialLang(ReportOptions.Lang.RU);

            Stage dialog = new Stage();
            dialog.initOwner(measurementsTableView.getScene().getWindow()); // block the main window
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Report Options");
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);

            dialog.showAndWait(); // this blocks until dialog closes

            return ctrl.getResult();
        } catch (IOException ex) {
            ex.printStackTrace();
            showError(MessageFactory.get("error.dialog.title"), MessageFactory.get("error.dialog.message"));
            return Optional.empty();
        }
    }

    private File chooseOutputFile(ReportOptions.Format fmt, LocalDateTime creationDateTime) {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose report location...");
            chooser.setInitialFileName(formatToReportName(creationDateTime, fmt));
            if (fmt == ReportOptions.Format.PDF) {
                chooser.getExtensionFilters()
                        .add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            } else {
                chooser.getExtensionFilters()
                        .add(new FileChooser.ExtensionFilter("HTML Files", "*.html", "*.htm"));
            }
            return chooser.showSaveDialog(measurementsTableView.getScene().getWindow());
        } catch (SecurityException e) {
            e.printStackTrace();
            showError(MessageFactory.get("error.noPermission.title"), MessageFactory.get("error.noPermission.message"));
            return null;
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
