package com.example.user.tkpm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Activity_Play extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final String KEY_YOUTUBE = "AIzaSyB-rppiXny4ygct8js6FX7297Gawq7NmT0";
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerView playerYoutube;
    private TextView tv_script;
    private Gson gson;
    HScript hScript;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__play);


        gson = new Gson();

        String json = getIntent().getStringExtra("url");
        hScript = gson.fromJson(json, HScript.class);

        playerYoutube = findViewById(R.id.playerYoutube);
        playerYoutube.initialize(KEY_YOUTUBE, this);



        tv_script = findViewById(R.id.tv_script);
        tv_script.setText(Html.fromHtml(hScript.getScript()));



        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    final String[] videoIdRegex = {"\\?vi?=([^&]*)", "watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};

    public String extractVideoIdFromUrl(String url) {
        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(url);

        for (String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return "";
    }

    private String youTubeLinkWithoutProtocolAndDomain(String url) {
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return url.replace(matcher.group(), "");
        }
        return url;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {

        this.youTubePlayer = youTubePlayer;
        youTubePlayer.cueVideo(extractVideoIdFromUrl(hScript.getUrl()));

        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {
                int mil = (int) Float.parseFloat(hScript.getStart()) * 1000;
                youTubePlayer.seekToMillis(mil);
            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            //youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }

    }
}
