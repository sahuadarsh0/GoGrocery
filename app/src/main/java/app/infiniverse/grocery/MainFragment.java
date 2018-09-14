package app.infiniverse.grocery;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    TextView tvName, tvEmail, tvMob, tvAddress;
    Button btnChangePSW, btnChangeDetails;
    Context context;
    public static final String PREFS = "PREFS";
    SharedPreferences sp;
    public MainFragment(Context context) {
        // Required empty public constructor
        this.context=context;
        sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ImageView back = view.findViewById(R.id.back);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvMob = view.findViewById(R.id.tvMob);
        tvAddress = view.findViewById(R.id.tvAddress);
        btnChangePSW = view.findViewById(R.id.btnChangePSW);
        btnChangeDetails = view.findViewById(R.id.btnChangeDetails);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        btnChangePSW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,ChangePassword.class);
                startActivity(i);
            }
        });

        btnChangeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,ChangeClientDetail.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        View photoHeader = view.findViewById(R.id.photoHeader);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        /* For devices equal or higher than lollipop set the translation above everything else */
            photoHeader.setTranslationZ(6);
        /* Redraw the view to show the translation */
            photoHeader.invalidate();
        }

        ProductDetail productDetail = new ProductDetail();
        productDetail.execute(sp.getString("loginid",null));

        return view;
    }

    class ProductDetail extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String clientDetailUrl = getResources().getString(R.string.base_url) + "getClientDetails/";


            try {
                URL url = new URL(clientDetailUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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

            try {


                JSONArray clientDetailArray = new JSONArray(s);
                JSONObject json_data = new JSONObject();
                json_data = clientDetailArray.getJSONObject(0);

                    tvName.setText(json_data.getString("name"));
                    tvEmail.setText(json_data.getString("email"));
                    tvMob.setText(json_data.getString("mobile"));
                    tvAddress.setText(json_data.getString("address"));


            } catch (JSONException e) {
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


    }


}
