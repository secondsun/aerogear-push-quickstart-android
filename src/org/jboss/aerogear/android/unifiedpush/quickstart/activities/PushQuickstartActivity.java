/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.android.unifiedpush.quickstart.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.Registrar;
import org.jboss.aerogear.android.unifiedpush.quickstart.PushQuickstartApplication;
import org.jboss.aerogear.android.unifiedpush.quickstart.R;
import org.jboss.aerogear.android.unifiedpush.quickstart.fragments.PushQuickstartLeadsFragments;
import org.jboss.aerogear.android.unifiedpush.quickstart.fragments.PushQuickstartLoginFragment;
import org.jboss.aerogear.android.unifiedpush.quickstart.handler.NotifyingMessageHandler;
import org.jboss.aerogear.android.unifiedpush.quickstart.model.SaleAgent;

import java.nio.charset.Charset;

public class PushQuickstartActivity extends SherlockFragmentActivity implements MessageHandler {

    private enum Display {LOGIN, LEADS}

    private PushQuickstartApplication application;
    private Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        application = (PushQuickstartApplication) getApplication();

        if(application.isLoggedIn()) {
            displayLeadsScreen();
        } else {
            displayLoginScreen();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.leads, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                updateLeads();
                break;
            case R.id.logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!Display.LEADS.equals(display)) {
            menu.removeGroup(R.id.menuLead);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void onMessage(Context context, Bundle bundle) {
        updateLeads();
        Toast.makeText(this, bundle.getString("alert"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {
    }

    @Override
    public void onError() {
    }

    private void displayLoginScreen() {
        displayFragment(Display.LOGIN, new PushQuickstartLoginFragment());
    }

    private void displayLeadsScreen() {
        displayFragment(Display.LEADS, new PushQuickstartLeadsFragments());
    }

    private void displayFragment(Display display, Fragment fragment) {
        this.display = display;
        this.invalidateOptionsMenu();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();
    }

    public void login(String user, String pass) {
        final ProgressDialog dialog = ProgressDialog.show(this, "Wait...", "Loging", true, true);

        application.login(user, pass, new Callback<HeaderAndBody>() {
            @Override
            public void onSuccess(HeaderAndBody data) {
                String response = new String(data.getBody(), Charset.forName("UTF-8"));
                SaleAgent saleAgent = new Gson().fromJson(response, SaleAgent.class);
                application.setSaleAgente(saleAgent);
                dialog.dismiss();
                displayLeadsScreen();
            }

            @Override
            public void onFailure(Exception e) {
                displayErrorMessage(e, dialog);
            }
        });
    }

    public void logout() {
        final ProgressDialog dialog = ProgressDialog.show(this, "Wait...", "Logout", true, true);

        application.logout(new Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                displayLoginScreen();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                displayErrorMessage(e, dialog);
            }
        });

    }

    private void updateLeads() {
        PushQuickstartLeadsFragments leadsFragments = (PushQuickstartLeadsFragments)
                getSupportFragmentManager().findFragmentById(R.id.frame);
        leadsFragments.retrieveLeads();
    }

    public void displayErrorMessage(Exception e, ProgressDialog dialog) {
        Log.e("Login", "An error occurrence", e);
        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

}
