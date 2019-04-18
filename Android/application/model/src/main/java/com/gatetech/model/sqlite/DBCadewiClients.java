package com.gatetech.model.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gatetech.model.sqlite.DBModel.LoggerEntry;
import com.gatetech.model.sqlite.DBModel.UsersEntry;


public class DBCadewiClients extends SQLiteOpenHelper {


    //information of database
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "cadewiClientDB.db";
    private final Context context;
    private final String NEW = "nuevo";


    public DBCadewiClients(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        run_createtables(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        run_droptables(db);

        if (oldVersion < 1) {
            run_createtables(db);
        }
        else{
            run_alter_tables(db);

        }

    }


    private void run_droptables (SQLiteDatabase db) {
        //db.execSQL("DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBModel.LoggerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBModel.ClientsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBModel.AddressEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBModel.ContactEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBModel.ImagesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBModel.PropertyEntry.TABLE_NAME);
    }


    private void run_createtables(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + UsersEntry.TABLE_NAME+ " ("
                + UsersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UsersEntry.EMAIL + " TEXT NOT NULL,"
                + UsersEntry.NAME + " TEXT NOT NULL,"
                + UsersEntry.LASTNAME + " TEXT NOT NULL,"
                + UsersEntry.PASSWORD + " TEXT NOT NULL,"
                + UsersEntry.PROFILE + " TEXT,"
                + UsersEntry.ISACTIVE + " INTEGER NOT NULL,"
                + "UNIQUE (" + UsersEntry.EMAIL + "))"
        );

        run_alter_tables (db);

    }

    private void run_alter_tables(SQLiteDatabase db) {

        db.execSQL("ALTER TABLE " + UsersEntry.TABLE_NAME+ " ADD " + UsersEntry.STATUS + " TEXT DEFAULT '" + NEW + "';");

        db.execSQL("CREATE TABLE " + LoggerEntry.TABLE_NAME + " ("
                + LoggerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LoggerEntry.USER_EMAIL + " TEXT NOT NULL,"
                + LoggerEntry.TYPE + " TEXT NOT NULL,"
                + LoggerEntry.CATEGORY + " TEXT NOT NULL,"
                + LoggerEntry.DEVICE + " TEXT NOT NULL,"
                + LoggerEntry.MESSAGE + " TEXT NOT NULL,"
                + LoggerEntry.STATUS + " TEXT NOT NULL DEFAULT '"+ NEW+"',"
                + LoggerEntry.DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)"
        );

        db.execSQL("CREATE TABLE " + DBModel.ClientsEntry.TABLE_NAME + " ("
                + DBModel.ClientsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBModel.ClientsEntry.CLIENT + " INTEGER NOT NULL,"
                + DBModel.ClientsEntry.USER_EMAIL + " TEXT NOT NULL,"
                + DBModel.ClientsEntry.NAME + " TEXT NOT NULL,"
                + DBModel.ClientsEntry.RFC + " TEXT NOT NULL,"
                + DBModel.ClientsEntry.STATUS + " TEXT NOT NULL DEFAULT '"+ NEW+"',"
                + DBModel.ClientsEntry.DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)"
        );


        db.execSQL("CREATE TABLE " + DBModel.AddressEntry.TABLE_NAME + " ("
                + DBModel.AddressEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBModel.AddressEntry.CLIENT + " INTEGER NOT NULL,"
                + DBModel.AddressEntry.COUNTRY + " TEXT NOT NULL,"
                + DBModel.AddressEntry.ZIPCODE + " TEXT NOT NULL,"
                + DBModel.AddressEntry.STREET + " TEXT NOT NULL,"
                + DBModel.AddressEntry.MUNICIPALITY + " TEXT NOT NULL,"
                + DBModel.AddressEntry.STATE + " TEXT NOT NULL,"
                + DBModel.AddressEntry.CITY + " TEXT NOT NULL,"
                + DBModel.AddressEntry.COLONY + " TEXT NOT NULL,"
                + DBModel.AddressEntry.DETAILS + " TEXT NOT NULL,"
                + DBModel.AddressEntry.LONGITUDE + " TEXT,"
                + DBModel.AddressEntry.LATITUDE + " TEXT,"
                + DBModel.AddressEntry.LOCEDITABLE + " INTEGER DEFAULT 1,"
                + DBModel.AddressEntry.STATUS + " TEXT NOT NULL DEFAULT '"+ NEW+"',"
                + DBModel.AddressEntry.DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)"
        );


        db.execSQL("CREATE TABLE " + DBModel.ContactEntry.TABLE_NAME + " ("
                + DBModel.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBModel.ContactEntry.CLIENT + " INTEGER NOT NULL,"
                + DBModel.ContactEntry.TYPE + " TEXT NOT NULL,"
                + DBModel.ContactEntry.VALUE + " TEXT NOT NULL,"
                + DBModel.ContactEntry.STATUS + " TEXT NOT NULL DEFAULT '"+ NEW+"',"
                + DBModel.ContactEntry.DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)"
        );


        db.execSQL("CREATE TABLE " + DBModel.ImagesEntry.TABLE_NAME + " ("
                + DBModel.ImagesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBModel.ImagesEntry.ADDRESS + " TEXT NOT NULL,"
                + DBModel.ImagesEntry.CLIENT + " INTEGER NOT NULL,"
                + DBModel.ImagesEntry.NAME + " TEXT NOT NULL,"
                + DBModel.ImagesEntry.PATH + " TEXT NOT NULL,"
                + DBModel.ImagesEntry.NOTE + " TEXT NOT NULL,"
                + DBModel.ImagesEntry.STATUS + " TEXT NOT NULL DEFAULT '"+ NEW + "',"
                + DBModel.ImagesEntry.DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)"
        );

        db.execSQL("CREATE TABLE " + DBModel.PropertyEntry.TABLE_NAME + " ("
                + DBModel.PropertyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBModel.PropertyEntry.NAME + " TEXT NOT NULL,"
                + DBModel.PropertyEntry.CATEGORY + " TEXT NOT NULL,"
                + DBModel.PropertyEntry.STRING_VALUE + " TEXT DEFAULT NULL,"
                + DBModel.PropertyEntry.INTEGER_VALUE + " INTEGER DEFAULT NULL,"
                + DBModel.PropertyEntry.DECIMAL_VALUE + " DECIMAL DEFAULT NULL)"
        );

    }


}
