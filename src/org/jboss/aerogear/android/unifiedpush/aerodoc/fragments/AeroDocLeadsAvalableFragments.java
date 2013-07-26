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
package org.jboss.aerogear.android.unifiedpush.aerodoc.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.unifiedpush.aerodoc.AeroDocApplication;
import org.jboss.aerogear.android.unifiedpush.aerodoc.R;
import org.jboss.aerogear.android.unifiedpush.aerodoc.activities.AeroDocActivity;
import org.jboss.aerogear.android.unifiedpush.aerodoc.model.Lead;
import org.jboss.aerogear.android.unifiedpush.aerodoc.model.SaleAgent;

import java.util.List;

import static android.R.layout.*;

public class AeroDocLeadsAvalableFragments extends SherlockFragment {

    private AeroDocApplication application;
    private AeroDocActivity activity;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        application = (AeroDocApplication) getActivity().getApplication();
        activity = (AeroDocActivity) getActivity();

        final View view = inflater.inflate(R.layout.available_leads, null);

        listView = (ListView) view.findViewById(R.id.leads);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Lead lead = (Lead) adapterView.getItemAtPosition(position);
                displayLead(lead);
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.status);

        ArrayAdapter<String> allStatus = (ArrayAdapter) spinner.getAdapter();
        int spinnerPosition = allStatus.getPosition(application.getSaleAgent().getStatus());
        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String status = (String) adapterView.getItemAtPosition(position);
                if (!application.getSaleAgent().getStatus().equals(status)) {
                    updateStatus(status);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        retrieveLeads();

        return view;
    }

    public void retrieveLeads() {
        final ProgressDialog dialog = activity.showProgressDialog(getString(R.string.retreving_leads));

        Pipe<Lead> pipe = application.getLeadPipe(this);
        pipe.read(new Callback<List<Lead>>() {
            @Override
            public void onSuccess(List<Lead> data) {
                ArrayAdapter<Lead> adapter = new ArrayAdapter<Lead>(activity, simple_list_item_1, data);
                listView.setAdapter(adapter);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                activity.displayErrorMessage(e);
                dialog.dismiss();
            }
        });
    }

    private void displayLead(final Lead lead) {
        new AlertDialog.Builder(activity)
                .setMessage(lead.getName())
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        acceptLead(lead);
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

    private void acceptLead(final Lead lead) {
        final ProgressDialog dialog = activity.showProgressDialog(getString(R.string.updating_lead));

        lead.setSaleAgent(application.getSaleAgent().getId());
        Pipe<Lead> leadPipe = application.getLeadPipe(this);
        leadPipe.save(lead, new Callback<Lead>() {
            @Override
            public void onSuccess(Lead data) {
                application.getLocalStore().save(lead);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                activity.displayErrorMessage(e);
                dialog.dismiss();
            }
        });
    }

    private void updateStatus(String status) {
        final ProgressDialog dialog = activity.showProgressDialog(getString(R.string.updating_status));

        SaleAgent saleAgent = application.getSaleAgent();
        saleAgent.setStatus(status);

        Pipe<SaleAgent> pipe = application.getSaleAgentPipe(this);
        pipe.save(saleAgent, new Callback<SaleAgent>() {
            @Override
            public void onSuccess(SaleAgent data) {
                dialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                activity.displayErrorMessage(e);
                dialog.dismiss();
            }
        });
    }

}
