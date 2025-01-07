package com.example.liber.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.liber.R
import com.example.liber.buildyourfirstmap.BitmapHelper
import com.example.liber.buildyourfirstmap.MarkerInfoWindowAdapter
import com.example.liber.buildyourfirstmap.place.Place
import com.example.liber.buildyourfirstmap.place.PlaceRenderer
import com.example.liber.buildyourfirstmap.place.PlacesReader
import com.example.liber.controller.TownTechApi
import com.example.liber.model.Publicacao
import com.example.liber.model.Servico
import com.example.liber.service.RetrofitService
import com.example.liber.view.activity.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.maps.android.clustering.ClusterManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {

    private val places: List<Place> by lazy {
        PlacesReader(requireContext()).read()
    }

    protected var service: TownTechApi = RetrofitService.createService(
        TownTechApi::class.java)

    private val PERMISSION_ID = 42
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private var currentLocation: LatLng? = null
    private var listView: ListView? = null
    private var isLegendExpanded = true
    private var isLocationPicker: Boolean = false
    private var locationPickerCallback: ((Double, Double) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isLocationPicker = arguments?.getBoolean("isLocationPicker", false) ?: false

        /*googleMap.setOnMapClickListener { p0 ->
                println("CLIQUE");
                println(p0.latitude);
                println(p0.longitude);*/

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            // Ensure all places are visible in the map

            googleMap.setOnMapLoadedCallback {
                val bounds = LatLngBounds.builder()
                places.forEach { bounds.include(it.latLng) }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))
            }

            googleMap.setOnMapClickListener { latLng ->
                if (isLocationPicker) {
                    googleMap.clear()
                    
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title("Localização selecionada")
                    )

                    view.findViewById<Button>(R.id.btn_confirm_location)?.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                            locationPickerCallback?.invoke(latLng.latitude, latLng.longitude)
                        }
                    }
                }
            }

            googleMap.setOnMarkerClickListener {
                mMap.uiSettings.isMapToolbarEnabled = true
                false
            }

            addMarkers(googleMap)

            //addClusteredMarkers(googleMap)

            // Set custom info window adapter
            // googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))

            setupLegendToggle(view)

        }

        // Fetching API_KEY which we wrapped
        val ai: ApplicationInfo? = context?.packageManager
            ?.getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
        val value = ai?.metaData?.get("com.google.android.geo.API_KEY")
        val apiKey = value.toString()

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }

        mapFragment?.getMapAsync(this)

        // Initializing fused location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        listView = view.findViewById(R.id.lv_images)

        // Adding functionality to the button
        val btn = view.findViewById<Button>(R.id.currentLoc)
        btn.setOnClickListener {

            //mGetContent.launch("image/*");

            /*getLastLocation()
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation!!, 16F))*/
        }

        println("CARREGOU O MAPA");

        // Adicionar botão de voltar se estiver no modo de seleção

        if (isLocationPicker) {
            val buttonContainer = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(500, 124, 16, 16)
            }

            val confirmButton = Button(context).apply {
                id = R.id.btn_confirm_location
                text = "Confirmar Localização"
                visibility = View.GONE
            }

            val cancelButton = Button(context).apply {
                text = "Voltar"
                setOnClickListener {
                    // Voltar para a tela anterior
                    parentFragmentManager.popBackStack()
                }
            }

            buttonContainer.addView(confirmButton)
            buttonContainer.addView(cancelButton)
            (view as? ViewGroup)?.addView(buttonContainer)
        }
    }

    /*var mGetContent = registerForActivityResult<String, Uri>(
        GetContent()
    ) { uri ->

        try {
            val arrayAdapter: ArrayAdapter<*>

            var imagem = getFileName(uri).toString()

            val imagens = mutableListOf<String>(imagem)

            val f: File = getFile(applicationContext, uri)

            val bm = BitmapFactory.decodeFile(f.path)
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object


            val b = baos.toByteArray()
            baos.close()

            println("ByteArray: "+b.toString());

            val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)

            arrayAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1, imagens
            )
            listView!!.adapter = arrayAdapter

            listView!!.setOnItemClickListener { parent, view, position, id ->
                val element = arrayAdapter.getItem(position) // The item that was clicked
                println("Click Listview: "+element.toString())
                /*val intent = Intent(this, BookDetailActivity::class.java)
                startActivity(intent)*/
            }
        } catch (e: Exception) {
            System.out.println(e.message);
        }
    }*/

    @Throws(IOException::class)
    fun getFile(context: Context, uri: Uri): File {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                createFileFromStream(
                    ins!!,
                    destinationFilename
                )
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
        return destinationFilename
    }

    fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    /*fun imageBase64(filepath: String): String {

        var file : FileInputStream = openFileInput(filepath);

        file.use {
            val bytes = it.readBytes()
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }*/

    /**
     * Adds markers to the map with clustering support.
     */
    private fun addClusteredMarkers(googleMap: GoogleMap) {
        // Create the ClusterManager class and set the custom renderer
        val clusterManager = ClusterManager<Place>(requireContext(), googleMap)
        clusterManager.renderer =
            PlaceRenderer(
                requireContext(),
                googleMap,
                clusterManager
            )

        // Set custom info window adapter
        clusterManager.markerCollection.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext()))

        // Add the places to the ClusterManager
        clusterManager.addItems(places)
        clusterManager.cluster()

        // Show polygon
        clusterManager.setOnClusterItemClickListener { item ->
            addCircle(googleMap, item)
            return@setOnClusterItemClickListener false
        }

        // When the camera starts moving, change the alpha value of the marker to translucent
        googleMap.setOnCameraMoveStartedListener {
            clusterManager.markerCollection.markers.forEach { it.alpha = 0.3f }
            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 0.3f }
        }

        googleMap.setOnCameraIdleListener {
            // When the camera stops moving, change the alpha value back to opaque
            clusterManager.markerCollection.markers.forEach { it.alpha = 1.0f }
            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 1.0f }

            // Call clusterManager.onCameraIdle() when the camera stops moving so that re-clustering
            // can be performed when the camera stops moving
            clusterManager.onCameraIdle()
        }
    }

    private var circle: Circle? = null

    /**
     * Adds a [Circle] around the provided [item]
     */
    private fun addCircle(googleMap: GoogleMap, item: Place) {
        circle?.remove()
        circle = googleMap.addCircle(
            CircleOptions()
                .center(item.latLng)
                .radius(1000.0)
                .fillColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryTranslucent))
                .strokeColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        )
    }

    private val energyIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(requireContext(), R.color.bolt)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.baseline_bolt_24, color)
    }

    private val grassIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(requireContext(), R.color.grass)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.baseline_grass_24, color)
    }

    private val securityIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(requireContext(), R.color.security)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.baseline_dangerous_24, color)
    }

    private val trafficIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(requireContext(), R.color.bolt)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.baseline_traffic_24, color)
    }

    private val hardwareIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(requireContext(), R.color.bolt)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.baseline_hardware_24, color)
    }

    private val bicycleIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(requireContext(), R.color.blue)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.ic_directions_bike_black_24dp, color)
    }

    private val homeIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.ic_home_48px, color)
    }

    private fun buscarCoordenadas(endereco: String): String {
        var textoFinal = ""
        try {
            val geocoder = Geocoder(requireContext(), Locale("pt", "BR"))
            val enderecos = geocoder.getFromLocationName(endereco, 1)


            if (enderecos != null && !enderecos.isEmpty()) {
                val location = enderecos[0]
                val latitude = location.latitude
                val longitude = location.longitude


                // Formatar o endereço completo
                val enderecoCompleto = String.format(
                    "%s, %s - %s, %s",
                    location.thoroughfare,  // Rua
                    location.subThoroughfare,  // Número
                    location.subLocality,  // Bairro
                    location.locality // Cidade
                )


                // Atualizar o campo com o endereço formatado e as coordenadas
                textoFinal = ""+latitude+","+longitude


            } else {
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return textoFinal;
    }

    /**
     * Adds markers to the map. These markers won't be clustered.
     */
    private fun addMarkers(googleMap: GoogleMap) {

        val callAsync = service.publicacoes

        val callAsyncServico = service.servicos

        callAsyncServico.enqueue(object : Callback<List<Servico?>?> {
            override fun onResponse(
                call: Call<List<Servico?>?>,
                response: Response<List<Servico?>?>
            ) {
                if (response.isSuccessful) {
                    val servicos = response.body()

                    callAsync.enqueue(object : Callback<List<Publicacao?>?> {
                        override fun onResponse(
                            call: Call<List<Publicacao?>?>,
                            response: Response<List<Publicacao?>?>
                        ) {
                            val publicacoes = response.body()

                            if (publicacoes != null) {
                                publicacoes.forEach { publicacao ->

                                    if (publicacao != null) {
                                        if(publicacao.id > 4){

                                            //("Iluminação",1), ("Limpeza",1), ("Reforma",1), ("Segurança",1), ("Lazer",1), ("Sinalização",1);

                                            val localizacao = buscarCoordenadas(publicacao.localizacao)
                                            val coordenadas = localizacao.split(",")
                                            val latLng = if (coordenadas.size == 2) {
                                                try {
                                                    LatLng(
                                                        coordenadas[0].trim().toDouble(), // latitude
                                                        coordenadas[1].trim().toDouble()  // longitude
                                                    )
                                                } catch (e: NumberFormatException) {
                                                    println("Erro ao converter coordenadas: ${e.message}")
                                                    null
                                                }
                                            } else {
                                                println("Formato de localização inválido: $localizacao")
                                                null
                                            }

                                            if (latLng != null) {
                                                if(publicacao.idServico == 1){
                                                    googleMap.addMarker(
                                                        MarkerOptions()
                                                            .title("Iluminação")
                                                            .position(latLng)
                                                            .icon(energyIcon)
                                                    )!!
                                                }
                                                else if(publicacao.idServico == 2){
                                                    googleMap.addMarker(
                                                        MarkerOptions()
                                                            .title("Limpeza")
                                                            .position(latLng)
                                                            .icon(grassIcon)
                                                    )!!
                                                }
                                                else if(publicacao.idServico == 3){
                                                    googleMap.addMarker(
                                                        MarkerOptions()
                                                            .title("Reforma")
                                                            .position(latLng)
                                                            .icon(hardwareIcon)
                                                    )!!
                                                }
                                                else if(publicacao.idServico == 4){
                                                    googleMap.addMarker(
                                                        MarkerOptions()
                                                            .title("Segurança")
                                                            .position(latLng)
                                                            .icon(securityIcon)
                                                    )!!
                                                }
                                                else if(publicacao.idServico == 5){
                                                    googleMap.addMarker(
                                                        MarkerOptions()
                                                            .title("Lazer")
                                                            .position(latLng)
                                                            .icon(bicycleIcon)
                                                    )!!
                                                }
                                                else if(publicacao.idServico == 6){
                                                    googleMap.addMarker(
                                                        MarkerOptions()
                                                            .title("Sinalização")
                                                            .position(latLng)
                                                            .icon(trafficIcon)
                                                    )!!
                                                }
                                            }


                                        }
                                    }

                                }
                            }


                        }

                        override fun onFailure(call: Call<List<Publicacao?>?>, t: Throwable) {

                        }
                    })
                } else if (response.errorBody() != null) {
                }
            }

            override fun onFailure(call: Call<List<Servico?>?>, t: Throwable) {
            }
        })

        places.forEach { place ->
            val marker : Marker;

            if(place.name == "Valinhos"){
                marker = googleMap.addMarker(
                    MarkerOptions()
                        .title(place.name)
                        .position(place.latLng)
                        .icon(homeIcon)
                )!!
            }
            else if(place.name == "Área de Risco"){
                marker = googleMap.addMarker(
                    MarkerOptions()
                        .title(place.name)
                        .position(place.latLng)
                        .icon(securityIcon)
                )!!
            }
            else if(place.name == "Limpeza"){
                marker = googleMap.addMarker(
                    MarkerOptions()
                        .title(place.name)
                        .position(place.latLng)
                        .icon(grassIcon)
                )!!
            }
            else if(place.name == "Falta de Energia"){
                marker = googleMap.addMarker(
                    MarkerOptions()
                        .title(place.name)
                        .position(place.latLng)
                        .icon(energyIcon)
                )!!
            }
            else {
                marker = googleMap.addMarker(
                    MarkerOptions()
                        .title(place.name)
                        .position(place.latLng)
                        .icon(bicycleIcon)
                )!!
            }

            // Set place as the tag on the marker object so it can be referenced within
            // MarkerInfoWindowAdapter
            marker?.tag = place
        }
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }






    // Services such as getLastLocation()
    // will only run once map is ready
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        getLastLocation()
    }

    // Get current location
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        //mMap.clear()

                        if(currentLocation != null){
                            mMap.addMarker(MarkerOptions().position(currentLocation!!))
                        }
                        else {
                            Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    // Get current location, if shifted
    // from previous location
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = android.location.LocationRequest.QUALITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    // If current location could not be located, use last location
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
            currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    // function to check if GPS is on
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.FUSED_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.FUSED_PROVIDER
        )
    }

    // Check if location permissions are
    // granted to the application
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Request permissions if not granted before
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    // What must happen when permission is granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
    
    private fun setupLegendToggle(view: View) {
        val toggleButton = view.findViewById<ImageView>(R.id.toggle_legend)
        val legendContent = view.findViewById<LinearLayout>(R.id.legend_content)

        toggleButton.setOnClickListener {
            isLegendExpanded = !isLegendExpanded
            val rotation = if (isLegendExpanded) 0f else 180f
            toggleButton.animate()
                .rotation(rotation)
                .setDuration(300)
                .start()

            if (isLegendExpanded) {
                legendContent.visibility = View.VISIBLE
                legendContent.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            } else {
                legendContent.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        legendContent.visibility = View.GONE
                    }
                    .start()
            }
        }
    }

    fun setLocationPickerCallback(callback: (Double, Double) -> Unit) {
        locationPickerCallback = callback
    }
}