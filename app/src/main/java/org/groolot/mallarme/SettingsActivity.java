package org.groolot.mallarme;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class SettingsActivity extends PreferenceActivity implements OnClickListener {

    @SuppressWarnings("deprecation")
    private CheckBoxPreference chBox;
    private ToggleButton mToggleButton;
    private SeekBar seekBar;
    private EditText editText;


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_settings);

        chBox = (CheckBoxPreference) findPreference("checkBox");
        mToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        seekBar = (SeekBar) findViewById(R.id.seekBarPrefSeekBar);
        editText = (EditText) findViewById(R.id.editTextValue);

        mToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        mToggleButton.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                editText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String txt = editText.getText().toString();

                // Position du curseur à la fin de la chaine de caractères
                editText.setSelection(txt.length());

                // Test pour voir si une CdC est présente sinon on met 0
                if (!txt.equals("") && txt.trim().length() > 0) {
                    seekBar.setProgress(Integer.parseInt(s.toString()));
                    // Si la CdC dépasse 100 on l'a remet à 100
                    if (Integer.parseInt(s.toString()) > 100) {
                        editText.setText("100");
                        seekBar.setProgress(100);
                    }
                } else {
                    editText.setText("0");
                    seekBar.setProgress(0);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    @Override
    public void onClick(View view) {
        if (mToggleButton.isChecked()) {

            Log.i("TOGGLE", "ON");
        } else {
            Log.i("TOGGLE", "OFF");
        }
    }
}
