package com.example.androidserviceexample;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Belal on 12/30/2016.
 */

public class MyService extends Service {
    private static final String TAG = "MyService";
    private static String appFileDirectory;
    private static String executableFilePath;

    //creating a mediaplayer object
    private MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startMarketmaker() {

        // Get app path
        appFileDirectory = getFilesDir().getPath();
        executableFilePath = appFileDirectory + "/mm2";

        File directoryPath = new File(appFileDirectory);

        // Execute the file like this
        try {
            ProcessBuilder builder = new ProcessBuilder( executableFilePath, "{\"gui\":\"MM2GUI\",\"netid\":9999, \"passphrase\":\"YOUR_PASSPHRASE_HERE\", \"rpc_password\":\"YOUR_PASSWORD_HERE\"}");
            builder.directory(directoryPath); // this is where you set the root folder for the executable to run with
            builder.redirectErrorStream(true);
            Process process =  builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            process.waitFor();

            Log.d(TAG, "output: " + output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        player = MediaPlayer.create(getApplicationContext(), notification);
        player.setLooping(true);
        player.start();

        // Get app path
        appFileDirectory = getFilesDir().getPath();
        executableFilePath = appFileDirectory + "/mm2";

        this.startMarketmaker();

        //start sticky means service will be explicity started and stopped
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopping the player when service is destroyed
        player.stop();
    }
}