package com.gatetech.model.sqlite;

import android.provider.BaseColumns;

public class DBModel {

    public static abstract class PropertyEntry implements BaseColumns {
        public static final String TABLE_NAME ="Property";

        public static final String NAME = "Name";
        public static final String CATEGORY = "Category";
        public static final String STRING_VALUE = "String_Value";
        public static final String INTEGER_VALUE = "Integer_Value";
        public static final String DECIMAL_VALUE = "Decimal_Value";

    }



    public static abstract class LoggerEntry implements BaseColumns {
        public static final String TABLE_NAME ="Logger";

        public static final String USER_EMAIL = "UserEmail";
        public static final String TYPE = "Type"; // DEB, WAR, ERROR
        public static final String CATEGORY = "Category";
        public static final String DEVICE = "Device";
        public static final String MESSAGE = "Message";
        public static final String STATUS = "Status";
        public static final String DATE = "Date";
    }


    public static abstract class UsersEntry implements BaseColumns {
        public static final String TABLE_NAME ="Users";

        public static final String EMAIL = "Email";
        public static final String NAME = "Name";
        public static final String LASTNAME = "LastName";
        public static final String PASSWORD = "Password";
        public static final String PROFILE = "Profile";
        public static final String STATUS = "Status";
        public static final String ISACTIVE = "IsActive";

    }


    public static abstract class ClientsEntry implements BaseColumns {
        public static final String TABLE_NAME ="Clients";

        public static final String CLIENT = "Client";
        public static final String USER_EMAIL = "UserEmail";
        public static final String RFC = "Rfc";
        public static final String NAME = "Name";
        public static final String STATUS = "Status";
        public static final String DATE = "Date";
    }


    public static abstract class AddressEntry implements BaseColumns {
        public static final String TABLE_NAME ="Address";

        public static final String CLIENT = "Client";
        public static final String COUNTRY = "Country";
        public static final String ZIPCODE = "ZipCode";
        public static final String STREET = "Street";
        public static final String MUNICIPALITY = "Municipality";
        public static final String STATE = "State";
        public static final String CITY = "City";
        public static final String COLONY = "Colony";
        public static final String DETAILS = "Details";
        public static final String LONGITUDE = "Longitude";
        public static final String LATITUDE = "Latitude";
        public static final String LOCEDITABLE = "LocEditable";
        public static final String STATUS = "Status";
        public static final String DATE = "Date";
    }


    public static abstract class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME ="Contacts";

        public static final String CLIENT = "Client";
        public static final String TYPE = "Type";
        public static final String VALUE = "Value";
        public static final String STATUS = "Status";
        public static final String DATE = "Date";

    }


    public static abstract class ImagesEntry implements BaseColumns {
        public static final String TABLE_NAME ="Images";
        public static final String ADDRESS = "Address";
        public static final String CLIENT = "Client";
        public static final String PATH = "Path";
        public static final String NAME = "Name";
        public static final String NOTE = "Note";
        public static final String STATUS = "Status";
        public static final String DATE = "Date";
    }


}
