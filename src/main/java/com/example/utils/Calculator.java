package com.example.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Calculator {
    public static float concentration(float min, float max) {
        return (max + min) / 2;
    }

    public static float deviation(float min, float max) {
        return (max - min) / 2;
    }


    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException("places must be non-negative");

        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public static float twoSigFigsMantissa(float value) {
        // 1) Round to 2 significant figures
        BigDecimal bd = new BigDecimal(value, new MathContext(2, RoundingMode.HALF_UP));
        // 2) Get the scientific string, e.g. "1.4E-45"
        String sci = bd.toString();
        // 3) Strip off the exponent if present
        int e = sci.indexOf('E');
        String mantissa = (e >= 0) ? sci.substring(0, e) : sci;
        // 4) Parse back to float
        return Float.parseFloat(mantissa);
    }
}
