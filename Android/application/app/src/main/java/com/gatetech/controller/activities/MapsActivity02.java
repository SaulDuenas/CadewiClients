package com.gatetech.controller.activities;

import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gatetech.utils.Utils.get_manufacturer;


public class MapsActivity02 extends FragmentActivity implements OnMapReadyCallback {


    private static final int LOCATION_INTERNAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private static final float DEFAULT_ZOOM = 12f;

    private GoogleMap mMap;
    private Location mLocation;

    private TextInputEditText txtSearch;
    private TextView lblresult;
    private Button btnfocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gatetech_maps02);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            ext_createview();

            searchFree();

        }
        catch(Exception ex) {
            String title="Error onCreate_MApsActivity02: ";

            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.MAPS.toString(),get_manufacturer(),this);
        }
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }





    /********************* Methods Solutions ******************************/

    private  void ext_createview()  {
        txtSearch = (TextInputEditText) findViewById(R.id.txtSearch);
        btnfocus = (Button) findViewById(R.id.btnfocus);
        lblresult = (TextView) findViewById(R.id.lblresult);

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_SEND) {

                    searchFree();
                    handled = true;
                }
                return handled;
            }
        });
    }


    private void searchFree() {

        try {
            popUp.WaitMessageBox( "En Progreso", "Obteniendo Clientes, espere un momento ....", this);
        } catch (InterruptedException e) {
            popUp.CloseWaitMessageBox();
            popUp.AlertDialog( e.toString(), "Error !!!", "Cerrar", this);
        }

        //SADB: Clear Fragment
        ClientContent.ITEMS.clear();
        ClientContent.ITEM_MAP.clear();

        TextInputEditText txtSearch =(TextInputEditText) findViewById(R.id.txtSearch);

        // SADB: Call to REST API
        String busqueda=txtSearch.getText().toString().trim();

        // SADB: Call to REST API
        final Call<ClientesResponse> callClients = Utils.mApiService.getClientsbyFind( Utils.appUser.correo,Utils.appUser.perfil,busqueda);
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

            hideSoftKeyboard();
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



    private void refreshActivity () {

        Integer counter= 0;

        if (mMap !=null) {

            mMap.clear();

            for (ClientContent.ClientItem client:ClientContent.ITEMS) {
                // Se cuenta con dirección del cliente ?
                if (client.Address.ITEMS.size()>0){

                    AddressContent.AddressItem addresitem = client.Address.ITEMS.get(0);
                    // SADB: Se cuenta con su ubicación ?
                    if (addresitem.LocEditable.equals(0)) {

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
                                .title(title);

                        mMap.addMarker(point);

                        counter++;
                    }
                }

            }

        }

        lblresult.setText( "Clientes: " + counter.toString());
    }



}
