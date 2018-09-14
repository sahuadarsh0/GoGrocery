package app.infiniverse.grocery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Created by root on 24/3/18.
 */

public class Cart_Item_Adapter extends RecyclerView.Adapter<Cart_Item_Adapter.ProductsViewHolder> {

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
    private String[] product_qty;
    private TextView total_saving;
    private TextView total_pamt;

    public Cart_Item_Adapter(String[] product_id, String[] product_name, String[] product_desc, String[] product_img, String[] product_price, String[] product_brand, String[] product_sp, String[] product_dp, String[] product_qty,TextView total_saving,TextView total_pamt, Context context) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.product_img = product_img;
        this.product_price = product_price;
        this.product_brand = product_brand;
        this.product_sp = product_sp;
        this.product_dp = product_dp;
        this.product_qty = product_qty;
        this.context = context;
        this.total_saving=total_saving;
        this.total_pamt=total_pamt;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_cart, parent, false);
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
        String qty = product_qty[position];

        holder.pro_id.setText(id);
        holder.pro_name.setText(name);
        holder.pro_desc.setText(desc);
        holder.pro_price.setText(price);
        holder.pro_sp.setText(selling_price);
        holder.pro_brand.setText(brand);
        holder.pro_discount.setText(discount + " %   OFF");
        holder.pro_qty.setText(qty);

        if (Integer.parseInt(discount) <= 0) {
            holder.pro_discount.setVisibility(View.GONE);
        }
        if(selling_price.trim().equals(price.trim())){
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
        TextView pro_qty;
        ImageView pro_del;
        ImageView pro_img;
        ImageView add, remove;

        public ProductsViewHolder(final View itemView) {
            super(itemView);
            sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

            pro_id = itemView.findViewById(R.id.product_id);
            pro_name = itemView.findViewById(R.id.product_name);
            pro_desc = itemView.findViewById(R.id.product_short_desc);
            pro_img = itemView.findViewById(R.id.product_img);
            pro_price = itemView.findViewById(R.id.product_price);
            pro_sp = itemView.findViewById(R.id.selling_price);
            pro_brand = itemView.findViewById(R.id.brand_name);
            pro_discount = itemView.findViewById(R.id.discount);
            pro_del = itemView.findViewById(R.id.product_del);
            pro_qty = itemView.findViewById(R.id.product_qty);
            add = itemView.findViewById(R.id.add);
            remove = itemView.findViewById(R.id.remove);

            strikeThroughText(pro_price);

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
//                    Product detail = new Product();
//                    detail.startProductDetailActivity(pro_id.getText().toString(), context);

                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    class IncreaseProductQuantity extends AsyncTask<String, Void, String> {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);

//                            Intent intent = ((Activity) context).getIntent();
//                            ((Activity) context).finish();
//                            context.startActivity(intent);

                            int qtyi = Integer.parseInt(pro_qty.getText().toString());
                            qtyi++;
                            pro_qty.setText(Integer.toString(qtyi));

                            double gsp=Double.parseDouble(pro_sp.getText().toString().substring(2).trim());
                            double gmrp=Double.parseDouble(pro_price.getText().toString().substring(2).trim());

                            double profit=gmrp-gsp;

                            double old_samt=Double.parseDouble(total_saving.getText().toString().substring(1).trim());
                            double new_samt=old_samt+profit;
                            total_saving.setText("\u20B9"+new_samt);

                            double old_pamt=Double.parseDouble(total_pamt.getText().toString().substring(1).trim());
                            double new_pamt=old_pamt+gsp;
                            total_pamt.setText("\u20B9"+new_pamt);
                        }

                        @Override
                        protected String doInBackground(String... params) {

                            String urls = context.getResources().getString(R.string.base_url).concat("increaseProductQuantity/");
                            try {
                                URL url = new URL(urls);
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setDoInput(true);
                                httpURLConnection.setDoOutput(true);
                                OutputStream outputStream = httpURLConnection.getOutputStream();
                                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                                String post_Data = URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                                        URLEncoder.encode("product_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

                                bufferedWriter.write(post_Data);
                                bufferedWriter.flush();
                                bufferedWriter.close();
                                outputStream.close();
                                InputStream inputStream = httpURLConnection.getInputStream();
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                                String result = "", line = "";
                                while ((line = bufferedReader.readLine()) != null) {
                                    result += line;
                                }
                                return result;
                            } catch (Exception e) {
                                return e.toString();
                            }
                        }
                    }

                    //creating asynctask object and executing it
                    IncreaseProductQuantity ipqOBJ = new IncreaseProductQuantity();
                    ipqOBJ.execute(sp.getString("loginid", null), pro_id.getText().toString());


                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int qtyi = Integer.parseInt(pro_qty.getText().toString());

                    if (qtyi != 1) {

                        class DecreaseProductQuantity extends AsyncTask<String, Void, String> {

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                int qtyi = Integer.parseInt(pro_qty.getText().toString());
                                if (qtyi != 1) {
                                    qtyi--;
                                    pro_qty.setText(Integer.toString(qtyi));
                                }

                                double gsp=Double.parseDouble(pro_sp.getText().toString().substring(2).trim());
                                double gmrp=Double.parseDouble(pro_price.getText().toString().substring(2).trim());

                                double profit=gmrp-gsp;

                                double old_samt=Double.parseDouble(total_saving.getText().toString().substring(1).trim());
                                double new_samt=old_samt-profit;
                                total_saving.setText("\u20B9"+new_samt);

                                double old_pamt=Double.parseDouble(total_pamt.getText().toString().substring(1).trim());
                                double new_pamt=old_pamt-gsp;
                                total_pamt.setText("\u20B9"+new_pamt);

                            }

                            @Override
                            protected String doInBackground(String... params) {

                                String urls = context.getResources().getString(R.string.base_url).concat("decreaseProductQuantity/");
                                try {
                                    URL url = new URL(urls);
                                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                    httpURLConnection.setRequestMethod("POST");
                                    httpURLConnection.setDoInput(true);
                                    httpURLConnection.setDoOutput(true);
                                    OutputStream outputStream = httpURLConnection.getOutputStream();
                                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                                    String post_Data = URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                                            URLEncoder.encode("product_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

                                    bufferedWriter.write(post_Data);
                                    bufferedWriter.flush();
                                    bufferedWriter.close();
                                    outputStream.close();
                                    InputStream inputStream = httpURLConnection.getInputStream();
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                                    String result = "", line = "";
                                    while ((line = bufferedReader.readLine()) != null) {
                                        result += line;
                                    }
                                    return result;
                                } catch (Exception e) {
                                    return e.toString();
                                }
                            }
                        }

                        //creating asynctask object and executing it
                        DecreaseProductQuantity dpqOBJ = new DecreaseProductQuantity();
                        dpqOBJ.execute(sp.getString("loginid", null), pro_id.getText().toString());
                    }

                }
            });

            pro_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();


                    class DeleteProduct extends AsyncTask<String, Void, String> {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);

                            Intent intent = ((Activity) context).getIntent();
                            ((Activity) context).finish();
                            context.startActivity(intent);

                        }

                        @Override
                        protected String doInBackground(String... params) {

                            String urls = context.getResources().getString(R.string.base_url).concat("deleteCartItem/");
                            try {
                                URL url = new URL(urls);
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setDoInput(true);
                                httpURLConnection.setDoOutput(true);
                                OutputStream outputStream = httpURLConnection.getOutputStream();
                                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                                String post_Data = URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                                        URLEncoder.encode("product_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

                                bufferedWriter.write(post_Data);
                                bufferedWriter.flush();
                                bufferedWriter.close();
                                outputStream.close();
                                InputStream inputStream = httpURLConnection.getInputStream();
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                                String result = "", line = "";
                                while ((line = bufferedReader.readLine()) != null) {
                                    result += line;
                                }
                                return result;
                            } catch (Exception e) {
                                return e.toString();
                            }
                        }
                    }

                    DeleteProduct dpOBJ = new DeleteProduct();
                    dpOBJ.execute(sp.getString("loginid", null), pro_id.getText().toString());


                }
            });


        }


        private void strikeThroughText(TextView price) {
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


    }

}