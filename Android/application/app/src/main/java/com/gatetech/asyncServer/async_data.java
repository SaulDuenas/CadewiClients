package com.gatetech.asyncServer;

import android.app.Activity;
import android.content.Context;

import android.os.AsyncTask;

import com.gatetech.content.LoggerContent;
import com.gatetech.content.PhotoContent;
import com.gatetech.model.LoggerContext;
import com.gatetech.model.PhotoContext;
import com.gatetech.model.sqlite.DBModel;
import com.gatetech.utils.Internet;
import com.gatetech.utils.Utils;

import com.gatetech.restserver.Response.photoResponse;
import com.gatetech.restserver.Response.LoggerResponse;
import com.gatetech.utils.logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gatetech.utils.Utils.get_manufacturer;

public class async_data extends AsyncTask<Void, Integer, Void>  {


    private Context mContext;
    private Activity mActivity;

    private boolean Upload_Data =false;

    final PhotoContext dbImage;
    final LoggerContext dbLog;

    public enum TYPE_UPLOAD_DATA {

        INITIAL ("upload_initial"),
        LOGGER ("upload_logger"),
        PHOTO ("upload_photo");

        private final String stringValue;
        TYPE_UPLOAD_DATA(final String s) { stringValue = s; }
        public String toString() { return stringValue; }

    }

    private TYPE_UPLOAD_DATA type_upload_data;


    public async_data(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        dbImage = new PhotoContext(this.mContext);
        dbLog = new LoggerContext(this.mContext);
        this.type_upload_data=TYPE_UPLOAD_DATA.INITIAL;

    }

    @Override
    protected void onPreExecute() {


    }

    /*
     Ejecución en progreso
      */
    @Override
    protected Void doInBackground(Void... voids) {

        while(true) {

            try {

                //SADB: UPLOAD LOGGER
                if (!Upload_Data) {

                    List<oData> odataList = getoData();

                    if (!(odataList.isEmpty())) {

                        // Se cuenta con red y comunicación con el rest server ?
                        Internet.ESTATUS estatus =  Internet.isNetworkAvailable(this.mActivity,Utils.baseUrl);

                        if (estatus.equals(Internet.ESTATUS.URL_AVAILABLE)) {

                            // SADB: Disabled proccess
                            Upload_Data = true;
                            // Get the firts element to process
                            oData odataItem = odataList.get(0);

                            // SADB: 01. UPLOAD PHOTOS
                            if (odataItem.type.equals(TYPE_UPLOAD_DATA.PHOTO)) {

                                type_upload_data = TYPE_UPLOAD_DATA.PHOTO;

                                ByteArrayInputStream bs= new ByteArrayInputStream(odataItem.data); // bytes es el byte[]
                                ObjectInputStream is = new ObjectInputStream(bs);
                                PhotoContent.PhotoItem item  = ( PhotoContent.PhotoItem )is.readObject();
                                is.close();

                                // SADB: rutina de envio de la imagen por rest
                                this.upload_Photo(item.photoId,item.Client,item.Address,item.Path,item.Note);

                            }
                            // SADB: 02. UPLOAD LOGGER
                            else if (odataItem.type.equals(TYPE_UPLOAD_DATA.LOGGER)) {
                                type_upload_data = TYPE_UPLOAD_DATA.LOGGER;

                                ByteArrayInputStream bs= new ByteArrayInputStream(odataItem.data); // bytes es el byte[]
                                ObjectInputStream is = new ObjectInputStream(bs);
                                LoggerContent.LoggerItem item  = ( LoggerContent.LoggerItem )is.readObject();
                                is.close();

                               // LoggerContent.LoggerItem item = items_logger.get(0);
                                this.upload_logger(item.id, item.userMail, item.type, item.category, item.device, item.message);
                                // dbLog.updateLogger(item.id,item.userMail,item.type,item.category,item.device,item.message, Utils.ESTATUS.SENDING);
                            }
                        }

                    }
                }
            }
            catch(Exception ex) {

                String title="Error "+type_upload_data.toString()+": ";

                logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.ASYNCH_DATA.toString(),get_manufacturer(), mContext);

                Upload_Data = false;
            }

        }

    }

    /*
       Se informa en progressLabel que se canceló la tarea

        */
    @Override
    protected void onCancelled() {


    }


    /*
    Impresión del progreso en tiempo real
    */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    /*
      cuando se completo la tarea
       */
    @Override
    protected void onPostExecute(Void result) {


    }



    private void upload_logger (final Integer id, final String userMail,final String type,final String category,final String device,final String pMessage) {

        final StringBuilder message = new StringBuilder();

        final Call<LoggerResponse> uploadLogger = Utils.mApiService.saveLogger(userMail,type,category,device,pMessage);

        uploadLogger.enqueue(new Callback<LoggerResponse>() {
            @Override
            public void onResponse(Call<LoggerResponse> call, Response<LoggerResponse> response) {

                if (response.isSuccessful()) {

                    LoggerResponse logresponse = response.body();

                    if(logresponse.getEstado().equals(Utils.Status_OK)) {

                        // se actualiza el estado del logger
                        dbLog.updateLogger(id, userMail, type, category, device, pMessage, Utils.ESTATUS.SEND);

                    }
                    else {

                        logger.error(Utils.appUser.correo,"Error from server: ",logresponse.getExcepcion(),Utils.LOG_CATEGORY.ASYNCH_DATA.toString(),get_manufacturer(), mContext);
                    }
                }
                else{

                    message.append("Error HTTP: ").append(response.code()).append(" ").append( response.message());

                    logger.error(Utils.appUser.correo,"Error HTTP on upload_logger: ",message.toString(),Utils.LOG_CATEGORY.ASYNCH_DATA.toString(),get_manufacturer(), mContext);
                    dbLog.updateLogger(id, userMail, type, category, device, message.toString(), Utils.ESTATUS.ERROR_SEND);
                }
                // SADB:  Enabled Process
                Upload_Data = false;
            }

            @Override
            public void onFailure(Call<LoggerResponse> call, Throwable t) {
                message.append(t !=  null?t.getMessage():"Oops something went wrong");

                logger.error(Utils.appUser.correo,"onFailure uploadLogger.enqueue: ",message.toString(),Utils.LOG_CATEGORY.ASYNCH_DATA.toString(),get_manufacturer(), mContext);
                // SADB:  Enabled Process
                Upload_Data = false;
            }
        });

    }


    private void upload_Photo(final Integer photoId ,Integer client,Integer address,String pathfile,String note)  {

        final StringBuilder message = new StringBuilder();

        // Map is used to multipart the file using okhttp3.RequestBody
        File file = new File(pathfile);

        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        RequestBody clientId = RequestBody.create(MediaType.parse("multipart/form-data"), client.toString());
        RequestBody AddressId = RequestBody.create(MediaType.parse("multipart/form-data"), address.toString());
        RequestBody Note = RequestBody.create(MediaType.parse("multipart/form-data"), note);
        RequestBody Status = RequestBody.create(MediaType.parse("multipart/form-data"), Utils.ESTATUS.NEW.toString());
        RequestBody Path = RequestBody.create(MediaType.parse("multipart/form-data"), "");

        final Call<photoResponse> uploadPhoto = Utils.mApiService.uploadFile(fileToUpload,
                                                                             filename,
                                                                             clientId,
                                                                             AddressId,
                                                                             Note,
                                                                             Status,
                                                                             Path);





        uploadPhoto.enqueue(new Callback<photoResponse>() {
            @Override
            public void onResponse(Call<photoResponse> call, Response<photoResponse> response) {

                boolean issuccessfull = response.isSuccessful();

                if (issuccessfull) {
                    photoResponse fotoresponse = response.body();

                    if(fotoresponse.getEstado().equals(Utils.Status_OK))  {
                        // SADB: se actualiza el estado de la imagen
                        dbImage.updatePhoto(photoId,Utils.ESTATUS.SEND);

                    }
                    else{
                        message.append("Error from server: ").append(fotoresponse.getExcepcion());

                        logger.error(Utils.appUser.correo,"Error from server: ",message.toString(),Utils.LOG_CATEGORY.ASYNCH_DATA.toString(),get_manufacturer(), mContext);

                        dbImage.updatePhoto(photoId,Utils.ESTATUS.ERROR_SEND);
                    }
                }
                else {
                    message.append(response.code()).append(" ").append( response.message());

                    logger.error(Utils.appUser.correo,"Error HTTP on upload_Photo: ",message.toString(),Utils.LOG_CATEGORY.ASYNCH_DATA.toString(),get_manufacturer(), mContext);
                    dbImage.updatePhoto(photoId,Utils.ESTATUS.ERROR_SEND);
                }
                // SADB:  Enabled Process
                Upload_Data = false;
            }

            @Override
            public void onFailure(Call<photoResponse> call, Throwable t) {

                message.append((t !=  null? t.getMessage() :"Oops something went wrong"));

                logger.error(Utils.appUser.correo,"Failure uploadPhoto.enqueue: ",message.toString(),Utils.LOG_CATEGORY.ASYNCH_DATA.toString(),get_manufacturer(), mContext);
                dbImage.updatePhoto(photoId,Utils.ESTATUS.ERROR_SEND);
                // SADB:  Enabled Process
                Upload_Data = false;
            }

        });

    }


    private List<oData> getoData () throws ParseException, IOException {

        List<oData> retData = new ArrayList<oData>();

        /** SADB: get NO_SEND logger to upload  */

        Long strftime_now = new Date().getTime();
        strftime_now=strftime_now/1000;

        Integer delay = 5;   // SADB: variable set
        Long offset = new Long (60*delay);

        dbLog.clear();

        dbLog.where(DBModel.LoggerEntry.STATUS,"=",Utils.ESTATUS.NO_SEND.toString())
             .or_group_start()
             .where(DBModel.LoggerEntry.STATUS,"=",Utils.ESTATUS.ERROR_SEND.toString())
             .where("(strftime('%s',"+DBModel.LoggerEntry.DATE+ ")+"+offset.toString()+")","<", strftime_now)
             .group_end()
             .order_by(DBModel.LoggerEntry.STATUS,"DESC")
             .order_by(DBModel.LoggerEntry.DATE,"ASC");

 //       List<LoggerContent.LoggerItem> items_logger = dbLog.getlogger(Utils.ESTATUS.NO_SEND);

        List<LoggerContent.LoggerItem> items_logger = (List<LoggerContent.LoggerItem>) dbLog.get();

        for (LoggerContent.LoggerItem item:items_logger) {

            ByteArrayOutputStream bs= new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream (bs);
            os.writeObject(item);  // this es de tipo DatoUdp
            os.close();
            byte[] bytes =  bs.toByteArray(); // devuelve byte[]

            retData.add( new oData(TYPE_UPLOAD_DATA.LOGGER,item.status,bytes));
        }

        dbImage.clear();

        dbImage.where(DBModel.ImagesEntry.STATUS,"=",Utils.ESTATUS.NO_SEND.toString())
                .or_group_start()
                .where(DBModel.ImagesEntry.STATUS,"=",Utils.ESTATUS.ERROR_SEND.toString())
                .where("(strftime('%s',"+DBModel.LoggerEntry.DATE+ ")+"+offset.toString()+")","<", strftime_now)
                .group_end()
                .order_by(DBModel.ImagesEntry.STATUS,"DESC")
                .order_by(DBModel.ImagesEntry.DATE,"ASC");


        // SADB: get Photos to upload
        List<PhotoContent.PhotoItem> items_photo = (List<PhotoContent.PhotoItem>)dbImage.get();

        for (PhotoContent.PhotoItem item:items_photo) {

            ByteArrayOutputStream bs= new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream (bs);
            os.writeObject(item);  // this es de tipo DatoUdp
            os.close();
            byte[] bytes =  bs.toByteArray(); // devuelve byte[]

            retData.add( new oData(TYPE_UPLOAD_DATA.PHOTO,item.estatus.toString(),bytes));
        }


        /********** ERROR SEND **********/

        return retData;
    }


    static class  oData {

        TYPE_UPLOAD_DATA type;
        String status;
        byte[]  data;

        oData (TYPE_UPLOAD_DATA type,String status, byte[]  data ){
            this.type = type;
            this.status = status;
            this.data = data;
        }

    }

}
