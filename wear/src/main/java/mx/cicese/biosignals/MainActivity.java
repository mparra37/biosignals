package mx.cicese.biosignals;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends WearableActivity implements SensorEventListener, DataClient.OnDataChangedListener {

    private static final String TAG = "MainActivity";
    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    Sensor accelerometer;
    SensorEventListener sensorEventListener;
    List<HeartRate> listaDatos = new ArrayList<>();
    List<Accelerometer> listaAcc = new ArrayList<>();
    static int counter = 0;
    static int counterAcc = 0;
    private TextView mTextViewHeart;
    private String usuario = "Mario";
    private Button botonIniciar, botonDetener;
    private DataClient mDataClient;
    private static final String HR_KEY = "mx.cicese.key.hr";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewHeart = (TextView) findViewById(R.id.tv_heartrate);
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        botonIniciar = (Button) findViewById(R.id.botonComenzar);
        botonDetener = (Button) findViewById(R.id.botonDetener);

        Wearable.getDataClient(this).addListener(this);

        Toast.makeText(this, "Create", Toast.LENGTH_LONG).show();
        mDataClient = Wearable.getDataClient(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String accion = extras.getString("accion");

            if (accion.equalsIgnoreCase("comenzar")){
                botonIniciar.performClick();

            }

            if (accion.equalsIgnoreCase("detener")){
                botonDetener.performClick();
            }
        }


        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/comenzar") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    botonIniciar.performClick();
                    //updateCount(dataMap.getInt(COUNT_KEY));
                }
                if (item.getUri().getPath().compareTo("/detener") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    botonDetener.performClick();
                    //updateCount(dataMap.getInt(COUNT_KEY));
                }



            } else if (event.getType() == DataEvent.TYPE_DELETED) {

            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            int ritmo = (int)event.values[0];
            int accuracy = event.accuracy;
            Long time = System.currentTimeMillis() + ((event.timestamp- SystemClock.elapsedRealtimeNanos())/1000000L);
            mTextViewHeart.setText(ritmo+"");
            HeartRate hr = new HeartRate();
            hr.id = counter;
            counter++;
            hr.time = time;
            hr.ritmo = ritmo;
            hr.accuracy = accuracy;
            hr.IBI = (double) ritmo/60;
            listaDatos.add(hr);
            Client myClient = new Client("192.168.0.10", 9898, hr.toString());
            myClient.execute();
            //sendHRtoPhone(hr.getLongArray());
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double valueX = (double)event.values[0];
            double valueY = (double)event.values[1];
            double valueZ = (double)event.values[2];
            int accuracy = event.accuracy;
            Long time = System.currentTimeMillis() + ((event.timestamp- SystemClock.elapsedRealtimeNanos())/1000000L);
            Accelerometer acc = new Accelerometer();
            acc.id = counterAcc;
            counterAcc++;
            acc.time = time;
            acc.valueX = valueX;
            acc.valueY = valueY;
            acc.valueZ = valueZ;
            acc.accuracy = accuracy;

            listaAcc.add(acc);
           // Client myClient = new Client("192.168.0.10", 9898, hr.toString());
           // myClient.execute();
            //sendHRtoPhone(hr.getLongArray());
        }
        else
            Log.d(TAG, "Unknown sensor type");
    }

    public void sendHRtoPhone(long[] values){
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/hr");
        putDataMapReq.getDataMap().putLongArray(HR_KEY, values);
        putDataMapReq.setUrgent();
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Task<DataItem> putDataTask = mDataClient.putDataItem(putDataReq);
        boolean respuesta = putDataTask.isSuccessful();
        Log.d("respueta", respuesta + " ");
    }


    public void iniciar(View v){
        Toast.makeText(this, "iniciando sesión", Toast.LENGTH_SHORT).show();
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Long tiempoInicio = System.currentTimeMillis();
        Long tiempoClockNano = SystemClock.elapsedRealtimeNanos();
    }

    public void detener(View v){
        Toast.makeText(this, "sesión detenida", Toast.LENGTH_SHORT).show();

        String timestamp = String.valueOf(System.currentTimeMillis());
        String FILENAME = "HR_" + timestamp + ".txt";
        guardarArchivo(FILENAME);

        String FILENAME2 = "ACC_" + timestamp + ".txt";
        guardarArchivo2(FILENAME2);
        mTextViewHeart.setText("HR");
        mSensorManager.unregisterListener(this);

    }


    public void guardarArchivo(String nombre){
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/Biosignals");

        if (!dir.exists()){
            boolean res = dir.mkdir();
            Log.d("res", res+"");
        }

        File file = new File(dir.getAbsoluteFile() + File.separator + nombre);

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            for (HeartRate hr : listaDatos) {
                myOutWriter.append(hr.toString() + "\n");
            }

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Archivo guardado", Toast.LENGTH_SHORT).show();

    }

    public void guardarArchivo2(String nombre){
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/Biosignals");

        if (!dir.exists()){
            boolean res = dir.mkdir();
            Log.d("res", res+"");
        }

        File file = new File(dir.getAbsoluteFile() + File.separator + nombre);

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            for (Accelerometer acc : listaAcc) {
                myOutWriter.append(acc.toString() + "\n");
            }

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(this, "Archivo guardado", Toast.LENGTH_SHORT).show();

    }


}
