package com.vinrosa.mycounterservice;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class MyCounterService extends Service implements Runnable {

    private MyCounterBinder mBinder = new MyCounterBinder();

    public static final String START_COUNTER = "START_COUNTER";
    public static final String STOP_COUNTER = "STOP_COUNTER";

    private Integer counter;
    private boolean running;

    private Handler mHandler;
    private MyCounterListener listener;
    private NotificationManagerCompat mNotificationManager;

    public MyCounterService() {
    }
    // Overrides -- Service
    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.myLooper());
        mNotificationManager = NotificationManagerCompat.from(this);
        counter = 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;

        if (START_COUNTER.equals(intent.getAction())){
            mHandler.post(this);
            running = true;
        } else if (STOP_COUNTER.equals(intent.getAction())){
            mHandler.removeCallbacks(this);
            running = false;
            publishNotification();
        }
        return START_STICKY;
    }

    private void publishNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Example")
                        .setContentText("My counter is " + counter);
        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent
                .getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

    // Overrides -- Runnable
    @Override
    public void run(){
        counter++;
        mHandler.postDelayed(this, 500);
        if (this.listener != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onCurrentValue(counter);
                }
            });
        }
    }
    // ------- Binder
    public class MyCounterBinder extends Binder{
        public MyCounterService getService(){
            return MyCounterService.this;
        }
    }
    // ------- Getters
    public Integer getCounter() {
        return counter;
    }
    public boolean isRunning() {
        return running;
    }

    public void setCounterListener(MyCounterListener listener){
        this.listener = listener;
    }

    public interface MyCounterListener {
        void onCurrentValue(Integer counter);
    }
}
