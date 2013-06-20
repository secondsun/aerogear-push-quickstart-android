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
import org.jboss.aerogear.android.authentication.impl.AuthTypes;
import org.jboss.aerogear.android.authentication.impl.Authenticator;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.unifiedpush.PushConfig;
import org.jboss.aerogear.android.unifiedpush.Registrar;

import java.net.MalformedURLException;
import java.net.URL;

public class PushQuickstartApplication extends Application {

    private static final String TAG = PushQuickstartApplication.class.getSimpleName();

    private Registrar registrar;

    @Override
    public void onCreate() {
        super.onCreate();

        try {

            final URL registerURL = new URL("http://{SERVER}/ag-push/rest/registry/device");

            final Registrar r = new Registrar(registerURL);
            PushConfig config = new PushConfig("SENDER-ID");
            config.setMobileVariantId("MOBILE-VARIANT-ID");
            config.setAlias("BEAUTIFUL-ALIAS");

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

}
