package org.if_itb.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/** @author Steven Osborn - http://steven.bitsetters.com */
public class IconifiedTextListAdapter extends BaseAdapter {

     /** Remember our context so we can use it when constructing views. */
     private Context mContext;

     private List<IconifiedText> mItems = new ArrayList<IconifiedText>();

     public IconifiedTextListAdapter(Context context) {
          this.mContext = context;
     }

     public void addItem(IconifiedText it) { this.mItems.add(it); }

     @Override
	public boolean areAllItemsSelectable() { return false; }

     /** @return The number of items in the */
     public int getCount() { return this.mItems.size(); }

     public Object getItem(int position) { return this.mItems.get(position); }

     /** Use the array index as a unique id. */
     public long getItemId(int position) {
          return position;
     }

     /** @param convertView The old view to overwrite, if one is passed
      * @returns a IconifiedTextView that holds wraps around an IconifiedText */
     public View getView(int position, View convertView, ViewGroup parent) {
          IconifiedTextView btv;
          if (convertView == null) {
               btv = new IconifiedTextView(this.mContext, this.mItems.get(position));
          } else { // Reuse/Overwrite the View passed
               // We are assuming(!) that it is castable!
               btv = (IconifiedTextView) convertView;
               btv.setText(this.mItems.get(position).getText());
               btv.setIcon(this.mItems.get(position).getIcon());
          }
          return btv;
     }

     @Override
	public boolean isSelectable(int position) {
          try{
               return this.mItems.get(position).isSelectable();
          }catch (IndexOutOfBoundsException aioobe){
               return super.isSelectable(position);
          }
     }

     public void setListItems(List<IconifiedText> lit) { this.mItems = lit; }
}