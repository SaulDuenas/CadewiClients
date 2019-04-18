package com.gatetech.content;

import com.gatetech.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PhotoContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<PhotoItem> ITEMS = new ArrayList<PhotoItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, PhotoItem> ITEM_MAP = new HashMap<String, PhotoItem>();

   // private static final int COUNT = 25;

   // static {
   //     // Add some sample items.
   //     for (int i = 1; i <= COUNT; i++) {
   //         addItem(createPhotoItem(i));
   //     }
   // }

    public void addItem(PhotoItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.photoId.toString(), item);
    }

    private static PhotoItem createPhotoItem(int position) {
        return new PhotoItem(position, 0,0,null,"","","",Utils.ESTATUS.NO_SEND.toString(),"" );
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class PhotoItem implements Serializable {

        public final Integer photoId;
        public final Integer Address;
        public final Integer Client;
        public final Object Bynary;
        public final String Name;
        public final String Path;
        public final String Note;
        public final String DateTime;
        public final String estatus;

        public PhotoItem(Integer photoId, Integer Address,Integer Client,Object Bynary, String Name, String Path, String Note,String  estatus, String DateTime) {

            this.photoId = photoId;
            this.Address=Address;
            this.Client = Client;
            this.Bynary=Bynary;
            this.Name=Name;
            this.Path=Path;
            this.Note = (Note==null)?"":Note;
            this.DateTime=DateTime;
            this.estatus = estatus;
        }
    }
}
