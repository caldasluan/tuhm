package br.com.pignata.tuhm.view.view

import android.view.View

fun View.setOnSingleClickListener(l: (View) -> Unit) {
    setOnClickListener(OnSingleClickListener(l))
}