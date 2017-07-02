package com.example.yuya.hellogridview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yuya on 7/1/2017.
 */
public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String bbcnews = "http://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=144dd64c47c541738c17bec4a2656295";
    private static String bloomberg  = "http://newsapi.org/v1/articles?source=bloomberg&sortBy=top&apiKey=144dd64c47c541738c17bec4a2656295";
    private static String nationalgeographic = "http://newsapi.org/v1/articles?source=national-geographic&sortBy=top&apiKey=144dd64c47c541738c17bec4a2656295";
    private static String financialtimes = "http://newsapi.org/v1/articles?source=financial-times&sortBy=latest&apiKey=144dd64c47c541738c17bec4a2656295";
    private static String cnn = "http://newsapi.org/v1/articles?source=cnn&sortBy=top&apiKey=144dd64c47c541738c17bec4a2656295";
    private static String url;
    private static String title;
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent data = getIntent();
        Bundle extras = data.getExtras();
        String selected_url = extras != null ? extras.getString("SELECTED_URL") : "";
        if(selected_url.equals("0")){
            url = bbcnews;
            title = "BBC News";
        } else if(selected_url.equals("1")){
            url = bloomberg;
            title = "Bloomberg";
        } else if(selected_url.equals("2")){
            url = nationalgeographic;
            title = "National Geographic";
        } else if(selected_url.equals("3")){
            url = financialtimes;
            title = "Financial Times";
        } else{
            url = cnn;
            title = "CNN";
        }
        setTitle(title);

        contactList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.listView);
        //lv.setOnItemClickListener((AdapterView.OnItemClickListener) new ListItemClickListener());
        lv.setOnItemClickListener(new ListItemClickListener());

        new GetContacts().execute();
    }

    public class ListItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listview = (ListView) parent;
            HashMap<String, String> item = (HashMap) listview.getItemAtPosition(position);
            //String item = "aaa";

            Intent intent = new Intent(MainActivity.this,SecondActivity.class);
            intent.putExtra("SELECTED_PICT",item.get("url"));
            startActivity(intent);
        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("articles");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("author");
                        String name = c.getString("title");
                        String email = c.getString("description");
                        String url = c.getString("url");
                        String gender = c.getString("urlToImage");
                        String mobile = c.getString("publishedAt");
                        if(mobile != "null"){
                            mobile = mobile.substring(0,10);
                        }else{
                            mobile = "";
                        }

                        // Phone node is JSON Object
                        //JSONObject phone = c.getJSONObject("phone");
                        //String mobile = phone.getString("mobile");
                        //String home = phone.getString("home");
                        //String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);
                        contact.put("url", url);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"name", "email",
                    "mobile"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});

            lv.setAdapter(adapter);
        }

    }

}
