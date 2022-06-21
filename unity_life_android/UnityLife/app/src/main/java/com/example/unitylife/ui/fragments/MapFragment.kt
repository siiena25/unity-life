package com.example.unitylife.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.unitylife.App
import com.example.unitylife.R
import com.example.unitylife.databinding.FragmentMapsBinding
import com.example.unitylife.ext.injectViewModel
import com.example.unitylife.view_models.LoginViewModel
import com.github.reline.GoogleMapsBottomSheetBehavior
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.llc.aceplace_ru.di.ViewModelsFactory
import kotlinx.android.synthetic.main.bottom_sheet_autorization.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.IOnBackPressed
import utils.SharedPreferencesStorage
import javax.inject.Inject
import kotlin.math.abs


private const val ZOOM_DEFAULT = 15f
private const val LIST_STATE_KEY = "keyPromoList"

@DelicateCoroutinesApi
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener, IOnBackPressed {
    private var binding: FragmentMapsBinding? = null

    private var bottomSheetBehaviorAuthorization: BottomSheetBehavior<*>? = null

    private lateinit var authorizationViewModel: LoginViewModel

    //private lateinit var clusterManager: ClusterManager<MarkerModel>

    @Inject
    lateinit var factory: ViewModelsFactory
    lateinit var storage: SharedPreferencesStorage

    private var gMap: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private var behavior: GoogleMapsBottomSheetBehavior<*>? = null
    private var sensorManager: SensorManager? = null
    private var userMarker: Marker? = null
    private var userLocation: LatLng? = null
    private var lastLocation: Location? = null
    //private var currentPins: List<PinWithBusiness>? = null
    private var oldPos = -1
    private var queryTag: String? = null
    private var categoryIds = mutableListOf<String>()
    private var businessId: String? = null
    private var isClickOnPush = false
    private var promoListState: Parcelable? = null
    private var bundlePromoListState: Bundle? = null
    private var markersBundle: Bundle? = null
    //private var lastMarkerModel: MarkerModel? = null
    //private var markers: MutableList<MarkerModel> = mutableListOf()
    private var isEmojiSet = false
    private val offset = 0

    init {
        lifecycleScope.launchWhenResumed {
            //nearPromoViewModel.getState().collect { onNearPromoStateChanged(it) }
        }
        lifecycleScope.launchWhenResumed {
            //viewModel.getState().collect { onStateChanged(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage =
            (requireActivity().application as App).getAppComponent().getSharedPreferencesStorage()
        (requireActivity().application as App).getAppComponent().getAuthorizationViewModel().inject(this)
        authorizationViewModel = injectViewModel(factory)
        storage.putLastSearchQuery(null)

        if (storage.getLastQuery().isNullOrEmpty() && !storage.getIsOnMarkerPromoClick()) {
            storage.putSelectCategoriesOnMap(mutableListOf())
        }
    }

    private fun observeLocation() {
        val vm = (requireActivity().application as App).getAppComponent().getGlobalViewModel()
        vm.getLocationData().observe(viewLifecycleOwner) {
            //nearPromoViewModel.requestPromoFeed()
        }
    }

    private fun checkProfileStorage() {
        if (storage.getProfileId() == null) {
            //nearPromoViewModel.requestSync()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        storage.putLastTransitionFromMap(null)
        storage.putSelectCategoriesOnMap(null)
        storage.putLastSearchQuery(null)
        storage.putIsOnMarkerPromoClick(false)
        storage.putLastClickedMarkerId(null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        binding?.mapsMapView?.onCreate(savedInstanceState)
        binding?.mapsMapView?.getMapAsync(this)
        return binding?.root
    }

    override fun onStart() {
        super.onStart()
        binding?.mapsMapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding?.mapsMapView?.onResume()
        //viewModel.requestNearPartners()
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(sensorListener)
        cameraPosition = gMap?.cameraPosition

        //storage.putLastClickedMarkerId(lastMarkerModel?.title)

        binding?.mapsMapView?.apply {
            onPause()
            if (markersBundle != null) {
                onSaveInstanceState(markersBundle!!)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        binding?.mapsMapView?.onSaveInstanceState(state)
    }

    private fun init() {
        setupClickListeners()
        //viewModel.requestPopularTags()
        //nearPromoViewModel.requestPromoFeed()
        observeLocation()
        checkProfileStorage()
    }

    private fun setupClickListeners() {
        binding?.apply {
            mapsBtnLocation.setOnClickListener {
                userLocation?.let { location ->
                    gMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(location, ZOOM_DEFAULT)
                    )
                }
            }

            bottomSheetBehaviorAuthorization = BottomSheetBehavior.from(bottomSheetAutorization)

            bottomSheetBehaviorAuthorization?.apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
                isFitToContents = true
            }

            setupBottomSheetAutorizationClickListeners()
        }
    }

    private fun setupBottomSheetAutorizationClickListeners() {
        bottomSheetBehaviorAuthorization?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(view: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        hideLocationButton()
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> showLocationButton()
                }
            }

            override fun onSlide(view: View, p1: Float) {}
        })
    }

    private fun showLocationButton() {
        if (binding?.mapsBtnLocation?.visibility != View.VISIBLE) {
            val animationFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            animationFadeIn.setAnimationListener(object :
                Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    binding?.mapsBtnLocation?.visibility = View.VISIBLE
                    if (bottomSheetBehaviorAuthorization?.state != BottomSheetBehavior.STATE_HIDDEN) {
                        hideLocationButton()
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            binding?.mapsBtnLocation?.startAnimation(animationFadeIn)
        }
    }

    private fun hideLocationButton() {
        if (binding?.mapsBtnLocation?.visibility == View.VISIBLE) {
            val animationFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            animationFadeOut.setAnimationListener(object :
                Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    binding?.mapsBtnLocation?.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            binding?.mapsBtnLocation?.startAnimation(animationFadeOut)
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(map: GoogleMap) {
        this.gMap = map
        /*if (context != null) {
            clusterManager = ClusterManager(context, gMap)
            clusterManager.renderer =
                MapClusterRenderer(context, gMap, clusterManager, layoutInflater)
            clusterManager.setOnClusterItemClickListener {
                    item -> onMarkerClick(item)
            }
            clusterManager.setOnClusterClickListener { cluster ->
                val markerIds = cluster.items.map { markerModel -> markerModel.title }
                val action =
                    MapFragmentDirections.actionMapsFragmentToClusterListFragment(markerIds.toTypedArray())
                if (findNavController().currentDestination?.id == R.id.mapsFragment) {
                    findNavController().navigate(action)
                    storage.putLastTransitionFromMap(getString(R.string.cluster_list_fragment))
                }
                true
            }
            gMap?.setMapStyle(MapStyleOptions(getString(R.string.map_style)))

            GoogleMapOptions().mapToolbarEnabled(true)
            cameraPosition?.let { position ->
                gMap?.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        position
                    )
                )
            }
            behavior?.anchorMap(gMap)
            gMap?.setOnMarkerClickListener(clusterManager)
            gMap?.setOnMapClickListener(this)
            observeData()
            map.setOnCameraIdleListener {
                val position = map.cameraPosition.target
                val lat = position.latitude
                val lng = position.longitude
                if (!storage.getLastTransitionFromMap()
                        .isNullOrEmpty() || storage.getIsOnMarkerPromoClick()
                ) {
                    viewModel.requestTargetPartners(lat, lng)
                }
                clusterManager.cluster()
            }
            if (model != null
                && bottomSheetBehaviorSearch?.state == BottomSheetBehavior.STATE_HIDDEN
                && storage.getIsOnMarkerPromoClick()
            ) {
                behavior?.apply {
                    state = GoogleMapsBottomSheetBehavior.STATE_EXPANDED
                    isHideable = false
                    skipCollapsed = true
                }
            }
            val vm = (requireActivity().application as App).getAppComponent().getGlobalViewModel()
            vm.getLocationData().observe(viewLifecycleOwner) { newLocation ->
                updateLocationUI(newLocation)
            }
        }*/
    }

    /*private fun onMarkerClick(markerModel: MarkerModel?): Boolean {
        if (markerModel?.title == null) {
            return false
        }
        businessId = markerModel.title
        businessId?.let {
            val action =
                MapFragmentDirections.actionMapsFragmentToBusinessDetailFragment(businessId.toString())
            if (findNavController().currentDestination?.id == R.id.mapsFragment) {
                findNavController().navigate(action)
                storage.putLastTransitionFromMap(getString(R.string.cluster_list_fragment))
            }
        }
        return true
    }*/

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val rotationMatrix = FloatArray(16)
                    SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, event!!.values
                    )

                    val remappedRotationMatrix = FloatArray(16)
                    SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedRotationMatrix
                    )
                    val orientations = FloatArray(3)
                    SensorManager.getOrientation(remappedRotationMatrix, orientations)

                    for (i in 0..2) {
                        orientations[i] = Math.toDegrees(orientations[i].toDouble()).toFloat()
                    }

                    val bearing = orientations[2]
                    withContext(Dispatchers.Main) {
                        userMarker?.let { marker ->
                            if (abs(marker.rotation - bearing) >= 1) {
                                marker.rotation = bearing
                            }
                        }
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    private fun updateLocationUI(newLocation: Location) {
        lastLocation = newLocation
        val latLng = LatLng(newLocation.latitude, newLocation.longitude)
        //set the user position only first time
        if (userMarker == null) {
            gMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }

        userLocation = latLng
        userMarker?.remove()
        userMarker = gMap?.addMarker(
            MarkerOptions()
                .title("userMarker")
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_position))
        )
        startRotationTracking()
    }

    //private var model: BusinessModel? = null

    /*private fun observeData() {
        lifecycleScope.launch {
            viewModel.getState().collect { onStateChanged(it) }
        }
        viewModel.getData().observe(viewLifecycleOwner) { model ->
            this.model = model
            if (model.isUserRate) {
                businessId?.let { businessDetailViewModel.requestUserRate(it) }
            }
            businessId?.let { businessDetailViewModel.requestStockRates(offset.toString(), it) }
        }
    }*/

    override fun onMapClick(latLng: LatLng) {
        closePromoBottomSheet()
    }

    private fun closePromoBottomSheet() {
        storage.putIsOnMarkerPromoClick(false)
        bottomSheetAutorization?.visibility = View.VISIBLE
        behavior?.isHideable = true
        behavior?.state = GoogleMapsBottomSheetBehavior.STATE_HIDDEN
        behavior?.skipCollapsed = true
    }

    private fun startRotationTracking() {
        if (sensorManager != null) {
            return
        }
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        sensorManager?.let { sensor ->
            sensor.registerListener(
                sensorListener,
                sensor.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                1000
            )
        }
    }

    override fun onMarkerClick(p: Marker): Boolean {
        return true
    }

    override fun onBackPressed(): Boolean {
        return true
    }
}