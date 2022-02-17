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
import java.util.Arrays;
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
            (preferences.getString(Main.PREF_DIGITS, "3"));

        // Set digits
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

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
        List<String> valueList = new ArrayList<String>();

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

        // Get the saved value list
        if (valuesJSON != null)
        {
            try
            {
                // Update value list from JSON array
                JSONArray valuesArray = new JSONArray(valuesJSON);
                for (int i = 0; !valuesArray.isNull(i); i++)
                    valueList.add(valuesArray.getString(i));
            }

            catch (Exception e) {}
        }

        // Calculate value list
        else
        {
            // Format each value
            numberFormat.setGroupingUsed(true);
            for (String name : nameList)
            {
                Double v = valueMap.get(name);
                String value = numberFormat.format((v != null)? v: 0.0);

                valueList.add(value);
            }
        }

        // Create specie name list
        List<String> specieNameList = Arrays.asList(Main.SPECIE_NAMES);

        int entry = Integer.parseInt
            (preferences.getString(Main.PREF_ENTRY, "0"));

        String entryName = nameList.get(entry);
        String entryValue = valueList.get(entry);
        int entryIndex = specieNameList.indexOf(entryName);
        String longName = context.getString(Main.SPECIE_LONGNAMES[entryIndex]);

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

        views.setImageViewResource(R.id.flag, Main.SPECIE_FLAGS[entryIndex]);
        views.setTextViewText(R.id.name, entryName);
        views.setTextViewText(R.id.symbol, Main.SPECIE_SYMBOLS[entryIndex]);
        views.setTextViewText(R.id.value, entryValue);
        views.setTextViewText(R.id.long_name, longName);

        // Tell the AppWidgetManager to perform an update on the
        // current app widgets.
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }
}
