package org.tensorflow.dot;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class Network {
    private AssetManager assetManager;

    public Network(AssetManager assetManager, String urlString) {
        this.assetManager = assetManager;
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        InputStream caInput = null;
        try {
            caInput = new BufferedInputStream(this.assetManager.open("penndot.crt"));
            Log.d("PENNDOT", caInput.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert cf != null;
            Certificate ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = null;
            try {
                keyStore = KeyStore.getInstance(keyStoreType);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            try {
                keyStore.load(null, null);
            } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                keyStore.setCertificateEntry("ca", ca);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

// Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = null;
            try {
                tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                tmf.init(keyStore);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

// Create an SSLContext that uses our TrustManager
            SSLContext context = null;
            try {
                context = SSLContext.getInstance("TLS");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                context.init(null, tmf.getTrustManagers(), null);
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpsURLConnection urlConnection =
                    null;
            try {
                urlConnection = (HttpsURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            try {
                InputStream in = urlConnection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (CertificateException e) {
            e.printStackTrace();
        } finally {
            try {
                assert caInput != null;
                caInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
