package com.example.studentattendance;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class LecturersDetails extends Activity {
	
	private String TAG = LecturersDetails.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    
    // URL to get contacts JSON
    private static String url = "http://apilearningattend.totopeto.com/lecturers";
    
    ArrayList<HashMap<String, String>> lecturersList;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lecturers_details);
		
		lv = (ListView) findViewById(R.id.list);
        
        lv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
                        "API not found!",
                        Toast.LENGTH_LONG)
                        .show();
			}
		});
		
	}
	
	private class GetLecturers extends AsyncTask<Void, Void, Void> {
	   	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LecturersDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            
            // Making a request to URL and getting response
            String jsonStr = sh.makeServiceCall(url);
 
            Log.e(TAG, "Response from url: " + jsonStr);
 
            // Read JSON
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
 
                    // Getting JSON Array node
                    JSONArray students = jsonObj.getJSONArray("lecturers");
 
                    // looping through All Contacts
                    for (int i = 0; i < students.length(); i++) {
                        JSONObject c = students.getJSONObject(i);
                        
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String dob = c.getString("dob");
                        String created_at = c.getString("created_at");
                 
                        
                        // tmp hash map for single contact
                        HashMap<String, String> lecturer = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        lecturer.put("id", id);
                        lecturer.put("name", name);
                        lecturer.put("dob", dob);
                        lecturer.put("created_at", created_at);
                        
                        // adding contact to contact list
                        lecturersList.add(lecturer);
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
            		LecturersDetails.this, lecturersList,
                    R.layout.list_item_lecturers, new String[]{"name", "dob"}, new int[]{R.id.name,
                    R.id.dob});
 
            lv.setAdapter(adapter);
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	lecturersList = new ArrayList<HashMap<String, String>>();
    	new GetLecturers().execute();
    }
}
