package net.azurewebsites.snaptextbeta.myshite4;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.*;
import com.googlecode.leptonica.android.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
    public static final String PACKAGE_NAME = "net.azurewebsites.snaptextbeta.myshite4";
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/MainActivity/";


    // You should have the trained data file in assets folder
    // You can get them at:
    // http://code.google.com/p/tesseract-ocr/downloads/list
    public static final String lang = "eng";

    private static final String TAG = "MainActivity.java";

    public static String lastOCR = "NULL";

    protected Button takePhoto;
    protected Button myButt;
    protected Button wolf;

    protected String _path;
    protected boolean _taken;

    EditText editText;
    EditText editText2;
    EditText editText3;

    protected static final String PHOTO_TAKEN = "photo_taken";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        createDir();
    /*
        editText = (EditText)findViewById(R.id.textBox);
        editText2 = (EditText)findViewById(R.id.textBox2);
        editText3 = (EditText)findViewById(R.id.textBox3);
        */

        takePhoto = (Button) findViewById(R.id.button1);
        takePhoto.setOnClickListener(new ButtonClickHandler());
        myButt = (Button) findViewById(R.id.button2);
        myButt.setOnClickListener(new IntentClickHandler());

        _path = DATA_PATH + "/ocr.jpg";


        /***WOLFRAM SECTION*******/
        //spinner implementation from here down
        wolf = (Button)findViewById(R.id.button3);


        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, currency);


        //using httpget to send request to server.


        wolf.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                //apiSend();
                new AccessWebServiceTask().execute();
                //wolframAnswer(0,"","");
            }

        });

        /***EMILY STUFF***/
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    public void createDir(){
        // www.Gaut.am was here
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/"};

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    //Log.i(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    //Log.i(TAG, "Created directory " + path + " on sdcard");
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

    /*********WOLFRAM ALPHA SECTION*******/

    private InputStream OpenHttpConnection(String urlString) throws IOException
    {

        String result = "";
        InputStream in = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };

        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            in = urlConnection.getInputStream();
            return in;
            /*
            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                result += current;
                System.out.print(current);
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
        /*
            InputStream in = null;

        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if(!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP Connection");
        try
        {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK)
            {
                in = httpConn.getInputStream();
            }

        }
        catch(Exception ex)
        {
            Log.d("Wolf Post", ex.getLocalizedMessage());
            throw new IOException("Error Connecting");
        }
        return in;
        */
    }

    //method to send information and pull back xml format response
    private String wolframAnswer(int currencyVal, String firstSelect, String secondSelect)
    {
        InputStream in = null;

        String strWolfReturn = "";

        try
        {
            in = OpenHttpConnection("http://api.wolframalpha.com/v2/query?appid=9JJ2U4-273VULH74E&input=integrate%204x%20dx&format=image,plaintext ");
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;

            try
            {
                db = dbf.newDocumentBuilder();
                doc = db.parse(in);
            }
            catch(ParserConfigurationException e)
            {
                e.printStackTrace();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            doc.getDocumentElement().normalize();

            //retrieve the wolfram assumptions
            NodeList assumpElements = doc.getElementsByTagName("assumptions");

            //move through assumptions to correct one
            for (int i = 0; i < assumpElements.getLength(); i++)
            {
                Node itemNode = assumpElements.item(i);

                if(itemNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    //convert assumption to element
                    Element assumpElly = (Element) itemNode;

                    //get all the <query> elements under the <assumption> element
                    NodeList wolframReturnVal = (assumpElly).getElementsByTagName("query");

                    strWolfReturn = "";

                    //iterate through each <query> element
                    for(int j = 0; j < wolframReturnVal.getLength(); j++)
                    {
                        //convert query node into an element
                        Element wolframElementVal = (Element)wolframReturnVal.item(j);

                        //get all child nodes under query element
                        NodeList textNodes = ((Node)wolframElementVal).getChildNodes();

                        strWolfReturn += ((Node)textNodes.item(0)).getNodeValue() + ". \n";
                    }
                }

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
            return wolframAnswer(100, "ZAR", "DOL");
        }
        protected void onPostExecute(String result)
        {
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
        }
    }

    /*****WOLFRAM ALPHA SECTION****************/

    public void switchInt(){
        editText.setText("Changed Avticity");
        Intent myIntent = new Intent(this, MailSenderActivity.class);
        startActivity(myIntent);
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            //Log.i(TAG, "Starting Camera app");
            startCameraActivity();
        }
    }

    public class IntentClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            //Log.i(TAG, "Starting Camera app");
            //switchInt();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"christopher.pereyda@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "New OCR Result");
            i.putExtra(Intent.EXTRA_TEXT   , lastOCR);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Simple android photo capture:
    // http://labs.makemachine.net/2010/03/simple-android-photo-capture/

    protected void startCameraActivity() {
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.i(TAG, "resultCode: " + resultCode);
        if (resultCode == -1) {// && null != data
            onPhotoTaken();
        } else {
           // Log.i(TAG, "User cancelled");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(this.PHOTO_TAKEN, _taken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //Log.i(TAG, "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(this.PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }

    protected void onPhotoTaken() {
        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

        try {
            ExifInterface exif = new ExifInterface(_path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            //Log.i(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            //Log.i(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            //Log.i(TAG, "Couldn't correct orientation: " + e.toString());
        }

        // _image.setImageBitmap( bitmap );


        Pix myPix = ReadFile.readBitmap(bitmap);
        Pix sharp = Enhance.unsharpMasking(myPix);
        Pix edge = Edge.pixSobelEdgeFilter(myPix, Edge.L_ALL_EDGES);
        Pix noskew = Rotate.rotate(edge, Skew.findSkew(edge));

        OutputStream fOut = null;


        FileOutputStream out = null;
        Bitmap bit;

        File dir = new File(DATA_PATH + "/Proc");
        if(!dir.exists()){
            dir.mkdir();
        }
        try{
            File file = new File(dir, "asharp.png");
            out = new FileOutputStream(file);
            bit =  WriteFile.writeBitmap(sharp);
            out.flush();
            bit.compress(Bitmap.CompressFormat.PNG, 100,out);

            file = new File(dir, "bedge.png");
            out = new FileOutputStream(file);
            bit =  WriteFile.writeBitmap(edge);
            out.flush();
            bit.compress(Bitmap.CompressFormat.PNG, 100,out);

            file = new File(dir, "cnoskew.png");
            out = new FileOutputStream(file);
            bit =  WriteFile.writeBitmap(noskew);
            out.flush();
            bit.compress(Bitmap.CompressFormat.PNG, 100,out);

            out.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (out != null){
                    out.close();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        //Log.v(TAG, "Before baseApi");
        TessBaseAPI baseApi = new TessBaseAPI();

        //TessPdfRenderer doc = new TessPdfRenderer(baseApi, "MyPdf");

        //baseApi.beginDocument(doc, "MyDoc");

        baseApi.setDebug(true);

        baseApi.init(DATA_PATH, lang);

        baseApi.setImage(noskew);
        String recognizedText = baseApi.getUTF8Text();

        //baseApi.endDocument(doc);

        //doc.recycle();

        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        //Log.i(TAG, "OCRED TEXT: " + recognizedText);

        if ( lang.equalsIgnoreCase("eng") ) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        recognizedText = recognizedText.trim();

        if ( recognizedText.length() != 0 ) {
            //_field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
            //_field.setSelection(_field.getText().toString().length());
            //editText.setText(recognizedText);
        }
        else{
            //editText.setText("No words Recognized!");
        }
        lastOCR = recognizedText;
        // Cycle done.
    }

    /*
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
        if (id == R.id.action_new) {
            return true;
        }
        if (id == R.id.action_recent) {
            return true;
        }
        if (id == R.id.action_share) {
            return true;
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
    */
}

/*
import com.googlecode.tesseract.android.*;
import com.googlecode.leptonica.android.*;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends FragmentActivity {
    final String lang = "eng";


    TessBaseAPI myOcr;
    String myText = "";

    String path = "Noop";

    Uri outPutfileUri;
    static int TAKE_PIC = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        boolean initResult = false;


        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, );




         /*
        Try 2
         */
        /*
        TessBaseAPI _tessAPI = new TessBaseAPI();
        String LANG = "eng";
        String TESSDATA = "tessdata";
        File data = new File(getApplicationInfo().dataDir, TESSDATA);
        File traineddataFile = new File(data, LANG + ".traineddata");
        if (!traineddataFile.exists()) {
            try {
                data.mkdirs();
                copyAndClose(this.getAssets().open(TESSDATA + "/" + LANG + ".traineddata"), new FileOutputStream(traineddataFile));
            } catch (IOException e) {
                editText2.setText(e.getMessage());
            }
        }
        _tessAPI.init(getApplicationInfo().dataDir, LANG);


        // Test Code for working OCR change stuff later

        TessBaseAPI baseAPI = new TessBaseAPI();
        try {
            initResult = baseAPI.init(Environment.getExternalStorageDirectory().getParent(), lang);
        }
        catch( IllegalArgumentException e){
            editText.setText(e.getMessage());
        }

        if(initResult) {      //initResult
            InputStream is = null;
            try {
                is = getAssets().open("test2.jpg");
                final Drawable drw = Drawable.createFromStream(is, null);
                Bitmap bmp = ((BitmapDrawable) drw).getBitmap();

                baseAPI.setDebug(true);
                baseAPI.setImage(bmp);
                ImageView imageView = (ImageView)findViewById(R.id.imageView);
                imageView.setImageBitmap(bmp);

                String recognizedText = baseAPI.getUTF8Text();
                //Log.d("Rec Text", recognizedText);
                //TextView textView = (TextView) findViewById(R.id.textBox);
                editText.setText(recognizedText);
                //baseAPI.end();

            } catch (FileNotFoundException nfe) {
                Log.d("FNF", "File Not Found");
                nfe.printStackTrace();
            } catch (IOException ioe) {
                Log.d("Unable to open file", "Unable to open the file");
                ioe.printStackTrace();
            }
        } else {
            Log.d("OCR", "Unable to init Base API");
        }

    }

    public void copyAndClose(InputStream src, OutputStream dst) throws IOException {
        InputStream in = src;
        OutputStream out = dst;

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public void CameraClick(View v) {

        //Intent myIntent = new Intent(CameraActivity.this, MainActivity.class);

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = Environment.getExternalStorageDirectory().toString();
        File file = new File (path, "MyPhoto");
        outPutfileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
        intent.putExtra(android.provider.MediaStore.EXTRA_SIZE_LIMIT, "720000");
        startActivityForResult(intent, TAKE_PIC);

        editText2.setText("Photo saved to: " + outPutfileUri.getPath());

        // myIntent.putExtra(path, "hi");
    }

    public void alert(String text){
        new AlertDialog.Builder(this)
                .setTitle("Alert!")
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if (requestCode == TAKE_PIC && resultCode==RESULT_OK && null != data){
            Toast.makeText(this, outPutfileUri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void doStuff(View v){
        /*
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path = extras.getString("hi");
        }

        Bitmap myBit;
        File file = new File(path + "/MyPhoto");
        editText3.setText("Looking in: " + file.getPath());

        if (!file.exists())
        {
            editText.setText("ERROR: FILE NOT FOUND!");
        }
        else {
            File image = new File(path, "MyPhoto");
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            myOcr = new TessBaseAPI();
            myOcr.setImage(bitmap);



            StringBuilder myNums = new StringBuilder();
            /*
            myOcr = new TessBaseAPI();
            TessBaseAPI tessBase = new TessBaseAPI();
            TessPdfRenderer tessPDF = new TessPdfRenderer(tessBase ,path);
            myOcr.beginDocument(tessPDF);
            myOcr.setImage(file);


            myText = myOcr.getUTF8Text();
            editText.setText(myText);

            int[] array = myOcr.wordConfidences();
            for (int i = 0; i < array.length; i++) {
                myNums.append(array[i]);
                myNums.append(", ");
            }
            if (array.length == 0) {
                myNums.append("No Words Recognized!");
            }
            editText.setText(myNums);
            myOcr.end();
        }
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

*/