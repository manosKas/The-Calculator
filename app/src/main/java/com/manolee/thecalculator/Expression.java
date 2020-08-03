package com.manolee.thecalculator;

import java.util.ArrayList;
import java.util.List;

import static com.manolee.thecalculator.Constants.ADD;
import static com.manolee.thecalculator.Constants.DIV;
import static com.manolee.thecalculator.Constants.MUL;
import static com.manolee.thecalculator.Constants.SUB;
import static com.manolee.thecalculator.Helpers.isOperator;

public class Expression {
    public boolean hasOneValue;
    public List<String> operand, operator;
    public String expression;

    public Expression(String e) {
        operand = new ArrayList<>();
        operator = new ArrayList<>();
        expression = e;
        hasOneValue = false;
    }

    // String break down to operands and operators
    public void parseFormula(String formula) {
        int indexStart = 0, indexEnd;
        for (int i = 0; i < formula.length(); i++) {
            if (i == formula.length() - 1) // if end of string is reached instead of operator
                operand.add(formula.substring(indexStart)); // add number as operand
            if (isOperator(String.valueOf(formula.charAt(i)))) {
                indexEnd = i;
                operand.add(formula.substring(indexStart, indexEnd)); //add number as operands
                operator.add(formula.substring(indexEnd, indexEnd + 1)); //add math symbol as operators
                indexStart = indexEnd + 1;
            }
        }
        if (operand.size() == 1) hasOneValue = true;
    }

    // individual operation evaluation
    public double calculate() {
        double firstOP = Double.parseDouble(operand.get(0));
        double secondOP;
        double res = 0;
        for (int j = 0; j < operator.size(); j++) {
            secondOP = Double.parseDouble(operand.get(j + 1));
            switch (operator.get(j)) {
                case ADD:
                    res = Operation.add(firstOP, secondOP);
                    break;
                case SUB:
                    res = Operation.sub(firstOP, secondOP);
                    break;
                case DIV:
                    res = Operation.div(firstOP, secondOP);
                    break;
                case MUL:
                    res = Operation.mul(firstOP, secondOP);
                    break;
            }
            firstOP = res;
        }
        return res;
    }
}