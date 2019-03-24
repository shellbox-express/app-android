package com.example.augusto.shellbox_express;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.augusto.shellbox_express.model.Balance;
import com.example.augusto.shellbox_express.service.APIClient;
import com.example.augusto.shellbox_express.service.APIInterface;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {
    private final int SPEECH_RECOGNITION_CODE = 1;
    private TextView txtOutput;
    private ImageButton btnMicrophone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtOutput = (TextView) findViewById(R.id.txt_output);
        btnMicrophone = (ImageButton) findViewById(R.id.btn_mic);
        btnMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });
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

                    Log.e("audio",  "" + text.toUpperCase());

//                    if(text.toUpperCase().equals("QUAL O MEU SALDO")){
//                        getSaldo();
//                    }

                    txtOutput.setText(text);

                }
                break;
            }
        }
    }

    private APIInterface apiService;
    private Call<Balance> callBalance;

//    protected void getSaldo() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle(R.string.balance);
//        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_balance, (ViewGroup) getWindow().getDecorView().getRootView(), false);
//
//        final TextView tv_balance = (TextView) viewInflated.findViewById(R.id.tv_balance);
//
//        apiService = APIClient.getService().create(APIInterface.class);
//        callBalance = apiService.getBalance();
//
//        callBalance.enqueue(new Callback<Balance>() {
//            @Override
//            public void onResponse(Call<Balance> call, Response<Balance> response) {
//                if (response.raw().code() == 200) {
//
//                    Balance balance = response.body();
//
//                    tv_balance.setText("" + balance.getBalance());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Balance> call, Throwable t) {
//                Log.e("BALANCE", t.toString());
//            }
//        });
//
//        builder.setView(viewInflated);
//
//        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        builder.show();
//    }
}
