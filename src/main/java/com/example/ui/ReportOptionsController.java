package com.example.ui;

import com.example.models.ReportOptions;
import java.io.File;
import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.util.Optional;

import static com.example.utils.Formatter.formatToReportName;

public class ReportOptionsController {

    @FXML private ComboBox<ReportOptions.Format> formatCombo;
    @FXML private ComboBox<ReportOptions.Lang>   langCombo;
    @FXML private Button okBtn;
    @FXML private Button cancelBtn;

    // result will be set when user presses Generate
    private ReportOptions result = null;

    @FXML
    private void initialize() {
        // fill combos; default choices
        ObservableList<ReportOptions.Format> fmts = FXCollections.observableArrayList(ReportOptions.Format.PDF, ReportOptions.Format.HTML);
        formatCombo.setItems(fmts);
        formatCombo.setValue(ReportOptions.Format.PDF);

        ObservableList<ReportOptions.Lang> langs = FXCollections.observableArrayList(ReportOptions.Lang.RU, ReportOptions.Lang.EN);
        langCombo.setItems(langs);
        langCombo.setValue(ReportOptions.Lang.RU);
    }

    @FXML
    private void onOk(ActionEvent ev) {
        ReportOptions.Format fmt = formatCombo.getValue();
        ReportOptions.Lang lang  = langCombo.getValue();
        result = new ReportOptions(fmt, lang);
        // close window
        closeWindow();
    }

    @FXML
    private void onCancel(ActionEvent ev) {
        result = null;
        closeWindow();
    }

    private void closeWindow() {
        Stage st = (Stage) okBtn.getScene().getWindow();
        st.close();
    }

    /**
     * Called by caller after showAndWait() on the dialog Stage.
     * If user cancelled it returns Optional.empty().
     */
    public Optional<ReportOptions> getResult() {
        return Optional.ofNullable(result);
    }



    // Optionally add setters to preselect defaults from MainController, e.g.:
    public void setInitialFormat(ReportOptions.Format f) { formatCombo.setValue(f); }
    public void setInitialLang(ReportOptions.Lang l)     { langCombo.setValue(l); }
}
