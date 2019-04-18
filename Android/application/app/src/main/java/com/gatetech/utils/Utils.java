package com.gatetech.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;

import com.gatetech.content.AddressContent;
import com.gatetech.content.ClientContent;
import com.gatetech.content.ContactContent;
import com.gatetech.content.PhotoContent;
import com.gatetech.content.UserContent;
import com.gatetech.model.sqlite.DBModel;
import com.gatetech.model.PhotoContext;

import com.gatetech.restserver.ApiAdapter;
import com.gatetech.restserver.IApiService;
import com.gatetech.restserver.Response.ClientesResponse;
import com.gatetech.restserver.Response.photoResponse;
import com.gatetech.restserver.Response.UsuariosResponse;

import java.util.ArrayList;
import java.util.List;


public final class Utils {

    public enum ESTATUS {

        NEW ("nuevo"),
        DELETED ("eliminado"),
        MODIFY ("modificado"),
        SEND ("send"),
        SENDING ("sending"),
        NO_SEND("no_send"),
        ERROR_SEND ("error_send");

        private final String stringValue;
        ESTATUS(final String s) { stringValue = s; }
        public String toString() { return stringValue; }

    }

    static public UserContent.UserItem appUser;

    static public final int Status_OK = 200;
    static public final int Status_FAIL = 400;

    static public String STR_001="";

    static public String baseUrl = "http://www.cadewigroup.com";


    // SADB: Variables para las fotos e imagenes
    public static Bitmap magicalCameraBitmap = null;
    public static String mphotoPath = null;
    public static final int BITMAP = 0;
    public static final int URIPATH = 1;

    static public IApiService mApiService = ApiAdapter.getApiService(baseUrl);

    static public String[] PERMISOS = {Manifest.permission.READ_EXTERNAL_STORAGE,
                                       Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                       Manifest.permission.CAMERA,
                                       Manifest.permission.ACCESS_NETWORK_STATE,
                                       Manifest.permission.INTERNET,
                                       Manifest.permission.ACCESS_FINE_LOCATION,
                                       Manifest.permission.ACCESS_COARSE_LOCATION};

    public enum LOG_CATEGORY {

        MAPS ("maps"),
        USER ("user"),
        CLIENT ("Clients"),
        PHOTO ("photo"),
        GENERAL ("general"),
        ASYNCH_DATA ("asynch_data");

        private final String stringValue;
        LOG_CATEGORY(final String s) { stringValue = s; }
        public String toString() { return stringValue; }

    }



    static public String get_manufacturer () {

        StringBuilder device = new  StringBuilder();

        device.append("Manufacturer: ").append(Build.MANUFACTURER ).append("/n");
        device.append("Brand: ").append(Build.BRAND ).append("/n");
        device.append("Model: ").append(Build.MODEL ).append("/n");
        device.append("Release: ").append(Build.VERSION.RELEASE );

        return  device.toString();

    }


    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public static String app_version(Context context) throws PackageManager.NameNotFoundException {
        String version="0.0";

        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
        version = packageInfo.versionName;

        return version;

    }


    static public UserContent.UserItem getUserItem(UsuariosResponse.Usuario user) {
        UserContent.UserItem usrObj = new UserContent.UserItem("0",
                                                               user.getCorreo(),
                                                               user.getNombre(),
                                                               user.getApellidos(),
                                                               user.getPassword(),
                                                               user.getSesionKey() == null ? "" : user.getSesionKey().toString(),
                                                               user.getFechaHora(),
                                                               user.getEliminado(),
                                                               user.getUsuario(),
                                                               user.getPerfil() == null ? "" : user.getPerfil().toString()
                                                            );

        return usrObj;
    }

    static public void fillUserContent (UserContent contUser, List<UsuariosResponse.Usuario> users){

        contUser.ITEM_MAP.clear();
        contUser.ITEMS.clear();

        for (UsuariosResponse.Usuario usr : users) {

            UserContent.UserItem user =  Utils.getUserItem(usr);
            contUser.addItem(user);
        }
    }

    static public void fillClientContent (ClientContent conClient, List<ClientesResponse.Cliente> list){

        conClient.ITEM_MAP.clear();
        conClient.ITEMS.clear();

        if (list != null && list.size()>0){
            for (ClientesResponse.Cliente rw : list) {
                AddressContent objAddress = new AddressContent();
                // Get Address
                if(rw.getDireccion() != null && rw.getDireccion().size()>0) {

                    for(ClientesResponse.Direccion drw :rw.getDireccion())

                        objAddress.addItem( new AddressContent.AddressItem( Integer.parseInt(drw.getDireccion()),
                                                                            drw.getCalle(),
                                                                            drw.getCodigoPostal(),
                                                                            drw.getColonia(),
                                                                            drw.getMunicipio(),
                                                                            drw.getEstado(),
                                                                            drw.getCiudad() ,
                                                                            drw.getLongitud().toString(),
                                                                            drw.getLatitud().toString(),
                                                                            Integer.parseInt(drw.getLocEditable().toString())) );
                }

                ContactContent objContact = new ContactContent();
                // Get Contacts
                if(rw.getContacto() != null && rw.getContacto().size()>0) {

                    for(ClientesResponse.Contacto crw :rw.getContacto()){
                        objContact.addItem( new ContactContent.ContactItem( Integer.parseInt(crw.getContacto()),crw.getTipo(),crw.getValor() ) );
                    }

                }

                ClientContent.ClientItem clnObj = new ClientContent.ClientItem(Integer.parseInt(rw.getCliente()),
                                                                               rw.getCorreoAgente(),
                                                                               rw.getRfc(),
                                                                               rw.getNombre(),
                                                                               ESTATUS.SEND.toString(),
                                                                               objAddress,
                                                                               objContact,
                                                                               null);

                conClient.addItem (clnObj);
            }
        }

    }

    /**   getPtohoItemList
     *    SQLITE -> List<PhotoContent.PhotoItem>
     * */
    static public List<PhotoContent.PhotoItem> getPtohoItemList(Integer addressId, Context context) {
        List<PhotoContent.PhotoItem>  items = new ArrayList<PhotoContent.PhotoItem>();

        // SADB: Obtenemos el listado de base de datos local
        PhotoContext dbImage = new PhotoContext(context);

        dbImage.clear();

        dbImage.where_in (DBModel.ImagesEntry.STATUS,new String[]{Utils.ESTATUS.NO_SEND.toString(),Utils.ESTATUS.ERROR_SEND.toString()} )
               .order_by(DBModel.ImagesEntry.DATE,"DESC");

        List<PhotoContent.PhotoItem> dbitems = (List<PhotoContent.PhotoItem>)dbImage.get();

        for (PhotoContent.PhotoItem item : dbitems) {

            PhotoContent.PhotoItem photoObj = new PhotoContent.PhotoItem(item.photoId,
                                                                        item.Address,
                                                                        item.Client,
                                                                        null,
                                                                        item.Name,
                                                                        item.Path,
                                                                        item.Note,
                                                                        item.estatus,
                                                                        item.DateTime);

            items.add(photoObj);

        }

        return items;

    }

    /**   getPtohoItemList
     *    List<photoResponse.photoItem>  -> List<PhotoContent.PhotoItem>
     * */
    static public List<PhotoContent.PhotoItem>  getPhotoItemList (List<photoResponse.photoItem> list) {

        List<PhotoContent.PhotoItem>  items = new ArrayList<PhotoContent.PhotoItem>();

        // SADB: Obtenemos el listado de servidor

        if (list != null && list.size()>0){
            for (photoResponse.photoItem rw : list) {

                PhotoContent.PhotoItem photoObj = new PhotoContent.PhotoItem(Integer.parseInt(rw.getImagen()),
                                                                             Integer.parseInt(rw.getDireccion()),
                                                                             Integer.parseInt(rw.getCliente()),
                                                                             rw.getBynary(),
                                                                             rw.getNombre(),
                                                                             rw.getRuta(),
                                                                             rw.getNota(),
                                                                             ESTATUS.SEND.toString(),
                                                                             rw.getFechaHora());

                items.add(photoObj);
            }
        }

        return items;
    }


}
