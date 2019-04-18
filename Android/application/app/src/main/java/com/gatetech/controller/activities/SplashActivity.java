package com.gatetech.controller.activities;
/*
The MIT License (MIT)

Copyright (c) 2015 Viktor Arsovski

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.gatetech.cadewiclients.R;
import com.gatetech.utils.Internet;
import com.gatetech.utils.Utils;
import com.gatetech.content.UserContent;

import com.gatetech.model.UsersContext;
import com.gatetech.restserver.ApiAdapter;
import com.gatetech.restserver.Response.UsuariosResponse;
import com.gatetech.utils.logger;
import com.gatetech.utils.magicalCamera;
import com.gatetech.utils.popUp;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gatetech.utils.Utils.get_manufacturer;


/**
 * Created by Fabian on 28/02/2017.
 * This class shown the splash screen of the application
 * init the backendless and the shared preferences
 */

public class SplashActivity extends AwesomeSplash {
    private final int SPLASH_DISPLAY_LENGTH = 1000;


    @Override
    public void initSplash(ConfigSplash configSplash) {

        //Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        magicalCamera.setInitialSharedPreference(this, false);

        Intent i = new Intent(this, BaseActivity.class);
        startService(i);

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.colorWhite); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(SPLASH_DISPLAY_LENGTH); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(  R.drawable.cadewi_splash); //or any other drawable
        configSplash.setAnimLogoSplashDuration(SPLASH_DISPLAY_LENGTH); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeIn); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        //Customize Title
        String version = null;
        try {
            version = Utils.app_version(this);
        } catch (PackageManager.NameNotFoundException ex) {
            String title="Error to getVersion: ";

            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.GENERAL.toString(),get_manufacturer(),this);

            version="";
        }

        configSplash.setTitleSplash(getString(R.string.app_name) + " v" + version);
        configSplash.setTitleTextColor(R.color.colorBlackLight);
        configSplash.setTitleTextSize(25f); //float value
        configSplash.setAnimTitleDuration(2000);
        configSplash.setAnimTitleTechnique(Techniques.DropOut);

/*
        PropertyContext property = new PropertyContext(this);

        property.insertProperty("BaseUrl", "REST", "http://www.cadewigroup.com", null, null);
        property.insertProperty("Status_OK", "REST", null, 200, null);
        property.insertProperty("Status_FAIL", "REST", null, 400, null);
        property.insertProperty("Base_Minutes_Send", "REST", null, 5, null);

        property.insertProperty("RESIZE_PHOTO_PIXELS_PERCENTAGE", "PHOTO", null, 1000, null);
        property.insertProperty("QUALITY_PHOTO_PERCENTAGE", "PHOTO", null, 50, null);
*/

    }

    @Override
    public void animationsFinished() {

        credencialvalidation();

        //Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
        //startActivity(loginIntent);

    }


    private void credencialvalidation() {

        try {

            // accedo a sqlite
            //DBCadewiClients db = new DBCadewiClients(this);

            UsersContext db = new UsersContext(this);
            UserContent.UserItem user = db.GetActiveUser();

            // SADB: Se cuenta con cuenta de usuario registrado ?
            if (user != null) {

                Utils.appUser = user;

                // Se cuenta con red y comunicación con el rest server ?
                Internet.ESTATUS estatus =  Internet.isNetworkAvailable(this,Utils.baseUrl);

                // SADB: La red esta disponible y el servidor url ?
                if (estatus.equals(Internet.ESTATUS.URL_AVAILABLE)) {

                    Toast.makeText(this,"Validando credenciales...",Toast.LENGTH_SHORT).show();
                    // SADB: Validando credenciales
                    final Call<UsuariosResponse> callLogin = ApiAdapter.getApiService(getResources().getString(R.string.base_Url)).login (user.correo, user.password );

                    callLogin.enqueue(new Callback<UsuariosResponse>() {
                        @Override
                        public void onResponse(Call<UsuariosResponse> call, Response<UsuariosResponse> response) {

                            UsuariosResponse usuariosResponse = new UsuariosResponse();

                            boolean issuccessfull = response.isSuccessful();

                            if (issuccessfull) {
                                usuariosResponse = response.body();
                                if(usuariosResponse.getEstado().equals(Utils.Status_OK)) {

                                    if (usuariosResponse.getUsuarios().size()>0) {
                                        // referenciamos los datos del usuario que se firmo en la app
                                        // Usuario valido, se lanza actitivy  principal (Base)
                                        Intent mainIntent = new Intent(SplashActivity.this, BaseActivity.class);
                                        startActivity(mainIntent);
                                    }

                                }

                                else if (!usuariosResponse.getEstado().equals(Utils.Status_OK)) {
                                    // SADB: Usuario No Valido o no existente !!!
                                    // no existe usuario activo, se lanza activity de login
                                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt( "OperationMode", LoginActivity.URLSERVER_SESSION );
                                    bundle.putString ( "eMailUser", Utils.appUser.correo);
                                    bundle.putString ( "password", Utils.appUser.password );
                                    loginIntent.putExtras ( bundle );

                                    startActivity(loginIntent);
                                    Toast.makeText(SplashActivity.this,"Credenciales no validas",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {

                                StringBuilder message= new StringBuilder();
                                message.append("Status: ")
                                        .append( response.code() )
                                        .append(" ")
                                        .append( response.message());

                                logger.error(Utils.appUser.correo,"Error on splashActivity",message.toString(), Utils.LOG_CATEGORY.GENERAL.toString(),get_manufacturer(),SplashActivity.this);

                                // Fallo en la respuesta del servidor
                                popUp.AlertDialog(message.toString(),"Error !!!","Cerrar",SplashActivity.this);

                                // SADB: No hay conexión a internet, operación offline
                                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt( "OperationMode", LoginActivity.LOCAL_SESSION );
                                loginIntent.putExtras ( bundle );
                                startActivity(loginIntent);
                            }

                        }

                        @Override
                        public void onFailure(Call<UsuariosResponse> call, Throwable t) {
                            // SADB: Algo fallo del lado del servidor
                            String title="Error credencialvalidation - onFailure: ";

                            StringBuilder message= new StringBuilder();
                            message.append(title)
                                    .append( t.getMessage());

                            logger.error(Utils.appUser.correo,title,t.getMessage(), Utils.LOG_CATEGORY.GENERAL.toString(),get_manufacturer(),SplashActivity.this);

                        //    popUp.AlertDialog(message.toString(),"Error !!!","Cerrar",SplashActivity.this);

                            // SADB: No hay conexión a internet, operación offline
                            Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt( "OperationMode", LoginActivity.LOCAL_SESSION );
                            loginIntent.putExtras ( bundle );
                            startActivity(loginIntent);
                        }
                    });

                }
                else if (estatus.equals(Internet.ESTATUS.DISABLED_NETWORK)) {

                    popUp.AlertDialog("Red no disponible, favor de revisar su conexión wifi o de datos","Informe","Cerrar",this);

                    // no existe usuario activo, se lanza activity de login
                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putInt( "OperationMode", LoginActivity.LOCAL_SESSION );
                    bundle.putString ( "eMailUser", Utils.appUser.correo);
                    bundle.putString ( "password", Utils.appUser.password);
                    loginIntent.putExtras ( bundle );

                    startActivity(loginIntent);
                }
                else if (estatus.equals(Internet.ESTATUS.URL_NOT_AVAILABLE)) {
                    popUp.AlertDialog(Utils.baseUrl +" no disponible, funcioonalidad limitada","Informe","Cerrar",this);

                    // Usuario valido, se lanza actitivy  principal (Base)
                    Intent mainIntent = new Intent(SplashActivity.this, BaseActivity.class);
                    startActivity(mainIntent);
                }
            }
            else {
                // no existe usuario activo, se lanza activity de login
                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt( "OperationMode", LoginActivity.URLSERVER_SESSION );
                loginIntent.putExtras ( bundle );

                startActivity(loginIntent);
            }

        }
        catch (Exception ex) {

            String title="Error credencialvalidation: ";

            StringBuilder message= new StringBuilder();
            message.append(title)
                    .append( ex.toString());

            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.GENERAL.toString(),get_manufacturer(),SplashActivity.this);

            Toast.makeText(this,message.toString(), Toast.LENGTH_LONG).show();
        }

    }

}
