package app.infiniverse.grocery;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ValidateOTP extends AppCompatActivity {

    EditText etOtp;
    Button btnvalidate;
    String eotp,name,email,mobile,city_id,locality_id,address,password,otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_otp);

        etOtp=findViewById(R.id.otp);
        btnvalidate=findViewById(R.id.submit_otp);

        Bundle bundle=getIntent().getExtras();

        name=bundle.getString("name");
        email=bundle.getString("email");
        mobile=bundle.getString("mobile");
        city_id=bundle.getString("city_id");
        locality_id=bundle.getString("locality_id");
        address=bundle.getString("address");
        password=bundle.getString("password");
        otp=bundle.getString("otp");




        btnvalidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eotp=etOtp.getText().toString();
                if(otp.equals(eotp)){
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(mobile, null, "OTP to is : "+otp+" to register with Raju Kirana Store", null, null);

                        RegisterUser registerObj = new RegisterUser();
                        registerObj.execute(name, email, mobile, city_id, locality_id, address, String.valueOf(otp),password);

                    } catch (Exception e) {
                        Toast.makeText(ValidateOTP.this, "MSG Can not sent Check Your Balance", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(ValidateOTP.this, "Invlaid Otp", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }


    class RegisterUser extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.trim().equals("REGISTERED")){
                Toast.makeText(ValidateOTP.this, "User Registration Successful. Now please login", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(ValidateOTP.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(ValidateOTP.this, s, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String urls = getResources().getString(R.string.base_url).concat("register_user/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        URLEncoder.encode("city", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                        URLEncoder.encode("locality", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&" +
                        URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&" +
                        URLEncoder.encode("otp", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8");

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
}
