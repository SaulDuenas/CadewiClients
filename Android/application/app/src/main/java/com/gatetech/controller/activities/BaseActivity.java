package com.gatetech.controller.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gatetech.asyncServer.async_data;
import com.gatetech.content.AddressContent;
import com.gatetech.controller.fragments.AddNewClientFragment;
import com.gatetech.controller.fragments.AddNewUserFragment;
import com.gatetech.controller.fragments.AddPhotoFragment;
import com.gatetech.controller.fragments.ClientDetailFragment;
import com.gatetech.controller.fragments.ClientListFragment;
import com.gatetech.controller.fragments.PhotoListFragment;
import com.gatetech.controller.fragments.UserListFragment;
import com.gatetech.model.UsersContext;
import com.gatetech.restserver.Response.ClientesResponse;
import com.gatetech.restserver.Response.UsuariosResponse;
import com.gatetech.utils.Utils;
import com.gatetech.cadewiclients.R;
import com.gatetech.content.ClientContent;
import com.gatetech.content.PhotoContent;
import com.gatetech.content.UserContent;

import com.gatetech.utils.logger;
import com.gatetech.utils.popUp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gatetech.utils.Utils.get_manufacturer;


public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                               AddNewUserFragment.OnFragmentInteractionListener,
                                                               UserListFragment.OnListFragmentInteractionListener,
                                                               AddNewClientFragment.OnFragmentInteractionListener,
                                                               ClientListFragment.OnListFragmentInteractionListener,
                                                               PhotoListFragment.OnListFragmentInteractionListener,
                                                               AddPhotoFragment.OnFragmentInteractionListener {

    DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );

        this.setTitle(R.string.app_name);


        setContentView( R.layout.activity_gatetech_base );
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        TextView txtversion = ( TextView ) findViewById( R.id.txtversion );

        try {
            txtversion.setText("version " + Utils.app_version(this) );
        } catch (PackageManager.NameNotFoundException ex) {

            String title="Error onCreate - BaseActivity: ";
            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.GENERAL.toString(),get_manufacturer(),BaseActivity.this);
            Toast.makeText(this,title+ ex.toString(), Toast.LENGTH_SHORT).show();
        }

        drawer = ( DrawerLayout ) findViewById( R.id.drawer_layout );

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );

        drawer.addDrawerListener( toggle );
        toggle.syncState();
        NavigationView navigationView = ( NavigationView ) findViewById( R.id.nav_view );

        //SADB: Set user info on drawer
        
        View hview = navigationView.getHeaderView( 0 );
        TextView name = (TextView) hview.findViewById( R.id.lblNavName );
        name.setText( Utils.appUser.nombre + " " + Utils.appUser.apellidos + " (" + Utils.appUser.perfil + ")" );
        TextView Email = (TextView) hview.findViewById( R.id.lblNavEmail );
        Email.setText( Utils.appUser.correo );
        

        //SADB: end
        navigationView.setNavigationItemSelectedListener( this );

        //SADB: Se lanza proceso en segundo plano
        async_data asyncData = new async_data(getApplicationContext(),this);
        asyncData.execute();

        /*

        // SADB: Fragmento de Inicio
        Fragment myFragment = new ClientListFragment();
        getSupportFragmentManager().beginTransaction().replace( R.id.content_main, myFragment ).addToBackStack(null).
                commit();

        */

        Toast.makeText(this,getString(R.string.welcome)+  " " + Utils.appUser.nombre + " " + Utils.appUser.apellidos,Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
            else{
                //SADB:
                setTitle(R.string.app_name);
                setTitleColor(R.color.primaryTextColor);
                drawer.openDrawer(GravityCompat.START);
                //super.onBackPressed();
            }
        }

        /*

        if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
  */
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }


/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

*/



    /** SADB: Navegación

     **/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment myFragment = null;

        /** SADB: ADD USER
         **/
        if (id == R.id.nav_AddUser) {

            if (Utils.appUser.perfil.toString().trim().equals( getResources().getString(R.string.user_root) )){
                //myFragment = new AddNewUserFragment();
                myFragment = AddNewUserFragment.newInstance(AddNewUserFragment.MODE_REGISTER,
                                                            AddNewUserFragment.JUMP_USERLIST,
                                                            null);
            }
            else{
                popUp.AlertDialog( getResources().getString(R.string.message_user), "Mensaje", "Cerrar", this );
            }

        /** SADB: USER LIST
         **/
        } else if (id == R.id.nav_UserList) {

            if (Utils.appUser.perfil.toString().trim().equals( getResources().getString(R.string.user_root) )){
                myFragment = new UserListFragment();
            }
            else{
                popUp.AlertDialog( getResources().getString(R.string.message_user), "Mensaje", "Cerrar", this );
            }

        /** SADB: ADD CLIENT
         **/
        } else if (id == R.id.nav_AddClient) {
            //myFragment = new AddNewClientFragment();
            myFragment = AddNewClientFragment.newInstance(AddNewClientFragment.MODE_REGISTER,
                                                          AddNewClientFragment.JUMP_CLIENTLIST,
                                                          null);

        /** SADB: CLIENT LIST
         **/
        } else if (id == R.id.nav_ClientsList) {
            myFragment = new ClientListFragment();


        /** SADB: LOCATION VISOR
         **/
        }else if (id == R.id.nav_Maps) {
            Intent intent = new Intent( getApplicationContext(), MapsActivity02.class );
            Bundle bundle = new Bundle();
            startActivity( intent );

        /** SADB: CLOSE USER SESION
         **/
        } else if (id == R.id.nav_Close_Sesion) {

            Toast.makeText(getApplicationContext(),"Cerrando Sesión de usuario",Toast.LENGTH_SHORT).show();
            try {
                // SADB: Delete user into sqlite
                // DBCadewiClients db = new DBCadewiClients(this);
                UsersContext db = new UsersContext(this);
                db.delete(Utils.appUser);

                Intent ObjbaseActivity = new Intent( getApplicationContext(), LoginActivity.class );
                startActivity( ObjbaseActivity );
                finish(); // delete to activity stack
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        /** SADB: CLOSE APP
         **/
        else if (id == R.id.nav_Exit) {
            Intent salida=new Intent(Intent.ACTION_MAIN); //Llamando a la activity principal
            finish(); // La cerramos.
        }


        /** SADB: LAUNCH SCREEN (SELECTED FRAGMENT)
         **/
        if (myFragment!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                    addToBackStack(null).commit();
        }

        // SADB:: CLOSE DRAWER
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /** SADB: CLIENT OPTIONS
     **/
    @Override
    public void onListFragmentInteraction(ClientContent.ClientItem item) {

        final ClientContent.ClientItem iclientItem = item;

        Toast.makeText(getApplicationContext(),iclientItem.name,Toast.LENGTH_SHORT).show();


        Fragment myFragment= ClientDetailFragment.newInstance(iclientItem);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                addToBackStack(null).commit();

        /*

        final String[] items = {"Información - Cliente",
                                "Galeria - Cliente",
                                "Ubicación - Cliente",
                                "Editar - cliente",
                                "Eliminar - Cliente"
                                };

        AlertDialog.Builder builder =
             //   new AlertDialog.Builder(new ContextThemeWrapper(BaseActivity.this,R.style.SplashTheme));
                  new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle("Selecciona la opcion deseada - Cliente: " + iclientItem.name);
        builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        Fragment myFragment= null;

                        switch (item) {
                            case 0:
                                // SADB: Launch Add Client Fragment - MODE VISOR  ( Client Detail )
                                myFragment = AddNewClientFragment.newInstance(AddNewClientFragment.MODE_VISOR, AddNewClientFragment.JUMP_CLIENTLIST, iclientItem);
                                break;
                            case 1:
                                // SADB: Launch Client PhotoList
                                myFragment = PhotoListFragment.newInstance( iclientItem, Utils.appUser );
                                break;
                            case 2:
                                // SADB: Launch Map
                                Integer operation_mode=0;
                                boolean launch=false;
                                // Se cuenta con dirección ?
                                if ((iclientItem.Address != null && iclientItem.Address.ITEMS.size()>0)) {
                                    // Definir opciones de la actividad ?
                                    Integer editable = iclientItem.Address.ITEMS.get(0).LocEditable;
                                    operation_mode =editable>0? MapsActivity.MODE_REGISTER:MapsActivity.MODE_VISOR;
                                    launch=true;
                                }
                                else {
                                    popUp.AlertDialog( "El cliente no cuenta con una dirección que asociar","Mensaje","Cerrar",BaseActivity.this );
                                    launch=false;
                                }

                                if (launch) {
                                    ClientContent.focusClientItem = iclientItem;

                                    Intent intent = new Intent( getApplicationContext(), MapsActivity.class );
                                    Bundle bundle = new Bundle();
                                    bundle.putInt( "OperationMode", operation_mode );
                                    bundle.putInt( "position", 0 );
                                    intent.putExtras ( bundle );
                                    startActivity( intent );
                                }

                                break;

                            case 3:
                                // SADB: Launch Add Client Fragment - MODE VISOR  ( Client Detail )
                                myFragment = AddNewClientFragment.newInstance(AddNewClientFragment.MODE_UPDATE, AddNewClientFragment.JUMP_CLIENTLIST, iclientItem);

                                break;

                            case 4:
                                // SADB: Check Permisisons to eliminate user
                                if (Utils.appUser.perfil.toString().trim().equals( getResources().getString(R.string.user_root) )) {

                                    android.app.AlertDialog.Builder dialog02 = new android.app.AlertDialog.Builder(BaseActivity.this);

                                    dialog02.setTitle( "Confirmar" );
                                    dialog02.setMessage( "Estas seguro de eliminar el Cliente: " + iclientItem.name + "?");
                                    dialog02.setCancelable( false );
                                    dialog02.setPositiveButton( "Confirmar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                   // rutina de eliminaciòn de usuario

                                                    AddressContent.AddressItem addressItem = iclientItem.Address.ITEMS.size()>0? iclientItem.Address.ITEMS.get( 0 ): new AddressContent.AddressItem();

                                                    updateClient(iclientItem.client.toString(),
                                                                 addressItem.address.toString(),
                                                                 iclientItem.name,
                                                                 iclientItem.rfc,
                                                                 iclientItem.userMail,
                                                                 iclientItem.Contacts.getItem( "telefono" ).value,
                                                                 iclientItem.Contacts.getItem( "Correo" ).value,
                                                                 addressItem.street,
                                                                 addressItem.zipCode,
                                                                 addressItem.Neighborhood,
                                                                 addressItem.Delegation,
                                                                 addressItem.State,
                                                                 addressItem.City,
                                                                 addressItem.Longitude,
                                                                 addressItem.Latitude,
                                                                 addressItem.LocEditable.toString(),
                                                            "Eliminando cliente, espere un momento ....",
                                                            "cliente eliminado satisfactoriamente",
                                                            Utils.ESTATUS.DELETED.toString()
                                                    );
                                                }
                                            }
                                    );

                                    dialog02.setNegativeButton( "Cancelar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // NO SE HACE NADA
                                                }
                                            }
                                    );

                                    dialog02.show();

                                }
                                else {
                                    popUp.AlertDialog( getResources().getString(R.string.message_user), "Mensaje", "Cerrar", BaseActivity.this );
                                }
                                break;
                        }

                        if (myFragment !=null){
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                                    addToBackStack(null).commit();
                        }

                    }
                }).show();


        */




    }


    /** SADB: USER OPTIONS
     **/
    @Override
    public void onListFragmentInteraction(UserContent.UserItem item) {

        final UserContent.UserItem iuserItem = item;

        Toast.makeText(getApplicationContext(),iuserItem.nombre + " " + iuserItem.apellidos,Toast.LENGTH_SHORT).show();

        final String[] items = {"Información - Usuario",
                                "Editar - Usuario",
                                "Eliminar - Usuario"};

        AlertDialog.Builder builder =
                //   new AlertDialog.Builder(new ContextThemeWrapper(BaseActivity.this,R.style.SplashTheme));
                new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle("Selecciona la opcion deseada - Usuario: " + iuserItem.nombre + " " +iuserItem.apellidos );
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        // SADB: Launch User Detail Fragment
                        Fragment myFragment = AddNewUserFragment.newInstance(AddNewUserFragment.MODE_VISOR,
                                AddNewUserFragment.JUMP_USERLIST,
                                iuserItem);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                                addToBackStack(null).commit();
                        break;

                    case 1:
                        Fragment myFragmentUpdate = AddNewUserFragment.newInstance(AddNewUserFragment.MODE_UPDATE,
                                AddNewUserFragment.JUMP_USERLIST,
                                iuserItem);

                        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragmentUpdate).
                                addToBackStack(null).commit();
                        break;


                    case 2:
                        // SADB: Check Permisisons to eliminate user
                        if (Utils.appUser.perfil.toString().trim().equals( getResources().getString(R.string.user_root) )) {

                            android.app.AlertDialog.Builder dialog02 = new android.app.AlertDialog.Builder(BaseActivity.this);

                            dialog02.setTitle("Confirmar");
                            dialog02.setMessage("Estas seguro de eliminar el Usuario: " + iuserItem.nombre + " "+ iuserItem.apellidos + " ?");
                            dialog02.setCancelable(false);
                            dialog02.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    updateUser( iuserItem.correo,
                                                iuserItem.nombre,
                                                iuserItem.apellidos,
                                                iuserItem.password,
                                                iuserItem.perfil,
                                                "Eliminando usuario, espere un momento ....",
                                            "Usuario eliminado satisfactoriamente",
                                                Utils.ESTATUS.DELETED.toString());

                                }
                            });

                            dialog02.setNegativeButton( "Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // NO SE HACE NADA
                                        }
                                    }
                            );

                            dialog02.show();

                        }
                        else {
                            popUp.AlertDialog( getResources().getString(R.string.message_user), "Mensaje", "Cerrar", BaseActivity.this );
                        }
                        break;
                }

            }
        }).show();

    }


    /** SADB: PHOTO OPTION
     **/
    @Override
    public void onListFragmentInteraction(PhotoContent.PhotoItem item) {

        Intent intent = new Intent(this, FullscreenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("source",Utils.URIPATH);
        Utils.mphotoPath = item.Path;
        bundle.putString(  "path", item.Path);
        intent.putExtras ( bundle );
        startActivity(intent);

    }


    private void updateUser (String email,
                             String name,
                             String last_name,
                             String password,
                             String profile,
                             String wait_message,
                             String accomplished_message,
                             String status) {

        final String iwait_message = wait_message;
        final String iaccomplished_message = accomplished_message;
        final StringBuilder message  = new StringBuilder();

        try {
                popUp.WaitMessageBox("En Progreso",iwait_message,BaseActivity.this);

                // SADB: Call to REST API
                final Call<UsuariosResponse> callUsers = Utils.mApiService.updateUser(email,
                                                                                      name,
                                                                                      last_name,
                                                                                      password,
                                                                                      profile,
                                                                                      status
                );


                callUsers.enqueue(new Callback<UsuariosResponse>() {
                    @Override
                    public void onResponse(Call<UsuariosResponse> call, Response<UsuariosResponse> response) {
                        popUp.CloseWaitMessageBox();

                        if (response.isSuccessful()) {
                            UsuariosResponse usuariosResponse = new UsuariosResponse();
                            usuariosResponse = response.body();

                            // SADB: Response is succesfull ?
                            if(usuariosResponse.getEstado().equals(Utils.Status_OK))  {
                                popUp.AlertDialog(iaccomplished_message,"Mensaje","Cerrar",BaseActivity.this);

                            }
                            else if (usuariosResponse.getEstado().equals(Utils.Status_FAIL)){

                                logger.error(Utils.appUser.correo,"Error HTTP: ",usuariosResponse.getExcepcion(),Utils.LOG_CATEGORY.USER.toString(),get_manufacturer(),BaseActivity.this);

                                popUp.AlertDialog(usuariosResponse.getExcepcion(),"Mensaje","Aceptar",BaseActivity.this);

                            }
                        }
                        else  {

                            message.append("Error HTTP: ").append(response.code()).append(" ").append( response.message());

                            logger.error(Utils.appUser.correo,"Error HTTP: ",message.toString(),Utils.LOG_CATEGORY.USER.toString(),get_manufacturer(),BaseActivity.this);

                            popUp.AlertDialog(message.toString(),"Error","Aceptar",BaseActivity.this);
                        }

                        // SADB: Launch UserListFragment
                        Fragment myFragment = new UserListFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                                addToBackStack(null).commit();

                    }

                    @Override
                    public void onFailure(Call<UsuariosResponse> call, Throwable t) {

                        String title = "Failure updateUser.enqueue: ";
                        String msg = (t !=  null? t.getMessage() :"Oops something went wrong");

                        logger.error(Utils.appUser.correo,title,msg,Utils.LOG_CATEGORY.USER.toString(),get_manufacturer(),BaseActivity.this);

                        popUp.CloseWaitMessageBox();

                        popUp.AlertDialog(msg,title,"Cerrar",BaseActivity.this);

                        // SADB: Launch UserListFragment
                        Fragment myFragment = new UserListFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                                addToBackStack(null).commit();

                    }
                });

        } catch (InterruptedException ex) {

            String title="Error updateUser: ";

            logger.error(Utils.appUser.correo,"Failure updateUser.enqueue: ",ex, Utils.LOG_CATEGORY.USER.toString(),get_manufacturer(),BaseActivity.this);

            popUp.CloseWaitMessageBox();
            popUp.AlertDialog(ex.toString(),title,"Cerrar",this);
        }
    }


    private void updateClient ( String client,
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
                                final String wait_message,
                                final String accomplished_message,
                                String status) {


        final StringBuilder message  = new StringBuilder();

        try {
            popUp.WaitMessageBox("En Progreso",wait_message,BaseActivity.this);

            // SADB: Call to REST API
            final Call<ClientesResponse> callupdateClient= Utils.mApiService.updateClient( client,
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

            callupdateClient.enqueue( new Callback<ClientesResponse>() {
                @Override
                public void onResponse(Call<ClientesResponse> call, Response<ClientesResponse> response) {

                    popUp.CloseWaitMessageBox();
                    if (response.isSuccessful()) {

                        ClientesResponse cln = response.body();
                        if(cln.getEstado() == Utils.Status_OK)  {

                            popUp.AlertDialog(accomplished_message,"Mensaje","Cerrar",BaseActivity.this);

                        }
                        else if (cln.getEstado() == Utils.Status_FAIL){

                            logger.error(Utils.appUser.correo,"onResponse result: ",cln.getExcepcion(),Utils.LOG_CATEGORY.CLIENT.toString(),get_manufacturer(),BaseActivity.this);

                            popUp.AlertDialog(cln.getExcepcion(),"Mensaje","Aceptar",BaseActivity.this);

                        }
                    }
                    else  {

                        message.append("Error HTTP: ").append(response.code()).append(" ").append( response.message());

                        logger.error(Utils.appUser.correo,"Error HTTP: ",message.toString(),Utils.LOG_CATEGORY.CLIENT.toString(),get_manufacturer(),BaseActivity.this);

                        popUp.AlertDialog(message.toString(),"Error","Aceptar",BaseActivity.this);

                    }

                    // SADB: Launch CLientListFragment
                    Fragment myFragment = new ClientListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                            addToBackStack(null).commit();

                }

                @Override
                public void onFailure(Call<ClientesResponse> call, Throwable t) {

                    String title = "Failure updateClient.enqueue: ";
                    String msg = (t !=  null? t.getMessage() :"Oops something went wrong");

                    logger.error(Utils.appUser.correo,title,msg,Utils.LOG_CATEGORY.CLIENT.toString(),get_manufacturer(),BaseActivity.this);

                    popUp.CloseWaitMessageBox();

                    popUp.AlertDialog(msg,title,"Cerrar",BaseActivity.this);

                    // SADB: Launch CLientListFragment
                    Fragment myFragment = new ClientListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                            addToBackStack(null).commit();

                }
            } );

        } catch (InterruptedException ex) {

            String title="Error updateClient: ";

            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.CLIENT.toString(),get_manufacturer(),BaseActivity.this);

            popUp.CloseWaitMessageBox();
            popUp.AlertDialog(ex.toString(),title,"Cerrar",this);
        }

    }

}