package com.pothiwala.atirek.accelerometerapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    CustomDrawableView mCustomDrawableView = null;
    public float xPosition, xAcceleration, xVelocity = 0.0f;
    public float yPosition, yAcceleration, yVelocity = 0.0f;
    private Bitmap mBitmap;
    private SensorManager sensorManager = null;
    public float frameTime = 0.666f;
    public float xmax = 0;
    public float ymax = 0;
    public float xS = 0;
    public float yS = 0;
    RelativeLayout relativeLayoutPane;

    ImageView iv_hole;
    Button btn_retry, btn_fire;

    MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayoutPane = (RelativeLayout) findViewById(R.id.relativeLayoutPane);
        iv_hole = (ImageView) findViewById(R.id.iv_hole);
        btn_retry = (Button) findViewById(R.id.btn_retry);
        btn_fire = (Button) findViewById(R.id.btn_fire);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        btn_retry.setVisibility(View.INVISIBLE);
        mCustomDrawableView = new CustomDrawableView(this);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        relativeLayoutPane.addView(mCustomDrawableView);

        btn_fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                }
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.bullet);
                mediaPlayer.start();
            }
        });

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

/*
                btn_retry.setVisibility(View.INVISIBLE);
                mCustomDrawableView = new CustomDrawableView(MainActivity.this);
                isRegistered = true;
                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
                relativeLayoutPane.addView(mCustomDrawableView);
*/

                Intent intent = getIntent();
                finish();
                startActivity(intent);


            }
        });

    }

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent) {
        {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                //Set sensor values as acceleration
                xAcceleration = sensorEvent.values[0];
                yAcceleration = sensorEvent.values[1];

                Log.d("AccelerationX: ", xAcceleration + "");
                Log.d("AccelerationY: ", yAcceleration + "");

            }
        }
    }


    // I've chosen to not implement this method
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        // Unregister the listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    public class CustomDrawableView extends View {

        public CustomDrawableView(Context context) {
            super(context);

            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_ball);

        }

        protected void onDraw(Canvas canvas) {

            xVelocity += (xAcceleration * frameTime);
            yVelocity += (yAcceleration * frameTime);

            //Calc distance travelled in that time
            xS = (xVelocity / 2) * frameTime;
            yS = (yVelocity / 2) * frameTime;

            //Add to position negative due to sensor
            //readings being opposite to what we want!

            xPosition = -xS * 3;
            Log.d("PositionX ", xPosition + "");
            yPosition = +yS * 3;
            Log.d("PositionY ", yPosition + "");

            if (mBitmap != null) {
                xmax = canvas.getWidth() - mBitmap.getWidth();
                ymax = canvas.getHeight() - mBitmap.getHeight();
            }

            if (xPosition > xmax) {
                xPosition = xmax;
            } else if (xPosition < 0) {
                xPosition = 0;
            }

            if (yPosition > ymax) {
                yPosition = ymax;
            } else if (yPosition < 0) {
                yPosition = 0;
            }

            if (mBitmap != null) {
                canvas.drawBitmap(mBitmap, xPosition, yPosition, null);

                if (xPosition >= canvas.getWidth() / 2 - iv_hole.getWidth() / 2 && xPosition <= canvas.getWidth() / 2 + iv_hole.getWidth() / 2
                        && yPosition >= canvas.getHeight() / 2 - iv_hole.getWidth() / 2 && yPosition <= canvas.getHeight() / 2 + iv_hole.getHeight() / 2) {
                    mBitmap = null;
                    btn_retry.setVisibility(VISIBLE);
                    btn_retry.bringToFront();
                    sensorManager.unregisterListener(MainActivity.this);
                    relativeLayoutPane.removeView(mCustomDrawableView);
                    mCustomDrawableView = null;

                }
            }

            invalidate();
        }
    }

}