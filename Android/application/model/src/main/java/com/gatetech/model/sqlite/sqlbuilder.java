package com.gatetech.model.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public abstract class sqlbuilder {

    StringBuilder query = new StringBuilder();
    String select_columns="";
    String pivot_table="";

    List<query_join> joins = new ArrayList<query_join>();
    List<query_arguments> arguments = new ArrayList<query_arguments>();
    List<query_order> orders = new ArrayList<query_order>();

    static final String WHERE = "WHERE";
    final String DELIMITER= ",";

    boolean is_group_start = false;

    private SQLiteOpenHelper database;

    private SQLiteDatabase db;


    public void clear() {
        joins.clear();
        arguments.clear();
        orders.clear();
    }

    public SQLiteDatabase  db_read () {
        db = database.getReadableDatabase();
        return this.db;
    }


    public SQLiteDatabase  db_write () {
       db = database.getWritableDatabase();
       return this.db;
    }

    public void close() {
        this.db.close();
    }

    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    public sqlbuilder(SQLiteOpenHelper sqlhelper ) {
        database = sqlhelper;
    }


    public String last_query () {

        //delete all contents from previous iteration
        this.query.delete(0,  this.query.length());

        query.append("SELECT ").append(select_columns).append("\n")
                .append("FROM ").append(pivot_table).append("\n");

        // build joins
        for (query_join join:joins) {

            query.append(join.type).append(" JOIN").append(join.table).append(" ").append("(").append(join.criterial).append(")").append("\n");

        }

        // build arguments
        for (query_arguments arg:arguments) {

            if (arg.condition.trim().contains("IN")) {
                query.append(arg.type).append(arg.column).append(arg.condition).append("(").append(arg.value).append(")").append("\n");
            }
            else if (arg.condition.trim().contains("(")) {

            }

            else {
                query.append(arg.type).append(arg.column).append(arg.condition).append(arg.value).append("\n");
            }
        }

        // build order arguments

        final int length = orders.size();
        if (length == 0) {
            return query.toString();
        }

        query.append("ORDER BY ").append(orders.get(0).column).append(" ").append(orders.get(0).type);
        for (int i = 1; i < length; i++) {
            query.append(DELIMITER);
            query.append(orders.get(i).column).append(" ").append(orders.get(i).type);

        }

        return query.toString();
    }



    public sqlbuilder select (String columns) {

        this.select_columns = columns;
        return this;
    }


    public sqlbuilder select (String[] columns) {

        final String lcolumns = this.join_arguments(DELIMITER,columns);
        this.select_columns = lcolumns;
        return this;
    }

    public sqlbuilder from_table (String table) {

        this.pivot_table = table;
        return this;
    }


    public sqlbuilder order_by (String column,String type) {



        orders.add(new query_order(column,type));
        return this;
    }

    public sqlbuilder join (String table, String criterial,String type) {

        return addItem_join(table,criterial,type);

    }


    public sqlbuilder group_start () {

        String type = check_where_arg() + " ( ";

        this.is_group_start = true;
        return addItem_argument(type,"","",null);
    }

    public sqlbuilder or_group_start () {

        this.is_group_start = true;
        return addItem_argument("OR (","","",null);
    }

    public sqlbuilder group_end () {

        return addItem_argument(")","","",null);
    }

    public sqlbuilder where (String column,String condition,Object value) {

        String type = "";

        if ( this.is_group_start ) {
            type= "";
            this.is_group_start = false;
        }else {
            type = check_where_arg();
        }


        return addItem_argument(type,column,condition,value);

    }

    public sqlbuilder  where_not (String column,String condition,Object value) {

        String type = check_where_arg();

        return addItem_argument(type + "NOT ",column,condition,value);

    }



    public sqlbuilder  or_where (String column,String condition,Object value) {

        return addItem_argument("OR ",column,condition,value);

    }

    public sqlbuilder  or_where_not (String column,String condition,Object value) {

        return addItem_argument("OR NOT ",column,condition,value);

    }

    public sqlbuilder where_in (String column,Object[] values) {

        String type = check_where_arg();

        return addItem_argument(type,column," IN ",values);
    }

    public sqlbuilder where_not_in (String column,Object[] values) {

        String type = check_where_arg();

        return addItem_argument(type,column," NOT IN ",values);
    }

    public sqlbuilder or_where_in (String column,Object[] values) {

        return addItem_argument("OR ",column," IN ",values);

    }

    public sqlbuilder or_where_not_in (String column,Object[] values) {

        return addItem_argument("OR ",column," NOT IN ",values);

    }



    public sqlbuilder  like (String column,Object value) {

        String type = check_where_arg();

        return addItem_argument(type,column," LIKE ",value);

    }

    public sqlbuilder  like_not (String column,Object value) {

        String type = check_where_arg();

        return addItem_argument(type + " NOT ",column," LIKE ",value);

    }

    public sqlbuilder  or_like (String column,Object value) {

        return addItem_argument(" OR ",column," LIKE ",value);

    }

    public sqlbuilder  or_like_not (String column,Object value) {

        return addItem_argument(" OR NOT ",column," LIKE ",value);

    }


    private String check_where_arg() {

        String type = WHERE + " ";

        for (query_arguments arg:arguments) {
            if (arg.type.trim().contains(WHERE)) {
                type = "AND ";
                break;
            }
        }

        return type;
    }


    private static String join_arguments(@NonNull CharSequence delimiter,@NonNull CharSequence quota, @NonNull Object tokens) {

        final Object[] tokensls = (Object[]) tokens;

        final int length = tokensls.length;
        if (length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(quota).append(tokensls[0]).append(quota);
        for (int i = 1; i < length; i++) {
            sb.append(delimiter);
            sb.append(quota);
            sb.append(tokensls[i]);
            sb.append(quota);
        }
        return sb.toString();
    }

    private static String join_arguments(@NonNull CharSequence delimiter, @NonNull Object tokens) {

        final Object[] tokensls = (Object[]) tokens;

        final int length = tokensls.length;
        if (length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(tokensls[0]);
        for (int i = 1; i < length; i++) {
            sb.append(delimiter);
            sb.append(tokensls[i]);
        }
        return sb.toString();
    }


    private String where_in_args (Object values) {

        String value_list="";

        if (values instanceof String[] || values instanceof Character[] ||values instanceof Date[]) {
            value_list = this.join_arguments(DELIMITER,"'",values);
        }

        else if (values instanceof Byte[]) {
            value_list = this.join_arguments(DELIMITER,values);
        }

        else if (values instanceof Integer[]) {
            value_list = this.join_arguments(DELIMITER,values);
        }

        else if (values instanceof Long[]) {
            value_list = this.join_arguments(DELIMITER,values);
        }

        else if (values instanceof Float[]) {
            value_list = this.join_arguments(DELIMITER,values);
        }
        else if (values instanceof Double[]) {
            value_list = this.join_arguments(DELIMITER,values);
        }

        else if (values == null) {
            value_list = "";
        }

        else {
            value_list = "";
        }

        return value_list;

    }

    private String prepared_value (Object value) {

        String ret_value="";

        if (value instanceof String || value instanceof Character ||value instanceof Date) {
            ret_value = "'"+value.toString()+"'";
        }

        else if (value instanceof Byte) {
            ret_value = value.toString();
        }

        else if (value instanceof Integer) {
            ret_value = value.toString();
        }

        else if (value instanceof Long) {
            ret_value = value.toString();
        }

        else if (value instanceof Float) {
            ret_value = value.toString();
        }
        else if (value instanceof Double) {
            ret_value = value.toString();
        }

        else {
            ret_value = where_in_args (value);
        }

        return ret_value;

    }


    public sqlbuilder addItem_argument (String type,String column,String condition,Object value) {

        arguments.add(new query_arguments(type,column,condition,prepared_value(value)));
        return this;
    }


    public sqlbuilder addItem_join (String table,String criterial,String type) {
        joins.add(new query_join(table,criterial,type));
        return this;
    }


    public abstract Object get() throws ParseException;

    public abstract Object put(Object obj) throws ParseException;

    public abstract Object add(Object obj) throws ParseException;

    public abstract Object remove() throws ParseException;


    static class query_join {

        public String table="";
        public String criterial="";
        public String type="";

        public query_join(String table, String criterial,String type) {
            this.table = table;
            this.criterial = criterial;
            this.type = type;
        }

    }

    static class query_arguments {

        String type ="";
        String condition = "";
        String column = "";
        String value="";

        List<query_arguments> arguments = new ArrayList<query_arguments>();

        public void add(query_arguments arg) {
            arguments.add(arg);
        }

        public query_arguments (String type,String column,String condition,String value) {

            this.type = type;
            this.column = column;
            this.condition = condition;
            this.value = value;

        }

        public String check_argument () {
            String type = WHERE + " ";

            for (query_arguments arg : arguments) {
                if (arg.type.trim().contains(WHERE)) {
                    type = "AND ";
                    break;
                }


            }

            return type;
        }
    }

    static class query_order {

        String column = "";
        String type = "";

        public query_order (String column ,String type) {

            this.column = column;
            this.type = type;

        }
    }

    static class values {

        String key="";
        String value="";

        public values (String key ,String value) {

            this.key = key;
            this.value = value;

        }

    }

}
