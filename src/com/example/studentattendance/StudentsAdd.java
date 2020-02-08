package com.example.studentattendance;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StudentsAdd extends Activity {
	
	private String TAG = StudentsAdd.class.getSimpleName();
    private ProgressDialog pDialog;
    
    private static String url = "http://apilearningattend.totopeto.com/students";
    
    private String Pesan = "";
    TextView tlogin;
	Button bsimpan;
	EditText etcode, etname, etdob;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.students_add);
		
		tlogin = (TextView) findViewById(R.id.txtLogin);
		
		bsimpan = (Button) findViewById(R.id.btsimpan);
		etcode = (EditText) findViewById(R.id.etcode);
		etname = (EditText) findViewById(R.id.etname);
		etdob = (EditText) findViewById(R.id.etdob);
		
		bsimpan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AddStudent().execute();
				
				Intent intent = new Intent(StudentsAdd.this, MainActivity.class);
				startActivity(intent);
				
			}
		});
		
		tlogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(StudentsAdd.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private class AddStudent extends AsyncTask<Void, Void, Void> {
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StudentsAdd.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        
        @Override
        protected Void doInBackground(Void... arg0) {
            String post_params = null;
            JSONObject params = new JSONObject();
 
            try {
            	
            	
            	params.put("code", etcode.getText().toString());
            	params.put("name", etname.getText().toString());
            	params.put("dob", etdob.getText().toString());
            	post_params = params.toString();
            	
            } catch (JSONException e) {
            	e.printStackTrace();
            }
            
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
            
            
            //Log.e("outputnya", Pesan);
            
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
}
