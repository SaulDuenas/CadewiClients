package com.gatetech.restserver;

import com.gatetech.restserver.Response.ClientesResponse;
import com.gatetech.restserver.Response.DirectoriosPostales;
import com.gatetech.restserver.Response.photoResponse;
import com.gatetech.restserver.Response.LoggerResponse;
import com.gatetech.restserver.Response.UsuariosResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface IApiService {

    /* *************************  User Service ************************************/

    @GET("restserver/Usuarios")
    Call<UsuariosResponse> getResponseUsuers();

    @GET("restserver/usuarios/find")
    Call<UsuariosResponse> findUsers(@Query("Search") String search);

    @POST("restserver/Usuarios")  // New User
    @FormUrlEncoded
    Call<UsuariosResponse> addNewUser(@Field("Name") String name,
                                      @Field("Last_Name") String last_name,
                                      @Field("Passw") String Password,
                                      @Field("Email") String mail,
                                      @Field("Profile") String perfil,
                                      @Field("Status") String estatus
                                    );

    @POST("restserver/Usuarios/Update")  // Update User
    @FormUrlEncoded
    Call<UsuariosResponse> updateUser(@Field("Email") String email,
                                      @Field("Name") String name,
                                      @Field("Last_Name") String last_name,
                                      @Field("Passw") String Password,
                                      @Field("Profile") String perfil,
                                      @Field("Status") String status
                                    );

    @POST("restserver/Usuarios/login")
    @FormUrlEncoded
    Call<UsuariosResponse> login(@Field("Email") String email,
                                 @Field("Passw") String password
                                );


    /* *************************  Clients Service *********************************** */

    @POST("restserver/Clientes/List")
    @FormUrlEncoded
    Call<ClientesResponse> getResponseClients(@Field("EmailAgent") String mail,
                                              @Field("Profile") String profile);

    @POST("restserver/Clientes/saveloc")
    @FormUrlEncoded
    Call<ClientesResponse> saveLocationAddress(@Field("Client") String client,
                                               @Field("Address") String address,
                                               @Field("Longitude") String longitude,
                                               @Field("Latitude") String latitude);

    @GET("restserver/Clientes/find")
    Call<ClientesResponse> getClientsbyFind(@Query("EmailAgent") String mail,
                                            @Query("Profile") String profile,
                                            @Query("Search") String search);


    @POST("restserver/Clientes/")
    @FormUrlEncoded
    Call<ClientesResponse> addNewClient(@Field("EmailAgent") String mailAgent,
                                        @Field("Name") String name,
                                        @Field("Rfc") String profile,
                                        @Field("Phone") String phone,
                                        @Field("Email") String email,
                                        @Field("Street") String street,
                                        @Field("ZipCode") String zipcode,
                                        @Field("Neighborhood") String neighborhood,
                                        @Field("Delegation") String delegation,
                                        @Field("State") String state,
                                        @Field("City") String city,
                                        @Field("Status") String status);


    @POST("restserver/Clientes/update")
    @FormUrlEncoded
    Call<ClientesResponse> updateClient(@Field("Client") String client,
                                        @Field("Address") String address,
                                        @Field("Name") String clientName,
                                        @Field("Rfc") String rfc,
                                        @Field("EmailAgent") String mailAgent,
                                        @Field("Status") String status,
                                        @Field("Phone") String phone,
                                        @Field("Email") String email,
                                        @Field("Street") String street,
                                        @Field("ZipCode") String zipcode,
                                        @Field("Neighborhood") String neighborhood,
                                        @Field("Delegation") String delegation,
                                        @Field("State") String state,
                                        @Field("City") String city,
                                        @Field("Longitude") String longitude,
                                        @Field("Latitude") String latitude,
                                        @Field("LocEditable") String LocEditable);


    /* *************************  Directorio Postal *********************************** */

    @GET("restserver/DirectorioPostal")
    Call <DirectoriosPostales> getAddress(@Query("ZipCode") String order);


    /* **********************************  Fotos  ***************************************** */

    @GET("restserver/fotos/header")
    Call <photoResponse> getPhotosbyAddress(@Query("Address") String address);


    @GET("restserver/fotos/binary")
    Call <photoResponse> getPhoto(@Query("PhotoId") String PhotoId);


    @POST("restserver/fotos/save_photo")
    @FormUrlEncoded
    Call<photoResponse> savePhoto(@Field("Address") String Address,
                                  @Field("Client") String Client,
                                  @Field("Binary") String Binary,
                                  @Field("Name") String Name,
                                  @Field("Path") String Path,
                                  @Field("Note") String Nota,
                                  @Field("Status") String status);


    @POST("restserver/fotos/upload")
    @Multipart
    /*
    Call<photoResponse> uploadFile(@Part MultipartBody.Part file,
                                   @Part("Name") RequestBody name,
                                   @PartMap() Map<String, RequestBody> partMap
                                   );
*/


     Call<photoResponse> uploadFile(@Part MultipartBody.Part file,
                                    @Part("Name") RequestBody name,
                                    @Part("Client") RequestBody ClientId,
                                    @Part("Address") RequestBody AddressId,
                                    @Part("Note") RequestBody Note,
                                    @Part("Status") RequestBody Status,
                                    @Part("Path") RequestBody Path
                                   );




    /* **********************************  Logger  ***************************************** */

    @GET("restserver/logger")
    Call<LoggerResponse> getLogger(@Query("find") String find);

    @POST("restserver/logger/save")
    @FormUrlEncoded
    Call<LoggerResponse> saveLogger(@Field("UserEmail") String Address,
                                    @Field("Type") String Client,
                                    @Field("Category") String Binary,
                                    @Field("Device") String Name,
                                    @Field("Message") String Path);

}
