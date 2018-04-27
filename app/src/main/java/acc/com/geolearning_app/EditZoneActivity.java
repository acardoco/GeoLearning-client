package acc.com.geolearning_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import acc.com.geolearning_app.db.SqliteHelper;
import acc.com.geolearning_app.dto.Place;
import acc.com.geolearning_app.dto.Zone;
import acc.com.geolearning_app.util.utils;

public class EditZoneActivity extends AppCompatActivity {

    SqliteHelper sqliteHelper;

    MapView map = null;

    Zone zone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_zone);

        sqliteHelper = new SqliteHelper(this);


        //guardar retoques
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.imageButton_edit);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        zone = sqliteHelper.getZona(getIntent().getStringExtra("id_zone"));
        //rellenar ListView
        ArrayList<Place> lugares = sqliteHelper.getAllElements(zone.getId());
        PlaceAdapter adapter = new PlaceAdapter(this,lugares);
        ListView lv= (ListView) findViewById(R.id.list_edit_view);
        lv.setAdapter(adapter);

        //---------------OSM------------------------------
        //-------------------------------------------------
        //-------------------------------------------------
        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        map = (MapView) findViewById(R.id.map_osm_edit);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);


        IMapController mapController = map.getController();
        mapController.setZoom(19);
        GeoPoint startPoint = new GeoPoint(zone.getLat(),zone.getLon());
        mapController.setCenter(startPoint);
        //-------------------------------------------------
        //-------------------------------------------------
        //-------------------------------------------------

    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        Intent intent = new Intent(EditZoneActivity.this, ItemDetailActivity.class);
        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, zone.getId());
        startActivity(intent);
        finish();
    }


}
