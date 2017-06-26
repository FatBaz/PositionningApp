package fr.utbm.lo53.p2017.positionningapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class PositioningActivity extends BaseActivity {

    private static final String TAG = "PositioningActivity";

    private ConstraintLayout start_layout;
    private ImageView mapView;
    private ImageView imageLoading;

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
                                int mapId = response.getInt("map");
                                imageLoading.setVisibility(View.GONE);
                                if (hasCorrectMap(mapId)) {
                                    putPoint(x, y);
                                } else {
                                    getMap(mapId);
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Could not parse the response: " + e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Remove random position
                    Random r = new Random();
                    putPoint(r.nextFloat(), r.nextFloat());
                    Snackbar.make(mapView, "Error during positioning request", Snackbar.LENGTH_LONG).show();
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

        mapView = (ImageView) findViewById(R.id.map);

        imageLoading = (ImageView) findViewById(R.id.img_loading);

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

        final Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotate.setDuration(1500);
        imageLoading.setVisibility(View.VISIBLE);
        imageLoading.startAnimation(rotate);

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
        mapView.setVisibility(View.VISIBLE);
    }

    private boolean hasCorrectMap(int mapId) {
        return (this.mapId != null && this.mapId.equals(mapId));
    }

    private void getMap(final int mapId) {
        mapView.setVisibility(View.INVISIBLE);
        ImageRequest mapRequest = new ImageRequest(
            getURLSolver().mapBytesURL(mapId),
            new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    PositioningActivity.this.mapId = mapId;
                    mapView.setImageBitmap(bitmap);
                    mapView.setVisibility(View.VISIBLE);
                    Snackbar.make(mapView, "You need to set a point before measurement.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }, 0, 0, null,
            new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(mapView, "Can't load map !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
        });
        queue.add(mapRequest);
    }
}
