package com.example.models;

public class CarbonEquivalentData {

    private final float concentration;
    private final float deviation;

    public CarbonEquivalentData(float concentration, float deviation) {
        this.concentration = concentration;
        this.deviation = deviation;
    }

    public String toString(){
        return concentration + "";
    }
}
