package com.gatetech.asyncServer;

import android.app.Activity;

import com.gatetech.restserver.Response.photoResponse;
import com.gatetech.utils.Utils;
import com.gatetech.utils.logger;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gatetech.utils.Utils.get_manufacturer;

public class DataImageRestService {



    static OnRestServiceImageListener Imagelistener;



    static public void setImageListener(OnRestServiceImageListener listener) {
        DataImageRestService.Imagelistener=listener;
    }




    static public void getPhotos(final Activity activity, Integer AddressId) {

        final StringBuilder message = new StringBuilder();
        // SADB: get Photos from RestServer
        final Call<photoResponse> callFotos = Utils.mApiService.getPhotosbyAddress( AddressId.toString() );
        callFotos.enqueue(new Callback<photoResponse>() {
            @Override
            public void onResponse(Call<photoResponse> call, Response<photoResponse> response) {


                if (response.isSuccessful()) {
                    photoResponse photoresponse = new photoResponse();
                    photoresponse = response.body();

                    if(photoresponse.getEstado().equals(Utils.Status_OK)) {
                        //photoresponse.getPhotoItems();
                        //message.append("");
                        logger.info(Utils.appUser.correo, "getPhotos.onResponse Status_OK: ",photoresponse.getExcepcion() , Utils.LOG_CATEGORY.PHOTO.toString(), get_manufacturer(), activity);
                        onDataRestImageResponse(message.toString(),RestService.Status.SUCCESSFUL,photoresponse.getPhotoItems());

                    } else if (photoresponse.getEstado() == Utils.Status_FAIL) {

                        message.append("updateClient.onResponse Status_FAIL: ");
                        message.append(photoresponse.getExcepcion());

                        logger.error(Utils.appUser.correo, "getPhotos.onResponse Status_FAIL: ", photoresponse.getExcepcion(), Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);

                        onDataRestImageResponse(message.toString(),RestService.Status.FAILRESPONSE,null);
                    }
                } else {

                    message.append("Error HTTP: ").append(response.code()).append(" ").append(response.message());

                    logger.error(Utils.appUser.correo, "Error HTTP: ", message.toString(), Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);
                    onDataRestImageResponse(message.toString(),RestService.Status.FAILRESPONSE,null);

                }

            }

            @Override
            public void onFailure(Call<photoResponse> call, Throwable t) {

                String title = "Failure updateClient.enqueue: ";
                String msg = (t != null ? t.getMessage() : "Oops something went wrong");

                logger.error(Utils.appUser.correo, title, msg, Utils.LOG_CATEGORY.PHOTO.toString(), get_manufacturer(), activity);
                onDataRestImageResponse(title + msg,RestService.Status.FAILURE,null);

            }


            private void onDataRestImageResponse(String message, RestService.Status status, List<photoResponse.photoItem> photoList) {
                if (Imagelistener != null) {
                    Imagelistener.onDataRestImageResponse(RestService.Action.GETIMAGES, status,message,photoList);
                }
            }

        });

    }




    public interface OnRestServiceImageListener {
        void onDataRestImageResponse(RestService.Action action , RestService.Status result, String message, List<photoResponse.photoItem> photoList);
    }
}
