package com.galaxy.superstar;

import com.galaxy.superstar.PullHeadView.OnStateListerer;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class PullRefreshListView extends ListView implements OnScrollListener,OnStateListerer {
	
	private static final int STATE_NORMAL = 0;
	private static final int STATE_READY = 1;
	private static final int STATE_PULL = 2;
	private static final int STATE_UPDATING = 3;
	private static final int INVALID_POINTER_ID = -1;
	protected PullHeadView mListHeaderView;
	private int mActivePointerId;
	private float mLastY;
	private int mOffset;
	private int mState;
	private int mTouchSlop;
	private Context mContext;
	private PullHeadView mHeadView;
	protected boolean hasHeader = true;
	protected boolean hasFooter;
	protected boolean hasMoreData = true;
	protected boolean isCanLoadMore = true;
	protected boolean isNeedRetry = false;
	protected boolean isAutoLoading = true;
	protected boolean isBusy = false;
	protected OnRefreshListener mOnRefreshListener;
	protected OnScrollPositionListener mScrollPositionListener;
	
	
	public PullRefreshListView(Context context) {
		super(context);
		this.mContext = context;
		initView();
	}

	public PullRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView();
	}

	public PullRefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initView();
	}
	
	public void initView() {
		if (hasHeader) {
			mListHeaderView = new PullHeadView(mContext);
			mListHeaderView.setStateListener(this);
			setHeaderDividersEnabled(false);
			setFooterDividersEnabled(false);
			addHeaderView(mListHeaderView, null, false);
			mState = STATE_NORMAL;
			final ViewConfiguration configuration = ViewConfiguration.get(mContext);
			mTouchSlop = configuration.getScaledTouchSlop();
		}
		
		super.setOnScrollListener(this);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (hasHeader) {
			if (mState == STATE_UPDATING) {
				return super.dispatchTouchEvent(ev);
			}
			final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				mLastY = ev.getY();
				isFirstViewTop();
				break;
			case MotionEvent.ACTION_MOVE:
				if (mActivePointerId == INVALID_POINTER_ID) {
					break;
				}

				if (mState == STATE_NORMAL) {
					isFirstViewTop();
				}

				if (mState == STATE_READY) {
					final int activePointerId = mActivePointerId;
					final int activePointerIndex = MotionEventCompat
							.findPointerIndex(ev, activePointerId);
					final float y = MotionEventCompat.getY(ev,
							activePointerIndex);
					final int deltaY = (int) (y - mLastY);
//					mLastY = y;
					if (deltaY <= 0 || Math.abs(deltaY) < mTouchSlop) {
						mState = STATE_NORMAL;
					} else {
						mLastY = y;
						mState = STATE_PULL;
						ev.setAction(MotionEvent.ACTION_CANCEL);
						super.dispatchTouchEvent(ev);
					}
				}

				if (mState == STATE_PULL) {
					final int activePointerId = mActivePointerId;
					final int activePointerIndex = MotionEventCompat
							.findPointerIndex(ev, activePointerId);
					final float y = MotionEventCompat.getY(ev,
							activePointerIndex);
					final int deltaY = (int)(y - mLastY);
					mOffset += deltaY;
					mLastY = y;
					
					setHeaderHeight(mOffset * 4/9);
					return true;
				}

				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				mActivePointerId = INVALID_POINTER_ID;
				if (mState == STATE_PULL) {
					update();
				}
				break;
			case MotionEventCompat.ACTION_POINTER_DOWN:
				final int index = MotionEventCompat.getActionIndex(ev);
				final float y = MotionEventCompat.getY(ev, index);
				mLastY = y;
				mActivePointerId = MotionEventCompat.getPointerId(ev, index);
				break;
			case MotionEventCompat.ACTION_POINTER_UP:
				onSecondaryPointerUp(ev);
				break;
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastY = MotionEventCompat.getY(ev, newPointerIndex);
			mActivePointerId = MotionEventCompat.getPointerId(ev,
					newPointerIndex);
		}
	}

	private void setHeaderHeight(int height) {
		mListHeaderView.setHeaderHeight(height);
	}

	private boolean isFirstViewTop() {
		final int count = getChildCount();
		if (count == 0) {
			return true;
		}
		final int firstVisiblePosition = this.getFirstVisiblePosition();
		final View firstChildView = getChildAt(0);
		boolean needs = firstChildView.getTop() == 0
				&& (firstVisiblePosition == 0);
		if (needs) {
			mState = STATE_READY;
		}

		return needs;
	}

	/**
	 * 在下拉刷新完成后更换状态
	 * 
	 * @param refreshSuccess
	 *            是否获取数据成功
	 */
	public void onRefreshComplete(boolean refreshSuccess) {
		if (mListHeaderView != null) {
			if (mState == STATE_UPDATING) {
				mListHeaderView.reset(STATE_NORMAL);
			}
			if(refreshSuccess){
				mListHeaderView.updateLastTimeLable();
			}
		}
	}

	private void update() {
		if (mListHeaderView.isUpdateNeeded()) {
	          mListHeaderView.startUpdate();
	            mState = STATE_UPDATING;
			if (mOnRefreshListener != null) {
				mOnRefreshListener.onRefresh();
			}
		} else {
			mListHeaderView.reset(STATE_NORMAL);
		}
	}

	@Override
	public void onReset() {
		mState = STATE_NORMAL;
		mOffset = 0;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mScrollPositionListener != null) {
			mScrollPositionListener.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			isBusy = false;
			int first = getFirstVisiblePosition();
			int count = getChildCount();
			int nHeadCount = getHeaderViewsCount();
			int currPosition;
			for (int i = 0; i < count; i++) {
				currPosition = first - nHeadCount + i;
				if (currPosition == -nHeadCount) {
					continue;// 下拉刷新标题栏
				}
				if (currPosition >= getCount()) {// 加载更多布局显示的时候
					return;
				}
			}
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			isBusy = true;
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			isBusy = true;
			break;
		default:
			break;
		}

		if (mScrollPositionListener != null) {
			mScrollPositionListener.onScrollStateChanged(view, scrollState);
		}
	}

	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		mOnRefreshListener = onRefreshListener;
	}

	public void setPullTimeTag(String ptg) {
		mListHeaderView.setTimeTag(ptg);
	}

	public boolean isBusy() {
		return this.isBusy;
	}

	public void onRefresh() {
		if (mOnRefreshListener != null) {
			if (hasHeader) {
				mOnRefreshListener.onRefresh();
			}
		}
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public interface OnScrollPositionListener {
		public void onScrollStateChanged(AbsListView view, int scrollState);

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		return true;
	}


	public void startUpdateImmediate() {
		if (mState == STATE_UPDATING) {
			return;
		}
		setSelectionFromTop(0, 0);
		mListHeaderView.moveToUpdateHeight();
		update();
	}

	public OnScrollPositionListener getOnScrollPositionListener() {
		return mScrollPositionListener;
	}

	public void setOnScrollPositionListener(
			OnScrollPositionListener mScrollPositionListener) {
		this.mScrollPositionListener = mScrollPositionListener;
	}

	public void Clear() {
		if (mHeadView != null) {
			removeHeaderView(mHeadView);
		}
		removeAllViewsInLayout();
	}
}
