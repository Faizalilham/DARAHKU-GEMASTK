package com.example.donordarah.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.donordarah.R
import com.example.donordarah.databinding.ActivityMapsBinding
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {
    private var _binding : ActivityMapsBinding? = null
    private val binding get() = _binding!!
    private var mapBoxMap : MapboxMap? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationComponent: LocationComponent? = null
    private val geoJsonSourceLayerId = "GeoJsonSourceLayerId"
    private val symbolIconId = "SymbolIconId"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this,getString(R.string.mapbox_access_token))
        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapsActivity)

        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapBoxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS){ style : Style? ->
            enableUserLocation(style)
            addMarker(style)
            initSearchFab()
            setUpSource(style!!)
            setUpLayer(style!!)

            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.pin, null)
            val bitmapUtils = BitmapUtils.getBitmapFromDrawable(drawable)
            style!!.addImage("symbolIconId", bitmapUtils!!)
        }
    }



    private fun addMarker(style: Style?) {
        val geoJsonSource = GeoJsonSource("marker")
        val destination = SymbolLayer("marker","marker")
        destination.withProperties(
            PropertyFactory.iconImage("marker"),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true))
        style?.apply {
            addImage("marker", BitmapFactory.decodeResource(this@MapsActivity.resources,R.drawable.pin))
            addSource(geoJsonSource)
            addLayer(destination)
        }
    }

    private fun setUpLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(SymbolLayer("SYMBOL_LAYER_ID", geoJsonSourceLayerId).withProperties(
            PropertyFactory.iconImage(symbolIconId),
            PropertyFactory.iconOffset(arrayOf(0f, -8f))
        ))
    }

    private fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(geoJsonSourceLayerId))
    }

    private fun initSearchFab() {
        binding.fabSearch.setOnClickListener{
            val intent = PlaceAutocomplete.IntentBuilder()
                .accessToken(
                    (if (Mapbox.getAccessToken() != null) Mapbox.getAccessToken() else getString(R.string.mapbox_access_token))!!
                ).placeOptions(
                    PlaceOptions.builder()
                    .backgroundColor(Color.parseColor("#EEEEEE"))
                    .limit(10)
                    .build(PlaceOptions.MODE_CARDS))
                .build(this)
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            /** Retrieve selected location's CarmenFeature **/
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)

            /** Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
             * Then retrieve and update the source designated for showing a selected location's symbol layer icon **/
            if (mapBoxMap != null) {
                val style = mapBoxMap!!.style
                if (style != null) {
                    val source = style.getSourceAs<GeoJsonSource>(geoJsonSourceLayerId)
                    source?.setGeoJson(FeatureCollection.fromFeatures(arrayOf(Feature.fromJson(selectedCarmenFeature.toJson()))))

                    /** Move map camera to the selected location **/
                    mapBoxMap!!.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(
                                LatLng((selectedCarmenFeature.geometry() as Point?)!!.latitude(),
                                (selectedCarmenFeature.geometry() as Point?)!!.longitude())
                            )
                            .zoom(15.0)
                            .build()), 4000)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableUserLocation(style: Style?) {
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            locationComponent = mapBoxMap!!.locationComponent
            locationComponent?.apply {
                activateLocationComponent(this@MapsActivity,style!!)
                setLocationComponentEnabled(true)
                setCameraMode(CameraMode.TRACKING)
            }
        }else{
            permissionsManager = PermissionsManager(this)
            permissionsManager?.requestLocationPermissions(this)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, "Need Permissions", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted){
            enableUserLocation(mapBoxMap!!.style)
        }else{
            Toast.makeText(this, "Not Granted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    companion object{
        private val REQUEST_CODE_AUTOCOMPLETE = 101
    }



}