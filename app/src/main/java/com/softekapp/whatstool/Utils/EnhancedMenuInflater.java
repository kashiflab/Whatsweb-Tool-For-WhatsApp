package com.softekapp.whatstool.Utils;

import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.core.internal.view.SupportMenuItem;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.softekapp.whatstool.R;

/**
 * Created by amal on 11/04/16.
 */
public class EnhancedMenuInflater {
    public static void inflate(MenuInflater inflater, Menu menu, boolean forceVisible) {
        inflater.inflate(R.menu.menu_main, menu);

        if (!forceVisible) {
            return;
        }

        int size = menu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            // check if app:showAsAction = "ifRoom"
            if (((MenuItemImpl) item).requestsActionButton()) {
                item.setShowAsAction(SupportMenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        }
    }
}
