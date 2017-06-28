package fr.utbm.lo53.p2017.positionningapp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Calibration extends BaseActivity {
    public static final String TAG = "Calibration";
    float height,width;
    long map_id;
    private ImageView MapMarker;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageView Map = (ImageView) findViewById(R.id.Map);
        MapMarker =  (ImageView) findViewById(R.id.MapMarker);
        final Button setPointButton = (Button) findViewById(R.id.setPointButton);
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        final Button measureButton = (Button) findViewById(R.id.measureButton);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        final TextView txt_spinner = (TextView) findViewById(R.id.txt_select_floor);

        queue = Volley.newRequestQueue(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.map_choices, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Map.setVisibility(View.INVISIBLE);
        setPointButton.setVisibility(View.INVISIBLE);
        MapMarker.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        measureButton.setVisibility(View.INVISIBLE);

        //Map.setMaxHeight(50);

        Map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                /*motionEvent.getAction()==motionEvent.ACTION_MOVE &&*/
                if( setPointButton.getVisibility() == View.INVISIBLE && motionEvent.getX() <= Map.getWidth() && motionEvent.getY() <= Map.getHeight() && motionEvent.getX() >= 0 && motionEvent.getY() >= 0) {
                    MapMarker.setVisibility(View.VISIBLE);
                    width = motionEvent.getX() / Map.getWidth();
                    height = motionEvent.getY() / Map.getHeight();
                    /*Snackbar.make(view, " X : " + height + " Y : " + width, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();*/
                    Log.i(TAG, String.format("Width: %f, height: %f", width, height));
                    MapMarker.setX(motionEvent.getX() - MapMarker.getWidth()/2 + Map.getX());
                    MapMarker.setY(motionEvent.getY() - MapMarker.getHeight() + Map.getY());
                }
                return true;
            }

        });
        setPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPointButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                measureButton.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                txt_spinner.setVisibility(View.INVISIBLE);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPointButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                measureButton.setVisibility(View.INVISIBLE);
                MapMarker.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.VISIBLE);
                txt_spinner.setVisibility(View.VISIBLE);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, String.format("Pos: %d, ID %d", position, id));
                map_id = id;
                Map.setVisibility(View.VISIBLE);
                setPointButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Map.setVisibility(View.INVISIBLE);
                setPointButton.setVisibility(View.INVISIBLE);
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

    public void sendCalibrateRequest(final View v) {
        if(MapMarker.getVisibility()== View.INVISIBLE) {
            Snackbar.make(v, "You need to set a point before measurement.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            StringRequest request = new StringRequest(Request.Method.GET, getURLSolver().calibrateURL(getMacAddress(), width,height,map_id),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Snackbar.make(v, "Calibration request sent successfully", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(v, "Error during calibration request", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    error.printStackTrace();
                }
            });

//            JsonRequest request = new JsonObjectRequest(Request.Method.GET,getURLSolver().calibrateURL(getMacAddress(), width,height,map_id),null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                String x =  response.getString("calibration");
//                                if(x.equals("try_again")){
//                                    Snackbar.make(v, "An error with the server occurred, please try again", Snackbar.LENGTH_LONG)
//                                            .setAction("Action", null).show();
//                                }else{
//                                    Snackbar.make(v, "Calibration completed", Snackbar.LENGTH_LONG)
//                                            .setAction("Action", null).show();
//                                }
//                            } catch (JSONException e) {
//                                Log.e(TAG, e.toString());
//                                Snackbar.make(v, "Could not parse the response !", Snackbar.LENGTH_LONG)
//                                        .setAction("Action", null).show();
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Snackbar.make(v, "Error during calibration request", Snackbar.LENGTH_LONG)
//                                    .setAction("Action", null).show();
//                            error.printStackTrace();
//                        }
//                    });

            queue.add(request);
            Log.i("TAG", String.format("Request : %s", request.getUrl()));
        }
    }

}
