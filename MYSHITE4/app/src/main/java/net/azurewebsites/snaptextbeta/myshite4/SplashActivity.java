package net.azurewebsites.snaptextbeta.myshite4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by cpereyda18 on 3/30/2016.
 */
public class SplashActivity extends AppCompatActivity {

    //Soley used to create a splash screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}