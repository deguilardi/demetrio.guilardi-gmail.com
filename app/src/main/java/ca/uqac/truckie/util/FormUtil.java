package ca.uqac.truckie.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import ca.uqac.truckie.R;

import java.util.Objects;

public class FormUtil {

    public static void showKeyboard(Activity activity, EditText editText){
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideKeyBoard(Activity activity) {
        try {
            InputMethodManager keyboard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int checkEmptyField(AppCompatEditText field){
        if(field == null){
            return 1;
        }
        Context context = field.getContext();
        if(TextUtils.isEmpty(Objects.requireNonNull(field.getText()).toString())){
            field.setError(context.getString(R.string.err_empty_field));
            return 1;
        }
        return 0;
    }

    public static int checkEmptyField(AppCompatSpinner field, int defValueID){
        if(field == null){
            return 1;
        }
        Context context = field.getContext();
        String defValue = context.getString(defValueID);
        String value = field.getSelectedItem().toString();
        if(TextUtils.isEmpty(value) || value.equals(defValue)){
            TextView errorElement = (TextView)field.getSelectedView();
            errorElement.setError("");
            errorElement.setTextColor(Color.RED);
            errorElement.setText(context.getString(R.string.err_empty_spinner));
            return 1;
        }
        return 0;
    }

    public static int checkEmptyField(AppCompatTextView field, int defValueID){
        if(field == null) {
            return 1;
        }
        else{
            String value = field.getText().toString();
            if(TextUtils.isEmpty(value) || value.equals(field.getContext().getString(defValueID))){
                return 1;
            }
        }
        return 0;
    }

    public static void showIsNotValidErrorMessage(int errorCount, Activity activity){
        String errorMessage = activity.getResources().getQuantityString(R.plurals.err_found_error, errorCount);
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
