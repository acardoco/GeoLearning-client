package acc.com.geolearning_app.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import acc.com.geolearning_app.R;
import acc.com.geolearning_app.dto.Nodo;
import acc.com.geolearning_app.dto.Place;
import acc.com.geolearning_app.dto.Tag;
import acc.com.geolearning_app.dto.Zone;

public class utils {

    static double lat_AA = 0.0016688; //[+|-] en latitud para moverse arriba o abajo ¡
    static double lon_DI = 0.0021938; //[+|-] en longitud para moverse derecha o izquierda
    static int contador_osm = -1000;

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

    //pixels coordinates to lon coordinates in X --> para circulos
    public static double getLon(double lon_central, double x){

        double lon_final;
        double central = x;


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

    //pixels coordinates to Lat coordinates in Y --> para circulos
    public static double getLat(double lat_central, double y){

        double lat_final;
        double central = 400 - y;


        lat_final = (lat_AA * central ) / 400;

        lat_final = (lat_central - lat_AA/2) + lat_final;

        return  lat_final;

    }

    //dibujar un poligono
    public static  com.google.android.gms.maps.model.Polygon drawPolygon(com.google.android.gms.maps.model.Marker nodox2, com.google.android.gms.maps.model.Marker nodoh, com.google.android.gms.maps.model.Marker nodox, com.google.android.gms.maps.model.Marker nodow, GoogleMap googleMap){
         /*
        y-x           h-w


        x-h           w-x2
                         */
        PolygonOptions rectOptions = new PolygonOptions()
                .add(
                        nodox2.getPosition(),
                        nodoh.getPosition(),
                        nodox.getPosition(),
                        nodow.getPosition()
                ).strokeColor(R.color.colorPrimary).fillColor(0x00000000).strokeWidth(10);

        return googleMap.addPolygon(rectOptions);

    }


    public static void drawPolygons(MapView map, ArrayList<Place> lugares){

        for (final Place lugar: lugares) {

            List<GeoPoint> geoPoints = new ArrayList<>();
            ArrayList<Nodo> nodos = lugar.getNodos();
            //TODO rotondas
            if (lugar.getPlace_type().equals("rotonda")){

                for (Nodo nodo: nodos){
                    GeoPoint puntitoNodo = new GeoPoint(nodo.getLat(),nodo.getLon());
                    geoPoints.add(puntitoNodo);
                }

            }else {
                GeoPoint x = new GeoPoint(nodos.get(0).getLat(), nodos.get(0).getLon());
                GeoPoint w = new GeoPoint(nodos.get(1).getLat(), nodos.get(1).getLon());
                GeoPoint h = new GeoPoint(nodos.get(2).getLat(), nodos.get(2).getLon());
                GeoPoint x2 = new GeoPoint(nodos.get(3).getLat(), nodos.get(3).getLon());
                //add your points here;
                geoPoints.add(x);
                geoPoints.add(w);
                geoPoints.add(x2);
                geoPoints.add(h);
                geoPoints.add(x); //cierre de poligono
            }

                Polygon polygon = new Polygon();    //see note below
                polygon.setFillColor(Color.TRANSPARENT);
                geoPoints.add(geoPoints.get(0));    //forces the loop to close
                polygon.setPoints(geoPoints);
                polygon.setTitle("\n Figura:" + lugar.getPlace_type());


                /*int lat_medio = (lugar.getY() + lugar.getH() / 2);
                int lon_medio = (lugar.getX() + lugar.getW() / 2);*/

                Marker startMarker = new Marker(map);
                startMarker.setPosition(new GeoPoint(nodos.get(0).getLat(), nodos.get(0).getLon()));
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

    //ROTONDAS
    public static Nodo getNodoCirculo(int posicion, ArrayList<Nodo> nodos){
        for (Nodo nodo: nodos){
            if (nodo.getType()==posicion){
                return nodo;
            }
        }

        return new Nodo();
    }

    //dibujar circunferencia en GoogleMaps
    public static com.google.android.gms.maps.model.Polygon createCirculo(ArrayList<com.google.android.gms.maps.model.Marker> marcadores, GoogleMap googleMap){

        PolygonOptions rectOptions = new PolygonOptions().strokeColor(R.color.colorPrimary).fillColor(0x00000000).strokeWidth(10);
        for (com.google.android.gms.maps.model.Marker marker: marcadores){
            rectOptions.add(marker.getPosition());
        }

        return googleMap.addPolygon(rectOptions);
    }

    //ACTUALIZAR circunferencia
    public static com.google.android.gms.maps.model.Polygon updateCirculo(com.google.android.gms.maps.model.Marker marker, int pos, ArrayList<com.google.android.gms.maps.model.Marker> marcadores, GoogleMap googleMap){

        PolygonOptions rectOptions = new PolygonOptions().strokeColor(R.color.colorPrimary).fillColor(0x00000000).strokeWidth(10);
        for (com.google.android.gms.maps.model.Marker marcador: marcadores){
            if (marcador.getTitle().equals(String.valueOf(pos))){
                marcador.setPosition(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude));
            }
            rectOptions.add(marcador.getPosition());
        }

        return googleMap.addPolygon(rectOptions);
    }

    public static void writeXMLToOSM(ArrayList<Place> lugares, Zone zone){

        try{


            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("osm");
            doc.appendChild(rootElement);

            // set attribute to staff element
            Attr attr1 = doc.createAttribute("version");
            attr1.setValue("0.6");
            rootElement.setAttributeNode(attr1);

            Attr attr2 = doc.createAttribute("generator");
            attr2.setValue("GeoLearning Server");
            rootElement.setAttributeNode(attr2);

            for (Place lugar: lugares){

                //creamos 4 nodos para el way/lugar
                /*

                x(inicial)------------------------->w


                h<------------------------x2

                 */
                double latx = utils.getLat(zone.getLat(), lugar.getY());
                double lonx = utils.getLon(zone.getLon(), lugar.getX());

                double latw = utils.getLat(zone.getLat(), lugar.getY());
                double lonw = utils.getLon(zone.getLon(), lugar.getX()+ lugar.getW());

                double latx2 = utils.getLat(zone.getLat(), lugar.getY()+ lugar.getH());
                double lonx2 = utils.getLon(zone.getLon(), lugar.getX()+ lugar.getW());

                double lath = utils.getLat(zone.getLat(), lugar.getY() + lugar.getH());
                double lonh = utils.getLon(zone.getLon(), lugar.getX());

                Element node1 = doc.createElement("node");
                rootElement.appendChild(node1);
                String idn1 = String.valueOf(contador_osm);
                node1.setAttribute("id",idn1);
                node1.setAttribute("lat",String.valueOf(latx));
                node1.setAttribute("lon",String.valueOf(lonx));

                contador_osm-=1;

                Element node2 = doc.createElement("node");
                rootElement.appendChild(node2);
                String idn2 = String.valueOf(contador_osm);
                node2.setAttribute("id",idn2);
                node2.setAttribute("lat",String.valueOf(latw));
                node2.setAttribute("lon",String.valueOf(lonw));

                contador_osm-=1;

                Element node3 = doc.createElement("node");
                rootElement.appendChild(node3);
                String idn3 = String.valueOf(contador_osm);
                node3.setAttribute("id",idn3);
                node3.setAttribute("lat",String.valueOf(latx2));
                node3.setAttribute("lon",String.valueOf(lonx2));

                contador_osm-=1;

                Element node4 = doc.createElement("node");
                rootElement.appendChild(node4);
                String idn4 = String.valueOf(contador_osm);
                node4.setAttribute("id",idn4);
                node4.setAttribute("lat",String.valueOf(lath));
                node4.setAttribute("lon",String.valueOf(lonh));

                contador_osm-=1;

                //creamos el way
                Element way = doc.createElement("way");
                rootElement.appendChild(way);
                way.setAttribute("id",String.valueOf(contador_osm));
                //asignamos referencias de nodos
                Element nd1 = doc.createElement("nd");
                nd1.setAttribute("ref",idn1);
                Element nd2 = doc.createElement("nd");
                nd2.setAttribute("ref",idn2);
                Element nd3 = doc.createElement("nd");
                nd3.setAttribute("ref",idn3);
                Element nd4 = doc.createElement("nd");
                nd4.setAttribute("ref",idn4);
                way.appendChild(nd1);
                way.appendChild(nd2);
                way.appendChild(nd3);
                way.appendChild(nd4);

                //asignamos tags
                Element area_cerrada = doc.createElement("tag");
                area_cerrada.setAttribute("area","yes"); //Para indicar que es una zona CERRADA: https://wiki.openstreetmap.org/wiki/Way#Closed_way
                way.appendChild(area_cerrada);
                for (Tag tag: lugar.getTags()){
                    Element tag_ele = doc.createElement("tag");
                    tag_ele.setAttribute("k", tag.getKey());
                    tag_ele.setAttribute("v",tag.getValue());
                    way.appendChild(tag_ele);
                }
            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(System.out);

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
        pce.printStackTrace();
    } catch (TransformerException tfe) {
        tfe.printStackTrace();
    }

    }

}
