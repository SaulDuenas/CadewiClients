package com.gatetech.controller.activities;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import com.gatetech.asyncServer.async_data;

import com.gatetech.controller.fragments.AddNewClientFragment;
import com.gatetech.controller.fragments.AddNewUserFragment;
import com.gatetech.controller.fragments.AddPhotoFragment;
import com.gatetech.controller.fragments.ClientDetailFragment;
import com.gatetech.controller.fragments.ClientListFragment;
import com.gatetech.controller.fragments.PhotoListFragment;
import com.gatetech.controller.fragments.UserDetailFragment;
import com.gatetech.controller.fragments.UserListFragment;
import com.gatetech.model.UsersContext;

import com.gatetech.utils.Utils;
import com.gatetech.cadewiclients.R;
import com.gatetech.content.ClientContent;
import com.gatetech.content.PhotoContent;
import com.gatetech.content.UserContent;

import com.gatetech.utils.logger;
import com.gatetech.utils.popUp;

import static com.gatetech.utils.Utils.get_manufacturer;


public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                               AddNewUserFragment.OnFragmentInteractionListener,
                                                               UserListFragment.OnListFragmentInteractionListener,
                                                               AddNewClientFragment.OnFragmentInteractionListener,
                                                               ClientListFragment.OnListFragmentInteractionListener,
                                                               PhotoListFragment.OnListFragmentInteractionListener,
                                                               AddPhotoFragment.OnFragmentInteractionListener,
                                                               ClientDetailFragment.OnFragmentInteractionListener,
                                                               UserDetailFragment.OnFragmentInteractionListener {


    DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_gatetech_base );

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        drawer = ( DrawerLayout ) findViewById( R.id.drawer_layout );

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );

        drawer.addDrawerListener( toggle );
        toggle.syncState();

        Configview();

    }


    private void Configview() {

        this.setTitle(R.string.app_name);

        //SADB: Set user info on drawer
        NavigationView navigationView = ( NavigationView ) findViewById( R.id.nav_view );
        View hview = navigationView.getHeaderView( 0 );
        TextView name = (TextView) hview.findViewById( R.id.lblNavName );
        TextView Email = (TextView) hview.findViewById( R.id.lblNavEmail );

        name.setText( Utils.appUser.nombre + " " + Utils.appUser.apellidos + " (" + Utils.appUser.perfil + ")" );
        Email.setText( Utils.appUser.correo );

        navigationView.setNavigationItemSelectedListener( this );

        //SADB: Proceso sincroniación
        async_data asyncData = new async_data(getApplicationContext(),this);
        asyncData.execute();


        // obtener version app
        TextView txtversion = ( TextView ) findViewById( R.id.txtversion );

        try {
            txtversion.setText("version " + Utils.app_version(this) );
        } catch (PackageManager.NameNotFoundException ex) {

            String title="Error onCreate - BaseActivity: ";
            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.GENERAL.toString(),get_manufacturer(),BaseActivity.this);
            Toast.makeText(this,title+ ex.toString(), Toast.LENGTH_SHORT).show();
        }



        Toast.makeText(this,getString(R.string.welcome)+  " " + Utils.appUser.nombre + " " + Utils.appUser.apellidos,Toast.LENGTH_SHORT).show();


        /*

        // SADB: Fragmento de Inicio
        Fragment myFragment = new ClientListFragment();
        getSupportFragmentManager().beginTransaction().replace( R.id.content_main, myFragment ).addToBackStack(null).
                commit();

        */

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


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }
*/







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

        Toast.makeText(getApplicationContext(),item.name,Toast.LENGTH_SHORT).show();

        Fragment myFragment= ClientDetailFragment.newInstance(item);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                addToBackStack(null).commit();

    }


    /** SADB: USER OPTIONS
     **/
    @Override
    public void onListFragmentInteraction(UserContent.UserItem item) {

        Toast.makeText(getApplicationContext(),item.nombre + " " + item.apellidos,Toast.LENGTH_SHORT).show();

        Fragment myFragment= UserDetailFragment.newInstance(item);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                addToBackStack(null).commit();


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


    @Override
    public void onAddPhotoFragmentInteraction(Uri uri) {

    }
}