package com.example.user.tkpm;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;
import com.google.api.services.youtube.model.CaptionSnippet;
import com.google.common.collect.Lists;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, EventClick {
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
    private YouTubePlayer youTubePlayer;
    private RecyclerView recyclerViewScript;
    private Adapter_RecyclerViewScript adapter_recyclerViewScript;


    /**
     * Define a global instance of a YouTube object, which will be used to make
     * YouTube Data API requests.
     */
    private static YouTube youtube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
        initRecyclerViewScript();


        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = getBaseContext().getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = getBaseContext().getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", getBaseContext().getApplicationContext().getPackageName());

            for (android.content.pm.Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
       /* LoadExampleTask loadExampleTask = new LoadExampleTask();
        loadExampleTask.execute();*/



      /* *//* LoadExampleTask loadExampleTask = new LoadExampleTask();
        loadExampleTask.execute();*//*

        // This OAuth 2.0 access scope allows for full read/write access to the
        // authenticated user's account and requires requests to use an SSL connection.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");


        // Authorize the request.
        Credential credential = null;
        try {
            credential = Auth.authorize(scopes, "captions");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // This object is used to make YouTube Data API requests.
        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                .setApplicationName("youtube-cmdline-captions-sample").build();


       *//* try {
            List<Caption> captionList=listCaptions("M7FIvfx5J10");
        } catch (IOException e) {
            e.printStackTrace();
        }*//*
        *//*GetData getData = new GetData();
        getData.execute();*//*

       *//* url = "http://video.google.com/timedtext?lang=en&v=ziAA9tuLuiU";
        key_id_video = extractVideoIdFromUrl(url);
        initVideo();*/


        if(getIntent().hasExtra("url")){
            url=getIntent().getStringExtra("url");

            key_id_video = extractVideoIdFromUrl(url);
            initVideo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * Returns a list of caption tracks. (captions.listCaptions)
     *
     * @param videoId The videoId parameter instructs the API to return the
     *                caption tracks for the video specified by the video id.
     * @throws IOException
     */
    private static List<Caption> listCaptions(String videoId) throws IOException {
        // Call the YouTube Data API's captions.list method to
        // retrieve video caption tracks.
        CaptionListResponse captionListResponse = youtube.captions().
                list("snippet", videoId).execute();

        List<Caption> captions = captionListResponse.getItems();
        // Print information from the API response.
        System.out.println("\n================== Returned Caption Tracks ==================\n");
        CaptionSnippet snippet;
        for (Caption caption : captions) {
            snippet = caption.getSnippet();
            System.out.println("  - ID: " + caption.getId());
            System.out.println("  - Name: " + snippet.getName());
            System.out.println("  - Language: " + snippet.getLanguage());
            System.out.println("\n-------------------------------------------------------------\n");
        }

        return captions;
    }

    @Override
    public void onClick(int mil) {
        if (youTubePlayer != null)
            youTubePlayer.seekToMillis(mil);
    }

    class GetData extends AsyncTask<String, Void, List<Caption>> {

        private Exception exception;

        protected List<Caption> doInBackground(String... urls) {
            try {
                CaptionListResponse captionListResponse = youtube.captions().
                        list("snippet", "vx6NCUyg1NE").execute();

                List<Caption> captions = captionListResponse.getItems();
                // Print information from the API response.
                //System.out.println("\n================== Returned Caption Tracks ==================\n");
                CaptionSnippet snippet;
                for (Caption caption : captions) {
                    snippet = caption.getSnippet();
                    System.out.println("  - ID: " + caption.getId());
                    System.out.println("  - Name: " + snippet.getName());
                    System.out.println("  - Language: " + snippet.getLanguage());
                    System.out.println("\n-------------------------------------------------------------\n");
                }

                return captions;
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(List<Caption> result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            int x = 0;
        }
    }

    private void initRecyclerViewScript() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerViewScript.setLayoutManager(layoutManager);

        scriptList = new ArrayList<>();
        adapter_recyclerViewScript = new Adapter_RecyclerViewScript(getBaseContext(), scriptList, this);
        recyclerViewScript.setAdapter(adapter_recyclerViewScript);

        recyclerViewScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (youTubePlayer != null)
                    youTubePlayer.seekToMillis(10000);
            }
        });
    }

    private void initView() {
        playerYoutube = findViewById(R.id.playerYoutube);
        img_add = findViewById(R.id.img_add);
        layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recyclerViewScript = findViewById(R.id.recyclerViewScript);


        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(MainActivity.this);

                View v = layoutInflater.inflate(R.layout.dialog_add, null);
                dialog.setContentView(v);

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                final EditText editText = v.findViewById(R.id.edt_url);
                Button btn_ok = v.findViewById(R.id.btn_ok);


                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        url = editText.getText().toString();

                        key_id_video = extractVideoIdFromUrl(url);
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

        return null;
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
            youTubePlayer.cueVideo(key_id_video);
        this.youTubePlayer = youTubePlayer;
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {

            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }
        if (dialog != null)
            dialog.dismiss();
    }

    private void initVideo() {
        playerYoutube.initialize(KEY_YOUTUBE, this);

        LoadExampleTask loadExampleTask = new LoadExampleTask();
        loadExampleTask.execute();

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
            Document doc = parser.getDomElement(xml); // getting DOM element



            NodeList nl = doc.getElementsByTagName("transcript");


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
                adapter_recyclerViewScript.refreshData();
                adapter_recyclerViewScript.appendData(result);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
