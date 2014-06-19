/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.andinrajesh.emojicon;

import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.andinrajesh.emojicon.EmojiAdapter.OnEmojiItemClickedListener;
import com.andinrajesh.emojicon.emoji.Emojicon;
import com.andinrajesh.emojicon.emoji.Nature;
import com.andinrajesh.emojicon.emoji.Objects;
import com.andinrajesh.emojicon.emoji.People;
import com.andinrajesh.emojicon.emoji.Places;
import com.andinrajesh.emojicon.emoji.Symbols;

public class EmojiPopup extends PopupWindow implements OnPageChangeListener{
	private Context context;
	private OnEmojiconBackspaceClickedListener mOnEmojiconBackspaceClickedListener;
	private OnEmojiconClickedListener mOnEmojiconClickedListener;
	private int mEmojiTabLastSelectedIndex = -1;
	private View[] mEmojiTabs;
	private ViewPager emojisPager;
	
	
	public EmojiPopup(Context context){
		super(context);
		this.context = context;
		setEmojiLayout();
	}

	public void setOnEmojiconBackspaceClickedListener(OnEmojiconBackspaceClickedListener listener){
		if(listener != null)
			mOnEmojiconBackspaceClickedListener = listener;
	}


	public void setOnEmojiconClickedListener(OnEmojiconClickedListener listener){
		if(listener != null){
			mOnEmojiconClickedListener = listener;
		}
	}

	
	private void setEmojiLayout() {
		View view = LayoutInflater.from(context).inflate(R.layout.emojicons, null);
		emojisPager = (ViewPager) view.findViewById(R.id.emojis_pager);
		ViewPagerAdapter emojisAdapter = new ViewPagerAdapter();
		emojisPager.setAdapter(emojisAdapter);
		emojisPager.setOnPageChangeListener(this);
		mEmojiTabs = new View[5];
		mEmojiTabs[0] = view.findViewById(R.id.emojis_tab_0_people);
		mEmojiTabs[1] = view.findViewById(R.id.emojis_tab_1_nature);
		mEmojiTabs[2] = view.findViewById(R.id.emojis_tab_2_objects);
		mEmojiTabs[3] = view.findViewById(R.id.emojis_tab_3_cars);
		mEmojiTabs[4] = view.findViewById(R.id.emojis_tab_4_punctuation);
		for (int i = 0; i < mEmojiTabs.length; i++) {
			final int position = i;
			mEmojiTabs[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					emojisPager.setCurrentItem(position);
				}
			});
		}

		view.findViewById(R.id.emojis_backspace).setOnTouchListener(new RepeatListener(1000, 50, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnEmojiconBackspaceClickedListener != null) {
					mOnEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);
				}
			}
		}));	
		setContentView(view);
	}

	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff) {
		super.showAsDropDown(anchor, xoff, yoff);
		onPageSelected(0);
	}




	public class ViewPagerAdapter extends PagerAdapter implements OnEmojiItemClickedListener{
		private GridView emojiGrid;

		public ViewPagerAdapter() {

		}

		public int getCount() {
			return 5;
		}

		public Object instantiateItem(View collection, int position) {
			Emojicon[] data = null;
			View v = LayoutInflater.from(context).inflate(R.layout.emojicon_grid, null);
			emojiGrid = (GridView) v.findViewById(R.id.Emoji_GridView);
			
			switch (position) {
			case 0:
				data = People.DATA;
				break;

			case 1:
				data = Objects.DATA;
				break;

			case 2:
				data = Nature.DATA;
				break;

			case 3:
				data = Places.DATA;
				break;

			case 4:
				data = Symbols.DATA;
				break;

			default:
				data =  Places.DATA;
				break;
			}
			EmojiAdapter adapter = new EmojiAdapter(context, data);
			adapter.setOnEmojiItemClickedListener(this);
			emojiGrid.setAdapter(adapter);
			((ViewPager) collection).addView(v, 0);
			return v;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void OnEmojiItemClicked(Emojicon emojicon) {
			mOnEmojiconClickedListener.onEmojiconClicked(emojicon);
		}
	}

	public static void input(EditText editText, Emojicon emojicon) {
		if (editText == null || emojicon == null) {
			return;
		}

		int start = editText.getSelectionStart();
		int end = editText.getSelectionEnd();
		if (start < 0) {
			editText.append(emojicon.getEmoji());
		} else {
			editText.getText().replace(Math.min(start, end), Math.max(start, end), emojicon.getEmoji(), 0, emojicon.getEmoji().length());
		}
	}

	public static void backspace(EditText editText) {
		KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
		editText.dispatchKeyEvent(event);
	}

	public static class RepeatListener implements View.OnTouchListener {

		private Handler handler = new Handler();

		private int initialInterval;
		private final int normalInterval;
		private final View.OnClickListener clickListener;

		private Runnable handlerRunnable = new Runnable() {
			@Override
			public void run() {
				if (downView == null) {
					return;
				}
				handler.removeCallbacksAndMessages(downView);
				handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
				clickListener.onClick(downView);
			}
		};

		private View downView;

		/**
		 * @param initialInterval The interval before first click event
		 * @param normalInterval  The interval before second and subsequent click
		 *                        events
		 * @param clickListener   The OnClickListener, that will be called
		 *                        periodically
		 */
		public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
			if (clickListener == null)
				throw new IllegalArgumentException("null runnable");
			if (initialInterval < 0 || normalInterval < 0)
				throw new IllegalArgumentException("negative interval");

			this.initialInterval = initialInterval;
			this.normalInterval = normalInterval;
			this.clickListener = clickListener;
		}

		public boolean onTouch(View view, MotionEvent motionEvent) {
			switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downView = view;
				handler.removeCallbacks(handlerRunnable);
				handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
				clickListener.onClick(view);
				return true;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_OUTSIDE:
				handler.removeCallbacksAndMessages(downView);
				downView = null;
				return true;
			}
			return false;
		}
	}

	public interface OnEmojiconBackspaceClickedListener {
		void onEmojiconBackspaceClicked(View v);
	}

	public interface OnEmojiconClickedListener {
		void onEmojiconClicked(Emojicon emojicon);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}


	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub	
	}


	@Override
	public void onPageSelected(int i) {
		if (mEmojiTabLastSelectedIndex == i) {
			return;
		}
		switch (i) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
			if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiTabs.length) {
				mEmojiTabs[mEmojiTabLastSelectedIndex].setSelected(false);
			}
			mEmojiTabs[i].setSelected(true);
			mEmojiTabLastSelectedIndex = i;
			emojisPager.setCurrentItem(i);
			break;
		}
	}
}
