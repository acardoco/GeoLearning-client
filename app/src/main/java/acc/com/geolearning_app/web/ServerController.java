package acc.com.geolearning_app.web;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import acc.com.geolearning_app.R;
import acc.com.geolearning_app.db.SqliteHelper;
import acc.com.geolearning_app.dto.Nodo;
import acc.com.geolearning_app.dto.Place;
import acc.com.geolearning_app.dto.Zone;
import acc.com.geolearning_app.util.Server_Status;
import acc.com.geolearning_app.util.utils;

public class ServerController extends AppCompatActivity {

    //singleton
    private static ServerController instance = null;

    private double LAT;
    private double LON;

    private Context context;
    private DrawerLayout drawerLayout;

    private String SERVER_URL_MAP = "http://192.168.0.159:5000/predict";//192.168.0.159
    private String SERVER_URL_MAP_LIST = "http://192.168.0.159:5000/predict_query";

    SqliteHelper sqliteHelper;

    Server_Status server_status = Server_Status.getInstance();

    ArrayList<Zone> lugares = new ArrayList<Zone>();


    protected ServerController(Context context, SqliteHelper sqliteHelper, DrawerLayout drawerLayout) {
        // Exists only to defeat instantiation.
        this.context = context;
        this.sqliteHelper = sqliteHelper;
        this.drawerLayout = drawerLayout;
    }
    public static ServerController getInstance(Context context, SqliteHelper sqliteHelper, DrawerLayout drawerLayout) {
        if(instance == null) {
            instance = new ServerController(context, sqliteHelper, drawerLayout);
        }
        return instance;
    }

    //Delega en el servidor pedir la imagen del mapa a la API de Google Maps
    public void getCandidates(double lat, double lon){

        AsyncTask myTask = new GetUrlJsonTask();

        LAT = lat;
        LON = lon;

        //valores de input
        Object[] arg = new String[]{SERVER_URL_MAP,String.valueOf(LAT),String.valueOf(LON)};


        myTask.execute(arg);


    }

    //Para enviar y procesas varias zonas a la vez
    public void getListCandidates(ArrayList<Zone> zonas){

        AsyncTask myTask = new GetListJsonTask(zonas);

        lugares = zonas;

        Object[] arg = new String[]{null};

        myTask.execute(arg);

    }
    /**************************************************/
    /**************************************************/
    /**************************************************/
    /**************************************************/
    /**************************************************/
    /*****************ASYNC CLASES *******************/
    /**************************************************/
    /**************************************************/
    /**************************************************/
    /**************************************************/
    //TODO AsyncTask <Input, Variables a meter en OnProgressUpdate, tipo del resultado>

    //TODO se pasa un json con lat y lon y se obtiene un hasmap con los candidatos
    private class GetUrlJsonTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... values) {

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            URL url = stringToURL(values[0]);
            Double lat = Double.parseDouble(values[1]);
            Double lon = Double.parseDouble(values[2]);
            HttpURLConnection connection = null;

            try{

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoOutput(true); //para peticiones POST
                connection.connect();

                String json = "";

                // Build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("lat", lat.toString());
                jsonObject.accumulate("lon", lon.toString());

                Log.i("JSON", jsonObject.toString());
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                outputStream.writeBytes(jsonObject.toString());

                outputStream.flush();
                outputStream.close();

                Log.i("STATUS", String.valueOf(connection.getResponseCode()));
                Log.i("MSG" , connection.getResponseMessage());

                //leer respuesta
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //cerrar conexion
                connection.disconnect();

                return response.toString();


            }catch(IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                // Disconnect the http url connection
                connection.disconnect();
            }
            return null;
            }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //progressDialog.dismiss();
            try {

                server_status.setBUSY_SERVER(false);

                Snackbar.make(drawerLayout, R.string.server_done,
                        Snackbar.LENGTH_LONG)
                        .show();

                if (s!=null) {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("candidatos");
                    Zone zone = new Zone();
                    zone.setLat(LAT);
                    zone.setLon(LON);
                    ArrayList<Place> places = new ArrayList<Place>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String label = jsonArray.getJSONObject(i).getString("label");
                        int x = Integer.parseInt(jsonArray.getJSONObject(i).getString("x"));
                        int y = Integer.parseInt(jsonArray.getJSONObject(i).getString("y"));
                        int w = Integer.parseInt(jsonArray.getJSONObject(i).getString("w"));
                        int h = Integer.parseInt(jsonArray.getJSONObject(i).getString("h"));
                        double prob = Double.parseDouble(jsonArray.getJSONObject(i).getString("prob"));
                        Place place = new Place(x, y, w, h, label, zone, prob);
                        //distincion para dibujar un "circulo" o rectangulos
                        if (label.equals("rotonda")){

                            int radioi =  Integer.parseInt(jsonArray.getJSONObject(i).getString("r"));
                            int ai =  Integer.parseInt(jsonArray.getJSONObject(i).getString("a"));
                            int bi =Integer.parseInt(jsonArray.getJSONObject(i).getString("b"));

                            //añadimos a place
                            place.setA_centro(ai);
                            place.setB_centro(bi);
                            place.setRadio(radioi);

                            //cast
                            double a = (double)ai;
                            double b = (double)bi;
                            double radio = (double)radioi;

                            double angulo = 0;

                            //se meten los nodos
                            for (int p = 1; p <= 16; p++){

                               Nodo nodo = new Nodo();
                               double a_centro = a + radio * Math.cos(angulo);
                               double b_centro = b + radio * Math.sin(angulo);
                               double lon = utils.getLon(zone.getLon(),a_centro);
                               double lat = utils.getLat(zone.getLat(),b_centro);
                               nodo.setLat(lat);
                               nodo.setLon(lon);
                               nodo.setType(p);
                               place.addNodo(nodo);

                               angulo+=(22.5 * Math.PI) / 180;//360/16 y en radianes
                            }
                            //añadir los nodos al lugar/place
                            places.add(place);

                        }else {
                            //nodos de un lugar/place
                            Nodo nodox = new Nodo();nodox.setType(0);// 0->x, 1->w, 2->h,3->x2
                            Nodo nodox2 = new Nodo();nodox2.setType(3);
                            Nodo nodow = new Nodo();nodow.setType(1);
                            Nodo nodoh = new Nodo();nodoh.setType(2);
                            nodox.setLat(utils.getLat(zone.getLat(), y));
                            nodox.setLon(utils.getLon(zone.getLon(), x));
                            nodow.setLat(utils.getLat(zone.getLat(), y));
                            nodow.setLon(utils.getLon(zone.getLon(), x + w));
                            nodoh.setLat(utils.getLat(zone.getLat(), y + h));
                            nodoh.setLon(utils.getLon(zone.getLon(), x));
                            nodox2.setLat(utils.getLat(zone.getLat(), y + h));
                            nodox2.setLon(utils.getLon(zone.getLon(), x + w));
                            place.addNodo(nodox);
                            place.addNodo(nodow);
                            place.addNodo(nodoh);
                            place.addNodo(nodox2);
                            //añadir los nodos al lugar/place
                            places.add(place);
                        }
                    }
                    zone.setPlaces(places);

                    //SQLITE
                    long id_zone = sqliteHelper.addZone(zone,true);
                    zone.setId(Long.toString(id_zone));
                    for (int i = 0; i < places.size(); i++) {
                        Place place = places.get(i);
                        place.setId_map(zone);
                        long id_place = sqliteHelper.addPlace(places.get(i));
                        place.setId(String.valueOf(id_place));
                        ArrayList<Nodo> nodos = places.get(i).getNodos();
                        for (Nodo nodo: nodos){
                            nodo.setId_place(place);
                            sqliteHelper.addNode(nodo);
                        }
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private class GetListJsonTask extends  AsyncTask<String,Void, String>{

        ArrayList<Zone> zonas = new ArrayList<>();

        GetListJsonTask(ArrayList<Zone> lugares){
            this.zonas = lugares;
        }

        @Override
        protected String doInBackground(String... values) {

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            URL url = stringToURL(SERVER_URL_MAP_LIST);
            HttpURLConnection connection = null;

            try{

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoOutput(true); //para peticiones POST
                connection.connect();

                String json = "";

                // Build jsonObject

                JSONArray list = new JSONArray();
                for (Zone zona: zonas){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("id",zona.getId());
                    jsonObject.accumulate("lat", zona.getLat().toString());
                    jsonObject.accumulate("lon", zona.getLon().toString());
                    list.put(jsonObject);
                }

                JSONObject mainObject = new JSONObject();
                mainObject.put("coordenadas",list);

                Log.i("JSON", mainObject.toString());
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                outputStream.writeBytes(mainObject.toString());

                outputStream.flush();
                outputStream.close();

                Log.i("STATUS", String.valueOf(connection.getResponseCode()));
                Log.i("MSG" , connection.getResponseMessage());

                //leer respuesta
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //cerrar conexion
                connection.disconnect();

                return response.toString();


            }catch(IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                // Disconnect the http url connection
                connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //progressDialog.dismiss();
            try {

                server_status.setBUSY_SERVER(false);

                Snackbar.make(drawerLayout, R.string.server_done,
                        Snackbar.LENGTH_LONG)
                        .show();

                if (s!=null) {
                    JSONObject jsonObject = new JSONObject(s);

                    JSONArray jsonArray = jsonObject.getJSONArray("candidatos");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        for (int j = 0; j < lugares.size(); j++ ){
                            String id_zone = jsonArray.getJSONObject(i).getString("id");
                            if (id_zone.equals(lugares.get(j).getId())){
                                Zone zone = lugares.get(j);
                                zone.setMapped(true);
                                sqliteHelper.updateZoneToMapped(lugares.get(j).getId());

                                String label = jsonArray.getJSONObject(i).getString("label");
                                int x = Integer.parseInt(jsonArray.getJSONObject(i).getString("x"));
                                int y = Integer.parseInt(jsonArray.getJSONObject(i).getString("y"));
                                int w = Integer.parseInt(jsonArray.getJSONObject(i).getString("w"));
                                int h = Integer.parseInt(jsonArray.getJSONObject(i).getString("h"));
                                double prob = Double.parseDouble(jsonArray.getJSONObject(i).getString("prob"));
                                Place place = new Place(x, y, w, h, label, zone, prob);
                                place.setId_map(zone);

                                //circunferencia
                                int radioi =  Integer.parseInt(jsonArray.getJSONObject(i).getString("r"));
                                int ai =  Integer.parseInt(jsonArray.getJSONObject(i).getString("a"));
                                int bi =Integer.parseInt(jsonArray.getJSONObject(i).getString("b"));
                                //añadimos a place
                                place.setA_centro(ai);
                                place.setB_centro(bi);
                                place.setRadio(radioi);

                                long id_place = sqliteHelper.addPlace(place);
                                place.setId(String.valueOf(id_place));
                                //nodos
                                if (label.equals("rotonda")){

                                    //cast
                                    double a = (double)ai;
                                    double b = (double)bi;
                                    double radio = (double)radioi;

                                    double angulo = 0; //360/12

                                    //meter nodos restantes
                                    for (int p = 1; p <= 16; p++){
                                        //puntos


                                        Nodo nodo = new Nodo();
                                        double a_centro = a + radio * Math.cos(angulo);
                                        double b_centro = b + radio * Math.sin(angulo);
                                        double lon = utils.getLon(zone.getLon(),a_centro);
                                        double lat = utils.getLat(zone.getLat(),b_centro);
                                        nodo.setLat(lat);
                                        nodo.setLon(lon);
                                        nodo.setType(p);
                                        nodo.setId_place(place);

                                        angulo+=(22.5 * Math.PI) / 180;//360/16 y en radianes
                                        //añadimos BD
                                        sqliteHelper.addNode(nodo);
                                    }

                                }else {
                                    Nodo nodox = new Nodo();Nodo nodox2 = new Nodo();Nodo nodow = new Nodo();Nodo nodoh = new Nodo();
                                    nodox.setLat(utils.getLat(zone.getLat(), y));nodox.setLon(utils.getLon(zone.getLon(), x));
                                    nodox.setId_place(place);nodox.setType(0);// 0->x, 1->w, 2->h,3->x2
                                    nodow.setLat(utils.getLat(zone.getLat(), y));nodow.setLon(utils.getLon(zone.getLon(), x + w));
                                    nodow.setId_place(place);nodow.setType(1);
                                    nodoh.setLat(utils.getLat(zone.getLat(), y + h));nodoh.setLon(utils.getLon(zone.getLon(),x));
                                    nodoh.setId_place(place);nodoh.setType(2);
                                    nodox2.setLat(utils.getLat(zone.getLat(), y + h));nodox2.setLon(utils.getLon(zone.getLon(), x + w));
                                    nodox2.setId_place(place);nodox2.setType(3);
                                    sqliteHelper.addNode(nodox);sqliteHelper.addNode(nodow);sqliteHelper.addNode(nodox2);sqliteHelper.addNode(nodoh);
                                }
                            }
                        }
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    //UTILES

    // Custom method to convert string to url
    protected URL stringToURL(String urlString){
        try{
            URL url = new URL(urlString);
            return url;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    // Custom method to save a bitmap into internal storage
    protected Uri saveImageToInternalStorage(Bitmap bitmap){
        // Initialize ContextWrapper
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());

        // Initializing a new file
        // The bellow line return a directory in internal storage
        File file = wrapper.getDir("Images",MODE_PRIVATE);

        // Create a file to save the image
        file = new File(file, "UniqueFileName"+".jpg");

        try{
            // Initialize a new OutputStream
            OutputStream stream = null;

            // If the output file exists, it can be replaced or appended to it
            stream = new FileOutputStream(file);

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

            // Flushes the stream
            stream.flush();

            // Closes the stream
            stream.close();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());

        // Return the saved image Uri
        return savedImageURI;
    }

}
