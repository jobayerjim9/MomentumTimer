package com.momentum.timer.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.momentum.timer.R;
import com.momentum.timer.controller.SoundService;
import com.momentum.timer.databinding.ActivityTimerBinding;
import com.momentum.timer.models.Constants;
import com.momentum.timer.models.TimerModel;

import java.util.HashMap;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {
    private TimerModel timerModel;
    TextToSpeech textToSpeech;
    ActivityTimerBinding binding;
    long wholeDuration,wholeDurationProgress;
    HashMap<String,Integer> timeChain=new HashMap<>();
    private boolean changingSeek=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_timer);
        init();
    }

    private void init() {
        timerModel=(TimerModel) getIntent().getSerializableExtra("timerModel");
        Log.d("timerModel",timerModel.toString());
        getSupportActionBar().setTitle(timerModel.getTimerTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        timerModel.calculateAllSegments();
        timerModel.getCurrentSegmentTime();
        binding.setData(timerModel);
        binding.currentHourText.setText(binding.getData().getSegmentHours()+"H");
        binding.currentMinuteText.setText(binding.getData().getSegmentMinutes()+"M");
        binding.currentSecondText.setText(binding.getData().getSegmentSeconds()+"S");
        wholeDuration=timerModel.getWholeDuration();
        Log.d("wholeDuration",wholeDuration+"");

        binding.wholeTimerSeekbar.setMax((int) wholeDuration);
        textToSpeech=new TextToSpeech(TimerActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status!=TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
        binding.timePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.getData().isTimeRunning())
                {
                        binding.timePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                        binding.getData().setTimeRunning(false);
                        binding.getData().setPaused(true);

                        if (segmentTimer!=null) {
                            segmentTimer.cancel();
                        }
                        if (restTimer!=null) {
                            restTimer.cancel();
                        }


                }
                else {
                    if (binding.getData().isPaused()) {
                        long remain=binding.getData().getCurrentHour()*Constants.HOUR_CONSTANT+binding.getData().getCurrentMinute()*Constants.MINUTE_CONSTANT+binding.getData().getCurrentSecond();
                        resumeTimer(remain);
                    }
                    else {
                        startDominoTime();
                    }

                }
            }
        });
        binding.segmentTimerSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    changingSeek=true;
                    binding.getData().setTimeRunning(false);
                    binding.getData().setPaused(true);
                    try {
                        //binding.timePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24))
                        segmentTimer.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        //binding.timePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24))
                        restTimer.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    long remain=0;
                    if (binding.getData().isDomino() && binding.getData().getCurrentDominoSegment()<binding.getData().getNumberOfSegment()) {
                      remain= binding.getData().getAllSegments().get(binding.getData().getCurrentDominoSegment())-progress;
                    } else if (binding.getData().getCurrentRestSegment()<binding.getData().getNumberOfSegment()) {
                        remain=binding.getData().getRestSegments().get(binding.getData().getCurrentRestSegment())-progress;
                    }
                    binding.segmentTimerSlider.setMax((int)(remain+progress));
                    long timerProgress=0;
                    int size=binding.getData().getCurrentDominoSegment()+binding.getData().getCurrentRestSegment();
                    for (int i=0 ;i<size;i++) {
                        timerProgress=timerProgress+binding.getData().getTimeChain().get(i);
                    }
                    timerProgress=timerProgress+progress;
                    binding.wholeTimerSeekbar.setProgress((int) timerProgress);
                    resumeTimer(remain);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                    changingSeek=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                changingSeek=false;
            }
        });

        binding.resetTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.getData().setPaused(false);
                binding.getData().setCurrentRepeat(0);
                binding.getData().setCurrentRestSegment(0);
                binding.getData().setCurrentDominoSegment(0);
                binding.getData().setActiveTime("domino");
                binding.getData().setTimeRunning(false);
                binding.resetTimerButton.setVisibility(View.GONE);
                binding.timePlayPause.setEnabled(true);
                binding.segmentTimerSlider.setProgress(0);
                binding.wholeTimerSeekbar.setProgress(0);
                binding.noOfRepeat.setText("Repeat No. "+(binding.getData().getCurrentRepeat()+1));
                binding.currentHourText.setText(binding.getData().getSegmentHours() + "H");
                binding.currentMinuteText.setText(binding.getData().getSegmentMinutes() + "M");
                binding.currentSecondText.setText(binding.getData().getSegmentSeconds() + "S");
                binding.getData().setCurrentHour(binding.getData().getSegmentHours());
                binding.getData().setCurrentMinute(binding.getData().getSegmentMinutes());
                binding.getData().setCurrentSecond(binding.getData().getSegmentSeconds());

                Toast.makeText(TimerActivity.this, "Time reset to default rule!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.wholeTimerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
//                    if (binding.getData().getTimeChain().size()>=10 ) {
//                        binding.wholeTimerSeekbar.setMax(binding.getData().getTimeChain().size()-1);
//                        Log.d("progress",progress+"");
//                        if (segmentTimer!=null) {
//                            segmentTimer.cancel();
//                        }
//                        if (restTimer!=null) {
//                            restTimer.cancel();
//                        }
//                        if (progress%2==0) {
//                            binding.getData().setActiveTime(Constants.DOMINO);
//                            binding.getData().setCurrentDominoSegment((int)(progress/2.0));
//                            binding.getData().setCurrentRestSegment((int)(progress/2.0));
//                            binding.segmentName.setText(timerModel.getDominoTitle());
//                            startDominoTime();
//                        }
//                        else {
//                            binding.segmentName.setText(timerModel.getRestTitle());
//                            binding.getData().setActiveTime("rest");
//                            binding.getData().setCurrentDominoSegment((int)(progress/2.0));
//                            binding.getData().setCurrentRestSegment((int)(progress/2.0));
//                            startRestTime();
//                        }
//                        binding.getData().setPaused(false);
//
//                    }
//                    else  {

                        wholeDurationProgress=wholeDuration-progress;
                        long temp=0;
                        timerModel=binding.getData();
                        int index=0;
                        for (int i=0;i<timerModel.getTimeChain().size();i++) {
                            temp=temp+timerModel.getTimeChain().get(i);
                            if (temp>=progress) {
                                index=i;
                                break;
                            }
                        }
                        int currentRemain=0;
                        int currentSegment=(int) Math.floor(index/2.0);
                        binding.getData().setCurrentDominoSegment(currentSegment);
                        binding.getData().setCurrentRestSegment(currentSegment);
                        for (int i=index+1;i<timerModel.getTimeChain().size();i++) {
                            Log.d("remainTimerCalculate",timerModel.getTimeChain().get(i)+" "+wholeDurationProgress);
                            wholeDurationProgress=wholeDurationProgress-timerModel.getTimeChain().get(i);
                        }
                        if (index%2==0) {
                            binding.segmentName.setText(timerModel.getDominoTitle());
                            binding.getData().setActiveTime(Constants.DOMINO);

                        } else {
                            binding.getData().setCurrentDominoSegment(currentSegment+1);
                            binding.segmentName.setText(timerModel.getRestTitle());
                            binding.getData().setActiveTime("rest");
                        }
                        if (segmentTimer!=null) {
                            segmentTimer.cancel();
                        }
                        if (restTimer!=null) {
                            restTimer.cancel();
                        }
                        binding.segmentTimerSlider.setMax((int) wholeDurationProgress+progress);
                        resumeTimer(wholeDurationProgress);
                        Log.d("timeChain:",temp+" remainDuration:"+wholeDurationProgress+" index:"+index+" progress:"+progress+" currentSegment:"+currentSegment);

                    }
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private void updateSegmentTimer(long millisUntilFinished) {
        int maxSegment= Math.round(binding.getData().getAllSegments().get(binding.getData().getCurrentDominoSegment()));
        binding.segmentTimerSlider.setMax(maxSegment);
        binding.getData().setCurrentHour(millisUntilFinished / 3600000);
        binding.getData().setCurrentMinute((millisUntilFinished / 60000) - (binding.getData().getCurrentHour() * 60));
        binding.getData().setCurrentSecond((millisUntilFinished / 1000) - (binding.getData().getCurrentMinute() * 60) - (binding.getData().getCurrentHour() * Constants.HOUR_CONSTANT));
        binding.currentHourText.setText(binding.getData().getCurrentHour() + "H");
        binding.currentMinuteText.setText(binding.getData().getCurrentMinute() + "M");
        binding.currentSecondText.setText(binding.getData().getCurrentSecond() + "S");
        int sec=(int) millisUntilFinished/1000;
        Log.d("segmentSec",sec+"");
        if (sec==3 && timerModel.getAudioSetting()==2)
        {
            startService(new Intent(TimerActivity.this, SoundService.class));
        }

        binding.getData().setTimeRunning(true);
        Log.d("changingSeek",changingSeek+"");
        if (!changingSeek) {
            binding.segmentTimerSlider.setProgress((int) (binding.getData().getAllSegments().get(binding.getData().getCurrentDominoSegment()) - (millisUntilFinished / 1000)));
        }
        if (binding.wholeTimerSeekbar.getProgress()<wholeDuration) {
            binding.wholeTimerSeekbar.setProgress(binding.wholeTimerSeekbar.getProgress()+1);
        }
    }
    private void updateRestTimer(long millisUntilFinished) {
        int maxSegment= Math.round(binding.getData().getRestSegments().get(binding.getData().getCurrentRestSegment()));
        binding.segmentTimerSlider.setMax(maxSegment);
        binding.getData().setCurrentHour( millisUntilFinished / 3600000);
        binding.getData().setCurrentMinute((millisUntilFinished / 60000) - (binding.getData().getCurrentHour() * 60));
        binding.getData().setCurrentSecond((millisUntilFinished / 1000) - (binding.getData().getCurrentMinute() * 60) - (binding.getData().getCurrentHour() * Constants.HOUR_CONSTANT));
        binding.currentHourText.setText(binding.getData().getCurrentHour() + "H");
        binding.currentMinuteText.setText(binding.getData().getCurrentMinute() + "M");
        binding.currentSecondText.setText(binding.getData().getCurrentSecond() + "S");
        binding.getData().setTimeRunning(true);
        int sec=(int) millisUntilFinished/1000;
        Log.d("restSec",sec+"");
        if (sec==3 && timerModel.getAudioSetting()==2)
        {
            startService(new Intent(TimerActivity.this, SoundService.class));
        }
        Log.d("changingSeek",changingSeek+"");
        if (!changingSeek) {
            binding.segmentTimerSlider.setProgress((int) (binding.getData().getRestSegments().get(binding.getData().getCurrentRestSegment()) - (millisUntilFinished / 1000)));
        }
        if (binding.wholeTimerSeekbar.getProgress()<wholeDuration) {
            binding.wholeTimerSeekbar.setProgress(binding.wholeTimerSeekbar.getProgress()+1);
        }
    }
    private void cancelSegment() {
        binding.getData().setCurrentHour(0);
        binding.getData().setCurrentMinute(0);
        binding.getData().setCurrentSecond(0);
        binding.currentHourText.setText(binding.getData().getCurrentHour() + "H");
        binding.currentMinuteText.setText(binding.getData().getCurrentMinute() + "M");
        binding.currentSecondText.setText(binding.getData().getCurrentSecond() + "S");
        binding.getData().setTimeRunning(false);
    }
    private void resumeTimer(long remain) {

        binding.getData().setTimeRunning(true);
         if (binding.getData().isDomino()) {
             if (timerModel.getAudioSetting()==1) {
                 textToSpeech.speak(timerModel.getDominoTitle(), TextToSpeech.QUEUE_FLUSH, null);
             }
            segmentTimer=new CountDownTimer(remain*1000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (binding.getData().getCurrentDominoSegment()<=binding.getData().getNumberOfSegment()) {
                        updateSegmentTimer(millisUntilFinished);
                    }
                    else {
                        cancel();
                        cancelSegment();
                    }
                }

                @Override
                public void onFinish() {

                    binding.getData().increaseDomino();
                    if (binding.getData().getCurrentRestSegment()<binding.getData().getNumberOfSegment()) {
                        binding.getData().setActiveTime("rest");
                        startRestTime();
                    }
                }
            }.start();
        }
        else {
             if (timerModel.getAudioSetting()==1) {
                 textToSpeech.speak(timerModel.getRestTitle(), TextToSpeech.QUEUE_FLUSH, null);
             }
            restTimer=new CountDownTimer(remain*1000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (binding.getData().getCurrentRestSegment()<binding.getData().getNumberOfSegment()) {
                        updateRestTimer(millisUntilFinished);
                    }
                    else {
                        cancel();
                        cancelSegment();
                    }
                }

                @Override
                public void onFinish() {

                    restFinish();
                }
            }.start();
        }
        binding.timePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
    }

    CountDownTimer segmentTimer,restTimer;
    private void startDominoTime() {
        binding.noOfRepeat.setText("Repeat No. "+(binding.getData().getCurrentRepeat()+1));
        Log.d("dominoTime domino:",binding.getData().getCurrentDominoSegment()+" rest:"+binding.getData().getCurrentRestSegment()+" repeat:"+binding.getData().getCurrentRepeat()+" segment:"+binding.getData().getNumberOfSegment()+ " segmentEq:"+binding.getData().getAllSegments().size()+" restEq:"+binding.getData().getRestSegments().size());
        int segmentMax= (int) Math.floor(binding.getData().getAllSegments().get(binding.getData().getCurrentDominoSegment()));
        if (timerModel.getAudioSetting()==1) {

            textToSpeech.speak(timerModel.getDominoTitle(), TextToSpeech.QUEUE_FLUSH, null);
        }
        binding.segmentTimerSlider.setMax(segmentMax);
        binding.timePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
        binding.segmentName.setText(binding.getData().getDominoTitle());
        segmentTimer=new CountDownTimer(binding.getData().getAllSegments().get(binding.getData().getCurrentDominoSegment())*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (binding.getData().getCurrentDominoSegment()<binding.getData().getNumberOfSegment()) {
                        updateSegmentTimer(millisUntilFinished);
                }
                else {
                    cancel();
                    cancelSegment();
                }
            }

            @Override
            public void onFinish() {
                binding.getData().increaseDomino();
                if (binding.getData().getCurrentRestSegment()<binding.getData().getNumberOfSegment()) {
                    binding.getData().setActiveTime("rest");
                    startRestTime();
                }

            }
        }.start();

    }

    private void startRestTime() {
        if (timerModel.getAudioSetting()==1) {
            textToSpeech.speak(timerModel.getRestTitle(), TextToSpeech.QUEUE_FLUSH, null);
        }
        Log.d("restTime domino:",binding.getData().getCurrentDominoSegment()+" rest:"+binding.getData().getCurrentRestSegment()+" repeat:"+binding.getData().getCurrentRepeat());
        binding.segmentName.setText(binding.getData().getRestTitle());
        binding.timePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
        int segmentMax= (int) Math.floor(binding.getData().getRestSegments().get(binding.getData().getCurrentRestSegment()));
        binding.segmentTimerSlider.setMax(segmentMax);
        Log.d("restSegment",binding.getData().getRestSegments().get(binding.getData().getCurrentRestSegment())+"");
        restTimer=new CountDownTimer(binding.getData().getRestSegments().get(binding.getData().getCurrentRestSegment())*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (binding.getData().getCurrentRestSegment()<binding.getData().getNumberOfSegment()) {
                    updateRestTimer(millisUntilFinished);
                }
                else {
                    cancel();
                    cancelSegment();
                }


            }

            @Override
            public void onFinish() {

                restFinish();

            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timer_details_menu, menu);
        return true;
    }

    private void restFinish() {
        binding.getData().increaseRest();
        Log.d("restFinish",binding.getData().getCurrentDominoSegment()+" "+binding.getData().getCurrentRestSegment()+" "+binding.getData().getCurrentRepeat());
        if (binding.getData().getCurrentDominoSegment()<binding.getData().getNumberOfSegment()) {
            binding.getData().setActiveTime(Constants.DOMINO);
            startDominoTime();
        }
        else {
            binding.getData().increaseRepeat();
            if (binding.getData().getCurrentRepeat()<binding.getData().getNumberOfRepeat()) {
                binding.noOfRepeat.setText("Repeat No. "+(binding.getData().getCurrentRepeat()+1));
                binding.getData().setCurrentDominoSegment(0);
                binding.getData().setCurrentRestSegment(0);
                binding.getData().setCurrentRestSegment(0);
                binding.getData().setActiveTime(Constants.DOMINO);
                binding.getData().setPaused(false);
                binding.wholeTimerSeekbar.setProgress(0);
                startDominoTime();
            }
            else {
                binding.timePlayPause.setEnabled(false);
                binding.resetTimerButton.setVisibility(View.VISIBLE);
                binding.timePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                binding.getData().setTimeRunning(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.timerSettings) {
            Intent intent=new Intent(TimerActivity.this,TimerSettingsActivity.class);
            intent.putExtra("data",timerModel);
            startActivity(intent);
        }
        finish();
        return super.onOptionsItemSelected(item);
    }
}