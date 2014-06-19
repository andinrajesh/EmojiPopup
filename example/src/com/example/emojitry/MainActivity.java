package com.example.emojitry;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andinrajesh.emojicon.EmojiPopup;
import com.andinrajesh.emojicon.EmojiPopup.OnEmojiconBackspaceClickedListener;
import com.andinrajesh.emojicon.EmojiPopup.OnEmojiconClickedListener;
import com.andinrajesh.emojicon.emoji.Emojicon;


public class MainActivity extends Activity{

	private EditText mEditEmojicon;
	private TextView mTxtEmojicon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mEditEmojicon = (EditText) findViewById(R.id.editEmojicon);
		mTxtEmojicon = (TextView) findViewById(R.id.txtEmojicon);
		final Button b = (Button) findViewById(R.id.Button);

		final EmojiPopup popup = new EmojiPopup(this);
		popup.setHeight(400);
		popup.setWidth(LayoutParams.MATCH_PARENT);
		popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener(){

			@Override
			public void onEmojiconBackspaceClicked(View v) {
				EmojiPopup.backspace(mEditEmojicon);
			}
			
		});
		popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {
			
			@Override
			public void onEmojiconClicked(Emojicon emojicon) {
				EmojiPopup.input(mEditEmojicon, emojicon);	
			}
		});
		
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(popup.isShowing()){
					popup.dismiss();
				}else{
					popup.showAsDropDown(b, 0,0);
				}
			}
		});
		
		mEditEmojicon.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTxtEmojicon.setText(s);
            }
        });
		
	}
}
