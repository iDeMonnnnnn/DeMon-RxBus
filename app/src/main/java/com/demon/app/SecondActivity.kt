package com.demon.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.demon.app.msg.StickyMsg
import com.demon.rxbus.RxBus
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)


        RxBus.getInstance().toObservableSticky(this, StickyMsg::class.java, Lifecycle.Event.ON_STOP).subscribe { msg ->
            tvMsg.text = msg.msg
        }
    }
}
