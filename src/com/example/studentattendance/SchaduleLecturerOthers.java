package com.example.studentattendance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
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

public class SchaduleLecturerOthers extends Activity {
	
	private String TAG = SchaduleLecturerOthers.class.getSimpleName();
    private ProgressDialog pDialog;
	private String lecturer_id, lecturer_name;
	TextView tname;
	private ListView lv;
	
	// URL to get contacts JSON
    private static String url_schedule = "http://apilearningattend.totopeto.com/schedules?lecturer_id=";
	
    public static final SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	public static final SimpleDateFormat EEEddMMMyyyy = new SimpleDateFormat("EEE dd-MMM-yyyy HH:mm", Locale.US);
	private String NewFormatDateTimeIn = "", NewFormatDateTimeOut = "";
	
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
	
	ArrayList<HashMap<String, String>> scheduleList;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_lecturer_others);
		
		Intent intent = getIntent();
		lecturer_id = intent.getStringExtra("id");
		lecturer_name = intent.getStringExtra("name");
		tname = (TextView) findViewById(R.id.txtName);
		lv = (ListView) findViewById(R.id.listSchedule);
		tname.setText(lecturer_name);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
                        "Cannot open schedule lecturer other!",
                        Toast.LENGTH_LONG)
                        .show();
			}
		});
		
	
	}
	
	private class GetScheduleLecturerDetails extends AsyncTask<Void, Void, Void> {
	   	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SchaduleLecturerOthers.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            
        	
            HttpHandler sh_schedule = new HttpHandler();
        	//Log.e("responnya gini", url_schedule + lecturer_id );
            // Making a request to URL and getting response
            String jsonStr_schedule = sh_schedule.makeServiceCall(url_schedule + lecturer_id);
 
            Log.e(TAG, "Response from url: " + jsonStr_schedule);
 
            // Read JSON
            if (jsonStr_schedule != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr_schedule);
 
                    // Getting JSON Array node
                    JSONArray schedules = jsonObj.getJSONArray("schedules");
 
                    // looping through All Contacts
                    for (int i = 0; i < schedules.length(); i++) {
                        JSONObject c = schedules.getJSONObject(i);
                        
                        if(c.getString("time_in") != "null" && c.getString("time_out") != "null"){
                        	//Extract DateTime To EEEddMMyyy
                            String in = c.getString("time_in");
            				String[] Tgl_arr = in.split("T");
            				String Tgl = Tgl_arr[0], Time = Tgl_arr[1];
            				String JadwalMasuk = Tgl+" "+Time;
            				
            				Log.e("ini set tanggal", JadwalMasuk);
            				
            				String out = c.getString("time_out");
            				String[] Tgl_arr2 = out.split("T");
            				String Tgl2 = Tgl_arr2[0], Time2 = Tgl_arr2[1];
            				String JadwalKeluar = Tgl2+" "+Time2;
            				
            				try {
            					NewFormatDateTimeIn = parseDate(JadwalMasuk, ymdFormat, EEEddMMMyyyy);
            					NewFormatDateTimeOut = parseDate(JadwalKeluar, ymdFormat, EEEddMMMyyyy);
            				} catch (java.text.ParseException e) {
            					// TODO Auto-generated catch block
            					e.printStackTrace();
            				}
            				// End Extract
            				
                            String id = c.getString("id");
                            String lecturer_id = c.getString("lecturer_id");;
                            String subject_name = c.getString("subject_name");
                            String time_in = NewFormatDateTimeIn;
                            String time_out = NewFormatDateTimeOut;
                     
                            
                            // tmp hash map for single contact
                            HashMap<String, String> schedule = new HashMap<String, String>();
                            
                            if (!TextUtils.isEmpty(subject_name) && !TextUtils.isEmpty(time_in) && !TextUtils.isEmpty(time_out)) {
                            	// adding each child node to HashMap key => value
                                schedule.put("id", id);
                                schedule.put("lecturer_id", lecturer_id);
                                schedule.put("subject_name", subject_name);
                                schedule.put("time_in", time_in);
                                schedule.put("time_out", time_out);
                                
                                // adding contact to contact list
                                scheduleList.add(schedule);
                            }
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
            
            Log.e("responnya","masuk sini");
         
            ListAdapter adapter_schedule = new SimpleAdapter(
            		SchaduleLecturerOthers.this, scheduleList,
                    R.layout.list_item_lecturer_others, new String[]{"subject_name", "time_in", "time_out"}, new int[]{R.id.tvsubjectName,
                    R.id.tvtimeIn, R.id.tvtimeOut});
 
            lv.setAdapter(adapter_schedule);
        }
    }
	
    
    @Override
    public void onResume() {
    	super.onResume();
    	scheduleList = new ArrayList<HashMap<String, String>>();
    	new GetScheduleLecturerDetails().execute();
    }
}
