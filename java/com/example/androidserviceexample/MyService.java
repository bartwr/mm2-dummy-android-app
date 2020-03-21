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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Belal on 12/30/2016.
 */

public class MyService extends Service {
    //creating a mediaplayer object
    private MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    int executeCommandLine(String commandLine)
    {
        Log.d("executeCommandLine", commandLine);
        try {
            Process process = Runtime.getRuntime().exec(commandLine);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer output = new StringBuffer();
            char[] buffer = new char[4096];
            int read;

            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }

            reader.close();

            process.waitFor();

            Log.d("executeCommandLine", output.toString());

            return process.exitValue();
        } catch (IOException e) {
            throw new RuntimeException("Unable to execute '"+commandLine+"'", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Unable to execute '"+commandLine+"'", e);
        }
    }

    public void runAsRoot(String[] cmds) throws Exception {
        Process p = Runtime.getRuntime().exec("exec");
        DataOutputStream os = new DataOutputStream(p.getOutputStream());
        InputStream is = p.getInputStream();
        for (String tmpCmd : cmds) {
            os.writeBytes(tmpCmd+"\n");
            int readed = 0;
            byte[] buff = new byte[4096];

            // if cmd requires an output
            // due to the blocking behaviour of read(...)
            boolean cmdRequiresAnOutput = true;
            if (cmdRequiresAnOutput) {
                while( is.available() <= 0) {
                    try { Thread.sleep(200); } catch(Exception ex) {}
                }

                while( is.available() > 0) {
                    readed = is.read(buff);
                    if ( readed <= 0 ) break;
                    String seg = new String(buff,0,readed);
                    Log.i("#>", seg);
                }
            }
        }
        os.writeBytes("exit\n");
        os.flush();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        player = MediaPlayer.create(getApplicationContext(), notification);
        player.setLooping(true);
        player.start();

//        this.executeCommandLine("/system/bin/ls /sdcard");
        String [] cmds = {"ls \n"};
        try {
            this.runAsRoot(cmds);
        } catch (Exception e) {
            e.printStackTrace();
        }

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