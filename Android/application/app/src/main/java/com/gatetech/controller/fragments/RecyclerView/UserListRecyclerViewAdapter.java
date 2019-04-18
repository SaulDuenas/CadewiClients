package com.gatetech.controller.fragments.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gatetech.controller.fragments.UserListFragment;
import com.gatetech.cadewiclients.R;
import com.gatetech.content.UserContent;

import java.util.List;


/**
 * {@link RecyclerView.Adapter} that can display a {@link UserContent.UserItem} and makes a call to the
 * specified {@link UserListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class UserListRecyclerViewAdapter extends RecyclerView.Adapter<UserListRecyclerViewAdapter.ViewHolder> {

    private final List<UserContent.UserItem> mValues;
    private final UserListFragment.OnListFragmentInteractionListener mListener;

    public UserListRecyclerViewAdapter(List<UserContent.UserItem> items, UserListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gatetech_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mName.setText(mValues.get(position).nombre + " " + mValues.get(position).apellidos);
        holder.mMail.setText(mValues.get(position).correo);
        holder.mPerfil.setText(mValues.get(position).perfil);
        // holder.mContentView.setText(mValues.get(position).content);

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
        public final TextView mName;
        public final TextView mMail;
        public final TextView mPerfil;
        public UserContent.UserItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mName = (TextView) view.findViewById(R.id.lblName);
            mMail = (TextView) view.findViewById(R.id.lblMail);
            mPerfil = (TextView) view.findViewById(R.id.lblPerfil);
            //mContentView = (TextView) view.findViewById(R.id.content);
        }

        //  @Override
        // public String toString() {
        //    return super.toString() + " '" + mContentView.getText() + "'";
    }
}
