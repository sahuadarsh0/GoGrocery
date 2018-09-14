package app.infiniverse.grocery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

public class ChangeClientDetail extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String name, mobile, city_id, locality_id, address, password;
    Spinner city, locality;
    EditText etName, etMobile, etEmail, etAddress, etPassword;
    Button register;
    List<String> city_ids = new ArrayList<String>();
    List<String> locality_ids = new ArrayList<String>();
    public static final String PREFS="PREFS";
    SharedPreferences sp;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_client_detail);


        sp=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        edit=sp.edit();

        city = findViewById(R.id.city);
        locality = findViewById(R.id.locality);
        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etMobile = findViewById(R.id.mobile);
        etAddress = findViewById(R.id.address);
        etPassword = findViewById(R.id.password);
        register = findViewById(R.id.register);



        city.setOnItemSelectedListener(this);
        locality.setOnItemSelectedListener(this);

        class LoadCitySpinner extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                city_ids.clear();
                List<String> city_list = new ArrayList<String>();
                try {
                    JSONArray jArray = new JSONArray(s);
                    JSONObject json_data = new JSONObject();
                    for (int i = 0; i < jArray.length(); i++) {
                        json_data = jArray.getJSONObject(i);
                        city_list.add(json_data.getString("city"));
                        city_ids.add(json_data.getString("id"));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChangeClientDetail.this, android.R.layout.simple_spinner_item, city_list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    city.setAdapter(adapter);
                } catch (Exception e) {
                    Toast.makeText(ChangeClientDetail.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String urls = getResources().getString(R.string.base_url).concat("cities");
                    URL url = new URL(urls);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }
        LoadCitySpinner citySpinnerObj = new LoadCitySpinner();
        citySpinnerObj.execute();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDetail();
            }
        });


        getUserDetails();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getId() == R.id.city) {

            final String cid = city_ids.get(i);
            city_id = cid;
            locality_id = "";
            class LoadLocalitySpinner extends AsyncTask<Void, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    locality_ids.clear();
                    List<String> locality_list = new ArrayList<String>();
                    try {
                        JSONArray jArray = new JSONArray(s);
                        JSONObject json_data = new JSONObject();
                        for (int i = 0; i < jArray.length(); i++) {
                            json_data = jArray.getJSONObject(i);
                            locality_list.add(json_data.getString("locality"));
                            locality_ids.add(json_data.getString("id"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChangeClientDetail.this, android.R.layout.simple_spinner_item, locality_list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        locality.setAdapter(adapter);
                    } catch (Exception e) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(ChangeClientDetail.this);
                        builder.show();
                    }
                }

                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        String urls = getResources().getString(R.string.base_url).concat("localities/").concat(cid);
                        //creating a URL
                        URL url = new URL(urls);

                        //Opening the URL using HttpURLConnection
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        //StringBuilder object to read the string from the service
                        StringBuilder sb = new StringBuilder();

                        //We will use a buffered reader to read the string from service
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                        //A simple string to read values from each line
                        String json;

                        //reading until we don't find null
                        while ((json = bufferedReader.readLine()) != null) {

                            //appending it to string builder
                            sb.append(json + "\n");
                        }

                        //finally returning the read string
                        return sb.toString().trim();
                    } catch (Exception e) {
                        return null;
                    }

                }
            }

            //creating asynctask object and executing it
            LoadLocalitySpinner localitySpinnerObj = new LoadLocalitySpinner();
            localitySpinnerObj.execute();
        } else if (adapterView.getId() == R.id.locality) {
            final String lid = locality_ids.get(i);
            locality_id = lid;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void updateDetail() {
        name = etName.getText().toString();
        mobile = etMobile.getText().toString();
        address = etAddress.getText().toString();
        password = etPassword.getText().toString();

        if ((!name.equals("")) && (!mobile.equals("")) && (!address.equals("")) && (!city_id.equals("")) && (!locality_id.equals(""))) {
            class RegisterUser extends AsyncTask<String, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if(s.trim().equals("DUS")){
//                        Toast.makeText(ChangeClientDetail.this, "Detail Updated Successfully ", Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeClientDetail.this);
                        builder.setTitle("Successful")
                                .setMessage("Detail Updated Successfully")
                                .setIcon(R.drawable.ic_check_black)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        Intent ii = new Intent(ChangeClientDetail.this, HomeActivity.class);
                                        startActivity(ii);
                                        finish();
                                    }
                                });
                        builder.show();
                        edit.putString("name", name);
                        edit.putString("mobile", mobile);
                        edit.putString("city", address);
                        edit.apply();
                    }else {
                        Toast.makeText(ChangeClientDetail.this, s, Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                protected String doInBackground(String... params) {

                    String urls = getResources().getString(R.string.base_url).concat("changeProfileDetails/");
                    try {
                        URL url = new URL(urls);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setDoOutput(true);
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        String post_Data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                                URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                                URLEncoder.encode("city", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                                URLEncoder.encode("locality", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                                URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&" +
                                URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&" +
                                URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8");

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
            RegisterUser registerObj = new RegisterUser();
            registerObj.execute(name, mobile, city_id, locality_id, address,sp.getString("loginid",null) ,password);
        } else {
            Toast.makeText(this, "All Fields Are Mandatory", Toast.LENGTH_SHORT).show();
        }
    }

    public void getUserDetails() {
        class FeedUserDetails extends AsyncTask<String, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    try {


                        JSONArray clientDetailArray = new JSONArray(s);
                        JSONObject json_data = new JSONObject();
                        json_data = clientDetailArray.getJSONObject(0);

                        etName.setText(json_data.getString("name"));
                        etMobile.setText(json_data.getString("mobile"));
                        etAddress.setText(json_data.getString("address"));


                    } catch (JSONException e) {
                        Toast.makeText(ChangeClientDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                protected String doInBackground(String... params) {

                    String urls = getResources().getString(R.string.base_url).concat("getClientDetails/");
                    try {
                        URL url = new URL(urls);
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
            }

            //creating asynctask object and executing it
            FeedUserDetails feedUserDetails = new FeedUserDetails();
            feedUserDetails.execute(sp.getString("loginid",null));

    }
}
