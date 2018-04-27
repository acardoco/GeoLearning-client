package acc.com.geolearning_app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import acc.com.geolearning_app.db.SqliteHelper;
import acc.com.geolearning_app.dto.Place;
import acc.com.geolearning_app.dto.Zone;
import acc.com.geolearning_app.util.utils;


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    //Sqlite
    SqliteHelper sqliteHelper;

    /**
     * The dummy content this fragment is presenting.
     */
    private Zone mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            sqliteHelper  = new SqliteHelper(getActivity());

            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //mItem = Zone.ZONE_MAP.get(getArguments().getString(ARG_ITEM_ID));
            mItem = sqliteHelper.getZona(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("Zone with id: " + mItem.getId());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        ArrayList<Place> lugares = sqliteHelper.getAllElements(getArguments().getString(ARG_ITEM_ID));

        if (mItem != null) {

            TextView txt= (TextView) rootView.findViewById(R.id.item_detail_list);
            txt.setText(" Places: " + lugares.size() + "\n" + " Accuracy: 80%"
                    );



            ImageView image = (ImageView) rootView.findViewById(R.id.item_detail_image);

            String url_lat_lon = utils.getStringUrl(mItem.getLat(),mItem.getLon());

            //++

            Bitmap bitmap = null;

            AsyncTask<String,Void,Bitmap> task = new DownloadImageTask(bitmap);

            try {
                bitmap = task.execute(url_lat_lon).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            /*new DownloadImageTask(image)
                    .execute(url_lat_lon);*/

            Bitmap bmp= bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas cnvs=new Canvas(bmp);
            //img.setImageBitmap(bmp);

            Paint paint=new Paint();
            paint.setColor(Color.RED);

            cnvs.drawBitmap(bmp,0,0,null);
            cnvs.drawRect(20, 20,50,50 , paint);

            image.setImageBitmap(bmp);

            //--




        }

        return rootView;
    }


    // Llama al servidor de Google Maps y devuelve una imagen convertida a Bitmap
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bmImage;

        public DownloadImageTask(Bitmap bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage = result;
        }
    }

}
