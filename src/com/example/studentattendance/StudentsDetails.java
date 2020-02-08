package com.example.studentattendance;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import java.util.HashMap;


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
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StudentsDetails extends Activity {
	
	private String TAG = StudentsDetails.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv_ongoing, lv_history;
    private Button btn_checkin, btn_checkout, btn_allstudents;
    boolean isChekinClick = false;
    boolean isChekoutClick = false;
    Date currentTime = null;
    private String Pesan = "";
    
   
    // URL to get List Schedule Checkin JSON
    private static String Scheduleurl = "http://apilearningattend.totopeto.com/students/";
    
    // URL to get Checkin JSON
    private static String checkoutUrl = "http://apilearningattend.totopeto.com/attendances/checkout";
   
    public static final SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	public static final SimpleDateFormat EEEddMMMyyyy = new SimpleDateFormat("EEE dd-MMM-yyyy HH:mm", Locale.US);
	private String NewFormatDateCheckIn = "", NewFormatDateCheckOut = "";
	
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
	
    private String student_id, student_name;
    String schedule_id = "";
   
    ArrayList<HashMap<String, String>> ongoingList;
    ArrayList<HashMap<String, String>> attendancesList;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.students_details);
		
		Intent intent = getIntent();
		student_id = intent.getStringExtra("id");
		student_name = intent.getStringExtra("name");
		
		setTitle("Hello," + student_name);
		
		lv_ongoing = (ListView) findViewById(R.id.listOngoingSchedule);
		lv_history = (ListView) findViewById(R.id.listHistorySchedule);
		btn_checkin = (Button) findViewById(R.id.btnCheckin);
		btn_checkout = (Button) findViewById(R.id.btnCheckout);
		btn_allstudents = (Button) findViewById(R.id.btnallstudents);
		

		btn_allstudents.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				openAllStudent();
			}
		});

		btn_checkin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				openCheckinStudent();
			}
		});
		
		btn_checkout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				openCheckoutStudent();
				
				/*
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
                "check out!",
                Toast.LENGTH_LONG)
                .show();*/
			}
		});
		
		lv_history.setOnItemClickListener(new OnItemClickListener() {
			
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
	
	private void openAllStudent(){
		Intent intent = new Intent(StudentsDetails.this, AllStudents.class);
		startActivity(intent);
	}
	
	private void openCheckinStudent(){
		
		Intent intent = new Intent(StudentsDetails.this, StudentsCheckAttendance.class);
		intent.putExtra("id", student_id);
		intent.putExtra("name", student_name);
		intent.putExtra("text_check", "check in");
		startActivity(intent);
	}
	
	private void openCheckoutStudent(){
		new CheckoutStudent().execute();
		
	}
	
	private class CheckoutStudent extends AsyncTask<Void, Void, Void> {
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StudentsDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        
        @Override
        protected Void doInBackground(Void... arg0) {
            String post_params = null;
            JSONObject params = new JSONObject();
            
            Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.HOUR_OF_DAY, 12);
			cal.getTime();
			
			currentTime  = cal.getTime();
			
            Log.e("wakawak" , "Response from url: " + " student : " + student_id + " schedule_id : " + schedule_id + " check_in : " + currentTime);
            
            try {
            	
            	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        		String CheckinDate = df.format(currentTime);

            	params.put("student_id", student_id);
            	params.put("schedule_id", schedule_id);
            	params.put("check_out", CheckinDate);
            	post_params = params.toString();
            	
            } catch (JSONException e) {
            	e.printStackTrace();
            }
            
            Log.e("responnya", post_params);
            
            if(!TextUtils.isEmpty(post_params) && !TextUtils.isEmpty(schedule_id)){
            	HttpHandler data = new HttpHandler();
                String jsonStr = data.makePostRequest(checkoutUrl, post_params);
                Log.e(TAG, "Response from url: " + jsonStr);
              
                JSONObject respons = null;
    			try {
    				respons = new JSONObject(jsonStr);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
                if(respons != null)
                {
                    try {
                        Pesan = respons.getString("message");
                        isChekoutClick = true;
                       
                    } catch (JSONException ex) {
                    	Log.e("API", "Failure", ex);
                    }
                }
                
            }else{
            	Pesan = "Please fill this field";
            }

            return null;
            
        }
        
        @Override
        protected void onPostExecute(Void result) {
        	//Log.e("ini dia", Pesan);
        	String message = Pesan.substring(0,1).toUpperCase() + Pesan.substring(1);
        	super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            	Toast.makeText(getApplicationContext(),
            		message,
                    Toast.LENGTH_LONG)
                    .show();
            	
        	if(isChekoutClick){
        		
				Log.e("test klik", "masuk sini");
				btn_checkin.setVisibility(View.VISIBLE);
				btn_checkout.setVisibility(View.INVISIBLE);
				attendancesList.clear();
				new GetSchedule().execute();
			}
            /**
             * Updating parsed JSON data into ListView
             * */
        }
	}
	
	private class GetSchedule extends AsyncTask<Void, Void, Void> {
	   	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StudentsDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
        	
        	ongoingList.clear();
        	
            HttpHandler sh_schedule = new HttpHandler();
        	//Log.e("responnya gini", url_schedule + lecturer_id );
            
            // Making a request to URL and getting response
            String jsonStr_schedule = sh_schedule.makeServiceCall(Scheduleurl + student_id + "/attendance");
 
            Log.e(TAG, "Response from url: " + jsonStr_schedule);
 
            // Read JSON
            if (jsonStr_schedule != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr_schedule);
 
                    // Getting JSON Array node
                    JSONArray attendances = jsonObj.getJSONArray("attendances");
 
                    // looping through All Attendance
                    for (int i = 0; i < attendances.length(); i++) {
                        JSONObject c = attendances.getJSONObject(i);
                        	
        				
                        if (c.getString("check_out").contentEquals("null") && !c.getString("check_in").contentEquals("null")) {
                        	schedule_id = c.getString("schedule_id");
                        	
                        	String id = c.getString("id");
                            String student_id = c.getString("student_id");
                            String Schedules = c.getString("schedule");
                            
                            // tmp hash map for single contact
                            HashMap<String, String> ongoing = new HashMap<String, String>();
                            
                            // adding each child node to HashMap key => value
                            ongoing.put("id", id);
                            ongoing.put("student_id", student_id);
                            ongoing.put("schedule", Schedules);
                        
                            JSONObject c2 = c.getJSONObject("schedule");
                            String subject_name = c2.getString("subject_name");
                            
                            JSONObject c3 = c2.getJSONObject("lecturer");
                            String lecturer_name = c3.getString("name");
                            
                            
                            Log.e("test yang", subject_name);
               
                            ongoing.put("subject_name", subject_name);
                            ongoing.put("lecturer_name", lecturer_name);
                            
                            ongoingList.add(ongoing);
                        	
                        	isChekinClick = true;
                        	
                        }else if(!c.getString("check_out").contentEquals("null") && !c.getString("check_in").contentEquals("null")){
                        	
                        	//Extract DateTime To EEEddMMyyy
                            String in = c.getString("check_in");
            				String[] Tgl_arr = in.split("T");
            				String Tgl = Tgl_arr[0], Time = Tgl_arr[1];
            				String JadwalMasuk = Tgl+" "+Time;
            				
                        	String out = c.getString("check_out");
            				String[] Tgl_arr2 = out.split("T");
            				String Tgl2 = Tgl_arr2[0], Time2 = Tgl_arr2[1];
            				String JadwalKeluar = Tgl2+" "+Time2;
            				
            				try {
            					NewFormatDateCheckIn = parseDate(JadwalMasuk, ymdFormat, EEEddMMMyyyy);
            					NewFormatDateCheckOut = parseDate(JadwalKeluar, ymdFormat, EEEddMMMyyyy);
            				} catch (java.text.ParseException e) {
            					// TODO Auto-generated catch block
            					e.printStackTrace();
            				}
            				
                            String id = c.getString("id");
                            String student_id = c.getString("student_id");
                            String Schedules = c.getString("schedule");
                            String check_in = NewFormatDateCheckIn;
                            String check_out = NewFormatDateCheckOut;
                            
                            // tmp hash map for single contact
                            HashMap<String, String> attendance = new HashMap<String, String>();
                            
                            // adding each child node to HashMap key => value
                        	attendance.put("id", id);
                        	attendance.put("student_id", student_id);
                        	attendance.put("check_in", check_in);
                            attendance.put("check_out", check_out);
                            attendance.put("schedule", Schedules);
                            
                            /*// adding contact to contact list
                            attendancesList.add(attendance);*/
                        
                            JSONObject c2 = c.getJSONObject("schedule");
                            String subject_name = c2.getString("subject_name");
                            
                            JSONObject c3 = c2.getJSONObject("lecturer");
                            String lecturer_name = c3.getString("name");
                            
                            
                            Log.e("test yang", subject_name);
               
                            attendance.put("subject_name", subject_name);
                            attendance.put("lecturer_name", lecturer_name);
                            
                            attendancesList.add(attendance);
                            
                            //Log.e("test", "Check in : " + check_in + " Check out :" + check_out );
                            isChekoutClick = true;
                            
                             
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
            
            ListAdapter adapter_schedule = new SimpleAdapter(
            		StudentsDetails.this, attendancesList,
                    R.layout.list_item_history, new String[]{"lecturer_name", "subject_name", "check_in", "check_out"}, new int[]{R.id.tvlecturerName, R.id.tvsubjectName,
                    R.id.tvcheckIn, R.id.tvcheckOut});
            
            lv_history.setAdapter(adapter_schedule);
            
            if(ongoingList.isEmpty()){
            	
        		//Log.e("test klik", "masuk sini");
            	isChekinClick = false;
				btn_checkout.setVisibility(View.INVISIBLE);
				btn_checkin.setVisibility(View.VISIBLE);
        	}
            
            if(isChekinClick){
				Log.e("test klik", "masuk sini");
				btn_checkin.setVisibility(View.INVISIBLE);
				btn_checkout.setVisibility(View.VISIBLE);
				
				ListAdapter adapter_ongoing = new SimpleAdapter(
	            		StudentsDetails.this, ongoingList,
	                    R.layout.list_item_ongoing, new String[]{"lecturer_name", "subject_name"}, new int[]{R.id.tvlecturerName, R.id.tvsubjectName});
	            
				lv_ongoing.setAdapter(adapter_ongoing);
				
			}
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	attendancesList = new ArrayList<HashMap<String, String>>();
    	ongoingList = new ArrayList<HashMap<String, String>>();
    	new GetSchedule().execute();
    }
}
