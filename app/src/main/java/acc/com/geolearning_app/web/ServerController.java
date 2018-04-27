package acc.com.geolearning_app.web;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import acc.com.geolearning_app.R;
import acc.com.geolearning_app.db.SqliteHelper;
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


    String urlbase = "https://maps.googleapis.com/maps/api/staticmap?center=";
    String urlcomun = "&zoom=18&format=jpg&size=400x400&maptype=satellite&key=";
    String urlkey= "AIzaSyB9qW-QzzGtT2xEsJlsuLgA5TOYNJS8ogo";
    String destinationFile = "image.jpg";

    SqliteHelper sqliteHelper;

    Server_Status server_status = Server_Status.getInstance();


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

        String url_local = "http://192.168.0.159:5000/predict"; //192.168.0.159

        LAT = lat;
        LON = lon;

        //valores de input
        Object[] arg = new String[]{url_local,String.valueOf(LAT),String.valueOf(LON)};


        myTask.execute(arg);


    }

    //Hace una peticion directamente a la API de Google Maps y obtiene una imagen que guarda en el dispositivo
    public void getImage(double lat, double lon){

        AsyncTask mMyTask = new GetUrlImageTask();

        String string_final = utils.getStringUrl(lat,lon);
        URL url_final =  stringToURL(string_final);

        Object[] arg = new URL[]{url_final,null,null};

        mMyTask.execute(arg);
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

    //clase para ejecutar en segundo plano la peticion
    private class GetUrlImageTask extends AsyncTask<URL,Void,Bitmap> {
        // Before the tasks execution
        protected void onPreExecute(){
            // Display the progress dialog on async task start
            //mProgressDialog.show();
        }

        // Do the task in background/non UI thread
        protected Bitmap doInBackground(URL...urls){

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            URL url = urls[0];
            HttpURLConnection connection = null;

            try{
                // Initialize a new http url connection
                connection = (HttpURLConnection) url.openConnection();

                // Connect the http url connection
                connection.connect();

                // Get the input stream from http url connection
                InputStream inputStream = connection.getInputStream();

                /*
                    BufferedInputStream
                        A BufferedInputStream adds functionality to another input stream-namely,
                        the ability to buffer the input and to support the mark and reset methods.
                */
                /*
                    BufferedInputStream(InputStream in)
                        Creates a BufferedInputStream and saves its argument,
                        the input stream in, for later use.
                */
                // Initialize a new BufferedInputStream from InputStream
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                /*
                    decodeStream
                        Bitmap decodeStream (InputStream is)
                            Decode an input stream into a bitmap. If the input stream is null, or
                            cannot be used to decode a bitmap, the function returns null. The stream's
                            position will be where ever it was after the encoded data was read.

                        Parameters
                            is InputStream : The input stream that holds the raw data
                                              to be decoded into a bitmap.
                        Returns
                            Bitmap : The decoded bitmap, or null if the image data could not be decoded.
                */
                // Convert BufferedInputStream to Bitmap object
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                // Return the downloaded bitmap
                return bmp;

            }catch(IOException e){
                e.printStackTrace();
            }finally{
                // Disconnect the http url connection
                connection.disconnect();
            }
            return null;
        }

        // When all async task done
        protected void onPostExecute(Bitmap result){
            // Hide the progress dialog
            //mProgressDialog.dismiss();

            if(result!=null){
                // Display the downloaded image into ImageView
                //mImageView.setImageBitmap(result);

                // Save bitmap to internal storage
                Uri imageInternalUri = saveImageToInternalStorage(result);
                // Set the ImageView image from internal storage
                //mImageViewInternal.setImageURI(imageInternalUri);
            }else {
                // Notify user that an error occurred while downloading image
                //Snackbar.make(mCLayout,"Error",Snackbar.LENGTH_LONG).show();
            }
        }
    }

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
                    ArrayList<Place> puntos = new ArrayList<Place>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String label = jsonArray.getJSONObject(i).getString("label");
                        int x = Integer.parseInt(jsonArray.getJSONObject(i).getString("x"));
                        int y = Integer.parseInt(jsonArray.getJSONObject(i).getString("y"));
                        int w = Integer.parseInt(jsonArray.getJSONObject(i).getString("w"));
                        int h = Integer.parseInt(jsonArray.getJSONObject(i).getString("h"));
                        double prob = Double.parseDouble(jsonArray.getJSONObject(i).getString("prob"));
                        Place place = new Place(x, y, w, h, label, zone, prob);
                        puntos.add(place);
                    }
                    zone.setPlaces(puntos);

                    //SQLITE
                    long id_zone = sqliteHelper.addZone(zone);
                    zone.setId(Long.toString(id_zone));
                    for (int i = 0; i < puntos.size(); i++) {
                        Place place = puntos.get(i);
                        place.setId_map(zone);
                        sqliteHelper.addPlace(puntos.get(i));
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
