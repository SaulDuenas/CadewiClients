package com.gatetech.controller.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.MagicalPermissions;

import com.gatetech.controller.activities.FullscreenActivity;
import com.gatetech.cadewiclients.R;
import com.gatetech.content.ClientContent;
import com.gatetech.model.PhotoContext;
import com.gatetech.utils.Utils;

import com.gatetech.utils.logger;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;

import java.util.Map;


import static android.app.Activity.RESULT_OK;
import static com.gatetech.utils.Utils.get_manufacturer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddPhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AddPhotoFragment extends Fragment {

    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 1000;
    private int QUALITY_PHOTO_PERCENTAGE = 50;
    private String DIRECTORY_NAME = "Cadewi Images";

    //static public float DIMENSION_MAYOR = 1280;


    private int media_source=0;

    //  private PhotoUtils photoUtils;
    private ImageButton btnPhotoButton;
    private ImageButton btnGaleryButton;
    private ImageButton btnSaveSelection;

    private ImageView mphotoViewer;
    private TextInputEditText txtNote;
    private TextView lblClient;

    private FloatingActionMenu floatingBtnMenu;
    //see the information data of the picture
    private FloatingActionButton floatingBtnPhotoInformation;
    //button for rotate the image in bitmap and in image view
    private FloatingActionButton floatingBtnRotate;

    private FloatingActionButton floatingBtnFullImage;

    private LinearLayout panelSaveSelection;


    private MagicalCamera magicalCamera;
    private MagicalPermissions magicalPermissions;
    private com.gatetech.utils.magicalCamera magical_utils;

    private View principalLayout;

    private ProgressDialog progressdialog;

    private final String[] permissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    Map<String, Boolean> mapPermissions = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param01";
    private static final String ARG_PARAM2 = "param02";

    // TODO: Rename and change types of parameters
    private static ClientContent.ClientItem mClientId;
    private static Integer mAddressId;
    private OnFragmentInteractionListener mListener;

    public AddPhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ClientId Parameter 1.
     * @return A new instance of fragment AddPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPhotoFragment newInstance(ClientContent.ClientItem ClientId) {
        AddPhotoFragment fragment = new AddPhotoFragment();
        Bundle args = new Bundle();
        mClientId=ClientId;

        mAddressId = 0;
        if (mClientId.Address.ITEMS.size()>0){
            mAddressId  = mClientId.Address.ITEMS.get(0).address;
        }

        //  args.putString( ARG_PARAM1, ClientId );
        //  args.putString( ARG_PARAM2, AddressId );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments() != null) {
            //     mAddressId = getArguments().getString( ARG_PARAM1 );
            //      mClientId = getArguments().getString( ARG_PARAM2 );
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onCreate( outState );
        // outState.putInt("CONT", cont);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate( R.layout.fragment_gatetech_add_photo, container, false );
        //View v = inflater.inflate(android.R.layout.simple_list_item_1, null);
        ext_Create(view);
        // Inflate the layout for this fragment
        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction( uri );
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
        if (context instanceof OnFragmentInteractionListener) {
            mListener = ( OnFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException( context.toString() + " must implement OnFragmentInteractionListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    /******************************************************************************************** */

    private void setUIComponents(View rootView) {

        final Activity activity = getActivity();

        progressdialog = new ProgressDialog(activity);

        principalLayout = rootView;

        magicalPermissions = new MagicalPermissions(this, permissions);
        magicalCamera = new MagicalCamera(getActivity(), RESIZE_PHOTO_PIXELS_PERCENTAGE, magicalPermissions);
        magicalCamera.setResizePhoto(QUALITY_PHOTO_PERCENTAGE);

        mphotoViewer = (ImageView) rootView.findViewById(R.id.photoViewer);
        txtNote = (TextInputEditText) rootView.findViewById(R.id.txtNote);
        lblClient = (TextView )rootView.findViewById(R.id.lblCliente);
        lblClient.setText( "Cliente: "+ mClientId.name );

        btnPhotoButton = (ImageButton) rootView.findViewById(R.id.btnPhoto);
        btnGaleryButton = (ImageButton) rootView.findViewById(R.id.btnGalery);
        btnSaveSelection = (ImageButton) rootView.findViewById(R.id.btnSaveSelection);

        floatingBtnMenu= (FloatingActionMenu)  rootView.findViewById(R.id.floatingBtnMenu);
        floatingBtnRotate = (FloatingActionButton)  rootView.findViewById(R.id.floatingBtnRotate);
        floatingBtnPhotoInformation = (FloatingActionButton) rootView.findViewById(R.id.floatingBtnPhotoInformation);
        floatingBtnFullImage = (FloatingActionButton) rootView.findViewById(R.id.floatingBtnFullImage);

       panelSaveSelection = (LinearLayout) rootView.findViewById(R.id.panelSaveSelection);

    }


    private void ext_Create(View view) {
        final Activity activity = getActivity();

        Context context = activity.getApplicationContext();

        setUIComponents(view);
        // Buscamos el botón por su ID y creamos una referencia al mismo

        btnPhotoButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opción Cámara
                try{

                    if (isActivePermission(MagicalCamera.CAMERA)) {
                        //call the method of take the picture
                        magicalCamera.takeFragmentPhoto(AddPhotoFragment.this);
                    } else {
                        magical_utils.viewSnackBar(getString(R.string.error_not_have_permissions), principalLayout);
                    }


                } catch (Exception ex) {

                    String title="Error to magicalCamera.takeFragmentPhoto: ";

                    logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.GENERAL.toString(),get_manufacturer(),getContext());

                    Toast.makeText(getActivity().getApplicationContext(), title + ex.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });


        btnGaleryButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Opción Galería
                try{
                    if (isActivePermission(MagicalCamera.EXTERNAL_STORAGE)) {
                        //call the method of selected picture
                        magicalCamera.selectFragmentPicture(AddPhotoFragment.this, String.valueOf(R.string.Header));
                    } else {
                        com.gatetech.utils.magicalCamera.viewSnackBar(getString(R.string.error_not_have_permissions), principalLayout);
                    }
                } catch (Exception ex) {

                    String title="Error to magicalCamera.selectFragmentPicture: ";

                    logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.GENERAL.toString(),get_manufacturer(),getContext());

                    Toast.makeText(getActivity().getApplicationContext(), title + ex.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });


        btnSaveSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (magical_utils.validateMagicalCameraNull(activity, principalLayout, magicalCamera)) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                    dialog.setTitle( "Confirmar" );
                    dialog.setMessage( "Estas seguro de asociar la imagen seleccionada ?" );
                    dialog.setCancelable( false );
                    dialog.setPositiveButton( "Confirmar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    prepared_Photo();
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
            }
        });


        floatingBtnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (magical_utils.validateMagicalCameraNull(activity, principalLayout, magicalCamera)) {
                    //for once click rotate the picture in 90ª, and set in the image view.
                    magicalCamera.setPhoto(magicalCamera.rotatePicture(magicalCamera.getPhoto(), MagicalCamera.ORIENTATION_ROTATE_90));

                    mphotoViewer.setImageBitmap(magicalCamera.getPhoto());
                }
            }
        });

       floatingBtnPhotoInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (magical_utils.validateMagicalCameraNull(activity, principalLayout, magicalCamera)) {
                    //for see the data you need to call this method ever
                    //if the function return true you have the posibility of see the data
                    if (magicalCamera.initImageInformation()) {

                        StringBuilder builderInformation = new StringBuilder();

                        //question in have data
                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getLatitude() + ""))
                            builderInformation.append(getString(R.string.info_data_latitude) + magicalCamera.getPrivateInformation().getLatitude() + "\n");

                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getLatitudeReference()))
                            builderInformation.append(getString(R.string.info_data_latitude_referene) + magicalCamera.getPrivateInformation().getLatitudeReference() + "\n");

                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getLongitude() + ""))
                            builderInformation.append(getString(R.string.info_data_longitude)  + magicalCamera.getPrivateInformation().getLongitude() + "\n");

                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getLongitudeReference()))
                            builderInformation.append(getString(R.string.info_data_longitude_reference) + magicalCamera.getPrivateInformation().getLongitudeReference() + "\n");

                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getDateTimeTakePhoto()))
                            builderInformation.append(getString(R.string.info_data_date_time_photo) + magicalCamera.getPrivateInformation().getDateTimeTakePhoto() + "\n");

                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getDateStamp()))
                            builderInformation.append(getString(R.string.info_data_date_stamp_photo) + magicalCamera.getPrivateInformation().getDateStamp() + "\n");

                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getIso()))
                            builderInformation.append(getString(R.string.info_data_ISO) + magicalCamera.getPrivateInformation().getIso() + "\n");

                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getOrientation()))
                            builderInformation.append(getString(R.string.info_data_orientation_photo) + magicalCamera.getPrivateInformation().getOrientation() + "\n");

                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getImageLength()))
                            builderInformation.append(getString(R.string.info_data_image_lenght) + magicalCamera.getPrivateInformation().getImageLength() + "\n");

                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getImageWidth()))
                            builderInformation.append(getString(R.string.info_data_image_width) + magicalCamera.getPrivateInformation().getImageWidth() + "\n");

                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getModelDevice()))
                            builderInformation.append(getString(R.string.info_data_model_device) + magicalCamera.getPrivateInformation().getModelDevice() + "\n");

                        if (magical_utils.notNullNotFill(magicalCamera.getPrivateInformation().getMakeCompany()))
                            builderInformation.append(getString(R.string.info_data_make_company) + magicalCamera.getPrivateInformation().getMakeCompany() + "\n");

                        new MaterialDialog.Builder(activity)
                                .title(getString(R.string.message_see_photo_information))
                                .content(builderInformation.toString())
                                .positiveText(getString(R.string.message_ok))
                                .show();
                    } else {
                        magical_utils.viewSnackBar(getString(R.string.error_not_have_data), principalLayout);
                    }
                }
            }
        });


        floatingBtnFullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (magical_utils.validateMagicalCameraNull(activity, principalLayout, magicalCamera)) {
                    Utils.magicalCameraBitmap = magicalCamera.getPhoto();
                    fullscreen();
                }
            }
        });


        panelSaveSelection.setVisibility(View.INVISIBLE);


    }


    private void fullscreen(){
        if (Utils.magicalCameraBitmap != null) {

            Intent intent = new Intent(getActivity().getApplicationContext(), FullscreenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("source",Utils.BITMAP);
            intent.putExtras ( bundle );
            startActivity(intent);

        }
    }

    /**
     * SADB: Obtenemos el resultado de la seleccion de la camara o galeria de fotos
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            super.onActivityResult(requestCode, resultCode, data);
            //CALL THIS METHOD EVER
            magicalCamera.resultPhoto(requestCode, resultCode, data);

            if(resultCode == RESULT_OK) {

                if (MagicalCamera.SELECT_PHOTO == requestCode) {
                    // SADB: El path ya existe, se rescata
                    Utils.mphotoPath = getpathFile(data);
                }

                //SADB: copiamos en la variable de paso entre actividades
                Utils.magicalCameraBitmap = magicalCamera.getPhoto();
                // SADB: Se visualiza en imagebitmap
                mphotoViewer.setImageBitmap(Utils.magicalCameraBitmap);

                media_source = requestCode;

                // SADB: Enabled Floating button options
                floatingBtnMenu.setVisibility((Utils.magicalCameraBitmap != null ? View.VISIBLE : View.INVISIBLE));

                panelSaveSelection.setVisibility(View.VISIBLE);
            }

        }catch (Exception ex) {

            String title="Error onActivityResult: ";

            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.GENERAL.toString(),get_manufacturer(),getContext());

            Toast.makeText(getActivity().getApplicationContext(),title+ ex.toString(), Toast.LENGTH_SHORT).show();

        }

    }



    //verify if the permission is active
    private boolean isActivePermission(String permission) {
        //this map is return in method onRequestPermissionsResult for view what permissions are actives
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (mapPermissions != null) {
                if (mapPermissions.size() > 0) {
                    //obtain the code of camera permissions
                    return mapPermissions.get(permission);
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mapPermissions = magicalPermissions.permissionResult(requestCode, permissions, grantResults);

        for (String permission : mapPermissions.keySet()) {

            StringBuilder message = new StringBuilder();

            message.append("PERMISIONS ").append(permission).append(" was: ").append(mapPermissions.get(permission));

            logger.info(Utils.appUser.correo,"onResponse result: ",message.toString(),Utils.LOG_CATEGORY.PHOTO.toString(),get_manufacturer(),getContext());

        }
    }



    /******************************************************************************************** */

    private String getpathFile (Intent data){
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = this.getActivity().managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);

        return  selectedImagePath;
    }


    private void prepared_Photo () {

        try{

            // SADB: guardar bitmap !!!
            if (!((media_source == MagicalCamera.SELECT_PHOTO) && (Utils.mphotoPath.contains(DIRECTORY_NAME)))) {

                String name = "photo_C"+ mClientId.client+"A"+mAddressId;
                //save the picture in memory device, and return the physical path of this photo
                Utils.mphotoPath = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(), name, DIRECTORY_NAME,MagicalCamera.JPEG, true);

            }

            // SADB: Registrar photo en base de datos
            PhotoContext db = new PhotoContext(getContext());
            File f = new File(Utils.mphotoPath);
            String Note = txtNote.getText().toString();

            db.addPhoto(mAddressId,mClientId.client,f.getName(),Utils.mphotoPath,Note,Utils.ESTATUS.NO_SEND);


            AlertDialog.Builder builder;

            builder = new AlertDialog.Builder(getActivity());

            builder.setMessage("La photoItem/Imagen se guardo satisfactoriamente ")
                    .setTitle("Mensaje")
                    .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // SADB: Close Fragment
                            getActivity().getSupportFragmentManager().popBackStack();

                        }
                    });

            builder.create();
            builder.show();

        }catch (Exception ex) {

            String title="Error prepared_Photo: ";

            logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.PHOTO.toString(),get_manufacturer(),getContext());

            Toast.makeText(getActivity().getApplicationContext(),title+ ex.toString(), Toast.LENGTH_SHORT).show();
        }


    }

}

