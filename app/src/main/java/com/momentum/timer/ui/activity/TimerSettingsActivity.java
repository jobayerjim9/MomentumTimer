package com.momentum.timer.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.momentum.timer.R;
import com.momentum.timer.controller.dbhelper.DatabaseAccess;
import com.momentum.timer.databinding.ActivityTimerSettingsBinding;
import com.momentum.timer.models.TimerModel;

public class TimerSettingsActivity extends AppCompatActivity {
    ActivityTimerSettingsBinding binding;
    private TimerModel timerModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_timer_settings);
        init();
    }
    DatabaseAccess databaseAccess;
    private void init() {
        getSupportActionBar().setTitle("Timer Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseAccess=DatabaseAccess.getInstance(this);

        timerModel=(TimerModel)getIntent().getSerializableExtra("data");
        if (timerModel.getAudioSetting()==1) {
            binding.textToVoice.setChecked(true);
        }
        else {
            binding.warning.setChecked(true);
        }
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                databaseAccess.open();
                if (checkedId==R.id.textToVoice) {
                    databaseAccess.updateAudioSettings(timerModel.getId(),1);
                }
                else {
                    databaseAccess.updateAudioSettings(timerModel.getId(),2);
                }
                Toast.makeText(TimerSettingsActivity.this, "Audio Settings Updated!", Toast.LENGTH_SHORT).show();
                databaseAccess.close();
            }
        });
        binding.duplicateTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();
                if (databaseAccess.insertTimeRule(timerModel))
                {
                    Toast.makeText(TimerSettingsActivity.this, "Timer Duplicated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(TimerSettingsActivity.this, "Failed To Duplicate Timer!", Toast.LENGTH_SHORT).show();
                }
                databaseAccess.close();

            }
        });
        binding.deleteTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();
                if (databaseAccess.deleteTimer(timerModel.getId())) {
                    Toast.makeText(TimerSettingsActivity.this, "Timer Deleted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(TimerSettingsActivity.this, "Unable To Delete!", Toast.LENGTH_SHORT).show();
                }
                databaseAccess.close();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}