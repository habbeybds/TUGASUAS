package com.example.studentattendance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StudentsCheckAttendance extends Activity{
	private String TAG = StudentsDetails.class.getSimpleName();
    private ProgressDialog pDialog;
    
    // URL to get Lectures JSON
    private static String lecturerUrl = "http://apilearningattend.totopeto.com/lecturers";
    
    // URL to get Schedule JSON
    private static String scheduleUrl = "http://apilearningattend.totopeto.com/schedules?lecturer_id=";
    
    // URL to get Checkin JSON
    private static String checkinUrl = "http://apilearningattend.totopeto.com/attendances/checkin";
    
    
    ArrayList<HashMap<String, String>> lecturersList;
    ArrayList<HashMap<String, String>> scheduleList;
    
    private String Pesan = "";
    Spinner stoLecturer, stoSchedule;
    private String student_id, from_name, text_check;
    String lecturer_id = "", schedule_id = "";
    Button btnsimpan;
    TextView tvCheck;
    
    Date currentTime = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.students_attendance_check);
		
		Intent intent = getIntent();
		student_id = intent.getStringExtra("id");
		from_name = intent.getStringExtra("name");
		text_check = intent.getStringExtra("text_check");
		setTitle("From: " + from_name);
		
		tvCheck = (TextView) findViewById(R.id.tvCheckSchedule);
		
		tvCheck.setText(text_check);
		
		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		//String date = df.format(currentTime);
		//String CheckinDate = date + ".000Z";

		
		/*try {
			String TimeIn = "2020-02-02 17:10:00";
		    Date time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(TimeIn);
		    Calendar CTimeIn = Calendar.getInstance();
		    CTimeIn.setTime(time1);


		    String TimeOut = "2020-02-02 17:20:00";
		    Date time2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(TimeOut);
		    Calendar CTimeOut = Calendar.getInstance();
		    CTimeOut.setTime(time2);

		    Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.HOUR_OF_DAY, 12);
			cal.getTime();
			
			Date currentTime  = cal.getTime();
			
			Log.e("waktu sebelum cek", currentTime + "==" + CTimeIn.getTime() + "==" + CTimeOut.getTime());
			
		    if (currentTime.after(CTimeIn.getTime()) && currentTime.before(CTimeOut.getTime())) {
		        //checkes whether the current time is between 14:49:00 and 20:11:13.
		    	Log.e("waktu sekarang", "masuk sini");
		    }
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
				
	
		
		
		//Log.e("waktu sekarang", date1+"T"+time1+".000Z");
		
		stoLecturer = (Spinner) findViewById(R.id.sptolecturer);
		stoSchedule = (Spinner) findViewById(R.id.sptoschedule);
		
		btnsimpan = (Button) findViewById(R.id.btsimpan);
		
		stoLecturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
		            int pos, long id) {
				// TODO Auto-generated method stub
				HashMap<String, String> hm = lecturersList.get(pos);
				lecturer_id = hm.get("id");
				Log.e("Test Lecturer id", lecturer_id);
				
				scheduleList = new ArrayList<HashMap<String, String>>();
				new GetSchedules().execute();
				
				stoSchedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
				            int pos, long id) {
						// TODO Auto-generated method stub
						HashMap<String, String> hm = scheduleList.get(pos);
						schedule_id = hm.get("id");
						/*Toast.makeText(getApplicationContext(),
                                 "Test Schedule id: " + schedule_id,
                                 Toast.LENGTH_LONG)
                                 .show();*/
						Log.e("Test Schedule id", schedule_id);
						
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(),
                                "No schedule found.",
                                Toast.LENGTH_LONG)
                                .show();
					}
				
				});
				
				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnsimpan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				new CheckinStudent().execute();
				
				
				/*Intent intent = new Intent(StudentsCheckAttendance.this, StudentsDetails.class);
				intent.putExtra("id", student_id);
				intent.putExtra("name", from_name);
				startActivity(intent);
				*/
				
				
			}
		});
		
	}
	

	
	private class CheckinStudent extends AsyncTask<Void, Void, Void> {
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StudentsCheckAttendance.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        
        @Override
        protected Void doInBackground(Void... arg0) {
            String post_params = null;
            JSONObject params = new JSONObject();
            
            //Log.e("wakawak" , "Response from url: " + " student : " + student_id + " schedule_id : " + schedule_id + " check_in : " + currentTime);
            
            try {
            	
            	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        		String CheckinDate = df.format(currentTime);

            	params.put("student_id", student_id);
            	params.put("schedule_id", schedule_id);
            	params.put("check_in", CheckinDate);
            	post_params = params.toString();
            	
            } catch (JSONException e) {
            	e.printStackTrace();
            }
            
            Log.e("responnya", post_params);
            
            if(!TextUtils.isEmpty(post_params) && !TextUtils.isEmpty(schedule_id)){
            	HttpHandler data = new HttpHandler();
                String jsonStr = data.makePostRequest(checkinUrl, post_params);
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
            	
            finish();
            /**
             * Updating parsed JSON data into ListView
             * */
        }
	}
	
	private class GetLecturers extends AsyncTask<Void, Void, Void> {
	   	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StudentsCheckAttendance.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(lecturerUrl);
            Log.e(TAG, "Response from url: " + jsonStr);
 
            // Read JSON
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
 
                    // Getting JSON Array node
                    JSONArray lecturers = jsonObj.getJSONArray("lecturers");
                    
                    // looping through All Contacts
                    for (int i = 0; i < lecturers.length(); i++) {
                        JSONObject c = lecturers.getJSONObject(i);

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
            
            List<String> arrayList = new ArrayList<String>();
            
            for(int i=0; i < lecturersList.size(); i++) {
            	HashMap<String, String> hm = lecturersList.get(i);
            	arrayList.add(hm.get("name"));
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(StudentsCheckAttendance.this, android.R.layout.simple_spinner_item, arrayList); 
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stoLecturer.setAdapter(adapter);
        }
    }

	private class GetSchedules extends AsyncTask<Void, Void, Void> {
	   	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StudentsCheckAttendance.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
        		
        	HttpHandler sh_schedule = new HttpHandler();
        	//Log.e("responnya gini", url_schedule + lecturer_id );
            // Making a request to URL and getting response
            String jsonStr_schedule = sh_schedule.makeServiceCall(scheduleUrl + lecturer_id);
 
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
                        	
                        	String TimeIn = c.getString("time_in");
                    		String TimeOut = c.getString("time_out");
                    		
                    		//Extract DateTime To EEEddMMyyy
                            String in = TimeIn;
                    		String[] Tgl_arr = in.split("T");
                    		String Tgl = Tgl_arr[0],Time = Tgl_arr[1];
                    		String[] Time_arr = Time.split("\\.");
                    		String JadwalMasuk = Tgl+" "+Time_arr[0];
                    		
                    	
                    		String out = TimeOut;
                    		String[] Tgl_arr2 = out.split("T");
                    		String Tgl2 = Tgl_arr2[0], Time2 = Tgl_arr2[1];
                    		String[] Time_arr2 = Time2.split("\\.");
                    		String JadwalKeluar = Tgl2+" "+Time_arr2[0];
                    		// End Extrak
                    		
                    		//Log.e("ini set tanggal", JadwalMasuk + "=="+ JadwalKeluar);
                    		
                          
                            try {
                    			
                    		    Date time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(JadwalMasuk);
                    		    Calendar CTimeIn = Calendar.getInstance();
                    		    CTimeIn.setTime(time1);

                    		    Date time2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(JadwalKeluar);
                    		    Calendar CTimeOut = Calendar.getInstance();
                    		    CTimeOut.setTime(time2);

                    		    Calendar cal = Calendar.getInstance();
                    			cal.setTime(new Date());
                    			cal.add(Calendar.HOUR_OF_DAY, 12);
                    			cal.getTime();
                    			
                    			currentTime  = cal.getTime();
                    			
                    			//Log.e("waktu sebelum cek", currentTime + "==" + CTimeIn.getTime() + "==" + CTimeOut.getTime());
                    			
                    		    if (currentTime.after(CTimeIn.getTime()) && currentTime.before(CTimeOut.getTime())) {
                    		        //checkes whether the current time is between 14:49:00 and 20:11:13.
                    		    	
                    		    	String id = c.getString("id");
                                    String lecturer_id = c.getString("lecturer_id");;
                                    String subject_name = c.getString("subject_name");
                                    String time_in = c.getString("time_in");
                                    String time_out = c.getString("time_out");
                                    
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
                    		} catch (java.text.ParseException e) {
                    			// TODO Auto-generated catch block
                    			e.printStackTrace();
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
            
            List<String> arrayList = new ArrayList<String>();
            
            for(int i=0; i < scheduleList.size(); i++) {
            	HashMap<String, String> hm = scheduleList.get(i);
            	arrayList.add(hm.get("subject_name"));
            }
            
            ArrayAdapter<String> adapter_schedule = new ArrayAdapter<String>(StudentsCheckAttendance.this, android.R.layout.simple_spinner_item, arrayList); 
            adapter_schedule.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stoSchedule.setAdapter(adapter_schedule);
        }
    }
	
	@Override
    public void onResume() {
    	super.onResume();
    	lecturersList = new ArrayList<HashMap<String, String>>();
    	new GetLecturers().execute();
    }
}
