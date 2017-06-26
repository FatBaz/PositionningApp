package fr.utbm.lo53.p2017.positionningapp.network;


import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.util.Locale;

public class URLSolver implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "URLSolver;";

    private static final String HOSTNAME_KEY = "hostname";
    private static final String PORT_KEY = "port";

    private final String defaultHostname;
    private final String defaultPort;

    private String hostname;
    private int port;

    public URLSolver(SharedPreferences sharedPref, String defaultHostname, String defaultPort) {
        this.defaultHostname = defaultHostname;
        this.defaultPort = defaultPort;

        updateHostname(sharedPref);
        updatePort(sharedPref);
    }

    private void updateHostname(SharedPreferences sharedPref) {
        hostname =  sharedPref.getString(HOSTNAME_KEY, defaultHostname);
        Log.i(TAG, "Update host: " + hostname);
    }

    private void updatePort(SharedPreferences sharedPref) {
        port = Integer.valueOf(sharedPref.getString(PORT_KEY, defaultPort));
        Log.i(TAG, "Update port: " + port);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case HOSTNAME_KEY:  updateHostname(sharedPreferences); break;
            case PORT_KEY:      updatePort(sharedPreferences); break;
        }
    }

    private Uri.Builder baseURL() {
        Uri.Builder builder = new Uri.Builder();
        return builder.scheme("http").encodedAuthority(hostname + ":" + port).appendPath("positioning_server");
    }

    public String locateURL(String mac) {
        Uri.Builder b = baseURL();
        b.appendPath("positioning")
                .appendPath("request")
                .appendQueryParameter("CLIENT_MAC_ADDR", mac);
        return b.build().toString();
    }

    public String calibrateURL(String mac, float x, float y, long map_id) {
        Uri.Builder b = baseURL();
        b.appendPath("calibration")
                .appendPath("request")
                .appendQueryParameter("X", String.format(Locale.US, "%f",x))
                .appendQueryParameter("Y", String.format(Locale.US, "%f",y))
                .appendQueryParameter("CLIENT_MAC_ADDR", mac)
                .appendQueryParameter("MAP_ID", String.format("%d", map_id));
        return b.build().toString();
    }

    public String mapBytesURL(int mapId) {
        Uri.Builder b = baseURL();
        b.appendPath("map")
                .appendPath("byte")
                .appendQueryParameter("MAP_ID", String.format("%d", mapId));
        return b.build().toString();
    }
}
