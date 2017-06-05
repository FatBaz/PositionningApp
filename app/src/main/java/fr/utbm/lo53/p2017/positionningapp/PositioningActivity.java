package fr.utbm.lo53.p2017.positionningapp;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class PositioningActivity extends BaseActivity {

    private static final String TAG = "PositioningActivity";

    private ConstraintLayout start_layout;
    private TextView textView;

    private Animation hideAnimation;

    // Instantiate the RequestQueue.
    private RequestQueue queue;

    private String url = "http://www.google.com";

    // Request a string response from the provided URL.
    private StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    textView.setText("Response is: "+ response.substring(0,500));
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, error.getCause().toString());
            textView.clearComposingText();
        }
    });

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        start_layout = (ConstraintLayout) findViewById(R.id.start_layout);
        hideAnimation = createFadeOutAndHideAnimation();

        textView = (TextView) findViewById(R.id.ID_yolo);

        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.open_calibration_activity);
        item.setVisible(true);
        return true;
    }

    public void startLocating(View v) {
        start_layout.startAnimation(hideAnimation);
        handler.post(runnable);
    }

    private Animation createFadeOutAndHideAnimation() {
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
        return animation;
    }
}
