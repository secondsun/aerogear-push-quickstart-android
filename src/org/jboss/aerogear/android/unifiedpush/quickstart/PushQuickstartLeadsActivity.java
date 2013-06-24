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
package org.jboss.aerogear.android.unifiedpush.quickstart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.Registrar;

import java.util.List;

public class PushQuickstartLeadsActivity extends Activity implements MessageHandler {

    private PushQuickstartApplication application;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leads);

        application = (PushQuickstartApplication) getApplication();

        listView = (ListView) findViewById(R.id.leads);

        if (getIntent() != null && getIntent().hasExtra("alert")) {
            onMessage(this, getIntent().getExtras());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Registrar.unregisterBackgroundThreadHandler(NotifyingMessageHandler.instance);
        Registrar.registerMainThreadHandler(this);

        retrieveLeads();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Registrar.unregisterMainThreadHandler(this);
        Registrar.registerBackgroundThreadHandler(NotifyingMessageHandler.instance);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.leads, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        application.logout(new Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        return super.onOptionsItemSelected(item);
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

    public void retrieveLeads() {
        Pipe<Lead> pipe = application.getLeadPipe(this);
        pipe.read(new Callback<List<Lead>>() {
            @Override
            public void onSuccess(List<Lead> data) {
                ArrayAdapter<Lead> adapter = new ArrayAdapter<Lead>(PushQuickstartLeadsActivity.this,
                        android.R.layout.simple_list_item_1, data);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

}
