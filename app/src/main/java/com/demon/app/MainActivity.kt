package com.demon.app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import com.demon.app.msg.MsgEvent
import com.demon.app.msg.StickyMsg
import com.demon.rxbus.RxBus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RxBus.getInstance().toObservable(this, MsgEvent::class.java).subscribe { msg ->
            tvMsg.text = msg.msg
        }

        btn.setOnClickListener {
            val msg = StickyMsg("不如意事常八九，可与人说无二三。")
            RxBus.getInstance().postSticky(msg)
            startActivity(Intent(this, SecondActivity::class.java))
        }

        val title = arrayListOf("one", "two")
        val lists = listOf(Msg1Fragment(), Msg2Fragment())
        val mAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return lists[position]
            }

            override fun getCount(): Int = lists.size

            override fun getPageTitle(position: Int): CharSequence? {
                return title[position]
            }
        }
        viewPager.adapter = mAdapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = lists.size
    }


    override fun onPause() {
        super.onPause()
        Log.i("MainActivity", "----onPause-----")
    }

    override fun onStop() {
        super.onStop()
        Log.i("MainActivity", "----onStop-----")
    }
}
