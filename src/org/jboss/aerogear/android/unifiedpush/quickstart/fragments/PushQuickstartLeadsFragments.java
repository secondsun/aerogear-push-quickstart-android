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
package org.jboss.aerogear.android.unifiedpush.quickstart.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.unifiedpush.quickstart.PushQuickstartApplication;
import org.jboss.aerogear.android.unifiedpush.quickstart.R;
import org.jboss.aerogear.android.unifiedpush.quickstart.activities.PushQuickstartActivity;
import org.jboss.aerogear.android.unifiedpush.quickstart.model.Lead;

import java.util.List;

public class PushQuickstartLeadsFragments extends SherlockFragment {

    private PushQuickstartApplication application;
    private PushQuickstartActivity activity;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        application = (PushQuickstartApplication) getActivity().getApplication();
        activity = (PushQuickstartActivity) getActivity();

        final View view = inflater.inflate(R.layout.leads, null);

        listView = (ListView) view.findViewById(R.id.leads);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Lead lead = (Lead) adapterView.getItemAtPosition(position);
                displayLead(lead);
            }
        });

        retrieveLeads();

        return view;
    }

    public void retrieveLeads() {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Wait...", "Retrieving leads", true, true);

        Pipe<Lead> pipe = application.getLeadPipe(this);
        pipe.read(new Callback<List<Lead>>() {
            @Override
            public void onSuccess(List<Lead> data) {
                ArrayAdapter<Lead> adapter = new ArrayAdapter<Lead>(getActivity(),
                        android.R.layout.simple_list_item_1, data);
                listView.setAdapter(adapter);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                ((PushQuickstartActivity) getActivity()).displayErrorMessage(e, dialog);
            }
        });
    }

    private void displayLead(final Lead lead) {
        new AlertDialog.Builder(getActivity())
            .setMessage(lead.getName())
            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    lead.setSaleAgent(application.getSaleAgent().getId());
                    Pipe<Lead> leadPipe = application.getLeadPipe(PushQuickstartLeadsFragments.this);
                    leadPipe.save(lead, new Callback<Lead>() {
                        @Override
                        public void onSuccess(Lead data) {
                            retrieveLeads();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // Notify
                        }
                    });
                }
            })
            .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            })
        .create()
        .show();
    }

}
