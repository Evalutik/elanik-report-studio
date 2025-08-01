package com.example;

import javafx.beans.property.*;

public class Measurement {
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final IntegerProperty id;
    private final StringProperty dateTime;
    private final IntegerProperty pointsNum;
    private final IntegerProperty baseId;
    private final StringProperty comment;
    private final StringProperty alloyType;


    public Measurement(int id, String dateTime, int pointsNum, int baseId, String comment, String alloyType) {
        this.id = new SimpleIntegerProperty(id);
        this.dateTime = new SimpleStringProperty(dateTime);
        this.pointsNum = new SimpleIntegerProperty(pointsNum);
        this.baseId = new SimpleIntegerProperty(baseId);
        this.comment = new SimpleStringProperty(comment);
        this.alloyType = new SimpleStringProperty(alloyType);
    }

    public BooleanProperty selectedProperty() { return selected; }
    public IntegerProperty idProperty() { return id; }
    public StringProperty dateTimeProperty() { return dateTime; }
    public IntegerProperty pointsNumProperty() { return pointsNum; }
    public IntegerProperty baseIdProperty() { return baseId; }
    public StringProperty commentProperty() { return comment; }
    public StringProperty alloyTypeProperty() { return alloyType; }

    // Getters for TableView binding via PropertyValueFactory
    public boolean isSelected() { return selected.get(); }
    public int getId() { return id.get(); }
    public String getDateTime() { return dateTime.get(); }
    public int getPointsNum() { return pointsNum.get(); }
    public int getBaseId() { return baseId.get(); }
    public String getComment() { return comment.get(); }
    public String getAlloyType() { return alloyType.get(); }
}
