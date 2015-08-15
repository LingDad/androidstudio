package net.jianweixu.listennews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyAdapter extends SimpleAdapter{
	   private int mPositon;
	   private int mResource;
	   private int mCurNewsIndex;
	   //private Context context;
	   private ArrayList<HashMap<String, Object>> mData;
	   private LayoutInflater mInflater;
	   public MyAdapter(Context context, ArrayList<HashMap<String, Object>> listData, int resource, String[] from, int[] to, int Index) { 
		    
	        super(context, listData, resource, from, to);  
	        Log.e("adp", "0");
	        this.mResource = resource;
	        Log.e("adp", "1");
	        this.mData = listData;
	        Log.e("adp", "2");
	        this.mCurNewsIndex = Index;
	        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        Log.e("adp", "3");
	    }
	  
	   @Override
	   public View getView(int position, View convertView, ViewGroup parentView){
		   Log.e("adp", "0 mdata: " + mData);
		   View view = mInflater.inflate(mResource, null);
		   ImageView titleArrow = (ImageView) view.findViewById(R.id.arrow);
		   titleArrow.setBackgroundResource((Integer) mData.get(position).get("playpic"));
		   titleArrow.setVisibility(View.INVISIBLE);
		   
		   Log.e("adp", "1");
		   TextView titleIndex = (TextView)view.findViewById(R.id.title_index);
		   titleIndex.setText(mData.get(position).get("titleIndex").toString());
		   Log.e("adp", "2");
		   TextView titleContent = (TextView)view.findViewById(R.id.title_content);
		   titleContent.setText(mData.get(position).get("titleContent").toString());
		   Log.e("adp", "3");
		   
		   if(position==mCurNewsIndex){
			   titleArrow.setVisibility(View.VISIBLE);
			   titleContent.setTextColor(Color.BLACK);
			   titleIndex.setTextColor(Color.BLACK);
			  
		   }
		   return view;
		   
	   }

	   

}
