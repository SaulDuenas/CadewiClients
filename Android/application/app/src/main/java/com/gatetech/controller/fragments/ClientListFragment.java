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
import com.gatetech.content.ClientContent;
import com.gatetech.utils.Utils;
import com.gatetech.controller.fragments.RecyclerView.ClientListRecyclerViewAdapter;
import com.gatetech.restserver.Response.ClientesResponse;
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
public class ClientListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private static View view=null;

    private  TextInputEditText mtxtSearch;
    private TextView lblresult;
    private Button btnfocus;
    private  RecyclerView clientRecyclerView;

    String mtoken="";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ClientListFragment() {

    }



    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ClientListFragment newInstance(int columnCount) {
        ClientListFragment fragment = new ClientListFragment();
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
        getActivity().setTitle(R.string.prompt_ClientList_Title);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_gatetech_client_list, container, false);

        mtxtSearch = (TextInputEditText)this.view.findViewById(R.id.txtSearch);
        clientRecyclerView = (RecyclerView)this.view.findViewById(R.id.clientRecyclerView);
        btnfocus = (Button) this.view.findViewById(R.id.btnfocus);
        lblresult = (TextView) this.view.findViewById(R.id.lblresult);

        mtxtSearch.setText("");

        mtxtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_SEND) {

                    findClients();
                    handled = true;
                }
                return handled;
            }
        });

        // SADB: Obtain Clients from REST API
        findClients();
        //refreshFragment();

        return view;
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
        void onListFragmentInteraction(ClientContent.ClientItem item);
    }



    /********************* Methods Solutions ******************************/

    private void findClients() {

        try {
            popUp.WaitMessageBox( "En Progreso", "Obteniendo Clientes, espere un momento ....", getActivity() );
        } catch (InterruptedException e) {
            popUp.CloseWaitMessageBox();
            popUp.AlertDialog( e.toString(), "Error !!!", "Cerrar", getActivity() );
        }

        //SADB: Clear Fragment
        ClientContent.ITEMS.clear();
        ClientContent.ITEM_MAP.clear();

        //TextInputEditText txtSearch =(TextInputEditText) this.view.findViewById(R.id.txtSearch);

        String token="";
        // SADB: Call to REST API

        if (mtoken != mtxtSearch.getText().toString().trim()) {
            token = mtxtSearch.getText().toString().trim();
            mtoken = token;
        }

        else {
            token = mtoken;
        }

        // SADB: Call to REST API
        final Call<ClientesResponse> callClients = Utils.mApiService.getClientsbyFind( Utils.appUser.correo,Utils.appUser.perfil,token);
        callClients.enqueue(new Callback<ClientesResponse>() {

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
                    else if (clientes.getEstado().equals(Utils.Status_FAIL)){
                        popUp.AlertDialog(clientes.getExcepcion(),"Mensaje","Aceptar",getActivity());
                    }
                }
                else  {
                    popUp.AlertDialog(response.message(),"Mensaje","Aceptar",getActivity());
                }

                popUp.CloseWaitMessageBox();
                // SADB: Refresh Fragment
                refreshFragment();
            }

            @Override
            public void onFailure(Call<ClientesResponse> call, Throwable t) {
                popUp.CloseWaitMessageBox();
                Toast.makeText(getContext(), "Error: "+ t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } );

    }


    private void refreshFragment() {

        // Set the adapter
        Context context = this.view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.clientRecyclerView);

        if (recyclerView != null) {

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new ClientListRecyclerViewAdapter(ClientContent.ITEMS, mListener));
        }

        lblresult.setText( "Clientes: " + ClientContent.ITEMS.size() );

        // SADB: Hide Keyborad
        InputMethodManager inputMethodManager =  (InputMethodManager) getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
        inputMethodManager.hideSoftInputFromWindow( btnfocus.getWindowToken(),0 );

        mtxtSearch.clearFocus();

    }

}
