package net.azurewebsites.snaptextbeta.myshite4;

//By http://stackoverflow.com/users/28557/vinayak-b
// Edited by Chrisrotpher Pereyda

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MailSenderActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        final Button send = (Button) this.findViewById(R.id.button2);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailSend(v);
            }
        });
    }

    public void mailSend(View v) {
        // TODO Auto-generated method stub

        try {
            GMailSender sender = new GMailSender("Email", "Password");
            sender.sendMail("This is Subject",
                    "This is Body",
                    "christopher.pereyda@gmail.com",
                    "emacdonald16@my.whitworth.edu");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }

    }
}