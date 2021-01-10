package com.momentum.timer.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.momentum.timer.R;
import com.momentum.timer.databinding.ItemTimerBinding;
import com.momentum.timer.models.TimerModel;
import com.momentum.timer.ui.activity.TimerActivity;

import java.util.ArrayList;

public class TimerRecyclerAdapter extends RecyclerView.Adapter<TimerRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<TimerModel> timerModels;
    private ArrayList<Integer> colors;

    public TimerRecyclerAdapter(Context context, ArrayList<TimerModel> timerModels, ArrayList<Integer> colors) {
        this.context = context;
        this.timerModels = timerModels;
        this.colors = colors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_timer,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setData(timerModels.get(position));
        holder.binding.setCardColor(colors.get(position));
        holder.binding.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, TimerActivity.class);
                intent.putExtra("timerModel",timerModels.get(position));
                context.startActivity(intent);
            }
        });
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return timerModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemTimerBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
    }
}
