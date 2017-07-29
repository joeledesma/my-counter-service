package com.vinrosa.mycounterservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MyCounterService.MyCounterListener {

    private MyCounterService myCounterService;
    private TextView mTextView, mCurrentTextView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MyCounterService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        mTextView = (TextView) findViewById(R.id.my_text_view);
        mCurrentTextView = (TextView) findViewById(R.id.my_current_text_view);
        mButton = (Button) findViewById(R.id.my_button);
        mButton.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        //if logica
        if (myCounterService != null && myCounterService.isRunning()) {
            Intent intent = new Intent(this, MyCounterService.class);
            intent.setAction(MyCounterService.STOP_COUNTER);
            startService(intent);
            mTextView.setText("My counter is : " + myCounterService.getCounter());
            mButton.setText("START");
        } else {
            Intent intent = new Intent(this, MyCounterService.class);
            intent.setAction(MyCounterService.START_COUNTER);
            startService(intent);
            mTextView.setText("My counter is running ");
            mButton.setText("STOP");
        }
    }

    private ServiceConnection mServiceConnection
            = new ServiceConnection() {
        @Override
        public void onServiceConnected
                (ComponentName componentName, IBinder iBinder) {
            if (iBinder instanceof MyCounterService.MyCounterBinder){
                 myCounterService =
                         ((MyCounterService.MyCounterBinder) iBinder)
                                 .getService();
                myCounterService.setCounterListener(MainActivity.this);
                // Init Current Status as it may be running
                if (myCounterService.isRunning()) {
                    mTextView.setText("My counter is running ");
                    mButton.setText("STOP");
                } else {
                    mTextView.setText("My counter is : " + myCounterService.getCounter());
                    mButton.setText("START");
                }
            }
            Log.d("MainActivity", "Service Connected!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("MainActivity", "Service Disconnected!");
        }
    };


    @Override
    public void onCurrentValue(Integer counter) {
        mCurrentTextView.setText("Integer: " + counter);
    }
}
