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

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

// Parser class
public class Parser
{
    private static final String TAG = "Parser";
    private Map<String, Double> map;
    private String date;

    // Get map
    public Map<String, Double> getMap()
    {
        return map;
    }

    // Get date
    public String getDate()
    {
        return date;
    }

    // Start parser for a url
    public boolean startParser(String source)
    {
        // Create the map
        map = new HashMap<>();

        // Read the json from the url
        try
        {
            URL url = new URL(source);
            String json = read(url).toString();

            JSONObject entries = new JSONObject(json);

            // Use an iterator for the JSON object
            Iterator<String> keys = entries.keys();
            while (keys.hasNext())
            {
                String key = keys.next();
                JSONObject entry = entries.getJSONObject(key);
                String code = entry.getString("code");
                Double rate = entry.getDouble("rate");
                date = entry.getString("date");

                map.put(code, rate);
            }

            return true;
        }

        catch (Exception e)
        {
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            map.clear();
        }

        return false;
    }

    // Start parser from a resource
    public boolean startParser(Context context, int id)
    {
        // Create the map
        map = new HashMap<>();

        // Read the json from the resources
        try
        {
            String json = read(context, id).toString();

            JSONObject entries = new JSONObject(json);

            // Use an iterator for the JSON object
            Iterator<String> keys = entries.keys();
            while (keys.hasNext())
            {
                String key = keys.next();
                JSONObject entry = entries.getJSONObject(key);
                String code = entry.getString("code");
                Double rate = entry.getDouble("rate");
                date = entry.getString("date");

                map.put(code, rate);
            }

            return true;
        }

        catch (Exception e)
        {
            Log.d(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            map.clear();
        }

        return false;
    }

    // read
    public static CharSequence read(URL url)
    {
        StringBuilder text = new StringBuilder();

        try (BufferedReader buffer = new
             BufferedReader(new InputStreamReader(url.openStream())))
        {
            String line;
            while ((line = buffer.readLine()) != null)
                text.append(line).append(System.getProperty("line.separator"));
        }

        catch (Exception e) {}

        return text;
    }

    // read
    public static CharSequence read(Context context, int resId)
    {
        StringBuilder text = new StringBuilder();

        try (BufferedReader buffer = new BufferedReader
             (new InputStreamReader
              (context.getResources().openRawResource(resId))))
        {
            String line;
            while ((line = buffer.readLine()) != null)
                text.append(line).append(System.getProperty("line.separator"));
        }

        catch (Exception e) {}

        return text;
    }
}
