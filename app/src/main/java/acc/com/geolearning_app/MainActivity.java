package acc.com.geolearning_app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import acc.com.geolearning_app.db.SqliteHelper;
import acc.com.geolearning_app.dto.Zone;
import acc.com.geolearning_app.util.Server_Status;
import acc.com.geolearning_app.util.utils;
import acc.com.geolearning_app.web.ServerController;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {


    /**********************************************/
    /**********************************************/
    /***************Parametros mapa****************/
    /**********************************************/
    /**********************************************/
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private double marker_lat = 33.8523341;
    private double marker_lon = 151.2106085;
    /**********************************************/
    /**********************************************/
    /**********************************************/

    // insertar en BD
    SqliteHelper sqliteHelper;

    ArrayList<Zone> zonas = new ArrayList<Zone>();

    //controlar peticiones al servidor
    Server_Status server_status = Server_Status.getInstance();

    //-----------------------
    //DIALOGO de las opciones
    //-----------------------
    // Build an AlertDialog
    AlertDialog.Builder builder;

    // String array for alert dialog multi choice items
    String[] opciones = new String[]{"Satellite view", "See zones"};
    // Boolean array for initial selected items
    final boolean[] checkedOpciones = new boolean[]{false, false};
    final List<String> colorsList = Arrays.asList(opciones);

    //-----------------------
    //-----------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //opciones de navigation drawer
        super.onCreate(savedInstanceState);
        sqliteHelper = new SqliteHelper(this);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //GOOGLE MAPS ++
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        //GOOGLE MAPS --

        zonas = sqliteHelper.getAllElements();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mappingActualPosition(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        builder = new AlertDialog.Builder(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //OPCIONES DE mostrar vista satelital Y mostrar zonas mapeadas en el mapa
        if (id == R.id.action_settings) {

            setDialogoOpciones();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.actual_map) {
            //MAPEA la zona actual
            mappingActualPosition(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

        } else if (id == R.id.add_map_query) {
            //AÑADE una localización en la cola de pendientes
            addLocationToQuery();

            return true;

        } else if (id==R.id.map_query){
            //MAPEA zonas pendientes
            mapQuery();

            return true;

        }else if (id == R.id.show_maps) {
            //Hacia Lista de zonas/mapas
            Intent intent=new Intent(MainActivity.this,ItemListActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //-----------------------------------------------------
    //-----------------------------------------------------
    //-----------------------------------------------------
    //METODOS GOOGLE MAPS
    //-----------------------------------------------------
    //-----------------------------------------------------
    //-----------------------------------------------------

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        //añadir nueva localizacion a mano
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                mMap.clear();

                mLastKnownLocation.setLatitude(point.latitude);
                mLastKnownLocation.setLongitude(point.longitude);

                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(point.latitude, point.longitude)).title("Lat: "
                        + String.valueOf(point.latitude)
                        + "\nLon: "
                        + String.valueOf(point.longitude));


                mMap.addMarker(marker);
            }
        });

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map2), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });


        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    //mapea la posicion actual llamando al controlador, que llame al servidor
    public void mappingActualPosition(double lat, double lon){

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        if (!server_status.isBUSY_SERVER()) {

            Snackbar.make(drawerLayout, R.string.server_working,
                    Snackbar.LENGTH_INDEFINITE)
                    .show();

            server_status.setBUSY_SERVER(true);

            //Mapear zona actual

            //le paso el drawerLayout para mostrar un snackbar cuando acaba el proceso en el servidor
            ServerController serverController = ServerController.getInstance(this, this.sqliteHelper, drawerLayout);
            serverController.getCandidates(lat, lon);

        } else {

            Snackbar.make(drawerLayout, R.string.server_busy,
                    Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    //obtiene las coordenadas de las esquinas de una zona y las printea en el mapa (para ZOOM=18) y 640x640
    public void obtenerPoligono(){

        zonas = sqliteHelper.getAllElements();

        Double desplazamientoAA = 0.0019688; //0.0026700 en 640
        Double desplazamientoIZDE = 0.0021938; //0.0035100 en 640

        Double aa = desplazamientoAA/2; //lat
        Double izde = desplazamientoIZDE/2; //lon

        for (Zone lugar: zonas){

            Double lat = lugar.getLat();
            Double lon = lugar.getLon();

            /*
            y           h


            x           w
             */

            Double latx = lat - aa;
            Double lonx = lon - izde;

            Double laty = lat + aa;
            Double lony = lon - izde;

            Double latw = lat - aa;
            Double lonw = lon + izde;

            Double lath = lat + aa;
            Double lonh = lon + izde;

            PolygonOptions rectOptions = new PolygonOptions()
                    .add(
                            new LatLng(latw, lonw),
                            new LatLng(latx, lonx),
                            new LatLng(laty, lony),
                            new LatLng(lath, lonh)
                            ).strokeColor(R.color.colorPrimary).fillColor(R.color.colorAccent);

            Polygon polygon = mMap.addPolygon(rectOptions);


        }
    }

    //opciones de mostrar zonas y vista satelital
    public void setDialogoOpciones(){

        builder.setMultiChoiceItems(opciones, checkedOpciones, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                // Update the current focused item's checked status
                checkedOpciones[which] = isChecked;

                // Get the current focused item
                String currentItem = colorsList.get(which);

                // Notify the current action
                Toast.makeText(getApplicationContext(),
                        currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();
            }
        });

        // Specify the dialog is not cancelable
        builder.setCancelable(false);

        // Set a title for alert dialog
        builder.setTitle("Map options");

        // Set the positive/yes button click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               if (checkedOpciones[0] == true)
                   mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
                if (checkedOpciones[0] == false)
                    mMap.setMapType(mMap.MAP_TYPE_NORMAL);


               if (checkedOpciones[1] == true)
                   obtenerPoligono();
                if (checkedOpciones[1] == false)
                    mMap.clear();
            }
        });

        // Set the negative/no button click listener
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click the negative button
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }

    //añade una localización sin mapear(sin lugares mapeados) a la query
    public void addLocationToQuery(){
        Zone zone = new Zone();
        zone.setLat(mLastKnownLocation.getLatitude());
        zone.setLon(mLastKnownLocation.getLongitude());
        long id_zone = sqliteHelper.addZone(zone,false);
        zone.setMapped(false);
        zone.setId(String.valueOf(id_zone));

        zonas.add(zone);

        Toast.makeText(this,"Added zone with lat: "
                        + utils.truncateDecimal(zone.getLat(),4)
                        + " and lon : " + utils.truncateDecimal(zone.getLon(),4)
                , Toast.LENGTH_SHORT);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        Snackbar.make(drawer, "Added zone with lat: "
                        + utils.truncateDecimal(zone.getLat(),4)
                        + " and lon : " + utils.truncateDecimal(zone.getLon(),4),
                Snackbar.LENGTH_SHORT)
                .show();
    }

    //mapea zonas pendientes en cola
    public void mapQuery(){

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ArrayList<Zone> lugares_no_mapeados = new ArrayList<Zone>();
        for (Zone zone: zonas){
            if (!zone.getMapped())
                lugares_no_mapeados.add(zone);
        }

        if (lugares_no_mapeados.size()> 0 ) {

            Snackbar.make(drawer, R.string.server_working,
                    Snackbar.LENGTH_INDEFINITE)
                    .show();

            server_status.setBUSY_SERVER(true);

            ServerController serverController = ServerController.getInstance(this, this.sqliteHelper, drawer);
            serverController.getListCandidates(lugares_no_mapeados);

            for (Zone zone : zonas) {
                zone.setMapped(true);
            }
        }else{

            Snackbar.make(drawer, "Empty query.",
                    Snackbar.LENGTH_SHORT)
                    .show();

        }

        drawer.closeDrawer(GravityCompat.START);

    }


}
