////////////////////////////////////////////////////////////////////////////////
//
//  Specie - An android specie converter.
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

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class SpecieWidgetProvider extends AppWidgetProvider
{
    // onUpdate
    @Override
    @SuppressLint("InlinedApi")
    @SuppressWarnings("deprecation")
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
                         int[] appWidgetIds)
    {
        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);

        // Get digits
        int digits = Integer.parseInt
            (preferences.getString(Main.PREF_WIDGET, "1"));

        // Get current specie
        int currentIndex = preferences.getInt(Main.PREF_INDEX, 0);

        // Get current value
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        String value = preferences.getString(Main.PREF_VALUE, "1.0");
        double currentValue = 1.0;

        // Parse string value
        try
        {
            currentValue = Double.parseDouble(value);
        }

        catch (Exception ex)
        {
            currentValue = 1.0;
        }

        // Get saved specie rates
        String mapJSON = preferences.getString(Main.PREF_MAP, null);

        Map<String, Double> valueMap = new HashMap<String, Double>();

        // Check saved rates
        if (mapJSON != null)
        {
            // Create the value map from a JSON object
            try
            {
                // Create the JSON object
                JSONObject mapObject = new JSONObject(mapJSON);

                // Use an iterator for the JSON object
                Iterator<String> keys = mapObject.keys();
                while (keys.hasNext())
                {
                    String key = keys.next();
                    valueMap.put(key, mapObject.getDouble(key));
                }
            }

            catch (Exception e) {}
        }

        // Get saved specie lists
        String namesJSON = preferences.getString(Main.PREF_NAMES, null);
        String valuesJSON = preferences.getString(Main.PREF_VALUES, null);

        List<String> nameList = new ArrayList<String>();

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

        // Create an Intent to launch Specie
        Intent intent = new Intent(context, Main.class);
        PendingIntent pendingIntent =
            PendingIntent.getActivity(context, 0, intent,
                                      PendingIntent.FLAG_UPDATE_CURRENT |
                                      PendingIntent.FLAG_IMMUTABLE);

        // Get the layout for the widget and attach an on-click
        // listener to the view.
        RemoteViews views = new
            RemoteViews(context.getPackageName(), R.layout.widget);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        // Tell the AppWidgetManager to perform an update on the
        // current app widgets.
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }
}
