/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.sirius.madness.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sirius.madness.R;
import com.sirius.madness.ui.activities.SpeakerInfoActivity;

import java.util.HashMap;

public class SpeakerFeedListAdapter extends RecyclerView.Adapter<SpeakerFeedListAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "SpeakerFeedListAdapter";

    private String[] nameDataSet;
    private String[] titleDataSet;
    private String[] organizationDataSet;
    private String[] presenterIdDataSet;
    private byte[][] imagesDataSet;
    private Context mContext;

    /**
     * Provide a reference to the type of views that we are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView speakerName;
        private final TextView speakerTitle;
        private final TextView speakerOrganization;
        private final ImageView speakerIcon;
        private final TextView speakerPresenterId;

        public ViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                    Intent speakerInfoIntent = new Intent(v.getContext(), SpeakerInfoActivity.class);
                    speakerInfoIntent.putExtra("presenterId", speakerPresenterId.getText());
                    ((Activity) v.getContext()).startActivityForResult(speakerInfoIntent, 0);
                }
            });

            speakerName = (TextView) v.findViewById(R.id.SpeakerFeedName);
            speakerTitle = (TextView) v.findViewById(R.id.SpeakerFeedTitle);
            speakerOrganization = (TextView) v.findViewById(R.id.SpeakerFeedOrganization);
            speakerIcon = (ImageView) v.findViewById(R.id.SpeakerFeedIcon);
            speakerPresenterId = (TextView) v.findViewById(R.id.SpeakerFeedPresenterId);
        }

        public TextView getNameTextView() {
            return speakerName;
        }
        public TextView getTitleTextView() { return speakerTitle; }
        public TextView getOrganizationTextView() { return speakerOrganization; }
        public ImageView getIconImageView() { return speakerIcon; }
        public TextView getSpeakerPresenterId() { return speakerPresenterId; }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public SpeakerFeedListAdapter(String[] dataSet) {

        nameDataSet = dataSet;
    }

    public SpeakerFeedListAdapter(Context context, HashMap<String, String[]> dataSet, byte[][] images) {
        mContext = context;
        nameDataSet = dataSet.get("names");
        titleDataSet = dataSet.get("titles");
        organizationDataSet = dataSet.get("organizations");
        presenterIdDataSet = dataSet.get("presenterId");
        imagesDataSet = images;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.speaker_feed_list_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getNameTextView().setText(nameDataSet[position]);
        viewHolder.getTitleTextView().setText(titleDataSet[position]);
        viewHolder.getOrganizationTextView().setText(organizationDataSet[position]);
        viewHolder.getSpeakerPresenterId().setText(presenterIdDataSet[position]);

        Bitmap bMap;

        Log.d(TAG, "Attempting to parse imageDataSet[" + position + "] into Bitmap");
        Log.d(TAG, "imageDataSet[" + position + "] = " + imagesDataSet[position]);
        if(imagesDataSet[position] == null) {
            bMap = null;
        }else {
            bMap = BitmapFactory.decodeByteArray(imagesDataSet[position], 0, imagesDataSet[position].length);
        }

        if(bMap != null) {
            Drawable image = new BitmapDrawable(mContext.getResources(), bMap);
            viewHolder.getIconImageView().setImageDrawable(image);
        }else {
            Log.d(TAG, "bMap is null");
        }
    }

    // Return the size of dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return nameDataSet.length;
    }

    @Override
    public void onClick(View v) {
        Intent speakerInfoIntent = new Intent(v.getContext(), SpeakerInfoActivity.class);
        ((Activity) v.getContext()).startActivityForResult(speakerInfoIntent, 0);
    }
}
