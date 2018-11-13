package androidassistanthelper.jdubss.androidassistanthelper.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Fulfillment;
import ai.api.model.Result;
import androidassistanthelper.jdubss.androidassistanthelper.Config.Dagger.InitializeDagger;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal.AssistantUser;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.SpeechTranslator.SpeechTranslatorDTO;
import androidassistanthelper.jdubss.androidassistanthelper.R;
import androidassistanthelper.jdubss.androidassistanthelper.Service.AssistantUsersService.IAssistantUsersBusiness;
import androidassistanthelper.jdubss.androidassistanthelper.Service.MapsService.IMapsService;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.IExecutionTranslationCallback;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.IExecutionTranslationService;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.ISpeechTranslator;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.internal.IntentsEnum;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.internal.SpeechTranslatorImpl;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.API_KEY;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.Constants;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.ConstantSpeech;

public class MainActivity extends Activity implements AIListener,IExecutionTranslationCallback {

    public static final String ASSISTANT_SHARED_PREFS = "AssisstantSharedPrefs";
    private final int LOGGED_INTO_SPOTIFY_RESULT_CODE = 1;
    private final int LOGGED_INTO_GOOGLE_REQUEST_CODE = 2;

    private IAssistantUsersBusiness mUsersBusiness;
    private IExecutionTranslationService mExecutionService;
    private IMapsService mapsService;
    private ISpeechTranslator mSpeechTranslator;
    private String LOG_TAG = "MainActivity";
    private AIService aiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((InitializeDagger) getApplication()).getmAssistantComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Boolean started = Boolean.FALSE;

        if (savedInstanceState != null && savedInstanceState.getBoolean("started"))
            started = Boolean.TRUE;

        SharedPreferences sp = getSharedPreferences(ASSISTANT_SHARED_PREFS, Context.MODE_PRIVATE);
        Boolean loggedInToGoogle = sp.getBoolean(Constants.LOGGED_INTO_GOOGLE, Boolean.FALSE);
        Boolean loggedIntoSpotify = sp.getBoolean(Constants.LOGGED_INTO_SPOTIFY, Boolean.FALSE);

        //Gooogle account is required for log in =)
        if (!loggedInToGoogle || !loggedIntoSpotify) {
            Intent intent = new Intent(this, LoginActivity.class);
            if (!loggedInToGoogle)
                intent.putExtra(Constants.LOGGED_INTO_GOOGLE, Boolean.FALSE);

            if (!loggedIntoSpotify)
                intent.putExtra(Constants.LOGGED_INTO_SPOTIFY, Boolean.FALSE);

            startActivityForResult(intent, LOGGED_INTO_GOOGLE_REQUEST_CODE);
        } else {
            refreshSpotify(started);
            validateMicAvailableAndPermissions();
            mSpeechTranslator = new SpeechTranslatorImpl(this);
            getAIService();

            if (savedInstanceState == null) {
                TextView speechToTextView = findViewById(R.id.speechToTextView);
                mSpeechTranslator.convertTextToSpeech(new SpeechTranslatorDTO(ConstantSpeech.WELCOME, aiService, Boolean.TRUE, speechToTextView));

            }

            setEditManuallyOnFocusListener();

            savedInstanceState = new Bundle();
            savedInstanceState.putBoolean("started", Boolean.TRUE);
        }
    }

    /**
     * Start ai.api Listening Service
     * @param view
     */
    public void startListening(View view) {
        if (mSpeechTranslator != null)
            mSpeechTranslator.pause();

        mSpeechTranslator.resume(this);

        if (aiService != null)
            aiService.startListening();
    }

    /**
     * Stop ai.api Listening Service
     * @param view
     */
    public void stopListening(View view) {
        if (mSpeechTranslator != null)
            mSpeechTranslator.pause();

        if (aiService != null)
            aiService.stopListening();
    }

    /**
     * Every functionality that may be performed by voice
     * will Also be available with text input for Accessibility
     * @param view
     */
    public void manuallyEnter(View view) {
        String query = ((EditText) findViewById(R.id.manuallyEnter)).getText().toString();

        if(query != null)
            new EnterManually(aiService).execute(query);
    }

    /**
     * Called on Result of a api.ai Dialog Flow Query. Will
     * Gather necessary parameters, then call the Execution Translation
     * Class to execute the necessary Business Logic
     * @param response
     */
    @Override
    public void onResult(final AIResponse response) {
        Log.i(LOG_TAG, "onResults");
        TextView speechToTextView = findViewById(R.id.speechToTextView);
        Result result = response.getResult();
        Fulfillment fulfillment = result.getFulfillment();
        String intent = result.getAction();

        //if user said hello we still need to get action from them
        if (IntentsEnum.resolve(intent) == IntentsEnum.DEFAULT_WELCOME)
            mSpeechTranslator.convertTextToSpeech(new SpeechTranslatorDTO(fulfillment.getSpeech(), aiService, Boolean.TRUE, speechToTextView));

        //result is incomplete
        if (result.isActionIncomplete())
            getAllParametersForQuery(result);

        else{

            Boolean success = mExecutionService.translateAndExecute(result, this);

            if (!success)
                mSpeechTranslator.convertTextToSpeech(new SpeechTranslatorDTO(ConstantSpeech.ERROR, aiService, Boolean.FALSE, speechToTextView));


            /* Dispay parameters */

            // Get parameters
            String parameterString = "";
            if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                    parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
                }
            }

            TextView resultTextView = (TextView) findViewById(R.id.resultTextView);

            // Show results in TextView.
            resultTextView.setText("Query:" + result.getResolvedQuery() +
                    "\nAction: " + result.getAction() +
                    "\nParameters: " + parameterString);
        }
    }

    /**
     * Called from The Async Web Service Calls to Speek the Result
     * of the Web Service Call
     *
     * Must be run on the Main Thread since it updates the UI
     * @param speech
     */
    @Override
    public void speekResult(String speech) {
        runOnUiThread(new UpdateSpeechTextOnMainThread(speech));
    }

    /**
     * Untill all necessary parameters are gathered for an Action we will
     * continue gathering the parameters
     * @param result
     */
    private void getAllParametersForQuery(Result result) {
        Fulfillment fulfillment = result.getFulfillment();
        TextView speechToTextView = findViewById(R.id.speechToTextView);
        mSpeechTranslator.convertTextToSpeech(new SpeechTranslatorDTO(fulfillment.getSpeech(), aiService, Boolean.TRUE, speechToTextView));
    }


    /**
     * This will refresh the Spotify Token
     * This should really be replaced with a
     * web wervice call. Spotify Android API
     * does not provide a Refresh Token so this
     * is only solution if using the Android API
     * @param started
     */
    public void refreshSpotify(Boolean started) {

        if (!started) {

            Boolean extrasNull = getIntent().getExtras() == null;
            Boolean loggedInSpotify = !extrasNull && getIntent().getExtras().get(Constants.LOGGED_INTO_SPOTIFY) != null &&
                    (Boolean) getIntent().getExtras().get(Constants.LOGGED_INTO_SPOTIFY);



            if (extrasNull || (!extrasNull && !loggedInSpotify)) {
                Intent intent = new Intent(this, SpotifyRefreshTokenActivity.class);
                startActivityForResult(intent, LOGGED_INTO_SPOTIFY_RESULT_CODE);
            }
        }
    }

    /**
     * After logging in with Google Or Spotify for the first time
     * You are returned here and we save the necessary User Information
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AssistantUser currentUser = mUsersBusiness.getCurrentUser();

        if (requestCode == LOGGED_INTO_GOOGLE_REQUEST_CODE) {
            if (requestCode == RESULT_OK)
                currentUser.setLoggedInGoogle(Boolean.TRUE);
            else {
                currentUser.setLoggedInGoogle(Boolean.FALSE);
                Log.w(LOG_TAG, "Error logging into Google");
            }

        } else if (requestCode == LOGGED_INTO_SPOTIFY_RESULT_CODE) {
            if (requestCode == RESULT_OK)
                currentUser.setLoggedInSpotify(Boolean.TRUE);
            else {
                currentUser.setLoggedInSpotify(Boolean.FALSE);
                Log.w(LOG_TAG, "Error logging into Spotify");
            }
        }

        mUsersBusiness.insertOrUpdateUser(currentUser);
    }

    private void doPermAudio() {
        int MY_PERMISSIONS_RECORD_AUDIO = 1;
        MainActivity thisActivity = this;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(thisActivity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_RECORD_AUDIO);
        }
    }

    private void validateMicAvailableAndPermissions() {
        PackageManager pm = getPackageManager();
        List activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() == 0) {
            TextView errorView = (TextView) findViewById(R.id.micNotAvailable);
            errorView.setText("Mic Is not Available on this Device");
        }


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    doPermAudio();
                }
            }
        });
    }

    private void setEditManuallyOnFocusListener() {
        TextView speechTextView = findViewById(R.id.speechToTextView);
        speechTextView.requestFocus();
        EditText manuallyEnter = ((EditText) findViewById(R.id.manuallyEnter));
        manuallyEnter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    aiService.cancel();
            }
        });
    }

    private void cleanUpEnterManually() {
        ((EditText) findViewById(R.id.manuallyEnter)).setText("");
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        TextView speechTextView = findViewById(R.id.speechToTextView);
        speechTextView.requestFocus();
    }


    private void getAIService() {
        AIConfiguration config = new AIConfiguration(API_KEY.DIALOG_FLOW_ASSISTANT_HELPER_KEY,
                AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
    }

    /**
     * Uses api.ai to call Dialog Flow web service with the text
     * that was entered into the input at the bottom of the screen
     */
    public class EnterManually extends AsyncTask<String, Void, AIResponse> {

        private AIService service;

        public EnterManually(AIService service) {
            this.service = service;
        }

        @Override
        protected AIResponse doInBackground(String... query) {

            AIResponse response = null;

            try {
                response = service.textRequest(query[0], null);
            } catch (AIServiceException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(AIResponse aiResponse) {
            super.onPostExecute(aiResponse);
            onResult(aiResponse);
            cleanUpEnterManually();
        }
    }

    /**
     * The speech that is returned from Web Service Async calls
     * needs to update the Text on the Screen. This will update
     * the speech from Runnable on Main Thread
     */
    public class UpdateSpeechTextOnMainThread implements Runnable{

        private String speech;

        public UpdateSpeechTextOnMainThread(String speech) {
            this.speech = speech;
        }

        @Override
        public void run() {
            TextView speechToTextView = findViewById(R.id.speechToTextView);
            mSpeechTranslator.convertTextToSpeech(new SpeechTranslatorDTO(speech, aiService, Boolean.FALSE, speechToTextView));
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mSpeechTranslator != null) {
            mSpeechTranslator.resume(this);
        }
    }

    @Override
    public void onError(AIError error) {
        if (error.getMessage().equals("Speech recognition engine error: No speech input.")) {
            TextView speechToTextView = findViewById(R.id.speechToTextView);
            getAIService();
            mSpeechTranslator.convertTextToSpeech(new SpeechTranslatorDTO(ConstantSpeech.MISSED_THAT, aiService, Boolean.TRUE, speechToTextView));

        } else
            Log.i(LOG_TAG, error.getMessage());
    }


    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
       //ripple.startRipple();
    }

    @Override
    public void onListeningCanceled() {
        //ripple.setRipple(Boolean.FALSE);
    }

    @Override
    public void onListeningFinished() {
        //ripple.setRipple(Boolean.FALSE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mSpeechTranslator != null)
            mSpeechTranslator.pause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSpeechTranslator != null)
            mSpeechTranslator.pause();

    }

    @Inject
    public void setUsersBusiness(IAssistantUsersBusiness usersBusiness) {
        this.mUsersBusiness = usersBusiness;
    }

    @Inject
    public void setExecutionService(IExecutionTranslationService executionService) {
        this.mExecutionService = executionService;
    }

    @Inject
    public void setMapsService(IMapsService mapsService) {
        this.mapsService = mapsService;
    }
}