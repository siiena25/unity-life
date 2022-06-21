package com.example.unitylife.ext

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.example.unitylife.R
import com.example.unitylife.utils.InsetUtils
import com.example.unitylife.utils.OnSystemInsetsChangedListener

fun AppCompatActivity.navigateTo(fragment: Fragment, tag: String) {
    if(this.supportFragmentManager.fragments.size > 0){
        this.supportFragmentManager.beginTransaction().hide(this.supportFragmentManager.fragments[this.supportFragmentManager.fragments.size - 1])
        this.supportFragmentManager.beginTransaction()
            .add(R.id.containerMain_root, fragment, tag)
            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(tag)
            .commit()
    }else {
        this.supportFragmentManager.beginTransaction()
            .replace(R.id.containerMain_root, fragment, tag)
            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(tag)
            .commit()
    }
}

fun AppCompatActivity.showToast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showSnackbar(
    root: View,
    text: String,
    actionText: String? = null,
    onClickListener: View.OnClickListener? = null
) {
    Snackbar.make(root, text, 5000)
        .setAction(actionText, onClickListener)
        .setActionTextColor(root.context.getColor(R.color.colorPrimary))
        .setTextColor(root.context.getColor(R.color.colorBlack))
        .setBackgroundTint(root.context.getColor(R.color.colorWhite))
        .show()
}

fun Activity.setWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtils.removeSystemInsets(window.decorView, listener)
    window.navigationBarColor = Color.BLACK
    window.statusBarColor = Color.TRANSPARENT
}