package com.example.androidserviceexample;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static org.apache.commons.io.FileUtils.copyFile;

// import android.support.v7.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static String appFileDirectory;
    private static String executableFilePath;

    //button objects
    private Button buttonStart;
    private Button buttonStop;

    private void copyAssets(String filename) {

        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        Log.d(TAG, "Attempting to copy this file: " + filename); // + " to: " +       assetCopyDestination);

        try {
            in = assetManager.open(filename);
            File inFile = StreamUtil.stream2file(in);
            Log.d(TAG, "outDir: " + appFileDirectory);
            File outFile = new File(appFileDirectory, filename);
            out = new FileOutputStream(outFile);
            copyFile(inFile, outFile);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch(IOException e) {
            Log.e(TAG, "Failed to copy asset file: " + filename, e);
        }

        Log.d(TAG, "Copy success: " + filename);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Get app path
        appFileDirectory = getFilesDir().getPath();
        executableFilePath = appFileDirectory + "/mm2";

        //getting buttons from xml
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);

        //attaching onclicklistener to buttons
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);

        // In your app's project Java code: copy the executable file
        // from /assets folder into your app's "files" subfolder
        // (usually /data/data/your_app_name/files) with a function like this:
        //
        // See https://stackoverflow.com/a/18753500
        this.copyAssets("coins");
        this.copyAssets("mm2");

        // List files in folder
        //Creating a File object for directory
        File directoryPath = new File(appFileDirectory);
        //List of all files and directories
        String contents[] = directoryPath.list();
        System.out.println("List of files and directories in the specified directory:");
        for(int i=0; i<contents.length; i++) {
            System.out.println(contents[i]);
        }

        // Change the file permissions on executable_file to actually make it executable. Do it with Java calls:
        File execFile = new File(executableFilePath);
        execFile.setExecutable(true);
        // Execute the file like this
        try {
            Process process = Runtime.getRuntime().exec(new String[]{executableFilePath, "{\"gui\":\"MM2GUI\",\"netid\":9999, \"passphrase\":\"YOUR_PASSPHRASE_HERE\", \"rpc_password\":\"YOUR_PASSWORD_HERE\"}"});

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
    public void onClick(View view) {
        if (view == buttonStart) {
            //starting service
            startService(new Intent(this, MyService.class));
        } else if (view == buttonStop) {
            //stopping service
            stopService(new Intent(this, MyService.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
