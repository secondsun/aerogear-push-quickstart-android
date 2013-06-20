package org.jboss.aerogear.android.unifiedpush.quickstart;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.Registrar;

public class PushQuickstartActivity extends Activity implements MessageHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
        Toast.makeText(this, bundle.getString("alert"), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {
    }

    @Override
    public void onError() {
    }

}
