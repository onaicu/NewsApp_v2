package mobi.storedot.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    /**declare that the EarthquakePreferenceFragment class should implement the OnPreferenceChangeListener interface
     */
    public static class StoryPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener{

        /**
         * in the SettingsActivity, within the StoryPreferenceFragment inner class,
         * override the onCreate() method to use the settings_main XML resource that we defined earlier.
         */

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            /**Given the key of a preference, we can use PreferenceFragment's findPreference() method to get the Preference object,
             * and setup the preference using a helper method called bindPreferenceSummaryToValue().
             */
            Preference Sport = findPreference(getString(R.string.settings_sectionName_key));
            bindPreferenceSummaryToValue(Sport);

            /** find the “order by” Preference object according to its key.
             */

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }

        /**Now we need to define the bindPreferenceSummaryToValue() helper method to set the current
        *StoryPreferenceFragment instance as the listener on each preference. We also read the current
        *value of the preference stored in the SharedPreferences on the device, and display that in the preference
        *summary (so that the user can see the current value of the preference)
        */


        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        /**override the onPreferenceChange() method. The code in this method takes care of updating the displayed preference
         *summary after it has been changed.
         *
         * Since this is the first ListPreference that the EarthquakePreferenceFragment is encountering,
         * update the onPreferenceChange() method in EarthquakePreferenceFragment to properly update the summary
         * of a ListPreference (using the label, instead of the key).
         */

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }

            else if (preference instanceof EditTextPreference){
                preference.setSummary(stringValue);
            }
            return true;
        }
    }

}

