package com.gatetech.asyncServer;

import android.app.Activity;

import com.gatetech.restserver.Response.ClientesResponse;
import com.gatetech.utils.Utils;
import com.gatetech.utils.logger;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gatetech.utils.Utils.get_manufacturer;

public class DataClientRestService extends  RestService {

    static DataClientRestService.OnRestServiceClientListener Clientlistener;

    static public void setClientListener(DataClientRestService.OnRestServiceClientListener listener) {
        DataClientRestService.Clientlistener=listener;
    }


    static public void saveClient(final Activity activity,
                                  String mailAgent,
                                  String name,
                                  String profile,
                                  String phone,
                                  String email,
                                  String street,
                                  String zipcode,
                                  String neighborhood,
                                  String delegation,
                                  String state,
                                  String city) {


        final StringBuilder message  = new StringBuilder();

        // SADB: Call to REST API
        final Call<ClientesResponse> callSaveData = Utils.mApiService.addNewClient(mailAgent,
                                                                                    name,
                                                                                    profile,
                                                                                    phone,
                                                                                    email,
                                                                                    street,
                                                                                    zipcode,
                                                                                    neighborhood,
                                                                                    delegation,
                                                                                    state,
                                                                                    city,
                                                                                    Utils.ESTATUS.NEW.toString()
                                                                                    );
        callSaveData.enqueue( new Callback<ClientesResponse>() {
            @Override
            public void onResponse(Call<ClientesResponse> call, Response<ClientesResponse> response) {

                if (response.isSuccessful()) {

                    ClientesResponse cln = response.body();

                    if(cln.getEstado().equals(Utils.Status_OK))  {

                        logger.info(Utils.appUser.correo, "onResponse Status_OK: ", message.toString(), Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);
                        // Clientlistener.onDataRestClientResponse(Action.SAVECLIENT,Status.SUCCESSFUL,message.toString(),null);

                        onDataRestClientResponse(cln.getExcepcion(), RestService.Status.SUCCESSFUL);

                    }
                    else if (cln.getEstado().equals(Utils.Status_FAIL)) {

                        message.append(cln.getExcepcion());
                        logger.error(Utils.appUser.correo, "onResponse Status_FAIL: ", cln.getExcepcion(), Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);
                        //   Clientlistener.onDataRestClientResponse(Action.SAVECLIENT,Status.FAILRESPONSE,message.toString(),null);
                        onDataRestClientResponse(message.toString(), RestService.Status.FAILRESPONSE);
                    }
                }
                else  {

                    message.append("Error HTTP: ").append(response.code()).append(" ").append( response.message());
                    logger.error(Utils.appUser.correo, "Error HTTP: ", message.toString(), Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);

                    onDataRestClientResponse(message.toString(), RestService.Status.FAILRESPONSE);
                }

            }

            @Override
            public void onFailure(Call<ClientesResponse> call, Throwable t) {

                String title = "Failure updateClient.enqueue: ";
                String msg = (t != null ? t.getMessage() : "Oops something went wrong");

                logger.error(Utils.appUser.correo, title, msg, Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);

                onDataRestClientResponse(title + msg, RestService.Status.FAILURE);

            }


            private void onDataRestClientResponse(String message,RestService.Status status) {
                if (Clientlistener != null) {
                    Clientlistener.onDataRestClientResponse(RestService.Action.SAVECLIENT, status,message, (ClientesResponse.Cliente) null);
                }
            }

        } );

    }


    static public  void updateClient (final Activity activity,
                                      final String client,
                                      String address,
                                      String clientName,
                                      String rfc,
                                      String mailAgent,
                                      String phone,
                                      String email,
                                      String street,
                                      String zipcode,
                                      String neighborhood,
                                      String delegation,
                                      String state,
                                      String city,
                                      String longitude,
                                      String latitude,
                                      String LocEditable,
                                      String status) {

        // SADB: Call to REST API
        final Call<ClientesResponse> callupdateClient = Utils.mApiService.updateClient(client,
                                                                                       address,
                                                                                       clientName,
                                                                                       rfc,
                                                                                       mailAgent,
                                                                                       status,
                                                                                       phone,
                                                                                       email,
                                                                                       street,
                                                                                       zipcode,
                                                                                       neighborhood,
                                                                                       delegation,
                                                                                       state,
                                                                                       city,
                                                                                       longitude,
                                                                                       latitude,
                                                                                       LocEditable
                                                                                    );

        callupdateClient.enqueue(new Callback<ClientesResponse>() {

            StringBuilder message = new StringBuilder();

            @Override
            public void onResponse(Call<ClientesResponse> call, Response<ClientesResponse> response) {

                if (response.isSuccessful()) {

                    ClientesResponse cln = response.body();
                    if (cln.getEstado() == Utils.Status_OK) {
                        message.append("Cliente actualizado satisfactoriamente");

                        logger.info(Utils.appUser.correo, "onResponse Status_OK: ", message.toString(), Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);
                        onDataRestClientResponse(message.toString(),RestService.Status.SUCCESSFUL,cln.getCliente(client));

                    } else if (cln.getEstado() == Utils.Status_FAIL) {

                        message.append("updateClient.onResponse Status_FAIL: ");
                        message.append(cln.getExcepcion());

                        logger.error(Utils.appUser.correo, "updateClient.onResponse Status_FAIL: ", cln.getExcepcion(), Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);

                        onDataRestClientResponse(message.toString(),RestService.Status.FAILRESPONSE,null);
                    }
                } else {

                    message.append("Error HTTP: ").append(response.code()).append(" ").append(response.message());

                    logger.error(Utils.appUser.correo, "Error HTTP: ", message.toString(), Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);
                    onDataRestClientResponse(message.toString(),RestService.Status.FAILRESPONSE,null);

                }

            }

            @Override
            public void onFailure(Call<ClientesResponse> call, Throwable t) {

                String title = "Failure updateClient.enqueue: ";
                String msg = (t != null ? t.getMessage() : "Oops something went wrong");

                logger.error(Utils.appUser.correo, title, msg, Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);
                onDataRestClientResponse(msg,RestService.Status.FAILURE,null);

            }

            private void onDataRestClientResponse(String message,RestService.Status status,ClientesResponse.Cliente clientItem) {
                if (Clientlistener != null) {
                    Clientlistener.onDataRestClientResponse(RestService.Action.UPDATECLIENT, status,message,clientItem);
                }
            }

        });

    }


    public static void findClients(final Activity activity, final String Email, String profile, String token) {

        final StringBuilder message = new StringBuilder();

        // SADB: Call to REST API
        final Call<ClientesResponse> callClients = Utils.mApiService.getClientsbyFind( Email,profile,token.trim());
        callClients.enqueue(new Callback<ClientesResponse>() {

            @Override
            public void onResponse(Call<ClientesResponse> call, Response<ClientesResponse> response) {

                if (response.isSuccessful()) {

                    ClientesResponse clientes = new ClientesResponse();
                    clientes = response.body();
                    RestService.Status status = Status.INITIAL;


                    List<ClientesResponse.Cliente> clientList = null;

                    if (clientes.getEstado().equals(Utils.Status_OK)) {

                       clientList = (clientes.getClientes() != null)?clientes.getClientes(): new ArrayList<ClientesResponse.Cliente>();
                            //logger.info(Utils.appUser.correo, "onResponse Status_OK: ", message.toString(), Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);

                       message.append((clientes.getClientes() != null)?"": "No se encontraron clientes con el criterio de busqueda");

                       status = RestService.Status.SUCCESSFUL;
                    }
                    else if (clientes.getEstado().equals(Utils.Status_FAIL)) {

                        message.append("findClients.onResponse Status_FAIL: ");
                        message.append(clientes.getExcepcion());

                        logger.error(Utils.appUser.correo, "updateClient.onResponse Status_FAIL: ", clientes.getExcepcion(), Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);
                        status = RestService.Status.FAILRESPONSE;

                    }

                    onDataRestClientResponse(message.toString(),status,clientList);
                }
                else  {

                    message.append("Error HTTP: ").append(response.code()).append(" ").append(response.message());

                    logger.error(Utils.appUser.correo, "Error HTTP: ", message.toString(), Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);
                    onDataRestClientResponse(message.toString(),RestService.Status.FAILRESPONSE,null);

                }
            }

            @Override
            public void onFailure(Call<ClientesResponse> call, Throwable t) {

                String title = "Failure findClients.enqueue: ";
                String msg = (t != null ? t.getMessage() : "Oops something went wrong");

                logger.error(Email, title, msg, Utils.LOG_CATEGORY.CLIENT.toString(), get_manufacturer(), activity);
                onDataRestClientResponse(msg,RestService.Status.FAILURE,null);

            }

            private void onDataRestClientResponse(String message,RestService.Status status,List<ClientesResponse.Cliente> clientItemList) {
                if (Clientlistener != null) {
                    Clientlistener.onDataRestClientResponse(Action.FINDCLIENTS, status,message,clientItemList);
                }
            }

        } );

    }


    public interface OnRestServiceClientListener {
        void onDataRestClientResponse(Action action , RestService.Status result, String message, ClientesResponse.Cliente clientItem);

        void onDataRestClientResponse(Action action , RestService.Status result, String message, List<ClientesResponse.Cliente> clientItemList);
    }



}
