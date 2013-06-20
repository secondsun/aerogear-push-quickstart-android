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
