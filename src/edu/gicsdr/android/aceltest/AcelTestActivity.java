package edu.gicsdr.android.aceltest;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AcelTestActivity extends Activity implements SensorEventListener {
	/** Called when the activity is first created. */
	private double[] linear_acceleration = new double[3],
			gravity = new double[3];
	private double[] dist = new double[3], vel = new double[3];
	private double totalDist;
	private TextView tvXac, tvYac, tvZac;
	private TextView tvXdt, tvYdt, tvZdt, tvTdt;
	private SensorManager sensorManager;
	private Sensor mSensor;
	private boolean mSensorRegistered;
	private long lastTime = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tvXac = (TextView) findViewById(R.id.tvXac);
		tvYac = (TextView) findViewById(R.id.tvYac);
		tvZac = (TextView) findViewById(R.id.tvZac);
		tvXdt = (TextView) findViewById(R.id.tvXdt);
		tvYdt = (TextView) findViewById(R.id.tvYdt);
		tvZdt = (TextView) findViewById(R.id.tvZdt);
		tvTdt = (TextView) findViewById(R.id.tvTdt);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		mSensorRegistered = sensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
		for (int i = 0; i < 3; i++)
			dist[i] = vel[i] = gravity[i] = 0.0;
		totalDist = 0.;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mSensorRegistered) {
			sensorManager.unregisterListener(this, mSensor);
			mSensorRegistered = false;
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mSensorRegistered) {
			sensorManager.unregisterListener(this, mSensor);
			mSensorRegistered = false;
		}
	}

	private void actAccelLabel() {
		tvXac.setText(String.format("%.2f", linear_acceleration[0]));
		tvYac.setText(String.format("%.2f", linear_acceleration[1]));
		tvZac.setText(String.format("%.2f", linear_acceleration[2]));
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

		synchronized (this) {

			if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
				final float alpha = 0.8f;
				for (int i = 0; i < 3; i++)
					gravity[i] = alpha * gravity[i] + (1 - alpha)
							* event.values[i];
				for (int i = 0; i < 3; i++)
					linear_acceleration[i] = event.values[i] - gravity[i];
				actAccelLabel();
				if (lastTime != 0) {
					calcDist(event.timestamp - lastTime);
					actDistLabel();
				}
				lastTime = event.timestamp;

			}
		}
	}

	public void OnClickRestart(View v) {
		for (int i = 0; i < 3; i++)
			dist[i] = vel[i] = gravity[i] = 0.0;
		totalDist = 0.;
		actDistLabel();
	}

	private void actDistLabel() {
		tvXdt.setText(String.format("%.2f", dist[0]));
		tvYdt.setText(String.format("%.2f", dist[1]));
		tvZdt.setText(String.format("%.2f", dist[2]));
		tvTdt.setText(String.format("%.2f", totalDist));
	}

	private void calcDist(long deltaTime) {
		double delta = deltaTime * 1e-9;
		for (int i = 0; i < 3; i++)
			dist[i] += vel[i] * delta + delta * delta * linear_acceleration[i]
					* 0.5;

		totalDist = Math.sqrt(dist[0] * dist[0] + dist[1] * dist[1] + dist[2]
				* dist[2]);
		for (int i = 0; i < 3; i++)
			vel[i] += linear_acceleration[i] * delta;
	}
}