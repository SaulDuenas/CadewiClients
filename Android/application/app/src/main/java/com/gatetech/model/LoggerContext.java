package com.gatetech.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gatetech.content.LoggerContent;
import com.gatetech.model.sqlite.sqlbuilder;
import com.gatetech.utils.Utils;
import com.gatetech.model.sqlite.DBCadewiClients;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.gatetech.model.sqlite.DBModel.LoggerEntry;


public class LoggerContext extends sqlbuilder {

     public LoggerContext(Context context) {

         super(new DBCadewiClients(context));

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

    }

     /**
     *   SADB: Log functions
     *
     * */


     @Override
     public Object get() throws ParseException {

         List<LoggerContent.LoggerItem> LogList = new ArrayList<LoggerContent.LoggerItem>();

         String query = this.last_query();

         SQLiteDatabase db = this.db_read();

         Cursor cursor = db.rawQuery(query,null);

         while (cursor.moveToNext()) {

             LogList.add(new LoggerContent.LoggerItem(cursor.getInt(0),        // _id
                                                     cursor.getString(1),              // USER_EMAIL
                                                     cursor.getString(2),              // TYPE
                                                     cursor.getString(3),              // CATEGORY
                                                     cursor.getString(4),              // DEVICE
                                                     cursor.getString(5),              // MESSAGE
                                                     cursor.getString(6),              // STATUS
                                                     this.dateFormat.parse(cursor.getString (7)))); // DATE

         }

         cursor.close();
         db.close();

         return LogList;

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

/*
    public List<LoggerContent.LoggerItem> getlogger(String estatus,Date date) throws ParseException {

        List<LoggerContent.LoggerItem> LogList = new ArrayList<LoggerContent.LoggerItem>();

        String query = " SELECT * FROM " + LoggerEntry.TABLE_NAME +
                       " WHERE 1=1 ";

        if (estatus != null) { query = query + " AND "+ LoggerEntry.STATUS +" = '" + estatus +"'"; }
        if (date != null) {query = query +  " AND " + LoggerEntry.DATE + "> '" + dateFormat.format(date) +"'" ; }

        query = query +  " ORDER BY " + LoggerEntry.DATE + " ASC;";

        SQLiteDatabase db = this.db_read();
        Cursor cursor = db.rawQuery(query,null);

        while (cursor.moveToNext()) {

             LogList.add(new LoggerContent.LoggerItem(cursor.getInt(0),        // _id
                                             cursor.getString(1),              // USER_EMAIL
                                             cursor.getString(2),              // TYPE
                                             cursor.getString(3),              // CATEGORY
                                             cursor.getString(4),              // DEVICE
                                             cursor.getString(5),              // MESSAGE
                                             cursor.getString(6),              // STATUS
                                             this.dateFormat.parse(cursor.getString (7)))); // DATE

        }

        cursor.close();
        db.close();

        return LogList;

    }
*/

    public void updateLogger (Integer id,String userMail,String type, String category,String device,String message,Utils.ESTATUS estatus) {
         ContentValues values = new ContentValues();

         values.put(LoggerEntry.USER_EMAIL, userMail);
         values.put(LoggerEntry.TYPE, type);
         values.put( LoggerEntry.CATEGORY, category);
         values.put( LoggerEntry.DEVICE, device);
         values.put( LoggerEntry.MESSAGE, message);
         values.put( LoggerEntry.STATUS, estatus.toString());
         values.put( LoggerEntry.DATE, this.dateFormat.format(new Date()));

         SQLiteDatabase db = this.db_write();
         db.update (LoggerEntry.TABLE_NAME,values, "_id="+id.toString(),null );
         db.close();
     }



    public int depureLogger () {

        int retval;

        SQLiteDatabase db = this.db_write();

        retval = db.delete(LoggerEntry.TABLE_NAME,
                LoggerEntry.STATUS  + " = ?",
                new String[]{Utils.ESTATUS.SEND.toString() } );

        return retval;
    }


    public void insertLogger(String userMail, String type, String category, String device ,String message,String estatus) {
        ContentValues values = new ContentValues();

        values.put(LoggerEntry.USER_EMAIL, userMail);
        values.put(LoggerEntry.TYPE, type);
        values.put(LoggerEntry.CATEGORY, category);
        values.put(LoggerEntry.DEVICE, device);
        values.put(LoggerEntry.MESSAGE, message);
        values.put(LoggerEntry.STATUS, estatus);

        SQLiteDatabase db = this.db_write();
        db.insert(LoggerEntry.TABLE_NAME, null, values);
        db.close();

    }


}
