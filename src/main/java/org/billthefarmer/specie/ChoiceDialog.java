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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Choice dialog
public class ChoiceDialog extends Activity
    implements View.OnClickListener, AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener, SearchView.OnQueryTextListener
{

    private Button clear;
    private Button select;

    private List<Integer> selectList;

    private ListView listView;
    private ChoiceAdapter adapter;

    private int mode = Main.DISPLAY_MODE;

    // On create
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        boolean theme = preferences.getBoolean(Main.PREF_DARK, true);

        if (!theme)
            setTheme(R.style.DialogLightTheme);

        setContentView(R.layout.choose);

        // Find views
        listView = findViewById(R.id.list);

        Button cancel = findViewById(R.id.cancel);
        clear = findViewById(R.id.clear);
        select = findViewById(R.id.select);

        SearchView search = findViewById(R.id.search);

        // Set the listeners
        if (listView != null)
        {
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);
        }

        if (cancel != null)
            cancel.setOnClickListener(this);

        if (clear != null)
            clear.setOnClickListener(this);

        if (select != null)
            select.setOnClickListener(this);

        if (search != null)
            search.setOnQueryTextListener(this);

        selectList = new ArrayList<>();

        // Create the adapter
        adapter = new ChoiceAdapter(this, R.layout.choice,
                                    Arrays.asList(Main.SPECIES), selectList);

        // Set the adapter
        if (listView != null)
            listView.setAdapter(adapter);
    }

    // On restore
    @Override
    public void onRestoreInstanceState(Bundle savedState)
    {
        List<Integer> list =
            savedState.getIntegerArrayList(Main.SAVE_SELECT);

        if (list != null)
        {
            // Update the selection list
            selectList.addAll(list);
        }

        // Disable buttons if empty
        if (selectList.isEmpty())
        {
            if (clear != null)
                clear.setEnabled(false);
            if (select != null)
                select.setEnabled(false);
            mode = Main.DISPLAY_MODE;
        }

        // Enable buttons if selection
        else
        {
            if (clear != null)
                clear.setEnabled(true);
            if (select != null)
                select.setEnabled(true);
            mode = Main.SELECT_MODE;
        }

        // Notify adapter
        adapter.notifyDataSetChanged();
        super.onRestoreInstanceState(savedState);
    }

    // On save
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        // Save the selection list
        outState.putIntegerArrayList(Main.SAVE_SELECT,
                                     (ArrayList<Integer>) selectList);
    }

    // On click
    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        switch (id)
        {
        // Cancel
        case R.id.cancel:
            setResult(RESULT_CANCELED);
            finish();
            break;

        // Clear
        case R.id.clear:
            if (clear != null)
                clear.setEnabled(false);
            if (select != null)
                select.setEnabled(false);
            mode = Main.DISPLAY_MODE;

            // Start a new selection
            selectList.clear();
            adapter.notifyDataSetChanged();
            break;

        // Select
        case R.id.select:
            // Return new specie list in intent
            Intent intent = new Intent();
            intent.putIntegerArrayListExtra(Main.CHOICE,
                                            (ArrayList<Integer>) selectList);
            setResult(RESULT_OK, intent);
            finish();
            break;
        }
    }

    // On item click
    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id)
    {
        // Check mode
        switch (mode)
        {
        // Normal
        case Main.DISPLAY_MODE:
            selectList.add(position);
            // Return new specie in intent
            Intent intent = new Intent();
            intent.putIntegerArrayListExtra(Main.CHOICE,
                                            (ArrayList<Integer>) selectList);
            setResult(RESULT_OK, intent);
            finish();
            break;

        // Select
        case Main.SELECT_MODE:
            if (selectList.contains(position))
                selectList.remove(selectList.indexOf(position));

            else
                selectList.add(position);

            if (selectList.isEmpty())
            {
                if (clear != null)
                    clear.setEnabled(false);
                if (select != null)
                    select.setEnabled(false);
                mode = Main.DISPLAY_MODE;
            }

            adapter.notifyDataSetChanged();
            break;
        }
    }

    // On item long click
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id)
    {
        if (clear != null)
            clear.setEnabled(true);
        if (select != null)
            select.setEnabled(true);
        mode = Main.SELECT_MODE;

        // Start a new selection
        selectList.clear();
        selectList.add(position);
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        if (newText.length() > 0)
        {
            for (int i = 0; i < Main.SPECIES.length; i++)
            {
                if (Main.SPECIES[i].name.startsWith(newText))
                {
                    listView.smoothScrollToPosition(i);
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return true;
    }
}
