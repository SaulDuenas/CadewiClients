package com.gatetech.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.gatetech.content.UserContent;
import com.gatetech.model.sqlite.DBCadewiClients;
import com.gatetech.model.sqlite.DBModel;
import com.gatetech.model.sqlite.sqlbuilder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class UsersContext extends sqlbuilder {

    private DBCadewiClients database;

    public UsersContext(Context context) {
        super(new DBCadewiClients(context));
        database = new DBCadewiClients(context);
    }



    /**
     *   SADB: User funtions
     *
     * */
    public List<UserContent.UserItem> GetAll () {

        List<UserContent.UserItem> UserList = new ArrayList<UserContent.UserItem>();

        String query = "SELECT * FROM " + DBModel.UsersEntry.TABLE_NAME+";";
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {

            UserList.add(new UserContent.UserItem( cursor.getString(0),    // id
                                                   cursor.getString(1),    // correo
                                                   cursor.getString(2),    // nombre
                                                   cursor.getString(3),    // apellidos
                                                   cursor.getString(4),    // password
                                                   "",                       // sesionkey
                                                    "",                      // fechaHora
                                                   cursor.getString(6),    // eliminado
                                                    "",                        // usuario
                                                   cursor.getString(5))    // perfil
                        );

        }

        cursor.close();
        db.close();

        return UserList;
    }


    public UserContent.UserItem GetActiveUser () {

        UserContent.UserItem ActiveUser;

        String query = "SELECT * FROM " + DBModel.UsersEntry.TABLE_NAME + " WHERE "+DBModel.UsersEntry.ISACTIVE+"=1 LIMIT 1;";
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ActiveUser = new UserContent.UserItem(  cursor.getString(0),
                                                    cursor.getString(1),
                                                    cursor.getString(2),
                                                    cursor.getString(3),
                                                    cursor.getString(4),
                                                    "",
                                                    "",
                                                    cursor.getString(6),
                                                    "",
                                                    cursor.getString(5));
            cursor.close();
        } else {
            ActiveUser = null;
        }
        db.close();

        return ActiveUser;
    }

    public void add(String email,String name,String last_name,String password,String profile,String status,Integer isActive) {
        ContentValues values = new ContentValues();

        values.put(DBModel.UsersEntry.EMAIL, email);
        values.put(DBModel.UsersEntry.NAME, name);
        values.put( DBModel.UsersEntry.LASTNAME, last_name);
        values.put( DBModel.UsersEntry.PASSWORD, password);
        values.put( DBModel.UsersEntry.PROFILE, profile);
        values.put( DBModel.UsersEntry.STATUS, status);
        values.put( DBModel.UsersEntry.ISACTIVE, isActive);

        SQLiteDatabase db = database.getWritableDatabase();
        db.insert(DBModel.UsersEntry.TABLE_NAME, null, values);
        db.close();
    }


    public void update(Integer id,String email,String name,String last_name,String password,String profile,String status,Integer isActive) {
        ContentValues values = new ContentValues();

        values.put(DBModel.UsersEntry.EMAIL, email);
        values.put(DBModel.UsersEntry.NAME, name);
        values.put( DBModel.UsersEntry.LASTNAME, last_name);
        values.put( DBModel.UsersEntry.PASSWORD, password);
        values.put( DBModel.UsersEntry.PROFILE, profile);
        values.put( DBModel.UsersEntry.STATUS, status);
        values.put( DBModel.UsersEntry.ISACTIVE, isActive);

        SQLiteDatabase db = database.getWritableDatabase();
        db.update(DBModel.UsersEntry.TABLE_NAME, values, "_id="+id.toString(),null );
        db.close();
    }


    public int delete (UserContent.UserItem userItem){

        int retval;

        SQLiteDatabase db = database.getWritableDatabase();

        retval = db.delete(DBModel.UsersEntry.TABLE_NAME,
                DBModel.UsersEntry.EMAIL + " LIKE ?",
                new String[]{userItem.correo});


        return retval;

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
