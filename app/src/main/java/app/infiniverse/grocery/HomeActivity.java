package app.infiniverse.grocery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements BaseSliderView.OnSliderClickListener,AddorRemoveCallbacks {

    SliderLayout sliderShow;
    private static int cart_count=0;
    HashMap<String, String> url_maps = new HashMap<>();

        private GridView mGridView;
    private ProgressBar mProgressBar;
    List<String> bsp_id_list = new ArrayList<String>();
    private Bsp_Grid mGridAdapter;
    private ArrayList<GridItem> mGridData;
    public static final String PREFS = "PREFS";
    SharedPreferences sp;
    LinearLayout l2;
    SharedPreferences.Editor editor;

    private Drawer result = null;
    private AccountHeader headerResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        editor = sp.edit();
        l2 = findViewById(R.id.ll_best_selling);
        mProgressBar =findViewById(R.id.progressBar);


        handleIntent(getIntent());
        final IProfile profile;
        if (sp.getString("loginid", null) == null) {
            profile = new ProfileDrawerItem().withName("RKS").withEmail("profile@rks.com").withIcon(R.drawable.icon).withTag("RKS");

        } else {
            profile = new ProfileDrawerItem().withName(sp.getString("name", null)).withEmail(sp.getString("mobile", null)).withIcon(R.drawable.icon).withTag("CUSTOMER");

        }

//        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(0).withName("Adarsh");

        final Intent i = new Intent(this, ProfileActivity.class);
        final Intent r = new Intent(this, RegisterActivity.class);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.profile_bg)
                .addProfiles(
                        profile,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("Register Now").withDescription("Add new RKS Account").withTag("REGISTER")

                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {


                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getTag().equals("CUSTOMER")) {
//                            headerResult.removeProfile(profile);
                            startActivity(i);
                        } else if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getTag().equals("REGISTER")) {
                            startActivity(r);

                        }
                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();


        new DrawerBuilder().withActivity(this).build();


        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            if (drawerItem.getTag().toString().equals("LOGIN")) {
                                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();

                            } else if (drawerItem.getTag().toString().equals("ORDER_HISTORY")) {
                                Intent i = new Intent(HomeActivity.this, OrderActivity.class);
                                startActivity(i);

                            } else if (drawerItem.getTag().toString().equals("MY_CART")) {
                                Intent i = new Intent(HomeActivity.this, MyCart.class);
                                startActivity(i);

                            } else if (drawerItem.getTag().toString().equals("LOG_OUT")) {
                                cart_count=0;
                                invalidateOptionsMenu();
                                editor.clear().apply();
                                Intent i = new Intent(HomeActivity.this, StartActivity.class);
                                startActivity(i);
                                finish();

                            } else if (drawerItem.getTag().toString().equals("CATEGORIES")) {

                            } else if (drawerItem.getTag().toString().equals("SUB_CATEGORIES")) {
                                Intent intent = new Intent(HomeActivity.this, Category_wise_products.class);
                                intent.putExtra("sub_cat_id", String.valueOf(drawerItem.getIdentifier()));
                                intent.putExtra("cart_count",""+cart_count);
                                intent.putExtra("sub_category", ((Nameable)drawerItem).getName().toString());
                                startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
//                .withShowDrawerUntilDraggedOpened(true)
                .build();


        if (sp.getString("loginid", null) != null) {
            PrimaryDrawerItem order_history = new PrimaryDrawerItem().withName("Order History").withIcon(R.drawable.ic_history_black).withTag("ORDER_HISTORY");
            PrimaryDrawerItem my_cart = new PrimaryDrawerItem().withName("My Cart").withIcon(R.drawable.ic_shopping_cart_black).withTag("MY_CART");
            result.addItem(order_history);
            result.addItem(my_cart);
            result.addStickyFooterItem(new PrimaryDrawerItem().withName("Log Out").withIcon(R.drawable.ic_log_out).withTag("LOG_OUT"));




        } else {
            result.addStickyFooterItem(new PrimaryDrawerItem().withName("Log In").withIcon(R.drawable.ic_person_black).withTag("LOGIN"));
        }



        result.addItem(new DividerDrawerItem());
        result.addItem(new SecondaryDrawerItem().withName("Shop By Category").withTag("CATEGORY_LABEL").withSelectable(false).withSetSelected(false).withTextColor(getResources().getColor(R.color.material)));
        result.addItem(new DividerDrawerItem());

        class Categories extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String productUrl = getResources().getString(R.string.base_url) + "getCategoryAndSubCategory/";

                try {
                    URL url = new URL(productUrl);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Received Message");
                try {

                    JSONObject json_data = new JSONObject(s);
                    Iterator<String> temp = json_data.keys();
                    while (temp.hasNext()) {
                        String key = temp.next();
                        JSONArray sub_cat = json_data.getJSONArray(key);
                        ExpandableDrawerItem item = new ExpandableDrawerItem().withName(key.replace("&amp;","&")).withIcon(R.drawable.ic_filter_list_black).withIdentifier(0).withSelectable(false).withTag("CATEGORIES");
                        JSONObject sub_cat_json_data = new JSONObject();
                        for (int i = 0; i < sub_cat.length(); i++) {
                            sub_cat_json_data = sub_cat.getJSONObject(i);
//                            product_ids[i] = json_data.getString("id");
                            item.withSubItems(new SecondaryDrawerItem().withLevel(2).withName(sub_cat_json_data.getString("sub_category").replace("&amp;","&")).withIcon(R.drawable.ic_minus_black).withIdentifier(Integer.parseInt(sub_cat_json_data.getString("id"))).withTag("SUB_CATEGORIES"));


                        }
                        result.addItem(item);
                    }

//                    Toast.makeText(HomeActivity.this, ""+json_data.length(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    builder.setCancelable(true);
                    builder.setTitle("No Internet Connection");
//                    builder.setMessage(e.toString());
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
        Categories categories = new Categories();
        categories.execute();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);


        @SuppressLint("StaticFieldLeak")
        class LoadSliderImages extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONArray jArray = new JSONArray(s);
                    JSONObject json_data = new JSONObject();
                    for (int i = 0; i < jArray.length(); i++) {
                        json_data = jArray.getJSONObject(i);
                        String str = getResources().getString(R.string.img_base_url) + "slider_images/" + json_data.getString("image");
                        url_maps.put("", str);
                        sliderShow = findViewById(R.id.slider);
                        for (String name : url_maps.keySet()) {
                            DefaultSliderView defaultSliderView = new DefaultSliderView(HomeActivity.this);
                            // initialize a SliderLayout
                            defaultSliderView
                                    .image(url_maps.get(name))
                                    .setOnSliderClickListener(HomeActivity.this);


                            defaultSliderView.bundle(new Bundle());
                            defaultSliderView.getBundle()
                                    .putString("extra",json_data.getString("product_id"));


                            sliderShow.addSlider(defaultSliderView);
                        }
                        sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                        sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
                    }

                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String urls = getResources().getString(R.string.base_url).concat("slider_images");
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
        LoadSliderImages sliderImgLoaderObj = new LoadSliderImages();
        sliderImgLoaderObj.execute();



        /*  Grid View Best Selling Product  */

        mGridView = findViewById(R.id.gridView);

        mProgressBar = findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new Bsp_Grid(this, R.layout.bsp_grid_single, mGridData);
        mGridView.setAdapter(mGridAdapter);

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Product detail = new Product();
                detail.startProductDetailActivity(bsp_id_list.get(position), HomeActivity.this);
//                Toast.makeText(HomeActivity.this, bsp_id_list.get(position), Toast.LENGTH_SHORT).show();
//        Get item at position
//                GridItem item = (GridItem) parent.getItemAtPosition(position);
//
//                Intent intent = new Intent(GridViewActivity.this, DetailsActivity.class);
//                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);
//
//                // Interesting data to pass across are the thumbnail size/location, the
//                // resourceId of the source bitmap, the picture description, and the
//                // orientation (to avoid returning back to an obsolete configuration if
//                // the device rotates again in the meantime)
//
//                int[] screenLocation = new int[2];
//                imageView.getLocationOnScreen(screenLocation);
//
//                //Pass the image title and url to DetailsActivity
//                intent.putExtra("left", screenLocation[0]).
//                        putExtra("top", screenLocation[1]).
//                        putExtra("width", imageView.getWidth()).
//                        putExtra("height", imageView.getHeight()).
//                        putExtra("title", item.getTitle()).
//                        putExtra("image", item.getImage());
//
//                //Start details activity
////                startActivity(intent);
            }
        });

        //Start download
        new LoadGridImages().execute();

        mProgressBar.setVisibility(View.VISIBLE);
        class BestDeals extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String productUrl = getResources().getString(R.string.base_url) + "getBestSellingProducts/";

                try {
                    URL url = new URL(productUrl);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Received Message");

                try {

                    JSONArray productArray = new JSONArray(s);

                    String[] product_ids = new String[productArray.length()];
                    String[] product_names = new String[productArray.length()];
                    String[] product_descs = new String[productArray.length()];
                    String[] product_imgs = new String[productArray.length()];
                    String[] product_prices = new String[productArray.length()];
                    String[] product_brands = new String[productArray.length()];
                    String[] product_sps = new String[productArray.length()];
                    String[] product_dps = new String[productArray.length()];


                    JSONObject json_data = new JSONObject();
                    for (int i = 0; i < productArray.length(); i++) {
                        json_data = productArray.getJSONObject(i);
                        product_ids[i] = json_data.getString("id");
                        product_names[i] = json_data.getString("name");
                        product_descs[i] = json_data.getString("description");
                        product_imgs[i] = json_data.getString("image");
                        product_prices[i] = json_data.getString("mrp") + " /-";
                        product_brands[i] = json_data.getString("brand");
                        product_sps[i] = "\u20B9" + json_data.getString("selling_price") + " /-";
                        double p_mrp = Double.parseDouble(json_data.getString("mrp"));
                        double p_sp = Double.parseDouble(json_data.getString("selling_price"));
                        double p_dp = (p_mrp - p_sp) / (p_mrp / 100);
                        int p_dp_i = (int) p_dp;
                        product_dps[i] = String.valueOf(p_dp_i);


                    }

                    l2.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);

                    RecyclerView product_recyclerview = findViewById(R.id.recyclerview_best_deals);
                    product_recyclerview.setNestedScrollingEnabled(false);
                    product_recyclerview.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                    product_recyclerview.setAdapter(new Recent_Products_Adapter(product_ids, product_names, product_descs, product_imgs, product_prices, product_brands, product_sps, product_dps, HomeActivity.this));
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
        BestDeals products = new BestDeals();
        products.execute();

//
//        class RecentProducts extends AsyncTask<String, Void, String> {
//
//            @Override
//            protected String doInBackground(String... params) {
//                String productUrl = getResources().getString(R.string.base_url) + "getRecentProducts/";
//
//                try {
//                    URL url = new URL(productUrl);
//
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    httpURLConnection.setRequestMethod("POST");
//                    httpURLConnection.setDoInput(true);
//                    httpURLConnection.setDoOutput(true);
//
//                    InputStream inputStream = httpURLConnection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//                    String result = "", line = "";
//                    while ((line = bufferedReader.readLine()) != null) {
//                        result += line;
//                    }
//                    return result;
//                } catch (Exception e) {
//                    return e.toString();
//                }
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
//                builder.setTitle("Received Message");
//
//                try {
//
//                    JSONArray productArray = new JSONArray(s);
//
//                    String[] product_ids = new String[productArray.length()];
//                    String[] product_names = new String[productArray.length()];
//                    String[] product_descs = new String[productArray.length()];
//                    String[] product_imgs = new String[productArray.length()];
//                    String[] product_prices = new String[productArray.length()];
//                    String[] product_brands = new String[productArray.length()];
//                    String[] product_sps = new String[productArray.length()];
//                    String[] product_dps = new String[productArray.length()];
//
//
//                    JSONObject json_data = new JSONObject();
//                    for (int i = 0; i < productArray.length(); i++) {
//                        json_data = productArray.getJSONObject(i);
//                        product_ids[i] = json_data.getString("id");
//                        product_names[i] = json_data.getString("name");
//                        product_descs[i] = json_data.getString("description");
//                        product_imgs[i] = json_data.getString("image");
//                        product_prices[i] = json_data.getString("mrp") + " /-";
//                        product_brands[i] = json_data.getString("brand");
//                        product_sps[i] = "\u20B9" + json_data.getString("selling_price") + " /-";
//                        double p_mrp = Double.parseDouble(json_data.getString("mrp"));
//                        double p_sp = Double.parseDouble(json_data.getString("selling_price"));
//                        double p_dp = (p_mrp - p_sp) / (p_mrp / 100);
//                        int p_dp_i = (int) p_dp;
//                        product_dps[i] = String.valueOf(p_dp_i);
//
//
//                    }
//                    RecyclerView product_recyclerview = findViewById(R.id.recyclerview_recent_products);
//                    product_recyclerview.setNestedScrollingEnabled(false);
//                    product_recyclerview.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
//                    product_recyclerview.setAdapter(new Recent_Products_Adapter(product_ids, product_names, product_descs, product_imgs, product_prices, product_brands, product_sps, product_dps, HomeActivity.this));
//                } catch (JSONException e) {
//                    builder.setCancelable(true);
//                    builder.setTitle("No Internet Connection");
//                    builder.setMessage(s);
//                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                        }
//                    });
//                    builder.show();
//                }
//
//            }
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//
//        }
//        RecentProducts recentProducts = new RecentProducts();
//        recentProducts.execute();


    }




    class LoadGridImages extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//                Toast.makeText(HomeActivity.this, s, Toast.LENGTH_SHORT).show();
            try {
                JSONArray jArray = new JSONArray(s);
                JSONObject json_data = new JSONObject();
                GridItem item;

                for (int i = 0; i < jArray.length(); i++) {

                    json_data = jArray.getJSONObject(i);
                    String title="Nothing";
                    if(json_data.getString("name").length()>20) {
                        title = json_data.getString("name").substring(0, 19);
                    }else{
                        title = json_data.getString("name");
                    }
                    item = new GridItem();
                    item.setTitle(title);

                    item.setImage(getResources().getString(R.string.img_base_url) + "product_images/" + json_data.getString("image"));
                    bsp_id_list.add(json_data.getString("id"));
                    mGridData.add(item);
                }

                mGridAdapter.setGridData(mGridData);
                mProgressBar.setVisibility(View.GONE);

            } catch (Exception e) {
                Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }

        //in this method we are fetching the json string
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String urls = getResources().getString(R.string.base_url).concat("getRecentProducts");
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


    @Override
    protected void onPostResume() {
        super.onPostResume();
        invalidateOptionsMenu();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {


        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        final MenuItem menuItem = menu.findItem(R.id.cart);
        menuItem.setIcon(Converter.convertLayoutToImage(HomeActivity.this,cart_count,R.drawable.ic_shopping_cart_white));


        if(sp.getString("loginid",null)!=null){
            class GetCartItemCount extends AsyncTask<String, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    cart_count = Integer.parseInt(s);
                    menuItem.setIcon(Converter.convertLayoutToImage(HomeActivity.this,cart_count,R.drawable.ic_shopping_cart_white));
                }

                @Override
                protected String doInBackground(String... params) {

                    String urls = getResources().getString(R.string.base_url).concat("getItemCount/");
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
            GetCartItemCount catItemObj = new GetCartItemCount();
            catItemObj.execute(sp.getString("loginid",null));
        }

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.cart) {



            if(sp.getString("loginid",null)!=null) {
                Intent i = new Intent(this, MyCart.class);
                startActivity(i);
                return true;
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Heyy..")
                        .setMessage("To see your cart you have to login first. Do you want to login ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                                startActivity(intent);
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


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {

        String product_id=slider.getBundle().get("extra").toString();
        Product detail = new Product();
        detail.startProductDetailActivity(product_id, HomeActivity.this);


    }


    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Intent i = new Intent(this, SearchResultsActivity.class);
            i.putExtra("search_text", query);
            startActivity(i);
            //use the query to search your data somehow
        }
    }




    @Override
    public void onAddProduct() {
        cart_count++;
        invalidateOptionsMenu();
    }

    @Override
    public void onRemoveProduct() {
        cart_count--;
        invalidateOptionsMenu();
    }
}
