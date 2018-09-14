package app.infiniverse.grocery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


/**
 * Created by root on 24/3/18.
 */

public class Recent_Products_Adapter extends RecyclerView.Adapter<Recent_Products_Adapter.ProductsViewHolder> {

    public static final String PREFS = "PREFS";
    Context context;
    SharedPreferences sp;
    private String[] product_id;
    private String[] product_name;
    private String[] product_desc;
    private String[] product_img;
    private String[] product_price;
    private String[] product_brand;
    private String[] product_sp;
    private String[] product_dp;

    public Recent_Products_Adapter(String[] product_id, String[] product_name, String[] product_desc, String[] product_img, String[] product_price, String[] product_brand, String[] product_sp, String[] product_dp, Context context) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.product_img = product_img;
        this.product_price = product_price;
        this.product_brand = product_brand;
        this.product_sp = product_sp;
        this.product_dp = product_dp;
        this.context = context;
    }


    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_recent_products, parent, false);
        return new ProductsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ProductsViewHolder holder, int position) {
        String id = product_id[position];
        String name = product_name[position];
        String desc = product_desc[position];
        String img = context.getResources().getString(R.string.img_base_url) + "product_images/" + product_img[position];
        String price = product_price[position];
        String selling_price = product_sp[position];
        String brand = product_brand[position];
        String discount = product_dp[position];

        holder.pro_id.setText(id);
        holder.pro_name.setText(name);
        holder.pro_desc.setText(desc);
        holder.pro_price.setText(price);
        holder.pro_sp.setText(selling_price);
        holder.pro_brand.setText(brand);
        holder.pro_discount.setText(discount + " %   OFF");

        if (Integer.parseInt(discount) <= 0) {
            holder.pro_discount.setVisibility(View.GONE);
        }
        if(selling_price.trim().equals("\u20B9"+price.trim())){
            holder.pro_price.setVisibility(View.GONE);
        }


        Picasso.with(context).load(img).placeholder(R.drawable.watermark_icon).into(holder.pro_img);

    }

    @Override
    public int getItemCount() {
        return product_id.length;
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder {

        TextView pro_id;
        TextView pro_name;
        TextView pro_desc;
        TextView pro_price;
        TextView pro_sp;
        TextView pro_brand;
        TextView pro_discount;
        TextView pro_add;
        ImageView pro_img;
        LinearLayout layout;


        public ProductsViewHolder(final View itemView) {

            super(itemView);
            pro_id = itemView.findViewById(R.id.product_id);
            pro_name = itemView.findViewById(R.id.product_name);
            pro_desc = itemView.findViewById(R.id.product_short_desc);
            pro_img = itemView.findViewById(R.id.product_img);
            pro_price = itemView.findViewById(R.id.product_price);
            pro_sp = itemView.findViewById(R.id.selling_price);
            pro_brand = itemView.findViewById(R.id.brand_name);
            pro_discount = itemView.findViewById(R.id.discount);
            pro_add = itemView.findViewById(R.id.product_add);
            strikeThroughText(pro_price);
            layout = itemView.findViewById(R.id.product_card);

            sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product detail = new Product();
                    detail.startProductDetailActivity(pro_id.getText().toString(), context);
                }
            });
            pro_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product detail = new Product();
                    detail.startProductDetailActivity(pro_id.getText().toString(), context);
                }
            });
            pro_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product detail = new Product();
                    detail.startProductDetailActivity(pro_id.getText().toString(), context);

                }
            });
            pro_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(sp.getString("loginid",null)!=null) {
                        AddToCart addToCart = new AddToCart(context);
                        addToCart.addToCart(pro_id.getText().toString(), "1");
                        ((AddorRemoveCallbacks)context).onAddProduct();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Heyy..")
                                .setMessage("To add this item in your cart you have to login first. Do you want to login ")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent=new Intent(context,LoginActivity.class);
                                        context.startActivity(intent);
                                    }
                                })
                                .setNegativeButton("No Just Continue ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setCancelable(false);
                        builder.show();
                    }

                }
            });

        }


        private void strikeThroughText(TextView price) {
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


    }

}