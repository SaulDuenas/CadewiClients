package com.gatetech.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Map;


public class Permissions extends AppCompatActivity {


    static private int PERMISO_COCEDIDO = 100;
    Map<String, Boolean> mapPermissions = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean validarPermisos(Activity activity, String[] permisos){

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        boolean result_permision=false;
        // Ya se solicitaron permisos ?
        for (String str:permisos) {
            if (ContextCompat.checkSelfPermission(activity,str) == PackageManager.PERMISSION_GRANTED) {
                result_permision = true;
            }
            else{
                result_permision = false;
            }
        }

        if (result_permision) { return true;}

/*
        if ((checkSelfPermission( CAMERA )== PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission( WRITE_EXTERNAL_STORAGE )== PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission( READ_EXTERNAL_STORAGE )== PackageManager.PERMISSION_GRANTED) ){
            return true;
        }
*/
        // solicitamos permisos !!!
        for (String str:permisos){
            if (activity.shouldShowRequestPermissionRationale( str )){
                cargarDialogoRecomendacion(permisos);
            }
            else{
                activity.requestPermissions( permisos,PERMISO_COCEDIDO );
            }
        }

        /*
        if ((shouldShowRequestPermissionRationale( CAMERA )) ||
                (shouldShowRequestPermissionRationale( WRITE_EXTERNAL_STORAGE )) ||
                (shouldShowRequestPermissionRationale( READ_EXTERNAL_STORAGE ))) {
            cargarDialogoRecomendacion();
        }
        else{
            requestPermissions( PERMISOS,PERMISO_COCEDIDO );
        }
        */
        return false;
    }

    private void cargarDialogoRecomendacion(String[] permisos) {

        final String[] lpermisos = permisos;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("PERMISOS desactivados")
                .setTitle("Debe de aceptar los permisos para correcto funcionamiento de la App")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermissions( lpermisos,PERMISO_COCEDIDO );
                    }
                });

        builder.create();
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

        if (requestCode==PERMISO_COCEDIDO) {
            if (grantResults.length==permissions.length) {
                boolean result = false;
                for(int permision: grantResults) {
                    if(permision==PackageManager.PERMISSION_GRANTED) {
                        result = true;
                    }
                    else{
                        result = false;
                        break;
                    }
                }

                if (result) {
                    // PERMISOS CONCEDIDOS
                }
                else{
                    solicitarPermisosManual();
                }
            }
        }
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    /************************ SADB PERMISOS ****************************/


}
