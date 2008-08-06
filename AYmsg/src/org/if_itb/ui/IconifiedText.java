package org.if_itb.ui;

import org.if_itb.client.AYmsgBuddy;
import org.if_itb.client.AYmsgGroup;
import android.graphics.drawable.Drawable;

/** @author Steven Osborn - http://steven.bitsetters.com */
public class IconifiedText implements Comparable {
   
	public static final byte TYPE_GROUP = 0;
	public static final byte TYPE_BUDDY = 1;
	private AYmsgBuddy buddy;
	private AYmsgGroup group;
	
	private String mText = "";
    private Drawable mIcon;
    private boolean mSelectable = true;
    private int paddingLeft = 2;
	
	/**
	 * The type, Group or Buddy
	 */
	private byte type;

	public IconifiedText(String text, Drawable bullet) {
          this.mIcon = bullet;
          this.mText = text;
     }

	public IconifiedText(String text, Drawable bullet, int paddingLeft) {
		this.mIcon = bullet;
		this.mText = text;
		this.paddingLeft = paddingLeft;
	}
    
	/** Make IconifiedText comparable by its name */
     public int compareTo(Object o) {
    	 IconifiedText other = (IconifiedText) o;
          if(this.mText != null)
               return this.mText.compareTo(other.getText());
          else
               throw new IllegalArgumentException();
     }

	/**
	 * @return the buddy
	 */
	public AYmsgBuddy getBuddy()
	{
		return this.buddy;
	}

	/**
	 * @return the group
	 */
	public AYmsgGroup getGroup()
	{
		return this.group;
	}

	public Drawable getIcon() {
          return this.mIcon;
     }

	
     
     public int getPaddingLeft()
	{
		return this.paddingLeft;
	}

	public String getText() {
          return this.mText;
     }

	/**
	 * @return the type
	 */
	public byte getType()
	{
		return this.type;
	}

	public boolean isSelectable() {
          return this.mSelectable;
     }
     
     /**
	 * @param buddy the buddy to set
	 */
	public void setBuddy(AYmsgBuddy buddy)
	{
		this.buddy = buddy;
	}
     
     
     
     /**
	 * @param group the group to set
	 */
	public void setGroup(AYmsgGroup group)
	{
		this.group = group;
	}
     
     public void setIcon(Drawable icon) {
          this.mIcon = icon;
     }
     
     public void setPaddingLeft(int paddingLeft)
	{
		this.paddingLeft = paddingLeft;
	}
     
     public void setSelectable(boolean selectable) {
          this.mSelectable = selectable;
     }
     
     public void setText(String text) {
          this.mText = text;
     }
     
     /**
	 * @param type the type to set
	 */
	public void setType(byte type)
	{
		this.type = type;
	}
  
} 
