package com.demon.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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

        btn.setOnClickListener {
            finish()
        }

        RxBus.getInstance().toObservableSticky(this, StickyMsg::class.java).subscribe { msg ->
            tvMsg.text = msg.msg
        }
    }


    override fun onStop() {
        super.onStop()
        Log.i("SecondActivity", "----onStop-----")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("SecondActivity", "----onDestroy-----")
    }
}
