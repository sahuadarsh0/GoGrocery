package app.infiniverse.grocery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class OrderDetailsActivity extends AppCompatActivity {
    public static final String PREFS = "PREFS";
    SharedPreferences sp;
    double savings = 0;
    double payable_amt = 0;
    TextView tvSavings, tvPayableAmt;
    LinearLayout l1, l2;
    private ProgressBar mProgressBar;
    public String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Details");


        sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String login_id = sp.getString("loginid", null);

        Bundle extras=getIntent().getExtras();
        order_id=extras.getString("order_id");

        tvSavings = findViewById(R.id.total_discount);
        tvPayableAmt = findViewById(R.id.total_amount);
        mProgressBar = findViewById(R.id.progressBar);
        l1 = findViewById(R.id.ll_item_products);
        l2 = findViewById(R.id.ll_item);

        mProgressBar.setVisibility(View.VISIBLE);




        class OrderItems extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String OrdersItemURL = getResources().getString(R.string.base_url) + "orderDetails/";
                try {
                    URL url = new URL(OrdersItemURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_Data = URLEncoder.encode("order_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
                builder.setTitle("Received Message");

                try {

                    JSONArray productArray = new JSONArray(s);
                    if (productArray.length() != 0) {


                        String[] product_ids = new String[productArray.length()];
                        String[] product_names = new String[productArray.length()];
                        String[] product_descs = new String[productArray.length()];
                        String[] product_imgs = new String[productArray.length()];
                        String[] product_prices = new String[productArray.length()];
                        String[] product_brands = new String[productArray.length()];
                        String[] product_sps = new String[productArray.length()];
                        String[] product_dps = new String[productArray.length()];
                        String[] product_qtys = new String[productArray.length()];


                        JSONObject json_data = new JSONObject();
                        for (int i = 0; i < productArray.length(); i++) {
                            json_data = productArray.getJSONObject(i);

                            product_ids[i] = json_data.getString("id");
                            product_names[i] = json_data.getString("name");
                            product_descs[i] = json_data.getString("description");
                            product_imgs[i] = json_data.getString("image");
                            product_prices[i] = " \u20B9 " + json_data.getString("mrp") + " ";
                            product_brands[i] = json_data.getString("brand");
                            product_sps[i] = " \u20B9 " + json_data.getString("selling_price") + " ";
                            double p_mrp = Double.parseDouble(json_data.getString("mrp"));
                            double p_sp = Double.parseDouble(json_data.getString("selling_price"));
                            double p_dp = (p_mrp - p_sp) / (p_mrp / 100);
                            int p_dp_i = (int) p_dp;
                            product_dps[i] = String.valueOf(p_dp_i);
                            product_qtys[i] = json_data.getString("qty");
                            int p_qty = Integer.parseInt(json_data.getString("qty"));
                            savings = savings + ((p_mrp - p_sp) * p_qty);
                            payable_amt = payable_amt + (p_sp * p_qty);


                        }
                        tvSavings.setText("\u20B9" + Double.toString(savings));
                        tvPayableAmt.setText("\u20B9" + Double.toString(payable_amt));

                        l1.setVisibility(View.VISIBLE);
                        l2.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);

                        RecyclerView order_detail_item_recyclerview = findViewById(R.id.recyclerview_item_orders);
                        order_detail_item_recyclerview.setLayoutManager(new LinearLayoutManager(OrderDetailsActivity.this));
                        order_detail_item_recyclerview.setAdapter(new Item_Order_Detail(product_ids, product_names, product_descs, product_imgs, product_prices, product_brands, product_sps, product_dps, product_qtys,  OrderDetailsActivity.this));
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    builder.setCancelable(true);
                    builder.setTitle("No Internet Connection");
//                    builder.setMessage(s);
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


        }
        OrderItems items = new OrderItems();
        items.execute(order_id);

    }

}
