package livewind.example.andro.liveWind;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {


            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(livewind.example.andro.liveWind.R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };



    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction()
                .add(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(livewind.example.andro.liveWind.R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            SwitchPreference mChangeDisplay = (SwitchPreference) findPreference(getString(R.string.settings_display_boolean_key));
            EditTextPreference mCostFilter = (EditTextPreference) findPreference(getString(R.string.settings_filter_cost_key));
            MultiSelectListPreference mSportsFilter = (MultiSelectListPreference) findPreference(getString(R.string.settings_filter_sports_key));
            PreferenceCategory mCategory = (PreferenceCategory) findPreference("pref_key_settings_title_display");
            mCategory.removePreference(mChangeDisplay);
            mCategory.removePreference(mCostFilter);
            mCategory.removePreference(mSportsFilter);
            //mCategory.removePreference(mFromTimestamp);
            //mCategory.removePreference(mToTimestamp);

            bindPreferenceSummaryToValue(findPreference(getString(livewind.example.andro.liveWind.R.string.settings_user_phone_key)));
            bindPreferenceSummaryToValue(findPreference(getString(livewind.example.andro.liveWind.R.string.settings_user_email_key)));
            bindPreferenceSummaryToValue(findPreference(getString(livewind.example.andro.liveWind.R.string.settings_user_web_key)));

         //   bindPreferenceSummaryToValue(findPreference(getString(R.string.settings_display_countries_title)));
            //Display and sorting options
         //   bindPreferenceSummaryToValue(findPreference(getString(R.string.settings_display_events)));
         //   bindPreferenceSummaryToValue(findPreference(getString(R.string.settings_display_trips)));
            bindPreferenceSummaryToValue(findPreference(getString(livewind.example.andro.liveWind.R.string.settings_display_sorting_events_by_key)));
            bindPreferenceSummaryToValue(findPreference(getString(livewind.example.andro.liveWind.R.string.settings_display_sorting_trips_by_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.settings_display_sorting_order_trips_by_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.settings_display_wind_power_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.settings_display_trips_key)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.settings_filter_cost_key)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.settings_filter_date_from_key)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.settings_filter_date_to_key)));
            // feedback preference click listener
            Preference myPref = findPreference(getString(R.string.settings_feedback_send_key));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });

            bindPreferenceSummaryToValue(findPreference(getString(R.string.settings_discount_code_key)));
            Preference discountPref = findPreference(getString(R.string.settings_discount_code_key));
            discountPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                    String discountCode = sharedPrefs.getString(getContext().getString(R.string.settings_discount_code_key),"1");
                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Discount code", discountCode);
                    if (clipboard == null) return true;
                    clipboard.setPrimaryClip(clip);
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), CatalogActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jgjozkowy@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from SurfAdvisor");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
}
