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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AllStudents extends Activity {
	
	String TAG = LecturersDetails.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    
    ArrayList<HashMap<String, String>> studentsList;
    
 // URL to get List Students JSON
    private static String url = "http://apilearningattend.totopeto.com/students";
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_students);
		
		
		lv = (ListView) findViewById(R.id.listAllStudents);
		
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, String> hm = studentsList.get(position);
				
				//Log.e("output_string", NewFormatDateCreated);

				Intent intent = new Intent(AllStudents.this, StudentsItemDetails.class);
				intent.putExtra("id", hm.get("id"));
				intent.putExtra("code", hm.get("code"));
				intent.putExtra("name", hm.get("name"));
				intent.putExtra("dob", hm.get("dob"));
				startActivity(intent);
			}
		});
		
	
	}
	
	private class GetStudents extends AsyncTask<Void, Void, Void> {
	   	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AllStudents.this);
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
                    JSONArray students = jsonObj.getJSONArray("students");
 
                    // looping through All Contacts
                    for (int i = 0; i < students.length(); i++) {
                        JSONObject c = students.getJSONObject(i);
                        
                        String id = c.getString("id");
                        String code = c.getString("code");
                        String name = c.getString("name");
                        String dob = c.getString("dob");
                        String created_at = c.getString("created_at");
                 
                        
                        // tmp hash map for single contact
                        HashMap<String, String> student = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        student.put("id", id);
                        student.put("code", code);
                        student.put("name", name);
                        student.put("dob", dob);
                        student.put("created_at", created_at);
                        
                        // adding contact to contact list
                        studentsList.add(student);
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
                    AllStudents.this, studentsList,
                    R.layout.list_item, new String[]{"name", "code"}, new int[]{R.id.name,
                    R.id.code});
 
            lv.setAdapter(adapter);
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	studentsList = new ArrayList<HashMap<String, String>>();
    	new GetStudents().execute();
    }
}
