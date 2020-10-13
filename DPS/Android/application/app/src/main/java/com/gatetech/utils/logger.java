package com.gatetech.utils;

import android.content.Context;

import com.gatetech.model.LoggerContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;


public class logger {

    public enum LOG_TYPE{

        ERROR ("error"),
        WARNING ("warning"),
        INFO ("info");

        private final String stringValue;
        LOG_TYPE(final String s) { stringValue = s; }
        public String toString() { return stringValue; }

    }


    static public void error(String userEmail,String title,Exception ex,String category,String manufacturer,Context context,Date date ) {

        StringBuilder message = new StringBuilder();

        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));

        message.append(title).append(errors.toString());

        final LoggerContext dbLog = new LoggerContext(context);
        dbLog.insertLogger(userEmail, LOG_TYPE.ERROR.toString(), category,manufacturer,message.toString(), Utils.ESTATUS.NO_SEND.toString(),date);
    }


    static public void error(String userEmail,String title,Exception ex,String category,String manufacturer,Context context ) {

        logger.error(userEmail,title,ex,category,manufacturer,context, new Date() );

    }

    static public void error(String userEmail,String title,String message,String category,String manufacturer,Context context,Date date ) {

        StringBuilder msg_log = new StringBuilder();

        msg_log.append(title).append(message);

        final LoggerContext dbLog = new LoggerContext(context);
        dbLog.insertLogger(userEmail, LOG_TYPE.ERROR.toString(), category,manufacturer,msg_log.toString(),Utils.ESTATUS.NO_SEND.toString(),date);

    }


    static public void error(String userEmail,String title,String message,String category,String manufacturer,Context context ) {

        error(userEmail,title,message,category,manufacturer,context,new Date() );
    }

    static public void war(String userEmail,String title,Exception ex,String category,String manufacturer,Context context ) {

        StringBuilder message = new StringBuilder();

        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));

        message.append(title).append(errors.toString());

        final LoggerContext dbLog = new LoggerContext(context);
        dbLog.insertLogger(userEmail, LOG_TYPE.WARNING.toString(), category,manufacturer,message.toString(), Utils.ESTATUS.NO_SEND.toString());

    }

    static public void war(String userEmail,String title,String message,String category,String manufacturer,Context context ) {

        StringBuilder msg_log = new StringBuilder();

        msg_log.append(title).append(message);

        final LoggerContext dbLog = new LoggerContext(context);
        dbLog.insertLogger(userEmail, LOG_TYPE.WARNING.toString(), category,manufacturer,msg_log.toString(),Utils.ESTATUS.NO_SEND.toString());

    }


    static public void info(String userEmail,String title,Exception ex,String category,String manufacturer,Context context ) {

        StringBuilder message = new StringBuilder();

        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));

        message.append(title).append(errors.toString());

        final LoggerContext dbLog = new LoggerContext(context);
        dbLog.insertLogger(userEmail, LOG_TYPE.INFO.toString(), category,manufacturer,message.toString(), Utils.ESTATUS.NO_SEND.toString());

    }

    static public void info(String userEmail,String title,String message,String category,String manufacturer,Context context )  {

        StringBuilder msg_log = new StringBuilder();

        msg_log.append(title).append(message);

        final LoggerContext dbLog = new LoggerContext(context);
        dbLog.insertLogger(userEmail, LOG_TYPE.INFO.toString(), category,manufacturer,msg_log.toString(),Utils.ESTATUS.NO_SEND.toString());

    }

}
