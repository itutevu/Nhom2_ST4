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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Activity_MySentences extends AppCompatActivity implements EventClickPlay, EventClickRemove {

    private RecyclerView recyclerViewScript;
    private Session_Script session_script;
    private ImageView img_back;

    private List<HScript> hScripts;
    private Adapter_RecyclerViewHScript adapter_recyclerViewHScript;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__my_sentences);

        session_script = new Session_Script(getBaseContext());
        gson = new Gson();
        initView();
        initRecyclerView();

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerViewScript.setLayoutManager(layoutManager);

        hScripts = new ArrayList<>();
        if (session_script.getLichSuTimKiemTDList() != null)
            hScripts.addAll(session_script.getLichSuTimKiemTDList());
        adapter_recyclerViewHScript = new Adapter_RecyclerViewHScript(getBaseContext(), hScripts, this, this);
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
    public void onClickPlay(HScript script, int position) {
        String json = gson.toJson(script);
        Intent intent = new Intent(Activity_MySentences.this, Activity_Play.class);
        intent.putExtra("url", json);
        startActivity(intent);
    }

    @Override
    public void onClickRemove(final HScript script, final int position) {
        View view1 = getLayoutInflater().inflate(R.layout.dialog_layout_2, null);
        final Dialog dialog = new Dialog(Activity_MySentences.this);
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

                List<HScript> hScripts = new ArrayList<>();
                if (session_script.getLichSuTimKiemTDList() != null)
                    hScripts.addAll(session_script.getLichSuTimKiemTDList());


                for (int i=0;i<hScripts.size();i++){
                    HScript item=hScripts.get(i);
                    if(item.getUrl().equals(script.getUrl())&&item.getScript().equals(script.getScript()))
                    {
                        hScripts.remove(i);
                        break;
                    }
                }

                session_script.clearData();
                session_script.createSession(hScripts);
                Toast.makeText(getBaseContext(),"Xóa thành công",Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });


        dialog.show();


    }
}
