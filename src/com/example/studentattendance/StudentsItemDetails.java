package com.example.studentattendance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class StudentsItemDetails extends Activity {
	
	private String dateCreated;
	private String TAG = StudentsItemDetails.class.getSimpleName();
    private ProgressDialog pDialog;
    
    //URL to get contacts JSON
    private static String url = "http://apilearningattend.totopeto.com/students/";
    
    public static final SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	public static final SimpleDateFormat EEEddMMMyyyy = new SimpleDateFormat("EEE dd-MMM-yyyy", Locale.US);
	private String NewFormatDateCreated = "";
	
	public static String parseDate(String inputDateString, SimpleDateFormat inputDateFormat, SimpleDateFormat outputDateFormat) throws java.text.ParseException {
	    Date date = null;
	    String outputDateString = null;
	    try {
	        date = inputDateFormat.parse(inputDateString);
	        outputDateString = outputDateFormat.format(date);
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
	    return outputDateString;
	}
	
    ArrayList<HashMap<String, String>> studentList;
    
	TextView tname, tcode, tdob, tcreated;
	
	String name, code, dob, created;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_item_details);
		
		tname = (TextView) findViewById(R.id.tvname);
		tcode = (TextView) findViewById(R.id.tvcode);
		tdob = (TextView) findViewById(R.id.tvdob);
		tcreated = (TextView) findViewById(R.id.tvCreatedDate);
	
	}
	
	private class GetStudents extends AsyncTask<Void, Void, Void> {
	   	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StudentsItemDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
        	Intent intent = getIntent();
    		String id = intent.getStringExtra("id");

            HttpHandler sh = new HttpHandler();
 
            // Making a request to URL and getting response
            String jsonStr = sh.makeServiceCall(url + id);
 
            Log.e(TAG, "Response from url: " + jsonStr);
 
            // Read JSON
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    
                    JSONObject c = jsonObj.getJSONObject("student");
                    
                    //Extract DateTime To EEEddMMyyy
    				dateCreated = c.getString("created_at");
    				String[] separated = dateCreated.split("T");
    				
    				try {
    					NewFormatDateCreated = parseDate(separated[0], ymdFormat, EEEddMMMyyyy);
    				} catch (java.text.ParseException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    				// End Extract
    				
                    name = c.getString("name");
                    code = c.getString("code");
                    dob = c.getString("dob");
                    created = NewFormatDateCreated;
                    
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
            
            tname.setText(name);
    		tcode.setText(code);
    		tdob.setText(dob);
    		tcreated.setText(created);
        }
    }

    @Override
    public void onResume() {
    	super.onResume();
    	new GetStudents().execute();
    }
}
