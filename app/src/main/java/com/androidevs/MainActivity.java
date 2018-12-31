package com.androidevs;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.androiddevs.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Spinner calendarIdSpinner;
    private Hashtable<String, String> calendarIdTable;
    private Button newEventButton;
    private long timeInMilliseconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarIdSpinner = (Spinner) findViewById(R.id.calendarid_spinner);
        newEventButton = (Button) findViewById(R.id.btnAddEvent);

        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CalendarHelper.haveCalendarReadWritePermissions(MainActivity.this)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        addNewEvent();
                    }
                } else {
                    CalendarHelper.requestCalendarReadWritePermission(MainActivity.this);
                }
            }
        });


        calendarIdSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        if (CalendarHelper.haveCalendarReadWritePermissions(this)) {
            //Load calendars
            calendarIdTable = CalendarHelper.listCalendarId(this);

            updateCalendarIdSpinner();

        }


    }

    private void updateCalendarIdSpinner() {
        if (calendarIdTable == null) {
            return;
        }
        List<String> list = new ArrayList<String>();
        Enumeration e = calendarIdTable.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            list.add(key);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calendarIdSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == CalendarHelper.CALENDARHELPER_PERMISSION_REQUEST_CODE) {
            if (CalendarHelper.haveCalendarReadWritePermissions(this)) {
                Toast.makeText(this, (String) "Have Calendar Read/Write Permission.",
                        Toast.LENGTH_LONG).show();

            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addNewEvent() {
        if (calendarIdTable == null) {
            Toast.makeText(this, (String) "No calendars found. Please ensure at least one google account has been added.",
                    Toast.LENGTH_LONG).show();
            //Load calendars
            calendarIdTable = CalendarHelper.listCalendarId(this);

            updateCalendarIdSpinner();

            return;
        }

        final long oneHour = 1000 * 60 * 60;
        final long tenMinutes = 1000 * 60 * 10;

        long oneHourFromNow = (new Date()).getTime() + oneHour;
        long tenMinutesFromNow = (new Date()).getTime() + tenMinutes;

        String startStrDate = "2019/01/01 00:00";
        String endStrDate = "2019/01/04 00:00";
        long startDate = convertMiliSecond(startStrDate);
        long endDate = convertMiliSecond(endStrDate);

        String calendarString = calendarIdSpinner.getSelectedItem().toString();

        int calendar_id = Integer.parseInt(calendarIdTable.get(calendarString));

        CalendarHelper.MakeNewCalendarEntry(this, "Android", "Android Team", "Android Developers", startDate, endDate, false, true, calendar_id, 3);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private long convertMiliSecond(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        try {
            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();
            Log.e(TAG, "convertMiliSecond: >>" + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }
}
