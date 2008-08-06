/**
 * 
 */
package org.if_itb.ui;

import java.util.Map;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * @author ahmy
 *
 */
public class LogWebView extends WebView
{
	private StringBuffer sbData = new StringBuffer();
	/**
	 * @param context
	 */
	public LogWebView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param inflateParams
	 */
	public LogWebView(Context context, AttributeSet attrs, Map inflateParams)
	{
		super(context, attrs, inflateParams);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param inflateParams
	 * @param defStyle
	 */
	public LogWebView(Context context, AttributeSet attrs, Map inflateParams,
			int defStyle)
	{
		super(context, attrs, inflateParams, defStyle);
		// TODO Auto-generated constructor stub
		
	}
	
	public void append(String text)
	{
		this.sbData.append(text);
		this.loadData(this.sbData.toString(), "text/html", "utf-8");
	}
}
