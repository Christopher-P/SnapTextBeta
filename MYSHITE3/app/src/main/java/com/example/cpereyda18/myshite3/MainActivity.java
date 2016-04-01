package com.example.cpereyda18.myshite3;

import com.googlecode.tesseract.android.*;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends FragmentActivity {
    EditText editText;
    TessBaseAPI myOcr;
    static int TAKE_PIC =1;
    Uri outPutfileUri;
    String myText = "";
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.textBox);
    }
    public void CameraClick(View v) {

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = Environment.getExternalStorageDirectory().toString();
        File file = new File (path, "MyPhoto");
        outPutfileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
        startActivityForResult(intent, TAKE_PIC);

        editText.setText("RUNNING");

        editText.setText("THIS FINISHED YAY");
    }

    public void doStuff(View v){
        File file = new File(path + "MyPhoto.jpg");
        if (!file.exists())
        {
            editText.setText("ERROR: FILE NOT FOUND!");
        }
        else {
            StringBuilder myNums = new StringBuilder();
            myOcr = new TessBaseAPI();
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
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if (requestCode == TAKE_PIC && resultCode==RESULT_OK){
            Toast.makeText(this, outPutfileUri.toString(), Toast.LENGTH_LONG).show();
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

