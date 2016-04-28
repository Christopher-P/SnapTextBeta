package net.azurewebsites.snaptextbeta.myshite4;

//By http://stackoverflow.com/users/28557/vinayak-b

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MailSenderActivity extends Activity {

    EditText editText2;
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
            GMailSender sender = new GMailSender("christopher.pereyda@gmail.com", "f=gm18m2/d62");
            sender.sendMail("This is Subject",
                    "This is Body",
                    "christopher.pereyda@gmail.com",
                    "emacdonald16@my.whitworth.edu");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }

    }
}