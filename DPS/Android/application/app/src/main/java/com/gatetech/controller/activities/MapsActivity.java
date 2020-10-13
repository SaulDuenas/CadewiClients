package com.gatetech.controller.activities;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


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
import android.support.design.widget.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.gatetech.utils.Utils.get_manufacturer;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private int mPosition=0;


    public static boolean enabledDialogProvider = false;

    int mPermissionCheck = 0;

    boolean mRegistrar = false;
    boolean mEditable = false;

    String[] mProviders = {LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER};

    public enum OPERATIONMODE {

        MODE_REGISTER("register_mode"),
        MODE_VISOR ("visor_mode");

        private final String stringValue;
        OPERATIONMODE(final String s) { stringValue = s; }

       static OPERATIONMODE newInstance(final String s) {
            OPERATIONMODE val=s.equals(OPERATIONMODE.MODE_REGISTER)? OPERATIONMODE.MODE_REGISTER:OPERATIONMODE.MODE_VISOR;
            return val;
        }

        public String toString() { return stringValue; }

    }

    private OPERATIONMODE mPositionMode=OPERATIONMODE.MODE_VISOR;

    private List<LocationManager> mlocationManager;

    private static final int LOCATION_INTERNAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private static final float DEFAULT_ZOOM = 18f;

    public GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FloatingActionButton btnRegister;
    private FloatingActionButton btnGPS;
    private ClientContent.ClientItem mclientItem;
    private AddressContent.AddressItem mAddressItem;

    private Location mLocation;


    static public Intent newInstance (Context context, ClientContent.ClientItem client, AddressContent.AddressItem address) throws IOException {

        Intent intent=new Intent(context, MapsActivity.class );

        Bundle bundle = new Bundle();

        bundle.putByteArray("ClientItem",Utils.convertBytes(client));
        bundle.putByteArray("AddressItem", Utils.convertBytes(address));

        bundle.putInt( "position", 0 );
        intent.putExtras ( bundle );

        return intent;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_gatetech_maps );

        // SADB: Get parameters
        Bundle datos = this.getIntent().getExtras();

        try {
            Object obj = (Object) Utils.convertObject(datos.getByteArray ("ClientItem"));
            mclientItem = (ClientContent.ClientItem) obj;

            Object obj02 = (Object) Utils.convertObject(datos.getByteArray ("AddressItem"));
            mAddressItem = (AddressContent.AddressItem) obj02;

            // Se cuenta con dirección ?
            if ((mAddressItem != null)) {
                // Definir opciones de la actividad ?
                Integer editable = mAddressItem.LocEditable;

                mPositionMode = mAddressItem.LocEditable.toString().equals(AddressContent.AddressItem.MODE.MODE_REGISTER.toString() ) ? OPERATIONMODE.MODE_REGISTER:OPERATIONMODE.MODE_VISOR;

            }

            mlocationManager = new ArrayList<LocationManager>();

            initMap();

      //      checkMap();


        } catch (IOException ex) {
            String title="Error "+ MapsActivity.class +": ";
            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.MAPS.toString(),get_manufacturer(), new MapsActivity());
        } catch (ClassNotFoundException ex) {
            String title="Error "+ MapsActivity.class +": ";
            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.MAPS.toString(),get_manufacturer(), new MapsActivity());
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

        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setMyLocationButtonEnabled( false );

        configView();

    }


/*
    @SuppressLint("RestrictedApi")
    private void operationMode(String operation) {

        LatLng lat=null;
        String title="";
        btnRegister.setVisibility( View.INVISIBLE );

        if (operation.equals(OPERATIONMODE.MODE_REGISTER)) {
            btnGPS.setVisibility( View.VISIBLE);
            mEditable=true;

            lat = new LatLng( 0, 0 );
            title= "No se tiene ubicación para el cliente";
        }
        else if (operation.equals(OPERATIONMODE.MODE_VISOR)) {

            btnGPS.setVisibility( View.INVISIBLE);
            AddressContent.AddressItem addresitem = mclientItem.Address.ITEMS.get(0);

            Double latitude = addresitem.Latitude.trim().equals( "" )? 0: Double.parseDouble( addresitem.Latitude);
            Double longitude = addresitem.Longitude.trim().equals( "" )?0: Double.parseDouble( addresitem.Longitude);

            lat = new LatLng( latitude, longitude );
            title= mclientItem.name;
        }

        moveCamera(lat,DEFAULT_ZOOM,title);

    }

*/


    private void configView() {

        // operationMode(mPositionMode);
        TextView lblTitle = (TextView) findViewById( R.id.lblClient );
        btnGPS = (FloatingActionButton) findViewById( R.id.btnGPS );
        btnRegister = (FloatingActionButton) findViewById( R.id.btnRegistrar );

        LatLng lat=null;
        String title="";
        btnRegister.setVisibility( View.INVISIBLE );

        if (mPositionMode.equals(OPERATIONMODE.MODE_REGISTER)) {
            btnGPS.setVisibility( View.VISIBLE);
            mEditable=true;

            lat = new LatLng( 0, 0 );
            title= "No se tiene ubicación para el cliente";
        }

        else if (mPositionMode.equals(OPERATIONMODE.MODE_VISOR)) {

            btnGPS.setVisibility( View.INVISIBLE);

            for (AddressContent.AddressItem addresitem:mclientItem.Address.ITEMS) {
                Double latitude = addresitem.Latitude.trim().equals( "" )? 0: Double.parseDouble( addresitem.Latitude);
                Double longitude = addresitem.Longitude.trim().equals( "" )?0: Double.parseDouble( addresitem.Longitude);

                lat = new LatLng( latitude, longitude );
                title= mclientItem.name;
                moveCamera(lat,DEFAULT_ZOOM,title);
            }

        }


        lblTitle.setText( mclientItem.name );


        btnGPS.setVisibility((mEditable)? View.VISIBLE:View.INVISIBLE);
        btnGPS.setOnClickListener( new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           getLocation();
                                       }
                                   }
        );


        btnRegister.setVisibility(  (mRegistrar &&  mEditable)? View.VISIBLE:View.INVISIBLE );

        btnRegister.setOnClickListener( new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Toast.makeText(MapsActivity.this, "Registrando Ubicación" ,Toast.LENGTH_SHORT).show();
                                                confirmregisterLoc();
                                            }
                                        }
        );

        // Check Permisions
        mPermissionCheck = ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION );

        if (mPermissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION )) {

            } else {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1 );
            }
        }

    }



    private void checkMap() {

        if(mapFragment == null) {
            mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                    .findFragmentById( R.id.map );

            if (mapFragment != null){
                // configurarMapa();

            }
        }

    }



    /**
     *   SADB: getlocation
     *
     *
     */
    private void getLocation() {

        try {

            mlocationManager.clear();
            boolean setSettings = false;

            Toast.makeText(this, "Obteniendo localización ...", Toast.LENGTH_SHORT).show();

            for (String str : mProviders) {
                // Register the Listener
                mPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                LocationManager loc = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                loc.requestLocationUpdates(str, LOCATION_INTERNAL, LOCATION_DISTANCE, new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        // Called when a new location is found by the network location provider
                        mLocation = location;
                        // update map
                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), 0,
                                "Ubicación Actual");

                        mRegistrar = mLocation != null ? true : false;

                        if ((mRegistrar && mEditable)) {
                            btnRegister.show();
                        } else {
                            btnRegister.hide();
                        }

                        Toast.makeText(MapsActivity.this, " Longitud: " + location.getLongitude() +
                                " Latitud: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Toast.makeText(MapsActivity.this,
                                provider + " disabled", Toast.LENGTH_SHORT).show();

                        if (provider.equals(LocationManager.GPS_PROVIDER) && !enabledDialogProvider) {

                            enabledDialogProvider = true;
                            // se lanza activity de habilitación de servicios
                            activeProviders();
                        }

                    }
                });
                mlocationManager.add(loc);

            }
        }
        catch (Exception ex) {

            String title="Error "+ Utils.LOG_CATEGORY.MAPS + "- getLocation() : ";

            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.MAPS.toString(),get_manufacturer(), this);

        }

    }

    /**
     *
     * SADB: activeProviders
     * Dialogo de Activación de servicios de ubicación
     *
     * */
    private void activeProviders() {

        if (!this.isFinishing()) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("CadewiClients necesita acceder a tu ubicación. Activa los servicios de Ubicación.")
                    .setCancelable(false)
                    .setPositiveButton("Configuración", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            enabledDialogProvider=false;
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }

                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            enabledDialogProvider=false;
                            dialog.cancel();

                        }
                    });

            AlertDialog alert;

            alert = builder.create();
            alert.show();

        }

    }


    private void moveCamera(LatLng latLng, float zoom, String title) {
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

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private void confirmregisterLoc() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle( "Confirmar" );
        dialog.setMessage( "Estas seguro de registrar esta ubicación para el cliente: "  + mclientItem.name + "?" );
        dialog.setCancelable( false );
        dialog.setPositiveButton( "Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        registerLoc();
                    }
                }
        );

        dialog.setNegativeButton( "Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // NO SE HACE NADA
                    }
                }
        );

        dialog.show();
    }

    private void registerLoc() {

      //  try {
            Integer address = mclientItem.Address.ITEMS.get( 0 ).address;
            popUp.WaitMessageBox("En Progreso","Registrando la ubicación del Cliente ....",this);

            //   Toast.makeText(getContext(),"Registrando Datos ...",Toast.LENGTH_LONG).show();
            // SADB: Call to REST API
            final retrofit2.Call<ClientesResponse> callSaveLoc = Utils.mApiService.saveLocationAddress( mclientItem.client.toString(),
                                                                                                        address.toString(),
                                                                                                        String.valueOf(mLocation.getLongitude()),
                                                                                                        String.valueOf(mLocation.getLatitude()) );
            callSaveLoc.enqueue(new Callback<ClientesResponse>() {

                /****************************  SADB: REST API  ******************************************/

                @SuppressLint("RestrictedApi")
                @Override
                public void onResponse(Call<ClientesResponse> call, Response<ClientesResponse> response) {
                    btnRegister.setVisibility(View.INVISIBLE );
                    popUp.CloseWaitMessageBox();
                    Toast.makeText(MapsActivity.this,"Cliente Registrado satisfactoriamente",Toast.LENGTH_LONG).show();

                    //mAddressItem = mclientItem.Address.ITEMS.get( mPosition ).LocEditable=0;
                    // SADB: Close edition Address
                    mAddressItem.LocEditable=0;
                    mAddressItem.Latitude = String.valueOf( mLocation.getLatitude());
                    mAddressItem.Longitude = String.valueOf(mLocation.getLongitude());
                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onFailure(Call<ClientesResponse> call, Throwable t) {
                    btnRegister.setVisibility(View.INVISIBLE );
                    popUp.CloseWaitMessageBox();
                    popUp.AlertDialog(t.getMessage(),"Error !!!","Cerrar",MapsActivity.this);
                }

                /**********************************************************************************************/
            }  );
/*
        } catch (InterruptedException ex) {
            popUp.CloseWaitMessageBox();
            popUp.AlertDialog(ex.toString(),"Error !!!","Cerrar",this);

            String title="Error "+ Utils.LOG_CATEGORY.MAPS + "- registerLoc() : ";
            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.MAPS.toString(),get_manufacturer(), this);

        }
  */

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}