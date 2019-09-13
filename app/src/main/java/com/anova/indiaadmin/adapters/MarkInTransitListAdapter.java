package com.anova.indiaadmin.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.MarkInTransitModel;
import com.anova.indiaadmin.R;
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by iqbal on 11/5/18.
 */

public class MarkInTransitListAdapter extends RecyclerView.Adapter<MarkInTransitListAdapter.MarkInTransitViewHolder> implements AppNetworkResponse {

    Context context;
    List<MarkInTransitModel> markInTransitModelList;
    MarkInTransitListAdapter markInTransitListAdapter;
    int mPosition;

    public MarkInTransitListAdapter(Context context, List<MarkInTransitModel> markInTransitModelList) {
        this.context = context;
        this.markInTransitModelList = markInTransitModelList;
        markInTransitListAdapter = this;
    }

    @Override
    public MarkInTransitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_row_mark_in_transit, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MarkInTransitViewHolder vh = new MarkInTransitViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MarkInTransitViewHolder holder, int position) {
        final MarkInTransitModel currentMarkInTransitModel = markInTransitModelList.get(position);
        holder.sampleId.setText(currentMarkInTransitModel.getQci()==null?"NULL":currentMarkInTransitModel.getQci());
        holder.date.setText(currentMarkInTransitModel.getDate());
        holder.markInTransit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    mPosition = holder.getAdapterPosition();
                    jsonObject.put("sample_id", Integer.parseInt(currentMarkInTransitModel.getSampleId()));
                    Volley volley = Volley.getInstance();
                    volley.postSession(Constants.apiPostMarkInTransit, jsonObject, markInTransitListAdapter,getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_MARK_IN_TRANSIT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return markInTransitModelList.size();
    }

    public void add(int position, MarkInTransitModel item) {
        markInTransitModelList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        markInTransitModelList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode){
            case Constants.REQ_POST_MARK_IN_TRANSIT:{
                Toast.makeText(context, "Sample Id : " + markInTransitModelList.get(mPosition).getSampleId() + ", is now marked in transit", Toast.LENGTH_SHORT).show();
                remove(mPosition);
            }
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {

    }

    public class MarkInTransitViewHolder extends RecyclerView.ViewHolder {
        public TextView sampleId, date, markInTransit;

        public MarkInTransitViewHolder(View itemView) {
            super(itemView);
            sampleId = (TextView) itemView.findViewById(R.id.sampleId);
            date = (TextView) itemView.findViewById(R.id.date);
            markInTransit = (TextView) itemView.findViewById(R.id.tvMarkInTransit);
        }


    }
    public String getFromPrefs(String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }
}
