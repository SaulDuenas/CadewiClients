package com.gatetech.controller.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gatetech.asyncServer.DataClientRestService;
import com.gatetech.asyncServer.DataImageRestService;
import com.gatetech.asyncServer.RestService;
import com.gatetech.cadewiclients.R;
import com.gatetech.content.AddressContent;
import com.gatetech.content.ClientContent;

import com.gatetech.controller.activities.MapsActivity;
import com.gatetech.restserver.Response.ClientesResponse;
import com.gatetech.restserver.Response.photoResponse;
import com.gatetech.utils.Utils;
import com.gatetech.utils.logger;
import com.gatetech.utils.popUp;

import java.io.IOException;
import java.util.List;


import static com.gatetech.utils.Utils.get_manufacturer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClientDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClientDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientDetailFragment extends Fragment implements DataClientRestService.OnRestServiceClientListener,
                                                              DataImageRestService.OnRestServiceImageListener  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Integer Position = 0;

    ImageView mimgViewClient=null;


    static public ClientContent.ClientItem mclientItem;

    private OnFragmentInteractionListener mListener;

    public ClientDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClientDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClientDetailFragment newInstance(String param1, String param2) {
        ClientDetailFragment fragment = new ClientDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    static public  ClientDetailFragment newInstance(ClientContent.ClientItem client) {
        ClientDetailFragment fragment = new ClientDetailFragment();

        fragment.mclientItem = client;

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);
        DataClientRestService.setClientListener(this);
        DataImageRestService.setImageListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client_detail, container, false);
       // return inflater.inflate(R.layout.fragment_client_detail, container, false);

        ConfigView(view);

        return view;
    }


    private void ConfigView(View rootView) {

        TextView txtName = (TextView) rootView.findViewById(R.id.txtName);
        TextView txtRfc = (TextView) rootView.findViewById(R.id.txtRFC);

        TextView txtPhone = (TextView) rootView.findViewById(R.id.txtphone);
        TextView txtEmail = (TextView) rootView.findViewById(R.id.txtemail);

        TextView txtStreet = (TextView) rootView.findViewById(R.id.txtstreet);
        TextView txtZipCode = (TextView) rootView.findViewById(R.id.txtzipCode);
        TextView txtDelegation = (TextView) rootView.findViewById(R.id.txtDelegation);
        TextView txtState = (TextView) rootView.findViewById(R.id.txtState);

        CheckBox chkLocation= (CheckBox) rootView.findViewById(R.id.chkLocation);
        mimgViewClient = (ImageView)rootView.findViewById(R.id.imageClient);

        if (mclientItem !=null) {

            txtName.setText(mclientItem.name);

            if (mclientItem.rfc.isEmpty() || mclientItem.rfc == null) {
                txtRfc.setVisibility(View.INVISIBLE);
            }

            else {
                txtRfc.setText(mclientItem.rfc);
            }

            txtPhone.setText(mclientItem.Contacts.getItem( "telefono" ).value );
            txtEmail.setText(mclientItem.Contacts.getItem( "Correo" ).value);

            if (mclientItem.Address.ITEMS.size() > 0) {


                AddressContent.AddressItem address =  mclientItem.Address.ITEMS.get(Position);
                txtStreet.setText(address.street);
                txtZipCode.setText(address.zipCode + ", " + address.Neighborhood);
                //txtNeighborhood.setText(address.Neighborhood);
                txtDelegation.setText(address.Delegation);
                txtState.setText(address.State);
                // txtCity.setText(address.City);
                String message_chk = getString(address.LocEditable ==  1 ? R.string.unregistered_location:R.string.registered_location);

                chkLocation.setText(message_chk);
                chkLocation.setEnabled(address.LocEditable ==  1 ? false:true);

                DataImageRestService.getPhotos(getActivity(),address.address);

            }

        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_client, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        Fragment myFragment = null;

        switch (item.getItemId()) {

            case R.id.menu_galery_client:
                // Toast.makeText(getContext(), "menu_galery_client ", Toast.LENGTH_SHORT).show();
                myFragment = PhotoListFragment.newInstance( mclientItem, Utils.appUser );
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                        addToBackStack(null).commit();
                return true;

            case R.id.menu_add_image:
                // Toast.makeText(getContext(), "menu_galery_client ", Toast.LENGTH_SHORT).show();


                // Se cuenta con dirección ?
                if ((mclientItem.Address != null && mclientItem.Address.ITEMS.size()>0)) {
                    AddressContent.AddressItem addressItem = mclientItem.Address.ITEMS.size()>0? mclientItem.Address.ITEMS.get( Position ): new AddressContent.AddressItem();

                    if (addressItem.address != null && addressItem.address != 0) {
                        myFragment = AddPhotoFragment.newInstance(mclientItem);

                        if (myFragment != null) {
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, myFragment).addToBackStack(null).commit();
                        }
                    }

                }

                return true;


            case R.id.menu_location_client:
              //  Toast.makeText(getContext(), "menu_location_client ", Toast.LENGTH_SHORT).show();

                // Se cuenta con dirección ?
                if ((mclientItem.Address != null && mclientItem.Address.ITEMS.size()>0)) {

                    try {
                        Intent intent = null;
                        intent = MapsActivity.newInstance(getContext(),mclientItem,mclientItem.Address.ITEMS.get(Position));
                        startActivity( intent );
                    } catch (IOException ex) {
                        String title="Error "+ MapsActivity.class +": ";
                        logger.error(Utils.appUser.correo,title,ex, Utils.LOG_CATEGORY.MAPS.toString(),get_manufacturer(), getContext());
                    }

                }
                else {
                    popUp.AlertDialog( "El cliente no cuenta con una dirección que asociar","Mensaje","Cerrar",getActivity() );
                }


                return true;

            case R.id.menu_edit_client:
                // SADB: Launch Add Client Fragment - MODE VISOR  ( Client Detail )
                myFragment = AddNewClientFragment.newInstance(AddNewClientFragment.MODE_UPDATE, AddNewClientFragment.JUMP_CLIENTLIST, mclientItem);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                        addToBackStack(null).commit();
                //Toast.makeText(getContext(), "menu_edit_client ", Toast.LENGTH_SHORT).show();

               // break;
                return true;

            case R.id.menu_delete_client:
                //Toast.makeText(getContext(), "menu_delete_client ", Toast.LENGTH_SHORT).show();

                // SADB: Check Permisisons to eliminate user
                if (Utils.appUser.perfil.toString().trim().equals( getResources().getString(R.string.user_root) )) {

                    android.app.AlertDialog.Builder dialog02 = new android.app.AlertDialog.Builder(getContext());

                    dialog02.setTitle( "Confirmar" );
                    dialog02.setMessage( "Estas seguro de eliminar el Cliente: " + mclientItem.name + "?");
                    dialog02.setCancelable( false );
                    dialog02.setPositiveButton( "Confirmar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    popUp.WaitMessageBox("En Progreso","Eliminando cliente ... ", getActivity());
                                    // rutina de eliminaciòn de usuario
                                    AddressContent.AddressItem addressItem = mclientItem.Address.ITEMS.size()>0? mclientItem.Address.ITEMS.get(Position): new AddressContent.AddressItem();

                                    DataClientRestService.updateClient(getActivity(),
                                                                        mclientItem.client.toString(),
                                                                        addressItem.address.toString(),
                                                                        mclientItem.name,
                                                                        mclientItem.rfc,
                                                                        mclientItem.userMail,
                                                                        mclientItem.Contacts.getItem( "telefono" ).value,
                                                                        mclientItem.Contacts.getItem( "Correo" ).value,
                                                                        addressItem.street,
                                                                        addressItem.zipCode,
                                                                        addressItem.Neighborhood,
                                                                        addressItem.Delegation,
                                                                        addressItem.State,
                                                                        addressItem.City,
                                                                        addressItem.Longitude,
                                                                        addressItem.Latitude,
                                                                        addressItem.LocEditable.toString(),
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
                    popUp.AlertDialog( getResources().getString(R.string.message_user), "Mensaje", "Cerrar", getActivity() );
                }


                return true;

                default:
                return super.onOptionsItemSelected(item);

        }

    }


    private void close_fragment(String message, String title, String strbutton, Activity activity) {

        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(activity);

        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(strbutton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // SADB: Close actual fragment
                        getActivity().getFragmentManager().popBackStack();
                    }
                });

        builder.create();
        builder.show();

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        popUp.CloseWaitMessageBox();
    }

    @Override
    public void onDataRestClientResponse(RestService.Action action, RestService.Status result, String message, ClientesResponse.Cliente clientItem) {
        popUp.CloseWaitMessageBox();

        String title=(result == RestService.Status.SUCCESSFUL)?"Procedimiento exitoso":"Error en Procedimiento";
        String msg = (result == RestService.Status.SUCCESSFUL)?"Cliente eliminado exitosamente":message;

        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(getContext());

        builder.setMessage(msg)
                .setTitle(title)
                .setIcon(R.drawable.alert_circle)
                .setPositiveButton("cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog.cancel();
                        getActivity().getFragmentManager().popBackStack();

                    }
                });

        builder.create();
        builder.show();

    }

    @Override
    public void onDataRestClientResponse(RestService.Action action, RestService.Status result, String message, List<ClientesResponse.Cliente> clientItemList) {

    }

    @Override
    public void onDataRestImageResponse(RestService.Action action, RestService.Status result, String message, List<photoResponse.photoItem> photoList) {

        if (photoList != null && photoList.size()>0 && mimgViewClient != null) {
            photoResponse.photoItem item = photoList.get(Position);

            Glide.with( getContext())
                    .load( item.getRuta() )
                    .apply( new RequestOptions().placeholder( R.drawable.camera_off ) )
                    .into(mimgViewClient);
        }

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
}
