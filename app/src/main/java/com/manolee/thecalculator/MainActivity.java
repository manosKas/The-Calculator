package com.manolee.thecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, asyncTaskListener {
    EditText input;
    TextView result, history;
    ImageView allClear, backspace,cc;
    Button mul, div, sub, add, point, calc;
    Button zero, one, two, three, four, five, six, seven, eight, nine;
    String currentToken = "";
    String typedInput;
    static final String RX_NUMBER = "\\d{0,8}[.]?\\d{0,8}";
    private List<String> operand;
    private List<String> operator;
    private int n;
    private static String API_URL = "http://data.fixer.io/api/latest?access_key=ab5207dc17e68e020bf5e8590f2e01b8&symbols=USD,GBP,JPY,INR,RUB";
    private boolean currencyConverterHint = true;
    private int chosenCurrency=0;
    private double[] currencyRate ;
    private String[] currencyCode = {"USD","GBP","JPY","INR","RUB"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button initialization
        input = findViewById(R.id.input);
        input.setInputType(InputType.TYPE_NULL);
        result = findViewById(R.id.result);
        history = findViewById(R.id.history);
        allClear = findViewById(R.id.allclear);
        backspace = findViewById(R.id.backspace);
        mul = findViewById(R.id.multiplication);
        div = findViewById(R.id.division);
        sub = findViewById(R.id.subtraction);
        add = findViewById(R.id.addition);
        cc = findViewById(R.id.curr_conv);
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

        cc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                currencyConverterHint = false;
                new AlertDialog.Builder(MainActivity.this).setItems(R.array.currency_name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosenCurrency = which;
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
            case R.id.curr_conv: // currency conversion
                CurrencyConversion();
                break;
            case R.id.calculate: // get result
                Calculation();
                break;
            case R.id.allclear: // clear all
                typedInput = "ac";
                break;
            case R.id.backspace: // backspace
                typedInput = "bs";
                break;
            case R.id.multiplication:
                typedInput = "*";
                break;
            case R.id.division:
                typedInput = "/";
                break;
            case R.id.subtraction:
                typedInput = "-";
                break;
            case R.id.addition:
                typedInput = "+";
                break;
            case R.id.point:
                typedInput = ".";
                break;
            case R.id.zero:
                typedInput = "0";
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
        else
            Log.d("derp", "! invalid input : " + currentToken);
    }

    private boolean ValidateInput() {
        Log.d("derp", "typed input: " + typedInput);
        return ifNumber() || ifOperator() || ifAction();
    }

    private boolean ifAction() {
        Log.d("derp", "ifAction ");
        return typedInput.equals("ac") || typedInput.equals("c") || typedInput.equals("bs");
    }

    private boolean ifOperator() {
        Log.d("derp", "ifOperator ");
        if (isOperator(typedInput)) { // check if input is operator
            if (input.length() == 0) // if operator is first check
                return false;
            if (isOperator(PreviousCharacter())) { // double operator check
                input.setText(input.getText().replace(input.length() - 1, input.length(), typedInput));
                return false;
            }
            if (currentToken.length() > 0) // reset current token
                currentToken = "";
            return true;
        }
        return false;
    }

    private boolean ifNumber() {
        Log.d("derp", "ifNumber ");
        if (currentToken.length() == 0 && typedInput.equals(".")) // case 0.xxx
            typedInput = "0" + typedInput;
        if (currentToken.equals("0") && typedInput.equals("0"))
            return false;
        if ((currentToken + typedInput).matches(RX_NUMBER)) {
            currentToken = currentToken + typedInput;
            return true;
        }
        return false;
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("/") || token.equals("*");
    }

    private String PreviousCharacter() {
        if (input.length() > 0)
            return String.valueOf(input.getText().charAt(input.length() - 1));
        else
            return "";
    }

    private void Calculation() {
        if (input.length() >= 3) {
            if (isOperator(PreviousCharacter())) // checks if there is a stray operator at the end of the input and removes it
                input.setText(input.getText().replace(input.length(), input.length(), ""));
            result.setText(formatResult(calculate()));
        }
    }

    public static String formatResult(double d) {
        if (Double.isInfinite(d)) return "Can't divide by Zero.";
        if (Double.isNaN(d)) return "Not a number";
        if (d == (long) d) return String.format("%d", (long) d);
        else return String.format("%s", d);
    }

    private int parseFormula() {
        String formula = String.valueOf(input.getText());
        int indexStart = 0, indexEnd;
        operand = new ArrayList<>();
        operator = new ArrayList<>();
        n = 1;
        for (int i = 1; i < formula.length(); i++) {
            if (i == formula.length() - 1) {
                operand.add(formula.substring(indexStart));
            }
            if (isOperator(String.valueOf(formula.charAt(i)))) {
                indexEnd = i;
                operand.add(formula.substring(indexStart, indexEnd)); // numbers as operands
                operator.add(formula.substring(indexEnd, indexEnd + 1)); // math symbols as operators
                n++;
                indexStart = indexEnd + 1;
            }
        }
        return operand.size();
    }

    private double calculate() {
        parseFormula();
        double firstOP = Double.parseDouble(operand.get(0));
        double secondOP;
        double res = 0;
        for (int j = 0; j < n - 1; j++) {
            secondOP = Double.parseDouble(operand.get(j + 1));
            Log.d("derp", "number of total operands: " + operand.size());
            Log.d("derp", (j + 1) + " of " + operator.size() + " operations: " + firstOP + operator.get(j) + secondOP);
            switch (operator.get(j)) {
                case "+":
                    res = Operation.add(firstOP, secondOP);
                    break;
                case "-":
                    res = Operation.sub(firstOP, secondOP);
                    break;
                case "/":
                    res = Operation.div(firstOP, secondOP);
                    break;
                case "*":
                    res = Operation.mul(firstOP, secondOP);
                    break;
            }
            firstOP = res;
        }
        return res;
    }

    private void CurrencyConversion() {
        if(currencyConverterHint){
            Toast.makeText(this,"Long press to choose currency",Toast.LENGTH_LONG).show();
        }
        if (hasInternetAccess() && isOneValue())
            new CurrencyConversionAPI(this).execute(API_URL);
    }

    private boolean isOneValue() {
        parseFormula();
        return true;
    }

    private boolean hasInternetAccess() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    public void performAction() {
        switch (typedInput) {
            case "bs":
                if (input.length() > 0) Backspace();
                break;
            case "ac":
                Reset();
                break;
            default:
                input.append(typedInput);
        }
        Log.d("derp", "current token: " + currentToken);
    }

    private void Backspace() {
        input.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_DEL, 0));
        input.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_DEL, 0));
        if (currentToken.length() > 0)
            currentToken = currentToken.substring(0, currentToken.length() - 1);
    }

    private void Reset() {
        input.setText("");
        result.setText("");
        currentToken = "";
    }

    @Override
    public void onTaskComplete(String r) {
        Log.d("derp","json: "+r);
        Currencies c;
        try {
            JSONObject rates = new JSONObject(r).getJSONObject("rates");
            currencyRate = new double[rates.length()];
            for(int i=0; i<rates.length(); i++)
                currencyRate[i] = rates.getDouble(currencyCode[i]);
        }catch(JSONException e){
            e.printStackTrace();
        }
        double baseValue = Double.parseDouble(String.valueOf(input.getText()));
        double res = baseValue*currencyRate[chosenCurrency];
        result.setText(String.valueOf(res));
    }
}