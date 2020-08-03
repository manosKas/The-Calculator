package com.manolee.thecalculator;

import android.annotation.SuppressLint;

import static com.manolee.thecalculator.Constants.ADD;
import static com.manolee.thecalculator.Constants.DIV;
import static com.manolee.thecalculator.Constants.MUL;
import static com.manolee.thecalculator.Constants.SUB;

public class Helpers {

    // Returns last char
    public static String PreviousCharacter(String s) {
        if (s.length() > 0)
            return String.valueOf(s.charAt(s.length() - 1));
        else
            return "";
    }

    // Returns true if token is operator
    public static boolean isOperator(String token) {
        return token.equals(ADD) || token.equals(SUB) || token.equals(DIV) || token.equals(MUL);
    }

    // Formats the answer and adds context info in some cases
    @SuppressLint("DefaultLocale")
    public static String formatResult(double d) {
        if (Double.isInfinite(d)) return "Can't divide by Zero.";
        if (Double.isNaN(d)) return "Not a number";
        if (d == (long) d) return String.format("%d", (long) d);
        else return String.format("%s", d);
    }
}
