package com.demon.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.demon.app.msg.MsgEvent
import com.demon.rxbus.RxBus
import kotlinx.android.synthetic.main.fragment_msg.*

/**
 * @author DeMonnnnnn
 * @date 2019/11/12
 * @email 757454343@qq.com
 * @description
 */
class Msg2Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_msg, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        text.text = "Two Fragment"
        btn.setOnClickListener {
            val msg = MsgEvent("种一棵树最好的时间是十年前，其次是现在。")
            RxBus.getInstance().post(msg)
        }

    }
}