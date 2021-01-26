package com.momentum.timer.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.momentum.timer.R;
import com.momentum.timer.controller.dbhelper.DatabaseAccess;
import com.momentum.timer.databinding.ActivityTimerSettingsBinding;
import com.momentum.timer.models.TimerModel;

public class TimerSettingsActivity extends AppCompatActivity {
    ActivityTimerSettingsBinding binding;
    private final int RINGTONE_PICKER = 999;
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
        databaseAccess = DatabaseAccess.getInstance(this);

        timerModel = (TimerModel) getIntent().getSerializableExtra("data");
        if (timerModel.getSelectedAudio() != null) {
            Log.d("selectedTone", timerModel.getSelectedAudio());
            Ringtone ringtone = RingtoneManager.getRingtone(TimerSettingsActivity.this, Uri.parse(timerModel.getSelectedAudio()));
            binding.chengeToneText.setText("Change Alarm Tone (" + ringtone.getTitle(TimerSettingsActivity.this) + ")");
        }
        if (timerModel.getAudioSetting() == 1) {
            binding.textToVoice.setChecked(true);
        } else if (timerModel.getAudioSetting() == 2) {
            binding.warning.setChecked(true);
        } else if (timerModel.getAudioSetting() == 3) {
            binding.textToVoice.setChecked(true);
            binding.warning.setChecked(true);
        } else {
            binding.textToVoice.setChecked(false);
            binding.warning.setChecked(false);
        }
        binding.textToVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                databaseAccess.open();
                Log.d("textToVoice", isChecked + "");
                if (binding.warning.isChecked() && isChecked) {
                    databaseAccess.updateAudioSettings(timerModel.getId(), 3);
                } else if (isChecked) {
                    databaseAccess.updateAudioSettings(timerModel.getId(), 1);
                } else {
                    if (binding.warning.isChecked()) {
                        databaseAccess.updateAudioSettings(timerModel.getId(), 2);
                    } else {
                        databaseAccess.updateAudioSettings(timerModel.getId(), 0);
                    }
                }
                databaseAccess.close();
                Toast.makeText(TimerSettingsActivity.this, "Audio Settings Updated!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.warning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                databaseAccess.open();
                Log.d("textToVoice", isChecked + "");
                if (binding.textToVoice.isChecked() && isChecked) {
                    databaseAccess.updateAudioSettings(timerModel.getId(), 3);
                } else if (isChecked) {
                    databaseAccess.updateAudioSettings(timerModel.getId(), 2);
                } else {
                    if (binding.textToVoice.isChecked()) {
                        databaseAccess.updateAudioSettings(timerModel.getId(), 1);
                    } else {
                        databaseAccess.updateAudioSettings(timerModel.getId(), 0);
                    }
                }
                databaseAccess.close();
                Toast.makeText(TimerSettingsActivity.this, "Audio Settings Updated!", Toast.LENGTH_SHORT).show();

            }
        });

        binding.duplicateTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();
                if (databaseAccess.insertTimeRule(timerModel)) {
                    Toast.makeText(TimerSettingsActivity.this, "Timer Duplicated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
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
                } else {
                    Toast.makeText(TimerSettingsActivity.this, "Unable To Delete!", Toast.LENGTH_SHORT).show();
                }
                databaseAccess.close();
            }
        });
        binding.changeToneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.warning.isChecked()) {
                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for warning");
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                    if (timerModel.getSelectedAudio() != null) {
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Uri.parse(timerModel.getSelectedAudio()));
                    }
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                    startActivityForResult(intent, RINGTONE_PICKER);
                } else {
                    Toast.makeText(TimerSettingsActivity.this, "Enable 3 Second Warning Tick Sound For Changing Tone!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RINGTONE_PICKER) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            Ringtone ringtone = RingtoneManager.getRingtone(TimerSettingsActivity.this, uri);
            try {
                MediaPlayer player = MediaPlayer.create(this, uri);
                player.setLooping(false);
                player.start();
                Log.d("ringtonePicked", requestCode + " picked " + ringtone.getTitle(TimerSettingsActivity.this));
                databaseAccess.open();
                databaseAccess.updateTone(timerModel.getId(), uri.toString());
                databaseAccess.close();
                binding.chengeToneText.setText("Change Alarm Tone (" + ringtone.getTitle(TimerSettingsActivity.this) + ")");
                Toast.makeText(this, "Sound Updated Successfully", Toast.LENGTH_SHORT).show();
                player.stop();
            } catch (Exception e) {
                e.printStackTrace();
                binding.chengeToneText.setText("Change Alarm Tone");
                Toast.makeText(this, "This audio file is not supported!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}