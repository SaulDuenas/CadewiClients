package com.gatetech.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gatetech.content.PhotoContent;
import com.gatetech.model.sqlite.sqlbuilder;
import com.gatetech.utils.Utils;
import com.gatetech.model.sqlite.DBCadewiClients;
import com.gatetech.model.sqlite.DBModel.ImagesEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PhotoContext extends sqlbuilder {

    private DBCadewiClients database;


    public PhotoContext(Context context ) {
        //super();
        super(new DBCadewiClients(context));

        this.select(new String[] { ImagesEntry._ID,
                                   ImagesEntry.ADDRESS,
                                   ImagesEntry.CLIENT,
                                   ImagesEntry.NAME,
                                   ImagesEntry.PATH,
                                   ImagesEntry.NOTE,
                                   ImagesEntry.STATUS,
                                   ImagesEntry.DATE
        } ).from_table(ImagesEntry.TABLE_NAME);

     //   database = new DBCadewiClients(context);
    }


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

/*
    public List<PhotoContent.PhotoItem> getPhotos(String estatus, Integer addressId, Date date) {

        List<PhotoContent.PhotoItem> Items= new ArrayList<PhotoContent.PhotoItem>();

        String query = "SELECT * FROM " + ImagesEntry.TABLE_NAME +
                       " WHERE 1 = 1";
        if (estatus != null) { query = query + " AND "+ ImagesEntry.STATUS +" = '" + estatus +"'"; }
        if (addressId != null) { query = query +  " AND " + ImagesEntry.ADDRESS + "=" +addressId.toString() ; }
        if (date != null) {query = query +  " AND " + ImagesEntry.DATE + "> '" + dateFormat.format(date) +"'" ; }

        query = query +  " ORDER BY " + ImagesEntry.DATE + " DESC;";

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {

            Items.add(
                new PhotoContent.PhotoItem(Integer.parseInt(cursor.getString(0)),
                                           Integer.parseInt(cursor.getString(1)),
                                           Integer.parseInt(cursor.getString(2)),
                                           null,
                                           cursor.getString(3),
                                           cursor.getString(4),
                                           cursor.getString(5),
                                           cursor.getString(6),
                                           cursor.getString(7)
                                          )
            );

        }

        cursor.close();
        db.close();


        return Items;
    }


*/
    public void addPhoto(Integer addressId,Integer clientId,String name,String path,String note,Utils.ESTATUS estatus) {
        ContentValues values = new ContentValues();

        values.put(ImagesEntry.ADDRESS, addressId);
        values.put(ImagesEntry.CLIENT, clientId);
        values.put(ImagesEntry.NAME , name);
        values.put(ImagesEntry.PATH, path);
        values.put(ImagesEntry.NOTE, note);
        values.put(ImagesEntry.STATUS, estatus.toString());

        SQLiteDatabase db = this.db_write();
        db.insert(ImagesEntry.TABLE_NAME, null, values);
        db.close();
    }

    public int deletePhoto(Integer id) {

        int retval;

        SQLiteDatabase db = this.db_write();

        retval = db.delete(ImagesEntry.TABLE_NAME,
                ImagesEntry._ID + " = ?",
                new String[]{id.toString()});

        return retval;
    }

    public int updatePhoto(Integer id,Utils.ESTATUS estatus) {

        int retval;

        SQLiteDatabase db = this.db_write();

        String[] args = new String[]{id.toString()};

        ContentValues values = new ContentValues();
        values.put(ImagesEntry.STATUS, estatus.toString());
        values.put(ImagesEntry.DATE, this.dateFormat.format(new Date()));

        retval = db.update(ImagesEntry.TABLE_NAME,values,ImagesEntry._ID + " = ?",args);

        return retval;

    }


    @Override
    public Object get() {

        List<PhotoContent.PhotoItem> Items= new ArrayList<PhotoContent.PhotoItem>();
        SQLiteDatabase db = this.db_read();

        String query = this.last_query();

        Cursor cursor = db.rawQuery(query,null);

        while (cursor.moveToNext()) {

            Items.add(
                    new PhotoContent.PhotoItem(Integer.parseInt(cursor.getString(0)),
                                                Integer.parseInt(cursor.getString(1)),
                                                Integer.parseInt(cursor.getString(2)),
                                                null,
                                                cursor.getString(3),
                                                cursor.getString(4),
                                                cursor.getString(5),
                                                cursor.getString(6), // STATUS
                                                cursor.getString(7)  // DATETI]ME
                    )
            );

        }

        cursor.close();
        db.close();

        return Items;

    }

    @Override
    public Object put(Object obj) throws ParseException {
        return null;
    }

    @Override
    public Object add(Object obj) throws ParseException {
        return null;
    }

    @Override
    public Object remove() throws ParseException {
        return null;
    }
}
