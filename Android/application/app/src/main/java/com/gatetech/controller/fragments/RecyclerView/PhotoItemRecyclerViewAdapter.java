package com.gatetech.controller.fragments.RecyclerView;


import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gatetech.cadewiclients.R;
import com.gatetech.content.PhotoContent;
import com.gatetech.controller.fragments.PhotoListFragment;


import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PhotoContent.PhotoItem} and makes a call to the
 * specified {@link PhotoListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PhotoItemRecyclerViewAdapter extends RecyclerView.Adapter<PhotoItemRecyclerViewAdapter.ViewHolder> {

    private final List<PhotoContent.PhotoItem> mValues;
    private final PhotoListFragment.OnListFragmentInteractionListener mListener;
    private Context context;

    public PhotoItemRecyclerViewAdapter(List<PhotoContent.PhotoItem> items, PhotoListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gatetech_photo_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            holder.mImageView.setBackgroundDrawable( null );
        }
        else{
            holder.mImageView.setBackground( null );
        }


        Glide.with( holder.mImageView.getContext())
                .load( holder.mItem.Path )
                .apply( new RequestOptions().placeholder( R.drawable.loading ) )
                .into(holder.mImageView);
        //  .placeholder(R.drawable.loading_spinner);

        if (holder.mItem.Note.isEmpty()){
            holder.midNotes.setVisibility(View.INVISIBLE);
        }
        else{
            holder.midNotes.setVisibility(View.VISIBLE);
            holder.midNotes.setText( holder.mItem.Note );
        }


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
        public final ImageView mImageView;
        public final TextView midNotes;

        public PhotoContent.PhotoItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mImageView = (ImageView) view.findViewById(R.id.imagePhoto);
            midNotes = (TextView ) view.findViewById(R.id.lblNotes);
            //   mContentView = (TextView) view.findViewById(R.id.content);
        }


    }
}
