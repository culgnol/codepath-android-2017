package com.codepath.nytarticlesearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.nytarticlesearch.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etBeginDate;
    Spinner sSortOrder;
    CheckBox cbArts;
    CheckBox cbFashionStyle;
    CheckBox cbSports;

    DatePickerDialog dpBeginDate;
    SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateFormatter = new SimpleDateFormat("yyyyMMdd", Locale.US);

        setupViews();
    }

    public void setupViews() {
        etBeginDate = (EditText) findViewById(R.id.etDate);
        sSortOrder = (Spinner) findViewById(R.id.spSort);
        cbArts = (CheckBox) findViewById(R.id.cbArts);
        cbFashionStyle = (CheckBox) findViewById(R.id.cbFashion);
        cbSports = (CheckBox) findViewById(R.id.cbSports);

        etBeginDate.setInputType(InputType.TYPE_NULL);

        etBeginDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        dpBeginDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etBeginDate.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void onSettingsSave(View v) {
        Intent i = new Intent();
        i.putExtra("string_beginDate", etBeginDate.getText().toString());
        i.putExtra("string_sortOrder", sSortOrder.getSelectedItem().toString());
        i.putExtra("bool_cbArts", cbArts.isChecked());
        i.putExtra("bool_cbSports", cbSports.isChecked());
        i.putExtra("bool_cbFashionStyle", cbFashionStyle.isChecked());
        setResult(RESULT_OK, i);
        finish();
    }

    public void onClick(View v) {
        dpBeginDate.show();
    }

}
