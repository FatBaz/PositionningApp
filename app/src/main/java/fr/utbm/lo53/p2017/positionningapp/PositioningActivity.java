package fr.utbm.lo53.p2017.positionningapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PositioningActivity extends BaseActivity {

    private static final String TAG = "PositioningActivity";

    private ConstraintLayout start_layout;
    private TextView textView;
    private ImageView mapView;

    private Animation hideAnimation;

    // Instantiate the RequestQueue.
    private RequestQueue queue;

    private boolean isLocating = false;

    private Integer mapId = null;

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Create request
            JsonObjectRequest locateRequest = new JsonObjectRequest(Request.Method.GET,
                    getURLSolver().locateURL(getMacAddress()),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                float x = (float) response.getDouble("x");
                                float y = (float) response.getDouble("y");
                                int mapId = response.getInt("map_id");
                                if (hasCorrectMap(mapId)) {
                                    putPoint(x, y);
                                    textView.setText("");
                                } else {
                                    getMap(mapId);
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, e.toString());
                                textView.setText("Could not parse the response !");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Remove random position
                    Random r = new Random();
                    putPoint(r.nextFloat(), r.nextFloat());
                    textView.setText("Error: " + error.getMessage());
                    Log.i(TAG, "Error during location request");
                }
            });
            Log.v(TAG, "" + locateRequest);
            queue.add(locateRequest);

            handler.postDelayed(runnable, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        start_layout = (ConstraintLayout) findViewById(R.id.start_layout);
        initFadeOutAndHideAnimation();

        textView = (TextView) findViewById(R.id.ID_yolo);

        mapView = (ImageView) findViewById(R.id.map);

        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLocating) {
            handler.post(runnable);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.open_calibration_activity);
        item.setVisible(true);
        return true;
    }

    public void startLocating(View v) {
        Log.i(TAG, v.toString());
        if (isLocating) {
            return;
        }

        isLocating = true;
        start_layout.startAnimation(hideAnimation);

        handler.post(runnable);
    }

    private void initFadeOutAndHideAnimation() {
        ImageView hiding_image = (ImageView) findViewById(R.id.hiding_image);
        final float initial_alpha = hiding_image.getImageAlpha() / 255;
        Animation animation = new AlphaAnimation(initial_alpha, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(1000);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                start_layout.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        hideAnimation = animation;
    }

    private void putPoint(float x, float y) {
        ImageView mapMarker = (ImageView) findViewById(R.id.map_marker);
        float x_on_map = (float) mapView.getWidth() * x;    // in px
        float y_on_map = (float) mapView.getHeight() * y;
        mapMarker.setX(mapView.getX() + x_on_map - mapMarker.getWidth()/2);
        mapMarker.setY(mapView.getY() + y_on_map - mapMarker.getHeight());
        mapMarker.setVisibility(View.VISIBLE);
        mapView.setVisibility(mapView.VISIBLE);
    }

    private boolean hasCorrectMap(int mapId) {
        return (this.mapId != null && this.mapId.equals(mapId));
    }

    private void getMap(int mapId) {
        ImageRequest mapRequest = new ImageRequest(
            getURLSolver().mapDataURL(mapId),
            new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    mapView.setImageBitmap(bitmap);
                    mapView.setVisibility(mapView.VISIBLE);
                }
            }, 0, 0, null,
            new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    mapView.setImageResource(R.drawable.map2);
                    textView.setText("Can't load map!!!");
                }
        });
        queue.add(mapRequest);
    }

    protected String getMacAddress() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Get MAC address prior to android 6.0
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            return wInfo.getMacAddress();
        } else {
            // Removed in Android 6.0 -> manually get MAC address
            try {
                List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {
                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        //res1.append(Integer.toHexString(b & 0xFF) + ":");
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return "02:00:00:00:00:00";
        }
    }
}
