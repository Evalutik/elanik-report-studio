package com.example;

import javafx.beans.property.*;

public class ElementData {
    private final StringProperty name;
    private final FloatProperty concentration;
    private final FloatProperty deviation;
    private final StringProperty mark1;
    private final StringProperty mark2;
    private final StringProperty mark3;

    public ElementData(String name, float concentration, float deviation, String mark1, String mark2, String mark3) {
        this.name = new SimpleStringProperty(name);
        this.concentration = new SimpleFloatProperty(concentration);
        this.deviation = new SimpleFloatProperty(deviation);
        this.mark1 = new SimpleStringProperty(mark1);
        this.mark2 = new SimpleStringProperty(mark2);
        this.mark3 = new SimpleStringProperty(mark3);
    }

    public StringProperty nameProperty() {return name;}
    public FloatProperty concentrationProperty() {return concentration;}
    public FloatProperty deviationProperty() {return deviation;}
    public StringProperty mark1Property() {return mark1;}
    public StringProperty mark2Property() {return mark2;}
    public StringProperty mark3Property() {return mark3;}
}
