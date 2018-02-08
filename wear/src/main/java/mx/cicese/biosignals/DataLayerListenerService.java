package mx.cicese.biosignals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Mario on 02/02/2018.
 */

public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayerService";
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String START_SENSING_PATH = "/start-sensing";
    private static final String STOP_SENSING_PATH = "/stop-sensing";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        LOGD(TAG, "onMessageReceived: " + messageEvent);

        // Check to see if the message is to start an activity
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
            Intent startIntent = new Intent(this, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
            Toast.makeText(this,"Message received", Toast.LENGTH_LONG).show();
        }

        if (messageEvent.getPath().equals(START_SENSING_PATH)) {
            Intent startIntent = new Intent(this, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle extras = new Bundle();
            extras.putString("accion","comenzar");
//            startIntent.putExtra("accion", "comenzar");
            startIntent.putExtras(extras);
            startActivity(startIntent);
            Toast.makeText(this,"Start sensing", Toast.LENGTH_LONG).show();
        }

        if (messageEvent.getPath().equals(STOP_SENSING_PATH)) {
            Intent startIntent = new Intent(this, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle extras = new Bundle();
            extras.putString("accion","detener");
//            startIntent.putExtra("accion", "detener");
            startIntent.putExtras(extras);
            startActivity(startIntent);
            Toast.makeText(this,"Stop sensing", Toast.LENGTH_LONG).show();
        }
    }

    public static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

}
