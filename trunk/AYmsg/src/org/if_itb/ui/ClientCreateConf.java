/**
 * 
 */
package org.if_itb.ui;

import java.util.ArrayList;
import org.if_itb.client.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

/**
 * @author Ahmy Yulrizka ahmy135 [at] gmail [dot] com
 * 
 */
public class ClientCreateConf extends Activity
{
	public final static String BUDDY = "group";
	public final static String CONF_LIST = "conferenceList";
	public final static String CONF_NAME = "conferenceName";
	public final static String MESSAGE = "message";
	private EditText txtConfName;
	private EditText txtConfMsg;
	private Button btnCreateConf;
	private Spinner spConf;
	private RadioButton rbE;
	private RadioButton rbC;

	private View.OnClickListener mCreateConference = new OnClickListener() {
		public void onClick(View arg0)
		{
			String buddyName = ClientCreateConf.this.getIntent().getExtras().getString(BUDDY);
			String confName = (ClientCreateConf.this.rbC.isChecked()) ? ClientCreateConf.this.txtConfName.getText().toString() : (String)ClientCreateConf.this.spConf.getSelectedItem(); 
			Log.d(ClientMain.TAG, "ClientCreateConf > room: " + confName + " buddy = " + buddyName + " message: " + ClientCreateConf.this.txtConfMsg.toString());
			
			Bundle extras = new Bundle();
			extras.putString(BUDDY, buddyName);
			extras.putString(CONF_NAME, confName);
			extras.putString(MESSAGE, ClientCreateConf.this.txtConfMsg.getText().toString());
			
			ClientCreateConf.this.setResult(RESULT_OK, null, extras);
			ClientCreateConf.this.finish();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle icicle)
	{
		this.setContentView(R.layout.create_conference);
		this.txtConfName = (EditText) this.findViewById(R.id.conf_room_name);
		this.txtConfMsg = (EditText) this.findViewById(R.id.conf_message);
		this.btnCreateConf = (Button) this.findViewById(R.id.btn_create_conference);
		this.btnCreateConf.setOnClickListener(this.mCreateConference);
		this.spConf = (Spinner) this.findViewById(R.id.spinnerConf);
		this.rbE = (RadioButton) this.findViewById(R.id.radConfExisting);
		this.rbC = (RadioButton) this.findViewById(R.id.radConfCustom);
		// populate Conference Name
		if (this.getIntent() == null)
			return;
		Bundle extras = this.getIntent().getExtras();
		Bundle cfNames = extras.getBundle(CONF_LIST);
		if (cfNames != null)
		{
			// get names;
			String cfs[] = new String[cfNames.size()];
			cfNames.keySet().toArray(cfs);
			ArrayList<String> arCf = new ArrayList<String>();
			for (String cf : cfs)
			{
				arCf.add(cf);
			}
			ArrayAdapter<String> cfAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, arCf);
			this.spConf.setAdapter(cfAdapter);
			// set spinner
			this.spConf
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						/*
						 * (non-Javadoc)
						 * 
						 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
						 *      android.view.View, int, long)
						 */
						public void onItemSelected(AdapterView parent, View v,
								int position, long id)
						{
								ClientCreateConf.this.rbE.setChecked(true);
						}

						/*
						 * (non-Javadoc)
						 * 
						 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
						 */
						public void onNothingSelected(AdapterView arg0)
						{}
					});
		} else //cfName == null
		{
			this.rbE.setEnabled(false);
			this.rbC.setChecked(true);
			this.spConf.setEnabled(false);
		}
		
		//set txtConfName
		this.txtConfName.setOnClickListener(new OnClickListener(){

			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View arg0)
			{
				if (!ClientCreateConf.this.rbC.isChecked())
				{
					ClientCreateConf.this.rbC.toggle();
				}
			}
			
		});	
			
		super.onCreate(icicle);
	}
}
