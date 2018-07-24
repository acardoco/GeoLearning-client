package acc.com.geolearning_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import acc.com.geolearning_app.dto.Nodo;
import acc.com.geolearning_app.dto.Place;
import acc.com.geolearning_app.dto.Tag;
import acc.com.geolearning_app.dto.Zone;

public class SqliteHelper extends SQLiteOpenHelper{

    //DATABASE NAME
    public static final String DATABASE_NAME = "geolearning";

    //DATABASE VERSION
    public static final int DATABASE_VERSION = 2;

    //TABLAS
    public static final String TABLE_MAP = "map"; //Equivalente a ZONE
    public static final String TABLE_PLACE ="place";
    public static final String TABLE_TAG = "tag";
    public static final String TABLE_NODE = "node";


    //ATRIBUTOS user

    //ATRIBUTOS map
    public static final String KEY_ID_MAP = "id_map";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON ="lon";
    public static final String KEY_IS_MAPPED = "mapped";

    //ATRIBUTOS place
    public static final String KEY_ID_PLACE = "id_place";
    public static final String KEY_X = "x";
    public static final String KEY_Y = "y";
    public static final String KEY_W = "w";
    public static final String KEY_H = "h";
    public static final String KEY_TYPE = "place_type";
    public static final String KEY_A_CENTRO = "a_centro";
    public static final String KEY_B_CENTRO = "b_centro";
    public static final String KEY_RADIO = "radio";
    public static final String FOREIGN_KEY_MAP = "place_mapper";

    //ATRIBUTOS tag
    public static final String KEY_ID_TAG ="id_tag";
    public static final String KEY_KEY = "tag_key";
    public static final String KEY_VALUE = "value";
    public static final String FOREIGN_KEY_PLACE = "id_place";

    //ATRIBUTOS NODE
    public static final String KEY_ID_NODE ="id_node";
    public static final String KEY_LAT_NODE="lat";
    public static final String KEY_LON_NODE="lon";
    public static final String KEY_TYPE_NODE ="type";
    //misma foreign key que tag

    public static final String KEY_ID_CHANGESET = "id_changeset";

    //CREAR tabla de mapas
    public static final String SQL_TABLE_MAP = " CREATE TABLE " + TABLE_MAP
            + "( "
            + KEY_ID_MAP + " INTEGER PRIMARY KEY, "
            + KEY_LAT + " REAL, "
            + KEY_LON + " REAL, "
            + KEY_IS_MAPPED + " INTEGER " //no hay booleanos en SQLite
            + " ) ";

    //CREAR tabla de lugares
    public static final String SQL_TABLE_PLACE = " CREATE TABLE " + TABLE_PLACE
            + " ( "
            + KEY_ID_PLACE + " INTEGER PRIMARY KEY, "
            + KEY_X + " INTEGER, "
            + KEY_Y + " INTEGER, "
            + KEY_W + " INTEGER, "
            + KEY_H + " INTEGER, "
            + KEY_TYPE + " TEXT, "
            + KEY_A_CENTRO + " INTEGER,"
            + KEY_B_CENTRO + " INTEGER,"
            + KEY_RADIO + " INTEGER,"
            + KEY_ID_CHANGESET + " INTEGER,"
            + FOREIGN_KEY_MAP + " INTEGER, "
            + " FOREIGN KEY(" + FOREIGN_KEY_MAP + ") REFERENCES " +  TABLE_MAP + "(" + KEY_ID_MAP + ")"
            + " ) ";

    //CREAR tabla de tags
    public static final String SQL_TABLE_TAG = " CREATE TABLE " + TABLE_TAG
            + "( "
            + KEY_ID_TAG + " INTEGER PRIMARY KEY, "
            + KEY_KEY + " TEXT, "
            + KEY_VALUE + " TEXT, "
            + FOREIGN_KEY_PLACE + " INTEGER, "
            + " FOREIGN KEY(" + FOREIGN_KEY_PLACE + ") REFERENCES " +  TABLE_PLACE + "(" + KEY_ID_PLACE + ")"
            + " ) ";
    //CREAR tabla de nodos
    public static final String SQL_TABLE_NODE = " CREATE TABLE " + TABLE_NODE
            + "( "
            + KEY_ID_NODE + " INTEGER PRIMARY KEY, "
            + KEY_LAT_NODE + " REAL, "
            + KEY_LON_NODE + " REAL, "
            + KEY_TYPE_NODE + " INTEGER, " // 0->x, 1->w, 2->h,3->x2
            + KEY_ID_CHANGESET + " INTEGER, "
            + FOREIGN_KEY_PLACE + " INTEGER, "
            + " FOREIGN KEY(" + FOREIGN_KEY_PLACE + ") REFERENCES " +  TABLE_PLACE + "(" + KEY_ID_PLACE + ")"
            + " ) ";

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Se crean las tablas
        sqLiteDatabase.execSQL(SQL_TABLE_MAP);
        sqLiteDatabase.execSQL(SQL_TABLE_PLACE);
        sqLiteDatabase.execSQL(SQL_TABLE_NODE);
        sqLiteDatabase.execSQL(SQL_TABLE_TAG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //drop table to create new one if database version updated
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_NODE);
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_TAG);
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_PLACE);
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_MAP);
    }

    //AÑADIR mapa
    public long addZone(Zone zone, boolean is_mapped){

        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();

        values.put(KEY_LAT, zone.getLat());
        values.put(KEY_LON, zone.getLon());

        if (is_mapped)
            values.put(KEY_IS_MAPPED,1);
        else
            values.put(KEY_IS_MAPPED,0);


        return  db.insert(TABLE_MAP, null, values);

    }

    //AÑADIR lugar
    public long addPlace(Place place){

        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();

        values.put(KEY_X, place.getX());
        values.put(KEY_Y, place.getY());
        values.put(KEY_W, place.getW());
        values.put(KEY_H, place.getH());
        values.put(KEY_TYPE, place.getPlace_type());
        values.put(KEY_A_CENTRO, place.getA_centro());
        values.put(KEY_B_CENTRO, place.getB_centro());
        values.put(KEY_RADIO, place.getRadio());
        values.put(KEY_ID_CHANGESET, "0");
        values.put(FOREIGN_KEY_MAP, place.getId_map().getId());

        return db.insert(TABLE_PLACE, null, values);

    }

    //añadir un tag
    public long addTag(Tag tag){

        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();

        values.put(KEY_KEY,tag.getKey());
        values.put(KEY_VALUE,tag.getValue());
        values.put(FOREIGN_KEY_PLACE,tag.getId_place().getId());

        return db.insert(TABLE_TAG, null, values);

    }

    public long addNode(Nodo node){

        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();

        values.put(KEY_LAT_NODE,node.getLat());
        values.put(KEY_LON_NODE,node.getLon());
        values.put(KEY_TYPE_NODE,node.getType()); // 0->x, 1->w, 2->h,3->x2
        values.put(KEY_ID_CHANGESET, "0");
        values.put(FOREIGN_KEY_PLACE,node.getId_place().getId());

        return db.insert(TABLE_NODE, null, values);

    }

    // obtener todas las zonas/mapas
    public ArrayList<Zone> getAllElements() {

        ArrayList<Zone> list = new ArrayList<Zone>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MAP;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {

                        Zone obj = new Zone();
                        obj.setId(cursor.getString(0));
                        obj.setLat(cursor.getDouble(1));
                        obj.setLon(cursor.getDouble(2));
                        int mapped = cursor.getInt(3);
                        if (mapped==1)
                            obj.setMapped(true);
                        else
                            obj.setMapped(false);
                        list.add(obj);

                    } while (cursor.moveToNext());
                }
            } finally {
                try { cursor.close(); } catch (Exception ignore) {}
            }
        } finally {
            try { db.close(); } catch (Exception ignore) {}
        }
        return list;
    }



    //obtiene los lugares de una zona
    public ArrayList<Place> getAllElements(String id) {
        ArrayList<Place> list = new ArrayList<Place>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT  P.id_place, P.x, P.y, P.w, P.h, P.a_centro, P.b_centro, P.radio, P.place_type, P.id_changeset  FROM "
                + TABLE_PLACE
                + " P JOIN "
                + TABLE_MAP
                + " Z ON P.place_mapper=Z.id_map WHERE P."
                + FOREIGN_KEY_MAP + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {

                        Place obj = new Place();
                        obj.setId(cursor.getString(0));
                        obj.setX(cursor.getInt(1));
                        obj.setY(cursor.getInt(2));
                        obj.setW(cursor.getInt(3));
                        obj.setH(cursor.getInt(4));
                        obj.setA_centro(cursor.getInt(5));
                        obj.setB_centro(cursor.getInt(6));
                        obj.setRadio(cursor.getInt(7));
                        obj.setPlace_type(cursor.getString(8));
                        obj.setId_changeset(cursor.getString(9));
                        obj.setTags(getAllTags(obj.getId()));
                        list.add(obj);

                    } while (cursor.moveToNext());
                }
            } finally {
                try { cursor.close(); } catch (Exception ignore) {}
            }
        } finally {
            try { db.close(); } catch (Exception ignore) {}
        }


        return list;
    }


    // obtener tags de un lugar
    public ArrayList<Tag> getAllTags(String id) {

        ArrayList<Tag> list = new ArrayList<Tag>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT  T.id_tag, T.tag_key, T.value FROM "
                + TABLE_TAG
                + " T JOIN "
                + TABLE_PLACE
                + " P ON T.id_place=P.id_place WHERE T."
                + FOREIGN_KEY_PLACE + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {

                        Tag obj = new Tag();
                        obj.setId(cursor.getString(0));
                        obj.setKey(cursor.getString(1));
                        obj.setValue(cursor.getString(2));
                        list.add(obj);

                    } while (cursor.moveToNext());
                }
            } finally {
                try { cursor.close(); } catch (Exception ignore) {}
            }
        } finally {
            try { db.close(); } catch (Exception ignore) {}
        }
        return list;
    }

    // obtener nodos de un lugar
    public ArrayList<Nodo> getAllNodos(String id) {

        ArrayList<Nodo> list = new ArrayList<Nodo>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT  N.id_node, N.lat, N.lon, N.type, N.id_changeset FROM "
                + TABLE_NODE
                + " N JOIN "
                + TABLE_PLACE
                + " P ON N.id_place=P.id_place WHERE N."
                + FOREIGN_KEY_PLACE + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {

                        Nodo obj = new Nodo();
                        obj.setId(cursor.getString(0));
                        obj.setLat(cursor.getDouble(1));
                        obj.setLon(cursor.getDouble(2));
                        obj.setType(cursor.getInt(3));
                        obj.setId_changeset(cursor.getString(4));
                        list.add(obj);

                    } while (cursor.moveToNext());
                }
            } finally {
                try { cursor.close(); } catch (Exception ignore) {}
            }
        } finally {
            try { db.close(); } catch (Exception ignore) {}
        }
        return list;
    }

    public Zone getZona(String id){

        Zone obj = new Zone();

        String selectQuery = "SELECT  * FROM " + TABLE_MAP + " WHERE " + KEY_ID_MAP + "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            obj.setId(cursor.getString(0));
            obj.setLat(cursor.getDouble(1));
            obj.setLon(cursor.getDouble(2));
            int mapped = cursor.getInt(3);
            if (mapped==1)
                obj.setMapped(true);
            else
                obj.setMapped(false);

        }

        cursor.close();
        db.close();

        return obj;

    }

    public boolean deleteLugar(String id){

        SQLiteDatabase db = this.getReadableDatabase();

        return db.delete(TABLE_PLACE,KEY_ID_PLACE + "=" + id,null) > 0;

    }

    public boolean deleteTag(String id){

        SQLiteDatabase db = this.getReadableDatabase();

        return db.delete(TABLE_TAG,KEY_ID_TAG + "=" + id,null) > 0;

    }

    public boolean deleteNodosLugar(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        return db.delete(TABLE_NODE,FOREIGN_KEY_PLACE + "=" + id,null) > 0;
    }

    public void updateZoneToMapped(String id){

        String selectQuery = "UPDATE " + TABLE_MAP + " SET " + KEY_IS_MAPPED + "=1 WHERE " + KEY_ID_MAP + "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL(selectQuery);

    }

    public void updatePlaceChangeset(Place place){
        String selectQuery = "UPDATE " + TABLE_PLACE + " SET "
                + KEY_ID_CHANGESET + "=" + place.getId_changeset()
                + " WHERE " + KEY_ID_PLACE + "=" + place.getId();
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL(selectQuery);
    }

    public void updateNodo(Nodo nodo){
        String selectQuery = "UPDATE " + TABLE_NODE + " SET "
                + KEY_LAT_NODE + "=" + nodo.getLat()
                + ","
                + KEY_LON_NODE + "=" + nodo.getLon()
                + " WHERE " + KEY_ID_NODE + "=" + nodo.getId();
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL(selectQuery);
    }

    public void updateNodoChangeset(Nodo nodo){
        String selectQuery = "UPDATE " + TABLE_NODE + " SET "
                + KEY_ID_CHANGESET + "=" + nodo.getId_changeset()
                + " WHERE " + KEY_ID_NODE + "=" + nodo.getId();
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL(selectQuery);
    }


}
