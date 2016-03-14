package com.sirius.madness.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.sirius.madness.R;
import com.sirius.madness.beans.SessionBean;
import com.sirius.madness.ui.views.CustomFontTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 915649 on 16/02/15.
 */
public class RegisterParticipantAdapter extends ArrayAdapter {

    private List<SessionBean> session;
    private Context mContext;
    private int mResource;

    public RegisterParticipantAdapter(Context context, int resource, List<SessionBean> objects) {
        super(context, resource, objects);
        session = objects;
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View rowView = inflater.inflate(mResource, null, false);

        final SessionBean bean = session.get(position);

        LinearLayout registerListBackground = (LinearLayout) rowView.findViewById(R.id.registerItemBackground);
        LinearLayout registerListDarkBackground = (LinearLayout) rowView.findViewById(R.id.registerItemDarkBackground);

        int color = getColorForCell(position);
        int color2 = getColorForHeader(position);
        registerListBackground.setBackgroundColor(color);
        registerListDarkBackground.setBackgroundColor(color2);

        Date fromDate = new Date(bean.getFromTime());
        Log.d("FromTime:",String.valueOf(fromDate.getDate()));

        CustomFontTextView registerTime = (CustomFontTextView)rowView.findViewById(R.id.registerItemIconText);
        SimpleDateFormat fromTime = new SimpleDateFormat("h:mm a");
        registerTime.setText(fromTime.format(bean.getFromTime()));

        final CustomFontTextView sessionTitle = (CustomFontTextView)rowView.findViewById(R.id.SessionItem);

        sessionTitle.setText(bean.getSessionName());

        return rowView;
    }

    private int getColorForHeader(int index) {

        int red;
        int blue;
        int green;
        int alpha = 255;

        if (index > 0) {
            index = index % 2;
        }

        switch (index) {
            case 0:
                red = 241;
                green = 92;
                blue = 39;
                //result = R.color.theme_schedule_background_orange;
                break;
            case 1:
                red = 17;
                green = 100;
                blue = 138;
                //result = R.color.theme_schedule_background_blue;
                break;

            default:
                red = 241;
                green = 92;
                blue = 39;
                //result = R.color.theme_schedule_background_orange;
                break;
        }


        return Color.argb(alpha, red, green, blue);
    }

    private int getColorForCell(int index) {

        int red;
        int blue;
        int green;
        int alpha = 255;

        if (index > 0) {
            index = index % 2;
        }

        switch (index) {
            case 0:
                red = 248;
                green = 150;
                blue = 31;
                //result = R.color.theme_tertiary_light_orange;
                break;
            case 1:
                red = 25;
                green = 128;
                blue = 184;
                //result = R.color.theme_secondary_blue;
                break;

            default:
                red = 248;
                green = 150;
                blue = 31;
                //result = R.color.theme_tertiary_light_orange;
                break;
        }

        return Color.argb(alpha, red, green, blue);
    }

}
