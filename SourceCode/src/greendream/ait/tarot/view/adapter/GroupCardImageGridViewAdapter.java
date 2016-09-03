package greendream.ait.tarot.view.adapter;

import greendream.ait.tarot.BuildConfig;
import greendream.ait.tarot.R;
import greendream.ait.tarot.activity.BrowseCardsActivity;
import greendream.ait.tarot.activity.BrowseGroupCardsActivity;
import greendream.ait.tarot.data.MapData;
import greendream.ait.tarot.data.ConfigData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupCardImageGridViewAdapter extends BaseAdapter implements
		OnTouchListener, OnClickListener {

	private Context mContext;
	private int imageEmptyCount;
	private int mNumColumns = 0;
	private int mTopBarHeight = 0;
	private int mBottomBarHeight = 0;

	public void setNumColumns(int numColumns) {
		// TODO Auto-generated method stub
		mNumColumns = numColumns;
	}

	public void setTopBarHeight(int height) {
		// TODO Auto-generated method stub
		mTopBarHeight = height;
	}

	public void setBottomBarHeight(int height) {
		// TODO Auto-generated method stub
		mBottomBarHeight = height;
	}

	public GroupCardImageGridViewAdapter(Context c) {
		mContext = c;
	}

	@Override
	public int getCount() {
		// the first mNumColumns for the start empty row
		// the second mNumColumns for the end empty row
		return mNumColumns + MapData.arrGroupCardImage_R_Id.length
				+ mNumColumns;
	}

	@Override
	public Object getItem(int position) {

		if (position - mNumColumns >= 0
				&& position - mNumColumns
						- MapData.arrGroupCardImage_R_Id.length < 0) {
			Log.w("INDEX", "" + (position - mNumColumns));
			return MapData.arrGroupCardImage_R_Id[position - mNumColumns];
		}

		return null;
	}

	@Override
	public long getItemId(int position) {

		if (position - mNumColumns >= 0
				&& position - mNumColumns
						- MapData.arrGroupCardImage_R_Id.length < 0) {
			return position - mNumColumns;
		}

		return 0;
	}

	@Override
	public int getViewTypeCount() {
		// Two types of views, the normal ImageView and the top row of empty
		// views
		return 4;
	}

	@Override
	public int getItemViewType(int position) {
		if (position - mNumColumns < 0) {
			return 0;
		}

		if (position - mNumColumns - MapData.arrGroupCardImage_R_Id.length < 0) {
			return 1;
		}

		imageEmptyCount = mNumColumns
				- (MapData.arrGroupCardImage_R_Id.length % mNumColumns);
		if (position - mNumColumns - MapData.arrGroupCardImage_R_Id.length
				- imageEmptyCount < 0) {
			return 2;
		}

		return 3;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {

		int w = BrowseCardsActivity.GoupCardFragment.mImageLoader
				.getImageWidth();
		int h = BrowseCardsActivity.GoupCardFragment.mImageLoader
				.getImageHeight();

		// First check if this is the top row
		if (position - mNumColumns < 0) {

			if (convertView == null) {
				convertView = new View(mContext);
			}
			// Set empty view with height of TopBar
			convertView.setLayoutParams(new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, mTopBarHeight));
			
			convertView.setOnClickListener(null);
			convertView.setOnLongClickListener(null);
			convertView.setLongClickable(false);
			convertView.setOnTouchListener(null);
			return convertView;
		}

		// Second check if this is the bottom row
		if (position - mNumColumns - MapData.arrGroupCardImage_R_Id.length >= 0) {

			if (convertView == null) {
				convertView = new View(mContext);
			}

			imageEmptyCount = mNumColumns
					- (MapData.arrGroupCardImage_R_Id.length % mNumColumns);

			if (position - mNumColumns - MapData.arrGroupCardImage_R_Id.length
					- imageEmptyCount < 0
					&& imageEmptyCount < mNumColumns) {
				// Set empty image view
				convertView.setLayoutParams(new GridView.LayoutParams(w, h));
			} else {
				// Set empty view with height of BototmBar
				convertView.setLayoutParams(new AbsListView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, mBottomBarHeight));
			}
			
			convertView.setOnClickListener(null);
			convertView.setOnLongClickListener(null);
			convertView.setLongClickable(false);
			convertView.setOnTouchListener(null);
			return convertView;
		}

		if (BuildConfig.DEBUG) {
			Log.w("position - mNumColumns = ", position + " - " + mNumColumns
					+ " = " + (position - mNumColumns));
		}

		// Inflater for custom cell in GridView
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.cell_grid_view_image_and_text,
					parent, false);
		}

		TextView tvCellGridName = (TextView) view
				.findViewById(R.id.tvCellGridName);
		tvCellGridName.setTypeface(ConfigData.UVNCatBien_R);
		tvCellGridName
				.setText(MapData.arrGroupCardName[position - mNumColumns]);

		ImageView ivCellGridImage = (ImageView) view.findViewById(R.id.ivCellGridImage);
		ivCellGridImage.setLayoutParams(new LinearLayout.LayoutParams(w, h));

		// Finally load the image asynchronously into the ImageView, this also
		// takes care of
		// setting a placeholder image while the background thread runs
		BrowseCardsActivity.GoupCardFragment.mImageLoader.loadImage(
				MapData.arrGroupCardImage_R_Id[position - mNumColumns] + "_"
						+ w + "_" + h, ivCellGridImage);

		view.setTag(position - mNumColumns);
		view.setOnTouchListener(this);
		view.setOnClickListener(this);

		return view;

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			v.startAnimation(ConfigData.animation_button_press);
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		// Reclaim memory and cancel all background task
		BrowseCardsActivity.GoupCardFragment.mInstance.restartCacheToClaimMemory();
		
		// TODO Auto-generated method stub
		int position = Integer.parseInt(v.getTag().toString());
		Intent intent = new Intent(mContext, BrowseGroupCardsActivity.class);
		intent.putExtra("mode", position);

		mContext.startActivity(intent);
	}

}