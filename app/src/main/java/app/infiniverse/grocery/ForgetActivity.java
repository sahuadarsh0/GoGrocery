package app.infiniverse.grocery;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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

public class ForgetActivity extends AppCompatActivity {

    EditText email;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        email = findViewById(R.id.email);
        submit = findViewById(R.id.submit_otp);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class EmailVerify extends AsyncTask<String, Void, String> {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (s.trim().equals("SENT")) {


                            Intent i = new Intent(ForgetActivity.this, OtpForgetActivity.class);
                            i.putExtra("email",email.getText().toString());
                            startActivity(i);
                            Toast.makeText(ForgetActivity.this, "OTP sent successfully, check your Email", Toast.LENGTH_SHORT).show();
                            finish();

                        } else
                            Toast.makeText(ForgetActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();

                    }

                    //
                    @Override
                    protected String doInBackground(String... params) {

                        String urls = getResources().getString(R.string.base_url).concat("generateOtpToResetPassword/");
                        try {
                            URL url = new URL(urls);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.setDoOutput(true);
                            OutputStream outputStream = httpURLConnection.getOutputStream();
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            String post_Data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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
                EmailVerify loginUsrObj = new EmailVerify();
                loginUsrObj.execute(email.getText().toString());
            }
        });

    }
}
