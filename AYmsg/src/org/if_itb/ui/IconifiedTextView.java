package org.if_itb.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconifiedTextView extends LinearLayout
{
	
	
	private TextView mText;
	private ImageView mIcon;
	

	public IconifiedTextView(Context context, IconifiedText aIconifiedText)
	{
		super(context);
		/* First Icon and the Text to the right (horizontal),
		 * not above and below (vertical) */
		this.setOrientation(HORIZONTAL);
		this.mIcon = new ImageView(context);
		this.mIcon.setImageDrawable(aIconifiedText.getIcon());
		// left, top, right, bottom
		this.mIcon.setPadding(aIconifiedText.getPaddingLeft(), 4, 5, 0); // 5px to the right
		/* At first, add the Icon to ourself
		 * (! we are extending LinearLayout) */
		this.addView(this.mIcon, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.mText = new TextView(context);
		this.mText.setTextColor(android.graphics.Color.BLACK);
		this.mText.setTypeface(Typeface.DEFAULT_BOLD);
		this.mText.setText(aIconifiedText.getText());
		this.mText.setAlignment(Alignment.ALIGN_CENTER);
		this.mText.setPadding(0, 10, 0, 10);
		this.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		/* Now the text (after the icon) */
		this.addView(this.mText, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
	}

	public void setIcon(Drawable bullet)
	{
		this.mIcon.setImageDrawable(bullet);
	}

	public void setText(String words)
	{
		this.mText.setText(words);
	}

	
}
