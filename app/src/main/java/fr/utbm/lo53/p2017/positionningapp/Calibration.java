package fr.utbm.lo53.p2017.positionningapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class Calibration extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Salut ! :) Ce bouton ne sert à rien.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final ImageView Map = (ImageView) findViewById(R.id.Map);
        final ImageView MapMarker =  (ImageView) findViewById(R.id.MapMarker);

        MapMarker.setVisibility(MapMarker.INVISIBLE);

        Map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==motionEvent.ACTION_MOVE) {
                    MapMarker.setVisibility(MapMarker.VISIBLE);
                    Snackbar.make(view, " X : " + motionEvent.getX() + " Y : " + motionEvent.getY(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    MapMarker.setX(motionEvent.getX()-(MapMarker.getWidth()/4));
                    MapMarker.setY(motionEvent.getY()-(MapMarker.getHeight()/4));
                }
                return true;
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.open_locate_activity);
        item.setVisible(true);
        return true;
    }

}
