EmojiPopup
==========

Android Emoji library with Popup window.


SampleCode
==========
    EditText mEditEmojicon = new EditText();
    TextView mTxtEmojicon = new TextView();
    EmojiPopup popup = new EmojiPopup(this);
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


Screen Shot
===========
![Alt text](https://github.com/andinrajesh/EmojiPopup/blob/master/library/res/drawable-ldpi/Sample.png?raw=true "Optional Title")




EmojiPopup is modification of https://github.com/rockerhieu/emojicon library.
