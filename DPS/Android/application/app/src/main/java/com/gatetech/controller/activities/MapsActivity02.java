package com.gatetech.controller.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.gatetech.cadewiclients.R;
import com.gatetech.content.AddressContent;
import com.gatetech.content.ClientContent;

import com.gatetech.utils.Utils;
import com.gatetech.restserver.Response.ClientesResponse;
import com.gatetech.utils.logger;
import com.gatetech.utils.popUp;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v4.app.FragmentActivity;

import android.support.design.widget.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gatetech.utils.Utils.get_manufacturer;


public class MapsActivity02 extends FragmentActivity implements OnMapReadyCallback {


    private static final int LOCATION_INTERNAL = 1000;

    private static final int REGISTERED_LOCATION = 0;

    private static final float LOCATION_DISTANCE = 10f;
    private static final float DEFAULT_ZOOM = 12f;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private Location mLocation;

    private TextInputEditText txtSearch;
    private TextView lblresult;

    private ImageButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gatetech_maps02);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
           // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
          //          .findFragmentById(R.id.map);
           // mapFragment.getMapAsync(this);

            initMap();

        }
        catch(Exception ex) {
            String title="Error onCreate_MApsActivity02: ";

            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.MAPS.toString(),get_manufacturer(),this);
        }
    }



    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // Log.d(TAG, "initMap: initializing map");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);


        searchFree("");
        ConfigView();

    }





    /********************* Methods Solutions ******************************/

    private  void ConfigView()  {
        txtSearch = (TextInputEditText) findViewById(R.id.txtSearch);

        lblresult = (TextView) findViewById(R.id.lblresult);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_SEND) {

                    // SADB: Call to REST API
                    String token=txtSearch.getText().toString().trim();

                    searchFree(token);
                    handled = true;
                }

                hideSoftKeyboard(MapsActivity02.this, v);

                return handled;
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // SADB: Call to REST API
                String token=txtSearch.getText().toString().trim();
                searchFree(token);

                hideSoftKeyboard(MapsActivity02.this, view);

            }
        });
    }


    private void searchFree(String token) {

        popUp.WaitMessageBox( "En Progreso", "Obteniendo Clientes, espere un momento ....", this);

        //SADB: Clear Fragment
        ClientContent.ITEMS.clear();
        ClientContent.ITEM_MAP.clear();

        // SADB: Call to REST API
        final Call<ClientesResponse> callClients = Utils.mApiService.getClientsbyFind( Utils.appUser.correo,Utils.appUser.perfil,token);
        callClients.enqueue(new Callback<ClientesResponse>() {
            /********************* Retrofit Methods ******************************/
            @Override
            public void onResponse(Call<ClientesResponse> call, Response<ClientesResponse> response) {

                if (response.isSuccessful()) {
                    ClientesResponse clientes = new ClientesResponse();
                    clientes = response.body();

                    if (clientes.getEstado().equals(Utils.Status_OK)) {

                        if (clientes.getClientes() != null) {

                            ClientContent clientcontent = new ClientContent();
                            Utils.fillClientContent(clientcontent, clientes.getClientes());
                        }
                    }
                    else if (clientes.getEstado().equals(Utils.Status_FAIL)) {

                       logger.error(Utils.appUser.correo,"Error HTTP: ",clientes.getExcepcion(),Utils.LOG_CATEGORY.MAPS.toString(),get_manufacturer(),MapsActivity02.this);

                       popUp.AlertDialog(response.message(),"Mensaje","Aceptar",MapsActivity02.this);

                    }
                }
                else {
                    final StringBuilder message  = new StringBuilder();
                    message.append("Error HTTP: ").append(response.code()).append(" ").append( response.message());

                    logger.error(Utils.appUser.correo,"Error HTTP: ",message.toString(),Utils.LOG_CATEGORY.MAPS.toString(),get_manufacturer(),MapsActivity02.this);

                    popUp.AlertDialog(message.toString(),"Error","Aceptar",MapsActivity02.this);
                }

                // SADB: Refresh Fragment
                refreshActivity();
                popUp.CloseWaitMessageBox();

            }

            @Override
            public void onFailure(Call<ClientesResponse> call, Throwable t) {

                popUp.CloseWaitMessageBox();

                final StringBuilder message  = new StringBuilder();

                String title = "Failure searchFree.enqueue: ";
                String msg = (t !=  null? t.getMessage() :"Oops something went wrong");

                message.append(title).append(msg);

                logger.error(Utils.appUser.correo,title,msg,Utils.LOG_CATEGORY.MAPS.toString(),get_manufacturer(),MapsActivity02.this);

                popUp.AlertDialog(message.toString(),"Error !!!","Cerrar",MapsActivity02.this);

            }
        });

    }


    private void moveCamera(LatLng latLng, float zoom, String title){
        // Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );

        if (mMap !=null) {

            CameraPosition pos = mMap.getCameraPosition();
            float mZoom=0;
            mZoom =  (zoom == 0)? pos.zoom:zoom;
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng, mZoom ) );

            if (!title.equals( "My Location" )) {
                MarkerOptions options = new MarkerOptions()
                        .position( latLng )
                        .title( title );
                mMap.clear();
                mMap.addMarker( options );
            }
        }
    }


    public static void hideSoftKeyboard (Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }


    private void refreshActivity () {

        Integer counter= 0;

        if (mMap !=null) {

            mMap.clear();

            for (ClientContent.ClientItem client:ClientContent.ITEMS) {
                // Se cuenta con dirección del cliente ?
                if (client.Address.ITEMS.size()>0) {

                    AddressContent.AddressItem addresitem = client.Address.ITEMS.get(0);
                    // SADB: Se cuenta con su ubicación ?
                    if (addresitem.LocEditable.equals(REGISTERED_LOCATION)) {

                        Double latitude = addresitem.Latitude.trim().equals("") ? 0 : Double.parseDouble(addresitem.Latitude);
                        Double longitude = addresitem.Longitude.trim().equals("") ? 0 : Double.parseDouble(addresitem.Longitude);

                        LatLng latLng = null;
                        latLng = new LatLng(latitude, longitude);

                        String title = client.name;

                        CameraPosition pos = mMap.getCameraPosition();
                        float mZoom = 0;
                        mZoom = (DEFAULT_ZOOM == 0) ? pos.zoom : DEFAULT_ZOOM;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mZoom));

                        MarkerOptions point = new MarkerOptions()
                                .position(latLng)
                                .title(title)
                                .snippet("DESCRIPTION FOR MARKER HERE.");

                        mMap.addMarker(point);

                        counter++;
                    }
                }

            }

        }

        lblresult.setText(  getString(R.string.clients)  +":"+ counter.toString());

    }



}
