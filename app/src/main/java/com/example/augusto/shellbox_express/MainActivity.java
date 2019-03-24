package com.example.augusto.shellbox_express;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.augusto.shellbox_express.model.Payload;
import com.example.augusto.shellbox_express.service.APIClient;
import com.example.augusto.shellbox_express.service.APIInterface;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity  implements TextToSpeech.OnInitListener {
    private final int SPEECH_RECOGNITION_CODE = 1;
    private ImageButton btnMicrophone;
    private TextToSpeech t1;
    private GifImageView gifImageView;
    private ImageView imageViewPos;
    private ImageView preViewPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gifImageView = (GifImageView) findViewById(R.id.button_gif);
        imageViewPos = (ImageView) findViewById(R.id.button_gif_pos);
        preViewPos = (ImageView) findViewById(R.id.btn_mic);


        preViewPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gifImageView.setImageResource(R.drawable.fluxo1);

                gifImageView.animate().start();
                preViewPos.setVisibility(View.INVISIBLE);
                gifImageView.setVisibility(View.VISIBLE);


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startSpeechToText();
                        imageViewPos.setVisibility(View.VISIBLE);
                        gifImageView.setVisibility(View.INVISIBLE);
                    }
                }, 4000);
            }
        });
    }

    private TextToSpeech myTTS;
    private String textToSpeak;

    public void speak(String text) {
        gifImageView.setVisibility(View.INVISIBLE);
        textToSpeak = text;

        if (myTTS == null) {
            try {
                myTTS = new TextToSpeech(this, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sayText(textToSpeak);

        Toast.makeText(getApplicationContext(), text,Toast.LENGTH_SHORT).show();
    }

    private void sayText(String textToSpeak) {
        myTTS.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH,     null);

        preViewPos.setVisibility(View.VISIBLE);
        imageViewPos.setVisibility(View.INVISIBLE);
        gifImageView.setVisibility(View.VISIBLE);
    }


    public void onInit(int initStatus) {
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.getDefault());
                }
            }
        });
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    protected void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                R.string.action);
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), R.string.sorry,
                    Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);

                    Log.e("audio0000",  "" + text.toUpperCase());

                    if(payload == null)
                        payload = new Payload();

                    payload.setText(text);


                    interact();
                }
                break;
            }
        }
    }

    private APIInterface apiService;
    private Call<Payload> callBalance;

    private Payload payload;

    protected void interact() {
        //Payload payload

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.balance);


        apiService = APIClient.getService().create(APIInterface.class);
        callBalance = apiService.postVoice(payload);

        Log.e("INIT REQUEST", "" + payload.getText());

        try {
            getSSLSocketFactory();
        }catch (Exception e){
            Log.e("ERRO", "" + e);
        }
        Log.i("Request in API", "" + callBalance.request().url().toString());

        callBalance.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(Call<Payload> call, Response<Payload> response) {
                if (response.raw().code() == 200) {

                    Payload payloadResponse = response.body();

                    speak(payloadResponse.getOutput());

                    payload = payloadResponse;

                    Log.e("RESULT REQUEST", payload.getOutput());


                    if(payload.getLat() != null && payload.getLng() != null) {
                        openWaze(payload.getLat(), payload.getLng());
                    }

                    // openWaze("-23.5240457", "-46.5197863");

                }

                Log.e("RESULT REQUEST", "" + response.body());
            }

            @Override
            public void onFailure(Call<Payload> call, Throwable t) {
                Log.e("BALANCE", t.toString());
            }
        });
    }

    public SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException,
            NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = getResources().openRawResource(R.raw.cert); // your certificate file
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();
        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);
        return sslContext.getSocketFactory();
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkClientTrusted(certs, authType);
                        } catch (CertificateException ignored) {
                        }
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkServerTrusted(certs, authType);
                        } catch (CertificateException ignored) {
                        }
                    }
                }
        };
    }

    private void openWaze(String lat, String lng) {
        try {
            // Launch Waze
            String mapRequest = "https://waze.com/ul?q=" + lat + "," + lng + "&navigate=yes&zoom=17";
            Uri gmmIntentUri = Uri.parse(mapRequest);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.waze");
            startActivity(mapIntent);

        } catch (ActivityNotFoundException e) {
            // If Waze is not installed, open it in Google Play
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
            startActivity(intent);
        }
    }
}
