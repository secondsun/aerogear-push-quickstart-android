package org.jboss.aerogear.android.unifiedpush.quickstart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.http.HeaderAndBody;

public class PushQuickstartLoginActivity extends Activity {

    private PushQuickstartApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        application = (PushQuickstartApplication) getApplication();

        final TextView username = (TextView) findViewById(R.id.username);
        final TextView password = (TextView) findViewById(R.id.password);
        final Button login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                final ProgressDialog dialog = ProgressDialog.show(PushQuickstartLoginActivity.this,
                        "Wait...", "Loging", true, true);

                application.login(user, pass, new Callback<HeaderAndBody>() {
                    @Override
                    public void onSuccess(HeaderAndBody data) {
                        startActivity(new Intent(PushQuickstartLoginActivity.this, PushQuickstartLeadsActivity.class));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        displayErrorMessage(e, dialog);
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void displayErrorMessage(Exception e, ProgressDialog dialog) {
        Log.e("Login", "An error occurrence", e);
        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

}
