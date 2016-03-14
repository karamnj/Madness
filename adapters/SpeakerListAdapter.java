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
import com.sirius.madness.beans.SpeakerBean;
import com.sirius.madness.ui.activities.SpeakerInfoActivity;

import java.util.List;

public class SpeakerListAdapter extends RecyclerView.Adapter<SpeakerListAdapter.ViewHolder> {
    private static final String TAG = "SpeakerListAdapter";
    public static List<SpeakerBean> speakers;
    private Context mContext;

    /**
     * Provide a reference to the type of views that we are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getPosition();
                    SpeakerBean speaker = speakers.get(position);
                    Intent speakerInfoIntent = new Intent(v.getContext(), SpeakerInfoActivity.class);
                    speakerInfoIntent.putExtra("presenterId", String.valueOf(speaker.getSpeakerId()));
                    v.getContext().startActivity(speakerInfoIntent);
                }
            });
            textView = (TextView) v.findViewById(R.id.tvSpeakerName);
            imageView = (ImageView) v.findViewById(R.id.ivSpeakerImage);

        }

        public TextView getTextView() {
            return textView;
        }
        public ImageView getImageView(){
            return imageView;
        }

    }

    public SpeakerListAdapter(Context context, List<SpeakerBean> speakers) {
        this.speakers = speakers;
        this.mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.speaker_item_thumbnail, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTextView().setText(speakers.get(position).getFirstName()+" " + speakers.get(position).getLastName());
        Bitmap bMap = BitmapFactory.decodeByteArray(speakers.get(position).getImage(), 0, speakers.get(position).getImage().length);
        Drawable image = new BitmapDrawable(mContext.getResources(),bMap);
        viewHolder.getImageView().setImageDrawable(image);
    }

    // Return the size of dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return speakers.size();
    }
}
