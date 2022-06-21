package com.example.unitylife.ext

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.example.unitylife.R
import com.example.unitylife.network.models.LocationModel
import com.example.unitylife.utils.InsetUtils
import com.example.unitylife.utils.OnSystemInsetsChangedListener
import java.util.*

fun Fragment.navigateTo(fragment: Fragment) {
    this.parentFragmentManager.beginTransaction()
        .replace(R.id.containerMain_root, fragment, fragment::class.java.name)
        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .addToBackStack(null)
        .commit()
}

fun Fragment.hideKeyboard() {
    val inputMethodManager =
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager?.let {
        val view = view
        it.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}

fun Fragment.launchForwardDialog(type: Types, id: String) {
    val template = "https://api.aceplace.ru/links/${type.name.toLowerCase(Locale.ROOT)}/$id"
    val newIntent = Intent()

    newIntent.action = Intent.ACTION_SEND
    newIntent.putExtra(Intent.EXTRA_TEXT, type.name.toLowerCase(Locale.ROOT))
    newIntent.putExtra(Intent.EXTRA_TEXT, template)
    newIntent.type = "text/plain"
    startActivity(Intent.createChooser(newIntent, ""))
}

fun Fragment.launchMap(location: LocationModel) {
    location.coordinates?.let { coords ->
        val lat = coords.getOrNull(1)
        val lng = coords.getOrNull(0)
        if (lat != null && lng != null) {
            val latLng = LatLng(lat.toDouble(), lng.toDouble())
            val template = "geo:${latLng.latitude},${latLng.longitude}"
            val newIntent = Intent(Intent.ACTION_VIEW, Uri.parse(template))
            try {
                startActivity(newIntent)
            } catch (e: Exception) {

            }
        }
    }
}

fun Fragment.showToast(text: String) {
    if (isVisible) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.showToast(resId: Int) {
    if (isVisible) {
        Toast.makeText(requireContext(), getString(resId), Toast.LENGTH_SHORT).show()
    }
}

@Keep
fun showSnackbar(
    root: View,
    text: String,
    actionText: String? = null,
    onClickListener: View.OnClickListener? = null,
    duration: Int = 1000
): Snackbar {
    val snackbar = Snackbar.make(root, text, duration)
    snackbar.setAction(actionText, onClickListener)
    snackbar.show()
    return snackbar

}

fun Fragment.setWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtils.removeSystemInsets(requireActivity().window.decorView, listener)
    requireActivity().window.navigationBarColor = Color.TRANSPARENT
    requireActivity().window.statusBarColor = Color.TRANSPARENT
}

enum class Types {
    BUSINESS,
    NEWS,
    STOCK
}