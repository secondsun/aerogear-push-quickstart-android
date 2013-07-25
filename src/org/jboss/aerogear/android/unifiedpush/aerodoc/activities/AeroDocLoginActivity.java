package org.jboss.aerogear.android.unifiedpush.aerodoc.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.unifiedpush.aerodoc.AeroDocApplication;
import org.jboss.aerogear.android.unifiedpush.aerodoc.R;
import org.jboss.aerogear.android.unifiedpush.aerodoc.model.SaleAgent;

import java.nio.charset.Charset;

public class AeroDocLoginActivity extends Activity {

    private AeroDocApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        application = (AeroDocApplication) getApplication();

        final TextView username = (TextView) findViewById(R.id.username);
        final TextView password = (TextView) findViewById(R.id.password);
        final Button login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                login(user, pass);
            }
        });
    }

    public void login(final String user, final String pass) {
        final ProgressDialog dialog = application.showProgressDialog(this, getString(R.string.loging));
        application.login(user, pass, new Callback<HeaderAndBody>() {
            @Override
            public void onSuccess(HeaderAndBody data) {
                String response = new String(data.getBody(), Charset.forName("UTF-8"));
                SaleAgent saleAgent = new Gson().fromJson(response, SaleAgent.class);
                application.setSaleAgente(saleAgent);
                dialog.dismiss();
                startActivity(new Intent(AeroDocLoginActivity.this, AeroDocActivity.class));
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                application.displayErrorMessage(e, dialog);
            }
        });
    }

}
