package kr.co.company.plccontrol;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    ImageButton observe,history,settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
        observe = (ImageButton)findViewById(R.id.observebtn);
        history = (ImageButton)findViewById(R.id.historybtn);
        settings = (ImageButton)findViewById(R.id.settingsbtn);
    }

    public void onclick(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.observebtn:
                intent = new Intent(this,Observation.class);
                startActivity(intent);
                break;
            case R.id.historybtn:
                intent = new Intent(this,History.class);
                startActivity(intent);
                break;

            case R.id.settingsbtn:
                intent = new Intent(this,Settings.class);
                startActivity(intent);
                break;

        }
    }
}
