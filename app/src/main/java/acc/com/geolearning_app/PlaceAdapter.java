package acc.com.geolearning_app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.osmdroid.views.MapView;

import java.util.ArrayList;

import acc.com.geolearning_app.db.SqliteHelper;
import acc.com.geolearning_app.dto.Nodo;
import acc.com.geolearning_app.dto.Place;
import acc.com.geolearning_app.dto.Tag;
import acc.com.geolearning_app.dto.Zone;
import acc.com.geolearning_app.util.utils;

public class PlaceAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Place> items;
    private SqliteHelper sqliteHelper;
    private MapView mapView;
    private final Zone zona;

    public PlaceAdapter(Context context, ArrayList<Place> items,MapView map, Zone zona) {
        this.context = context;
        this.items = items;
        this.mapView = map;
        this.zona = zona;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        sqliteHelper = new SqliteHelper(context);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adaptor_place_list, parent, false);
        }

        // se cogen los ids en los layouts
        final ImageView imagen = (ImageView) convertView.findViewById(R.id.icono_lugar);
        final TextView label = (TextView) convertView.findViewById(R.id.clase_label);
        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.delete_place);
        ImageButton addTagsButton = (ImageButton) convertView.findViewById(R.id.add_tags);
        ImageButton infoTagsButton = (ImageButton) convertView.findViewById(R.id.show_tags); //TODO
        ImageButton editCoor = (ImageButton) convertView.findViewById(R.id.edit_coor);


        //se rellenan los campos
        final Place item = this.items.get(position);
        ArrayList<Nodo> nodos = sqliteHelper.getAllNodos(item.getId());

        if (item.getPlace_type().equals("piscina")) {
            imagen.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.stepladder));
        }
        if (item.getPlace_type().equals("rotonda")){
            imagen.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.roundabout));
        }

        if (item.getPlace_type().equals("parking")){
            imagen.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.parking));
        }

        label.setText(item.getPlace_type() + " con id: " + item.getId());


        deleteButton.setTag(position);
        deleteButton.setOnClickListener(new View.OnClickListener()   {
            @Override
            public void onClick(View v)  {
                sqliteHelper.deleteLugar(item.getId());
                items.remove(position);
                notifyDataSetChanged();

                mapView.getOverlayManager().clear();
                mapView.getOverlays().clear();
                utils.drawPolygons(mapView,items);

                Toast.makeText(context,"Place removed", Toast.LENGTH_SHORT).show();
            }
        });

        //para añadir nuevos tags
        addTagsButton.setTag(position);
        addTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Add tag");

                //A Dialog only contains one root View, that's why setView() overwrites the first EditText.
                // The solution is simple put everything in one ViewGroup, for instance a LinearLayout:
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                // KEY
                final EditText key_input = new EditText(context);
                key_input.setHint("Key");
                key_input.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(key_input);

                // VALUE
                final EditText value_input = new EditText(context);
                value_input.setHint("Value");
                value_input.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(value_input);

                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String key_Tag  = key_input.getText().toString();
                        String value_Tag  = value_input.getText().toString();

                        Tag tag = new Tag();
                        tag.setValue(value_Tag);
                        tag.setKey(key_Tag);
                        tag.setId_place(item);

                        long id_tag = sqliteHelper.addTag(tag);

                        Toast.makeText(context,"Tag added with id: " + id_tag, Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();



            }
        });

        //TODO Opcion de editar las coordenadas

        editCoor.setTag(position);
        editCoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                Window w = dialog.getWindow();//
                w.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.edit_coor_place_map);


                com.google.android.gms.maps.MapView mMapView = (com.google.android.gms.maps.MapView) dialog.findViewById(R.id.mapView_coor);
                MapsInitializer.initialize(context);

                mMapView = (com.google.android.gms.maps.MapView) dialog.findViewById(R.id.mapView_coor);
                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();// needed to get the map to display immediately
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    //DIALOGO de mapa que muestra el lugar para poder EDITARse
                    public void onMapReady(final GoogleMap googleMap) {

                        //se buscan los nodos
                        ArrayList<Nodo> nodos = sqliteHelper.getAllNodos(item.getId());
                        final Nodo nodox = nodos.get(0);
                        final Nodo nodow = nodos.get(1);
                        final Nodo nodoh = nodos.get(2);
                        final Nodo nodox2 = nodos.get(3);

                        //se crea y ajusta el mapa y se fija la camara
                        LatLng posisiabsen = new LatLng(nodox.getLat(),nodox.getLon()); ////your lat lng
                        //googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("Editar coordenadas"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
                        googleMap.setMapType(googleMap.MAP_TYPE_SATELLITE);

                        //dibujar el lugar
                        final Marker mx= googleMap.addMarker(new MarkerOptions().position(new LatLng(nodox.getLat(),nodox.getLon())).draggable(true));
                        final Marker mw= googleMap.addMarker(new MarkerOptions().position(new LatLng(nodow.getLat(),nodow.getLon())).draggable(true));
                        final Marker mh= googleMap.addMarker(new MarkerOptions().position(new LatLng(nodoh.getLat(),nodoh.getLon())).draggable(true));
                        final Marker mx2= googleMap.addMarker(new MarkerOptions().position(new LatLng(nodox2.getLat(),nodox2.getLon())).draggable(true));
                        mx.setTitle("x");mw.setTitle("w");mh.setTitle("h");mx2.setTitle("x2");

                        //resimensiaonamos la imagen
                        int height = 50;
                        int width = 50;
                        BitmapDrawable bitmapdraw=(BitmapDrawable)context.getResources().getDrawable(R.drawable.pegatina_circulo_verde);
                        Bitmap b=bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                        //asignamos el icono a cada marcador
                        mx.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        mw.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        mh.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        mx2.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                        final Polygon polygon = utils.drawPolygon(mx2,mh,mx,mw,googleMap);//se dibuja un poligono

                        //en caso de arrastrar algun marcador
                        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                            Polygon polygonUpdate = polygon;
                            Nodo nodoxUpdate = nodox;
                            Nodo nodowUpdate = nodow;
                            Nodo nodohUpdate = nodoh;
                            Nodo nodox2Update = nodox2;


                            @Override
                            public void onMarkerDragStart(Marker marker) {

                            }

                            @Override
                            //actualiza la forma del poligono segun se desplaza el marcador
                            public void onMarkerDrag(Marker marker) {
                                if (marker.getTitle().equals("x")){
                                    polygonUpdate.remove();
                                    polygonUpdate = utils.drawPolygon(mx2,mh,marker,mw,googleMap);
                                    nodoxUpdate.setLat(marker.getPosition().latitude);
                                    nodoxUpdate.setLon(marker.getPosition().longitude);
                                }
                                if (marker.getTitle().equals("w")){
                                    polygonUpdate.remove();
                                    polygonUpdate = utils.drawPolygon(mx2,mh,mx,marker,googleMap);
                                    nodowUpdate.setLat(marker.getPosition().latitude);
                                    nodowUpdate.setLon(marker.getPosition().longitude);}
                                if (marker.getTitle().equals("h")){
                                    polygonUpdate.remove();
                                    polygonUpdate = utils.drawPolygon(mx2,marker,mx,mw,googleMap);
                                    nodohUpdate.setLat(marker.getPosition().latitude);
                                    nodohUpdate.setLon(marker.getPosition().longitude);}
                                if (marker.getTitle().equals("x2")){
                                    polygonUpdate.remove();
                                    polygonUpdate = utils.drawPolygon(marker,mh,mx,mw,googleMap);
                                    nodox2Update.setLat(marker.getPosition().latitude);
                                    nodox2Update.setLon(marker.getPosition().longitude);}
                            }

                            @Override
                            public void onMarkerDragEnd(Marker marker) {
                                //persistir en base de datos
                                if (marker.getTitle().equals("x")){
                                    sqliteHelper.updateNodo(nodoxUpdate);
                                }
                                if (marker.getTitle().equals("w")){
                                    sqliteHelper.updateNodo(nodowUpdate);
                                }
                                if (marker.getTitle().equals("h")){
                                    sqliteHelper.updateNodo(nodohUpdate);
                                }
                                if (marker.getTitle().equals("x2")){
                                    sqliteHelper.updateNodo(nodox2Update);
                                }
                            }
                        });

                        //si se selecciona guardar cambios
                        FloatingActionButton saveChanges = (FloatingActionButton)dialog.findViewById(R.id.edit_place_ok);
                        saveChanges.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //TODO
                                //actualizar mapa de lugares
                                mapView.getOverlays().clear();
                                ArrayList<Place> lugaresNuevos = sqliteHelper.getAllElements(zona.getId());
                                for (Place lugar: lugaresNuevos){
                                    ArrayList<Nodo> nodos = sqliteHelper.getAllNodos(lugar.getId());
                                    lugar.setNodos(nodos);
                                }
                                utils.drawPolygons(mapView,lugaresNuevos);
                                //cerrar dialogo
                                dialog.dismiss();
                            }
                        });
                    }
                });

                dialog.show();
            }


        });

        return convertView;
    }

}
