package com.example.studentattendance;

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

public class ChooseRegister extends Activity {
	private Button btmahasiswa, btdosen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_register);
		
		btmahasiswa = (Button) findViewById(R.id.btMahasiswa);
		btdosen = (Button) findViewById(R.id.btDosen);
		
		btmahasiswa.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openMahasiswaPage();
				
			}
		});
		
		btdosen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openDosenPage();
				
			}
		});

	}
	
	public void openMahasiswaPage(){
		Intent intent = new Intent(ChooseRegister.this,StudentsAdd.class);
		startActivity(intent);
	}
	
	public void openDosenPage(){
		Intent intent = new Intent(ChooseRegister.this,LecturersAdd.class);
		startActivity(intent);
	}
}
