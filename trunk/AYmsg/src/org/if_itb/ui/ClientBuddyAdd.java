/**
 * 
 */
package org.if_itb.ui;

import java.util.ArrayList;
import java.util.Iterator;
import org.if_itb.client.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * @author Ahmy Yulrizka ahmy135 [at] gmail [dot] com
 * Activity to add Buddy
 */
public class ClientBuddyAdd extends Activity
{
	public static final String ID = "yahooId";
	public static final String GROUP = "yahooGroup";
	public static final String GROUP_LIST = "groupList";
	
	private EditText txtYahooId;
	private EditText txtCustomGroup;
	private RadioButton radExisting;
	private RadioButton radCustom;
	private Spinner spinGroup;
	private OnClickListener mAddBuddy = new OnClickListener() {

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		public void onClick(View arg0)
		{
			
			String yahooId = ClientBuddyAdd.this.txtYahooId.getText().toString();
			String group;
			if (ClientBuddyAdd.this.radCustom.isChecked())
			{
				group = ClientBuddyAdd.this.txtCustomGroup.getText().toString();
			} else
			{
				group = (String) ClientBuddyAdd.this.spinGroup.getSelectedItem();
			}
			if (yahooId == null || yahooId.length() ==0 || group == null || group.length() == 0)
				return;					
			Bundle extras = new Bundle();
			extras.putString(GROUP, group);
			ClientBuddyAdd.this.setResult(RESULT_OK, yahooId, extras);
			ClientBuddyAdd.this.finish();
		}
		
	};
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		
		this.setContentView(R.layout.buddy);
		
		this.txtYahooId = (EditText) this.findViewById(R.id.txtYahooId); 
		this.txtCustomGroup = (EditText) this.findViewById(R.id.txtCustomGroup);
		this.radCustom = (RadioButton) this.findViewById(R.id.radCustom);
		this.radExisting= (RadioButton) this.findViewById(R.id.radExisting);
		this.spinGroup = (Spinner) this.findViewById(R.id.spinnerGroup);
		
		Button b = (Button)this.findViewById(R.id.btnAddBuddy);
		b.setOnClickListener(this.mAddBuddy);
		
		this.txtCustomGroup.setOnFocusChangeListener(new OnFocusChangeListener() {

			/* (non-Javadoc)
			 * @see android.view.View.OnFocusChangeListener#onFocusChanged(android.view.View, boolean)
			 */
			public void onFocusChanged(View arg0, boolean arg1)
			{
				if (!ClientBuddyAdd.this.radCustom.isChecked())
				{
					ClientBuddyAdd.this.radCustom.toggle();
				}
			}
		});
		
		this.spinGroup.setOnItemSelectedListener(new OnItemSelectedListener() {

			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemSelected(AdapterView parent, View v, int position, long id)
			{
				if (!ClientBuddyAdd.this.radExisting.isChecked())
				{
					ClientBuddyAdd.this.radExisting.toggle();
				}
				
			}

			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
			 */
			public void onNothingSelected(AdapterView arg0)
			{
				
			}			
		});
		
		Bundle bs = this.getIntent().getExtras();
		if (bs != null)
		{
			Bundle bGrp = bs.getBundle(GROUP_LIST);
			ArrayList<String> groupList = new ArrayList<String>();

			if (bGrp != null)
			{
				for (Iterator<String> i = bGrp.keySet().iterator(); i.hasNext();)
				{
					groupList.add(bGrp.getString(i.next()));
				}
			}
			ArrayAdapter<String> grpAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,groupList);
			this.spinGroup.setAdapter(grpAdapter);
		}
		//populate Group List
		
		
		
	}
}
