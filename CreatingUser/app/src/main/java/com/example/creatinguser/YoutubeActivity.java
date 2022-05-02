package com.example.creatinguser;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    private static final String TAG = "YoutubeActivity";
    static final String GOOGLE_API_KEY = "AIzaSyAO9CdtiQeD5l7pmEnrYueVxvkaR-52gpw";
    static final String YOUTUBE_VIDEO_ID = "T0nFoJFn2gA"; // portion of web address
    static final String YOUTUBE_PLAYLIST = "PLn3nHXu50t5zaUJUABsZexD7ki0lADzCU"; // portion of web address
    static final String NBA_HIGHLIGHTS = "PLlVlyGVtvuVm4_E_faSFuu3nU0F9O6XbR";
    static final String NBA_PLAYOFFS = "PLn3nHXu50t5wuHX2myLSY5cBdu_v0qNAl";
    static final String NFL_PLAYLIST = "PL1H2IyN18L0Sig6TkkAqLaoJFVBnukx9X";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_youtube);
//        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.activity_youtube);

        // alternative way than on top
        ConstraintLayout layout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_youtube, null);
        setContentView(layout);

//        Button button1 = new Button(this);
//        button1.setLayoutParams(new ConstraintLayout.LayoutParams(300, 80));
//        button1.setText("Button added");
//        layout.addView(button1);

        YouTubePlayerView playerView = new YouTubePlayerView(this);
        playerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)); // match parent will take all available space in layout
        layout.addView(playerView);
        playerView.initialize(GOOGLE_API_KEY,this);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        Log.d(TAG, "onInitializationSuccess: provider is " + provider.getClass().toString());
        //Toast.makeText(this, "Initialized Youtube Player successfully", Toast.LENGTH_LONG).show();

        if(!wasRestored){
            youTubePlayer.cueVideo(YOUTUBE_VIDEO_ID);
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        final int REQUEST_CODE = 1;

        if(youTubeInitializationResult.isUserRecoverableError()){
            youTubeInitializationResult.getErrorDialog(this, REQUEST_CODE).show();
        }else{
            String errorMessage = String.format("There was an error initializing the YoutubePlaer (%1$s)", youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }

    }

    // PlaybackEventListener is an interface, methods not required to play videos
//    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
//        @Override
//        public void onPlaying() {
//            Toast.makeText(YoutubeActivity.this, "Video is playing", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onPaused() {
//            Toast.makeText(YoutubeActivity.this, "Video has paused", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onStopped() {
//
//        }
//
//        @Override
//        public void onBuffering(boolean b) {
//
//        }
//
//        @Override
//        public void onSeekTo(int i) {
//
//        }
//    };

//    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
//        @Override
//        public void onLoading() {
//
//        }
//
//        @Override
//        public void onLoaded(String s) {
//
//        }
//
//        @Override
//        public void onAdStarted() {
//            Toast.makeText(YoutubeActivity.this, "Click on add if you like", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onVideoStarted() {
//            Toast.makeText(YoutubeActivity.this, "Video has started", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onVideoEnded() {
//            Toast.makeText(YoutubeActivity.this, "Video has ended", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onError(YouTubePlayer.ErrorReason errorReason) {
//
//        }
//    };
}