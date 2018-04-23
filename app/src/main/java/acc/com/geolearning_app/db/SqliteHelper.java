package acc.com.geolearning_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import acc.com.geolearning_app.dto.Map;
import acc.com.geolearning_app.dto.Place;
import acc.com.geolearning_app.dto.User;

public class SqliteHelper extends SQLiteOpenHelper{

    //DATABASE NAME
    public static final String DATABASE_NAME = "geolearning";

    //DATABASE VERSION
    public static final int DATABASE_VERSION = 1;

    //TABLAS
    public static final String TABLE_USER = "user";
    public static final String TABLE_MAP = "map";
    public static final String TABLE_PLACE ="place";


    //ATRIBUTOS user
    //ID COLUMN @primaryKey
    public static final String KEY_ID = "id_user";
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    //ATRIBUTOS map
    public static final String KEY_ID_MAP = "id_map";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON ="lon";
    public static final String FOREIGN_KEY_USER = "map_user";

    //ATRIBUTOS place
    public static final String KEY_ID_PLACE = "id_place";
    public static final String KEY_X = "x";
    public static final String KEY_Y = "y";
    public static final String KEY_W = "w";
    public static final String KEY_H = "h";
    public static final String KEY_TYPE = "place_type";
    public static final String FOREIGN_KEY_MAP = "place_mapper";

    //CREAR tabla de usuarios
    public static final String SQL_TABLE_USER = " CREATE TABLE " + TABLE_USER
            + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_USER_NAME + " TEXT, "
            + KEY_EMAIL + " TEXT, "
            + KEY_PASSWORD + " TEXT"
            + " ) ";

    //CREAR tabla de mapas
    public static final String SQL_TABLE_MAP = " CREATE TABLE " + TABLE_MAP
            + "( "
            + KEY_ID_MAP + " INTEGER PRIMARY KEY, "
            + KEY_LAT + " REAL, "
            + KEY_LON + " REAL "
           /* + FOREIGN_KEY_USER + " INTEGER, "
            + " FOREIGN KEY(" + FOREIGN_KEY_USER + ") REFERENCES " +  TABLE_USER + "(" + KEY_ID + ")"*/
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
            + FOREIGN_KEY_MAP + " INTEGER, "
            + " FOREIGN KEY(" + FOREIGN_KEY_MAP + ") REFERENCES " +  TABLE_MAP + "(" + KEY_ID_MAP + ")"
            + " ) ";

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Se crean las tablas
        //sqLiteDatabase.execSQL(SQL_TABLE_USER);
        sqLiteDatabase.execSQL(SQL_TABLE_MAP);
        sqLiteDatabase.execSQL(SQL_TABLE_PLACE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //drop table to create new one if database version updated
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_PLACE);
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_MAP);
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_USER);
    }

    //AÑADIR usuario
    public void addUser(User user) {

        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();


        values.put(KEY_USER_NAME, user.getUserName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());

        // insert row
        long todo_id = db.insert(TABLE_USER, null, values);
    }

    //AÑADIR mapa
    public void addMap(Map map){

        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();

        values.put(KEY_LAT, map.getLat());
        values.put(KEY_LON, map.getLon());
        values.put(FOREIGN_KEY_USER, map.getId_user().getId());

        long todo_id = db.insert(TABLE_MAP, null, values);

    }

    //AÑADIR zona
    public void addPlace(Place place){

        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();

        values.put(KEY_X, place.getX());
        values.put(KEY_Y, place.getY());
        values.put(KEY_W, place.getW());
        values.put(KEY_H, place.getH());
        values.put(KEY_TYPE, place.getPlace_type());
        values.put(FOREIGN_KEY_MAP, place.getId_map().getId());

        long todo_id = db.insert(TABLE_PLACE, null, values);


    }

    public User Authenticate(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,// Selecting Table
                new String[]{KEY_ID, KEY_USER_NAME, KEY_EMAIL, KEY_PASSWORD},//Selecting columns want to query
                KEY_USER_NAME + "=?",
                new String[]{user.getUserName()},//Where clause
                null, null, null);

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            //if cursor has value then in user database there is user associated with this given email
            User user1 = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));

            //Match both passwords check they are same or not
            if (user.getPassword().equalsIgnoreCase(user1.getPassword())) {
                return user1;
            }
        }

        //if user password does not matches or there is no record with that email then return @false
        return null;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,// Selecting Table
                new String[]{KEY_ID, KEY_USER_NAME, KEY_EMAIL, KEY_PASSWORD},//Selecting columns want to query
                KEY_EMAIL + "=?",
                new String[]{email},//Where clause
                null, null, null);

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            //if cursor has value then in user database there is user associated with this given email so return true
            return true;
        }

        //if email does not exist return false
        return false;
    }
}
