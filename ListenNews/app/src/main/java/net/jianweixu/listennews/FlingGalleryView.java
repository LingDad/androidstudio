package net.jianweixu.listennews;





import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class FlingGalleryView extends ViewGroup {
	private OnViewChangeListener mOnViewChangeListener;
	private static final int SNAP_VELOCITY = 1000;
	// ŒÇÂŒµ±Ç°ÆÁÄ»ÏÂ±ê£¬È¡Öµ·¶Î§ÊÇ£º0 µœ getChildCount()-1
	public int mCurrentScreen;
	private Scroller mScroller;
	// ËÙ¶È×·×ÙÆ÷£¬Ö÷ÒªÊÇÎªÁËÍš¹ýµ±Ç°»¬¶¯ËÙ¶ÈÅÐ¶Ïµ±Ç°»¬¶¯ÊÇ·ñÎªfling
	private VelocityTracker mVelocityTracker;
	// ŒÇÂŒ»¬¶¯Ê±ÉÏŽÎÊÖÖžËùŽŠµÄÎ»ÖÃ
	private float mLastMotionX;
	private float mLastMotionY;
	// Touch×ŽÌ¬Öµ 0£ºŸ²Ö¹ 1£º»¬¶¯
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	// ŒÇÂŒµ±Ç°touchÊÂŒþ×ŽÌ¬--»¬¶¯£šTOUCH_STATE_SCROLLING£©¡¢Ÿ²Ö¹£šTOUCH_STATE_REST Ä¬ÈÏ£©
	private int mTouchState = TOUCH_STATE_REST;
	// ŒÇÂŒtouchÊÂŒþÖÐ±»ÈÏÎªÊÇ»¬¶¯ÊÂŒþÇ°µÄ×îŽó¿É»¬¶¯ŸàÀë
	private int mTouchSlop;
	// ÊÖÖžÅ×¶¯×÷µÄ×îŽóËÙ¶Èpx/s Ã¿Ãë¶àÉÙÏñËØ
	private int mMaximumVelocity;
	// ¹ö¶¯µœÖž¶šÆÁÄ»µÄÊÂŒþ
	private OnScrollToScreenListener mScrollToScreenListener;
	// ×Ô¶šÒåtouchÊÂŒþ
	private OnCustomTouchListener mCustomTouchListener;
	//¹ö¶¯µœÃ¿žöÆÁÄ»Ê±ÊÇ·ñ¶ŒÒªŽ¥·¢OnScrollToScreenListenerÊÂŒþ
	private boolean isEveryScreen=false;
	
	
	public FlingGalleryView(Context context) {
		super(context);
		init();
		mCurrentScreen = 0;
	}

	public FlingGalleryView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FlingGalleryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.FlingGalleryView, defStyle, 0);
		mCurrentScreen = a.getInt(R.styleable.FlingGalleryView_defaultScreen, 0);
		a.recycle();
		init();
	}

	
	private void init() {
		mScroller = new Scroller(getContext());
		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	// ±£Ö€ÔÚÍ¬Ò»žöÆÁÄ»ÖŽÐÐÒ»ÏÂÇÐÆÁÊÂŒþµÄÒ»Ð©²ÎÊý
	private int count = -1;
	private int defaultScreen = -1;

	// µ±¹ö¶¯Ìõ»¬¶¯Ê±µ÷ÓÃ£¬startScroll()ÉèÖÃµÄÊÇ²ÎÊý£¬ÊµŒÊ»¬¶¯£¬ÔÚÆäÀïÖŽÐÐ£¬
	@Override
	public void computeScroll() {
		// mScroller.computeScrollOffsetŒÆËãµ±Ç°ÐÂµÄÎ»ÖÃ£¬true±íÊŸ»¹ÔÚ»¬¶¯£¬ÈÔÐèŒÆËã
		if (mScroller.computeScrollOffset()) {
			// ·µ»Øtrue£¬ËµÃ÷scroll»¹Ã»ÓÐÍ£Ö¹
			scrollTo(mScroller.getCurrX(), 0);
			if(isEveryScreen)singleScrollToScreen();
			postInvalidate();
		}
	}

	// ±£Ö€ÔÚÍ¬Ò»žöÆÁÄ»ÖŽÐÐÒ»ÏÂÇÐÆÁÊÂŒþ
	private void singleScrollToScreen() {
		final int screenWidth = getWidth();
		int whichScreen = (getScrollX() + (screenWidth / 2)) / screenWidth;
		if (whichScreen > (getChildCount() - 1)) {
			return;
		}
		if (defaultScreen == -1) {
			defaultScreen = whichScreen;
			count = 1;
		} else {
			if (defaultScreen == whichScreen && count == 0) {
				count = 1;
			} else {
				if (defaultScreen != whichScreen) {
					defaultScreen = whichScreen;
					count = 0;
				}
			}
		}
		if (count == 0) {
			if (mScrollToScreenListener != null) {
				mScrollToScreenListener.operation(whichScreen, getChildCount());
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurrentScreen * width, 0);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int childLeft = 0;
		// ºáÏòÆœÆÌchildView
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			child.setOnTouchListener(childTouchListener);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth,
						child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	// Éè¶šchildViewµÄTouchÊÂŒþ·µ»Øtrue£¬ÕâÑù¿ÉÒÔÔÚparentViewÖÐœØ»ñtouch£šŒŽonInterceptTouchEvent£©µÄmove,upµÈÊÂŒþ
	private OnTouchListener childTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			return true;
		}
	};

	// ÔÚÏµÍ³ÏòžÃViewGroupŒ°Æäž÷žöchildViewŽ¥·¢onTouchEvent()Ö®Ç°¶ÔÏà¹ØÊÂŒþœøÐÐÒ»ŽÎÀ¹œØ
	/*
	 * downÊÂŒþÊ×ÏÈ»áŽ«µÝµœonInterceptTouchEvent()·œ·š
	 * Èç¹ûžÃViewGroupµÄonInterceptTouchEvent()ÔÚœÓÊÕµœdownÊÂŒþŽŠÀíÍê³ÉÖ®ºóreturn
	 * false£¬ÄÇÃŽºóÐøµÄmove,
	 * upµÈÊÂŒþœ«ŒÌÐø»áÏÈŽ«µÝžøžÃViewGroup£¬Ö®ºó²ÅºÍdownÊÂŒþÒ»ÑùŽ«µÝžø×îÖÕµÄÄ¿±êviewµÄonTouchEvent()ŽŠÀí¡£
	 * Èç¹ûžÃViewGroupµÄonInterceptTouchEvent()ÔÚœÓÊÕµœdownÊÂŒþŽŠÀíÍê³ÉÖ®ºóreturn
	 * true£¬ÄÇÃŽºóÐøµÄmove,
	 * upµÈÊÂŒþœ«²»ÔÙŽ«µÝžøonInterceptTouchEvent()£¬¶øÊÇºÍdownÊÂŒþÒ»ÑùŽ«µÝžøžÃViewGroupµÄonTouchEvent
	 * ()ŽŠÀí£¬×¢Òâ£¬Ä¿±êviewœ«œÓÊÕ²»µœÈÎºÎÊÂŒþ¡£
	 * Èç¹û×îÖÕÐèÒªŽŠÀíÊÂŒþµÄviewµÄonTouchEvent()·µ»ØÁËfalse£¬ÄÇÃŽžÃÊÂŒþœ«±»Ž«µÝÖÁÆäÉÏÒ»²ãŽÎµÄviewµÄonTouchEvent
	 * ()ŽŠÀí¡£ Èç¹û×îÖÕÐèÒªŽŠÀíÊÂŒþµÄview
	 * µÄonTouchEvent()·µ»ØÁËtrue£¬ÄÇÃŽºóÐøÊÂŒþœ«¿ÉÒÔŒÌÐøŽ«µÝžøžÃviewµÄonTouchEvent()ŽŠÀí¡£
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mCustomTouchListener != null) {
			mCustomTouchListener.operation(ev);
		}
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			// ŒÆËãX·œÏòÒÆ¶¯µÄŸàÀë
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			final int touchSlop = mTouchSlop;
			if (xDiff > touchSlop) {
				// ÒÆ¶¯·œÏòÐ¡ÓÚ45¶ÈÊ±ŒŽX·œÏò¿ÉÒÔÒÆ¶¯
				if (Math.abs(mLastMotionY - y) / Math.abs(mLastMotionX - x) < 1) {
					mTouchState = TOUCH_STATE_SCROLLING;
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			Log.e("FLING", "Intercept mLastMOntion: " + mLastMotionX);
			mLastMotionY = y;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.e("Fling","fffffffff!!");
		
		if (mVelocityTracker == null) {
			Log.e("Fling","vvvvvvv!!");
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
		final int action = ev.getAction();
		final float x = ev.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.e("FLING", "ACTION_DOWN");
			if (!mScroller.isFinished()) {
				// ÖÕÖ¹¹ö¶¯ÌõµÄ»¬¶¯¶¯»­
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			count = -1;
			defaultScreen = -1;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.e("FLING", "ACTION_MOVE" + TOUCH_STATE_SCROLLING + mTouchState);
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final float t_width = (getWidth() / 4f);
				// ×îºóÒ»žöÆÁÄ»Ïò×óÒÆ¶¯Ê±£¬²»ÄÜ³¬¹ýÆÁÄ»µÄ4·ÖÖ®Ò»
				if (getScrollX() > ((getChildCount() - 1) * getWidth() + t_width)) {
					break;
				}
				// µÚÒ»žöÆÁÄ»ÏòÓÒÒÆ¶¯Ê±£¬²»ÄÜ³¬¹ýÆÁÄ»µÄ4·ÖÖ®Ò»
				if (getScrollX() < ((t_width) * -1)) {
					break;
				}
				final int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;
				scrollBy(deltaX, 0);
				Log.e("FLING", "SCROLL deltaX: " + deltaX);
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.e("FLING", "ACTION_UP");
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);// Ê¹ÓÃpix/sÎªµ¥Î»
				int velocityX = (int) velocityTracker.getXVelocity();
				if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
					// ÏòÓÒÒÆ¶¯
					snapToScreen(mCurrentScreen - 1, false);
				} else if (velocityX < -SNAP_VELOCITY
						&& mCurrentScreen < getChildCount() - 1) {
					// Ïò×óÒÆ¶¯
					snapToScreen(mCurrentScreen + 1, false);
				} else {
					snapToDestination();
				}
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.e("FLING", "ACTION_CANCEL");
			mTouchState = TOUCH_STATE_REST;
		}
		return true;
	}
	
	
	public boolean onTouchEvent(MotionEvent ev, int mTouchState) {
		Log.e("Fling","fffffffff!!");
		
		if (mVelocityTracker == null) {
			Log.e("Fling","vvvvvvv!!");
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
		final int action = ev.getAction();
		final float x = ev.getX();
		Log.e("FLING", "x is: " + x);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.e("FLING", "ACTION_DOWN");
			if (!mScroller.isFinished()) {
				// ÖÕÖ¹¹ö¶¯ÌõµÄ»¬¶¯¶¯»­
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			count = -1;
			defaultScreen = -1;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.e("FLING", "ACTION_MOVE" + TOUCH_STATE_SCROLLING + mTouchState);
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final float t_width = (getWidth() / 4f);
				// ×îºóÒ»žöÆÁÄ»Ïò×óÒÆ¶¯Ê±£¬²»ÄÜ³¬¹ýÆÁÄ»µÄ4·ÖÖ®Ò»
				if (getScrollX() > ((getChildCount() - 1) * getWidth() + t_width)) {
					break;
				}
				// µÚÒ»žöÆÁÄ»ÏòÓÒÒÆ¶¯Ê±£¬²»ÄÜ³¬¹ýÆÁÄ»µÄ4·ÖÖ®Ò»
				if (getScrollX() < ((t_width) * -1)) {
					break;
				}
				final int deltaX = (int) Math.abs(x - mLastMotionX);
				mLastMotionX = x;
				scrollBy(deltaX, 0);
				Log.e("FLING", "SCROLL deltaX: " + deltaX);
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.e("FLING", "ACTION_UP");
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);// Ê¹ÓÃpix/sÎªµ¥Î»
				int velocityX = (int) velocityTracker.getXVelocity();
				if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
					// ÏòÓÒÒÆ¶¯
					snapToScreen(mCurrentScreen - 1, false);
				} else if (velocityX < -SNAP_VELOCITY
						&& mCurrentScreen < getChildCount() - 1) {
					// Ïò×óÒÆ¶¯
					
					snapToScreen(mCurrentScreen + 1, false);
				} else {
					snapToDestination();
				}
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.e("FLING", "ACTION_CANCEL");
			mTouchState = TOUCH_STATE_REST;
		}
		return true;
	}

	// ŒÆËãÓŠžÃÈ¥ÄÄžöÆÁ
	private void snapToDestination() {
		final int screenWidth = getWidth();
		// Èç¹û³¬¹ýÆÁÄ»µÄÒ»°ëŸÍËãÊÇÏÂÒ»žöÆÁ
		final int whichScreen = (getScrollX() + (screenWidth / 2))/ screenWidth;
		snapToScreen(whichScreen, false);
		
	}

	// ÇÐ»»ÆÁÄ»
	private void snapToScreen(int whichScreen, boolean isJump) {
		// ÅÐ¶ÏÏÂÒ»žöÆÁÄ»ÊÇ·ñÓÐÐ§£¬²¢ŸÀÕý
		Log.e("FLING", "SNAP TO ... and whichscreen is: " + whichScreen + ". getwidth is: " + getWidth() + ". getscroll is: " + getScrollX() + isJump);
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		
		if (getScrollX() != (whichScreen * getWidth())) {
			final int delta = whichScreen * getWidth() - getScrollX();
			count = -1;
			defaultScreen = -1;
			// ¿ªÊŒ¹ö¶¯¶¯»­
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);
			final int t_mCurrentScreen = mCurrentScreen;
			mCurrentScreen = whichScreen;
			Log.e("xujianwei", "Current screen is " + mCurrentScreen);
			// ÅÐ¶ÏÊÇ·ñÔÚÍ¬Ò»žöÆÁÄ»£¬²»ÔÚÔòÖŽÐÐÇÐ»»ÆÁÄ»
			if (t_mCurrentScreen != whichScreen) {
				// ·ÀÖ¹ÖØžŽÖŽÐÐÇÐ»»ÆÁÄ»ÊÂŒþ
				if (Math.abs(t_mCurrentScreen - whichScreen) == 1 && !isJump) {
					Log.e("FLING","DO ON SRCOLL SRCEEN!");
					doOnScrollToScreen();
				}
			}
			invalidate();
			if (mOnViewChangeListener != null){
				Log.e("xujianwei", "Current screen is " + mCurrentScreen);
				mOnViewChangeListener.OnViewChange(mCurrentScreen);
			}
		}
	}
	
	public void snapToScreen(int whichScreen, boolean isJump, int key) {
		// ÅÐ¶ÏÏÂÒ»žöÆÁÄ»ÊÇ·ñÓÐÐ§£¬²¢ŸÀÕý
		
		//whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		int getScrollX = 800;
		int getWidth = 1080;
		Log.e("FLING", "SNAP TO ... and whichscreen is: " + whichScreen + ". getwidth is: " + getWidth + ". getscroll is: " + getScrollX + isJump);
		if (getScrollX != (whichScreen * getWidth)) {
			final int delta = whichScreen * getWidth - getScrollX;
			count = -1;
			defaultScreen = -1;
			// ¿ªÊŒ¹ö¶¯¶¯»­
			mScroller.startScroll(getScrollX, 0, delta, 0,
					Math.abs(delta) * 2);
			final int t_mCurrentScreen = 0;
			mCurrentScreen = whichScreen;
			Log.e("xujianwei", "Current screen is " + mCurrentScreen);
			// ÅÐ¶ÏÊÇ·ñÔÚÍ¬Ò»žöÆÁÄ»£¬²»ÔÚÔòÖŽÐÐÇÐ»»ÆÁÄ»
			if (t_mCurrentScreen != whichScreen) {
				// ·ÀÖ¹ÖØžŽÖŽÐÐÇÐ»»ÆÁÄ»ÊÂŒþ
				if (Math.abs(t_mCurrentScreen - whichScreen) == 1 && !isJump) {
					Log.e("FLING","DO ON SRCOLL SRCEEN!");
					doOnScrollToScreen();
				}
			}
			invalidate();
			if (mOnViewChangeListener != null){
				Log.e("xujianwei", "Current screen is " + mCurrentScreen);
				mOnViewChangeListener.OnViewChange(mCurrentScreen);
			}
		}
	}

	private void doOnScrollToScreen() {
		
		if (mScrollToScreenListener != null) {
			mScrollToScreenListener.operation(mCurrentScreen, getChildCount());
			
		}
	}
	
    public void setOnViewChangeListener(OnViewChangeListener listener){
    	mOnViewChangeListener = listener;
    }
    

	/**
	 * ÉèÖÃÇÐ»»µœµÄÖž¶šÏÂ±êÆÁÄ»0ÖÁgetChildCount()-1
	 * */
	public void setToScreen(int whichScreen, boolean isAnimation) {
		if (isAnimation) {
			Log.e("FLING", "WHICK SCREEN IS: " + whichScreen);
			snapToScreen(whichScreen, true);
		} else {
			whichScreen = Math.max(0,
					Math.min(whichScreen, getChildCount() - 1));
			mCurrentScreen = whichScreen;
			// Ö±œÓ¹ö¶¯µœžÃÎ»ÖÃ
			Log.e("FLING", "WHICK SRCREEN IS:" + whichScreen + ". getWidth is: " + getWidth());
			scrollTo(whichScreen * getWidth(), 0);
			if (whichScreen != mCurrentScreen) {
				doOnScrollToScreen();
			}
			invalidate();
		}
	}

	/**
	 * ÉèÖÃÄ¬ÈÏÆÁÄ»µÄÏÂ±ê
	 * */
	public void setDefaultScreen(int defaultScreen) {
		mCurrentScreen = defaultScreen;
	}

	/**
	 * »ñÈ¡µ±Ç°ÆÁÄ»µÄÏÂ±ê
	 * */
	public int getCurrentScreen() {
		return mCurrentScreen;
	}

	/**
	 * ×¢²á¹ö¶¯µœÖž¶šÆÁÄ»µÄÊÂŒþ
	 * */
	public void setOnScrollToScreenListener(
			OnScrollToScreenListener scrollToScreenListener) {
		if (scrollToScreenListener != null) {
			this.mScrollToScreenListener = scrollToScreenListener;
		}
	}

	/**
	 * ×¢²á×Ô¶šÒåTouchÊÂŒþ
	 * */
	public void setOnCustomTouchListener(
			OnCustomTouchListener customTouchListener) {
		if (customTouchListener != null) {
			this.mCustomTouchListener = customTouchListener;
		}
	}

	/**
	 * ¹ö¶¯µœÖž¶šÆÁÄ»µÄÊÂŒþ£šŒŽÇÐÆÁÊÂŒþ£©
	 * */
	public interface OnScrollToScreenListener {
		public void operation(int currentScreen, int screenCount);
	}

	/**
	 * ×Ô¶šÒåµÄÒ»žöTouchÊÂŒþ
	 * */
	public interface OnCustomTouchListener {
		public void operation(MotionEvent event);
	}
	
	/**
	 * ¹ö¶¯µœÃ¿žöÆÁÄ»Ê±ÊÇ·ñ¶ŒÒªŽ¥·¢OnScrollToScreenListenerÊÂŒþ
	 * */
	public void setEveryScreen(boolean isEveryScreen) {
		this.isEveryScreen = isEveryScreen;
	}
}
