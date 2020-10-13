package com.gatetech.controller.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gatetech.asyncServer.DataClientRestService;
import com.gatetech.asyncServer.RestService;
import com.gatetech.cadewiclients.R;
import com.gatetech.content.ClientContent;
import com.gatetech.restserver.Response.ClientesResponse;
import com.gatetech.utils.Utils;
import com.gatetech.controller.fragments.RecyclerView.ClientListRecyclerViewAdapter;

import com.gatetech.utils.popUp;

import java.util.List;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ClientListFragment extends Fragment implements DataClientRestService.OnRestServiceClientListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private static View view=null;


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

        setHasOptionsMenu(true);

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

        configView (this.view);

        DataClientRestService.setClientListener(this);
        // SADB: Obtain Clients from REST API
        findClients("");
        //refreshFragment();

        return view;
    }


    private void configView(View view) {

        //SADB:
        getActivity().setTitle(R.string.prompt_ClientList_Title);

        clientRecyclerView = (RecyclerView) view.findViewById(R.id.clientRecyclerView);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);

        SearchView searchView = (SearchView) menu.findItem(R.id.simpleSearchView).getActionView();    //  (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                findClients(query);

                //Toast.makeText(getContext(), "Press onQueryTextSubmit text: " + query, Toast.LENGTH_SHORT).show();

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                //Toast.makeText(getContext(), "Press onQueryTextChange ", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        Fragment myFragment = null;

        switch (item.getItemId()) {

            case R.id.menu_add_item:


                myFragment = AddNewClientFragment.newInstance(AddNewClientFragment.MODE_REGISTER,
                        AddNewClientFragment.JUMP_CLIENTLIST,
                        null);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                        addToBackStack(null).commit();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }




/*
    public static void hideSoftKeyboard (Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
*/

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

    private void findClients(String token) {
        try {
                popUp.WaitMessageBox( "En Progreso", "Obteniendo Clientes, espere un momento ....", getActivity() );

                //SADB: Clear Fragment
                ClientContent.ITEMS.clear();
                ClientContent.ITEM_MAP.clear();

                DataClientRestService.findClients(getActivity(),Utils.appUser.correo,Utils.appUser.perfil,token.trim());
        }
        catch (Exception ex) {
             Toast.makeText(getContext(), "Error: " +  ex.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onDataRestClientResponse(RestService.Action action, RestService.Status result, String message, ClientesResponse.Cliente clientItem) {

    }

    @Override
    public void onDataRestClientResponse(RestService.Action action, RestService.Status result, String message, List<ClientesResponse.Cliente> clientItemList) {
        popUp.CloseWaitMessageBox();

        if (result.equals(RestService.Status.SUCCESSFUL) && !clientItemList.isEmpty() ) {

            ClientContent clientcontent = new ClientContent();
            Utils.fillClientContent(clientcontent, clientItemList);
            // SADB: Refresh Fragment
            refreshFragment();

        }
        else if (result.equals(RestService.Status.SUCCESSFUL) && clientItemList != null && clientItemList.isEmpty()) {

            AlertDialog.Builder builder;

            builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(message)
                    .setTitle("Mensaje")
                    .setIcon(R.drawable.alert_circle)
                    .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // dialog.cancel();

                        }
                    });

            builder.create();
            builder.show();

        }
        else if (result.equals(RestService.Status.FAILRESPONSE) || result.equals(RestService.Status.FAILURE)) {
            AlertDialog.Builder builder;

            builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(message)
                    .setTitle("ERROR !!!")
                    .setIcon(R.drawable.alert_circle)
                    .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // dialog.cancel();

                        }
                    });

            builder.create();
            builder.show();

        }


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

    }

}
