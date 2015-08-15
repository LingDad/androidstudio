package net.jianweixu.listennews;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

public class NetworkStateService extends Service{
	private static OnNetworkStateChangedListener mChangedListener;
	private static final String tag="tag";
	private ConnectivityManager connectivityManager;
    private NetworkInfo info;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.d(tag, "缃�缁���舵��宸茬����瑰��");
                connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();  
                if(info != null && info.isAvailable()) {
                    String name = info.getTypeName();
                    Log.d(tag, "褰����缃�缁����绉帮��" + name);
                    mChangedListener.OnNetStateChanged();
                    //doSomething()
                } else {
                    Log.d(tag, "娌℃�������ㄧ��缁�");
                  //doSomething()
                }
            }
            
        }
    };
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void setOnNetworkStateChangedListener(OnNetworkStateChangedListener listener){
		
		mChangedListener = listener;
	}

}

