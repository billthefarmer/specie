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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
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
import android.widget.RemoteViews;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
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

    // Initial active specie name
    public static final String DEFAULT_SPECIE = "EUR";

    // Initial specie name list
    public static final String SPECIE_LIST[] =
    {
        "USD", "GBP", "CAD", "AUD"
    };

    public static final class Specie
    {
        public final String name;
        public final String symbol;
        public final int longname;
        public final int flag;

        private Specie(String name, String symbol, int longname, int flag)
        {
            this.name = name;
            this.symbol = symbol;
            this.longname = longname;
            this.flag = flag;
        }
    }

    public static final Specie SPECIES[] =
    {
        new Specie("EUR", "€",    R.string.long_eur, R.drawable.flag_eu),
        new Specie("USD", "$",    R.string.long_usd, R.drawable.flag_us),
        new Specie("GBP", "£",    R.string.long_gbp, R.drawable.flag_gb),
        new Specie("JPY", "¥",    R.string.long_jpy, R.drawable.flag_jp),
        new Specie("AUD", "$",    R.string.long_aud, R.drawable.flag_au),
        new Specie("CHF", "",     R.string.long_chf, R.drawable.flag_ch),
        new Specie("CAD", "$",    R.string.long_cad, R.drawable.flag_ca),
        new Specie("SVC", "$",    R.string.long_svc, R.drawable.flag_sv),
        new Specie("CVE", "",     R.string.long_cve, R.drawable.flag_cv),
        new Specie("ZMW", "",     R.string.long_zmw, R.drawable.flag_zm),
        new Specie("YER", "﷼",    R.string.long_yer, R.drawable.flag_ye),
        new Specie("LKR", "₨",    R.string.long_lkr, R.drawable.flag_lk),
        new Specie("PLN", "zł",   R.string.long_pln, R.drawable.flag_pl),
        new Specie("PEN", "S/.",  R.string.long_pen, R.drawable.flag_pe),
        new Specie("IQD", "",     R.string.long_iqd, R.drawable.flag_iq),
        new Specie("STN", "",     R.string.long_stn, R.drawable.flag_st),
        new Specie("XPF", "",     R.string.long_xpf, R.drawable.flag_pf),
        new Specie("ALL", "Lek",  R.string.long_all, R.drawable.flag_al),
        new Specie("MUR", "₨",    R.string.long_mur, R.drawable.flag_mu),
        new Specie("DOP", "RD$",  R.string.long_dop, R.drawable.flag_do),
        new Specie("NZD", "$",    R.string.long_nzd, R.drawable.flag_nz),
        new Specie("HRK", "kn",   R.string.long_hrk, R.drawable.flag_hr),
        new Specie("DZD", "",     R.string.long_dzd, R.drawable.flag_dz),
        new Specie("ARS", "$",    R.string.long_ars, R.drawable.flag_ar),
        new Specie("PYG", "Gs",   R.string.long_pyg, R.drawable.flag_py),
        new Specie("BND", "$",    R.string.long_bnd, R.drawable.flag_bn),
        new Specie("KMF", "",     R.string.long_kmf, R.drawable.flag_km),
        new Specie("SZL", "",     R.string.long_szl, R.drawable.flag_sz),
        new Specie("SOS", "S",    R.string.long_sos, R.drawable.flag_so),
        new Specie("ANG", "ƒ",    R.string.long_ang, R.drawable.flag_cw),
        new Specie("PKR", "₨",    R.string.long_pkr, R.drawable.flag_pk),
        new Specie("KRW", "₩",    R.string.long_krw, R.drawable.flag_kr),
        new Specie("AZN", "₼",    R.string.long_azn, R.drawable.flag_az),
        new Specie("CRC", "₡",    R.string.long_crc, R.drawable.flag_cr),
        new Specie("JMD", "J$",   R.string.long_jmd, R.drawable.flag_jm),
        new Specie("SSP", "",     R.string.long_ssp, R.drawable.flag_ss),
        new Specie("ERN", "",     R.string.long_ern, R.drawable.flag_er),
        new Specie("WST", "",     R.string.long_wst, R.drawable.flag_ws),
        new Specie("EGP", "£",    R.string.long_egp, R.drawable.flag_eg),
        new Specie("SGD", "$",    R.string.long_sgd, R.drawable.flag_sg),
        new Specie("ZAR", "R",    R.string.long_zar, R.drawable.flag_za),
        new Specie("KGS", "лв",   R.string.long_kgs, R.drawable.flag_kg),
        new Specie("MGA", "",     R.string.long_mga, R.drawable.flag_mg),
        new Specie("SRD", "$",    R.string.long_srd, R.drawable.flag_sr),
        new Specie("GHS", "",     R.string.long_ghs, R.drawable.flag_gh),
        new Specie("MOP", "",     R.string.long_mop, R.drawable.flag_mo),
        new Specie("BAM", "KM",   R.string.long_bam, R.drawable.flag_ba),
        new Specie("INR", "₹",    R.string.long_inr, R.drawable.flag_in),
        new Specie("TRY", "",     R.string.long_try, R.drawable.flag_tr),
        new Specie("TWD", "NT$",  R.string.long_twd, R.drawable.flag_tw),
        new Specie("TMT", "",     R.string.long_tmt, R.drawable.flag_tm),
        new Specie("VES", "",     R.string.long_ves, R.drawable.flag_ve),
        new Specie("SBD", "$",    R.string.long_sbd, R.drawable.flag_sb),
        new Specie("MWK", "",     R.string.long_mwk, R.drawable.flag_mw),
        new Specie("GTQ", "Q",    R.string.long_gtq, R.drawable.flag_gt),
        new Specie("LBP", "£",    R.string.long_lbp, R.drawable.flag_lb),
        new Specie("HUF", "Ft",   R.string.long_huf, R.drawable.flag_hu),
        new Specie("NGN", "₦",    R.string.long_ngn, R.drawable.flag_ng),
        new Specie("IRR", "﷼",    R.string.long_irr, R.drawable.flag_ir),
        new Specie("MKD", "ден",  R.string.long_mkd, R.drawable.flag_mk),
        new Specie("BIF", "",     R.string.long_bif, R.drawable.flag_bi),
        new Specie("MMK", "",     R.string.long_mmk, R.drawable.flag_mm),
        new Specie("MVR", "",     R.string.long_mvr, R.drawable.flag_mv),
        new Specie("SEK", "kr",   R.string.long_sek, R.drawable.flag_se),
        new Specie("NPR", "₨",    R.string.long_npr, R.drawable.flag_np),
        new Specie("ISK", "kr",   R.string.long_isk, R.drawable.flag_is),
        new Specie("GIP", "£",    R.string.long_gip, R.drawable.flag_gi),
        new Specie("GEL", "₾",    R.string.long_gel, R.drawable.flag_ge),
        new Specie("COP", "$",    R.string.long_cop, R.drawable.flag_co),
        new Specie("BZD", "BZ$",  R.string.long_bzd, R.drawable.flag_bz),
        new Specie("GNF", "",     R.string.long_gnf, R.drawable.flag_gn),
        new Specie("SLL", "",     R.string.long_sll, R.drawable.flag_sl),
        new Specie("KES", "",     R.string.long_kes, R.drawable.flag_ke),
        new Specie("AED", "",     R.string.long_aed, R.drawable.flag_ae),
        new Specie("PHP", "₱",    R.string.long_php, R.drawable.flag_ph),
        new Specie("ILS", "₪",    R.string.long_ils, R.drawable.flag_il),
        new Specie("MRO", "",     R.string.long_mro, R.drawable.flag_mr),
        new Specie("BOB", "$b",   R.string.long_bob, R.drawable.flag_bo),
        new Specie("GYD", "$",    R.string.long_gyd, R.drawable.flag_gy),
        new Specie("RWF", "",     R.string.long_rwf, R.drawable.flag_rw),
        new Specie("MZN", "MT",   R.string.long_mzn, R.drawable.flag_mz),
        new Specie("UGX", "",     R.string.long_ugx, R.drawable.flag_ug),
        new Specie("CNY", "¥",    R.string.long_cny, R.drawable.flag_cn),
        new Specie("SAR", "﷼",    R.string.long_sar, R.drawable.flag_sa),
        new Specie("MYR", "RM",   R.string.long_myr, R.drawable.flag_my),
        new Specie("KZT", "лв",   R.string.long_kzt, R.drawable.flag_kz),
        new Specie("AFN", "؋",    R.string.long_afn, R.drawable.flag_af),
        new Specie("NAD", "$",    R.string.long_nad, R.drawable.flag_na),
        new Specie("SYP", "£",    R.string.long_syp, R.drawable.flag_sy),
        new Specie("TOP", "",     R.string.long_top, R.drawable.flag_to),
        new Specie("VUV", "",     R.string.long_vuv, R.drawable.flag_vu),
        new Specie("IDR", "Rp",   R.string.long_idr, R.drawable.flag_id),
        new Specie("TND", "",     R.string.long_tnd, R.drawable.flag_tn),
        new Specie("XOF", "",     R.string.long_xof, R.drawable.flag_be),
        new Specie("TJS", "",     R.string.long_tjs, R.drawable.flag_tj),
        new Specie("ETB", "",     R.string.long_etb, R.drawable.flag_et),
        new Specie("XCD", "",     R.string.long_xcd, R.drawable.flag_ag),
        new Specie("LAK", "₭",    R.string.long_lak, R.drawable.flag_la),
        new Specie("BWP", "P",    R.string.long_bwp, R.drawable.flag_bw),
        new Specie("KWD", "",     R.string.long_kwd, R.drawable.flag_kw),
        new Specie("CZK", "Kč",   R.string.long_czk, R.drawable.flag_cz),
        new Specie("PGK", "",     R.string.long_pgk, R.drawable.flag_pg),
        new Specie("UAH", "₴",    R.string.long_uah, R.drawable.flag_ua),
        new Specie("GMD", "",     R.string.long_gmd, R.drawable.flag_gm),
        new Specie("AWG", "ƒ",    R.string.long_awg, R.drawable.flag_aw),
        new Specie("AOA", "",     R.string.long_aoa, R.drawable.flag_ao),
        new Specie("KHR", "៛",    R.string.long_khr, R.drawable.flag_kh),
        new Specie("NOK", "kr",   R.string.long_nok, R.drawable.flag_no),
        new Specie("MAD", "",     R.string.long_mad, R.drawable.flag_ma),
        new Specie("RON", "lei",  R.string.long_ron, R.drawable.flag_ro),
        new Specie("BYN", "",     R.string.long_byn, R.drawable.flag_by),
        new Specie("RSD", "Дин.", R.string.long_rsd, R.drawable.flag_rs),
        new Specie("BSD", "$",    R.string.long_bsd, R.drawable.flag_bs),
        new Specie("DJF", "",     R.string.long_djf, R.drawable.flag_dj),
        new Specie("HNL", "L",    R.string.long_hnl, R.drawable.flag_hn),
        new Specie("SCR", "₨",    R.string.long_scr, R.drawable.flag_sc),
        new Specie("BHD", "",     R.string.long_bhd, R.drawable.flag_bh),
        new Specie("OMR", "﷼",    R.string.long_omr, R.drawable.flag_om),
        new Specie("RUB", "₽",    R.string.long_rub, R.drawable.flag_ru),
        new Specie("LYD", "",     R.string.long_lyd, R.drawable.flag_ly),
        new Specie("CLP", "$",    R.string.long_clp, R.drawable.flag_cl),
        new Specie("UYU", "$U",   R.string.long_uyu, R.drawable.flag_uy),
        new Specie("FJD", "$",    R.string.long_fjd, R.drawable.flag_fj),
        new Specie("CDF", "",     R.string.long_cdf, R.drawable.flag_cd),
        new Specie("LSL", "",     R.string.long_lsl, R.drawable.flag_ls),
        new Specie("TZS", "",     R.string.long_tzs, R.drawable.flag_tz),
        new Specie("BBD", "$",    R.string.long_bbd, R.drawable.flag_bb),
        new Specie("BDT", "",     R.string.long_bdt, R.drawable.flag_bd),
        new Specie("QAR", "﷼",    R.string.long_qar, R.drawable.flag_qa),
        new Specie("MXN", "$",    R.string.long_mxn, R.drawable.flag_mx),
        new Specie("AMD", "",     R.string.long_amd, R.drawable.flag_am),
        new Specie("NIO", "C$",   R.string.long_nio, R.drawable.flag_ni),
        new Specie("LRD", "$",    R.string.long_lrd, R.drawable.flag_lr),
        new Specie("SDG", "",     R.string.long_sdg, R.drawable.flag_sd),
        new Specie("MRU", "",     R.string.long_mru, R.drawable.flag_mr),
        new Specie("MNT", "₮",    R.string.long_mnt, R.drawable.flag_mn),
        new Specie("BRL", "R$",   R.string.long_brl, R.drawable.flag_br),
        new Specie("HKD", "$",    R.string.long_hkd, R.drawable.flag_hk),
        new Specie("THB", "฿",    R.string.long_thb, R.drawable.flag_th),
        new Specie("XAF", "",     R.string.long_xaf, R.drawable.flag_cm),
        new Specie("MDL", "",     R.string.long_mdl, R.drawable.flag_md),
        new Specie("PAB", "B/.",  R.string.long_pab, R.drawable.flag_pa),
        new Specie("TTD", "TT$",  R.string.long_ttd, R.drawable.flag_tt),
        new Specie("HTG", "",     R.string.long_htg, R.drawable.flag_ht),
        new Specie("CUP", "₱",    R.string.long_cup, R.drawable.flag_cu),
        new Specie("DKK", "kr",   R.string.long_dkk, R.drawable.flag_dk),
        new Specie("JOD", "",     R.string.long_jod, R.drawable.flag_jo),
        new Specie("BGN", "лв",   R.string.long_bgn, R.drawable.flag_bg),
        new Specie("VND", "₫",    R.string.long_vnd, R.drawable.flag_vn),
        new Specie("UZS", "лв",   R.string.long_uzs, R.drawable.flag_uz),
    };

    public static int specieIndex(String name)
    {
        for (int i = 0; i < SPECIES.length; i++)
        {
            if (SPECIES[i].name.equals(name))
            {
                return i;
            }
        }
        return -1;
    }

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
    public static final String PREF_ENTRY = "pref_entry";
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
    private int widgetEntry = 0;
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
        currentIndex = preferences.getInt(PREF_INDEX, -1);
        if (currentIndex == -1) {
            currentIndex = specieIndex(DEFAULT_SPECIE);
        }

        // Get widget entry
        widgetEntry = Integer.parseInt(preferences.getString(PREF_ENTRY, "0"));

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
            flagView.setImageResource(SPECIES[currentIndex].flag);
        if (nameView != null)
            nameView.setText(SPECIES[currentIndex].name);
        if (symbolView != null)
            symbolView.setText(SPECIES[currentIndex].symbol);
        if (longNameView != null)
            longNameView.setText(SPECIES[currentIndex].longname);

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
            numberFormat.setGroupingUsed(true);
            for (String name : nameList)
            {
                try
                {
                    Double v = valueMap.get(name);
                    valueList.add(numberFormat.format(v));
                }

                catch (Exception e)
                {
                    valueList.add(numberFormat.format(Double.NaN));
                }
            }
        }

        // Get the current conversion rate
        convertValue = valueMap.containsKey(SPECIES[currentIndex].name)?
            valueMap.get(SPECIES[currentIndex].name): Double.NaN;

        // Recalculate all the values
        valueList.clear();
        numberFormat.setGroupingUsed(true);
        for (String name : nameList)
        {
            try
            {
                Double v = (currentValue / convertValue) *
                    valueMap.get(name);

                valueList.add(numberFormat.format(v));
            }

            catch (Exception e)
            {
                valueList.add(numberFormat.format(0.0));
            }
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
            int index = specieIndex(name);

            if (flagList != null)
                flagList.add(SPECIES[index].flag);
            if (symbolList != null)
                symbolList.add(SPECIES[index].symbol);
            if (longNameList != null)
                longNameList.add(SPECIES[index].longname);
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

        // Update widgets
        updateWidgets();

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
        editor.putString(PREF_ENTRY, Integer.toString(widgetEntry));

        editor.putString(PREF_VALUE, Double.toString(currentValue));
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

    // updateWidgets
    @SuppressLint("InlinedApi")
    private void updateWidgets()
    {
        // Set digits
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);
        numberFormat.setGroupingUsed(true);

        String value = numberFormat.format(currentValue);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        // Get manager
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName provider = new
            ComponentName(this, SpecieWidgetProvider.class);

        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(provider);
        for (int appWidgetId: appWidgetIds)
        {
            widgetEntry = preferences.getInt(String.valueOf(appWidgetId),
                                             widgetEntry);
            if (widgetEntry >= nameList.size())
                widgetEntry = 0;

            String entryName = nameList.get(widgetEntry);
            String entryValue = valueList.get(widgetEntry);
            int entryIndex = specieIndex(entryName);
            String longName = getString(SPECIES[entryIndex].longname);

            // Create an Intent to launch Specie
            Intent intent = new Intent(this, Main.class);
            PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent,
                                          PendingIntent.FLAG_UPDATE_CURRENT |
                                          PendingIntent.FLAG_IMMUTABLE);

            // Create an Intent to configure widget
            Intent config = new Intent(this, SpecieWidgetConfigure.class);
            config.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent configIntent =
                PendingIntent.getActivity(this, 0, config,
                                          PendingIntent.FLAG_UPDATE_CURRENT |
                                          PendingIntent.FLAG_IMMUTABLE);
            // Get the layout for the widget
            RemoteViews views = new
                RemoteViews(getPackageName(), R.layout.widget);

            // Attach an on-click listener to the view.
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            views.setOnClickPendingIntent(R.id.config, configIntent);

            views.setTextViewText(R.id.current_name,
                                  SPECIES[currentIndex].name);
            views.setTextViewText(R.id.current_symbol,
                                  SPECIES[currentIndex].symbol);
            views.setTextViewText(R.id.current_value, value);

            views.setImageViewResource(R.id.flag, SPECIES[entryIndex].flag);
            views.setTextViewText(R.id.name, entryName);
            views.setTextViewText(R.id.symbol, SPECIES[entryIndex].symbol);
            views.setTextViewText(R.id.value, entryValue);
            views.setTextViewText(R.id.long_name, longName);

            // Tell the AppWidgetManager to perform an update on the
            // current app widget.
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
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
        numberFormat.setGroupingUsed(true);
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
                valueList.add(numberFormat.format(0.0));
            }
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
                try
                {
                    Double value = (currentValue / convertValue) *
                        valueMap.get(name);

                    valueList.add(numberFormat.format(value));
                }

                catch (Exception e)
                {
                    valueList.add(numberFormat.format(0.0));
                }
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
            currentIndex = specieIndex(nameList.get(position));

            try
            {
                currentValue = (oldValue / convertValue) *
                    valueMap.get(SPECIES[currentIndex].name);
            }

            catch (Exception e)
            {
                currentValue = Double.NaN;
            }

            convertValue = valueMap.containsKey(SPECIES[currentIndex].name)?
                valueMap.get(SPECIES[currentIndex].name): Double.NaN;

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
                flagView.setImageResource(SPECIES[currentIndex].flag);
            if (nameView != null)
                nameView.setText(SPECIES[currentIndex].name);
            if (symbolView != null)
                symbolView.setText(SPECIES[currentIndex].symbol);
            if (longNameView != null)
                longNameView.setText(SPECIES[currentIndex].longname);

            // Remove the selected specie from the lists
            flagList.remove(position);
            nameList.remove(position);
            symbolList.remove(position);
            valueList.remove(position);
            longNameList.remove(position);

            // Add the old current specie to the start of the list
            flagList.add(0, SPECIES[oldIndex].flag);
            nameList.add(0, SPECIES[oldIndex].name);
            symbolList.add(0, SPECIES[oldIndex].symbol);
            longNameList.add(0, SPECIES[oldIndex].longname);

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
            value = Double.toString(currentValue);
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
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);
        numberFormat.setGroupingUsed(true);

        // Add currencies from list
        for (int index : indexList)
        {
            // Don't add duplicates or currencies not available
            if ((currentIndex == index) ||
                nameList.contains(SPECIES[index].name) ||
                !valueMap.containsKey(SPECIES[index].name))
                continue;

            flagList.add(SPECIES[index].flag);
            nameList.add(SPECIES[index].name);
            symbolList.add(SPECIES[index].symbol);
            longNameList.add(SPECIES[index].longname);

            Double value = 1.0;

            try
            {
                value = (currentValue / convertValue) *
                    valueMap.get(SPECIES[index].name);
            }

            catch (Exception e) {}

            valueList.add(numberFormat.format(value));
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
            convertValue = valueMap.containsKey(SPECIES[currentIndex].name)?
                valueMap.get(SPECIES[currentIndex].name): Double.NaN;

            // Populate a new value list
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMinimumFractionDigits(digits);
            numberFormat.setMaximumFractionDigits(digits);
            numberFormat.setGroupingUsed(true);
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
