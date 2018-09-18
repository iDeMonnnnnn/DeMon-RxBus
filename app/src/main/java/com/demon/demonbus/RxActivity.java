package com.demon.demonbus;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.demon.rxbus.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RxActivity extends AppCompatActivity {
    private static final String TAG = "RxActivity";
    @BindView(R.id.java)
    Button java;
    @BindView(R.id.android)
    Button android;
    @BindView(R.id.rx_layout)
    FrameLayout rxLayout;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.rx_layout, new RxFragment());
        transaction.commit();
    }

    @OnClick({R.id.java, R.id.android})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.java:
                RxBus.getInstance().post(new MsgEvent("Java"));
                break;
            case R.id.android:
                RxBus.getInstance().post(new MsgEvent("Android"));
                break;
        }
    }
}
