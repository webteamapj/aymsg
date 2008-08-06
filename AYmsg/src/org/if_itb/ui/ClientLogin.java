package org.if_itb.ui;

import org.if_itb.client.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ClientLogin extends Activity
{

	OnClickListener mLogin = new OnClickListener() {

		public void onClick(View arg0)
		{
			Intent intent = new Intent(ClientLogin.this, ClientMain.class);

			EditText user = (EditText) ClientLogin.this.findViewById(R.id.des_name);
			EditText password = (EditText) ClientLogin.this.findViewById(R.id.des_pwd);
			
    		Bundle b = new Bundle();
            b.putString("user", user.getText().toString());
            b.putString("pass", password.getText().toString());
            intent.putExtras(b); 
            
    		ClientLogin.this.startSubActivity(intent,1);
		}
	};
	@Override
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		this.setContentView(R.layout.login);
		Button b= (Button) this.findViewById(R.id.login);

		b.setOnClickListener(this.mLogin);
	}
}
