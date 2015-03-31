package org.groolot.mallarme;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    private CheckBoxPreference chBox;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_settings);

        chBox = (CheckBoxPreference) findPreference("checkBox");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


}
