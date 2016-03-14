package com.sirius.madness.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private View view;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft(View view) {
    }

    public void onSwipeRight(View view) {
    }

    public boolean onTouch(View v, MotionEvent event) {
        view = v;
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 20;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight(view);
                else
                    onSwipeLeft(view);
                return true;
            }
            return false;
        }
    }
}
