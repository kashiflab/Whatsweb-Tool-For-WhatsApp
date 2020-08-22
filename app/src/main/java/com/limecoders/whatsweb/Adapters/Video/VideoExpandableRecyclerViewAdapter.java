package com.limecoders.whatsweb.Adapters.Video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.limecoders.whatsweb.Pojo.Media_File;
import com.limecoders.whatsweb.Pojo.Sorted_Media_Files;
import com.limecoders.whatsweb.R;

import com.limecoders.whatsweb.ViewHolders.Image.Video.HeaderViewHolder;
import com.limecoders.whatsweb.ViewHolders.Image.Video.childViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.model.ParentListItem;

import java.util.List;

public class VideoExpandableRecyclerViewAdapter extends ExpandableRecyclerAdapter<HeaderViewHolder, childViewHolder> {

    private LayoutInflater mInflator;
    private Context mContext;

    /**
     * Primary constructor. Sets up {@link #mParentItemList} and {@link #mItemList}.
     * <p>
     * Changes to {@link #mParentItemList} should be made through add/remove methods in
     * {@link ExpandableRecyclerAdapter}
     *
     * @param parentItemList List of all {@link ParentListItem} objects to be
     *                       displayed in the RecyclerView that this
     *                       adapter is linked to
     */
    public VideoExpandableRecyclerViewAdapter(Context mContext, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        this.mContext = mContext;
        mInflator = LayoutInflater.from(mContext);
    }


    @Override
    public void onBindParentViewHolder(HeaderViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        Sorted_Media_Files sorted_media_file = (Sorted_Media_Files) parentListItem;
        parentViewHolder.bind(sorted_media_file);
    }

    @Override
    public void onBindChildViewHolder(childViewHolder childViewHolder, int parentPosition, int childPosition, Object childListItem) {
        Media_File media_file = (Media_File) childListItem;
        childViewHolder.bind(mContext, media_file);

    }

    @Override
    public HeaderViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup, int viewType) {
        View headerView = mInflator.inflate(R.layout.list_header, parentViewGroup, false);
        return new HeaderViewHolder(headerView);
    }

    @Override
    public childViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {
        View childView = mInflator.inflate(R.layout.list_child, childViewGroup, false);
        return new childViewHolder(childView);
    }

}
