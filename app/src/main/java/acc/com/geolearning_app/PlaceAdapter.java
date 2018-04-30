package acc.com.geolearning_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import acc.com.geolearning_app.db.SqliteHelper;
import acc.com.geolearning_app.dto.Place;
import acc.com.geolearning_app.dto.Tag;
import acc.com.geolearning_app.dto.Zone;
import acc.com.geolearning_app.util.utils;

public class PlaceAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Place> items;
    private SqliteHelper sqliteHelper;
    private MapView mapView;
    private Zone zone;

    public PlaceAdapter(Context context, ArrayList<Place> items,MapView map, Zone zone) {
        this.context = context;
        this.items = items;
        this.mapView = map;
        this.zone = zone;
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
            convertView = inflater.inflate(R.layout.place_list, parent, false);
        }

        // se cogen los ids en los layouts
        ImageView imagen = (ImageView) convertView.findViewById(R.id.icono_lugar);
        TextView label = (TextView) convertView.findViewById(R.id.clase_label);
        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.delete_place);
        ImageButton addTagsButton = (ImageButton) convertView.findViewById(R.id.add_tags);


        //se rellenan los campos
        final Place item = this.items.get(position);

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
                utils.drawPolygons(mapView,items,zone);

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

        return convertView;
    }

}
