/**
 * 
 */
package org.if_itb.ui;

import java.util.Map;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author Ahmy Yulrizka ahmy135 [at] gmail [dot] com
 * Custom Layout to save buddy id information
 */
public class MyTab extends RelativeLayout
{
	private String buddyId;
	
	/**
	 * @param context
	 * @param attrs
	 * @param inflateParams
	 */
	public MyTab(Context context, AttributeSet attrs, Map inflateParams)
	{
		super(context, attrs, inflateParams);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param inflateParams
	 * @param defStyle
	 */
	public MyTab(Context context, AttributeSet attrs, Map inflateParams,
			int defStyle)
	{
		super(context, attrs, inflateParams, defStyle);
	}

	/**
	 * @param arg0
	 */
	public MyTab(Context arg0, String buddyId)
	{
		super(arg0);
		this.buddyId = buddyId;
	}

	/**
	 * @return the buddyID
	 */
	public String getBuddyId()
	{
		return this.buddyId;
	}

	/**
	 * @param buddyID the buddyID to set
	 */
	public void setBuddyId(String buddyID)
	{
		this.buddyId = buddyID;
	}
}
