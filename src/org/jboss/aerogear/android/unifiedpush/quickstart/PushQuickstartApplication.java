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

import android.app.Application;
import android.util.Log;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.authentication.AuthenticationConfig;
import org.jboss.aerogear.android.authentication.AuthenticationModule;
import org.jboss.aerogear.android.authentication.impl.Authenticator;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.unifiedpush.PushConfig;
import org.jboss.aerogear.android.unifiedpush.Registrar;

import java.net.MalformedURLException;
import java.net.URL;

public class PushQuickstartApplication extends Application {

    private static final String TAG = PushQuickstartApplication.class.getSimpleName();

    private static final String BASE_BACKEND_URL = "";
    private static final String REGISTER_SERVER_URL = "";
    private static final String SENDER_ID = "";
    private static final String MOBILE_VARIANT_ID = "";
    private static final String ALIAS = "";

    private AuthenticationModule authBackEnd;
    private Registrar registrar;

    @Override
    public void onCreate() {
        super.onCreate();

        registerDeviceOnPushServer();
        configureBackendAuthentication();
    }

    private void registerDeviceOnPushServer() {

        try {

            final URL registerURL = new URL(REGISTER_SERVER_URL);
            final Registrar r = new Registrar(registerURL);
            PushConfig pushConfig = new PushConfig(SENDER_ID);
            pushConfig.setMobileVariantId(MOBILE_VARIANT_ID);
            pushConfig.setAlias(ALIAS);


            r.register(getApplicationContext(), pushConfig, new Callback<Void>() {
                @Override
                public void onSuccess(Void ignore) {
                    registrar = r;
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

    public void login(String username, String password, Callback<HeaderAndBody> callback) {
        authBackEnd.login(username, password, callback);
    }

    public void logout(Callback<Void> callback) {
        authBackEnd.logout(callback);
    }

}