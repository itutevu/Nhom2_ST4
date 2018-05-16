package com.example.user.tkpm;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Activity_MyVideos extends AppCompatActivity implements EventClickPlayMyVideos, EventClickRemoveMyVideos {

    private RecyclerView recyclerViewScript;
    private Session_Name_Video session_name_video;
    private ImageView img_back;

    private List<NameVideoModel> stringList;
    private Adapter_RecyclerViewMyVideos adapter_recyclerViewHScript;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity__my_videos);
        session_name_video = new Session_Name_Video(getBaseContext());
        gson = new Gson();
        initView();
        initRecyclerView();

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerViewScript.setLayoutManager(layoutManager);

        stringList = new ArrayList<>();
        if (session_name_video.getListNameVideo() != null)
            stringList.addAll(session_name_video.getListNameVideo());
        adapter_recyclerViewHScript = new Adapter_RecyclerViewMyVideos(getBaseContext(), stringList, this, this);
        recyclerViewScript.setAdapter(adapter_recyclerViewHScript);
    }

    private void initView() {
        recyclerViewScript = findViewById(R.id.recyclerViewScript);
        img_back = findViewById(R.id.img_back);


        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClickPlay(String videoName, int position) {

        Intent intent = new Intent(Activity_MyVideos.this, Activity_PlayMyVideos.class);
        intent.putExtra("key_id_url", videoName);
        startActivity(intent);
    }

    @Override
    public void onClickRemove(final String videoName, final int position) {
        View view1 = getLayoutInflater().inflate(R.layout.dialog_layout_2, null);
        final Dialog dialog = new Dialog(Activity_MyVideos.this);
        dialog.setContentView(view1);
        dialog.setCancelable(true);

        ColorDrawable color = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(color, 50);
        dialog.getWindow().setBackgroundDrawable(inset);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();


        Button btn_dongy = (Button) view1.findViewById(R.id.btn_dongy);
        Button btn_huybo = (Button) view1.findViewById(R.id.btn_huybo);
        TextView txt_title = (TextView) view1.findViewById(R.id.txt_title);
        txt_title.setText("Bạn chắc chắn muốn xóa ?");

        btn_huybo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_dongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter_recyclerViewHScript.removeData(position);

                List<NameVideoModel> hScripts = new ArrayList<>();
                if (session_name_video.getListNameVideo() != null)
                    hScripts.addAll(session_name_video.getListNameVideo());


                for (int i = 0; i < hScripts.size(); i++) {
                    NameVideoModel item = hScripts.get(i);
                    if (item.getId().equals(videoName)) {
                        hScripts.remove(i);
                        break;
                    }
                }

                session_name_video.clearData();
                session_name_video.createSession(hScripts);

                Session_Script_Videos session_script_videos=new Session_Script_Videos(getBaseContext(),videoName);
                session_script_videos.clearData();
                Toast.makeText(getBaseContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });


        dialog.show();


    }
}
