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
package org.jboss.aerogear.android.unifiedpush.aerodoc.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.Registrar;
import org.jboss.aerogear.android.unifiedpush.aerodoc.AeroDocApplication;
import org.jboss.aerogear.android.unifiedpush.aerodoc.R;
import org.jboss.aerogear.android.unifiedpush.aerodoc.fragments.AeroDocLeadsAcceptedFragments;
import org.jboss.aerogear.android.unifiedpush.aerodoc.fragments.AeroDocLeadsAvalableFragments;
import org.jboss.aerogear.android.unifiedpush.aerodoc.handler.NotifyingMessageHandler;
import org.jboss.aerogear.android.unifiedpush.aerodoc.model.MessageType;

public class AeroDocActivity extends SherlockFragmentActivity implements MessageHandler {

    private enum Display {
        AVALABLE_LEADS, LEADS_ACCEPTED
    }

    private AeroDocApplication application;
    private Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        application = (AeroDocApplication) getApplication();

        if (application.isLoggedIn()) {
            displayAvalableLeadsScreen();
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
        case R.id.leads_accepted:
            displayLeadsAcceptedScreen();
            break;
        case R.id.avalable_leads:
            displayAvalableLeadsScreen();
            break;
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
        menu.setGroupVisible(R.id.menuAvalableLead, Display.AVALABLE_LEADS.equals(display));
        menu.setGroupVisible(R.id.menuLeadsAccepted, Display.LEADS_ACCEPTED.equals(display));
        return super.onPrepareOptionsMenu(menu);
    }

    public void onMessage(Context context, Bundle bundle) {
        String messageType = bundle.getString("messageType");
        if (MessageType.PUSHED.getType().equals(messageType)) {
            updateLeads();
            Toast.makeText(this, bundle.getString("alert"), Toast.LENGTH_SHORT).show();
        } else if (MessageType.ACCPET.getType().equals(messageType)) {
            updateLeads();
        }
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {
    }

    @Override
    public void onError() {
    }

    private void displayLoginScreen() {
        startActivity(new Intent(this, AeroDocLoginActivity.class));
        finish();
    }

    private void displayAvalableLeadsScreen() {
        displayFragment(Display.AVALABLE_LEADS, new AeroDocLeadsAvalableFragments());
    }

    private void displayLeadsAcceptedScreen() {
        displayFragment(Display.LEADS_ACCEPTED, new AeroDocLeadsAcceptedFragments());
    }

    private void displayFragment(Display display, Fragment fragment) {
        this.display = display;
        this.invalidateOptionsMenu();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();
    }

    public void logout() {
        final ProgressDialog dialog = application.showProgressDialog(this, getString(R.string.logout));

        application.logout(new Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                displayLoginScreen();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                application.displayErrorMessage(e, dialog);
            }
        });
    }

    private void updateLeads() {
        AeroDocLeadsAvalableFragments leadsFragments = (AeroDocLeadsAvalableFragments)
                getSupportFragmentManager().findFragmentById(R.id.frame);
        leadsFragments.retrieveLeads();
    }

}
