package app.infiniverse.grocery;

import android.content.Context;
import android.content.Intent;


/**
 * Created by root on 26/3/18.
 */

public class Product {
    void startProductDetailActivity(String product_id, Context context) {
        Intent productDetailIntent = new Intent(context, ProductDetailActivity.class);
        productDetailIntent.putExtra("p_id", product_id);
        context.startActivity(productDetailIntent);
    }
}
