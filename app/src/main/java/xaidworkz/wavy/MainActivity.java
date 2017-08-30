package xaidworkz.wavy;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Toast;

import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;
import com.cleveroad.audiovisualization.SpeechRecognizerDbmHandler;
import com.mapzen.speakerbox.Speakerbox;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_RECORD_AUDIO = 23;
    private GLAudioVisualizationView visualizationView;

    private FloatingActionButton fab;
    private BottomSheetBehavior mBottomSheetBehavior1;

    private SpeechRecognizerDbmHandler handler;
    private boolean recognizing;
    private Speakerbox sbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handlePermission(Manifest.permission.RECORD_AUDIO, REQUEST_RECORD_AUDIO);
    }

    private void handlePermission(String permission, int requestCode) {
        String permission2 = Manifest.permission.MODIFY_AUDIO_SETTINGS;
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission, permission2}, REQUEST_RECORD_AUDIO);
        } else {
            initViews();
        }
    }

    private void initViews() {

        fab = (FloatingActionButton) findViewById(R.id.fab);
        View bottomSheet = findViewById(R.id.bottom_sheet1);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recognizing) {
                    handler.stopListening();
                } else {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                    handler.startListening(intent);
                }
                fab.setEnabled(false);
            }
        });

        sbox = new Speakerbox(getApplication());

        setupVisualization();

        animateFab();


    }

    private void setupVisualization() {
        handler = DbmHandler.Factory.newSpeechRecognizerHandler(this);
        handler.innerRecognitionListener(new SimpleRecognizerListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {
                super.onReadyForSpeech(params);
                onStartRecognizing();
            }

            @Override
            public void onResults(Bundle results) {
                super.onResults(results);
                onStopRecognizing();
            }

            @Override
            public void onError(int error) {
                super.onError(error);
                onStopRecognizing();

            }

        });
        visualizationView = (GLAudioVisualizationView) findViewById(R.id.visualizer_view);
        visualizationView.linkTo(handler);
        visualizationView.linkTo(DbmHandler.Factory.newVisualizerHandler(this, 0));
    }

    private void animateFab() {
    /*animation*/
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int halfScreen = wm.getDefaultDisplay().getHeight() / 2;
        fab.setScaleX(0);
        fab.setScaleY(0);
        fab.animate()
                .setDuration(1000)
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setInterpolator(new AnticipateOvershootInterpolator())
                .setStartDelay(200)
                .translationYBy(-halfScreen + 56 * 2)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        sbox.play("I am your personal Assistant", null, new Runnable() {
                            @Override
                            public void run() {
                                sbox.play("Ask me Anything", null, new Runnable() {
                                    @Override
                                    public void run() {
                                        sbox.play("go to setting for options", null, new Runnable() {
                                            @Override
                                            public void run() {

                                            }
                                        }, null);
                                    }
                                }, null);
                            }
                        }, null);
                    }
                });
        /*animation*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, "hey", Toast.LENGTH_SHORT).show();
    }

    private void onStopRecognizing() {
        recognizing = false;
        Toast.makeText(this, "stopped listening", Toast.LENGTH_SHORT).show();
        fab.setEnabled(true);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    private void onStartRecognizing() {
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Toast.makeText(this, "started listening", Toast.LENGTH_SHORT).show();
        fab.setEnabled(true);
        recognizing = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        visualizationView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        visualizationView.onResume();
    }

    @Override
    public void onDestroy() {
        visualizationView.release();
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initViews();
            } else {
                handlePermission(Manifest.permission.RECORD_AUDIO, REQUEST_RECORD_AUDIO);
            }
        }
    }

    private static class SimpleRecognizerListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int i) {

        }

        @Override
        public void onResults(Bundle bundle) {

        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    }
}
