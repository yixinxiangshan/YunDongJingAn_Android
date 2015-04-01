package com.ecloudiot.framework.view;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class VoteView extends LinearLayout {
	private final static String TAG = "VoteView";
	private ImageView[] stars;
	private String starIcon;
	private String starIconPress;
	private int starsCount;
	private int starScore;
	private int vote;
	private OnVoteListener voteListener;
	{
		setStarsCount(5);
		setStarScore(2);
		starIcon = "gen_star_stroke";
		starIconPress = "gen_star_solid";
		setScore(0);
	}

	public VoteView(Context context) {
		super(context);
	}

	public VoteView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LogUtil.d(TAG, "VoteView : constructor ...");
		initialise(attrs);
		initView();
	}

	private void initialise(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.VoteView);
		if (StringUtil.isNotEmpty(a.getString(R.styleable.VoteView_star_icon))) {
			starIcon = a.getString(R.styleable.VoteView_star_icon);
		}
		if (StringUtil.isNotEmpty(a.getString(R.styleable.VoteView_star_icon_press))) {
			starIconPress = a.getString(R.styleable.VoteView_star_icon_press);
		}
		setStarsCount(a.getInt(R.styleable.VoteView_stars_count, 5));
		setStarScore(a.getInt(R.styleable.VoteView_star_score, 2));
		a.recycle();
	}

	private void initView() {
		stars = new ImageView[5];
		int i = 0;
		for (ImageView starView : stars) {
			starView = new ImageView(getContext());
			stars[i] = starView;
			final int position = i;
			starView.setImageResource(ResourceUtil.getDrawableIdFromContext(getContext(), starIcon));
			starView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					setVote(position+1);
					return false;
				}
			});
			starView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (voteListener != null) {
						voteListener.onVote(VoteView.this,(position+1)*getStarScore());
					}
				}
			});
			starView.setPadding(8, 8, 8, 8);
			setOrientation(HORIZONTAL);
			addView(starView, i);
			i++;
		}
		refresh();
	}

	private void refresh() {
		if (stars == null) {
			return;
		}
		int i = 0;
		for (ImageView starView : stars) {
			if (i<getVote()) {
				starView.setImageResource(ResourceUtil.getDrawableIdFromContext(getContext(), starIconPress));
			}else {
				starView.setImageResource(ResourceUtil.getDrawableIdFromContext(getContext(), starIcon));
			}
			i++;
		}
	}
	
	public interface OnVoteListener{
		public void onVote(VoteView voteView,int score);
	}

	public String getStarIcon() {
		return starIcon;
	}

	public void setStarIcon(String starIcon) {
		this.starIcon = starIcon;
	}

	public String getStarIconPress() {
		return starIconPress;
	}

	public void setStarIconPress(String starIconPress) {
		this.starIconPress = starIconPress;
	}

	public void setOnVoteListener(OnVoteListener voteListener) {
		this.voteListener = voteListener;
	}

	public int getStarScore() {
		return starScore;
	}

	public void setStarScore(int starScore) {
		this.starScore = starScore;
	}

	public int getStarsCount() {
		return starsCount;
	}

	public void setStarsCount(int starsCount) {
		this.starsCount = starsCount;
	}

	private int getVote() {
		return vote;
	}

	private void setVote(int vote) {
		this.vote = vote;
		refresh();
	}
	public void setScore(int score) {
		setVote(score/starScore);
	}

}
