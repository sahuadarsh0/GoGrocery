package app.infiniverse.grocery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by root on 28/3/18.
 */

public class Customer_Order_Adapter extends RecyclerView.Adapter<Customer_Order_Adapter.CustomerViewHolder> {

    public static final String PREFS = "PREFS";
    Context context;
    SharedPreferences sp;

    private String[] order_ids;
    private String[] order_savings;
    private String[] order_payableamts;
    private String[] order_status;
    private String[] order_dts;

    public Customer_Order_Adapter(String[] order_id, String[] order_saving, String[] order_payableamt, String[] order_statu, String[] order_dt, Context context) {
        this.order_ids=order_id;
        this.order_savings=order_saving;
        this.order_payableamts=order_payableamt;
        this.order_status=order_statu;
        this.order_dts=order_dt;
        this.context=context;
    }


    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_orders, parent, false);
        return new CustomerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        String order_id = order_ids[position];
        String order_saving = order_savings[position];
        String order_payableamt = order_payableamts[position];
        String order_statu = order_status[position];
        String order_dt = order_dts[position];

        holder.tvOrderId.setText(order_id);
        holder.tvOrderSaving.setText("\u20B9"+order_saving);
        holder.tvOrderPayableAmt.setText("\u20B9"+order_payableamt);
        holder.tvOrderStatus.setText(order_statu);
        holder.tvOrderDate.setText(order_dt.substring(8,10)+"/"+order_dt.substring(5,7)+"/"+order_dt.substring(0,4));
        holder.tvOrderTime.setText(order_dt.substring(11));

        if(order_statu.trim().equals("PENDING")){
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.holo_red_light));
        }else if(order_statu.trim().equals("DELIVERED")){
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.holo_green_light));
        }
    }

    @Override
    public int getItemCount() {
        return order_ids.length;
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderId;
        TextView tvOrderSaving;
        TextView tvOrderPayableAmt;
        TextView tvOrderStatus;
        TextView tvOrderDate;
        TextView tvOrderTime;
        CardView card ;


        public CustomerViewHolder(final View itemView) {

            super(itemView);
            tvOrderId = itemView.findViewById(R.id.orderId);
            tvOrderSaving = itemView.findViewById(R.id.total_savings);
            tvOrderPayableAmt = itemView.findViewById(R.id.payableAmount);
            tvOrderStatus = itemView.findViewById(R.id.status);
            tvOrderDate = itemView.findViewById(R.id.order_date);
            tvOrderTime = itemView.findViewById(R.id.order_time);

            card = itemView.findViewById(R.id.order_history_cart);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(context,OrderDetailsActivity.class);
                    i.putExtra("order_id",tvOrderId.getText().toString());
                    context.startActivity(i);
                }
            });

        }


    }

}