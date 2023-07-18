////////////////////////////////////////////////////////////////////////////////
//
//  Specie - An android currency converter.
//
//  Copyright (C) 2016	Bill Farmer
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.specie;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// SpecieWidgetUpdate
@SuppressWarnings("deprecation")
public class SpecieWidgetUpdate extends Service
    implements Data.TaskCallbacks
{
    public static final String TAG = "SpecieWidgetUpdate";
    public static final String EXTRA_UPDATE_DONE =
        "org.billthefarmer.specie.EXTRA_UPDATE_DONE";

    private Data data;

    // onCreate
    @Override
    public void onCreate()
    {
        // Get data instance
        data = Data.getInstance(this);

        if (BuildConfig.DEBUG)
            Log.d(TAG, "onCreate " + data);
    }

    // onStartCommand
    @Override
    @SuppressWarnings("deprecation")
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onStartCommand " + intent);

        // Start the task
        if (data != null)
            data.startParseTask(Main.DAILY_URL);

        else
        {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (BuildConfig.DEBUG)
            Log.d(TAG, "startParseTask");

        return START_NOT_STICKY;
    }

    // onBind
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    // onDestroy
    @Override
    public void onDestroy()
    {
        // Remove listener
        Data.getInstance(null);
    }

    // On progress update
    @Override
    public void onProgressUpdate(String... dates)
    {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onProgressUpdate " + dates[0]);

        SimpleDateFormat dateParser =
            new SimpleDateFormat(Main.DATE_FORMAT, Locale.getDefault());
        DateFormat dateFormat =
            DateFormat.getDateInstance(DateFormat.MEDIUM);
        String date = null;

        // Format the date for display
        if (dates[0] != null)
        {
            try
            {
                Date update = dateParser.parse(dates[0]);
                date = dateFormat.format(update);
            }

            catch (Exception e)
            {
                return;
            }
        }

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        // Get editor
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(Main.PREF_DATE, date);
        editor.apply();
    }

    // The system calls this to perform work in the UI thread and
    // delivers the result from doInBackground()
    @Override
    @SuppressWarnings("deprecation")
    public void onPostExecute(Map<String, Double> valueMap)
    {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onPostExecute " + valueMap);

        // Check the map
        if (valueMap.isEmpty())
        {
            stopSelf();
            return;
        }

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        valueMap.put("EUR", 1.0);

        // Get saved specie lists
        String namesJSON = preferences.getString(Main.PREF_NAMES, null);
        List<String> nameList = new ArrayList<String>();
        List<String> valueList = new ArrayList<String>();

        int digits = Integer.parseInt(preferences.getString
                                      (Main.PREF_DIGITS, "3"));
        // Set digits
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);
        numberFormat.setGroupingUsed(true);

        // Check saved name list
        if (namesJSON != null)
        {
            try
            {
                // Update name list from JSON array
                JSONArray namesArray = new JSONArray(namesJSON);
                for (int i = 0; !namesArray.isNull(i); i++)
                    nameList.add(namesArray.getString(i));
            }

            catch (Exception e) {}
        }

        // Use the default list
        else
        {
            nameList.addAll(Arrays.asList(Main.SPECIE_LIST));
        }

        // Get current specie
        int currentIndex = preferences.getInt(Main.PREF_INDEX, 0);

        double currentValue = Double.parseDouble(preferences.getString
                                                 (Main.PREF_VALUE, "1.0"));
        String stringValue = numberFormat.format(currentValue);

        // Get the convert value
        double convertValue =
            valueMap.containsKey(Main.SPECIES[currentIndex].name)?
            valueMap.get(Main.SPECIES[currentIndex].name): Double.NaN;

        // Populate a new value list
        for (String name : nameList)
        {
            try
            {
                Double value = (currentValue / convertValue) *
                    valueMap.get(name);

                valueList.add(numberFormat.format(value));
            }

            catch (Exception e)
            {
                valueList.add(numberFormat.format(Double.NaN));
            }
        }

        // Get editor
        SharedPreferences.Editor editor = preferences.edit();

        // Get entries
        JSONObject valueObject = new JSONObject(valueMap);
        JSONArray valueArray = new JSONArray(valueList);

        // Update preferences
        editor.putString(Main.PREF_MAP, valueObject.toString());
        editor.putString(Main.PREF_VALUES, valueArray.toString());
        editor.apply();

        // Get manager
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName provider = new
            ComponentName(this, SpecieWidgetProvider.class);

        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(provider);
        Intent broadcast = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        broadcast.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        broadcast.putExtra(EXTRA_UPDATE_DONE, true);
        sendBroadcast(broadcast);

        if (BuildConfig.DEBUG)
            Log.d(TAG, "Broadcast " + broadcast);

        stopSelf();
    }
}
