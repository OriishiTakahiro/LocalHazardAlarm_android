package com.example.takahiro.localhazardmap_01;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// fragment classes
import com.example.takahiro.localhazardmap_01.fragments.*;
import com.example.takahiro.localhazardmap_01.utility.DBAccesor;

public class BaseActivity extends FragmentActivity {

    public enum PageTag{HMAP(0), CONFIG(1), ALART(2);
        private final int id;
        private PageTag(final int id) {this.id =id;}
        public int getId() {return this.id;}
    }

    private FragmentManager frag_manager = null;
    private FragmentTransaction frag_transaction = null;
    private Fragment current_fragment = null;

    private String[] item_titles;
    private ListView item_list;
    private PageTag current_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // set NavigationDrawer
        this.item_titles = getResources().getStringArray(R.array.ITEM_LIST);
        this.item_list = (ListView)findViewById(R.id.left_drawer);
        this.item_list.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, item_titles));
        this.item_list.setOnItemClickListener(new DrawerItemClickListener());

        DBAccesor.getInstance(getApplicationContext()).updateOrganizations();

        swapFragment(PageTag.HMAP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void swapFragment(PageTag tag) {
        // FragmentManager and Fragment Transaction is need to each change of Fragment.
        this.frag_manager = getFragmentManager();
        this.frag_transaction = frag_manager.beginTransaction();

        switch(tag) {
            case HMAP:
                this.current_fragment = new HMapFragment();
                break;
            case CONFIG:
                this.current_fragment = new ConfigFragment();
                break;
        }
        this.frag_transaction.replace(R.id.content_frame, this.current_fragment);
        this.frag_transaction.addToBackStack(null);
        this.frag_transaction.commit();
        this.current_page = tag;
    }

    class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(current_page.getId() != position) {
                switch (position) {
                    case 0:
                        swapFragment(PageTag.HMAP);
                        break;
                    case 1:
                        break;
                    case 2:
                        swapFragment(PageTag.CONFIG);
                        break;
                }
            }
        }
    }
}
