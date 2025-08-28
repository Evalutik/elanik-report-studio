package com.example.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.*;

public class ElementData {
    private final StringProperty name;
    private final StringProperty concentration;
    private final StringProperty deviation;
    private final StringProperty alloyType1;
    private final StringProperty alloyType2;
    private final StringProperty alloyType3;

    public ElementData(String name, String concentration, String deviation, String mark1, String mark2, String mark3) {
        this.name = new SimpleStringProperty(name);
        this.concentration = new SimpleStringProperty(concentration);
        this.deviation = new SimpleStringProperty(deviation);
        this.alloyType1 = new SimpleStringProperty(mark1);
        this.alloyType2 = new SimpleStringProperty(mark2);
        this.alloyType3 = new SimpleStringProperty(mark3);
    }

    public StringProperty nameProperty() {return name;}
    public StringProperty concentrationProperty() {return concentration;}
    public StringProperty deviationProperty() {return deviation;}
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
    public void setConcentration(String concentration) {
        this.concentration.setValue(concentration);
    }
    public void setDeviation(String deviation) {
        this.deviation.setValue(deviation);
    }



    public String getName() {return name.getValue();}
    public String getConcentration() {return concentration.getValue();}
    public float getConcentrationFloat() {
        try {
            return Float.parseFloat(concentration.getValue());
        } catch (NumberFormatException e) {return 0f;}
    }
    public String getDeviation() {return deviation.getValue();}
    public String getAlloyType1() {return alloyType1.getValue();}
    public String getAlloyType2() {return alloyType2.getValue();}
    public String getAlloyType3() {return alloyType3.getValue();}
    public ArrayList<String> getAlloys() {
        return new ArrayList<>(Arrays.asList(getAlloyType1(), getAlloyType2(), getAlloyType3()));
    }
}
