package com.momentum.timer.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.momentum.timer.R;
import com.momentum.timer.controller.dbhelper.DatabaseAccess;
import com.momentum.timer.databinding.ActivityAddTimerBinding;
import com.momentum.timer.models.TimerModel;

public class AddTimerActivity extends AppCompatActivity {
    private ActivityAddTimerBinding binding;
    private TimerModel timerModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_add_timer);
        init();
    }

    private void init() {
        getSupportActionBar().setTitle(getResources().getString(R.string.add_timer));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.hoursInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String temp = s.toString();
                    if (Integer.parseInt(temp) > 20 || Integer.parseInt(temp) < 0) {
                        binding.hoursInput.setText(20 + "");
                        Toast.makeText(AddTimerActivity.this, "Value must be between 0-20", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.minutesInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String temp = s.toString();
                    if (Integer.parseInt(temp) > 59 || Integer.parseInt(temp) < 0) {
                        binding.minutesInput.setText(59 + "");
                        Toast.makeText(AddTimerActivity.this, "Value must be between 0-59", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.secondsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String temp = s.toString();
                    if (Integer.parseInt(temp) > 59 || Integer.parseInt(temp) < 0) {
                        binding.secondsInput.setText(59 + "");
                        Toast.makeText(AddTimerActivity.this, "Value must be between 0-59", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.spacingPercentageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String temp=progress+"%";
                binding.seekBarPercentage.setText(temp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.createTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();

            }
        });
        binding.incPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.spacingPercentageSeekBar.getProgress() < 100) {
                    binding.spacingPercentageSeekBar.setProgress(binding.spacingPercentageSeekBar.getProgress() + 1);
                }
            }
        });
        binding.dePercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.spacingPercentageSeekBar.getProgress() > 0)
                    binding.spacingPercentageSeekBar.setProgress(binding.spacingPercentageSeekBar.getProgress() - 1);
            }
        });
    }

    private void validateData() {
        String timerTitle=binding.timerTitleInput.getEditText().getText().toString().trim();
        String dominoTitle=binding.dominoSegmentTitleInput.getEditText().getText().toString().trim();
        String hoursText=binding.hoursInput.getText().toString().trim();
        String minutesText=binding.minutesInput.getText().toString().trim();
        String secondsText=binding.secondsInput.getText().toString().trim();
        String restTitle=binding.restSegmentTitleInput.getEditText().getText().toString().trim();
        int spacingPercentage=binding.spacingPercentageSeekBar.getProgress();
        String numberOfSegment=binding.numberOfDominoInput.getEditText().getText().toString().trim();
        String numberOfRepeats=binding.numberOfRepeatInput.getEditText().getText().toString().trim();
        if (timerTitle.isEmpty()) {
            binding.timerTitleInput.setErrorEnabled(true);
            binding.timerTitleInput.setError("Enter a title to proceed");
        }
        else if (dominoTitle.isEmpty()) {
            binding.timerTitleInput.setErrorEnabled(false);
            binding.dominoSegmentTitleInput.setErrorEnabled(true);
            binding.dominoSegmentTitleInput.setError("Enter a domino title to proceed");
        } else if (hoursText.isEmpty() && minutesText.isEmpty() && secondsText.isEmpty()) {
            binding.timerTitleInput.setErrorEnabled(false);
            binding.dominoSegmentTitleInput.setErrorEnabled(false);
            Toast.makeText(this, "Enter duration of domino segment", Toast.LENGTH_SHORT).show();
        } else if (restTitle.isEmpty()) {
            binding.timerTitleInput.setErrorEnabled(false);
            binding.dominoSegmentTitleInput.setErrorEnabled(false);
            binding.restSegmentTitleInput.setErrorEnabled(true);
            binding.restSegmentTitleInput.setError("Enter a domino title to proceed");
        }
        else if (spacingPercentage<1) {
            Toast.makeText(this, "Pick a valid percentage!", Toast.LENGTH_SHORT).show();
        }
        else if (numberOfSegment.isEmpty()) {
            binding.timerTitleInput.setErrorEnabled(false);
            binding.dominoSegmentTitleInput.setErrorEnabled(false);
            binding.restSegmentTitleInput.setErrorEnabled(false);
            binding.numberOfDominoInput.setErrorEnabled(true);
            binding.numberOfDominoInput.setError("Enter number of domino segments to proceed");
        }
        else if (numberOfRepeats.isEmpty()) {
            binding.timerTitleInput.setErrorEnabled(false);
            binding.dominoSegmentTitleInput.setErrorEnabled(false);
            binding.restSegmentTitleInput.setErrorEnabled(false);
            binding.numberOfDominoInput.setErrorEnabled(false);
            binding.numberOfRepeatInput.setErrorEnabled(true);
            binding.numberOfRepeatInput.setError("Enter number of repeat to proceed");
        }
        else {
            binding.timerTitleInput.setErrorEnabled(false);
            binding.dominoSegmentTitleInput.setErrorEnabled(false);
            binding.restSegmentTitleInput.setErrorEnabled(false);
            binding.numberOfDominoInput.setErrorEnabled(false);
            binding.numberOfRepeatInput.setErrorEnabled(false);
            if (hoursText.isEmpty()) {
                hoursText = "0";
            }
            if (minutesText.isEmpty()) {
                minutesText = "0";
            }
            if (secondsText.isEmpty()) {
                secondsText = "0";
            }
            TimerModel timerModel = new TimerModel(timerTitle, dominoTitle, restTitle, Integer.parseInt(hoursText), Integer.parseInt(minutesText), Integer.parseInt(secondsText), spacingPercentage, Integer.parseInt(numberOfSegment), Integer.parseInt(numberOfRepeats));
            saveRuleToDatabase(timerModel);

        }

    }

    private void saveRuleToDatabase(TimerModel timerModel) {
        DatabaseAccess databaseAccess=DatabaseAccess.getInstance(AddTimerActivity.this);
        databaseAccess.open();
        if (databaseAccess.insertTimeRule(timerModel)) {
            databaseAccess.close();
            Toast.makeText(this, "New Timer Added Successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Toast.makeText(this, "Unable to save timer!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}