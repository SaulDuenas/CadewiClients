package com.gatetech.controller.activities;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;

import android.content.Intent;
import android.content.Loader;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.KeyEvent;

import android.view.View;
import android.view.View.OnClickListener;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gatetech.content.ClientContent;
import com.gatetech.content.PhotoContent;
import com.gatetech.content.UserContent;
import com.gatetech.utils.Permissions;
import com.gatetech.utils.Utils;
import com.gatetech.utils.logger;
import com.gatetech.utils.popUp;
import com.gatetech.controller.fragments.AddNewUserFragment;
import com.gatetech.model.UsersContext;
import com.gatetech.restserver.Response.UsuariosResponse;

import java.util.ArrayList;
import java.util.List;

import com.gatetech.cadewiclients.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.gatetech.utils.Utils.get_manufacturer;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>,
                                                                AddNewUserFragment.OnFragmentInteractionListener {


    private int mPosition=0;
    private int mPositionMode=0;

    public static final int URLSERVER_SESSION=1;
    public static final int LOCAL_SESSION=2;



    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    //private UserLoginTask mAuthTask = null;
    // UI references.

    private AutoCompleteTextView mEmailView;

    private EditText mPasswordView;
    private AppCompatTextView linkNewUser;

    private View mProgressView;
    private View mLoginFormView;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitleColor(R.color.primaryTextColor);

        // SADB: GetParameters
        Bundle bundle = this.getIntent().getExtras();
        mPositionMode=bundle.getInt("OperationMode");

        Permissions permissions = new Permissions();
        permissions.validarPermisos(this,Utils.PERMISOS);

        configView();

    }



    private void configView () {


        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.txtEmail);
        mPasswordView = (EditText) findViewById(R.id.txtPassword);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {

                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager =  (InputMethodManager) getSystemService( INPUT_METHOD_SERVICE );
                inputMethodManager.hideSoftInputFromWindow( mPasswordView.getWindowToken(),0 );
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        // SADB:
        linkNewUser = (AppCompatTextView) findViewById(R.id.lblNewUser);
        linkNewUser.setOnClickListener(   new OnClickListener () {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        // SADB: Hide Keyborad
        InputMethodManager inputMethodManager =  (InputMethodManager) this.getSystemService( Context.INPUT_METHOD_SERVICE );
        inputMethodManager.hideSoftInputFromWindow( mEmailSignInButton.getWindowToken(),0 );

        mEmailView.clearFocus();
        mPasswordView.clearFocus();

        // SADB: Se valida credenciales
        mEmailSignInButton.setEnabled(false);
        //   credencialvalidation();
        mEmailSignInButton.setEnabled(true);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // if (mAuthTask != null) {
        //     return;
        // }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a async_data task to
            // perform the user login attempt.
//            showProgress(true);
            //   mAuthTask = new UserLoginTask(email, password);
            //   mAuthTask.execute((Void) null);


            popUp.WaitMessageBox("En Progreso","Revisando credenciales, espere un momento ....",this);

            final Call<UsuariosResponse> callLogin = Utils.mApiService.login (email, password );

            callLogin.enqueue(new Callback<UsuariosResponse>() {
                          /******************************SDAB: Retrofit methods************************************** */
                          @Override
                          public void onResponse(Call<UsuariosResponse> call, Response<UsuariosResponse> response) {
                              UsuariosResponse usuariosResponse = new UsuariosResponse();

                              if (response.isSuccessful()) {
                                  usuariosResponse = response.body();
                                  if(usuariosResponse.getEstado() == 200) {

                                      if (usuariosResponse.getUsuarios().size()>0) {

                                          try{

                                              UsuariosResponse.Usuario user = usuariosResponse.getUsuarios().get(0);

                                              // SADB: Add User to SQLITE
                                              UsersContext db = new UsersContext(LoginActivity.this);

                                              db.add( user.getCorreo(),
                                                      user.getNombre(),
                                                      user.getApellidos(),
                                                      user.getPassword(),
                                                      user.getPerfil().toString(),
                                                      "",
                                                      1
                                                    );


                                              //SADB:  Start Activity with personal function
                                              // referenciamos los datos del usuario que se firmo en la app
                                              Utils.appUser = Utils.getUserItem(user);

                                              Intent ObjbaseActivity = new Intent( getApplicationContext(), BaseActivity.class );
                                              startActivity( ObjbaseActivity );
                                              finish(); // delete to activity stack
                                          }
                                          catch (Exception e){
                                              Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                          }

                                      }

                                  }

                                  else if (usuariosResponse.getEstado() == 404){

                                      mEmailView.setError( usuariosResponse.getExcepcion());
                                      mPasswordView.setError( usuariosResponse.getExcepcion());
                                      Toast.makeText(LoginActivity.this, usuariosResponse.getExcepcion() ,Toast.LENGTH_LONG).show();
                                  }
                              }
                              popUp.CloseWaitMessageBox();
                              //showProgress(false);
                          }


                          @Override
                          public void onFailure(Call<UsuariosResponse> call, Throwable t) {
                              popUp.CloseWaitMessageBox();

                              String title = "Failure attemptLogin.enqueue: ";
                              StringBuilder message = new StringBuilder();

                              message.append("Error: ").append(t.getMessage());

                              logger.error(Utils.appUser.correo,title,message.toString(),Utils.LOG_CATEGORY.GENERAL.toString(),get_manufacturer(),LoginActivity.this);

                              Toast.makeText(LoginActivity.this, message.toString(), Toast.LENGTH_LONG).show();
                              //showProgress(false);

                          }

                }
            );

            Toast.makeText(this,"Validando ...",Toast.LENGTH_LONG).show();
        }
    }


    private void register() {
        // Toast.makeText(this, "Registrarse ... ", Toast.LENGTH_LONG).show();
        final AddNewUserFragment addNewUsr = AddNewUserFragment.newInstance(AddNewUserFragment.MODE_REGISTER,
                AddNewUserFragment.JUMP_LOGIN,
                null);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_login,addNewUsr).
                addToBackStack(null).commit();

    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},
                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(ClientContent.ClientItem item) {

    }

    @Override
    public void onListFragmentInteraction(PhotoContent.PhotoItem item) {

    }

    @Override
    public void onListFragmentInteraction(UserContent.UserItem item) {

    }

}



