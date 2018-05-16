package com.example.user.tkpm;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.tkpm.Model.Data;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;
import com.google.api.services.youtube.model.CaptionSnippet;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.jetty.Main;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.GONE;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, EventClick, EventClickEdit, EventClickSave {
    public static final String KEY_YOUTUBE = "AIzaSyB-rppiXny4ygct8js6FX7297Gawq7NmT0";
    private static final int RECOVERY_REQUEST = 1;


    private YouTubePlayerView playerYoutube;
    private ImageView img_add;
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
    private DrawerLayout drawerLayout;
    private ImageView img_menu;
    private EditText edt_search;
    private Session_Script session_script;
    private LinearLayout ln_my_sentences, ln_my_videos;
    private Session_Name_Video session_name_video;
    private ImageView vidEdit;
    private ImageView vidBookmark;

    private String title = "";

    Gson gson;
    /**
     * Define a global instance of a YouTube object, which will be used to make
     * YouTube Data API requests.
     */
    private static YouTube youtube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();
        initView();
        initRecyclerViewScript();
        playerYoutube.initialize(KEY_YOUTUBE, this);
        session_script = new Session_Script(getBaseContext());
        session_name_video = new Session_Name_Video(getBaseContext());

        if (getIntent().hasExtra("url")) {
            url = getIntent().getStringExtra("url");

            key_id_video = extractVideoIdFromUrl(url);
            initVideo();
        }

    }


    private boolean isPlay = false;

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null)
            handler.removeCallbacks(runnable);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onClick(int mil) {
        if (youTubePlayer != null)
            youTubePlayer.seekToMillis(mil);
    }

    @Override
    public void onClickEdit(Script script, final int postion) {
        View view1 = getLayoutInflater().inflate(R.layout.dialog_layout_1, null);
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(view1);
        dialog.setCancelable(true);

        ColorDrawable color = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(color, 50);
        dialog.getWindow().setBackgroundDrawable(inset);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        final EditText editText = view1.findViewById(R.id.edt_url);
        Button btn_dongy = (Button) view1.findViewById(R.id.btn_dongy);
        Button btn_huybo = (Button) view1.findViewById(R.id.btn_huybo);
        TextView txt_title = (TextView) view1.findViewById(R.id.txt_title);
        txt_title.setText("Chỉnh sửa phụ đề");

        editText.setText(script.getScript());

        btn_huybo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_dongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scriptList.get(postion).setScript(editText.getText().toString());
                scriptListTemp.get(postion).setScript(editText.getText().toString());
                adapter_recyclerViewScript.notifyItemChanged(postion);
                if (dialog != null)
                    dialog.dismiss();
            }
        });


        dialog.show();
    }


    @Override
    public void onClickSave(Script script, int position) {
        List<HScript> hScripts = new ArrayList<>();
        if (session_script.getLichSuTimKiemTDList() != null)
            hScripts.addAll(session_script.getLichSuTimKiemTDList());
        HScript hScript = new HScript(url, script.getScript(), script.getStart(), script.getDur());


        for (HScript item : hScripts
                ) {
            if (item.getUrl().equals(hScript.getUrl()) && item.getScript().equals(hScript.getScript())) {
                Toast.makeText(getBaseContext(), "Đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        hScripts.add(0, hScript);
        if (hScripts.size() > Session_Script.NUMBER_H)
            hScripts.remove(Session_Script.NUMBER_H);

        session_script.clearData();
        session_script.createSession(hScripts);
        Toast.makeText(getBaseContext(), "Đã lưu thành công", Toast.LENGTH_SHORT).show();
    }


    private void initRecyclerViewScript() {
        CenterLayoutManager centerLayoutManager = new CenterLayoutManager(getBaseContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerViewScript.setLayoutManager(centerLayoutManager);

        scriptList = new ArrayList<>();
        scriptListTemp = new ArrayList<>();
        adapter_recyclerViewScript = new Adapter_RecyclerViewScript(getBaseContext(), scriptList, this, this, this);
        recyclerViewScript.setAdapter(adapter_recyclerViewScript);


    }


    private void initView() {
        playerYoutube = findViewById(R.id.playerYoutube);
        img_add = findViewById(R.id.img_add);
        layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recyclerViewScript = findViewById(R.id.recyclerViewScript);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.parseColor("#66000000"));
        img_menu = findViewById(R.id.img_menu);
        edt_search = findViewById(R.id.edt_search);
        ln_my_sentences = findViewById(R.id.ln_my_sentences);
        ln_my_videos = findViewById(R.id.ln_my_videos);
        vidEdit = findViewById(R.id.img_edit);
        vidBookmark = findViewById(R.id.img_bookmark);

        vidBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (key_id_video.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Chưa tải video", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (scriptListTemp.size() == 0) {
                    Toast.makeText(getBaseContext(), "Chưa có subtitle", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<NameVideoModel> stringList = new ArrayList<>();
                stringList.addAll(session_name_video.getListNameVideo());

                NameVideoModel nameVideoModel = new NameVideoModel();
                nameVideoModel.setId(key_id_video);
                nameVideoModel.setTitle(title);
                stringList.add(0, nameVideoModel);
                session_name_video.clearData();
                session_name_video.createSession(stringList);


                Session_Script_Videos session_script_videos = new Session_Script_Videos(getBaseContext(), key_id_video);

                List<HScript> hScripts = new ArrayList<>();
                for (Script item : scriptListTemp) {
                    HScript hScript = new HScript(url, item.getScript(), item.getStart(), item.getDur());
                    hScripts.add(hScript);
                }

                session_script_videos.createSession(hScripts);

                Toast.makeText(getBaseContext(), "Lưu thành công", Toast.LENGTH_SHORT).show();
            }
        });

        ln_my_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Activity_MyVideos.class);
                startActivity(intent);
            }
        });


        ln_my_sentences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Activity_MySentences.class);
                startActivity(intent);
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

        vidEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view1 = getLayoutInflater().inflate(R.layout.dialog_layout_3, null);
                dialog = new Dialog(MainActivity.this);
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
                        scriptListTemp.add(0, script);
                        dialog.dismiss();
                    }
                });


                dialog.show();

            }
        });

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view1 = getLayoutInflater().inflate(R.layout.dialog_layout_1, null);
                dialog = new Dialog(MainActivity.this);
                dialog.setContentView(view1);
                dialog.setCancelable(true);

                ColorDrawable color = new ColorDrawable(Color.TRANSPARENT);
                InsetDrawable inset = new InsetDrawable(color, 50);
                dialog.getWindow().setBackgroundDrawable(inset);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();

                final EditText editText = view1.findViewById(R.id.edt_url);
                Button btn_dongy = (Button) view1.findViewById(R.id.btn_dongy);
                Button btn_huybo = (Button) view1.findViewById(R.id.btn_huybo);
                TextView txt_title = (TextView) view1.findViewById(R.id.txt_title);
                txt_title.setText("Paste a youtube video's url");

                btn_huybo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_dongy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText.getText().toString().isEmpty()) {
                            Toast.makeText(getBaseContext(), "Chưa nhập nội dung", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        url = editText.getText().toString();
                        adapter_recyclerViewScript.refreshData();
                        key_id_video = extractVideoIdFromUrl(url);
                        if (!url.isEmpty())
                            initVideo();
                        if (dialog != null)
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
        /*if (!b)
            youTubePlayer.cueVideo(key_id_video);*/

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

        LoadExampleTask loadExampleTask = new LoadExampleTask();
        loadExampleTask.execute();

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute(key_id_video);

        //img_add.setVisibility(GONE);
        vidEdit.setVisibility(View.VISIBLE);
        vidBookmark.setVisibility(View.VISIBLE);
    }


    private class LoadExampleTask extends
            AsyncTask<String, Integer, List<Script>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // code here
        }

        @Override
        protected List<Script> doInBackground(
                String... params) {
            List<Script> scriptList = new ArrayList<>();
            XMLParser parser = new XMLParser(); // the parser create as seen in the Gist from GitHub
            //http://video.google.com/timedtext?lang=en&v=ziAA9tuLuiU
            xml = parser.getXmlFromUrl("https://www.youtube.com/api/timedtext?lang=en&v=" + key_id_video + "&tlang=en"); // getting XML from URL

            if (xml.isEmpty()) {
                return scriptList;
            }
            Document doc = parser.getDomElement(xml); // getting DOM element

            if (doc == null)
                return scriptList;

            NodeList nl = doc.getElementsByTagName("transcript");

            if (nl == null)
                return scriptList;

            HashMap<String, String> map = new HashMap<String, String>();
            Element e = (Element) nl.item(0);
            NodeList n2 = e.getChildNodes();

            for (int i = 0; i < n2.getLength(); i++) {
                Element e2 = (Element) n2.item(i);
                int x = 0;

                String script = e2.getChildNodes().item(0).getNodeValue();
                String start = e2.getAttribute("start");
                String dur = e2.getAttribute("dur");
                scriptList.add(new Script(script, start, dur));
            }


            return scriptList;
        }

        @Override
        protected void onPostExecute(List<Script> result) {
            super.onPostExecute(result);

            try {
                if (result.size() != 0) {
                    scriptListTemp.clear();
                    scriptListTemp.addAll(result);
                    adapter_recyclerViewScript.refreshData();
                    adapter_recyclerViewScript.appendData(result);
                } else {
                    Toast.makeText(getBaseContext(), "Không thể tải phụ đề cho video này", Toast.LENGTH_SHORT).show();
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }


    public String getyoutubeitemfull_details() throws SQLException, IOException {
        try {
            YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("APP_ID").build();

            String apiKey = KEY_YOUTUBE; // you can get it from https://console.cloud.google.com/apis/credentials
            YouTube.Videos.List listVideosRequest = youtube.videos().list("statistics");
            listVideosRequest.setId("yVcMc9HVXvc"); // add list of video IDs here
            listVideosRequest.setKey(apiKey);
            VideoListResponse listResponse = listVideosRequest.execute();

            Video video = listResponse.getItems().get(0);

            BigInteger viewCount = video.getStatistics().getViewCount();
            BigInteger Likes = video.getStatistics().getLikeCount();
            BigInteger DisLikes = video.getStatistics().getDislikeCount();
            BigInteger Comments = video.getStatistics().getCommentCount();


            System.out.println("[View Count] " + viewCount);
            System.out.println("[Likes] " + Likes);
            System.out.println("[Dislikes] " + DisLikes);
            System.out.println("[Comments] " + Comments);

            return video.getSnippet().getTitle();

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }


    class MyAsyncTask extends AsyncTask<String, String, String> {

        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            String url_select = "https://www.googleapis.com/youtube/v3/videos?id=" + params[0] + "&key=" + KEY_YOUTUBE + "&fields=items(id,snippet(channelId,title,categoryId),statistics)&part=snippet,statistics";

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

            try {
                // Set up HTTP post

                // HttpClient is more then less deprecated. Need to change to URLConnection
                HttpClient httpClient = new DefaultHttpClient();

                HttpGet httpGet = new HttpGet(url_select);
                //httpPost.setEntity(new UrlEncodedFormEntity(param));
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();

                // Read content & Log
                inputStream = httpEntity.getContent();
            } catch (UnsupportedEncodingException e1) {
                Log.e("UnsupportedEncodingEx", e1.toString());
                e1.printStackTrace();
            } catch (ClientProtocolException e2) {
                Log.e("ClientProtocolException", e2.toString());
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            }
            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();

            } catch (Exception e) {
                Log.e("StrBuild&BufferedRead", "Error converting result " + e.toString());
            }
            return result;
        } // protected Void doInBackground(String... params)


        protected void onPostExecute(String result) {
            //parse JSON data
            try {
                JSONObject jsonObject = new JSONObject(result);

                Data data = gson.fromJson(result, Data.class);

                if (data.getItems().size() != 0)
                    title = data.getItems().get(0).getSnippet().getTitle();

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)
        } // protected void onPostExecute(Void v)
    } //class MyAsyncTask extends AsyncTask<String, String, Void>
}
