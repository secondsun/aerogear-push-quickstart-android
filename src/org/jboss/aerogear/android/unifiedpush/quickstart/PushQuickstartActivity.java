package org.jboss.aerogear.android.unifiedpush.quickstart;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.Registrar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PushQuickstartActivity extends Activity implements MessageHandler {

    private SimpleDateFormat dateFormat = new SimpleDateFormat ("MM/dd/yyyy HH:mm:ss Z");
    private TextView messageDisplay;
    private TextView timeDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        messageDisplay = (TextView) findViewById(R.id.message);
        timeDisplay = (TextView) findViewById(R.id.time);

        if (getIntent() != null && getIntent().hasExtra("alert")) {
            onMessage(this, getIntent().getExtras());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Registrar.unregisterBackgroundThreadHandler(NotifyingMessageHandler.instance);
        Registrar.registerMainThreadHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Registrar.unregisterMainThreadHandler(this);
        Registrar.registerBackgroundThreadHandler(NotifyingMessageHandler.instance);
    }

    @Override
    public void onMessage(Context context, Bundle bundle) {
        messageDisplay.setText(bundle.getString("alert"));

        StringBuilder time = new StringBuilder();
        time.append("\n");
        time.append("(");
        time.append(dateFormat.format(new Date()));
        time.append(")");

        timeDisplay.setText(time.toString());
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {
    }

    @Override
    public void onError() {
    }

}
