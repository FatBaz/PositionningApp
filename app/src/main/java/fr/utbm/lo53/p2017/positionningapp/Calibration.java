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
import android.widget.Button;
import android.widget.ImageView;

public class Calibration extends AppCompatActivity {

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
        final Button setPointButton = (Button) findViewById(R.id.setPointButton);
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        final Button measureButton = (Button) findViewById(R.id.measureButton);

        MapMarker.setVisibility(MapMarker.INVISIBLE);
        cancelButton.setVisibility(cancelButton.INVISIBLE);
        cancelButton.setX(50);
        measureButton.setVisibility(measureButton.INVISIBLE);
        measureButton.setX(300);

        Map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                /*motionEvent.getAction()==motionEvent.ACTION_MOVE &&*/
                if( setPointButton.getVisibility() == setPointButton.INVISIBLE && motionEvent.getX() <= Map.getWidth() && motionEvent.getY() <= Map.getHeight() && motionEvent.getX() >= 0 && motionEvent.getY() >= 0) {
                    MapMarker.setVisibility(MapMarker.VISIBLE);
                    Snackbar.make(view, " X : " + motionEvent.getX() + " Y : " + motionEvent.getY(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    MapMarker.setX(motionEvent.getX()-(MapMarker.getWidth()/4));
                    MapMarker.setY(motionEvent.getY());
                }
                return true;
            }

        });
        setPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPointButton.setVisibility(setPointButton.INVISIBLE);
                cancelButton.setVisibility(cancelButton.VISIBLE);
                measureButton.setVisibility(measureButton.VISIBLE);
            }
        });
        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MapMarker.getVisibility()==MapMarker.INVISIBLE) {
                    Snackbar.make(v, "You need to set a point before measurement.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPointButton.setVisibility(setPointButton.VISIBLE);
                cancelButton.setVisibility(cancelButton.INVISIBLE);
                measureButton.setVisibility(measureButton.INVISIBLE);
                MapMarker.setVisibility(MapMarker.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calibration, menu);
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
}
