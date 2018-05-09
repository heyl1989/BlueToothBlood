package com.blue.blueapplication.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.blue.blueapplication.FrameApp;
import com.blue.blueapplication.R;
import com.blue.blueapplication.fragment.ContentFragment;
import com.blue.blueapplication.fragment.MenuFragment;
import com.blue.blueapplication.xmenu.XMenu;

public class MainActivity extends BaseActivity {

    private XMenu xMenu;
    private MenuFragment mMenuFragment;
    private ContentFragment mContentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xMenu = new XMenu(this);
        setContentView(xMenu);
        configContent();
        configMenu();
    }

    private void configMenu() {
        xMenu.setMenu(R.layout.menu_container);
        mMenuFragment = new MenuFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, mMenuFragment).commit();
        xMenu.setMenuWidth(3 * FrameApp.mApp.ui.getmScreenWidth() / 4);
    }

    private void configContent() {
        mContentFragment = new ContentFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContentFragment).commit();
        xMenu.setContent(R.layout.activity_main);
    }
    public void onFragmentInteraction(String id){

    }
    public void toggle() {
        xMenu.toggle();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                toggle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
