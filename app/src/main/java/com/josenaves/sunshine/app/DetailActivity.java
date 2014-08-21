package com.josenaves.sunshine.app;

import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {

    public static final String SUNSHINE_APP = " #SunshineApp";

    private ShareActionProvider shareActionProvider;

    private static String forecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        // locate menu item with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // fetch and store ShareActionProvider
        shareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(item);

        // defines a share intent
        shareActionProvider.setShareIntent(createShareIntent());

        return true;
    }

    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, forecast + SUNSHINE_APP);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // call to update the share intent
    private void setShareActionProvider(Intent intent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(intent);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // get data passed by previous activity
            Intent intent = getActivity().getIntent();
            if (null != intent && intent.hasExtra(Intent.EXTRA_TEXT))  {
                forecast = intent.getStringExtra(Intent.EXTRA_TEXT);
                TextView detail = (TextView) rootView.findViewById(R.id.text_detail);
                detail.setText(forecast);
            }

            return rootView;
        }
    }
}
