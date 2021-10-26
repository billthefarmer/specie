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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

// Main class
public class Main extends Activity
    implements EditText.OnEditorActionListener,
    AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener,
    View.OnClickListener, TextWatcher,
    Data.TaskCallbacks
{
    // Initial specie name list
    public static final String SPECIE_LIST[] =
    {
        "USD", "GBP", "CAD", "AUD"
    };

    // Specie names
    public static final String SPECIE_NAMES[] =
    {
        "EUR", "USD", "GBP", "JPY", "AUD", "CHF", "CAD", "SVC", "CVE", "ZMW",
        "YER", "LKR", "PLN", "PEN", "IQD", "STN", "XPF", "ALL", "MUR", "DOP",
        "NZD", "HRK", "DZD", "ARS", "PYG", "BND", "KMF", "SZL", "SOS", "ANG",
        "PKR", "KRW", "AZN", "CRC", "JMD", "SSP", "ERN", "WST", "EGP", "SGD",
        "ZAR", "KGS", "MGA", "SRD", "GHS", "MOP", "BAM", "INR", "TRY", "TWD",
        "TMT", "VES", "SBD", "MWK", "GTQ", "LBP", "HUF", "NGN", "IRR", "MKD",
        "BIF", "MMK", "MVR", "SEK", "NPR", "ISK", "GIP", "GEL", "COP", "BZD",
        "GNF", "SLL", "KES", "AED", "PHP", "ILS", "MRO", "BOB", "GYD", "RWF",
        "MZN", "UGX", "CNY", "SAR", "MYR", "KZT", "AFN", "NAD", "SYP", "TOP",
        "VUV", "IDR", "TND", "XOF", "TJS", "ETB", "XCD", "LAK", "BWP", "KWD",
        "CZK", "PGK", "UAH", "GMD", "AWG", "AOA", "KHR", "NOK", "MAD", "RON",
        "BYN", "RSD", "BSD", "DJF", "HNL", "SCR", "BHD", "OMR", "RUB", "LYD",
        "CLP", "UYU", "FJD", "CDF", "LSL", "TZS", "BBD", "BDT", "QAR", "MXN",
        "AMD", "NIO", "LRD", "SDG", "MRU", "MNT", "BRL", "HKD", "THB", "XAF",
        "MDL", "PAB", "TTD", "HTG", "CUP", "DKK", "JOD", "BGN", "VND", "UZS"
    };

    // Specie symbols
    public static final String SPECIE_SYMBOLS[] =
    {
        "€", "$", "£", "¥", "$", "", "$", "$", "", "", "﷼", "₨", "zł", "S/.",
        "", "", "", "Lek", "₨", "RD$", "$", "kn", "", "$", "Gs", "$", "", "",
        "S", "ƒ", "₨", "₩", "₼", "₡", "J$", "", "", "", "£", "$", "R", "лв",
        "", "$", "", "", "KM", "₹", "", "NT$", "", "", "$", "", "Q", "£",
        "Ft", "₦", "﷼", "ден", "", "", "", "kr", "₨", "kr", "£", "₾", "$",
        "BZ$", "", "", "", "", "₱", "₪", "", "$b", "$", "", "MT", "", "¥",
        "﷼", "RM", "лв", "؋", "$", "£", "", "", "Rp", "", "", "", "", "", "₭",
        "P", "", "Kč", "", "₴", "", "ƒ", "", "៛", "kr", "", "lei", "", "Дин.",
        "$", "", "L", "₨", "", "﷼", "₽", "", "$", "$U", "$", "", "", "", "$",
        "", "﷼", "$", "", "C$", "$", "", "", "₮", "R$", "$", "฿", "", "",
        "B/.", "TT$", "", "₱", "kr", "", "лв", "₫", "лв",
    };

    // Specie long names
    public static final Integer SPECIE_LONGNAMES[] =
    {
        R.string.long_eur, R.string.long_usd, R.string.long_gbp,
        R.string.long_jpy, R.string.long_aud, R.string.long_chf,
        R.string.long_cad, R.string.long_svc, R.string.long_cve,
        R.string.long_zmw, R.string.long_yer, R.string.long_lkr,
        R.string.long_pln, R.string.long_pen, R.string.long_iqd,
        R.string.long_stn, R.string.long_xpf, R.string.long_all,
        R.string.long_mur, R.string.long_dop, R.string.long_nzd,
        R.string.long_hrk, R.string.long_dzd, R.string.long_ars,
        R.string.long_pyg, R.string.long_bnd, R.string.long_kmf,
        R.string.long_szl, R.string.long_sos, R.string.long_ang,
        R.string.long_pkr, R.string.long_krw, R.string.long_azn,
        R.string.long_crc, R.string.long_jmd, R.string.long_ssp,
        R.string.long_ern, R.string.long_wst, R.string.long_egp,
        R.string.long_sgd, R.string.long_zar, R.string.long_kgs,
        R.string.long_mga, R.string.long_srd, R.string.long_ghs,
        R.string.long_mop, R.string.long_bam, R.string.long_inr,
        R.string.long_try, R.string.long_twd, R.string.long_tmt,
        R.string.long_ves, R.string.long_sbd, R.string.long_mwk,
        R.string.long_gtq, R.string.long_lbp, R.string.long_huf,
        R.string.long_ngn, R.string.long_irr, R.string.long_mkd,
        R.string.long_bif, R.string.long_mmk, R.string.long_mvr,
        R.string.long_sek, R.string.long_npr, R.string.long_isk,
        R.string.long_gip, R.string.long_gel, R.string.long_cop,
        R.string.long_bzd, R.string.long_gnf, R.string.long_sll,
        R.string.long_kes, R.string.long_aed, R.string.long_php,
        R.string.long_ils, R.string.long_mro, R.string.long_bob,
        R.string.long_gyd, R.string.long_rwf, R.string.long_mzn,
        R.string.long_ugx, R.string.long_cny, R.string.long_sar,
        R.string.long_myr, R.string.long_kzt, R.string.long_afn,
        R.string.long_nad, R.string.long_syp, R.string.long_top,
        R.string.long_vuv, R.string.long_idr, R.string.long_tnd,
        R.string.long_xof, R.string.long_tjs, R.string.long_etb,
        R.string.long_xcd, R.string.long_lak, R.string.long_bwp,
        R.string.long_kwd, R.string.long_czk, R.string.long_pgk,
        R.string.long_uah, R.string.long_gmd, R.string.long_awg,
        R.string.long_aoa, R.string.long_khr, R.string.long_nok,
        R.string.long_mad, R.string.long_ron, R.string.long_byn,
        R.string.long_rsd, R.string.long_bsd, R.string.long_djf,
        R.string.long_hnl, R.string.long_scr, R.string.long_bhd,
        R.string.long_omr, R.string.long_rub, R.string.long_lyd,
        R.string.long_clp, R.string.long_uyu, R.string.long_fjd,
        R.string.long_cdf, R.string.long_lsl, R.string.long_tzs,
        R.string.long_bbd, R.string.long_bdt, R.string.long_qar,
        R.string.long_mxn, R.string.long_amd, R.string.long_nio,
        R.string.long_lrd, R.string.long_sdg, R.string.long_mru,
        R.string.long_mnt, R.string.long_brl, R.string.long_hkd,
        R.string.long_thb, R.string.long_xaf, R.string.long_mdl,
        R.string.long_pab, R.string.long_ttd, R.string.long_htg,
        R.string.long_cup, R.string.long_dkk, R.string.long_jod,
        R.string.long_bgn, R.string.long_vnd, R.string.long_uzs
    };

    // Specie flags
    public static final Integer SPECIE_FLAGS[] =
    {
        R.drawable.flag_eu, R.drawable.flag_us, R.drawable.flag_gb,
        R.drawable.flag_jp, R.drawable.flag_au, R.drawable.flag_ch,
        R.drawable.flag_ca, R.drawable.flag_sv, R.drawable.flag_cv,
        R.drawable.flag_zm, R.drawable.flag_ye, R.drawable.flag_lk,
        R.drawable.flag_pl, R.drawable.flag_pe, R.drawable.flag_iq,
        R.drawable.flag_st, R.drawable.flag_pf, R.drawable.flag_al,
        R.drawable.flag_mu, R.drawable.flag_do, R.drawable.flag_nz,
        R.drawable.flag_hr, R.drawable.flag_dz, R.drawable.flag_ar,
        R.drawable.flag_py, R.drawable.flag_bn, R.drawable.flag_km,
        R.drawable.flag_sz, R.drawable.flag_so, R.drawable.flag_cw,
        R.drawable.flag_pk, R.drawable.flag_kr, R.drawable.flag_az,
        R.drawable.flag_cr, R.drawable.flag_jm, R.drawable.flag_ss,
        R.drawable.flag_er, R.drawable.flag_ws, R.drawable.flag_eg,
        R.drawable.flag_sg, R.drawable.flag_za, R.drawable.flag_kg,
        R.drawable.flag_mg, R.drawable.flag_sr, R.drawable.flag_gh,
        R.drawable.flag_mo, R.drawable.flag_ba, R.drawable.flag_in,
        R.drawable.flag_tr, R.drawable.flag_tw, R.drawable.flag_tm,
        R.drawable.flag_ve, R.drawable.flag_sb, R.drawable.flag_mw,
        R.drawable.flag_gt, R.drawable.flag_lb, R.drawable.flag_hu,
        R.drawable.flag_ng, R.drawable.flag_ir, R.drawable.flag_mk,
        R.drawable.flag_bi, R.drawable.flag_mm, R.drawable.flag_mv,
        R.drawable.flag_se, R.drawable.flag_np, R.drawable.flag_is,
        R.drawable.flag_gi, R.drawable.flag_ge, R.drawable.flag_co,
        R.drawable.flag_bz, R.drawable.flag_gn, R.drawable.flag_sl,
        R.drawable.flag_ke, R.drawable.flag_ae, R.drawable.flag_ph,
        R.drawable.flag_il, R.drawable.flag_mr, R.drawable.flag_bo,
        R.drawable.flag_gy, R.drawable.flag_rw, R.drawable.flag_mz,
        R.drawable.flag_ug, R.drawable.flag_cn, R.drawable.flag_sa,
        R.drawable.flag_my, R.drawable.flag_kz, R.drawable.flag_af,
        R.drawable.flag_na, R.drawable.flag_sy, R.drawable.flag_to,
        R.drawable.flag_vu, R.drawable.flag_id, R.drawable.flag_tn,
        R.drawable.flag_be, R.drawable.flag_tj, R.drawable.flag_et,
        R.drawable.flag_ag, R.drawable.flag_la, R.drawable.flag_bw,
        R.drawable.flag_kw, R.drawable.flag_cz, R.drawable.flag_pg,
        R.drawable.flag_ua, R.drawable.flag_gm, R.drawable.flag_aw,
        R.drawable.flag_ao, R.drawable.flag_kh, R.drawable.flag_no,
        R.drawable.flag_ma, R.drawable.flag_ro, R.drawable.flag_by,
        R.drawable.flag_rs, R.drawable.flag_bs, R.drawable.flag_dj,
        R.drawable.flag_hn, R.drawable.flag_sc, R.drawable.flag_bh,
        R.drawable.flag_om, R.drawable.flag_ru, R.drawable.flag_ly,
        R.drawable.flag_cl, R.drawable.flag_uy, R.drawable.flag_fj,
        R.drawable.flag_cd, R.drawable.flag_ls, R.drawable.flag_tz,
        R.drawable.flag_bb, R.drawable.flag_bd, R.drawable.flag_qa,
        R.drawable.flag_mx, R.drawable.flag_am, R.drawable.flag_ni,
        R.drawable.flag_lr, R.drawable.flag_sd, R.drawable.flag_mr,
        R.drawable.flag_mn, R.drawable.flag_br, R.drawable.flag_hk,
        R.drawable.flag_th, R.drawable.flag_cm, R.drawable.flag_md,
        R.drawable.flag_pa, R.drawable.flag_tt, R.drawable.flag_ht,
        R.drawable.flag_cu, R.drawable.flag_dk, R.drawable.flag_jo,
        R.drawable.flag_bg, R.drawable.flag_vn, R.drawable.flag_uz
    };

    public static final String TAG = "Specie";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String PREF_MAP = "pref_map";
    public static final String PREF_DATE = "pref_date";
    public static final String PREF_NAMES = "pref_names";
    public static final String PREF_INDEX = "pref_index";
    public static final String PREF_VALUE = "pref_value";
    public static final String PREF_VALUES = "pref_values";

    public static final String PREF_WIFI = "pref_wifi";
    public static final String PREF_ROAMING = "pref_roaming";
    public static final String PREF_DIGITS = "pref_digits";
    public static final String PREF_FILL = "pref_fill";
    public static final String PREF_DARK = "pref_dark";
    public static final String PREF_ABOUT = "pref_about";

    public static final String SAVE_SELECT = "save_select";

    public static final String DAILY_URL =
        "https://www.floatrates.com/daily/eur.json";

    protected final static String CHOICE = "choice";

    public static final int DISPLAY_MODE = 0;
    public static final int SELECT_MODE = 1;

    private int mode = DISPLAY_MODE;

    private boolean wifi = true;
    private boolean roaming = false;
    private boolean select = true;
    private boolean dark = true;
    private int digits = 3;

    private int currentIndex = 0;
    private double currentValue = 1.0;
    private double convertValue = 1.0;
    private String date;

    private ImageView flagView;
    private TextView nameView;
    private TextView symbolView;
    private EditText editView;
    private TextView longNameView;
    private TextView dateView;
    private TextView statusView;

    private Data data;

    private List<String> specieNameList;

    private List<Integer> flagList;
    private List<String> nameList;
    private List<String> symbolList;
    private List<String> valueList;
    private List<Integer> longNameList;

    private List<Integer> selectList;
    private Map<String, Double> valueMap;

    private SpecieAdapter adapter;

    private Resources resources;

    // On create
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        dark = preferences.getBoolean(PREF_DARK, true);

        if (!dark)
            setTheme(R.style.AppLightTheme);

        setContentView(R.layout.main);

        // Get data instance
        data = Data.getInstance(this);

        // Find views
        flagView = findViewById(R.id.flag);
        nameView = findViewById(R.id.name);
        symbolView = findViewById(R.id.symbol);
        editView = findViewById(R.id.edit);
        longNameView = findViewById(R.id.long_name);
        dateView = findViewById(R.id.date);
        statusView = findViewById(R.id.status);
        ListView listView = findViewById(R.id.list);

        // Set the click listeners, just for the text selection logic
        if (flagView != null)
            flagView.setOnClickListener(this);

        if (nameView != null)
            nameView.setOnClickListener(this);

        if (symbolView != null)
            symbolView.setOnClickListener(this);

        if (longNameView != null)
            longNameView.setOnClickListener(this);

        // Set the listeners for the value field
        if (editView != null)
        {
            editView.addTextChangedListener(this);
            editView.setOnEditorActionListener(this);
            editView.setOnClickListener(this);
        }

        // Set the listeners for the list view
        if (listView != null)
        {
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);
        }

        // Create specie name list
        specieNameList = Arrays.asList(SPECIE_NAMES);

        // Create lists
        flagList = new ArrayList<>();
        nameList = new ArrayList<>();
        symbolList = new ArrayList<>();
        valueList = new ArrayList<>();
        longNameList = new ArrayList<>();

        // Check data instance
        if (data != null)
            selectList = data.getList();

        // Check select list
        if (selectList == null)
            selectList = new ArrayList<>();

        // Set mode
        if (selectList.isEmpty())
            mode = Main.DISPLAY_MODE;

        else
            mode = Main.SELECT_MODE;

        // Create the adapter
        adapter = new SpecieAdapter(this, R.layout.item, flagList, nameList,
                                      symbolList, valueList, longNameList,
                                      selectList);
        // Set the list view adapter
        if (listView != null)
            listView.setAdapter(adapter);
    }

    // On resume
    @Override
    @SuppressWarnings("deprecation")
    protected void onResume()
    {
        super.onResume();

        // Get resources
        resources = getResources();

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        boolean theme = dark;

        wifi = preferences.getBoolean(PREF_WIFI, true);
        dark = preferences.getBoolean(PREF_DARK, true);
        roaming = preferences.getBoolean(PREF_ROAMING, false);
        digits = Integer.parseInt(preferences.getString(PREF_DIGITS, "3"));

        if (theme != dark && Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
            recreate();

        // Get current specie
        currentIndex = preferences.getInt(PREF_INDEX, 0);

        // Get current value
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        String value = preferences.getString(PREF_VALUE, "1.0");

        // Parse string value
        try
        {
            currentValue = Double.parseDouble(value);
        }
        catch (Exception ex)
        {
            currentValue = 1.0;
        }

        // Get the date and format it for display
        date = preferences.getString(PREF_DATE, "");
        String format = resources.getString(R.string.updated);
        String updated = String.format(Locale.getDefault(), format, date);

        // Check the date view
        if (dateView != null)
            dateView.setText(updated);

        // Set current specie flag and names
        if (flagView != null)
            flagView.setImageResource(SPECIE_FLAGS[currentIndex]);
        if (nameView != null)
            nameView.setText(SPECIE_NAMES[currentIndex]);
        if (symbolView != null)
            symbolView.setText(SPECIE_SYMBOLS[currentIndex]);
        if (longNameView != null)
            longNameView.setText(SPECIE_LONGNAMES[currentIndex]);

        // Set current value
        numberFormat.setGroupingUsed(false);
        value = numberFormat.format(currentValue);
        if (editView != null)
            editView.setText(value);

        // Connect callbacks
        data = Data.getInstance(this);

        // Check data instance
        if (data != null)
            // Get the saved value map
            valueMap = data.getMap();

        // Check retained data
        if (valueMap == null)
        {
            // Get saved specie rates
            String mapJSON = preferences.getString(PREF_MAP, null);

            // Check saved rates
            if (mapJSON != null)
            {
                // Create the value map from a JSON object
                try
                {
                    // Create the JSON object
                    JSONObject mapObject = new JSONObject(mapJSON);
                    valueMap = new HashMap<>();

                    // Use an iterator for the JSON object
                    Iterator<String> keys = mapObject.keys();
                    while (keys.hasNext())
                    {
                        String key = keys.next();
                        valueMap.put(key, mapObject.getDouble(key));
                    }
                }
                catch (Exception e)
                {
                }
            }

            // Get old rates from resources
            else
            {
                // Get a parser
                Parser parser = new Parser();

                // Start the parser
                parser.startParser(this, R.raw.euro);

                DateFormat dateParser =
                    DateFormat.getDateInstance(DateFormat.FULL);
                    // new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                DateFormat dateFormat =
                    DateFormat.getDateInstance(DateFormat.MEDIUM);

                // Get the date from the parser
                String latest = parser.getDate();

                // Format the date for display
                if (latest != null)
                {
                    try
                    {
                        Date update = dateParser.parse(latest);
                        date = dateFormat.format(update);
                    }
                    catch (Exception e)
                    {
                    }

                    // Show the formatted date
                    format = resources.getString(R.string.updated);
                    updated = String.format(Locale.getDefault(), format, date);
                    if (dateView != null)
                        dateView.setText(updated);
                }
                else if (statusView != null)
                    statusView.setText(R.string.failed);

                valueMap = parser.getMap();
                valueMap.put("EUR", 1.0);
            }
        }

        // Get saved specie lists
        String namesJSON = preferences.getString(PREF_NAMES, null);
        String valuesJSON = preferences.getString(PREF_VALUES, null);

        // Check saved name list
        if (namesJSON != null)
        {
            try
            {
                // Update name list from JSON array
                JSONArray namesArray = new JSONArray(namesJSON);
                nameList.clear();
                for (int i = 0; !namesArray.isNull(i); i++)
                    nameList.add(namesArray.getString(i));
            }
            catch (Exception e)
            {
            }
        }

        // Use the default list
        else
        {
            nameList.addAll(Arrays.asList(SPECIE_LIST));
        }

        // Get the saved value list
        if (valuesJSON != null)
        {
            try
            {
                // Update value list from JSON array
                JSONArray valuesArray = new JSONArray(valuesJSON);
                valueList.clear();
                for (int i = 0; !valuesArray.isNull(i); i++)
                    valueList.add(valuesArray.getString(i));
            }
            catch (Exception e)
            {
            }
        }

        // Calculate value list
        else
        {
            valueList.clear();

            // Format each value
            for (String name : nameList)
            {
                Double v = valueMap.get(name);
                value = numberFormat.format((v != null)? v: 0.0);

                valueList.add(value);
            }
        }

        // Get the current conversion rate
        convertValue = valueMap.get(SPECIE_NAMES[currentIndex]);

        // Recalculate all the values
        valueList.clear();
        for (String name : nameList)
        {
            Double v = (currentValue / convertValue) *
                ((valueMap.get(name) != null)? valueMap.get(name): 0.0);

            String s = numberFormat.format(v);
            valueList.add(s);
        }

        // Clear lists
        if (flagList != null)
            flagList.clear();
        if (symbolList != null)
            symbolList.clear();
        if (longNameList != null)
            longNameList.clear();

        // Populate the lists
        for (String name : nameList)
        {
            int index = specieNameList.indexOf(name);

            if (flagList != null)
                flagList.add(SPECIE_FLAGS[index]);
            if (symbolList != null)
                symbolList.add(SPECIE_SYMBOLS[index]);
            if (longNameList != null)
                longNameList.add(SPECIE_LONGNAMES[index]);
        }

        // Update the adapter
        adapter.notifyDataSetChanged();

        // Check data instance
        if (data != null)
        {
            // Check retained data
            if (data.getMap() != null)
            {
                valueMap.put("EUR", 1.0);

                // Don't update
                return;
            }
        }

        // Check connectivity before update
        ConnectivityManager manager =
            (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check connected
        if (info == null || !info.isConnected())
        {
            if (statusView != null)
                statusView.setText(R.string.no_connection);
            return;
        }

        // Check wifi
        if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
        {
            if (statusView != null)
                statusView.setText(R.string.no_wifi);
            return;
        }

        // Check roaming
        if (!roaming && info.isRoaming())
        {
            if (statusView != null)
                statusView.setText(R.string.roaming);
            return;
        }

        // Schedule update
        if (statusView != null)
            statusView.setText(R.string.updating);

        // Start the task
        if (data != null)
            data.startParseTask(DAILY_URL);
    }

    // On pause
    @Override
    protected void onPause()
    {
        super.onPause();

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        // Get editor
        SharedPreferences.Editor editor = preferences.edit();

        // Get entries
        JSONObject valueObject = new JSONObject(valueMap);
        JSONArray nameArray = new JSONArray(nameList);
        JSONArray valueArray = new JSONArray(valueList);

        // Update preferences
        editor.putString(PREF_MAP, valueObject.toString());
        editor.putString(PREF_NAMES, nameArray.toString());
        editor.putString(PREF_VALUES, valueArray.toString());

        editor.putInt(PREF_INDEX, currentIndex);

        String value = Double.toString(currentValue);
        editor.putString(PREF_VALUE, value);
        editor.putString(PREF_DATE, date);
        editor.apply();

        // Save the select list and value map in the data instance
        if (data != null)
        {
            data.setList(selectList);
            data.setMap(valueMap);
        }

        // Disconnect callbacks
        data = Data.getInstance(null);
    }

    // On create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it
        // is present.
        MenuInflater inflater = getMenuInflater();

        // Check mode
        switch (mode)
        {
        case DISPLAY_MODE:
            inflater.inflate(R.menu.main, menu);
            break;

        case SELECT_MODE:
            inflater.inflate(R.menu.select, menu);
            break;
        }

        return true;
    }

    // On options item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Get id
        int id = item.getItemId();
        switch (id)
        {
        // Add
        case R.id.action_add:
            return onAddClick();

        // Refresh
        case R.id.action_refresh:
            return onRefreshClick();

        // Help
        case R.id.action_help:
            return onHelpClick();

        // Settings
        case R.id.action_settings:
            return onSettingsClick();

        // Clear
        case R.id.action_clear:
            return onClearClick();

        // Remove
        case R.id.action_remove:
            return onRemoveClick();

        // Copy
        case R.id.action_copy:
            return onCopyClick();
        }

        return false;
    }

    // On add click
    private boolean onAddClick()
    {
        // Start the choice dialog
        Intent intent = new Intent(this, ChoiceDialog.class);
        startActivityForResult(intent, 0);

        return true;
    }

    // On clear click
    private boolean onClearClick()
    {
        // Clear the list and update the adapter
        selectList.clear();
        adapter.notifyDataSetChanged();

        // Restore the menu
        mode = DISPLAY_MODE;
        invalidateOptionsMenu();
        return true;
    }

    // On copy click
    private boolean onCopyClick()
    {
        ClipboardManager clipboard =
            (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        // Copy value to clip
        String clip = null;
        for (int i : selectList)
        {
            try
            {
                numberFormat.setGroupingUsed(true);
                Number number = numberFormat.parse(valueList.get(i));
                Double value = number.doubleValue();

                // Remove grouping from value
                numberFormat.setGroupingUsed(false);
                clip = numberFormat.format(value);
            }
            catch (Exception e)
            {
            }
        }

        // Copy clip to clipboard
        clipboard.setPrimaryClip(ClipData.newPlainText("Specie", clip));

        // Clear selection
        selectList.clear();
        adapter.notifyDataSetChanged();

        // Restore menu
        mode = DISPLAY_MODE;
        invalidateOptionsMenu();
        return true;
    }

    // On remove click
    private boolean onRemoveClick()
    {
        List<String> removeList = new ArrayList<>();

        // Create a list of specie names to remove
        for (int i : selectList)
            removeList.add(nameList.get(i));

        for (String name : removeList)
        {
            // Look up name
            int i = nameList.indexOf(name);

            // Remove from the lists
            flagList.remove(i);
            nameList.remove(i);
            symbolList.remove(i);
            valueList.remove(i);
            longNameList.remove(i);
        }

        // Clear list and update adapter
        selectList.clear();
        adapter.notifyDataSetChanged();

        // Restore menu
        mode = DISPLAY_MODE;
        invalidateOptionsMenu();

        return true;
    }

    // On refresh click
    @SuppressWarnings("deprecation")
    private boolean onRefreshClick()
    {
        // Check connectivity before refresh
        ConnectivityManager manager =
            (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check connected
        if (info == null || !info.isConnected())
        {
            if (statusView != null)
                statusView.setText(R.string.no_connection);
            return false;
        }

        // Check wifi
        if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
        {
            if (statusView != null)
                statusView.setText(R.string.no_wifi);
            return false;
        }

        // Check roaming
        if (!roaming && info.isRoaming())
        {
            if (statusView != null)
                statusView.setText(R.string.roaming);
            return false;
        }

        // Schedule update
        if (statusView != null)
            statusView.setText(R.string.updating);

        // Start the task
        if (data != null)
            data.startParseTask(DAILY_URL);

        return true;
    }

    // On help click
    private boolean onHelpClick()
    {
        // Start help activity
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);

        return true;
    }

    // On settings click
    private boolean onSettingsClick()
    {
        // Start settings activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

        return true;
    }

    // On click
    public void onClick(View view)
    {
        int id = view.getId();

        switch (id)
        {
        // Value field
        case R.id.edit:
            if (select)
            {
                // Forces select all
                view.clearFocus();
                view.requestFocus();
            }

            // Do it only once
            select = false;
            break;

        // Any other view
        default:
            // Clear value field selection
            if (editView != null)
                editView.setSelection(0);
            select = true;
        }
    }

    // After text changed
    @Override
    public void afterTextChanged(Editable editable)
    {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        NumberFormat englishFormat = NumberFormat.getInstance(Locale.ENGLISH);

        String n = editable.toString();
        if (n.length() > 0)
        {
            // Parse current value
            try
            {
                Number number = numberFormat.parse(n);
                currentValue = number.doubleValue();
            }
            catch (Exception e)
            {
                // Try English locale
                try
                {
                    Number number = englishFormat.parse(n);
                    currentValue = number.doubleValue();
                }

                // Do nothing on exception
                catch (Exception ex)
                {
                    return;
                }
            }
        }

        // Recalculate all the values
        valueList.clear();
        for (String name : nameList)
        {
            Double value = (currentValue / convertValue) *
                ((valueMap.get(name) != null)? valueMap.get(name): 0.0);

            String s = numberFormat.format(value);
            valueList.add(s);
        }

        // Notify the adapter
        adapter.notifyDataSetChanged();
    }

    // Not used
    @Override
    public void beforeTextChanged(CharSequence s, int start,
                                  int count, int after) {}

    // Not used
    @Override
    public void onTextChanged(CharSequence s, int start,
                              int before, int count) {}

    // On editor action
    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event)
    {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        NumberFormat englishFormat = NumberFormat.getInstance(Locale.ENGLISH);

        switch (actionId)
        {
        case EditorInfo.IME_ACTION_DONE:

            // Parse current value
            String n = view.getText().toString();
            if (n.length() > 0)
            {
                try
                {
                    Number number = numberFormat.parse(n);
                    currentValue = number.doubleValue();
                }
                catch (Exception e)
                {
                    // Try English locale
                    try
                    {
                        Number number = englishFormat.parse(n);
                        currentValue = number.doubleValue();
                    }

                    // Set to one on exception
                    catch (Exception ex)
                    {
                        currentValue = 1.0;
                        view.setText(R.string.num_one);
                    }
                }
            }

            // Reformat the value field
            numberFormat.setGroupingUsed(false);
            String s = numberFormat.format(currentValue);
            view.setText(s);

            // Recalculate all the values
            valueList.clear();
            numberFormat.setGroupingUsed(true);
            for (String name : nameList)
            {
                Double value = (currentValue / convertValue) *
                               valueMap.get(name);

                s = numberFormat.format(value);
                valueList.add(s);
            }

            // Notify the adapter
            adapter.notifyDataSetChanged();

            return false; // Or the keypad won't go away
        }

        return false;
    }

    // On item click
    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id)
    {
        String value;
        int oldIndex;
        double oldValue;

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        // Check mode
        switch (mode)
        {
        // Display mode - replace the current specie
        case DISPLAY_MODE:
            // Save the current values
            oldIndex = currentIndex;
            oldValue = currentValue;

            // Set the current specie from the list
            currentIndex = specieNameList.indexOf(nameList.get(position));

            currentValue = (oldValue / convertValue) *
                valueMap.get(SPECIE_NAMES[currentIndex]);

            convertValue = valueMap.get(SPECIE_NAMES[currentIndex]);

            numberFormat.setGroupingUsed(false);
            value = numberFormat.format(currentValue);

            if (editView != null)
            {
                editView.setText(value);

                // Forces select all
                editView.clearFocus();
                editView.requestFocus();

                // Do it only once
                select = false;
            }

            if (flagView != null)
                flagView.setImageResource(SPECIE_FLAGS[currentIndex]);
            if (nameView != null)
                nameView.setText(SPECIE_NAMES[currentIndex]);
            if (symbolView != null)
                symbolView.setText(SPECIE_SYMBOLS[currentIndex]);
            if (longNameView != null)
                longNameView.setText(SPECIE_LONGNAMES[currentIndex]);

            // Remove the selected specie from the lists
            flagList.remove(position);
            nameList.remove(position);
            symbolList.remove(position);
            valueList.remove(position);
            longNameList.remove(position);

            // Add the old current specie to the start of the list
            flagList.add(0, SPECIE_FLAGS[oldIndex]);
            nameList.add(0, SPECIE_NAMES[oldIndex]);
            symbolList.add(0, SPECIE_SYMBOLS[oldIndex]);
            longNameList.add(0, SPECIE_LONGNAMES[oldIndex]);

            numberFormat.setGroupingUsed(true);
            value = numberFormat.format(oldValue);
            valueList.add(0, value);

            // Get preferences
            SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

            // Get editor
            SharedPreferences.Editor editor = preferences.edit();

            // Get entries
            JSONArray nameArray = new JSONArray(nameList);
            JSONArray valueArray = new JSONArray(valueList);

            // Update preferences
            editor.putString(PREF_NAMES, nameArray.toString());
            editor.putString(PREF_VALUES, valueArray.toString());
            editor.putInt(PREF_INDEX, currentIndex);
            numberFormat.setGroupingUsed(false);
            value = numberFormat.format(currentValue);
            editor.putString(PREF_VALUE, value);
            editor.apply();

            // Notify the adapter
            adapter.notifyDataSetChanged();
            break;

        // Select mode - toggle selection
        case SELECT_MODE:
            // Select mode - add or remove from list
            if (selectList.contains(position))
                selectList.remove(selectList.indexOf(position));

            else
                selectList.add(position);

            // Reset mode if list empty
            if (selectList.isEmpty())
            {
                mode = DISPLAY_MODE;
                invalidateOptionsMenu();
            }

            // Notify the adapter
            adapter.notifyDataSetChanged();
            break;
        }
    }

    // On item long click
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id)
    {
        // Switch to select mode, update menu
        mode = SELECT_MODE;
        invalidateOptionsMenu();

        // Clear the list and add the new selection
        selectList.clear();
        selectList.add(position);

        // Notify the adapter
        adapter.notifyDataSetChanged();
        return true;
    }

    // On activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        // Do nothing if cancelled
        if (resultCode != RESULT_OK)
            return;

        // Get index list from intent
        List<Integer> indexList = data.getIntegerArrayListExtra(CHOICE);

        // Add currencies from list
        for (int index : indexList)
        {
            // Don't add duplicates
            if ((currentIndex == index) ||
                nameList.contains(SPECIE_NAMES[index]))
                continue;

            flagList.add(SPECIE_FLAGS[index]);
            nameList.add(SPECIE_NAMES[index]);
            symbolList.add(SPECIE_SYMBOLS[index]);
            longNameList.add(SPECIE_LONGNAMES[index]);

            Double value = 1.0;

            try
            {
                value = (currentValue / convertValue) *
                    valueMap.get(SPECIE_NAMES[index]);
            }
            catch (Exception e)
            {
            }

            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMinimumFractionDigits(digits);
            numberFormat.setMaximumFractionDigits(digits);
            String s = numberFormat.format(value);

            valueList.add(s);
        }

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        // Get editor
        SharedPreferences.Editor editor = preferences.edit();

        // Get entries
        JSONArray nameArray = new JSONArray(nameList);
        JSONArray valueArray = new JSONArray(valueList);

        // Update preferences
        editor.putString(PREF_NAMES, nameArray.toString());
        editor.putString(PREF_VALUES, valueArray.toString());
        editor.apply();

        adapter.notifyDataSetChanged();
    }

    // On progress update
    @Override
    public void onProgressUpdate(String... date)
    {
        DateFormat dateParser =
            DateFormat.getDateInstance(DateFormat.FULL);
            // new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        DateFormat dateFormat =
            DateFormat.getDateInstance(DateFormat.MEDIUM);

        // Format the date for display
        if (date[0] != null)
        {
            try
            {
                Date update = dateParser.parse(date[0]);
                this.date = dateFormat.format(update);
            }
            catch (Exception e)
            {
            }

            String format = resources.getString(R.string.updated);
            String updated = String.format(Locale.getDefault(),
                                           format, this.date);
            if (dateView != null)
                dateView.setText(updated);
        }
        else if (statusView != null)
            statusView.setText(R.string.failed);
    }

    // The system calls this to perform work in the UI thread and
    // delivers the result from doInBackground()
    @Override
    public void onPostExecute(Map<String, Double> map)
    {
        // Check the map
        if (!map.isEmpty())
        {
            valueMap = map;
            valueMap.put("EUR", 1.0);

            // Empty the value list
            valueList.clear();

            // Get the convert value
            convertValue = valueMap.get(SPECIE_NAMES[currentIndex]);

            // Populate a new value list
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMinimumFractionDigits(digits);
            numberFormat.setMaximumFractionDigits(digits);
            for (String name : nameList)
            {
                int index = specieNameList.indexOf(name);

                Double value = (currentValue / convertValue) *
                    ((valueMap.get(name) != null)? valueMap.get(name): 0.0);

                String s = numberFormat.format(value);

                valueList.add(s);
            }

            // Get preferences
            SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

            // Get editor
            SharedPreferences.Editor editor = preferences.edit();

            // Get entries
            JSONObject valueObject = new JSONObject(valueMap);
            JSONArray nameArray = new JSONArray(nameList);
            JSONArray valueArray = new JSONArray(valueList);

            // Update preferences
            editor.putString(PREF_MAP, valueObject.toString());
            editor.putString(PREF_NAMES, nameArray.toString());
            editor.putString(PREF_VALUES, valueArray.toString());

            editor.putString(PREF_DATE, date);
            editor.apply();

            statusView.setText(R.string.ok);
            adapter.notifyDataSetChanged();
        }

        // Notify failed
        else if (statusView != null)
            statusView.setText(R.string.failed);
    }
}
