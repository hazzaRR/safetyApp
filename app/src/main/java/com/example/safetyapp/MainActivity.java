package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private boolean captureAccelData = false;
    private boolean captureGyroData = false;
    private ArrayList<Float> AccelX;
    private ArrayList<Float> AccelY;
    private ArrayList<Float> AccelZ;
    private ArrayList<Float> GyroX;
    private ArrayList<Float> GyroY;
    private ArrayList<Float> GyroZ;


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
                    }
                }, 0, 2000); // schedule the timer to run every 2 seconds


                sensorManager.registerListener(MainActivity.this, accelerometer, 1000000);
                sensorManager.registerListener(MainActivity.this, gyroscope, 1000000);
            }

        });
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("end button pressed");
                sensorManager.unregisterListener(MainActivity.this);
                postRequestScheduler.cancel();
            }

        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.out.println(event.values[0]); // Accel x-axis
            System.out.println(event.values[1]); // Accel y-axis
            System.out.println(event.values[2]); // Accel z-axis

            AccelX.add(event.values[0]);
            AccelY.add(event.values[1]);
            AccelZ.add(event.values[2]);

        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            System.out.println(event.values[0]); // Gyro x-axis
            System.out.println(event.values[1]); // Gyro y-axis
            System.out.println(event.values[2]); // Gyro z-axis

            GyroX.add(event.values[0]);
            GyroY.add(event.values[1]);
            GyroZ.add(event.values[2]);

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}