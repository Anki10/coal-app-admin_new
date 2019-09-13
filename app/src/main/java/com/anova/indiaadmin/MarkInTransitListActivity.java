package com.anova.indiaadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anova.indiaadmin.adapters.MarkInTransitListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MarkInTransitListActivity extends AppCompatActivity {

    @BindView(R.id.backLayout)
    RelativeLayout backLayout;
    @BindView(R.id.titleText)
    TextView titleText;
    @BindView(R.id.steptext)
    TextView steptext;
    @BindView(R.id.logoutLayout)
    RelativeLayout logoutLayout;
    @BindView(R.id.rvSamplelist)
    RecyclerView rvSamplelist;
    @BindView(R.id.llEmptyTransitList)
    LinearLayout llEmptyTransitListLayout;

    List<MarkInTransitModel> markInTransitModelList;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_in_transit_list);
        ButterKnife.bind(this);

        titleText.setText("Mark in Transit");
        rvSamplelist.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvSamplelist.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("transit_filter");

        markInTransitModelList = (ArrayList<MarkInTransitModel>) args.getSerializable("MARKLIST");

        MarkInTransitListAdapter markInTransitListAdapter = new MarkInTransitListAdapter(this, markInTransitModelList);
                    rvSamplelist.setAdapter(markInTransitListAdapter);
                    llEmptyTransitListLayout.setVisibility(View.GONE);
                    rvSamplelist.setVisibility(View.VISIBLE);



    }

    @OnClick({R.id.backLayout, R.id.logoutLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backLayout:
                onBackPressed();
                break;
            case R.id.logoutLayout:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
