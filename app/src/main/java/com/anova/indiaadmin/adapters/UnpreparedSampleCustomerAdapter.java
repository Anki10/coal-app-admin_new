package com.anova.indiaadmin.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anova.indiaadmin.R;
import com.anova.indiaadmin.UnpreparedSampleModel;

import java.util.List;

/**
 * Created by iqbal on 10/5/18.
 */

public class UnpreparedSampleCustomerAdapter extends RecyclerView.Adapter<UnpreparedSampleCustomerAdapter.CustomerViewHolder> {

    List<UnpreparedSampleModel.CustomersBean> customersList;
    Context context;

    public UnpreparedSampleCustomerAdapter(List<UnpreparedSampleModel.CustomersBean> customersList, Context context) {
        this.customersList = customersList;
        this.context = context;
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.item_row_expandable_customer_detail, parent, false);
        // set the view's size, margins, paddings and layout parameters
        CustomerViewHolder vh = new CustomerViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CustomerViewHolder holder, int position) {
        UnpreparedSampleModel.CustomersBean currentCustomer = customersList.get(position);
        //TODO fix customer name and FSA number, when API start giving data
        int colorBlack = ContextCompat.getColor(context, R.color.colorBlack);
        holder.customerName.setText(currentCustomer.getUserName());
        holder.customerName.setTextColor(colorBlack);

        if (currentCustomer.getAuctionType() == null || currentCustomer.getAuctionType().length()> 0){
            holder.auctionType.setText(currentCustomer.getAuctionType());
            holder.auctionType.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
        }

        if(currentCustomer.getDeclaredGrade()==null || currentCustomer.getDeclaredGrade().length()>0) {
            holder.declaredGrade.setText(currentCustomer.getDeclaredGrade());
            holder.declaredGrade.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
        }

        if (currentCustomer.getQuantity() == null || currentCustomer.getQuantity().length() > 0){
            holder.quantity_lifted.setText(currentCustomer.getQuantity());
            holder.quantity_lifted.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
        }


        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.collapsableView.getVisibility() == View.VISIBLE){
                    holder.downArrow.animate().rotation(180).start();
                    holder.collapsableView.setVisibility(View.GONE);
                } else {
                    holder.collapsableView.setVisibility(View.VISIBLE);
                    holder.downArrow.animate().rotation(0).start();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return customersList.size();
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder{
        private final LinearLayout parentView;
        private final TextView customerName;
        private final ImageView downArrow;
        private final LinearLayout collapsableView;
        private final TextView doDetails;
        private final TextView declaredGrade;
        private final TextView auctionType;
        private TextView quantity_lifted;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);
            customerName = (TextView) itemView.findViewById(R.id.customerName);
            downArrow = (ImageView) itemView.findViewById(R.id.downArrow);
            collapsableView = (LinearLayout) itemView.findViewById(R.id.collapsableView);
            doDetails = (TextView) itemView.findViewById(R.id.doDetails);
            declaredGrade = (TextView) itemView.findViewById(R.id.declaredGrade);
            auctionType = (TextView) itemView.findViewById(R.id.auctionType);
            quantity_lifted = (TextView) itemView.findViewById(R.id.quantity_lifted);
        }
    }
}
