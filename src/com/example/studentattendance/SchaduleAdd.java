package com.example.studentattendance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SchaduleAdd extends Activity {
	
	private String Pesan = "";
	private String TAG = SchaduleAdd.class.getSimpleName();
    private ProgressDialog pDialog;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    EditText txt_matkul, txt_tglpertemuan, txt_jadwalmulai, txt_jadwalselesai;
    Button bsimpan;
    
    private static String url = "http://apilearningattend.totopeto.com/schedules";
    private String lecturer_id, lecturer_name;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schadule_add);
		
		
		bsimpan = (Button) findViewById(R.id.btsimpan);
		txt_matkul = (EditText) findViewById(R.id.etmatkul);
		txt_tglpertemuan = (EditText) findViewById(R.id.ettglPertemuan);
		txt_jadwalmulai = (EditText) findViewById(R.id.etjadwalmulai);
		txt_jadwalselesai = (EditText) findViewById(R.id.etjadwalselesai);
		
		Intent intent = getIntent();
		lecturer_id = intent.getStringExtra("id");
		lecturer_name = intent.getStringExtra("name");
		
		myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
           

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
			}
        };
        
        txt_tglpertemuan.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SchaduleAdd.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        
        txt_jadwalmulai.setOnClickListener(new View.OnClickListener() {
        	 
        	@Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SchaduleAdd.this, new TimePickerDialog.OnTimeSetListener() {
 
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    	// TODO Auto-generated method stub
                    	txt_jadwalmulai.setText(selectedHour + ":" + selectedMinute);
                    }

					
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
 
            }
        });
 
        txt_jadwalselesai.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SchaduleAdd.this, new TimePickerDialog.OnTimeSetListener() {
 
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    	// TODO Auto-generated method stub
                    	txt_jadwalselesai.setText(selectedHour + ":" + selectedMinute);
                    }

					
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
 
            }
        });
 
        bsimpan.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
            	new AddSchadule().execute();
				
				/*Intent intent = new Intent(SchaduleAdd.this, LecturersDetails.class);
				intent.putExtra("id", lecturer_id);
				intent.putExtra("name", lecturer_name);
				startActivity(intent);*/
				
				
                /*Toast.makeText(SchaduleAdd.this,
                        "Jadwal Mulai : " + txt_jadwalmulai.getText().toString() + "\n" +
                                "Jadwal Selesai : " + txt_jadwalselesai.getText().toString()
                        , Toast.LENGTH_SHORT
                ).show();*/
            }
        });
        
		
	}
    
    private class AddSchadule extends AsyncTask<Void, Void, Void> {
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SchaduleAdd.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        
        @Override
        protected Void doInBackground(Void... arg0) {
            String post_params = null;
            JSONObject params = new JSONObject();
 
            try {
            	
            	String JadwalMulai = txt_tglpertemuan.getText().toString()+"T"+txt_jadwalmulai.getText().toString()+":00.000Z";
            	String JadwalSelesai = txt_tglpertemuan.getText().toString()+"T"+txt_jadwalselesai.getText().toString()+":00.000Z";
				//Log.e("responnya","Jadwal Mulai :" + JadwalMulai + "Jadwal Selesai" + JadwalSelesai);
            	String Matkul = txt_matkul.getText().toString();
            	
            	
            	
            	if (!TextUtils.isEmpty(Matkul) && !TextUtils.isEmpty(JadwalMulai) && !TextUtils.isEmpty(JadwalSelesai)) {
            		params.put("lecturer_id", lecturer_id);
                	params.put("subject_name", Matkul);
                	params.put("time_in", JadwalMulai);
                	params.put("time_out", JadwalSelesai);
                	post_params = params.toString();
            		
            	}
            	
            	
            } catch (JSONException e) {
            	e.printStackTrace();
            }
            
            if(!TextUtils.isEmpty(post_params)){
            	HttpHandler data = new HttpHandler();
                String jsonStr = data.makePostRequest(url, post_params);
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
            //Log.e("outputnya", Pesan);
            
            
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
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txt_tglpertemuan.setText(sdf.format(myCalendar.getTime()));
    }
}
