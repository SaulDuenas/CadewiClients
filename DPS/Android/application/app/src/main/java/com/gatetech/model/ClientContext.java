package com.gatetech.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gatetech.content.ClientContent;
import com.gatetech.model.sqlite.DBCadewiClients;
import com.gatetech.model.sqlite.DBModel;
import com.gatetech.model.sqlite.sqlbuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ClientContext  extends sqlbuilder {


    /**
     *   SADB: Client functions
     *
     * */

    public ClientContext(Context context) {

        super(new DBCadewiClients(context));

        /*

        this.select(new String[] { LoggerEntry._ID,
                LoggerEntry.USER_EMAIL,
                LoggerEntry.TYPE,
                LoggerEntry.CATEGORY,
                LoggerEntry.DEVICE,
                LoggerEntry.MESSAGE,
                LoggerEntry.STATUS,
                LoggerEntry.DATE
        } )
                .from_table(LoggerEntry.TABLE_NAME);

*/

    }

    public List<ClientContent.ClientItem> getClients(String [] status) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        List<ClientContent.ClientItem> clientList = new ArrayList<>();

        String query = "SELECT * FROM " + DBModel.ClientsEntry.TABLE_NAME + " WHERE Status IN (?)";

        SQLiteDatabase db = this.db_read();

        Cursor cursor = db.rawQuery(query, status);

        while (cursor.moveToNext()) {
            try {
                clientList.add(new ClientContent.ClientItem(cursor.getInt(0),
                        cursor.getString (1),
                        cursor.getString (2),
                        cursor.getString (3),
                        cursor.getString (4),
                        null,
                        null,
                        df.parse(cursor.getString (5))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        cursor.close();
        db.close();

        return clientList;

    }


    public long insertClient (String userMail,String rfc, String Name,String Status){

        long idClient=0;

        ContentValues values = new ContentValues();

        values.put(DBModel.ClientsEntry.USER_EMAIL, userMail);
        values.put(DBModel.ClientsEntry.RFC, rfc);
        values.put(DBModel.ClientsEntry.NAME, Name);
        values.put(DBModel.ClientsEntry.STATUS, Status);

        SQLiteDatabase db = this.db_write();

        idClient = db.insert(DBModel.ClientsEntry.TABLE_NAME, null, values);
        db.close();

        return idClient;
    }



    /**
     *   SADB: Address functions
     *
     * */

    public long insertAddress (Integer client,String country, String zipcode,String street,String municipality,
                               String state,String city,String colony,String details,String Status) {

        long idAddress=0;

        ContentValues values = new ContentValues();

        values.put(DBModel.AddressEntry.CLIENT, client);
        values.put(DBModel.AddressEntry.COUNTRY, country);
        values.put(DBModel.AddressEntry.ZIPCODE, zipcode);
        values.put(DBModel.AddressEntry.STREET, street);
        values.put(DBModel.AddressEntry.MUNICIPALITY, municipality);
        values.put(DBModel.AddressEntry.STATE, state);
        values.put(DBModel.AddressEntry.CITY, city);
        values.put(DBModel.AddressEntry.COLONY, colony);
        values.put(DBModel.AddressEntry.DETAILS, details);
        values.put(DBModel.AddressEntry.STATUS, Status);

        SQLiteDatabase db = this.db_write();

        idAddress = db.insert(DBModel.AddressEntry.TABLE_NAME, null, values);
        db.close();

        return idAddress;
    }


    @Override
    public Object get() throws ParseException {
        return null;
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
