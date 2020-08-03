package com.manolee.thecalculator;

public class CurrencyConversion {
    public static final int DEFAULT_CURRENCY = 0;
    public static final String[] currencyCode = {"USD", "GBP", "JPY", "INR", "RUB"};
    public static int conversionTo = DEFAULT_CURRENCY;
    public double[] currencyRate;
    public boolean showHint = true;

    public CurrencyConversion() {
    }

    // currency Conversion formula
    public String convertCurrency(double baseValue) {
        return String.valueOf(baseValue * currencyRate[conversionTo]);
    }

}
