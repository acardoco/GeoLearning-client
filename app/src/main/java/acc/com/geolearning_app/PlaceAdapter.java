package acc.com.geolearning_app;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import acc.com.geolearning_app.dto.Place;

public class PlaceAdapter extends BaseAdapter {

    private Context context;
    private List<Place> items;

    public PlaceAdapter(Context context, List<Place> items) {
        this.context = context;
        this.items = items;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.place_list, parent, false);
        }

        // Set data into the view.
        ImageView imagen = (ImageView) convertView.findViewById(R.id.icono_lugar);
        TextView coordenadas = (TextView) convertView.findViewById(R.id.coordenadas);
        TextView label = (TextView) convertView.findViewById(R.id.clase_label);

        Place item = this.items.get(position);

        coordenadas.setText(item.getX().toString() + "," + item.getY().toString() + "," + item.getW().toString() + "," + item.getH().toString());
        label.setText(item.getPlace_type());
        if (item.getPlace_type().equals("piscina")) {
            imagen.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.stepladder));
        }
        if (item.getPlace_type().equals("rotonda")){
            imagen.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.roundabout));
        }


        return convertView;
    }

}
