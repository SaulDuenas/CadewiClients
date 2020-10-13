package com.gatetech.controller.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import com.gatetech.cadewiclients.R;
import com.gatetech.content.ClientContent;
import com.gatetech.content.PhotoContent;
import com.gatetech.content.UserContent;
import com.gatetech.utils.Utils;
import com.gatetech.controller.fragments.RecyclerView.PhotoItemRecyclerViewAdapter;
import com.gatetech.restserver.Response.photoResponse;
import com.gatetech.utils.logger;
import com.gatetech.utils.popUp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gatetech.utils.Utils.get_manufacturer;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PhotoListFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener  {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private static View view=null;

    private static TextView lblTitle;
    private static FloatingActionButton btnAddPhoto;

    private static UserContent.UserItem appUser;
    private static ClientContent.ClientItem mclientId;
    private static Integer maddressId =null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PhotoListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PhotoListFragment newInstance(ClientContent.ClientItem client, UserContent.UserItem appUser) {
        PhotoListFragment fragment = new PhotoListFragment();

        PhotoListFragment.appUser = appUser;
        PhotoListFragment.mclientId = client;

        maddressId=0;
        if (mclientId.Address.ITEMS.size()>0){
            maddressId = mclientId.Address.ITEMS.get(0).address;
        }


        //Bundle args = new Bundle();
        //args.putInt(ARG_COLUMN_COUNT, columnCount);
        //fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        //SADB:
        Utils.STR_001 =  getActivity().getTitle().toString();
        getActivity().setTitle(R.string.prompt_PhototList_Title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_gatetech_photo_list, container, false);

        ext_oncreate();

        getPhotos();

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
        //SADB:
        mListener = null;
        getActivity().setTitle(Utils.STR_001 );
    }


    @Override
    public void onRefresh() {

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
        void onListFragmentInteraction(PhotoContent.PhotoItem item);
    }


    /******************************** Methods Solutions ************************************/

    private void ext_oncreate() {

        lblTitle = (TextView) view.findViewById(R.id.lblClient);
        lblTitle.setText("Cliente: " + mclientId.name);

        btnAddPhoto = (FloatingActionButton) view.findViewById(R.id.btnAddPhoto);
        btnAddPhoto.setEnabled( maddressId > 0?true:false );

        btnAddPhoto.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (maddressId != null && maddressId != 0) {
                    Fragment myFragment = AddPhotoFragment.newInstance( mclientId );

                    if (myFragment!=null){
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,myFragment).addToBackStack(null).commit();
                    }
                }
            }
        } );

    }

    private void refreshFragment() {

        // Set the adapter
        Context context = this.view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.PhotoRecyclerView);

        if (recyclerView != null) {
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new PhotoItemRecyclerViewAdapter( PhotoContent.ITEMS, mListener));

            /***
             * onscroll*/

            //recyclerView.scrollToPosition(0);
/*
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()

            );
*/

        }

        if (PhotoContent.ITEMS.isEmpty()) {
            popUp.AlertDialog( "No se encontraron imagenes asociadas a la dirección", getResources().getString(R.string.pop_up_title_message), getResources().getString(R.string.pop_up_button_close), getActivity() );

        }

    }




    /******************************** Methods Solutions ************************************/


    private void getPhotos() {

        popUp.WaitMessageBox( "En Progreso", "Obteniendo galeria, espere un momento ....", getActivity() );

        PhotoContent photocontent = new PhotoContent();
        //SADB: Clear Fragment
        PhotoContent.ITEMS.clear();
        PhotoContent.ITEM_MAP.clear();

        if (maddressId != 0 && maddressId != null) {

            // SADB: Get Photos from Sqlite
            PhotoContent.ITEMS.addAll( Utils.getPtohoItemList(maddressId,getContext()) );


            // SADB: get Photos from RestServer
            final Call<photoResponse> callFotos = Utils.mApiService.getPhotosbyAddress( maddressId.toString() );
            callFotos.enqueue(new Callback<photoResponse>() {
                @Override
                public void onResponse(Call<photoResponse> call, Response<photoResponse> response) {

                    if (response.isSuccessful()) {
                        photoResponse photoresponse = new photoResponse();
                        photoresponse = response.body();


                        if(photoresponse.getEstado().equals(Utils.Status_OK)) {
//                            photoContent = Utils.getPhotoItemList(photoresponse.getPhotoItems());
                            PhotoContent.ITEMS.addAll(Utils.getPhotoItemList(photoresponse.getPhotoItems()));
                        }

                        else {
                       //     popUp.AlertDialog( photoresponse.getExcepcion(), getResources().getString(R.string.pop_up_title_message), getResources().getString(R.string.pop_up_button_close), getActivity() );
                        }

                    }

                    else {
                        StringBuilder message = new StringBuilder();
                        message.append("Error HTTP: ").append(response.code()).append(" ").append( response.message());

                        logger.error(Utils.appUser.correo,"Error HTTP on PhotoListFragment: ",message.toString(),Utils.LOG_CATEGORY.PHOTO.toString(),get_manufacturer(), getContext());

                        popUp.AlertDialog( message.toString(), getResources().getString(R.string.pop_up_title_message), getResources().getString(R.string.pop_up_button_close), getActivity() );
                    }

                    // SADB: Refresh Fragment
                    refreshFragment();

                    popUp.CloseWaitMessageBox();
                }

                @Override
                public void onFailure(Call<photoResponse> call, Throwable t) {

                    popUp.CloseWaitMessageBox();
                    // SADB: Refresh Fragment
                    refreshFragment();

                    Toast.makeText(getContext(), "Error: "+ t.getMessage(), Toast.LENGTH_LONG).show();

                }

            });
        }
        else {
            popUp.CloseWaitMessageBox();
            popUp.AlertDialog( "No se encontro registro de dirección para el cliente", getResources().getString(R.string.pop_up_title_message), getResources().getString(R.string.pop_up_button_close), getActivity() );
        }

    }

}
