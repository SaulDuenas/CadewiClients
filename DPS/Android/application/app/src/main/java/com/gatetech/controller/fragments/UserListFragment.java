package com.gatetech.controller.fragments;

import android.content.Context;
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

        setHasOptionsMenu(true);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_gatetech_user_list, container, false);

        configView (this.view);

        searchFree("");

        return this.view;
    }

    private void configView (View view) {

        //SADB:
        getActivity().setTitle(R.string.prompt_UserList_Title);
        userRecyclerView = (RecyclerView) view.findViewById(R.id.userRecyclerView);
//        searchFree("");
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

                searchFree(query);

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
                // Toast.makeText(getContext(), "menu_galery_client ", Toast.LENGTH_SHORT).show();

                if (Utils.appUser.perfil.toString().trim().equals( getResources().getString(R.string.user_root) )) {
                    //myFragment = new AddNewUserFragment();
                    myFragment = AddNewUserFragment.newInstance(AddNewUserFragment.MODE_REGISTER,
                            AddNewUserFragment.JUMP_USERLIST,
                            null);

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).
                            addToBackStack(null).commit();
                }
                else{
                    popUp.AlertDialog( getResources().getString(R.string.message_user), "Mensaje", "Cerrar", getActivity() );
                }


                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

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

    private void searchFree(String token) {

    //    try {
            popUp.WaitMessageBox( "En Progreso", "Obteniendo Usuarios, espere un momento ....", getActivity() );

   /*
        } catch (InterruptedException e) {
            popUp.CloseWaitMessageBox();
            popUp.AlertDialog( e.toString(), "Error !!!", "Cerrar", getActivity() );
        }
*/
        // Clear Fragment
        UserContent.ITEMS.clear();
        UserContent.ITEM_MAP.clear();
        // SADB: Message to user

        // SADB: Call to REST API

        final Call<UsuariosResponse> searchUsers = Utils.mApiService.findUsers(token.trim());
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

                popUp.CloseWaitMessageBox();
                // SADB: Refresh Fragment
                refreshFragment();

            }

            @Override
            public void onFailure(Call<UsuariosResponse> call, Throwable t) {
                popUp.CloseWaitMessageBox();
                Toast.makeText(getContext(), "Error: "+ t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void refreshFragment() {

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

    }

}

