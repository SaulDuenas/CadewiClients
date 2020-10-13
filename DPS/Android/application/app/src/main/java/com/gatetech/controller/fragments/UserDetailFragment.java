package com.gatetech.controller.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gatetech.cadewiclients.R;
import com.gatetech.content.UserContent;
import com.gatetech.restserver.Response.UsuariosResponse;
import com.gatetech.utils.Utils;
import com.gatetech.utils.logger;
import com.gatetech.utils.popUp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gatetech.utils.Utils.get_manufacturer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserContent.UserItem mUserItem;

    private OnFragmentInteractionListener mListener;

    public UserDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDetailFragment newInstance(String param1, String param2) {
        UserDetailFragment fragment = new UserDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    static public  UserDetailFragment newInstance(UserContent.UserItem user) {
        UserDetailFragment fragment = new UserDetailFragment();

        fragment.mUserItem = user;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_user_detail, container, false);

        ConfigView(view);

        return view;
    }

    private void ConfigView(View rootView) {

        TextView txtName = (TextView) rootView.findViewById(R.id.txtName);
        TextView txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
        TextView txtProfile = (TextView) rootView.findViewById(R.id.txtProfile);

        if (mUserItem !=null) {

            txtName.setText(mUserItem.nombre + " " + mUserItem.apellidos);
            txtEmail.setText(mUserItem.correo);
            txtProfile.setText(mUserItem.perfil);
        }

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_user, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        Fragment myFragment = null;

        switch (item.getItemId()) {

            case R.id.menu_edit_user:
                //Toast.makeText(getContext(), "menu_edit_user ", Toast.LENGTH_SHORT).show();

                myFragment = AddNewUserFragment.newInstance(AddNewUserFragment.MODE_UPDATE,
                        AddNewUserFragment.JUMP_USERLIST,
                        mUserItem);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                        addToBackStack(null).commit();

                return true;

            case R.id.menu_delete_user:

                //Toast.makeText(getContext(), "menu_delete_user ", Toast.LENGTH_SHORT).show();

                // SADB: Check Permisisons to eliminate user
                if (Utils.appUser.perfil.toString().trim().equals( getResources().getString(R.string.user_root) )) {

                    android.app.AlertDialog.Builder dialog02 = new android.app.AlertDialog.Builder(getActivity());

                    dialog02.setTitle("Confirmar");
                    dialog02.setMessage("Estas seguro de eliminar el Usuario: " + mUserItem.nombre + " "+ mUserItem.apellidos + " ?");
                    dialog02.setCancelable(false);
                    dialog02.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            updateUser( mUserItem.correo,
                                        mUserItem.nombre,
                                        mUserItem.apellidos,
                                        mUserItem.password,
                                        mUserItem.perfil,
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
                    popUp.AlertDialog( getResources().getString(R.string.message_user), "Mensaje", "Cerrar", getActivity() );
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

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


        popUp.WaitMessageBox("En Progreso",iwait_message, getActivity());

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

                        close_fragment(iaccomplished_message,"Mensaje","Cerrar",getActivity());

                    }
                    else if (usuariosResponse.getEstado().equals(Utils.Status_FAIL)){

                        logger.error(Utils.appUser.correo,"Error HTTP: ",usuariosResponse.getExcepcion(),Utils.LOG_CATEGORY.USER.toString(),get_manufacturer(),getActivity());

                        close_fragment(usuariosResponse.getExcepcion(),"Mensaje","Cerrar",getActivity());

                    }
                }
                else  {

                    message.append("Error HTTP: ").append(response.code()).append(" ").append( response.message());

                    logger.error(Utils.appUser.correo,"Error HTTP: ",message.toString(),Utils.LOG_CATEGORY.USER.toString(),get_manufacturer(),getActivity());

                    close_fragment(message.toString(),"Error","Cerrar",getActivity());
                }

            }

            @Override
            public void onFailure(Call<UsuariosResponse> call, Throwable t) {

                String title = "Failure updateUser.enqueue: ";
                String msg = (t !=  null? t.getMessage() :"Oops something went wrong");

                logger.error(Utils.appUser.correo,title,msg,Utils.LOG_CATEGORY.USER.toString(),get_manufacturer(),getActivity());

                popUp.CloseWaitMessageBox();

                close_fragment(msg,title,"Cerrar",getActivity());

            }
        });

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
