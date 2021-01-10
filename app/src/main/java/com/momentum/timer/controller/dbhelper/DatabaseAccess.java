package com.momentum.timer.controller.dbhelper;



import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.momentum.timer.models.TimerModel;

import java.util.ArrayList;

public class DatabaseAccess {
    DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private static DatabaseAccess instance;
    Cursor cursor=null;
    private DatabaseAccess(Context context) {
        this.databaseHelper=new DatabaseHelper(context);

    }
    public static DatabaseAccess getInstance(Context context) {
        if (instance==null) {
            instance=new DatabaseAccess(context);
        }
        return instance;
    }
    public void open() {
        this.sqLiteDatabase=databaseHelper.getWritableDatabase();
    }
    public void close() {
        if (sqLiteDatabase!=null) {
            this.sqLiteDatabase.close();
        }
    }
    public Boolean insertTimeRule(TimerModel timerModel) {
        try {
            String sql="INSERT INTO time_rule(timerTitle,dominoTitle,restTitle,segmentHours,segmentMinutes,segmentSeconds,spacingPercentage,numberOfSegment,numberOfRepeat) VALUES ("+ "'"+timerModel.getTimerTitle()+"','"+timerModel.getDominoTitle()+"','"+timerModel.getRestTitle()+"',"+timerModel.getSegmentHours()+","+timerModel.getSegmentMinutes()
                    +","+timerModel.getSegmentSeconds()+","+timerModel.getSpacingPercentage()+","+timerModel.getNumberOfSegment()+","+timerModel.getNumberOfRepeat()+")";
            Log.d("sql",sql);
            sqLiteDatabase.execSQL(sql,new String[]{});
            return true;
        } catch (SQLException e) {
            return false;
        }

    }
    public Boolean updateAudioSettings(int id,int settings ) {
        try {
            String sql = "UPDATE time_rule SET audioSettings=" + settings + " WHERE id=" + id;
            sqLiteDatabase.execSQL(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public ArrayList<TimerModel> getAllTimerRule(){
        ArrayList<TimerModel> timerModels = new ArrayList<>();
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM time_rule", new String[]{});
             while (cursor.moveToNext()) {
                 Log.d("tableSize",cursor.getCount()+" "+cursor.getColumnCount()+" "+cursor.getInt(10));
                 TimerModel timerModel = new TimerModel(cursor.getInt(0), cursor.getString(1)
                        , cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5)
                        , cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), cursor.getInt(9),cursor.getInt(10));
                timerModels.add(timerModel);
            }
            return timerModels;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return timerModels;
    }
    public Boolean deleteTimer(int id) {
        try {
            String sql="DELETE FROM time_rule WHERE id="+id;
            sqLiteDatabase.execSQL(sql);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
