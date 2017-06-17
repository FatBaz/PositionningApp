package fr.utbm.lo53.p2017.positionningapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import fr.utbm.lo53.p2017.positionningapp.network.URLSolver;

public abstract class BaseActivity extends AppCompatActivity {

    private URLSolver urlSolver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String defaultHostname = getString(R.string.pref_default_hostname);
        String defaultPort = getString(R.string.pref_default_port);
        urlSolver = new URLSolver(PreferenceManager.getDefaultSharedPreferences(this), defaultHostname, defaultPort);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(getURLSolver());
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(getURLSolver());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.open_locate_activity) {
            Intent intent = new Intent(this, PositioningActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.open_calibration_activity) {
            Intent intent = new Intent(this, Calibration.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calibration, menu);
        return true;
    }

    public URLSolver getURLSolver() {
        return urlSolver;
    }

}
