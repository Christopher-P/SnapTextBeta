package net.azurewebsites.snaptextbeta.myshite4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    /****************Some constants to use throughout the program*********/
    public static final String PACKAGE_NAME = "net.azurewebsites.snaptextbeta.myshite4";
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/MainActivity/";
    public static final String _path = Environment.getExternalStorageDirectory().toString() + "/MainActivity/" + "/ocr.jpg";

    protected static final String PHOTO_TAKEN = "photo_taken";
    private static final String TAG = "MainActivity.java";
    public String lang = "eng";

    /***********************Used to hold results from OCR and Wolfram**************/
    public String WolfAns = null;
    public static String lastOCR = "What is the meaning of life?";           //Default used for debugging

    /*******************Objects located in our layout to use for UI***********/
    protected Button takePhoto;
    protected Button myButt;
    protected Button wolf;
    protected Button four;
    protected Button conduct;

    protected WebView webber;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        createDir();        //Creates a directory and places OCR "eng" data in there if it does not exist

        /***************Init Layout objects used***************/
        takePhoto = (Button) findViewById(R.id.button1);
        takePhoto.setOnClickListener(new ButtonClickHandler());
        conduct = (Button) findViewById(R.id.OCR);
        conduct.setOnClickListener(new ConductOCR());
        myButt = (Button) findViewById(R.id.button2);
        myButt.setOnClickListener(new IntentClickHandler());
        webber = (WebView) findViewById(R.id.webview);
        four = (Button) findViewById(R.id.button4);
        four.setOnClickListener(new LoadPage());
        wolf = (Button)findViewById(R.id.button3);

        // This is different from the others because it uses "multi-threading"
        wolf.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                new AccessWebServiceTask().execute();               //Create connection and get result from wolfram
                webber.loadData(WolfAns, "text/html", null);        //Load returned data
                webber.setVisibility(View.VISIBLE);                 //Set visibility to true (starts as false)
            }
        });
    }

    /*******Button Click handlers***********/

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            Intent myIntent = new Intent(MainActivity.this, TakePhoto.class);
            startActivity(myIntent);
        }
    }

    public class ConductOCR implements View.OnClickListener {
        public void onClick(View view){
            Intent myIntent = new Intent(MainActivity.this, OCR.class);
            startActivity(myIntent);
        }
    }

    public class IntentClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"christopher.pereyda@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "SnapTextBeta Result");
            i.putExtra(Intent.EXTRA_TEXT, lastOCR);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class LoadPage implements View.OnClickListener{
        public void onClick(View view){
            doIT();
        }
    }

    /******Create menubar******/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*****Create/load directory if it is nonexistant or empty*********/
    public void createDir(){
        // www.Gaut.am was here
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/"};

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.i(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.i(TAG, "Created directory " + path + " on sdcard");
                }
            }
        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {

            try {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open(lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                //Log.i(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                //Log.i(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }
    }

    /*********Used to create a connection through the interwebs***************/
    private InputStream OpenHttpConnection(String urlString) throws IOException
    {
        //Redundant to set to null BUT FOR MY COMPLETION IT IS NECESSARY
        URL url = null;
        HttpURLConnection urlConnection = null;

        InputStream in = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };

        try {
            url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            in = urlConnection.getInputStream();
            return in;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }

    //method to send information and pull back xml format response
    private void doIT() {
        String altered = lastOCR;               //make a replica of the last OCR taken
        altered.replace(" ", "%20");            //Wolfram standard input conversion
        altered.replace("?", "%3F");
        webber.loadUrl("http://api.wolframalpha.com/v2/query?appid=9JJ2U4-273VULH74E&input=" + altered + "&format=image,plaintext ");
        webber.setVisibility(View.VISIBLE);
    }

    /*******returns a result from alpha based on lastOCR**************/
    private String wolframAnswer(int currencyVal, String firstSelect, String secondSelect)
    {
        InputStream in = null;

        String strWolfReturn = "";

        String altered = lastOCR;
        altered.replace(" ", "%20");
        altered.replace("?", "%3F");

        try
        {
            URL url = new URL("http://api.wolframalpha.com/v2/query?appid=9JJ2U4-273VULH74E&input=" + altered + "&format=image,plaintext ");
            BufferedReader inn = new BufferedReader(new InputStreamReader(url.openStream()));

            String outLine;

            while ((outLine = inn.readLine()) != null) {
                strWolfReturn += outLine;
            }
        }
        catch(IOException io)
        {
            Log.d("Network activity", io.getLocalizedMessage());
        }


        //editText3.setText(strWolfReturn);
        return strWolfReturn;
    }

    //using async class to run a task similtaneously with the app without crashing it
    private class AccessWebServiceTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {
            WolfAns = wolframAnswer(100, "ZAR", "DOL");
            return WolfAns;
        }
        protected void onPostExecute(String result)
        {
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
        }
    }


    /*******Used to share content with other apps and things******/
    private void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Working");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, lastOCR);
        startActivity(Intent.createChooser(sharingIntent, "Share via..."));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new) {
            Intent myIntent = new Intent(MainActivity.this, TakePhoto.class);
            startActivity(myIntent);
        }
        if (id == R.id.action_recent) {
            return true;
        }
        if (id == R.id.action_share) {
            shareIt();
        }
        if (id == R.id.action_rate) {
            return true;
        }
        if (id == R.id.action_help) {
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_account) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}