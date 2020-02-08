package com.example.studentattendance;

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



public class MainActivity extends Activity {
	private String Pesan = "";
	private String resultValidate = "", getId, getName;
	TextView bregister;
	private Button btnLogin;
	private String TAG = MainActivity.class.getSimpleName();
	private ProgressDialog pDialog;
	EditText etname, etpassword;
	
	// URL to get contacts JSON
    private static String url_students = "http://apilearningattend.totopeto.com/students";
    
    // URL to get contacts JSON
    private static String url_lecturers = "http://apilearningattend.totopeto.com/lecturers";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnLogin = (Button) findViewById(R.id.btlogin);
		bregister = (TextView) findViewById(R.id.txtRegister);
		
		
		etname = (EditText) findViewById(R.id.etname);
		etpassword = (EditText) findViewById(R.id.etpassword);
		
		//setOnClick(btnLogin, 0);
		
		
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new CheckLogin().execute();
			}
		});
		
		bregister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openRegisterPage();
				
			}
		});
	}
	
	public Void validateLogin(boolean Check){
		resultValidate = String.valueOf(Check);
		return null;
	}
	
	public void openStudentsPage(){
		Intent intent = new Intent(MainActivity.this,StudentsDetails.class);
		intent.putExtra("id", getId);
		intent.putExtra("name", getName);
		startActivity(intent);
	}
	
	public void openLecturersPage(){
		Intent intent = new Intent(MainActivity.this,LecturersDetails.class);
		//Log.e("cek id",getId);
		intent.putExtra("id", getId);
		intent.putExtra("name", getName);
		startActivity(intent);
	}
	public void openRegisterPage(){
		Intent intent = new Intent(MainActivity.this,ChooseRegister.class);
		startActivity(intent);
	}
	
	private class CheckLogin extends AsyncTask<Void, Void, Void> {
			
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
            
            
            HttpHandler data = new HttpHandler();
            
            // Making a request to URL and getting response
            String jsonStr = data.makeServiceCall(url_students);
 
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
                        String name = c.getString("name");
                        String code = c.getString("code");
                        String password = "1234567890";
                        
                        //Log.e("outputnya", "POST =" + etpassword.getText().toString() + "GET = " + password + "&&" + "POST =" + etname.getText().toString() + "GET = " + code);
                        
                        if(etname.getText().toString().contentEquals(code) && etpassword.getText().toString().contentEquals(password)){
                        	getId = id;
                        	getName = name;
                        	resultValidate = "true";
                        	break;
                        }else{
                        	resultValidate = "false";
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
            
            //Log.e("cek kalo salah jadi :" , resultValidate);
            
            if(resultValidate.contentEquals("false")){
            	
            	// Making a request to URL and getting response
                String jsonStrLecturers = data.makeServiceCall(url_lecturers);
     
                Log.e(TAG, "Response from url lecturers: " + jsonStrLecturers);
                
                // Read JSON
                if (jsonStrLecturers != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStrLecturers);
     
                        // Getting JSON Array node
                        JSONArray lecturers = jsonObj.getJSONArray("lecturers");
     
                        // looping through All Contacts
                        for (int i = 0; i < lecturers.length(); i++) {
                            JSONObject c = lecturers.getJSONObject(i);
                            
                            String id = c.getString("id");
                            String name = c.getString("name");
                            String password = "1234567890";
                            
                            //Log.e("outputnya", "POST =" + etpassword.getText().toString() + "GET = " + password + "&&" + "POST =" + etname.getText().toString() + "GET = " + code);
                            
                            if(etname.getText().toString().contentEquals(name) && etpassword.getText().toString().contentEquals(password)){
                            	getId = id;
                            	getName = name;
                            	resultValidate = "true_lecturers";
                            	break;
                            }else{
                            	resultValidate = "false";
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
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
        	Log.e("ini dia", resultValidate);
        	Pesan = "username or password not match";
        	String message = Pesan.substring(0,1).toUpperCase() + Pesan.substring(1);
        	
        	if(resultValidate.contentEquals("true")){
        		openStudentsPage();
        	}else if(resultValidate.contentEquals("true_lecturers")){
        		
        		openLecturersPage();
        	}else{
        		Toast.makeText(getApplicationContext(),
        		message,
                Toast.LENGTH_LONG)
                .show();
        	}
        	
        	super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            	
            /**
             * Updating parsed JSON data into ListView
             * */
        }

	}
	
}
