package fr.utbm.lo53.p2017.positionningapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

public class PositioningActivity extends BaseActivity {

    private ConstraintLayout start_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Animation hideAnimation = createFadeOutAndHideAnimation();

        start_layout = (ConstraintLayout) findViewById(R.id.start_layout);
        start_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_layout.startAnimation(hideAnimation);;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.open_calibration_activity);
        item.setVisible(true);
        return true;
    }

    private Animation createFadeOutAndHideAnimation() {
        ImageView hiding_image = (ImageView) findViewById(R.id.hiding_image);
        float initial_alpha = hiding_image.getImageAlpha();
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
