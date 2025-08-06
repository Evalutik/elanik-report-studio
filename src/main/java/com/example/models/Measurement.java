package com.example.models;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.*;

public class Measurement {
    private CarbonEquivalentData ce = null;

    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final IntegerProperty id;
    private final StringProperty dateTime;
    private final IntegerProperty pointsNum;
    private final StringProperty baseElementName;
    private final StringProperty alloyType;
    private final StringProperty comment;

    private final List<String> alloyNames = new ArrayList<>();

    private final List<ElementData> elementsData = new ArrayList<>();

    public Measurement(int id, String dateTime, int pointsNum, String baseElementName, String comment, String alloyType) {
        this.id = new SimpleIntegerProperty(id);
        this.dateTime = new SimpleStringProperty(dateTime);
        this.pointsNum = new SimpleIntegerProperty(pointsNum);
        this.baseElementName = new SimpleStringProperty(baseElementName);
        this.alloyType = new SimpleStringProperty(alloyType);
        this.comment = new SimpleStringProperty(comment);
    }

    public BooleanProperty selectedProperty() { return selected; }
    public IntegerProperty idProperty() { return id; }
    public StringProperty dateTimeProperty() { return dateTime; }
    public IntegerProperty pointsNumProperty() { return pointsNum; }
    public StringProperty baseElementNameProperty() { return baseElementName; }
    public StringProperty alloyTypeProperty() { return alloyType; }
    public StringProperty commentProperty() { return comment; }

    // Getters for TableView binding via PropertyValueFactory
    public boolean isSelected() { return selected.get(); }
    public int getId() { return id.get(); }
    public String getDateTime() { return dateTime.get(); }
    public int getPointsNum() { return pointsNum.get(); }
    public String getBaseElementName() { return baseElementName.get(); }
    public String getAlloyType() { return alloyType.get(); }
    public String getComment() { return comment.get(); }
    public List<ElementData> getElementsData() { return elementsData; }

    public List<String> getAlloyNames() {
        return alloyNames;
    }

    public CarbonEquivalentData getCE() {
        return ce;
    }
    public void setCE(CarbonEquivalentData ce) { this.ce = ce; }
}
