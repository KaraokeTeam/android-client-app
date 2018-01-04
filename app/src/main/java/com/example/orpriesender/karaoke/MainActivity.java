package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;

public class MainActivity extends Activity {

    private Button record;
    private Button stop;
    private MediaRecorder recorder;
    private String outputFile;
    private TextView message;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onResume() {
        super.onResume();
        this.onCreate(null);
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        record = (Button) findViewById(R.id.record);
        stop = (Button) findViewById(R.id.stop);
        message = (TextView) findViewById(R.id.message);

        stop.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.3gp";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(outputFile);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    recorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                recorder.start();
                record.setEnabled(false);
                stop.setEnabled(true);
                message.setText("Started Recording");
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.release();
                recorder = null;
                stop.setEnabled(false);
                record.setEnabled(true);

                final ProgressBar pb = findViewById(R.id.main_progress_bar);
                pb.setVisibility(View.VISIBLE);
                WebServiceUtil util = WebServiceUtil.getInstance("http://10.160.19.157:5000/",getBaseContext());
                util.getGrade(outputFile, new onGradeResponseListener() {
                    @Override
                    public void onGradeResponse(Integer grade) {
                        Log.d("TAG","got response :" + grade);
                        Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                        intent.putExtra("outputFile",outputFile);
                        intent.putExtra("grade",grade);
                        pb.setVisibility(View.GONE);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailureRespnonse(String code) {
                        pb.setVisibility(View.GONE);
                    }
                });

            }
        });

    }




}
