package com.anova.indiaadmin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anova.indiaadmin.R;
import com.anova.indiaadmin.database.samplecustomer.MoredetailsEntity;

import java.util.ArrayList;

/**
 * Created by raj on 6/7/2018.
 */

public class DoDetailsAdapter extends RecyclerView.Adapter<DoDetailsAdapter.viewHolder> {

    private ArrayList<MoredetailsEntity>arrayList;
    private Context context;
    private CustomerFormListAdapter adapter;
    private int pos;

    public DoDetailsAdapter(CustomerFormListAdapter adapter,Context context,ArrayList<MoredetailsEntity>list,int pos1){

        this.context = context;
        this.arrayList = list;
        this.adapter = adapter;
        this.pos = pos1;

    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.do_details_row, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, final int position) {

        MoredetailsEntity pojo = arrayList.get(position);

        if (!pojo.getFsa_number().equalsIgnoreCase("0")){
            holder.tv_fsaNumber.setText(pojo.getFsa_number());
        }else {
            holder.tv_fsaNumber.setText("");
        }

        if (!pojo.getDo_details().equalsIgnoreCase("0")){
            holder.tv_doDetails.setText(pojo.getDo_details());
        }else {
            holder.tv_doDetails.setText("");
        }

        holder.tv_quantity.setText(pojo.getQuantity());


        holder.iv_cross_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.remove(position);
                notifyDataSetChanged();

                try {
   //                adapter.dolist.remove(position);

                    adapter.setDeleteList(arrayList,pos);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        private TextView tv_fsaNumber,tv_doDetails,tv_quantity;
        private ImageView iv_cross_do;

        public viewHolder(View itemView) {
            super(itemView);

            tv_fsaNumber = (TextView) itemView.findViewById(R.id.tv_fsaNumber);
            tv_doDetails = (TextView) itemView.findViewById(R.id.tv_doDetails);
            tv_quantity = (TextView) itemView.findViewById(R.id.tv_quantity);

            iv_cross_do = (ImageView) itemView.findViewById(R.id.iv_cross_do);
        }
    }
}
