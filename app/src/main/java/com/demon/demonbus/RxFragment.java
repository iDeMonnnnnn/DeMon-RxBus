package com.demon.demonbus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.demon.rxbus.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * @author DeMon
 * @date 2018/9/7
 * @description
 */
public class RxFragment extends Fragment {
    private static final String TAG = "RxFragment";
    @BindView(R.id.text)
    TextView text;
    Unbinder unbinder;
    private View view;

    @SuppressLint("CheckResult")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rx, container, false);
        unbinder = ButterKnife.bind(this, view);
        RxBus.getInstance().toObservable(this, MsgEvent.class).subscribe(new Consumer<MsgEvent>() {
            @Override
            public void accept(MsgEvent msgEvent) throws Exception {
                //处理事件
                String s = msgEvent.getMsg();
                text.setText(msgEvent.getMsg());
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
