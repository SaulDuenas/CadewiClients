package com.gatetech.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoggerContent {

    public static final List<LoggerItem> ITEMS = new ArrayList<LoggerItem>();


    public static void addItem(LoggerItem item) {
        ITEMS.add(item);
    }


    public static class LoggerItem implements Serializable {

        public final Integer id;
        public final String userMail;
        public final String type;
        public final String category;
        public final String message;
        public final String device;

        public final String status;
        public final Date date;


        public LoggerItem(Integer id,String userMail,String type,String category,String device,String message,String status,Date date){

            this.id = id;
            this.userMail = userMail;
            this.type=type;
            this.category=category;
            this.message=message;
            this.device=device;
            this.status=status;
            this.date=date;
        }
    }

}
