package com.sirius.madness.ui.activities;

import android.os.Bundle;
import android.view.Menu;

import com.sirius.madness.R;

public class SolutionsGalleryActivity extends BaseActivity {

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_SOLUTIONS_GALLERY;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solutions_gallery);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
}
