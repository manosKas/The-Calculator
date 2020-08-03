package com.manolee.thecalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.manolee.thecalculator.Constants.ADD;
import static com.manolee.thecalculator.Constants.API_URL;
import static com.manolee.thecalculator.Constants.BACKSPACE;
import static com.manolee.thecalculator.Constants.CALCULATE;
import static com.manolee.thecalculator.Constants.CLEAR;
import static com.manolee.thecalculator.Constants.CURRENCY_CONVERSION;
import static com.manolee.thecalculator.Constants.DIV;
import static com.manolee.thecalculator.Constants.MUL;
import static com.manolee.thecalculator.Constants.RX_NUMBER;
import static com.manolee.thecalculator.Constants.SUB;
import static com.manolee.thecalculator.Constants.ZERO;
import static com.manolee.thecalculator.Helpers.PreviousCharacter;
import static com.manolee.thecalculator.Helpers.formatResult;
import static com.manolee.thecalculator.Helpers.isOperator;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, asyncTaskListener {
    TextView input, result, history;
    ImageView backspace, currencyConversion;
    Button clear, mul, div, sub, add, point, calc, zero, one, two, three, four, five, six, seven, eight, nine;
    String currentToken = "", typedInput;

    private Expression e;
    private CurrencyConversion cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cc = new CurrencyConversion();
        // Button initialization
        input = findViewById(R.id.input);
        input.setInputType(InputType.TYPE_NULL);
        result = findViewById(R.id.result);
        history = findViewById(R.id.history);
        clear = findViewById(R.id.clear);
        backspace = findViewById(R.id.backspace);
        mul = findViewById(R.id.multiplication);
        div = findViewById(R.id.division);
        sub = findViewById(R.id.subtraction);
        add = findViewById(R.id.addition);
        currencyConversion = findViewById(R.id.curr_conv);
        point = findViewById(R.id.point);
        calc = findViewById(R.id.calculate);
        zero = findViewById(R.id.zero);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        six = findViewById(R.id.six);
        seven = findViewById(R.id.seven);
        eight = findViewById(R.id.eight);
        nine = findViewById(R.id.nine);
        // Long click functionality to currency conversion button
        currencyConversion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cc.showHint = false;
                new AlertDialog.Builder(MainActivity.this).setItems(R.array.currency_name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CurrencyConversion.conversionTo = which;
                    }
                }).create().show();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        typedInput = "";
        switch (v.getId()) {
            case R.id.input: // focus input
                break;
            case R.id.result: // focus result
                break;
            case R.id.history: // expand history
                break;
            case R.id.curr_conv:
                typedInput = CURRENCY_CONVERSION;
                break;
            case R.id.calculate:
                typedInput = CALCULATE;
                break;
            case R.id.clear:
                typedInput = CLEAR;
                break;
            case R.id.backspace:
                typedInput = BACKSPACE;
                break;
            case R.id.multiplication:
                typedInput = MUL;
                break;
            case R.id.division:
                typedInput = DIV;
                break;
            case R.id.subtraction:
                typedInput = SUB;
                break;
            case R.id.addition:
                typedInput = ADD;
                break;
            case R.id.point:
                typedInput = ".";
                break;
            case R.id.zero:
                typedInput = ZERO;
                break;
            case R.id.one:
                typedInput = "1";
                break;
            case R.id.two:
                typedInput = "2";
                break;
            case R.id.three:
                typedInput = "3";
                break;
            case R.id.four:
                typedInput = "4";
                break;
            case R.id.five:
                typedInput = "5";
                break;
            case R.id.six:
                typedInput = "6";
                break;
            case R.id.seven:
                typedInput = "7";
                break;
            case R.id.eight:
                typedInput = "8";
                break;
            case R.id.nine:
                typedInput = "9";
                break;
        }
        if (ValidateInput())
            performAction();
    }

    // Checks the validity of the current input
    private boolean ValidateInput() {
        return ifAction() || ifOperator() || ifNumber();
    }

    // Returns true if input is an Action
    private boolean ifAction() {
        return typedInput.equals(CLEAR) || typedInput.equals(BACKSPACE) || typedInput.equals(CURRENCY_CONVERSION) || typedInput.equals(CALCULATE);
    }

    // Returns true if input is an Operator
    private boolean ifOperator() {
        if (isOperator(typedInput)) { // check if input is operator
            if (input.length() == 0) // do not allow an operator as first character in formula input
                return false;
            if (isOperator(PreviousCharacter(String.valueOf(input.getText()))))  // double operator typed
                input.setText(input.getText().subSequence(0, input.length() - 1)); // removes previous operator for the new to be added
            if (currentToken.length() > 0) // reset current token
                currentToken = ""; // ready to parse next operand number
            return true;
        }
        return false;
    }

    // Returns true if input is an Number
    private boolean ifNumber() {
        if (currentToken.equals(ZERO) && typedInput.equals(ZERO)) // allow only one zero to be typed at the start of a number
            return false;
        if (currentToken.length() == 0 && typedInput.equals(".")) // case point is typed first
            typedInput = ZERO + typedInput; // add a zero on the left
        if (!(currentToken + typedInput).matches(RX_NUMBER)) // match current parsed token with typed character if matches to a number format
            return false;
        else
            currentToken = currentToken + typedInput; // append the typed character at the end of the current number token
        return true;
    }

    // Performs action accordingly
    public void performAction() {
        switch (typedInput) {
            case BACKSPACE:
                Backspace();
                break;
            case CLEAR:
                Clear();
                break;
            case CALCULATE:
                Calculation();
                break;
            case CURRENCY_CONVERSION:
                CurrencyConversion();
                break;
            default:
                input.append(typedInput);
        }
    }

    // Action: Calculate
    // makes new expression and calulates it,
    private void Calculation() {
        makeExpression();
        if ((e.operand.size() > 1) && (e.operator.size() == (e.operand.size() - 1))) { // checks the least acceptable expression format
            result.setText(formatResult(e.calculate())); // calculate expression
        }
    }

    // Action: Backspace
    // Perform Backspace operatrion
    private void Backspace() {
        if (input.length() > 0) {
            input.setText(input.getText().subSequence(0, input.length() - 1));
            if (currentToken.length() > 0)
                currentToken = currentToken.substring(0, currentToken.length() - 1);
        }
    }

    // Action: CurrencyConversion
    // Calls the API to get current currency conversion rates
    private void CurrencyConversion() {
        makeExpression();
        if (e.operand.size() == 1) {
            if (cc.showHint)
                Toast.makeText(this, "Long press to choose currency", Toast.LENGTH_LONG).show();
            new CurrencyConversionAPI(this).execute(API_URL);
        } else
            Toast.makeText(this, "Please, enter one value to convert", Toast.LENGTH_SHORT).show();
    }

    // Action: Clear
    // Resets all the necessary fields
    private void Clear() {
        input.setText("");
        result.setText("");
        currentToken = "";
    }

    // Creates new expression to evaluate
    private void makeExpression() {
        e = new Expression(String.valueOf(input.getText()));
        if (isOperator(PreviousCharacter(e.expression)))
            Backspace();
        e.parseFormula(String.valueOf(input.getText()));
    }

    // Callback for the fixer API
    @Override
    public void onTaskComplete(String r) {
        JsonParser(r);
        makeExpression();
        result.setText(cc.convertCurrency(Double.parseDouble(e.operand.get(0))));
    }

    // Unpack response
    private void JsonParser(String r) {
        try {
            JSONObject rates = new JSONObject(r).getJSONObject("rates");
            cc.currencyRate = new double[rates.length()];
            for (int i = 0; i < rates.length(); i++)
                cc.currencyRate[i] = rates.getDouble(CurrencyConversion.currencyCode[i]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}