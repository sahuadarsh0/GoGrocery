package app.infiniverse.grocery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS = "PREFS";
    EditText etLogin_id, etPassword;
    Button btnLogin;
    String login_id, password;
    TextView signup, forget;
    SharedPreferences sp;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        edit = sp.edit();

        signup = findViewById(R.id.signup);
        etLogin_id = findViewById(R.id.loginid);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        forget = findViewById(R.id.forget);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ForgetActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void login() {
        login_id = etLogin_id.getText().toString();
        password = etPassword.getText().toString();
        if ((!login_id.equals("")) && (!password.equals(""))) {
            class LoginUser extends AsyncTask<String, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (s.trim().equals("INLOGIN")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Login Status")
                                .setMessage("Invalid Login");
                        builder.show();
                    } else {

                        try {
                            JSONArray clientDetailArray = new JSONArray(s);
                            JSONObject json_data = new JSONObject();
                            json_data = clientDetailArray.getJSONObject(0);


                            edit.putString("loginid", json_data.getString("email"));
                            edit.putString("name", json_data.getString("name"));
                            edit.putString("mobile", json_data.getString("mobile"));
                            edit.putString("city", json_data.getString("city"));
                            edit.putString("locality", json_data.getString("locality"));
                            edit.putString("address", json_data.getString("address"));
                            edit.putString("c_dt", json_data.getString("c_dt"));
                            edit.apply();
                            Intent i = new Intent(LoginActivity.this, StartActivity.class);
                            startActivity(i);
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Invalid Login"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                protected String doInBackground(String... params) {

                    String urls = getResources().getString(R.string.base_url).concat("login_user/");
                    try {
                        URL url = new URL(urls);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setDoOutput(true);
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        String post_Data = URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                                URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

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
            LoginUser loginUsrObj = new LoginUser();
            loginUsrObj.execute(login_id, password);
        } else {
            Toast.makeText(this, "All Fields Are Mandatory", Toast.LENGTH_SHORT).show();
        }
    }
}