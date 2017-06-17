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

public class Calibration extends BaseActivity {
    float height,width;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageView Map = (ImageView) findViewById(R.id.Map);
        final ImageView MapMarker =  (ImageView) findViewById(R.id.MapMarker);
        final Button setPointButton = (Button) findViewById(R.id.setPointButton);
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        final Button measureButton = (Button) findViewById(R.id.measureButton);

        MapMarker.setVisibility(MapMarker.INVISIBLE);
        cancelButton.setVisibility(cancelButton.INVISIBLE);
        measureButton.setVisibility(measureButton.INVISIBLE);

        //Map.setMaxHeight(50);

        Map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                /*motionEvent.getAction()==motionEvent.ACTION_MOVE &&*/
                if( setPointButton.getVisibility() == setPointButton.INVISIBLE && motionEvent.getX() <= Map.getWidth() && motionEvent.getY() <= Map.getHeight() && motionEvent.getX() >= 0 && motionEvent.getY() >= 0) {
                    MapMarker.setVisibility(MapMarker.VISIBLE);
                    height = motionEvent.getX();
                    width = motionEvent.getY();
                    Snackbar.make(view, " X : " + height + " Y : " + width, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    MapMarker.setX(motionEvent.getX()- (view.getWidth()- Map.getWidth())/2 -40);//- MapMarker.getWidth()/2);
                    MapMarker.setY(motionEvent.getY()+ (view.getHeight()/5));//+(MapMarker.getWidth()/(5/4)));
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
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.open_locate_activity);
        item.setVisible(true);
        return true;
    }

    public void sendCalibrateRequest(View v) {
        Snackbar.make(v, "Coucou, X :" + height + " Y : " + width, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
