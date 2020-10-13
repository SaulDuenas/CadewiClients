package com.gatetech.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gatetech.model.sqlite.DBCadewiClients;
import com.gatetech.model.sqlite.DBModel;


public class PropertyContext {

    private DBCadewiClients database;


    public PropertyContext(Context context ) {
        database = new DBCadewiClients(context);
    }


    public void insertProperty(String Name, String Category, String sValue, Integer iValue, Double dValue) {

        ContentValues values = new ContentValues();

        values.put(DBModel.PropertyEntry.NAME, Name);
        values.put( DBModel.PropertyEntry.CATEGORY, Category);
        values.put( DBModel.PropertyEntry.STRING_VALUE, sValue);
        values.put( DBModel.PropertyEntry.INTEGER_VALUE, iValue);
        values.put( DBModel.PropertyEntry.DECIMAL_VALUE, dValue);

        SQLiteDatabase db = database.getWritableDatabase();
        db.insert(DBModel.PropertyEntry.TABLE_NAME, null, values);
        db.close();

    }


    public String getStringValue(String Name) {

        String retValue = null;

        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM ")
                .append(DBModel.PropertyEntry.TABLE_NAME)
                .append(" WHERE Name IN (?)");

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.rawQuery(query.toString(), new String[]{Name});

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                retValue = cursor.getString(3); // sValue
            }

            cursor.close();
            db.close();
        }

        return retValue;
    }


    public Integer getIntegerValue(String Name) {

        Integer retValue = null;

        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM ")
                .append(DBModel.PropertyEntry.TABLE_NAME)
                .append(" WHERE Name IN (?)");

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.rawQuery(query.toString(), new String[]{Name});

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                retValue = cursor.getInt(4); // iValue
            }

            cursor.close();
            db.close();
        }

        return retValue;
    }

    public Double getDecimalValue(String Name) {

        Double retValue = null;

        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM ")
                .append(DBModel.PropertyEntry.TABLE_NAME)
                .append(" WHERE Name IN (?)");

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.rawQuery(query.toString(), new String[]{Name});

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                retValue = cursor.getDouble (5); // iValue
            }

            cursor.close();
            db.close();
        }

        return retValue;
    }

}
