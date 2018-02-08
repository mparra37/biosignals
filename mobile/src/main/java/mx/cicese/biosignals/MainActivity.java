package mx.cicese.biosignals;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements DataClient.OnDataChangedListener{

    String FILENAME = "datosAcc.txt";
    List<HeartRate> listaDatos = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String START_SENSING_PATH = "/start-sensing";
    private static final String STOP_SENSING_PATH = "/stop-sensing";
    private static final String COMENZAR_KEY = "com.example.key.comenzar";
    private static final String DETENER_KEY = "com.example.key.detener";
    private DataClient mDataClient;
    private static final String HR_KEY = "mx.cicese.key.hr";
    TextView tv_hearRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataClient = Wearable.getDataClient(this);

        tv_hearRate = (TextView) findViewById(R.id.tv_heartRate);

        HeartRate hr1 = new HeartRate(1, 20, 70);
        HeartRate hr2 = new HeartRate(2, 22, 77);
        HeartRate hr3 = new HeartRate(3, 24, 75);
        HeartRate hr4 = new HeartRate(4, 223, 80);
        HeartRate hr5 = new HeartRate(5, 2235, 73);

        listaDatos.add(hr1);
        listaDatos.add(hr2);
        listaDatos.add(hr3);
        listaDatos.add(hr4);
        listaDatos.add(hr5);

    }

    public void comenzar(View v){
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/comenzar");
        putDataMapReq.getDataMap().putInt(COMENZAR_KEY, 1);
        putDataMapReq.setUrgent();
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Task<DataItem> putDataTask = mDataClient.putDataItem(putDataReq);
        boolean respuesta = putDataTask.isSuccessful();
        Log.d("respueta", respuesta + " ");

    }


    public void detener(View v){
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/detener");
        putDataMapReq.getDataMap().putInt(DETENER_KEY, 2);
        putDataMapReq.setUrgent();
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Task<DataItem> putDataTask = mDataClient.putDataItem(putDataReq);
        boolean respuesta = putDataTask.isSuccessful();
        Log.d("respueta", respuesta + " ");
    }

    public void guardarArchivo(String nombre){
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/Sensapp");

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


    }

    public void guardar(View v){
//        String FILENAME = "datosAcc.txt";
//
//        guardarArchivo(FILENAME);
//
//        Toast.makeText(this, "Archivo guardado", Toast.LENGTH_LONG).show();
    }


    /**
     * Sends an RPC to start a fullscreen Activity on the wearable.
     */
    public void onStartWearableActivityClick(View view) {
        LOGD(TAG, "Generating RPC");

        // Trigger an AsyncTask that will query for a list of connected nodes and send a
        // "start-activity" message to each connected node.
        new StartWearableActivityTask().execute(START_ACTIVITY_PATH);
    }

    @WorkerThread
    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();

        Task<List<Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();



        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            List<Node> nodes = Tasks.await(nodeListTask);

            for (Node node : nodes) {
                results.add(node.getId());

            }

        } catch (ExecutionException exception) {
            Log.e(TAG, "Task failed: " + exception);

        } catch (InterruptedException exception) {
            Log.e(TAG, "Interrupt occurred: " + exception);
        }

        return results;
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/hr") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateHR(dataMap.getLongArray(HR_KEY));

                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {

            }
        }
    }

    public void updateHR(long[] values){
        tv_hearRate.setText(values[2]+"");
    }


    private class StartWearableActivityTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node, args[0]);
            }
            return null;
        }
    }

    @WorkerThread
    private void sendStartActivityMessage(String node, String arg) {

        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this).sendMessage(node, arg, new byte[0]);

        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            Integer result = Tasks.await(sendMessageTask);
            LOGD(TAG, "Message sent: " + result);

        } catch (ExecutionException exception) {
            Log.e(TAG, "Task failed: " + exception);

        } catch (InterruptedException exception) {
            Log.e(TAG, "Interrupt occurred: " + exception);
        }
    }

    private static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }
}
