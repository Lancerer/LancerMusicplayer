package com.example.lancer.lancermusicplayer;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FirstActivity extends AppCompatActivity {

    @InjectView(R.id.ll_local)
    LinearLayout llLocal;
    @InjectView(R.id.ll_intent)
    LinearLayout llIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ButterKnife.inject(this);
    }

    @SuppressLint("NewApi")
    @OnClick({R.id.ll_intent, R.id.ll_local})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.ll_local:
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            case R.id.ll_intent:
                Intent intent1=new Intent(this,IntentActivity.class);
                startActivity(intent1, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            default:
                break;
        }
    }
}
