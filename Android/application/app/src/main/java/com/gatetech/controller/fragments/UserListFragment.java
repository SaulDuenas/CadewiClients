package com.gatetech.controller.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gatetech.cadewiclients.R;
import com.gatetech.content.UserContent;
import com.gatetech.utils.Utils;
import com.gatetech.controller.fragments.RecyclerView.UserListRecyclerViewAdapter;
import com.gatetech.restserver.Response.UsuariosResponse;
import com.gatetech.utils.popUp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private static View view=null;

    private  TextInputEditText txtSearch;
    private TextView lblresult;
    private Button btnfocus;
    private  RecyclerView userRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public UserListFragment() {

    }
    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserListFragment newInstance(int columnCount) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        //SADB:
        getActivity().setTitle(R.string.prompt_UserList_Title);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_gatetech_user_list, container, false);

        txtSearch = (TextInputEditText)this.view.findViewById(R.id.txtSearch);
        userRecyclerView = (RecyclerView)this.view.findViewById(R.id.userRecyclerView);
        btnfocus = (Button) this.view.findViewById(R.id.btnfocus);
        lblresult = (TextView) this.view.findViewById(R.id.lblresult);


        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_SEND) {
                    //Toast.makeText(getContext(), "Message: Tecla enter" , Toast.LENGTH_LONG).show();
                    //sendMessage();
                    searchFree();
                    handled = true;
                }
                return handled;
            }
        });

        searchFree();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(UserContent.UserItem item);
    }

    /**************************SADB: Methods Solutions ******************************/

    private void searchFree() {
        try {
            popUp.WaitMessageBox("En Progreso","Obteniendo Usuarios, espere un momento ....",getActivity());
        } catch (InterruptedException e) {
            popUp.CloseWaitMessageBox();
            popUp.AlertDialog(e.toString(),"Error !!!","Cerrar",getActivity());
        }

        // Clear Fragment
        UserContent.ITEMS.clear();
        UserContent.ITEM_MAP.clear();
        // SADB: Message to user

        TextInputEditText txtSearch =(TextInputEditText) this.view.findViewById(R.id.txtSearch);

        // SADB: Call to REST API
        String busqueda=txtSearch.getText().toString().trim();
        final Call<UsuariosResponse> searchUsers = Utils.mApiService.findUsers(busqueda);
        searchUsers.enqueue(new Callback<UsuariosResponse>() {
            @Override
            public void onResponse(Call<UsuariosResponse> call, Response<UsuariosResponse> response) {

                if (response.isSuccessful()) {
                    UsuariosResponse usuariosResponse = new UsuariosResponse();
                    usuariosResponse = response.body();

                    if (usuariosResponse.getEstado()==Utils.Status_OK) {

                        if (usuariosResponse.getUsuarios() != null) {
                            UserContent userobj = new UserContent();
                            Utils.fillUserContent(userobj, usuariosResponse.getUsuarios());
                        }
                    }
                    else if (usuariosResponse.getEstado()==Utils.Status_FAIL){
                        popUp.AlertDialog(usuariosResponse.getExcepcion(),"Mensaje","Aceptar",getActivity());
                    }


                }
                else  {
                    popUp.AlertDialog(response.message(),"Mensaje","Aceptar",getActivity());
                }

                // SADB: Refresh Fragment
                refreshFragment();
                popUp.CloseWaitMessageBox();
            }

            @Override
            public void onFailure(Call<UsuariosResponse> call, Throwable t) {
                popUp.CloseWaitMessageBox();
                Toast.makeText(getContext(), "Error: "+ t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void refreshFragment(){

        // SADB: Hide Keyborad
        InputMethodManager inputMethodManager =  (InputMethodManager) getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
        inputMethodManager.hideSoftInputFromWindow( btnfocus.getWindowToken(),0 );

        // Set the adapter
        // if (this.view instanceof RecyclerView) {
        Context context = this.view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.userRecyclerView); //  this.view;
        if (recyclerView != null) {
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(new UserListRecyclerViewAdapter(UserContent.ITEMS, mListener));

        }
        lblresult.setText( "Usuarios: " + UserContent.ITEMS.size() );
    }

}

