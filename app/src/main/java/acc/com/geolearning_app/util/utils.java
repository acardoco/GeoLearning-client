package acc.com.geolearning_app.util;

import android.graphics.Color;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import acc.com.geolearning_app.dto.Place;
import acc.com.geolearning_app.dto.Zone;

public class utils {

    static double lat_AA = 0.0016688; //[+|-] en latitud para moverse arriba o abajo ¡
    static double lon_DI = 0.0021938; //[+|-] en longitud para moverse derecha o izquierda

    public static BigDecimal truncateDecimal(double x, int numberofDecimals)
    {
        if ( x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
        }
    }


    //CCNSTRUYE una url en formato String
    public static String getStringUrl(double lat, double lon){

        String urlbase = "https://maps.googleapis.com/maps/api/staticmap?center=";
        String urlcomun = "&zoom=18&format=jpg&size=400x400&maptype=satellite&key=";
        String urlkey= "AIzaSyB9qW-QzzGtT2xEsJlsuLgA5TOYNJS8ogo";

        StringBuilder url_total = new StringBuilder();
        url_total.append(urlbase)
                .append(lat)
                .append(",")
                .append(lon)
                .append(urlcomun)
                .append(urlkey);

        return url_total.toString();

    }

    //pixels coordinates to lon coordinates in X -->
    public static double getLon(double lon_central, int x){

        double lon_final;
        int central = x;


        lon_final = (lon_DI * central ) / 400;

        lon_final = (lon_central - lon_DI/2) + lon_final;

        return  lon_final;

    }

    //pixels coordinates to Lat coordinates in Y -->
    public static double getLat(double lat_central, int y){

        double lat_final;
        int central = 400 - y;


        lat_final = (lat_AA * central ) / 400;

        lat_final = (lat_central - lat_AA/2) + lat_final;

        return  lat_final;

    }


    public static void drawPolygons(MapView map, ArrayList<Place> lugares, Zone zone){

        for (final Place lugar: lugares) {

            List<GeoPoint> geoPoints = new ArrayList<>();
            GeoPoint x = new GeoPoint(utils.getLat(zone.getLat(), lugar.getY()), utils.getLon(zone.getLon(), lugar.getX()));
            GeoPoint w = new GeoPoint(utils.getLat(zone.getLat(), lugar.getY()), utils.getLon(zone.getLon(), lugar.getX() + lugar.getW()));
            GeoPoint y = new GeoPoint(utils.getLat(zone.getLat(), lugar.getY()), utils.getLon(zone.getLon(), lugar.getX()));
            GeoPoint h = new GeoPoint(utils.getLat(zone.getLat(), lugar.getY() + lugar.getH()), utils.getLon(zone.getLon(), lugar.getX()));

            GeoPoint x2 = new GeoPoint(utils.getLat(zone.getLat(), lugar.getY() + lugar.getH()), utils.getLon(zone.getLon(), lugar.getX() + lugar.getW()));

            //add your points here;
            geoPoints.add(x);
            geoPoints.add(w);
            geoPoints.add(y);
            geoPoints.add(h);
            geoPoints.add(x2);
            Polygon polygon = new Polygon();    //see note below
            polygon.setFillColor(Color.TRANSPARENT);
            geoPoints.add(geoPoints.get(1));    //forces the loop to close
            polygon.setPoints(geoPoints);
            polygon.setTitle("\n A sample polygon");


            int lat_medio = (lugar.getY() + lugar.getH() / 2);
            int lon_medio = (lugar.getX() + lugar.getW() / 2);

            Marker startMarker = new Marker(map);
            startMarker.setPosition(new GeoPoint(utils.getLat(zone.getLat(), lat_medio), utils.getLon(zone.getLon(), lon_medio)));
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.setTitle("ID: " + lugar.getId() + "\n" + "Class: " + lugar.getPlace_type());

            startMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    Toast.makeText(mapView.getContext(),"ID: " + lugar.getId(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });


            map.getOverlayManager().add(polygon);
            map.getOverlays().add(startMarker);
        }

    }

}
