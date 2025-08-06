package com.example.models;

import javafx.beans.property.*;

public class ElementData {
    private final StringProperty name;
    private final FloatProperty concentration;
    private final FloatProperty deviation;
    private final StringProperty alloyType1;
    private final StringProperty alloyType2;
    private final StringProperty alloyType3;

    public ElementData(String name, float concentration, float deviation, String mark1, String mark2, String mark3) {
        this.name = new SimpleStringProperty(name);
        this.concentration = new SimpleFloatProperty(concentration);
        this.deviation = new SimpleFloatProperty(deviation);
        this.alloyType1 = new SimpleStringProperty(mark1);
        this.alloyType2 = new SimpleStringProperty(mark2);
        this.alloyType3 = new SimpleStringProperty(mark3);
    }

    public StringProperty nameProperty() {return name;}
    public FloatProperty concentrationProperty() {return concentration;}
    public FloatProperty deviationProperty() {return deviation;}
    public StringProperty alloy1Property() {return alloyType1;}
    public StringProperty alloy2Property() {return alloyType2;}
    public StringProperty alloy3Property() {return alloyType3;}

    public void setAlloy(int position, String alloyData) {
        switch (position) {
            case 1 -> alloyType1.set(alloyData);
            case 2 -> alloyType2.set(alloyData);
            case 3 -> alloyType3.set(alloyData);
        }
    }
}
