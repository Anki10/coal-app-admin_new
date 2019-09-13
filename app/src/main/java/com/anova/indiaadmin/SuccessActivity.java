package com.anova.indiaadmin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SuccessActivity extends AppCompatActivity {

    Typeface light, bold;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.subTitle)
    TextView subTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        ButterKnife.bind(this);

        new Handler().postDelayed(new Runnable() {

			/*
             * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {
                goToHome();
            }
        }, 3000);
        light = Typeface.createFromAsset(getAssets(), "fonts/avenir_nextltpro_cn.otf");
        bold = Typeface.createFromAsset(getAssets(), "fonts/avenir_nextltpro_demi.otf");

        title.setTypeface(bold);
        subTitle.setTypeface(light);

    }

    private void goToHome() {
        Intent in = new Intent(this, HomeActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
        finish();
    }
}
