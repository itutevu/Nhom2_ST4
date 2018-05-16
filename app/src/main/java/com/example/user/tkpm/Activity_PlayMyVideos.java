package com.example.user.tkpm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.services.youtube.YouTube;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Activity_PlayMyVideos extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, EventClick, EventClickEdit, EventClickSave {
    public static final String KEY_YOUTUBE = "AIzaSyB-rppiXny4ygct8js6FX7297Gawq7NmT0";
    private static final int RECOVERY_REQUEST = 1;


    private YouTubePlayerView playerYoutube;

    private LayoutInflater layoutInflater;
    private String url = "";
    private String key_id_video = "";
    private Dialog dialog;
    private String xml = "";
    private List<Script> scriptList;
    private List<Script> scriptListTemp;
    private YouTubePlayer youTubePlayer;
    private RecyclerView recyclerViewScript;
    private Adapter_RecyclerViewScript adapter_recyclerViewScript;

    private ImageView img_back;
    private ImageView img_add_sub;
    private EditText edt_search;


    /**
     * Define a global instance of a YouTube object, which will be used to make
     * YouTube Data API requests.
     */
    private static YouTube youtube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__play_my_videos);

        initView();

        playerYoutube.initialize(KEY_YOUTUBE, this);


        scriptList = new ArrayList<>();
        scriptListTemp = new ArrayList<>();

        Session_Script_Videos session_script_videos = new Session_Script_Videos(getBaseContext(), getIntent().getStringExtra("key_id_url"));

        for (HScript item : session_script_videos.getLichSuTimKiemTDList()) {
            scriptList.add(new Script(item.getScript(), item.getStart(), item.getDur()));
            scriptListTemp.add(new Script(item.getScript(), item.getStart(), item.getDur()));
        }

        initRecyclerViewScript();

    }


    private void initRecyclerViewScript() {
        CenterLayoutManager layoutManager = new CenterLayoutManager(getBaseContext());
        recyclerViewScript.setLayoutManager(layoutManager);


        adapter_recyclerViewScript = new Adapter_RecyclerViewScript(getBaseContext(), scriptList, this, this, this);
        recyclerViewScript.setAdapter(adapter_recyclerViewScript);


    }

    private Handler handler;
    Runnable runnable;

    private void handleSub() {

        if (handler != null)
            handler.removeCallbacks(runnable);

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {

                if (youTubePlayer != null) {
                    int mil = youTubePlayer.getCurrentTimeMillis();

                    for (int i = 0; i < scriptListTemp.size(); i++) {
                        int time = (int) Float.parseFloat(scriptListTemp.get(i).getStart()) * 1000;
                        int time2 = 0;
                        if (i + 1 < scriptListTemp.size()) {
                            time2 = (int) Float.parseFloat(scriptListTemp.get(i + 1).getStart()) * 1000;
                        }
                        if (time2 == 0) {
                            if (mil >= time) {
                                int poi = i;
                                if (poi < scriptList.size()) {
                                    if (scriptList.get(poi).getStart().equals(scriptListTemp.get(poi).getStart())) {
                                        adapter_recyclerViewScript.setPoi(poi);
                                        adapter_recyclerViewScript.notifyDataSetChanged();
                                        //recyclerViewScript.scrollToPosition(poi);
                                        recyclerViewScript.smoothScrollToPosition(poi);
                                    }
                                }

                                break;
                            }
                        } else {
                            if (mil >= time && mil < time2) {
                                int poi = i;
                                if (poi < scriptList.size()) {
                                    if (scriptList.get(poi).getStart().equals(scriptListTemp.get(poi).getStart())) {
                                        adapter_recyclerViewScript.setPoi(poi);
                                        adapter_recyclerViewScript.notifyDataSetChanged();
                                        //recyclerViewScript.scrollToPosition(poi);

                                        recyclerViewScript.smoothScrollToPosition(poi);
                                    }
                                }

                                break;
                            }
                        }

                    }
                }
                handler.postDelayed(this, 300);
            }
        };

        handler.postDelayed(runnable, 300);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null)
            handler.removeCallbacks(runnable);
    }

    private void initView() {
        playerYoutube = findViewById(R.id.playerYoutube);

        layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recyclerViewScript = findViewById(R.id.recyclerViewScript);


        img_add_sub = findViewById(R.id.img_add_sub);
        edt_search = findViewById(R.id.edt_search);
        img_back = findViewById(R.id.img_back);


        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                scriptList.clear();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String textSearch = charSequence.toString();
                for (Script item : scriptListTemp
                        ) {
                    if (item.getScript().trim().toLowerCase().contains(textSearch.trim().toLowerCase())) {
                        scriptList.add(item);
                    }
                }
                adapter_recyclerViewScript.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        img_add_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view1 = getLayoutInflater().inflate(R.layout.dialog_layout_3, null);
                dialog = new Dialog(Activity_PlayMyVideos.this);
                dialog.setContentView(view1);
                dialog.setCancelable(true);

                ColorDrawable color = new ColorDrawable(Color.TRANSPARENT);
                InsetDrawable inset = new InsetDrawable(color, 50);
                dialog.getWindow().setBackgroundDrawable(inset);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();


                Button btn_dongy = (Button) view1.findViewById(R.id.btn_dongy);
                Button btn_huybo = (Button) view1.findViewById(R.id.btn_huybo);
                final EditText edt_batdau = view1.findViewById(R.id.edt_batdau);
                final EditText edt_ketthuc = view1.findViewById(R.id.edt_ketthuc);
                final EditText edt_noidung = view1.findViewById(R.id.edt_noidung);

                btn_huybo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_dongy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (edt_batdau.getText().toString().isEmpty() || edt_ketthuc.getText().toString().isEmpty() || edt_noidung.getText().toString().isEmpty()) {
                            Toast.makeText(getBaseContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;

                        }
                        String start = edt_batdau.getText().toString();
                        String end = edt_ketthuc.getText().toString();
                        String dur = (Long.parseLong(start) - Long.parseLong(end)) + "";
                        Script script = new Script(edt_noidung.getText().toString(), Long.parseLong(start) + "", dur);
                        adapter_recyclerViewScript.addDataTop(script);
                        recyclerViewScript.scrollToPosition(0);
                        dialog.dismiss();
                    }
                });


                dialog.show();

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
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b)
            youTubePlayer.cueVideo(getIntent().getStringExtra("key_id_url"));

        this.youTubePlayer = youTubePlayer;
        if (dialog != null)
            dialog.dismiss();

        youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {

                handleSub();
            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onStopped() {

            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {

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
        if (dialog != null)
            dialog.dismiss();
    }

    private void initVideo() {

        if (youTubePlayer != null)
            youTubePlayer.cueVideo(key_id_video);


    }


    @Override
    public void onClick(int mil) {

    }

    @Override
    public void onClickEdit(Script script, int position) {

    }

    @Override
    public void onClickSave(Script script, int position) {

    }
}
