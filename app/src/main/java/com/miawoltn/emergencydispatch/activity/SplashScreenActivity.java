package com.miawoltn.emergencydispatch.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.miawoltn.emergencydispatch.R;
import com.miawoltn.emergencydispatch.setup.InstructionsActivity;

public class SplashScreenActivity extends Activity {

    private static int SPLASH_TIME_OUT = 300;
    private static int INSTRUCTIONS_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        SharedPreferences settings = getSharedPreferences("prefs", 0);
        boolean firstRun = settings.getBoolean("firstRun", true);
        if ( firstRun ) {
            // here run your first-time instructions, for example :
            startActivityForResult(
                    new Intent(SplashScreenActivity.this, InstructionsActivity.class),
                    INSTRUCTIONS_CODE);

        }else {
            finish();
            startActivity(new Intent(SplashScreenActivity.this, MainViewActivity.class));
        }

    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            SharedPreferences settings = getSharedPreferences("prefs", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstRun", false);
            editor.commit();
            finish();
            startActivity(new Intent(SplashScreenActivity.this, MainViewActivity.class));
        }
    }

}
