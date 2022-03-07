# ![Logo](src/main/res/drawable-hdpi/ic_launcher.png) Specie [![.github/workflows/build.yml](https://github.com/billthefarmer/specie/workflows/.github/workflows/build.yml/badge.svg)](https://github.com/billthefarmer/specie/actions) [![Release](https://img.shields.io/github/release/billthefarmer/specie.svg?logo=github)](https://github.com/billthefarmer/specie/releases)
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.svg" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/org.billthefarmer.specie)

Android currency conversion. The app is available on [F-Droid](https://f-droid.org/packages/org.billthefarmer.specie) and [here](https://github.com/billthefarmer/specie/releases). This app is called Specie because I already have an app called [Currency](https://github.com/billthefarmer/currency).

![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/specie/specie.png) ![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/specie/choose.png)

![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/specie/settings.png) ![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/specie/about.png)

 * Currency rates from [Floatrates](https://www.floatrates.com)
 * 150 international currencies
 * Currency rates updated daily
 * Last rate update retained for use offline
 * App widget to display selected currency

## Flags
It's quite possible that one or more of the flags shown against
currencies are incorrect. If so, please report an
[issue](https://github.com/billthefarmer/specie/issues).

## Usage
### Edit
Touch the current currency value field to edit the value. The display
will be updated dynamically as you type. Touch the **Done** button on
the numeric keypad to update the value field.  The whole value field
is selected by default when touched so it will be overwritten by
typing in a new value. To deselect the text, touch an adjacent area,
like the currency flag.

### Toolbar
The icons in the toolbar from left to right are:
* **Add** a currency to the list. A scrollable list of currencies will
   pop up. Touch an entry to add it or touch the **Cancel** button
   below the list. Long touch an entry to select it. Once one currency
   is selected others may be added or removed from the selection by
   touching them. Another long touch on another currency will clear
   the list and select the new currency. Touch the **Clear** button to
   clear the selection. Touch the **Select** button to add the
   selection. The entries will be added to the list in the order
   selected.
* **Refresh** Get the day's currency rates from
  [Floatrates](https://www.floatrates.com). The date shown on the left
  above the currency list will be updated if new rates are
  available. It may show the previous day because the rates are
  updated at around midday. The status display on the right above
  the currency list will show 'OK', 'No Connection', 'No WiFi' or
  'Roaming' according to the update settings. It may show 'Failed' if
  the connection times out or fails to connect.
* **Help** Display help text.
* **Settings** Display the settings screen.

### Edit currency list
Touch a currency entry in the list to make it current. The old current
currency will move to the top of the list. Long touch a currency entry
to select it. Once one currency is selected others may be added or
removed from the selection by touching them. Another long touch on
another currency will clear the list and select the new currency. The
icons in the toolbar will change to:
* **Clear** the selection.
* **Remove** the selected currencies.
* **Copy** selection value to clipboard. Only one value will be copied.

## Widget
On creating a widget a selection dialog will pop up showing the
current currencies. Select the currency to display. This overrides the
**Widget entry** setting. Touch the configure icon in the top right of
the widget to change the currency. This will only change the currency
for the last widget created regardless of which widget was
touched. Android 12 provides an alternate method of configuring
widgets which will work correctly.

## Settings
### Update
* **WiFi** Update while connected on WiFi only
* **Roaming** Update while roaming

### Widget
* **Widget entry** Select the currency entry to display in the
  widget. Defaults to the first.

### Numbers
* **Fraction digits** Select the number of digits to display after the
  decimal point. A popup list of options will be displayed.

### Theme
* **Dark** Use dark theme

### About
* **About** Display the version, copyright and licence.
