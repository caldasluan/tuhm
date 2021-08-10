package br.com.pignata.tuhm.view.view

import android.view.View

class OnSingleClickListener(listener: (View) -> Unit) : View.OnClickListener {
    private val onClickListener: View.OnClickListener = View.OnClickListener { listener.invoke(it) }
    private var previousClickTimeMillis = 0L

    override fun onClick(v: View) {
        val currentTimeMillis = System.currentTimeMillis()

        if (currentTimeMillis >= previousClickTimeMillis + DELAY_MILLIS) {
            previousClickTimeMillis = currentTimeMillis
            onClickListener.onClick(v)
        }
    }

    companion object {
        private const val DELAY_MILLIS = 1000L
    }
}