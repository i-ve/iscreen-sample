package kr.ive.i_screen_sdk_sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.logging.Logger;

import kr.ive.iscreen.Const;
import kr.ive.iscreen.IScreen;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    public static final String TEXT_FORMAT = "매체 포인트 = %1$d";

    private BroadcastReceiver mAppPointChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int addingAppPoint = IScreen.getAddingAppPoint(context, intent);
            Log.e(TAG, "addingAppPoint = " + addingAppPoint);
            mAppPoint += addingAppPoint;

            updateAppPoint();
        }
    };

    private TextView mTextView;
    private int mAppPoint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.media_point_text_view);

        View button = findViewById(R.id.iscreen_button);
        button.setOnClickListener(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mAppPointChangeReceiver, new IntentFilter("kr.ive.iscreen.action.app_point_change_complete"));

        updateAppPoint();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAppPointChangeReceiver);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.iscreen_button) {
            IScreen.run(this);
        }
    }

    private void updateAppPoint() {
        mTextView.setText(String.format(TEXT_FORMAT, mAppPoint));
    }
}
