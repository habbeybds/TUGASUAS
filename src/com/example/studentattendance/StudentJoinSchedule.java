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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StudentJoinSchedule extends Activity {
	
	private String TAG = SchaduleLecturerOthers.class.getSimpleName();
    private ProgressDialog pDialog;
	private String schedule_id, subject_name, student_name;
	TextView tname;
	private ListView lv;
	
	// URL to get students join JSON
    private static String url_join = "http://apilearningattend.totopeto.com/schedules/";
	
    // URL to get Students JSON
    private static String student_url = "http://apilearningattend.totopeto.com/students/";
    
	ArrayList<HashMap<String, String>> studentJoinList;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_schedule_join);
		
		Intent intent = getIntent();
		schedule_id = intent.getStringExtra("id");
		subject_name = intent.getStringExtra("name");
		
		tname = (TextView) findViewById(R.id.txtName);
		lv = (ListView) findViewById(R.id.listStudentJoin);
		tname.setText(subject_name);

	}
	
	private class GetStudentJoin extends AsyncTask<Void, Void, Void> {
	   	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StudentJoinSchedule.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            
        	
            HttpHandler sh_schedule = new HttpHandler();
        	//Log.e("responnya gini", url_schedule + lecturer_id );
            // Making a request to URL and getting response
            String jsonStr_join = sh_schedule.makeServiceCall(url_join + schedule_id + "/attendance");
 
            Log.e(TAG, "Response from url: " + jsonStr_join);
 
            // Read JSON
            if (jsonStr_join != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr_join);
 
                    // Getting JSON Array node
                    JSONArray schedules = jsonObj.getJSONArray("attendances");
 
                    // looping through All Contacts
                    for (int i = 0; i < schedules.length(); i++) {
                        JSONObject c = schedules.getJSONObject(i);
                        
                        String id = c.getString("id");
                        String student_id = c.getString("student_id");  
                        String schedule_id = c.getString("schedule_id");;
    
                        // tmp hash map for single contact
                        HashMap<String, String> student_join = new HashMap<String, String>();
                        
                        if (!TextUtils.isEmpty(student_id) && !TextUtils.isEmpty(schedule_id)) {
                        	
                        	HttpHandler sh_student = new HttpHandler();
                            // Making a request to URL and getting response
                            String jsonStr_students = sh_student.makeServiceCall(student_url + student_id );
                            
                            if (jsonStr_students != null) {
                            	
                            	JSONObject jsonObj_students = new JSONObject(jsonStr_students);
                                
                            	JSONObject c2 = jsonObj_students.getJSONObject("student");
                                student_name = c2.getString("name");
                                
                                //Log.e("nama student", student_name);
                            }
                            
                        	// adding each child node to HashMap key => value
                        	student_join.put("id", id);
                        	student_join.put("student_name", student_name);
                        	student_join.put("schedule_id", schedule_id);
                            
                            // adding contact to contact list
                            studentJoinList.add(student_join);
                        }
                        
                        
                        
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
            
            //Log.e("responnya","masuk sini");
         
            ListAdapter adapter_schedule = new SimpleAdapter(
            		StudentJoinSchedule.this, studentJoinList,
                    R.layout.list_item_students_join, new String[]{"student_name"}, new int[]{R.id.name});
 
            lv.setAdapter(adapter_schedule);
        }
    }
	
    
    @Override
    public void onResume() {
    	super.onResume();
    	studentJoinList = new ArrayList<HashMap<String, String>>();
    	new GetStudentJoin().execute();
    }
}
