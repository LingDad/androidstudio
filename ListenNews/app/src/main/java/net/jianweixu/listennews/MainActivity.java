package net.jianweixu.listennews;


import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;

import java.io.InputStream;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnNetworkStateChangedListener, OnViewChangeListener, DownloadSizeListener, CircleProgressBarListener{
	private int preIndex = 0;
	private boolean firstEnterIndex1 = true;
	private boolean mStatePause;
	private int mPosition;
	private PopupWindow mPopupWindow;
	private boolean internationalDownloading = false;
	private boolean nationalDownloading = false;
	private boolean nativeDownloading = false;
	private boolean financeDownloading = false;
	private boolean entertainmentDownloading = false;
	private boolean sportDownloading = false;
	private boolean internationalCached = false;
	private boolean nationalCached = false;
	private boolean nativeCached = false;
	private boolean financeCached = false;
	private boolean entertainmentCached = false;
	private boolean sportCached = false;
	private Button internationalButton;
	private int mInternationalUndownloadCount;
	private int mNationalUndownloadCount;
	private int mNativeUndownloadCount;
	private int mFinanceUndownloadCount;
	private int mEntertainmentUndownloadCount;
	private int mSportUndownloadCount;
	private int mInternationalProgressStep;
	private int mNationalProgressStep;
	private int mNativeProgressStep;
	private int mFinanceProgressStep;
	private int mEntertainmentProgressStep;
	private int mSportProgressStep;
	private ArrayList<String> sizeList; 
	private boolean isFirstPress = true;
	private String international_path;
	private String national_path;
	private List<AVObject> international_obj_list;
	private List<AVObject> national_obj_list;
	private List<AVObject> native_obj_list;
	private List<AVObject> finance_obj_list;
	private List<AVObject> entertainment_obj_list;
	private List<AVObject> sport_obj_list;
	private int dSize = 0;
	private long dTime = 0;
	private int totalSpace = 0;
	private boolean TrueIndex;
	private String gen_path;
	private String gen_path_parent;
	public String TAG = "MainActivity";
	public int mcurScreenIndex;
	private MyAdapter listItemAdapter;
	ArrayList<HashMap<String, Object>> listData;
	ArrayList<HashMap<String, String>> urlArrayList;
	
	public boolean international_state = true;
	public boolean national_state = true;
	public boolean download_done = false;
	private MediaPlayer mediaPlayer;
	private ArrayList<String> newsArrayList;
	private ArrayList<String> downloadFinishedArrayList;
	public int newsIndex = 0;
	private int pathIndex = 0;
	
	public ProgressBar progressBar;
	public int progressState;
	private static final int msgKey1 = 1;
	private FlingGalleryView mFlingGalleryView;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//initialize the lean cloud
		AVOSCloud.initialize(this, "mu6jm2hrbxali7wkyhqtp7hqh71zkowkf3q229gypkl44zey", "rafjl9o2ctukuxd7v1gr7ej92wz1myuh070yxqjiziht8q57");
		init();
		//Intent mFilter = new Intent(this,NetworkStateService.class);
		//startService(mFilter);
		//initPopUpWindow();
		
		mediaPlayer = new MediaPlayer();	
		
		//initialize play buttonls
		buttonClickListener listener = new buttonClickListener();
		ImageButton playButton = (ImageButton)findViewById(R.id.play_button);
		playButton.setOnClickListener(listener);
		//initialize download button
		Button downloadWholeButton = (Button)findViewById(R.id.download_whole);
		downloadWholeButton.setOnClickListener(listener);
		//initialize forward button
		ImageButton forwardButton = (ImageButton)findViewById(R.id.forward_button);
		forwardButton.setOnClickListener(listener);
		//initialize backward button
		ImageButton backwardButton = (ImageButton)findViewById(R.id.backward_button);
		backwardButton.setOnClickListener(listener);
		//initialize international button
		LinearLayout internationalButton = (LinearLayout)findViewById(R.id.international_news);
		internationalButton.setOnClickListener(listener);
		//initialize national button
		LinearLayout nationalButton = (LinearLayout)findViewById(R.id.national_news);
		nationalButton.setOnClickListener(listener);
		//initialize native button
		LinearLayout nativeButton = (LinearLayout)findViewById(R.id.native_news);
		nativeButton.setOnClickListener(listener);
		//initialize finance button
		LinearLayout financeButton = (LinearLayout)findViewById(R.id.finance_news);
		financeButton.setOnClickListener(listener);
		//initialize entertainment button
		LinearLayout entertainmentButton = (LinearLayout)findViewById(R.id.entertainment_news);
		entertainmentButton.setOnClickListener(listener);
		//initialize sport button
		LinearLayout sportButton = (LinearLayout)findViewById(R.id.sport_news);
		sportButton.setOnClickListener(listener);
		
		//ImageButton shareButton = (ImageButton)findViewById(R.id.share_button);
		//shareButton.setOnClickListener(listener);
		
		newsArrayList = new ArrayList<String>();
		
		//generate download finished array list
		downloadFinishedArrayList = new ArrayList<String>();	
		downloadFinishedArrayList.add("/sdcard/subwaynews_International/international_");
		downloadFinishedArrayList.add("/sdcard/subwaynews_Domestic/domestic_");
		downloadFinishedArrayList.add("/sdcard/subwaynews_Native/native_");
		downloadFinishedArrayList.add("/sdcard/subwaynews_Finance/finance_");
		downloadFinishedArrayList.add("/sdcard/subwaynews_Entertainment/entertainment_");
		downloadFinishedArrayList.add("/sdcard/subwaynews_Sport/sport_");
		Log.e(TAG, "begin......");
		
		queryChannelList();
		
	}
	
/* ...InBackgroud in lean cloud is still in UI thread, so u can update the view but can not do something related network*/
public void queryChannelList(){
	AVQuery<AVObject> query = new AVQuery<AVObject>("Channel");
	
	query.findInBackground(new FindCallback<AVObject>(){

		@Override
		public void done(List<AVObject> resultList, AVException cqlException) {
			
			// TODO Auto-generated method stub
			if(cqlException == null){
				for(int listCount=0; listCount<resultList.size(); listCount++){
					AVObject mChannel = resultList.get(listCount);
					String channelName = mChannel.getString("name");
					String channelSubtitle = mChannel.getString("subTitle");
					Log.e(TAG, "channelName and channelSubtitle are: " + channelName + channelSubtitle);
					queryNewsList(mChannel,channelName);
					setChannelInfo(listCount, channelName, channelSubtitle);	
					Log.e(TAG, "listcount is: " + listCount);
					final int channelNo = listCount;
					Log.e(TAG, "chanaleNo is: " + channelNo);
					
					//fetch every channel's background image
					mChannel.fetchInBackground(new GetCallback<AVObject>(){

						@Override
						public void done(AVObject arg0, AVException arg1) {
							// TODO Auto-generated method stub
							String imageUrl = arg0.getAVFile("image").getUrl();							
							new mImageUrlThread(channelNo, imageUrl).start();
							Log.e(TAG,"Image URL is: " + imageUrl);
						}
						
					});
				}
				
			}
		}
		
	});
}	

public void setChannelInfo(int listCount, String channelName, String channelSubtitle){
	switch(listCount){
	case 0:
		TextView channelNameView_0 = (TextView)findViewById(R.id.channel_name_0);		
		channelNameView_0.setText(channelName);
		TextView channelSubtitleView_0 = (TextView)findViewById(R.id.channel_subtitle_0);
		channelSubtitleView_0.setText(channelSubtitle);
		break;
	case 1:
		TextView channelNameView_1 = (TextView)findViewById(R.id.channel_name_1);
		TextView channelSubtitleView_1 = (TextView)findViewById(R.id.channel_subtitle_1);
		channelNameView_1.setText(channelName);
		channelSubtitleView_1.setText(channelSubtitle);
		break;
	case 2:
		TextView channelNameView_2 = (TextView)findViewById(R.id.channel_name_2);
		TextView channelSubtitleView_2 = (TextView)findViewById(R.id.channel_subtitle_2);
		channelNameView_2.setText(channelName);
		channelSubtitleView_2.setText(channelSubtitle);
		break;
	case 3:
		TextView channelNameView_3 = (TextView)findViewById(R.id.channel_name_3);
		TextView channelSubtitleView_3 = (TextView)findViewById(R.id.channel_subtitle_3);
		channelNameView_3.setText(channelName);
		channelSubtitleView_3.setText(channelSubtitle);
		break;
	case 4:
		TextView channelNameView_4 = (TextView)findViewById(R.id.channel_name_4);
		TextView channelSubtitleView_4 = (TextView)findViewById(R.id.channel_subtitle_4);
		channelNameView_4.setText(channelName);
		channelSubtitleView_4.setText(channelSubtitle);
		break;
	case 5:
		TextView channelNameView_5 = (TextView)findViewById(R.id.channel_name_5);
		TextView channelSubtitleView_5 = (TextView)findViewById(R.id.channel_subtitle_5);
		channelNameView_5.setText(channelName);
		channelSubtitleView_5.setText(channelSubtitle);
		break;
	}
}

public class mImageUrlThread extends Thread{
	private String imageUrl;
	private int listCount;
	
	mImageUrlThread(int listCount, String url){
		this.imageUrl = url;
		this.listCount = listCount;
	}
	
	@Override
	public void run(){
		URL mImageUrl = null;
		
		try {
			mImageUrl = new URL(imageUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) mImageUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			Bitmap bmp = BitmapFactory.decodeStream(is);
			is.close();
			Message msg = new Message();
			msg.obj = bmp;
			msg.what = listCount;
			mImageUrlHandler.sendMessage(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

@SuppressLint("NewApi")
public Handler mImageUrlHandler = new Handler(){
	@Override
	public void handleMessage(Message msg){
		 
		Bitmap reBmp = (Bitmap) msg.obj;
		Resources res = getResources();
		Drawable mDrawable = new BitmapDrawable(res,reBmp);
		
		switch(msg.what){
		case 0:
			LinearLayout channelLayout_0 = (LinearLayout)findViewById(R.id.international_news);
			channelLayout_0.setBackground(mDrawable);
			break;
		case 1:
			LinearLayout channelLayout_1 = (LinearLayout)findViewById(R.id.national_news);
			channelLayout_1.setBackground(mDrawable);
			break;
		case 2:
			LinearLayout channelLayout_2 = (LinearLayout)findViewById(R.id.native_news);
			channelLayout_2.setBackground(mDrawable);
			break;
		case 3:
			LinearLayout channelLayout_3 = (LinearLayout)findViewById(R.id.finance_news);
			channelLayout_3.setBackground(mDrawable);
			break;
		case 4:
			LinearLayout channelLayout_4 = (LinearLayout)findViewById(R.id.entertainment_news);
			channelLayout_4.setBackground(mDrawable);
			break;
		case 5:
			LinearLayout channelLayout_5 = (LinearLayout)findViewById(R.id.sport_news);
			channelLayout_5.setBackground(mDrawable);
			break;
		//case 2 3 4 5 set channel background image
		}
	}
};


public void queryNewsList(AVObject channel, String channelName){
	Log.e(TAG, "query news list xxxxx");
	final String myChannelName = channelName;
	AVObject myChannel = channel;
	AVQuery<AVObject> query = AVQuery.getQuery("News");
	query.whereEqualTo("channel", myChannel);
	query.findInBackground(new FindCallback<AVObject>(){
		public void done(List<AVObject> newsList, AVException e){
			//find all international news
			Log.e(TAG, "bbbbbbbbbbbb");
			if(myChannelName.contains("国际")){ 
				// computer InternationalProgressStep
				international_obj_list = newsList;
				if(100%international_obj_list.size()==0){
					Log.e(TAG, "bbbbbbbnnnnnnmmmmm");
					mInternationalProgressStep = 100/international_obj_list.size();
				}else{
					Log.e(TAG, "ppppppppppppp");
					mInternationalProgressStep = 100/international_obj_list.size()+1;
				}
				//judge if files download
				if(alreadyDownload(international_obj_list.size(), "国际")==false){
					internationalCached = false;
					Log.e(TAG, "alreaddownload false");
					ImageView iv = (ImageView)findViewById(R.id.clock_0);
					iv.setVisibility(View.VISIBLE);
				}else{
					Log.e(TAG, "alreaddownload true");
					internationalCached = true;
					gen_path = downloadFinishedArrayList.get(0);
					queryListView();
					querySource();
					queryNewsContent();
					
					/*query first image URL*/
					international_obj_list.get(0).fetchInBackground(new GetCallback<AVObject>(){

						@Override
						public void done(AVObject arg0, AVException arg1) {
							// TODO Auto-generated method stub
							String firstImgUrlString = arg0.getAVFile("image").getUrl();
							Log.e(TAG, "first img url is: " + firstImgUrlString);
							new mCntImageUrlThread(0,firstImgUrlString).start();
							
						}				
					});
					/*-----------------------------*/									
				}
				
				Log.e(TAG, "international_obj_list are: " + international_obj_list + " SIZE XXX: " + international_obj_list.size());
			}
			
			//find all national news
			if(myChannelName.contains("国内")){ 
				
				national_obj_list = newsList;
				Log.e(TAG, "national_obj_list: " + national_obj_list);
				if(100%national_obj_list.size()==0){
					mNationalProgressStep = 100/national_obj_list.size();
				}else{
					mNationalProgressStep = 100/national_obj_list.size()+1;
				}
				
				if(alreadyDownload(national_obj_list.size(), "国内")==false){
					nationalCached = false;
					Log.e(TAG, "alreaddownload false");
					ImageView iv = (ImageView)findViewById(R.id.clock_1);
					iv.setVisibility(View.VISIBLE);
				}else{
					Log.e(TAG, "alreaddownload true");
					nationalCached = true;
				}
				Log.e(TAG, "national_obj_list are: " + national_obj_list);
			}
			
			//find all local news
				if(myChannelName.contains("北京")){ 
				
					native_obj_list = newsList;
					Log.e(TAG, "native_obj_list: " + native_obj_list);
				if(100%native_obj_list.size()==0){
					mNativeProgressStep = 100/native_obj_list.size();
				}else{
					mNativeProgressStep = 100/native_obj_list.size()+1;
				}
				
				if(alreadyDownload(native_obj_list.size(), "北京")==false){
					nativeCached = false;
					Log.e(TAG, "alreaddownload false");
					ImageView iv = (ImageView)findViewById(R.id.clock_2);
					iv.setVisibility(View.VISIBLE);
				}else{
					Log.e(TAG, "alreaddownload true");
					nativeCached = true;
				}
				Log.e(TAG, "native_obj_list are: " + native_obj_list);
			}
			//find all finance news
				if(myChannelName.contains("财经")){ 
					
					finance_obj_list = newsList;
					
				if(100%finance_obj_list.size()==0){
					mFinanceProgressStep = 100/finance_obj_list.size();
				}else{
					mFinanceProgressStep = 100/finance_obj_list.size()+1;
				}
				
				if(alreadyDownload(finance_obj_list.size(), "财经")==false){
					financeCached = false;
					Log.e(TAG, "alreaddownload false");
					ImageView iv = (ImageView)findViewById(R.id.clock_3);
					iv.setVisibility(View.VISIBLE);
				}else{
					Log.e(TAG, "alreaddownload true");
					financeCached = true;
				}
				Log.e(TAG, "finance_obj_list are: " + finance_obj_list);
			}
			//find all entertainment news
				if(myChannelName.contains("娱乐")){ 
					
					entertainment_obj_list = newsList;
				if(100%entertainment_obj_list.size()==0){
					mEntertainmentProgressStep = 100/entertainment_obj_list.size();
				}else{
					mEntertainmentProgressStep = 100/entertainment_obj_list.size()+1;
				}
				
				if(alreadyDownload(entertainment_obj_list.size(), "娱乐")==false){
					entertainmentCached = false;
					Log.e(TAG, "alreaddownload false");
					ImageView iv = (ImageView)findViewById(R.id.clock_4);
					iv.setVisibility(View.VISIBLE);
				}else{
					Log.e(TAG, "alreaddownload true");
					entertainmentCached = true;
				}
				Log.e(TAG, "entertainment_obj_list are: " + entertainment_obj_list);
			}
			//find all sport news
				if(myChannelName.contains("体育")){ 
					
					sport_obj_list = newsList;
				if(100%sport_obj_list.size()==0){
					mSportProgressStep = 100/sport_obj_list.size();
				}else{
					mSportProgressStep = 100/sport_obj_list.size()+1;
				}
				
				if(alreadyDownload(sport_obj_list.size(), "体育")==false){
					sportCached = false;
					Log.e(TAG, "alreaddownload false");
					ImageView iv = (ImageView)findViewById(R.id.clock_5);
					iv.setVisibility(View.VISIBLE);
				}else{
					Log.e(TAG, "alreaddownload true");
					sportCached = true;
					Log.e(TAG, "alreaddownload true: " + sportCached);
				}
				Log.e(TAG, "sport_obj_list are: " + sport_obj_list);
			}
			Log.e(TAG, "newsList are: " + newsList);
			// set here	
			Log.e(TAG, "WWWWWWW: " + internationalCached + nationalCached + nativeCached + financeCached + entertainmentCached + sportCached);
			if( true == internationalCached && nationalCached && nativeCached && financeCached && entertainmentCached && sportCached){
				Button mbt = (Button) findViewById(R.id.download_whole);
				mbt.setText("已全部缓存");
				mbt.setBackgroundColor(Color.rgb(105, 186, 106));
				mbt.setClickable(false);
			}
		}
	});
	
	
}

public boolean alreadyDownload(int sum, String str){
	FileUtils mFus = new FileUtils();
	boolean mRes = false;
	if(str.equals("国际")){
	mInternationalUndownloadCount = 0;
	for(int num=1; num<=sum; num++){
		String testStr = "/sdcard/subwaynews_International/international_" + num + ".mp3";	
		Log.e(TAG, "testStr:" + testStr);
		mRes = mFus.isDirExist(testStr);
		if(mRes==false){
			mInternationalUndownloadCount++;
		}
	}
	}
	if(str.equals("国内")){
		mNationalUndownloadCount = 0;
		for(int num=1; num<=sum; num++){
			String testStr = "/sdcard/subwaynews_Domestic/domestic_" + num + ".mp3";	
			Log.e(TAG, "testStr:" + testStr);
			mRes = mFus.isDirExist(testStr);
			if(mRes==false){
				mNationalUndownloadCount++;
			}
		}
		}
	if(str.equals("北京")){
		mNativeUndownloadCount = 0;
		for(int num=1; num<=sum; num++){
			String testStr = "/sdcard/subwaynews_Native/native_" + num + ".mp3";	
			Log.e(TAG, "testStr:" + testStr);
			mRes = mFus.isDirExist(testStr);
			if(mRes==false){
				mNativeUndownloadCount++;
			}
		}
		}
	if(str.equals("财经")){
		mFinanceUndownloadCount = 0;
		for(int num=1; num<=sum; num++){
			String testStr = "/sdcard/subwaynews_Finance/finance_" + num + ".mp3";	
			Log.e(TAG, "testStr:" + testStr);
			mRes = mFus.isDirExist(testStr);
			if(mRes==false){
				mFinanceUndownloadCount++;
			}
		}
		}
	if(str.equals("娱乐")){
		mEntertainmentUndownloadCount = 0;
		for(int num=1; num<=sum; num++){
			String testStr = "/sdcard/subwaynews_Entertainment/entertainment_" + num + ".mp3";	
			Log.e(TAG, "testStr:" + testStr);
			mRes = mFus.isDirExist(testStr);
			if(mRes==false){
				mEntertainmentUndownloadCount++;
			}
		}
		}
	if(str.equals("体育")){
		mSportUndownloadCount = 0;
		for(int num=1; num<=sum; num++){
			String testStr = "/sdcard/subwaynews_Sport/sport_" + num + ".mp3";	
			Log.e(TAG, "testStr:" + testStr);
			mRes = mFus.isDirExist(testStr);
			if(mRes==false){
				mSportUndownloadCount++;
			}
		}
		}
	return mRes;
}

//set title region
public void setTitleReg(){
	
	TextView mSub = (TextView) findViewById(R.id.subtitle_show);
	mSub.setText("今日热点");
	TextView mSub1 = (TextView) findViewById(R.id.subtitle_show3);
	mSub1.setText("今日热点");
	if(queryKeyMethod().equals("international")){
		Log.e(TAG, "here set location: " + queryKeyMethod());
		TextView titleShow = (TextView)findViewById(R.id.title_show);
		titleShow.setText("国际");
		TextView titleShowm = (TextView)findViewById(R.id.title_show_m);
		titleShowm.setText("国际");
	}
	if(queryKeyMethod().equals("domestic")){
		TextView titleShow = (TextView)findViewById(R.id.title_show);
		titleShow.setText("国内");
		TextView titleShowm = (TextView)findViewById(R.id.title_show_m);
		titleShowm.setText("国内");
	}
	if(queryKeyMethod().equals("native")){
		TextView titleShow = (TextView)findViewById(R.id.title_show);
		titleShow.setText("北京");
		TextView titleShowm = (TextView)findViewById(R.id.title_show_m);
		titleShowm.setText("北京");
	}
	if(queryKeyMethod().equals("finance")){
		TextView titleShow = (TextView)findViewById(R.id.title_show);
		titleShow.setText("财经");
		TextView titleShowm = (TextView)findViewById(R.id.title_show_m);
		titleShowm.setText("财经");
	}
	if(queryKeyMethod().equals("entertainment")){
		TextView titleShow = (TextView)findViewById(R.id.title_show);
		titleShow.setText("娱乐");
		TextView titleShowm = (TextView)findViewById(R.id.title_show_m);
		titleShowm.setText("娱乐");
	}
	if(queryKeyMethod().equals("sport")){
		TextView titleShow = (TextView)findViewById(R.id.title_show);
		titleShow.setText("体育");
		TextView titleShowm = (TextView)findViewById(R.id.title_show_m);
		titleShowm.setText("体育");
	}
}

//query source
public void querySource(){
	ArrayList<String> sourceArray = new ArrayList<String>();
	
	if(queryKeyMethod().equals("international")){
	for(int urlListCount=0; urlListCount<international_obj_list.size(); urlListCount++){
		String international_source = international_obj_list.get(urlListCount).getString("source");
		sourceArray.add(international_source);
	}
	Log.e(TAG, "source array data are: " + sourceArray);
	
	TextView sourceContent = (TextView)findViewById(R.id.title_source);
	sourceContent.setText(sourceArray.get(newsIndex));
	}
	
	if(queryKeyMethod().equals("domestic")){
		for(int urlListCount=0; urlListCount<national_obj_list.size(); urlListCount++){
			String national_source = national_obj_list.get(urlListCount).getString("source");
			sourceArray.add(national_source);
		}
		Log.e(TAG, "source array data are: " + sourceArray);
		
		TextView sourceContent = (TextView)findViewById(R.id.title_source);
		sourceContent.setText(sourceArray.get(newsIndex));
		}
	
	if(queryKeyMethod().equals("native")){
		for(int urlListCount=0; urlListCount<native_obj_list.size(); urlListCount++){
			String native_source = native_obj_list.get(urlListCount).getString("source");
			sourceArray.add(native_source);
		}
		Log.e(TAG, "source array data are: " + sourceArray);
		
		TextView sourceContent = (TextView)findViewById(R.id.title_source);
		sourceContent.setText(sourceArray.get(newsIndex));
		}
	
	if(queryKeyMethod().equals("finance")){
		for(int urlListCount=0; urlListCount<finance_obj_list.size(); urlListCount++){
			String finance_source = finance_obj_list.get(urlListCount).getString("source");
			sourceArray.add(finance_source);
		}
		Log.e(TAG, "source array data are: " + sourceArray);
		
		TextView sourceContent = (TextView)findViewById(R.id.title_source);
		sourceContent.setText(sourceArray.get(newsIndex));
		}
	
	if(queryKeyMethod().equals("entertainment")){
		for(int urlListCount=0; urlListCount<entertainment_obj_list.size(); urlListCount++){
			String entertainment_source = entertainment_obj_list.get(urlListCount).getString("source");
			sourceArray.add(entertainment_source);
		}
		Log.e(TAG, "source array data are: " + sourceArray);
		
		TextView sourceContent = (TextView)findViewById(R.id.title_source);
		sourceContent.setText(sourceArray.get(newsIndex));
		}
	
	if(queryKeyMethod().equals("sport")){
		for(int urlListCount=0; urlListCount<sport_obj_list.size(); urlListCount++){
			String sport_source = sport_obj_list.get(urlListCount).getString("source");
			sourceArray.add(sport_source);
		}
		Log.e(TAG, "source array data are: " + sourceArray);
		
		TextView sourceContent = (TextView)findViewById(R.id.title_source);
		sourceContent.setText(sourceArray.get(newsIndex));
		}
}



//query news list
public void queryListView(){
	String keyStr = queryKeyMethod();
	Log.e(TAG, "keystring is: " + keyStr);
	if(keyStr.equalsIgnoreCase("international")){
	ArrayList<String> titleArray = new ArrayList<String>();
	
	ArrayList<HashMap<String,Object>> titleArrayList = new ArrayList<HashMap<String,Object>>();
	for(int urlListCount=0; urlListCount<international_obj_list.size(); urlListCount++){
		HashMap<String,Object> titleMap = new HashMap<String,Object>();
		String international_str = international_obj_list.get(urlListCount).getString("title");		
		titleArray.add(international_str);
		String titleIndex = String.valueOf(urlListCount+1) + ".";
		titleMap.put("titleContent", international_str);
		titleMap.put("titleIndex", titleIndex);
		titleMap.put("playpic", R.drawable.pplay);
		//Log.e(TAG, "title map are" + titleMap);
		titleArrayList.add(titleMap);
		Log.e(TAG, "title array is: " + titleArray);
		}
	listData = titleArrayList;
	Log.e(TAG, "!!!!!!!List data are: " + listData);
	setListView();
	TextView titleContentMain = (TextView)findViewById(R.id.title_c);
	titleContentMain.setText(titleArray.get(newsIndex));
	setTitleReg();
	}
	if(keyStr.equalsIgnoreCase("domestic")){
		ArrayList<String> titleArray = new ArrayList<String>();
		
		ArrayList<HashMap<String,Object>> titleArrayList = new ArrayList<HashMap<String,Object>>();
		for(int urlListCount=0; urlListCount<national_obj_list.size(); urlListCount++){
			HashMap<String,Object> titleMap = new HashMap<String,Object>();
			String national_str = national_obj_list.get(urlListCount).getString("title");		
			titleArray.add(national_str);
			String titleIndex = String.valueOf(urlListCount+1) + ".";
			titleMap.put("titleContent", national_str);
			titleMap.put("titleIndex", titleIndex);
			titleMap.put("playpic", R.drawable.pplay);
			//Log.e(TAG, "title map are" + titleMap);
			titleArrayList.add(titleMap);
			Log.e(TAG, "title array is: " + titleArray);
			}
		listData = titleArrayList;
		Log.e(TAG, "!!!!!!!List data are: " + listData);
		setListView();
		TextView titleContentMain = (TextView)findViewById(R.id.title_c);
		titleContentMain.setText(titleArray.get(newsIndex));
		setTitleReg();
	}
	if(keyStr.equalsIgnoreCase("native")){
		ArrayList<String> titleArray = new ArrayList<String>();
		
		ArrayList<HashMap<String,Object>> titleArrayList = new ArrayList<HashMap<String,Object>>();
		for(int urlListCount=0; urlListCount<native_obj_list.size(); urlListCount++){
			HashMap<String,Object> titleMap = new HashMap<String,Object>();
			String native_str = native_obj_list.get(urlListCount).getString("title");		
			titleArray.add(native_str);
			String titleIndex = String.valueOf(urlListCount+1) + ".";
			titleMap.put("titleContent", native_str);
			titleMap.put("titleIndex", titleIndex);
			titleMap.put("playpic", R.drawable.pplay);
			//Log.e(TAG, "title map are" + titleMap);
			titleArrayList.add(titleMap);
			Log.e(TAG, "title array is: " + titleArray);
			}
		listData = titleArrayList;
		Log.e(TAG, "!!!!!!!List data are: " + listData);
		setListView();
		TextView titleContentMain = (TextView)findViewById(R.id.title_c);
		titleContentMain.setText(titleArray.get(newsIndex));
		setTitleReg();
	}
	if(keyStr.equalsIgnoreCase("finance")){
		ArrayList<String> titleArray = new ArrayList<String>();
		
		ArrayList<HashMap<String,Object>> titleArrayList = new ArrayList<HashMap<String,Object>>();
		for(int urlListCount=0; urlListCount<finance_obj_list.size(); urlListCount++){
			HashMap<String,Object> titleMap = new HashMap<String,Object>();
			String finance_str = finance_obj_list.get(urlListCount).getString("title");		
			titleArray.add(finance_str);
			String titleIndex = String.valueOf(urlListCount+1) + ".";
			titleMap.put("titleContent", finance_str);
			titleMap.put("titleIndex", titleIndex);
			titleMap.put("playpic", R.drawable.pplay);
			//Log.e(TAG, "title map are" + titleMap);
			titleArrayList.add(titleMap);
			Log.e(TAG, "title array is: " + titleArray);
			}
		listData = titleArrayList;
		Log.e(TAG, "!!!!!!!List data are: " + listData);
		setListView();
		TextView titleContentMain = (TextView)findViewById(R.id.title_c);
		titleContentMain.setText(titleArray.get(newsIndex));
		setTitleReg();
	}
	if(keyStr.equalsIgnoreCase("entertainment")){
		ArrayList<String> titleArray = new ArrayList<String>();
		
		ArrayList<HashMap<String,Object>> titleArrayList = new ArrayList<HashMap<String,Object>>();
		for(int urlListCount=0; urlListCount<entertainment_obj_list.size(); urlListCount++){
			HashMap<String,Object> titleMap = new HashMap<String,Object>();
			String entertainment_str = entertainment_obj_list.get(urlListCount).getString("title");		
			titleArray.add(entertainment_str);
			String titleIndex = String.valueOf(urlListCount+1) + ".";
			titleMap.put("titleContent", entertainment_str);
			titleMap.put("titleIndex", titleIndex);
			titleMap.put("playpic", R.drawable.pplay);
			//Log.e(TAG, "title map are" + titleMap);
			titleArrayList.add(titleMap);
			Log.e(TAG, "title array is: " + titleArray);
			}
		listData = titleArrayList;
		Log.e(TAG, "!!!!!!!List data are: " + listData);
		setListView();
		TextView titleContentMain = (TextView)findViewById(R.id.title_c);
		titleContentMain.setText(titleArray.get(newsIndex));
		setTitleReg();
	}
	if(keyStr.equalsIgnoreCase("sport")){
		ArrayList<String> titleArray = new ArrayList<String>();
		
		ArrayList<HashMap<String,Object>> titleArrayList = new ArrayList<HashMap<String,Object>>();
		for(int urlListCount=0; urlListCount<sport_obj_list.size(); urlListCount++){
			HashMap<String,Object> titleMap = new HashMap<String,Object>();
			String sport_str = sport_obj_list.get(urlListCount).getString("title");		
			titleArray.add(sport_str);
			String titleIndex = String.valueOf(urlListCount+1) + ".";
			titleMap.put("titleContent", sport_str);
			titleMap.put("titleIndex", titleIndex);
			titleMap.put("playpic", R.drawable.pplay);
			//Log.e(TAG, "title map are" + titleMap);
			titleArrayList.add(titleMap);
			Log.e(TAG, "title array is: " + titleArray);
			}
		listData = titleArrayList;
		Log.e(TAG, "!!!!!!!List data are: " + listData);
		setListView();
		TextView titleContentMain = (TextView)findViewById(R.id.title_c);
		titleContentMain.setText(titleArray.get(newsIndex));
		setTitleReg();
	}
}

//query content
public void queryNewsContent(){
	
	if(queryKeyMethod().equals("international")){
	ArrayList<String> contentArray = new ArrayList<String>();
	for(int urlListCount=0; urlListCount<international_obj_list.size(); urlListCount++){
		String international_content = international_obj_list.get(urlListCount).getString("body");
		contentArray.add(international_content);
	}
	Log.e(TAG, "content query list: " + contentArray);				
	TextView contentView = (TextView)findViewById(R.id.content_item);
	contentView.setText(contentArray.get(newsIndex));
	contentView.setMovementMethod(ScrollingMovementMethod.getInstance());
	}
	
	if(queryKeyMethod().equals("domestic")){
		ArrayList<String> contentArray = new ArrayList<String>();
		for(int urlListCount=0; urlListCount<national_obj_list.size(); urlListCount++){
			String national_content = national_obj_list.get(urlListCount).getString("body");
			contentArray.add(national_content);
		}
		Log.e(TAG, "content query list: " + contentArray);				
		TextView contentView = (TextView)findViewById(R.id.content_item);
		contentView.setText(contentArray.get(newsIndex));
		contentView.setMovementMethod(ScrollingMovementMethod.getInstance());
		}
	
	if(queryKeyMethod().equals("native")){
		ArrayList<String> contentArray = new ArrayList<String>();
		for(int urlListCount=0; urlListCount<native_obj_list.size(); urlListCount++){
			String native_content = native_obj_list.get(urlListCount).getString("body");
			contentArray.add(native_content);
		}
		Log.e(TAG, "content query list: " + contentArray);				
		TextView contentView = (TextView)findViewById(R.id.content_item);
		contentView.setText(contentArray.get(newsIndex));
		contentView.setMovementMethod(ScrollingMovementMethod.getInstance());
		}
	if(queryKeyMethod().equals("finance")){
		ArrayList<String> contentArray = new ArrayList<String>();
		for(int urlListCount=0; urlListCount<finance_obj_list.size(); urlListCount++){
			String finance_content = finance_obj_list.get(urlListCount).getString("body");
			contentArray.add(finance_content);
		}
		Log.e(TAG, "content query list: " + contentArray);				
		TextView contentView = (TextView)findViewById(R.id.content_item);
		contentView.setText(contentArray.get(newsIndex));
		contentView.setMovementMethod(ScrollingMovementMethod.getInstance());
		}
	if(queryKeyMethod().equals("entertainment")){
		ArrayList<String> contentArray = new ArrayList<String>();
		for(int urlListCount=0; urlListCount<entertainment_obj_list.size(); urlListCount++){
			String entertainment_content = entertainment_obj_list.get(urlListCount).getString("body");
			contentArray.add(entertainment_content);
		}
		Log.e(TAG, "content query list: " + contentArray);				
		TextView contentView = (TextView)findViewById(R.id.content_item);
		contentView.setText(contentArray.get(newsIndex));
		contentView.setMovementMethod(ScrollingMovementMethod.getInstance());
		}
	if(queryKeyMethod().equals("sport")){
		ArrayList<String> contentArray = new ArrayList<String>();
		for(int urlListCount=0; urlListCount<sport_obj_list.size(); urlListCount++){
			String sport_content = sport_obj_list.get(urlListCount).getString("body");
			contentArray.add(sport_content);
		}
		Log.e(TAG, "content query list: " + contentArray);				
		TextView contentView = (TextView)findViewById(R.id.content_item);
		contentView.setText(contentArray.get(newsIndex));
		contentView.setMovementMethod(ScrollingMovementMethod.getInstance());
		}
}

@SuppressLint("NewApi")
public long downloadedSize(){
	ArrayList<String> checkPathList = new ArrayList<String>();
	checkPathList.add("/sdcard/Subwaynews_International/");
	checkPathList.add("/sdcard/Subwaynews_Domestic/");
	checkPathList.add("/sdcard/Subwaynews_Native/");
	checkPathList.add("/sdcard/Subwaynews_Finance/");
	checkPathList.add("/sdcard/Subwaynews_Entertainment/");
	checkPathList.add("/sdcard/Subwaynews_Sport/");
	long currentSize =0;
	for(int listCount=0;listCount<checkPathList.size();listCount++){
		File mFile = new File(checkPathList.get(listCount));
		currentSize =+ mFile.getTotalSpace();
	}
	Log.e(TAG, "Current size is: " + currentSize);
	return currentSize;
}

//query news content image xujianwei
public void queryContentImg(){
	if(queryKeyMethod().equals("international")){
		
	for(int urlListCount=0; urlListCount<international_obj_list.size(); urlListCount++){
		final int keyIndex = urlListCount;
		AVObject international_obj = international_obj_list.get(urlListCount);
		Log.e(TAG, "IN querycontentimg international_obj: " + international_obj);
		international_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				
				// TODO Auto-generated method stub				
				String cntImgUrl = arg0.getAVFile("image").getUrl();
				if(keyIndex == newsIndex){
					new mCntImageUrlThread(keyIndex,cntImgUrl).start();
				}
				
			}			
		});
	}
	}
	
	if(queryKeyMethod().equals("domestic")){
		
	for(int urlListCount=0; urlListCount<national_obj_list.size(); urlListCount++){
		final int keyIndex = urlListCount;
		AVObject national_obj = national_obj_list.get(urlListCount);
		national_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub				
				String cntImgUrl = arg0.getAVFile("image").getUrl();
				if(keyIndex == newsIndex){
					new mCntImageUrlThread(keyIndex,cntImgUrl).start();
				}
			}			
		});
	}
	}
	
	if(queryKeyMethod().equals("native")){
		
	for(int urlListCount=0; urlListCount<native_obj_list.size(); urlListCount++){
		final int keyIndex = urlListCount;
		AVObject native_obj = native_obj_list.get(urlListCount);
		native_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub				
				String cntImgUrl = arg0.getAVFile("image").getUrl();	
				if(keyIndex == newsIndex){
					new mCntImageUrlThread(keyIndex,cntImgUrl).start();
				}
			}			
		});
	}
	}

	
	if(queryKeyMethod().equals("finance")){
		
	for(int urlListCount=0; urlListCount<finance_obj_list.size(); urlListCount++){
		final int keyIndex = urlListCount;
		AVObject finance_obj = finance_obj_list.get(urlListCount);
		finance_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub				
				String cntImgUrl = arg0.getAVFile("image").getUrl();
				if(keyIndex == newsIndex){
					new mCntImageUrlThread(keyIndex,cntImgUrl).start();
				}
			}			
		});
	}
	}
	
	if(queryKeyMethod().equals("entertainment")){
		
	for(int urlListCount=0; urlListCount<entertainment_obj_list.size(); urlListCount++){
		final int keyIndex = urlListCount;
		AVObject entertainment_obj = entertainment_obj_list.get(urlListCount);
		entertainment_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub				
				String cntImgUrl = arg0.getAVFile("image").getUrl();	
				if(keyIndex == newsIndex){
					new mCntImageUrlThread(keyIndex,cntImgUrl).start();
				}
			}			
		});
	}
	}
	
	if(queryKeyMethod().equals("sport")){
		
	for(int urlListCount=0; urlListCount<sport_obj_list.size(); urlListCount++){
		final int keyIndex = urlListCount;
		AVObject sport_obj = sport_obj_list.get(urlListCount);
		sport_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub				
				String cntImgUrl = arg0.getAVFile("image").getUrl();	
				if(keyIndex == newsIndex){
					new mCntImageUrlThread(keyIndex,cntImgUrl).start();
				}
			}			
		});
	}
	}
}

//xujianwei new

public class mCntImageUrlThread extends Thread{
	private String imageUrl;
	//private int listCount;
	
	mCntImageUrlThread(int index, String url){
		this.imageUrl = url;
		//this.listCount = listCount;
	}
	
	@Override
	public void run(){
		URL mImageUrl = null;
		
		try {
			mImageUrl = new URL(imageUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Log.e(TAG, "run url is: " + imageUrl);
			HttpURLConnection conn = (HttpURLConnection) mImageUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			Bitmap bmp = BitmapFactory.decodeStream(is);
			is.close();
			Message msg = new Message();
			msg.obj = bmp;
			mCntImageUrlHandler.sendMessage(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

@SuppressLint("NewApi")
public Handler mCntImageUrlHandler = new Handler(){
	@Override
	public void handleMessage(Message msg){
		 
		Bitmap reBmp = (Bitmap) msg.obj;
		Resources res = getResources();
		Drawable mDrawable = new BitmapDrawable(res,reBmp);
		
		ImageView mImageView = (ImageView) findViewById(R.id.content_img);
		mImageView.setBackground(mDrawable);
	}
}; 

//query file meta data
public void queryFileMetadata(){
	for(int urlListCount=0; urlListCount<international_obj_list.size(); urlListCount++){
		AVObject international_obj = international_obj_list.get(urlListCount);
		international_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub				
				String soundSize = arg0.getAVFile("sound").getMetaData().get("size").toString();
				totalSpace = totalSpace + Integer.valueOf(soundSize).intValue();
				Log.e(TAG,"sound size is: " + soundSize);
			}			
		});
	}
	
	for(int urlListCount=0; urlListCount<national_obj_list.size(); urlListCount++){
		AVObject national_obj = national_obj_list.get(urlListCount);
		national_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub				
				String soundSize = arg0.getAVFile("sound").getMetaData().get("size").toString();
				totalSpace = totalSpace + Integer.valueOf(soundSize).intValue();
				//sizeList.add(soundSize);
				Log.e(TAG,"sound size is: " + soundSize);
			}			
		});
	}
	
	for(int urlListCount=0; urlListCount<native_obj_list.size(); urlListCount++){
		AVObject native_obj = native_obj_list.get(urlListCount);
		native_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub				
				String soundSize = arg0.getAVFile("sound").getMetaData().get("size").toString();
				totalSpace = totalSpace + Integer.valueOf(soundSize).intValue();
				//sizeList.add(soundSize);
				Log.e(TAG,"sound size is: " + soundSize);
			}			
		});
	}

	for(int urlListCount=0; urlListCount<finance_obj_list.size(); urlListCount++){
		AVObject finance_obj = finance_obj_list.get(urlListCount);
		finance_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub				
				String soundSize = arg0.getAVFile("sound").getMetaData().get("size").toString();
				totalSpace = totalSpace + Integer.valueOf(soundSize).intValue();
				//sizeList.add(soundSize);
				Log.e(TAG,"sound size is: " + soundSize);
			}			
		});
	}
	
	for(int urlListCount=0; urlListCount<entertainment_obj_list.size(); urlListCount++){
		AVObject entertainment_obj = entertainment_obj_list.get(urlListCount);
		entertainment_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub				
				String soundSize = arg0.getAVFile("sound").getMetaData().get("size").toString();
				totalSpace = totalSpace + Integer.valueOf(soundSize).intValue();
				//sizeList.add(soundSize);
				Log.e(TAG,"sound size is: " + soundSize);
			}			
		});
	}
	
	for(int urlListCount=0; urlListCount<sport_obj_list.size(); urlListCount++){
		AVObject sport_obj = sport_obj_list.get(urlListCount);
		sport_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub				
				String soundSize = arg0.getAVFile("sound").getMetaData().get("size").toString();
				totalSpace = totalSpace + Integer.valueOf(soundSize).intValue();
				//sizeList.add(soundSize);
				Log.e(TAG,"sound size is: " + soundSize);
			}			
		});
	}
	
	totalSpace = (int) (totalSpace - downloadedSize());

}

public void downloadInternational(){
	for(int urlListCount=0; urlListCount<international_obj_list.size(); urlListCount++){
		AVObject international_obj = international_obj_list.get(urlListCount);
		final int inter_count = urlListCount;
		international_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "international_" + (inter_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				
				Log.e(TAG,"International URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
}

public void downloadNational(){
	for(int urlListCount=0; urlListCount<national_obj_list.size(); urlListCount++){
		AVObject national_obj = national_obj_list.get(urlListCount);
		final int national_count = urlListCount;
		national_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "domestic_" + (national_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				//urlMap.put(soundName, soundUrl);
				//Log.e(TAG, "national url map is:" + urlMap);
				Log.e(TAG,"National URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
}

public void downloadNative(){
	for(int urlListCount=0; urlListCount<native_obj_list.size(); urlListCount++){
		AVObject native_obj = native_obj_list.get(urlListCount);
		final int native_count = urlListCount;
		native_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "native_" + (native_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				//urlMap.put(soundName, soundUrl);
				//Log.e(TAG, "national url map is:" + urlMap);
				Log.e(TAG,"Native URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
}

public void downloadFinance(){
	for(int urlListCount=0; urlListCount<finance_obj_list.size(); urlListCount++){
		AVObject finance_obj = finance_obj_list.get(urlListCount);
		final int finance_count = urlListCount;
		finance_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "finance_" + (finance_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				//urlMap.put(soundName, soundUrl);
				//Log.e(TAG, "national url map is:" + urlMap);
				Log.e(TAG,"finance URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
}

public void downloadEntertainment(){
	for(int urlListCount=0; urlListCount<entertainment_obj_list.size(); urlListCount++){
		AVObject entertainment_obj = entertainment_obj_list.get(urlListCount);
		final int entertainment_count = urlListCount;
		entertainment_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "entertainment_" + (entertainment_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				//urlMap.put(soundName, soundUrl);
				//Log.e(TAG, "national url map is:" + urlMap);
				Log.e(TAG,"entertainment URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
}

public void downloadSport(){
	for(int urlListCount=0; urlListCount<sport_obj_list.size(); urlListCount++){
		AVObject sport_obj = sport_obj_list.get(urlListCount);
		final int sport_count = urlListCount;
		sport_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "sport_" + (sport_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				//urlMap.put(soundName, soundUrl);
				//Log.e(TAG, "national url map is:" + urlMap);
				Log.e(TAG,"sport URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
}

public void queryUrlMethod(){
	if(internationalDownloading == false && internationalCached == false){		
		ImageView iv = (ImageView)findViewById(R.id.clock_0);
		iv.setVisibility(View.GONE);
		NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_0);
		npb.setVisibility(View.VISIBLE);
		downloadInternational();
		internationalDownloading = true;
		
	}
	if(nationalDownloading == false && nationalCached == false){
		ImageView iv = (ImageView)findViewById(R.id.clock_1);
		iv.setVisibility(View.GONE);
		NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_1);
		npb.setVisibility(View.VISIBLE);
		downloadNational();
		nationalDownloading = true;
		
	}
	if(nativeDownloading == false && nationalCached == false){
		ImageView iv = (ImageView)findViewById(R.id.clock_2);
		iv.setVisibility(View.GONE);
		NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_2);
		npb.setVisibility(View.VISIBLE);
		downloadNative();
		nativeDownloading = true;
		
	}
	if(financeDownloading == false && financeCached == false){
		ImageView iv = (ImageView)findViewById(R.id.clock_3);
		iv.setVisibility(View.GONE);
		NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_3);
		npb.setVisibility(View.VISIBLE);
		downloadFinance();
		financeDownloading = true;
		
	}
	if(entertainmentDownloading == false && entertainmentCached == false){
		ImageView iv = (ImageView)findViewById(R.id.clock_4);
		iv.setVisibility(View.GONE);
		NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_4);
		npb.setVisibility(View.VISIBLE);
		downloadEntertainment();
		entertainmentDownloading = true;
		
	}
	if(sportDownloading == false && sportCached == false){
		ImageView iv = (ImageView)findViewById(R.id.clock_5);
		iv.setVisibility(View.GONE);
		NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_5);
		npb.setVisibility(View.VISIBLE);
		downloadSport();
		sportDownloading = true;
	}
}

//query news URL from lean cloud
public void queryUrl(){
	if(internationalDownloading == false){		
	for(int urlListCount=0; urlListCount<international_obj_list.size(); urlListCount++){
		AVObject international_obj = international_obj_list.get(urlListCount);
		final int inter_count = urlListCount;
		international_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "international_" + (inter_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				
				Log.e(TAG,"International URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
	}
	
	if(nationalDownloading==false){
	for(int urlListCount=0; urlListCount<national_obj_list.size(); urlListCount++){
		AVObject national_obj = national_obj_list.get(urlListCount);
		final int national_count = urlListCount;
		national_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "domestic_" + (national_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				//urlMap.put(soundName, soundUrl);
				//Log.e(TAG, "national url map is:" + urlMap);
				Log.e(TAG,"National URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
	}
	
	if(nativeDownloading == false){
	for(int urlListCount=0; urlListCount<native_obj_list.size(); urlListCount++){
		AVObject native_obj = native_obj_list.get(urlListCount);
		final int native_count = urlListCount;
		native_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "native_" + (native_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				//urlMap.put(soundName, soundUrl);
				//Log.e(TAG, "national url map is:" + urlMap);
				Log.e(TAG,"Native URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
	}
	/*
	if(financeDownloading == false){
	for(int urlListCount=0; urlListCount<finance_obj_list.size(); urlListCount++){
		AVObject finance_obj = finance_obj_list.get(urlListCount);
		final int finance_count = urlListCount;
		finance_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "finance_" + (finance_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				//urlMap.put(soundName, soundUrl);
				//Log.e(TAG, "national url map is:" + urlMap);
				Log.e(TAG,"finance URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
	}
	
	if(entertainmentDownloading == false){
	for(int urlListCount=0; urlListCount<entertainment_obj_list.size(); urlListCount++){
		AVObject entertainment_obj = entertainment_obj_list.get(urlListCount);
		final int entertainment_count = urlListCount;
		entertainment_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "entertainment_" + (entertainment_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				//urlMap.put(soundName, soundUrl);
				//Log.e(TAG, "national url map is:" + urlMap);
				Log.e(TAG,"entertainment URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
	}
	
	if(sportDownloading == false){
	for(int urlListCount=0; urlListCount<sport_obj_list.size(); urlListCount++){
		AVObject sport_obj = sport_obj_list.get(urlListCount);
		final int sport_count = urlListCount;
		sport_obj.fetchInBackground(new GetCallback<AVObject>(){

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				//HashMap<String, String> urlMap = new HashMap<String, String>();
				String soundUrl = arg0.getAVFile("sound").getUrl();							
				String soundName = "sport_" + (sport_count+1) + ".mp3";	
				downloadBatchThread mdownloadBatchThread = new downloadBatchThread(soundName, soundUrl);
				mdownloadBatchThread.start();
				//urlMap.put(soundName, soundUrl);
				//Log.e(TAG, "national url map is:" + urlMap);
				Log.e(TAG,"sport URL is: " + soundName + ", " + soundUrl);
			}
			
		});
	}
	}
	*/
}


public String queryKeyMethod(){
	int startKey = gen_path.lastIndexOf("/");
	int endKey = gen_path.lastIndexOf("_");
	Log.e(TAG, "gen_path: " + gen_path + " startkey: " + startKey + " endkey: " + endKey);
	String queryKeyStr = (String) gen_path.subSequence(startKey+1, endKey);
	Log.e(TAG, "query key: "+ queryKeyStr);
	return queryKeyStr;
}


// handler URL map message and start batch-download thread
public Handler mURLHandler = new Handler(){
	@Override
	public void handleMessage(Message msg){		
		Log.e(TAG, "URL MSG IS " + msg.obj);
		HashMap<String, String> urlMap = (HashMap<String, String>) msg.obj;
		Set<HashMap.Entry<String, String>> set = urlMap.entrySet();
		Iterator<HashMap.Entry<String, String>> iterator = set.iterator();
		while(iterator.hasNext()){
			HashMap.Entry<String, String> entry = iterator.next();
			downloadBatchThread mdownloadBatchThread = new downloadBatchThread(entry.getKey(), entry.getValue());
			mdownloadBatchThread.start();
			//Log.e(TAG, "Key is: " + entry.getKey() + ". Value is: " + entry.getValue());
		}
		super.handleMessage(msg);
	}
};



//batch-download thread
public class downloadBatchThread extends Thread{
		private String URL;
		private String name;
		
		public downloadBatchThread(String name, String URL){
			this.name = name;
			this.URL = URL;
		}
		
		@Override
		public void run(){
			if(name.startsWith("international")){
				HttpDownloader downloadResult = new HttpDownloader();
				downloadResult.downloadFile(this.URL, "subwaynews_International/", this.name);
			}
			if(name.startsWith("domestic")){
				HttpDownloader downloadResult = new HttpDownloader();
				downloadResult.downloadFile(this.URL, "subwaynews_Domestic/", this.name);
			}
			if(name.startsWith("native")){
				HttpDownloader downloadResult = new HttpDownloader();
				downloadResult.downloadFile(this.URL, "subwaynews_Native/", this.name);
			}
			if(name.startsWith("finance")){
				HttpDownloader downloadResult = new HttpDownloader();
				downloadResult.downloadFile(this.URL, "subwaynews_Finance/", this.name);
			}
			if(name.startsWith("entertainment")){
				HttpDownloader downloadResult = new HttpDownloader();
				downloadResult.downloadFile(this.URL, "subwaynews_Entertainment/", this.name);
			}
			if(name.startsWith("sport")){
				HttpDownloader downloadResult = new HttpDownloader();
				downloadResult.downloadFile(this.URL, "subwaynews_Sport/", this.name);
			}
			
		}
	
}
	
	
	/*--- generate news array for different channel path ---*/
	public void generateArray(int cnt){
		for(int mCnt=1; mCnt<=cnt; mCnt++){
			newsArrayList.add(gen_path + mCnt + ".mp3");
		}
		
		Log.e(TAG,"Finish to generate array, are: " + newsArrayList);
	}
	
	/*--- listen one piece news complete-state ---*/
	public final class CompletionListener implements OnCompletionListener{
		@Override
		public void onCompletion(MediaPlayer mp){
			//mThread.interrupted();
			TrueIndex = false;
			nextNews();
		}
	}
	
	/*--- continue to play next news item---*/
	private void nextNews(){
		//mediaPlayer.reset();
		if (newsIndex < newsArrayList.size() - 1){
			newsIndex = newsIndex + 1;
			Log.e(TAG,"News Index is " + newsIndex);
			playNews();
		}
		else{
			newsArrayList.clear();
			Log.e(TAG,"Prepare to play next channel.");
			playNextChannel();
		}
	}
	
	/*--- back to last news item ---*/
	
	private void lastNews(){
		if (newsIndex > 0){
			newsIndex = newsIndex - 1;
			playNews();
	    }
		else{
			playLastChannel();
		}
	}
	
	public void playLastChannel(){
		if(pathIndex > 0)
		pathIndex = pathIndex - 1;
		gen_path = downloadFinishedArrayList.get(pathIndex);
		gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
		newsArrayList.clear();
		generateArray(countFile(gen_path_parent));
		newsIndex = countFile(gen_path_parent)-1;
		playNews();
	}
	
	public void playNextChannel(){
		newsIndex = 0;
		//pathIndex = pathIndex + 1;
		
		if (pathIndex < downloadFinishedArrayList.size() - 1){
			pathIndex = pathIndex + 1;
			gen_path = downloadFinishedArrayList.get(pathIndex);			
			gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
			Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
			newsArrayList.clear();
			generateArray(countFile(gen_path_parent));
			//mediaPlayer.setOnCompletionListener(new CompletionListener());
			playNews();
		}
	}
	/*
	public void playLastChannel(){
		boolean inKey = false;
		if(pathIndex > 0){
		pathIndex = pathIndex - 1;
		gen_path = downloadFinishedArrayList.get(pathIndex);
		if(gen_path.contains("subwaynews_Entertainment")==true && alreadyDownload(entertainment_obj_list.size(), "娱乐")){
			inKey = true;
			gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
			newsArrayList.clear();
			generateArray(countFile(gen_path_parent));
			newsIndex = countFile(gen_path_parent)-1;
			playNews();
		}
		else if(gen_path.contains("subwaynews_Finance")==true && alreadyDownload(finance_obj_list.size(), "财经")){
			inKey = true;
			gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
			newsArrayList.clear();
			generateArray(countFile(gen_path_parent));
			newsIndex = countFile(gen_path_parent)-1;
			playNews();
		}
		else if(gen_path.contains("subwaynews_Native")==true && alreadyDownload(native_obj_list.size(), "北京")){
			inKey = true;
			gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
			newsArrayList.clear();
			generateArray(countFile(gen_path_parent));
			newsIndex = countFile(gen_path_parent)-1;
			playNews();
		}
		else if(gen_path.contains("subwaynews_Domestic")==true && alreadyDownload(national_obj_list.size(), "国内")){
			inKey = true;
			gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
			newsArrayList.clear();
			generateArray(countFile(gen_path_parent));
			newsIndex = countFile(gen_path_parent)-1;
			playNews();
		}
		else if(gen_path.contains("subwaynews_International")==true && alreadyDownload(international_obj_list.size(), "国际")){
			inKey = true;
			gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
			newsArrayList.clear();
			generateArray(countFile(gen_path_parent));
			newsIndex = countFile(gen_path_parent)-1;
			playNews();
		}
		
		if(inKey==false){
			playLastChannel();
		}
	}
		else{
			Log.e(TAG, "last channel now.");
		}
	}
	
	public void playNextChannel(){
		boolean inKey = false;
		newsIndex = 0;
		
		
		if (pathIndex < downloadFinishedArrayList.size() - 1){
			pathIndex = pathIndex + 1;
			gen_path = downloadFinishedArrayList.get(pathIndex);	
			
			if(gen_path.contains("subwaynews_Domestic")==true && alreadyDownload(national_obj_list.size(), "国内")){
				inKey = true;
				gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
				Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
				newsArrayList.clear();
				generateArray(countFile(gen_path_parent));
				playNews();
			}
			else if (gen_path.contains("subwaynews_Native")==true && alreadyDownload(native_obj_list.size(), "北京")) {
				inKey = true;
				gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
				Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
				newsArrayList.clear();
				generateArray(countFile(gen_path_parent));
				playNews();
			}
			else if (gen_path.contains("subwaynews_Finance")==true && alreadyDownload(finance_obj_list.size(), "财经")) {
				inKey = true;
				gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
				Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
				newsArrayList.clear();
				generateArray(countFile(gen_path_parent));
				playNews();
			}
			else if (gen_path.contains("subwaynews_Entertainment")==true && alreadyDownload(entertainment_obj_list.size(), "娱乐")) {
				inKey = true;
				gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
				Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
				newsArrayList.clear();
				generateArray(countFile(gen_path_parent));
				playNews();
			}
			else if (gen_path.contains("subwaynews_Sport")==true && alreadyDownload(sport_obj_list.size(), "体育")) {
				inKey = true;
				gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
				Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
				newsArrayList.clear();
				generateArray(countFile(gen_path_parent));
				playNews();
			}
			
			if(inKey==false && pathIndex != 5){
				playNextChannel();
			}
			
			if(inKey == false && pathIndex == 5){
				if(alreadyDownload(entertainment_obj_list.size(), "娱乐")==true){
					gen_path = downloadFinishedArrayList.get(4);
					gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
					Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
					newsArrayList.clear();
					generateArray(countFile(gen_path_parent));
					newsIndex = entertainment_obj_list.size()-1; 
					Log.e(TAG, "FFF news INDEX NOW: " + newsIndex);
				}
				else if(alreadyDownload(finance_obj_list.size(), "财经")==true){
					gen_path = downloadFinishedArrayList.get(3);
					gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
					Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
					newsArrayList.clear();
					generateArray(countFile(gen_path_parent));
					newsIndex = finance_obj_list.size()-1; 
				}
				else if(alreadyDownload(native_obj_list.size(), "北京")==true){
					gen_path = downloadFinishedArrayList.get(2);
					gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
					Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
					newsArrayList.clear();
					generateArray(countFile(gen_path_parent));
					newsIndex = native_obj_list.size()-1; 
				}
				else if(alreadyDownload(national_obj_list.size(), "国内")==true){
					gen_path = downloadFinishedArrayList.get(1);
					gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
					Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
					newsArrayList.clear();
					generateArray(countFile(gen_path_parent));
					newsIndex = national_obj_list.size()-1; 
				}
				else if(alreadyDownload(international_obj_list.size(), "国际")==true){
					gen_path = downloadFinishedArrayList.get(0);
					gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
					Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
					newsArrayList.clear();
					generateArray(countFile(gen_path_parent));
					newsIndex = international_obj_list.size()-1; 
				}
				
				
			}
			
			
			
		}
		else{
			Log.e(TAG, "out out out!");
			if(inKey == false && pathIndex == 5){
				if(alreadyDownload(entertainment_obj_list.size(), "娱乐")==true){
					gen_path = downloadFinishedArrayList.get(4);
					gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
					Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
					newsArrayList.clear();
					generateArray(countFile(gen_path_parent));
					newsIndex = entertainment_obj_list.size()-1; 
					Log.e(TAG, "FFF news INDEX NOW: " + newsIndex);
				}
				else if(alreadyDownload(finance_obj_list.size(), "财经")==true){
					gen_path = downloadFinishedArrayList.get(3);
					gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
					Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
					newsArrayList.clear();
					generateArray(countFile(gen_path_parent));
					newsIndex = finance_obj_list.size()-1; 
				}
				else if(alreadyDownload(native_obj_list.size(), "北京")==true){
					gen_path = downloadFinishedArrayList.get(2);
					gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
					Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
					newsArrayList.clear();
					generateArray(countFile(gen_path_parent));
					newsIndex = native_obj_list.size()-1; 
				}
				else if(alreadyDownload(national_obj_list.size(), "国内")==true){
					gen_path = downloadFinishedArrayList.get(1);
					gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
					Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
					newsArrayList.clear();
					generateArray(countFile(gen_path_parent));
					newsIndex = national_obj_list.size()-1; 
				}
				else if(alreadyDownload(international_obj_list.size(), "国际")==true){
					gen_path = downloadFinishedArrayList.get(0);
					gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
					Log.e(TAG, "playnextchannel gen path parent is: " + gen_path_parent);
					newsArrayList.clear();
					generateArray(countFile(gen_path_parent));
					newsIndex = international_obj_list.size()-1; 
				}
				
			}
		}
	}   */
	
	public int countFile(String cPath){
		File countPath = new File(cPath);
		Log.e(TAG, "count file is: " + countPath.list().length);
		return countPath.list().length;
	}
	
	
	/*--- listen the button clicked ---*/
	public final class buttonClickListener implements View.OnClickListener{
		

		@Override
		public void onClick(View v){
			switch(v.getId()){
			case R.id.download_whole:
				if(isNetworkConnected(MainActivity.this)==true){
				Button db=(Button)findViewById(R.id.download_whole);
				SpannableString ss = new SpannableString("缓 存 中         ");
				ss.setSpan(new RelativeSizeSpan(0.4f), 6, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				db.setText(ss);
				db.setBackgroundColor(Color.rgb(229, 176, 126));
				db.setClickable(false);
				queryFileMetadata();
	
				queryUrlMethod();
				Log.e(TAG, "Begin to downloadbatch!!!");
				}
				else{
					noNetworkDialog();
					//showPopUpWindow(mFlingGalleryView);
				}
				break;
					
			case R.id.international_news:
				//文件夹内是否有文件，有多少个文件
				
				newsIndex = 0;
				pathIndex = 0;
				gen_path_parent = "/sdcard/subwaynews_International/";
				gen_path = gen_path_parent + "international_";
				newsArrayList.clear();
				
				//generateArray(countFile(gen_path_parent));
				Log.e(TAG, "international_obj_list.size(): " + international_obj_list.size());
				if(alreadyDownload(international_obj_list.size(), "国际")==false && internationalDownloading==false){
					Log.e(TAG, "begin to download international news!");
					ImageView iv = (ImageView)findViewById(R.id.clock_0);
					iv.setVisibility(View.GONE);
					NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_0);
					npb.setVisibility(View.VISIBLE);
					downloadInternational();
					internationalDownloading = true;
				}		
				
				else{
					generateArray(countFile(gen_path_parent));
					mediaPlayer.setOnCompletionListener(new CompletionListener());
					playNews();
					FlingGalleryView fgv = (FlingGalleryView) findViewById(R.id.ScrolllayoutID);
					
					setSimulateFling(fgv);
				}
					break;
				
			case R.id.national_news:
				newsIndex = 0;
				pathIndex = 1;
				gen_path_parent ="/sdcard/subwaynews_Domestic/";
				gen_path = gen_path_parent + "domestic_";
				newsArrayList.clear();
				
				if(alreadyDownload(national_obj_list.size(), "国内")==false && nationalDownloading==false){
					ImageView iv = (ImageView)findViewById(R.id.clock_1);
					iv.setVisibility(View.GONE);
					NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_1);
					npb.setVisibility(View.VISIBLE);
					downloadNational();
					nationalDownloading = true;
				}	
				else{
					generateArray(countFile(gen_path_parent));
					mediaPlayer.setOnCompletionListener(new CompletionListener());
					playNews();
					FlingGalleryView fgv = (FlingGalleryView) findViewById(R.id.ScrolllayoutID);
					//fgv.snapToScreen(1, false, 1);
					
					setSimulateFling(fgv);
					//setPlayState();
				}
					break;
			
			case R.id.native_news:
				newsIndex = 0;
				pathIndex = 2;
				gen_path_parent = "/sdcard/subwaynews_Native/";
				gen_path = gen_path_parent + "native_";
				newsArrayList.clear();
				
				if(alreadyDownload(native_obj_list.size(), "北京")==false && nativeDownloading==false){
					ImageView iv = (ImageView)findViewById(R.id.clock_2);
					iv.setVisibility(View.GONE);
					NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_2);
					npb.setVisibility(View.VISIBLE);
					downloadNative();
					nativeDownloading = true;
				}
				else{
					generateArray(countFile(gen_path_parent));
					mediaPlayer.setOnCompletionListener(new CompletionListener());
					playNews();
					FlingGalleryView fgv = (FlingGalleryView) findViewById(R.id.ScrolllayoutID);
					//fgv.snapToScreen(1, false, 1);
					
					setSimulateFling(fgv);
					//setPlayState();
				}
					break;
					
			case R.id.finance_news:
				newsIndex = 0;
				pathIndex = 3;
				gen_path_parent = "/sdcard/subwaynews_Finance/";
				gen_path = gen_path_parent + "finance_";
				newsArrayList.clear();
				if(alreadyDownload(finance_obj_list.size(), "财经")==false && financeDownloading==false){
					ImageView iv = (ImageView)findViewById(R.id.clock_3);
					iv.setVisibility(View.GONE);
					NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_3);
					npb.setVisibility(View.VISIBLE);
					downloadFinance();
					financeDownloading = true;
				}
				else{
					generateArray(countFile(gen_path_parent));
					mediaPlayer.setOnCompletionListener(new CompletionListener());
					playNews();
					FlingGalleryView fgv = (FlingGalleryView) findViewById(R.id.ScrolllayoutID);
					//fgv.snapToScreen(1, false, 1);
					
					setSimulateFling(fgv);
					//setPlayState();
				}
					break;
					
			case R.id.entertainment_news:
				newsIndex = 0;
				pathIndex = 4;
				gen_path_parent = "/sdcard/subwaynews_Entertainment/";
				gen_path = gen_path_parent + "entertainment_";
				newsArrayList.clear();
				if(alreadyDownload(entertainment_obj_list.size(), "娱乐")==false && entertainmentDownloading==false){
					ImageView iv = (ImageView)findViewById(R.id.clock_4);
					iv.setVisibility(View.GONE);
					NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_4);
					npb.setVisibility(View.VISIBLE);
					downloadEntertainment();
					entertainmentDownloading = true;
				}
				else{
					generateArray(countFile(gen_path_parent));
					mediaPlayer.setOnCompletionListener(new CompletionListener());
					playNews();
					FlingGalleryView fgv = (FlingGalleryView) findViewById(R.id.ScrolllayoutID);
					//fgv.snapToScreen(1, false, 1);
					
					setSimulateFling(fgv);
					//setPlayState();
				}
					break;
					
			case R.id.sport_news:
				newsIndex = 0;
				pathIndex = 5;
				gen_path_parent = "/sdcard/subwaynews_Sport/";
				gen_path = gen_path_parent + "sport_";
				newsArrayList.clear();
				if(alreadyDownload(sport_obj_list.size(), "体育")==false && sportDownloading==false){
					ImageView iv = (ImageView)findViewById(R.id.clock_5);
					iv.setVisibility(View.GONE);
					NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_5);
					npb.setVisibility(View.VISIBLE);
					downloadSport();
					sportDownloading = true;
				}
				else{
					generateArray(countFile(gen_path_parent));
					mediaPlayer.setOnCompletionListener(new CompletionListener());
					playNews();
					FlingGalleryView fgv = (FlingGalleryView) findViewById(R.id.ScrolllayoutID);
					//fgv.snapToScreen(1, false, 1);
					
					setSimulateFling(fgv);
					//setPlayState();
				}
					break;
				
			case R.id.play_button:
				
				if (mediaPlayer.isPlaying()){
					mediaPlayer.pause();
					mPosition = mediaPlayer.getCurrentPosition();
					setPlayState();
					isFirstPress = false;
				}
				else{					
					if(isFirstPress == false){
						mediaPlayer.setOnCompletionListener(new CompletionListener());
						playPauseNews();
						//playNews();
						setPlayState();
					}else{
						FileUtils fus = new FileUtils();
						for (int index=0; index<downloadFinishedArrayList.size(); index++){
							String dirName = downloadFinishedArrayList.get(index) + "1.mp3";
							if(fus.isDirExist(dirName)){
								newsIndex = 0;
								pathIndex = index;
								gen_path = downloadFinishedArrayList.get(index);
								gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
								newsArrayList.clear();
								generateArray(countFile(gen_path_parent));
								if(national_state==false){
									// send HTTP request to server to download content
								}
								else{
									mediaPlayer.setOnCompletionListener(new CompletionListener());
									playNews();
									//setPlayState();
								}
								break;
							}
						}
					}
				}
				
				break;
				
			case R.id.forward_button:
				if(mediaPlayer.isPlaying()){
					nextNews();
				}
				else{
					
				}
				break;
				
			case R.id.backward_button:
				if(mediaPlayer.isPlaying()){
					lastNews();
				}
				else{
					
				}
				break;
				
			//case R.id.share_button:
				//showPopUpWindow(mFlingGalleryView);
				//break;
			}
		}
	}
	
	public void setSimulateFling(View view){
		FlingGalleryView fv = (FlingGalleryView) view;
		Log.e(TAG, "1");
		long downTime = SystemClock.uptimeMillis();
		Log.e(TAG, "2");
		
		
		final MotionEvent startMoveEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, 500, 450, 0);		
		downTime += 150;
		final MotionEvent firMoveEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, 530, 450, 0);
		downTime += 150;
		final MotionEvent secMoveEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, 550, 450, 0);
		downTime += 150;
		final MotionEvent endMoveEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, 1070, 450, 0);
		downTime += 250;
		final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 1070, 450, 0);
		
		fv.onTouchEvent(startMoveEvent,1);
		//fv.onInterceptTouchEvent(firMoveEvent);
		fv.onTouchEvent(firMoveEvent,1);
		//fv.onInterceptTouchEvent(secMoveEvent);
		fv.onTouchEvent(secMoveEvent,1);
		//fv.onInterceptTouchEvent(endMoveEvent);
		fv.onTouchEvent(endMoveEvent,1);
		//fv.onInterceptTouchEvent(upEvent);
		fv.onTouchEvent(upEvent,1);
		Log.e(TAG, "4");
		
		startMoveEvent.recycle();
		endMoveEvent.recycle();
		upEvent.recycle();
		Log.e(TAG, "ggggggggggggg!");
	}
	
	
	public void setPlayState(){
		if (mediaPlayer.isPlaying()){
			ImageButton playButton = (ImageButton)findViewById(R.id.play_button);
			playButton.setImageResource(R.drawable.pause);
			mStatePause = true; 
			
		}
		else{
			ImageButton playButton = (ImageButton)findViewById(R.id.play_button);
			playButton.setImageResource(R.drawable.play);}
	}
	
	public void playPauseNews(){
		mediaPlayer.pause();
		mediaPlayer.seekTo(mPosition);
		mediaPlayer.start();
		changeProgressBar();
	}
	
/* ---------------play news---------------------*/	
	@SuppressWarnings("resource")
	public void playNews(){
		
		try {
			TrueIndex = true;
			Log.e(TAG, "onclick success! and newsIndex is " + newsIndex);
			
			progressState = 0;		
			mediaPlayer.reset();
			Log.e(TAG,"Begin to set source: " + newsArrayList.get(newsIndex));
			
			mediaPlayer.setDataSource((new FileInputStream(new File(newsArrayList.get(newsIndex)))).getFD());
			Log.e(TAG,"Begin, mediaplayer Prepare content.");
			mediaPlayer.prepareAsync();
			Log.e(TAG,"mediaplayer is Preparing content.");
			
			mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					
					ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
					progressBar.setProgress(0);
					Log.e(TAG, "setprogress is 0!");
					int length = mediaPlayer.getDuration();
					Log.e(TAG, "legth is " + length + "ms");
					TextView totalDuration = (TextView)findViewById(R.id.total_duration);			
					totalDuration.setText("00:"+String.valueOf(length/1000));
					progressBar.setMax(length);
					
					
					mp.start();
					mp.setOnCompletionListener(new CompletionListener());
					
					
					
					changeProgressBar();
					TimeThread mThread = new TimeThread();
					mThread.start();
					
					if(isNetworkConnected(MainActivity.this)==true){
						Log.e(TAG, "play news and newsindex and gen_path are: " + newsIndex + "/" + gen_path);
						queryListView();					
						querySource();					
						queryNewsContent();
						queryContentImg();
						
					}
					
					setPlayState();
					
				}
			});
						
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	private void  changeProgressBar(){
		new Thread(new Runnable(){
			public void run(){
				while(mediaPlayer.isPlaying()){
					ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
					progressBar.setProgress(mediaPlayer.getCurrentPosition());
					//Log.e(TAG, "Current position is " + mediaPlayer.getCurrentPosition());
				try{
					Thread.sleep(100);
				}catch(InterruptedException e){
					
				}
			}
			}
		}).start();
	}
	
	
	public class TimeThread extends Thread {
        @Override
        public void run () {
        	while(TrueIndex){
                try {
					Thread.sleep(100);
					Message msg = new Message();
					msg.what = msgKey1;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}	
             
        }
    }
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                	
					while(mediaPlayer.isPlaying()){
                	TextView leftDuration = (TextView)findViewById(R.id.left_duration);	
                	//Log.e(TAG, "get duration for left time!");
                	if((mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition())/1000>=10){
                		leftDuration.setText("-00:"+String.valueOf((mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition())/1000));         		
                	}
                	else
                		leftDuration.setText("-00:0"+String.valueOf((mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition())/1000));         		
					
                    break;
					}
                default:
                    break;
            }
        }
    };

	
	@Override
	public void onResume(){
		super.onResume();
		
		//queryChannelList();
		
	}
	
	@Override
	public void onDestroy() {
		mediaPlayer.release();
		super.onDestroy();
	}	
	
	@Override
	public void OnCircleProgressBarUpdate(String progressPath){
		Log.e(TAG, "000");
		Message msg = new Message();
		msg.obj = progressPath;
		circleProgressHandler.sendMessage(msg);
		
		
	}
	
	@Override
	public void OnCircleProgressBarDone(String key){
		Log.e(TAG, "qqqqq");
		Message msg = new Message();
		msg.obj = key;
		circleProgressDoneHandler.sendMessage(msg);
	}
	
	private Handler circleProgressDoneHandler = new Handler() {
		@Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            if(msg.obj.toString().equalsIgnoreCase("international")){
            	NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_0);
            	npb.setVisibility(View.INVISIBLE);
            	}
            if(msg.obj.toString().equalsIgnoreCase("domestic")){
            	NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_1);
            	npb.setVisibility(View.INVISIBLE);
            	}
            if(msg.obj.toString().equalsIgnoreCase("native")){
            	NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_2);
            	npb.setVisibility(View.INVISIBLE);
            	}
            if(msg.obj.toString().equalsIgnoreCase("finance")){
            	NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_3);
            	npb.setVisibility(View.INVISIBLE);
            	}
            if(msg.obj.toString().equalsIgnoreCase("entertainment")){
            	NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_4);
            	npb.setVisibility(View.INVISIBLE);
            	}
            if(msg.obj.toString().equalsIgnoreCase("sport")){
            	NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_5);
            	npb.setVisibility(View.INVISIBLE);
            	}
		}
	};
	
	private Handler circleProgressHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            String progressPath = msg.obj.toString();
            Log.e(TAG, "0");
    		if(progressPath.contains("international")){
    			String progressIndex = "international";
    			Log.e(TAG, "if inter");
    			NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_0);
    			npb.incrementProgressBy(mInternationalProgressStep, progressIndex);
    			Log.e(TAG, "1");
    		}
    		if(progressPath.contains("domestic")){
    			String progressIndex = "domestic";
    			Log.e(TAG, "if domestic");
    			NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_1);
    			npb.incrementProgressBy(mNationalProgressStep, progressIndex);
    			Log.e(TAG, "2");
    		}
    		if(progressPath.contains("native")){
    			String progressIndex = "native";
    			Log.e(TAG, "if native");
    			NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_2);
    			npb.incrementProgressBy(mNativeProgressStep, progressIndex);
    			Log.e(TAG, "3");
    		}
    		if(progressPath.contains("finance")){
    			String progressIndex = "finance";
    			Log.e(TAG, "if finance");
    			NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_3);
    			npb.incrementProgressBy(mFinanceProgressStep, progressIndex);
    			Log.e(TAG, "4");
    		}
    		if(progressPath.contains("entertainment")){
    			String progressIndex = "entertainment";
    			Log.e(TAG, "if entertainment");
    			NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_4);
    			npb.incrementProgressBy(mEntertainmentProgressStep, progressIndex);
    			Log.e(TAG, "5");
    		}
    		if(progressPath.contains("sport")){
    			String progressIndex = "sport";
    			Log.e(TAG, "if sport");
    			NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_5);
    			npb.incrementProgressBy(mSportProgressStep, progressIndex);
    			Log.e(TAG, "6");
    		}
        }
    };
	
	@Override
	public void OnDownloadSizeChange(int size, long time){
		Log.e(TAG, "In downloadsizechange!");
		dSize += size;
		dTime += time;
		if(totalSpace != 0 ){
			long leftTime = (totalSpace-dSize)*dTime/dSize/1000;
			Message msg = new Message();
			msg.what = (int) leftTime;
			textHandler.sendMessage(msg);
			Log.e(TAG, "leftTime is: " + leftTime);
		}
		else{
			Log.e(TAG, "leftTime is can not compute because total space is 0!");
		}
	}
	
	private Handler textHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            Button db=(Button)findViewById(R.id.download_whole);
            if(msg.what>99){
    			SpannableString ss = new SpannableString("缓 存 中 大约还剩"+msg.what+"秒");
    			ss.setSpan(new RelativeSizeSpan(0.4f), 6, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    			db.setText(ss);
    			}
            if(msg.what>9 && msg.what<100){
            	SpannableString ss = new SpannableString("缓 存 中 大约还剩"+msg.what+"秒");
            	ss.setSpan(new RelativeSizeSpan(0.4f), 6, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            	db.setText(ss);
			}
            if(msg.what<10 && msg.what>0){
				SpannableString ss = new SpannableString("缓 存 中 大约还剩"+msg.what+"秒");
				ss.setSpan(new RelativeSizeSpan(0.4f), 6, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				db.setText(ss);
			}
            if(msg.what==0){
            	db.setText("已全部缓存");
            	db.setBackgroundColor(Color.rgb(105, 186, 106));
            }
            }
        };
	
	
	@Override
	public void OnViewChange(int view){
		setCurBar(view);
	}
	
	private void setCurBar(int index){
		
		Log.e(TAG,"Current index is " + index);
		ImageView leftScrollBar = (ImageView)findViewById(R.id.playbarleft);
		ImageView centerScrollBar = (ImageView)findViewById(R.id.playbarcenter);
		ImageView rightScrollBar = (ImageView)findViewById(R.id.playbarright);
		Log.e(TAG,"xxxxxxx!");
		if(index == 0){
			leftScrollBar.setBackgroundResource(R.drawable.playbaryes);
			rightScrollBar.setBackgroundResource(R.drawable.playbarno);
			centerScrollBar.setBackgroundResource(R.drawable.playbarno);
			setCurScreen(index);
			Log.e(TAG,"hhhhhhhh!");
		}
		
		if(index == 1 && firstEnterIndex1 == false){
			leftScrollBar.setBackgroundResource(R.drawable.playbarno);
			rightScrollBar.setBackgroundResource(R.drawable.playbarno);
			centerScrollBar.setBackgroundResource(R.drawable.playbaryes);
			Log.e(TAG,"aaaaaaaa!");
			setCurScreen(index);
			queryListView();
			querySource();					
			queryNewsContent();
			queryContentImg();
		}
		
		if(index == 1 && firstEnterIndex1 == true){
			firstEnterIndex1 = false;
			leftScrollBar.setBackgroundResource(R.drawable.playbarno);
			rightScrollBar.setBackgroundResource(R.drawable.playbarno);
			centerScrollBar.setBackgroundResource(R.drawable.playbaryes);
			Log.e(TAG,"aaaaaaaa!");
			setCurScreen(index);
			
			if(isNetworkConnected(MainActivity.this)==false){
				TextView mTextView = (TextView) findViewById(R.id.nonet);
				mTextView.setVisibility(View.VISIBLE);
				ListView list = (ListView)findViewById(R.id.list_items);
				list.setVisibility(View.GONE);		
			}
			
			
			if(alreadyDownload(international_obj_list.size(), "国际")==false && alreadyDownload(national_obj_list.size(), "国内")==false
					&& alreadyDownload(native_obj_list.size(), "北京")==false && alreadyDownload(finance_obj_list.size(), "财经")==false
					&& alreadyDownload(entertainment_obj_list.size(), "娱乐")==false && alreadyDownload(sport_obj_list.size(), "体育")==false){
				gen_path = downloadFinishedArrayList.get(0);
				queryListView();
				ImageView iv = (ImageView)findViewById(R.id.clock_0);
				iv.setVisibility(View.GONE);
				NumberCircleProgressBar npb = (NumberCircleProgressBar) findViewById(R.id.channel_progress_0);
				npb.setVisibility(View.VISIBLE);
				downloadInternational();
				internationalDownloading = true;	
				querySource();					
				queryNewsContent();
				queryContentImg();
			}
			
			if(firstEnterIndex1==true && alreadyDownload(international_obj_list.size(), "国际")==true){
				gen_path_parent ="/sdcard/subwaynews_International/";
				gen_path = gen_path_parent + "international_";
				queryListView();
				querySource();					
				queryNewsContent();
				queryContentImg();
			}			
			else if(firstEnterIndex1==true && alreadyDownload(national_obj_list.size(), "国内")==true){
				gen_path_parent ="/sdcard/subwaynews_Domestic/";
				gen_path = gen_path_parent + "domestic_";
				queryListView();
				querySource();					
				queryNewsContent();
				queryContentImg();
				
			}
			else if(firstEnterIndex1==true && alreadyDownload(native_obj_list.size(), "北京")==true){
				gen_path = "/sdcard/subwaynews_Native/native_";
				queryListView();
				querySource();					
				queryNewsContent();
				queryContentImg();
				
			}
			else if(firstEnterIndex1==true && alreadyDownload(finance_obj_list.size(), "财经")==true){
				gen_path = "/sdcard/subwaynews_Finance/finance_";
				queryListView();
				querySource();					
				queryNewsContent();
				queryContentImg();
				
			}
			else if(firstEnterIndex1==true && alreadyDownload(entertainment_obj_list.size(), "娱乐")==true){
				gen_path = "/sdcard/subwaynews_Entertainment/entertainment_";
				queryListView();
				querySource();					
				queryNewsContent();
				queryContentImg();
				
			}
			else if(firstEnterIndex1==true && alreadyDownload(sport_obj_list.size(), "体育")==true){
				gen_path = "/sdcard/subwaynews_Sport/sport_";
				queryListView();
				querySource();					
				queryNewsContent();
				queryContentImg();
				
			}
		
		}
		
		if(index == 2){
			leftScrollBar.setBackgroundResource(R.drawable.playbarno);
			rightScrollBar.setBackgroundResource(R.drawable.playbaryes);
			centerScrollBar.setBackgroundResource(R.drawable.playbarno);
			Log.e(TAG,"aaaaaaaa!");
			setCurScreen(index);
		}
	}
	
	private void setCurScreen(int curScreenIndex){
		mcurScreenIndex = curScreenIndex;
	}
	
	public void setListView(){
		TextView mTextView = (TextView) findViewById(R.id.nonet);
		mTextView.setVisibility(View.GONE);
		ListView list = (ListView)findViewById(R.id.list_items);
		list.setVisibility(View.VISIBLE);
		Log.e(TAG, "set list view and listdata is: " + listData);
		listItemAdapter = new MyAdapter(this,listData,R.layout.item,new String[]{"playpic","titleIndex","titleContent"},new int[]{R.id.arrow,R.id.title_index,R.id.title_content},newsIndex);
		list.setAdapter(listItemAdapter);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.e(TAG, "arg is: " + arg2);
				newsIndex = arg2;
				gen_path_parent = (String) gen_path.subSequence(0, gen_path.lastIndexOf("/")+1);
				newsArrayList.clear();
				generateArray(countFile(gen_path_parent));
				Log.e(TAG, "gen_path now is:" + gen_path);
				Log.e(TAG, "newsindex now is: " + newsIndex);
				switch (arg2) {
				case 0:
					
					Log.e(TAG, "after click item 0 newsindex now is: " + newsIndex);
					//generateArray(1);
					playNews();
					break;
				
				case 1:
					
					Log.e(TAG, "after click item 1 newsindex now is: " + newsIndex);
					//generateArray(2);
					playNews();
					break;
				
				case 2:
					
					Log.e(TAG, "after click item 2 newsindex now is: " + newsIndex);
					//generateArray(3);
					playNews();
					break;

				default:
					break;
				}
				
			}});
	}
	
	private void init(){
		mFlingGalleryView = (FlingGalleryView)findViewById(R.id.ScrolllayoutID);
		mFlingGalleryView.setOnViewChangeListener(this);
		FileUtils mFileUtils = new FileUtils();
		NumberCircleProgressBar mNumberCircleProgressBar = new NumberCircleProgressBar(this);
		mNumberCircleProgressBar.setOnCircleProgressListener(this);
		mFileUtils.setOnDownloadSizeListener(this);
		mFileUtils.setOnCircleProgressBarListener(this);
		NetworkStateService mNetworkStateService = new NetworkStateService();
		mNetworkStateService.setOnNetworkStateChangedListener(this);
	}
	
	public boolean isNetworkConnected(Context context){
		if(context!=null){
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if(mNetworkInfo!=null){
				Log.e(TAG, "NETWORK: " + mNetworkInfo.isAvailable());
				return mNetworkInfo.isAvailable();
			}
		}
		return false;		
	}
	
	protected void noNetworkDialog(){
		Builder mBuilder = new Builder(MainActivity.this);
		mBuilder.setMessage("无网络连接！");
		mBuilder.setPositiveButton("OK", new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				arg0.dismiss();
				//MainActivity.this.finish();
			}
			
		} );
		mBuilder.create().show();
	}
	
	public boolean isWifiConnected(Context context){
		if(context!=null){
			ConnectivityManager mConnectivityManager =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if(mWifiNetworkInfo!=null){
				Log.e(TAG, "WIFI: " + mWifiNetworkInfo.isAvailable());
				return mWifiNetworkInfo.isAvailable();			
			}
		}
		return false;
	}
	
	private void showPopUpWindow(View v){
		LayoutInflater mInflater = LayoutInflater.from(this);
		View popView = mInflater.inflate(R.layout.pop_layout, null);
		
		PopupWindow mPopupWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT, 250);
		
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		
		

		mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]+1150);
		
	}
	
	private void initPopUpWindow(){
		LayoutInflater mInflater = LayoutInflater.from(this);
		View popView = mInflater.inflate(R.layout.pop_layout, null);
		mPopupWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		
	}
	
	public void showPopUpWindow(View parent, int gra, int x, int y){
		mPopupWindow.showAtLocation(parent, gra, x, y);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
	}

	@Override
	public void OnNetStateChanged() {
		// TODO Auto-generated method stub
		//queryChannelList();
		
	}
	
	public static void delete(File file) {  
		if (file.isFile()) {  
			file.delete();  
		    return;  
		}  
		  
		if(file.isDirectory()){  
		    File[] childFiles = file.listFiles();  
		    if (childFiles == null || childFiles.length == 0) {  
		    	file.delete();  
		        return;  
		    }  
		      
		   for (int i = 0; i < childFiles.length; i++) {  
		        delete(childFiles[i]);  
		   }  
		   file.delete();  
		}  
	}
		
}
