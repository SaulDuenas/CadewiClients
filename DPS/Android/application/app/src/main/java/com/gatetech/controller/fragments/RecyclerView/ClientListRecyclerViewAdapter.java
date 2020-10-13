package com.gatetech.controller.fragments.RecyclerView;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gatetech.cadewiclients.R;
import com.gatetech.content.ClientContent;
import com.gatetech.controller.fragments.ClientListFragment;

import java.util.List;


/**
 * {@link RecyclerView.Adapter} that can display a {@link ClientContent.ClientItem} and makes a call to the
 * specified {@link ClientListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */

public class ClientListRecyclerViewAdapter extends RecyclerView.Adapter<ClientListRecyclerViewAdapter.ViewHolder> {

    private final List<ClientContent.ClientItem> mValues;
    private final ClientListFragment.OnListFragmentInteractionListener mListener;

    public ClientListRecyclerViewAdapter(List<ClientContent.ClientItem> items, ClientListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gatetech_client_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position); // Client Item

        holder.mName.setText(mValues.get(position).name);
        holder.mPhone.setText("Telefono: " +  mValues.get(position).Contacts.getItem( "telefono" ).value);
        holder.mMail.setText("Correo: " +  mValues.get(position).Contacts.getItem( "Correo" ).value);

        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }

        });
    }

    @Override
    public int getItemCount() {

        return mValues.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        // public final TextView mIdView;
        // public final TextView mContentView;

        public final TextView mName;
        public final TextView mPhone;
        public final TextView mMail;

        public ClientContent.ClientItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            //      mIdView = (TextView) view.findViewById(R.id.item_number);
            //      mContentView = (TextView) view.findViewById(R.id.content);

            mName = (TextView) view.findViewById(R.id.lblName);
            mPhone = (TextView) view.findViewById(R.id.lblPhone);
            mMail = (TextView) view.findViewById(R.id.lblMail);
        }

        //@Override
        // public String toString() { return super.toString() + " '" + mContentView.getText() + "'"; }
    }
}

