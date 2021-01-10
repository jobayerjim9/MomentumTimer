package com.momentum.timer.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.momentum.timer.R;
import com.momentum.timer.controller.Utils;
import com.momentum.timer.controller.adapter.TimerRecyclerAdapter;
import com.momentum.timer.controller.dbhelper.DatabaseAccess;
import com.momentum.timer.databinding.ActivityMainBinding;
import com.momentum.timer.models.TimerModel;
import com.momentum.timer.ui.fragment.LoaderDialogFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ArrayList<TimerModel> timerModels=new ArrayList<>();
    private TimerRecyclerAdapter timerRecyclerAdapter;
    private ArrayList<Integer> colors=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init();
    }

    private void init() {
        binding.addTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddTimerActivity.class));
            }
        });
        binding.timerRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        timerRecyclerAdapter=new TimerRecyclerAdapter(MainActivity.this,timerModels,colors);
        binding.timerRecycler.setAdapter(timerRecyclerAdapter);
        getAllTimeRule();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume","MainActivity");
        getAllTimeRule();
    }

    private void getAllTimeRule() {
        DatabaseAccess databaseAccess=DatabaseAccess.getInstance(this);
        databaseAccess.open();
        timerModels.clear();
        timerModels.addAll(databaseAccess.getAllTimerRule());
        colors.addAll(Utils.getColors(timerModels.size()));
        Log.d("colors",colors.size()+"");
        timerRecyclerAdapter.notifyDataSetChanged();
        if (timerModels.size()==0) {
            binding.noItem.setVisibility(View.VISIBLE);
        }
        else {
            binding.noItem.setVisibility(View.GONE);
        }
        databaseAccess.close();
    }
}