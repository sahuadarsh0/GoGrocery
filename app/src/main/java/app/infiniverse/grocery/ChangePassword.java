package app.infiniverse.grocery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class ChangePassword extends AppCompatActivity {

    EditText tvOldPsw,tvNewPsw,tvConfPsw;
    Button bChange;
    public static final String PREFS = "PREFS";
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        tvOldPsw=findViewById(R.id.otp);
        tvNewPsw=findViewById(R.id.new_psw);
        tvConfPsw=findViewById(R.id.conf_psw);

        bChange=findViewById(R.id.change);

        sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);


//        Toast.makeText(this, sp.getString("loginid",null), Toast.LENGTH_SHORT).show();


        bChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvNewPsw.getText().toString().equals(tvConfPsw.getText().toString())){




                    class ChangePasswordAsync extends AsyncTask<String, Void, String> {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                        }
                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            Toast.makeText(ChangePassword.this, s, Toast.LENGTH_SHORT).show();
                            if(s.equals("Password Changed Successfully"))
                            {
                                tvOldPsw.setText("");
                                tvNewPsw.setText("");
                                tvConfPsw.setText("");
                                finish();

                            }


                        }
                        @Override
                        protected String doInBackground(String... params) {

                            String urls = getResources().getString(R.string.base_url).concat("changePassword/");
                            try {
                                URL url = new URL(urls);
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setDoInput(true);
                                httpURLConnection.setDoOutput(true);
                                OutputStream outputStream = httpURLConnection.getOutputStream();
                                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                                String post_Data = URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                                        URLEncoder.encode("new_password", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8");

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
                    ChangePasswordAsync changePasswordAsync = new ChangePasswordAsync();
                    changePasswordAsync.execute(sp.getString("loginid",null),tvOldPsw.getText().toString(),tvNewPsw.getText().toString());
                }else{
                    Toast.makeText(ChangePassword.this, "Password Did Not Matched", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
