package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStructure;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button startButton;
    private Button endButton;
    private EditText usernameTextBox;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;


    private ArrayList<Float> AccelX = new ArrayList<>();
    private ArrayList<Float> AccelY = new ArrayList<>();
    private ArrayList<Float> AccelZ = new ArrayList<>();
    private ArrayList<Float> GyroX = new ArrayList<>();
    private ArrayList<Float> GyroY = new ArrayList<>();
    private ArrayList<Float> GyroZ = new ArrayList<>();
    JSONArray jsonDataArray = new JSONArray();
    JsonArrayRequest jsonArrayRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get references to start and stop buttons
        startButton = findViewById(R.id.startTracking);
        endButton = findViewById(R.id.endTracking);
        usernameTextBox = findViewById(R.id.username);
        Timer postRequestScheduler = new Timer();


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        }
        else {
            Toast.makeText(this, "Sensor service not detected", Toast.LENGTH_SHORT).show();
        }

        // set click listeners on buttons
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameTextBox.getText().toString();

                postRequestScheduler.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("username = " + username);

                        System.out.println(AccelX);
                        System.out.println(AccelY);
                        System.out.println(AccelZ);
                        System.out.println(GyroX);
                        System.out.println(GyroY);
                        System.out.println(GyroZ);

                        sendPostRequest();
                    }
                }, 2000, 2000); // schedule the timer to run every 2 seconds


                sensorManager.registerListener(MainActivity.this, accelerometer, 100000);
                sensorManager.registerListener(MainActivity.this, gyroscope, 100000);
            }

        });
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("end button pressed");
                sensorManager.unregisterListener(MainActivity.this);
                postRequestScheduler.cancel();


                synchronized (AccelX) {
                AccelX.clear();
                }
                synchronized (AccelY) {
                AccelY.clear();
                }
                synchronized (AccelZ) {
                AccelZ.clear();
                }
                synchronized (GyroX) {
                GyroX.clear();
                }
                synchronized (GyroY) {
                    GyroY.clear();
                }
                synchronized (GyroZ) {
                    GyroZ.clear();
                }
            }

        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            System.out.println(event.values[0]); // Accel x-axis
//            System.out.println(event.values[1]); // Accel y-axis
//            System.out.println(event.values[2]); // Accel z-axis

            synchronized (AccelX){
            AccelX.add(event.values[0]);
            }
            synchronized (AccelY){
            AccelY.add(event.values[1]);
            }
            synchronized (AccelZ) {
            AccelZ.add(event.values[2]);
            }

        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//            System.out.println(event.values[0]); // Gyro x-axis
//            System.out.println(event.values[1]); // Gyro y-axis
//            System.out.println(event.values[2]); // Gyro z-axis

            synchronized (GyroX) {
            GyroX.add(event.values[0]);
            }

            synchronized (GyroY) {
            GyroY.add(event.values[1]);
            }

            synchronized (GyroZ) {
            GyroZ.add(event.values[2]);
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void sendPostRequest() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);


        try {

        String[] details = {"Harry"};


        synchronized (jsonDataArray) {


//            jsonDataArray.remove(0);
//            jsonDataArray.remove(1);
//            jsonDataArray.remove(2);
//            jsonDataArray.remove(3);
//            jsonDataArray.remove(4);
//            jsonDataArray.remove(5);
//            jsonDataArray.remove(6);

            jsonDataArray = new JSONArray();


            jsonDataArray.put(new JSONArray(AccelX));
            jsonDataArray.put(new JSONArray(AccelY));
            jsonDataArray.put(new JSONArray(AccelZ));
            jsonDataArray.put(new JSONArray(GyroX));
            jsonDataArray.put(new JSONArray(GyroY));
            jsonDataArray.put(new JSONArray(GyroZ));
            jsonDataArray.put(new JSONArray(details));

        }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        synchronized (AccelX) {
            AccelX.clear();
        }
        synchronized (AccelY) {
            AccelY.clear();
        }
        synchronized (AccelZ) {
            AccelZ.clear();
        }
        synchronized (GyroX) {
            GyroX.clear();
        }
        synchronized (GyroY) {
            GyroY.clear();
        }
        synchronized (GyroZ) {
            GyroZ.clear();
        }

        // Send a Post request to the api
        jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, "http://192.168.0.17:5000/predict", jsonDataArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println(response);
                        try {
                            String result = (String) response.get(0);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
                System.out.println(error);

            }
        });

        System.out.println(jsonDataArray.length());
        requestQueue.add(jsonArrayRequest);
    }
}