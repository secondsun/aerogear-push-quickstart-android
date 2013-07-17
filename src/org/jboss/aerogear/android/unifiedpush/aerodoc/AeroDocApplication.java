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
package org.jboss.aerogear.android.unifiedpush.aerodoc;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.DataManager;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.authentication.AuthenticationConfig;
import org.jboss.aerogear.android.authentication.AuthenticationModule;
import org.jboss.aerogear.android.authentication.impl.Authenticator;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.impl.datamanager.SQLStore;
import org.jboss.aerogear.android.impl.datamanager.StoreConfig;
import org.jboss.aerogear.android.impl.datamanager.StoreTypes;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.unifiedpush.PushConfig;
import org.jboss.aerogear.android.unifiedpush.Registrar;
import org.jboss.aerogear.android.unifiedpush.aerodoc.model.Lead;
import org.jboss.aerogear.android.unifiedpush.aerodoc.model.SaleAgent;

import java.net.MalformedURLException;
import java.net.URL;

public class AeroDocApplication extends Application {

    private static final String TAG = AeroDocApplication.class.getSimpleName();

    private static final String BASE_BACKEND_URL = "";

    private static final String REGISTER_SERVER_URL = "";
    private static final String SENDER_ID = "";
    private static final String VARIANT_ID = "";
    private static final String SECRET = "";
    private static final String ALIAS = "";

    private AuthenticationModule authBackEnd;
    private Pipeline pipeline;
    private SQLStore<Lead> localStore;
    private SaleAgent saleAgent;

    public SaleAgent getSaleAgent() {
        return saleAgent;
    }

    public void setSaleAgente(SaleAgent saleAgent) {
        this.saleAgent = saleAgent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerDeviceOnPushServer();

        configureBackendAuthentication();
        createApplicationPipes();
        createLocalStorage();
    }

    private void registerDeviceOnPushServer() {

        try {

            final URL registerURL = new URL(REGISTER_SERVER_URL);
            final Registrar r = new Registrar(registerURL);
            PushConfig pushConfig = new PushConfig(SENDER_ID);
            pushConfig.setVariantID(VARIANT_ID);
            pushConfig.setSecret(SECRET);
            pushConfig.setAlias(ALIAS);

            r.register(getApplicationContext(), pushConfig, new Callback<Void>() {
                @Override
                public void onSuccess(Void ignore) {
                    Log.i(TAG, "Device registered");
                }

                @Override
                public void onFailure(Exception exception) {
                    Log.e(TAG, exception.getMessage(), exception);
                }
            });

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    private void configureBackendAuthentication() {
        Authenticator authenticator = new Authenticator(BASE_BACKEND_URL);
        AuthenticationConfig authenticationConfig = new AuthenticationConfig();
        authenticationConfig.setLoginEndpoint("/login");
        authenticationConfig.setLogoutEndpoint("/logout");
        authBackEnd = authenticator.auth("login", authenticationConfig);
    }

    private void createApplicationPipes() {

        try {

            final URL serverURL = new URL(BASE_BACKEND_URL);
            pipeline = new Pipeline(serverURL);

            PipeConfig leadPipeConfig = new PipeConfig(serverURL, Lead.class);
            leadPipeConfig.setEndpoint("leads");
            pipeline.pipe(Lead.class, leadPipeConfig);

            PipeConfig saleAgentPipeConfig = new PipeConfig(serverURL, SaleAgent.class);
            saleAgentPipeConfig.setName("agent");
            saleAgentPipeConfig.setEndpoint("saleagents");
            pipeline.pipe(SaleAgent.class, saleAgentPipeConfig);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    private void createLocalStorage() {

        DataManager dataManager = new DataManager();

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setContext(getApplicationContext());
        storeConfig.setType(StoreTypes.SQL);
        storeConfig.setKlass(Lead.class);

        localStore = (SQLStore) dataManager.store("leadStore", storeConfig);
        localStore.open(new Callback() {
            @Override
            public void onSuccess(Object data) {
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    public void login(String username, String password, Callback<HeaderAndBody> callback) {
        authBackEnd.login(username, password, callback);
    }

    public void logout(Callback<Void> callback) {
        authBackEnd.logout(callback);
    }

    public boolean isLoggedIn() {
        return authBackEnd.isLoggedIn();
    }

    public Pipe<Lead> getLeadPipe(Fragment fragment) {
        return this.pipeline.get("lead", fragment, getApplicationContext());
    }

    public Pipe<SaleAgent> getSaleAgentPipe(Fragment fragment) {
        return this.pipeline.get("agent", fragment, getApplicationContext());
    }

    public SQLStore<Lead> getLocalStore() {
        return localStore;
    }
}