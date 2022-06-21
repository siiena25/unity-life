package com.example.unitylife.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgument
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.unitylife.App
import com.example.unitylife.R
import com.example.unitylife.databinding.ContainerMainBinding
import com.example.unitylife.ext.hide
import com.example.unitylife.ext.show
import com.example.unitylife.network.services.AuthService
import com.example.unitylife.ui.fragments.MapFragment
import com.example.unitylife.utils.InsetUtils
import com.example.unitylife.view_models.GlobalViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import utils.IOnBackPressed
import utils.SharedPreferencesStorage
import javax.inject.Inject


private const val MIN_TIME_MS = 10_000L
private const val MIN_DISTANCE_M = 10f

class ContainerMainActivity : AppCompatActivity() {
    private var binding: ContainerMainBinding? = null

    private lateinit var viewModel: GlobalViewModel

    @Inject
    lateinit var storage: SharedPreferencesStorage

    @Inject
    lateinit var tokenRefresher: TokenRefresher

    private var locationManager: LocationManager? = null

    private var launcher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onPermissionResult(isGranted)
        }

    private lateinit var mQrResultLauncher: ActivityResultLauncher<Intent>

    private var navHostFragment: Fragment? = null

    private fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            startLocationTracking()
        }
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onStart() {
        super.onStart()
        if (isLocationPermissionGranted()) {
            startLocationTracking()
        } else {
            launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContainerMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding?.root)
        initComponents()
        init()
        storage.putLastClickedMarkerId(null)
    }

    override fun onResume() {
        super.onResume()
        callTokenRefresh()
    }

    private fun callTokenRefresh() {
        lifecycleScope.launch(Dispatchers.IO) { tokenRefresher.callRefresh() }
    }

    override fun onPause() {
        super.onPause()
        locationManager?.removeUpdates(locationListener)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationTracking() {
        locationManager?.let { manager ->
            val locationProvider: String = when {
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                    LocationManager.NETWORK_PROVIDER
                }
                manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                    LocationManager.GPS_PROVIDER
                }
                else -> {
                    return
                }
            }

            manager.requestLocationUpdates(
                locationProvider,
                MIN_TIME_MS,
                MIN_DISTANCE_M,
                locationListener,
            )
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun initComponents() {
        (application as App).getAppComponent().inject(this)
        viewModel = (application as App).getAppComponent().getGlobalViewModel()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latLng = LatLng(
                location.latitude,
                location.longitude
            )
            storage.putLatitude(latLng.latitude)
            storage.putLongitude(latLng.longitude)
            viewModel.updateLocation(location)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }
    }

    private fun init() {
        binding?.containerMainBottomNavigation?.apply {
            background = null
        }
        navHostFragment = supportFragmentManager.findFragmentById(R.id.containerMain_root)
        navHostFragment?.let { host ->
            binding?.containerMainBottomNavigation?.setupWithNavController(host.findNavController())
            host.childFragmentManager.addFragmentOnAttachListener { _, fragment ->
                if (fragment !is MapFragment) {
                    returnSystemInsets()
                }
            }
            host.findNavController().addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.markerCreationFragment,
                    -> onHideNavigator()
                    else -> onShowNavigator(destination)
                }
            }
        }
    }

    /*private fun authorizationAction() {
        val newIntent = Intent(this, ContainerLoginActivity::class.java)
        startActivity(newIntent)
    }*/

    private fun returnSystemInsets() {
        InsetUtils.returnSystemInsets(binding?.root!!)
    }

    fun onHideNavigator() {
        binding?.containerMainBottomNavigation?.let { navigation ->
            navigation.hide()
            binding?.bottomAppBar?.hide()
        }
    }

    fun onShowNavigator(destination: NavDestination?) {
        if (destination?.id == R.id.mapsFragment) {
            val argument = NavArgument.Builder().setDefaultValue("").build()
            destination.addArgument("businessId", argument)
        }
        binding?.containerMainBottomNavigation?.let { navigation ->
            if (navigation.visibility == View.VISIBLE) {
                return
            }
            navigation.show()
            //binding?.qrcodeBtn?.visibility = View.VISIBLE
            binding?.bottomAppBar?.show()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finishActivity(Activity.RESULT_CANCELED)
        } else {
            if ((navHostFragment?.childFragmentManager?.fragments?.get(0) as? IOnBackPressed)?.onBackPressed() != false) {
                super.onBackPressed()
            }
        }
    }

    class TokenRefresher @Inject constructor(
        private val storage: SharedPreferencesStorage,
        private val authService: AuthService
    ) {
        suspend fun callRefresh() {
            val authResponse = authService.auth().body()
            authResponse?.token?.let { token ->
                storage.putAuthToken(token)
            }
        }
    }

    companion object {
        val bottomNavigationScreenIds = listOf(
            R.id.mapsFragment,
            R.id.profileFragment
        )
    }
}