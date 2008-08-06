/**
 * 
 */
package org.if_itb.ui;

import org.if_itb.client.AYmsgType;
import org.if_itb.client.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This Activity is used to change the status
 * @author Ahmy Yulrizka ahmy135 [at] gmail [dot] com
 *
 */
public class ClientStatus extends Activity
{
	public static final String STATUS_VALUE = "status";
	public static final String USECUSTOM_VALUE = "useCustom";
	private TextView txtCustomMessage;
	
	private Spinner spinStatus;
	private CheckBox chkCustomStatus;
	private OnClickListener mSetStatus = new OnClickListener() {

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		public void onClick(View arg0)
		{
			Bundle extras = new Bundle();
			int selectedStatus;
			boolean useCustom = false;
			
			switch (ClientStatus.this.spinStatus.getSelectedItemPosition())
			{
				case 0:
					selectedStatus = AYmsgType.YAHOO_STATUS_AVAILABLE;
					break;
				case 1:
					selectedStatus = AYmsgType.YAHOO_STATUS_BRB;
					break;
				case 2:
					selectedStatus = AYmsgType.YAHOO_STATUS_BUSY;
					break;
				case 3:
					selectedStatus = AYmsgType.YAHOO_STATUS_NOTATHOME;
					break;
				case 4:
					selectedStatus = AYmsgType.YAHOO_STATUS_NOTATDESK;
					break;
				case 5:
					selectedStatus = AYmsgType.YAHOO_STATUS_NOTINOFFICE;
					break;
				case 6:
					selectedStatus = AYmsgType.YAHOO_STATUS_ONPHONE;
					break;
				case 7:
					selectedStatus = AYmsgType.YAHOO_STATUS_ONVACATION;
					break;
				case 8:
					selectedStatus = AYmsgType.YAHOO_STATUS_OUTTOLUNCH;
					break;
				case 9:
					selectedStatus = AYmsgType.YAHOO_STATUS_STEPPEDOUT;
					break;
				case 12:
					selectedStatus = AYmsgType.YAHOO_STATUS_INVISIBLE;
					break;
				default:
					selectedStatus = -1;
					break;
			}
			useCustom = ClientStatus.this.chkCustomStatus.isChecked();
			extras.putInt(STATUS_VALUE, selectedStatus);
			extras.putBoolean(USECUSTOM_VALUE, useCustom);
			
			ClientStatus.this.setResult(RESULT_OK, ClientStatus.this.txtCustomMessage.getText().toString(), extras);
			ClientStatus.this.finish();
		}
		
	};
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle icicle)
	{
		this.setContentView(R.layout.status);
		Spinner s1 = (Spinner) this.findViewById(R.id.spinnerStatus);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.status  , android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s1.setAdapter(adapter);
		
		Button b = (Button) this.findViewById(R.id.btnSetStatus);
		b.setOnClickListener(this.mSetStatus);
		
		this.txtCustomMessage = (TextView) this.findViewById(R.id.txtCustom);
		this.spinStatus = (Spinner) this.findViewById(R.id.spinnerStatus);
		this.chkCustomStatus = (CheckBox) this.findViewById(R.id.chkCustom);

		super.onCreate(icicle);
	}	
}
