package greendream.ait.tarot.activity;

import greendream.ait.tarot.R;
import greendream.ait.tarot.R.id;
import greendream.ait.tarot.data.MapData;
import greendream.ait.tarot.data.ConfigData;
import greendream.ait.tarot.util.ImageLoaderAsynch;
import greendream.ait.tarot.util.Utils;
import greendream.ait.tarot.util.ImageCache.ImageCacheParams;
import greendream.ait.tarot.view.adapter.CardImageGridViewAdapter;
import greendream.ait.tarot.view.adapter.CardImageSectionListViewAdapter;
import greendream.ait.tarot.view.adapter.GroupCardImageGridViewAdapter;
import greendream.ait.tarot.view.adapter.GroupNumberImageGridViewAdapter;
import greendream.ait.tarot.view.adapter.GroupStarImageGridViewAdapter;
import greendream.ait.tarot.view.adapter.GroupSuitImageGridViewAdapter;
import greendream.ait.tarot.view.adapter.GroupSymbolImageGridViewAdapter;
import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class BrowseGroupCardsActivity extends FragmentActivity implements
		OnClickListener {

	public static Context mContext;
	public static final String BROWSE_GROUP_IMAGE_CACHE_DIR = "browse_group_image_cache";

	// Has 4 levels (0 --> 3) default with level 1 and 5 columns
	public static int zoom_level = 0;
	private TextView tvTitle;
	private ImageButton btn_home;

	// Fragment to show Browse mode
	GroupSuitCardFragment mGroupSuitCardFragment;
	GroupNumberCardFragment mGroupNumberCardFragment;
	GroupSymbolCardFragment mGroupSymbolCardFragment;
	GroupStarCardFragment mGroupStarCardFragment;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_group_cards);
		
		// Reload screen size and background
		ConfigData.reloadScreen(this);
				
		// Load background
		((ImageView) findViewById(R.id.background))
				.setBackgroundDrawable(ConfigData.rbdBackground);

		mContext = this.getApplicationContext();

		int mode = this.getIntent().getExtras().getInt("mode");

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(ConfigData.UVNCatBien_R);

		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_home.setOnClickListener(this);

		// Replace container by mGridCardFragment
		selectBrowserMode(mode);

	}
	
	@Override
	protected void onResume() {
		// Load background
		((ImageView) findViewById(R.id.background))
				.setBackgroundDrawable(ConfigData.rbdBackground);
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		ConfigData.IS_USER_DESTROY_BY_BACK_BUTTON = true;
		super.onBackPressed();
	}

	/**
	 * select browser view mode
	 * 
	 * @param mode
	 */
	public void selectBrowserMode(int mode) {

		FragmentManager fragmentManager = getSupportFragmentManager();
		tvTitle.setText(MapData.arrGroupCardName[mode]);

		switch (mode) {
		case 0: // Grid Suit card mode
			if (mGroupSuitCardFragment == null) {
				mGroupSuitCardFragment = new GroupSuitCardFragment();
			}
			fragmentManager.beginTransaction()
					.replace(R.id.browse_container, mGroupSuitCardFragment)
					.commit();
			break;

		case 1: // Grid Star card mode
			if (mGroupStarCardFragment == null) {
				mGroupStarCardFragment = new GroupStarCardFragment();
			}
			fragmentManager.beginTransaction()
					.replace(R.id.browse_container, mGroupStarCardFragment)
					.commit();
			break;

		case 2: // Grid Number card mode
			if (mGroupNumberCardFragment == null) {
				mGroupNumberCardFragment = new GroupNumberCardFragment();
			}
			fragmentManager.beginTransaction()
					.replace(R.id.browse_container, mGroupNumberCardFragment)
					.commit();
			break;

		case 3: // Grid Symbol card mode
			if (mGroupSymbolCardFragment == null) {
				mGroupSymbolCardFragment = new GroupSymbolCardFragment();
			}
			fragmentManager.beginTransaction()
					.replace(R.id.browse_container, mGroupSymbolCardFragment)
					.commit();
			break;

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_home) {
			this.finish();
			this.startActivity(new Intent(this.getApplicationContext(),
					HomeActivity.class));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.encyclopedia_menu, menu);
		return true;
	}

	public static class GroupSuitCardFragment extends
			android.support.v4.app.Fragment {

		public static int cell_width = 0;
		private GridView gridview;
		private GroupSuitImageGridViewAdapter mGroupSuitImageGridViewAdapter;
		public static ImageLoaderAsynch mImageLoader;
		public static GroupSuitCardFragment mInstance;

		public GroupSuitCardFragment() {
			mInstance = this;
		}

		private void createImageLoader() {
			// TODO INIT FOR IMAGE CACHE
			ImageCacheParams cacheParams = new ImageCacheParams(getActivity(),
					BROWSE_GROUP_IMAGE_CACHE_DIR);

			cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to
														// 25% of app memory

			// The ImageFetcher takes care of loading images into our ImageView
			// children asynchronously
			mImageLoader = new ImageLoaderAsynch(getActivity());
			mImageLoader.setImageSize((ConfigData.SCREEN_WIDTH - 50) / 3);
			mImageLoader.setLoadingImage(null);
			mImageLoader.addImageCache(getActivity()
					.getSupportFragmentManager(), cacheParams);
			mImageLoader.setImageFadeIn(false);
		}

		/**
		 * Reclaim memory and cancel all background task
		 */
		public void restartCacheToClaimMemory() {
			// TODO Auto-generated method stub
			mImageLoader.clearMemCache();
			mImageLoader.setPauseWork(false);
			mImageLoader.setExitTasksEarly(true);
			mImageLoader.flushCache();
			mImageLoader.closeCache();
			createImageLoader();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			createImageLoader();

			View view = inflater.inflate(R.layout.fragment_gridcard, container, false);

			mGroupSuitImageGridViewAdapter = new GroupSuitImageGridViewAdapter(view.getContext());

			gridview = (GridView) view.findViewById(R.id.gridview);
			gridview.setAdapter(mGroupSuitImageGridViewAdapter);
			cell_width = (ConfigData.SCREEN_WIDTH - 40) / 2;
			gridview.setColumnWidth(cell_width);
			return view;
		}

		@Override
		public void onResume() {
			super.onResume();
			mImageLoader.setExitTasksEarly(false);
			mGroupSuitImageGridViewAdapter.notifyDataSetChanged();
		}

		@Override
		public void onPause() {
			super.onPause();
			mImageLoader.setPauseWork(false);
			mImageLoader.setExitTasksEarly(true);
			mImageLoader.flushCache();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mImageLoader.clearMemCache();
			if (ConfigData.IS_USER_DESTROY_BY_BACK_BUTTON) {
				mImageLoader.clearDiskCache();
				ConfigData.IS_USER_DESTROY_BY_BACK_BUTTON = false;
			}
			mImageLoader.closeCache();

		}

	}

	public static class GroupNumberCardFragment extends
			android.support.v4.app.Fragment {

		public static int cell_width = 0;
		private GridView gridview;
		private GroupNumberImageGridViewAdapter mGroupNumberImageGridViewAdapter;
		public static ImageLoaderAsynch mImageLoader;
		public static GroupNumberCardFragment mInstance;

		public GroupNumberCardFragment() {
			mInstance = this;
		}

		private void createImageLoader() {
			// TODO INIT FOR IMAGE CACHE
			ImageCacheParams cacheParams = new ImageCacheParams(getActivity(),
					BROWSE_GROUP_IMAGE_CACHE_DIR);

			cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to
														// 25% of app memory

			// The ImageFetcher takes care of loading images into our ImageView
			// children asynchronously
			mImageLoader = new ImageLoaderAsynch(getActivity());
			mImageLoader.setImageSize((ConfigData.SCREEN_WIDTH - 50) / 3);
			mImageLoader.setLoadingImage(null);
			mImageLoader.addImageCache(getActivity()
					.getSupportFragmentManager(), cacheParams);
			mImageLoader.setImageFadeIn(false);
		}

		/**
		 * Reclaim memory and cancel all background task
		 */
		public void restartCacheToClaimMemory() {
			// TODO Auto-generated method stub
			mImageLoader.clearMemCache();
			mImageLoader.setPauseWork(false);
			mImageLoader.setExitTasksEarly(true);
			mImageLoader.flushCache();
			mImageLoader.closeCache();
			createImageLoader();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			createImageLoader();
			
			View view = inflater.inflate(R.layout.fragment_gridcard, container,
					false);

			mGroupNumberImageGridViewAdapter = new GroupNumberImageGridViewAdapter(view.getContext());

			gridview = (GridView) view.findViewById(R.id.gridview);
			gridview.setAdapter(mGroupNumberImageGridViewAdapter);
			cell_width = (ConfigData.SCREEN_WIDTH - 40) / 3;

			gridview.setColumnWidth(cell_width);

			return view;
		}

		@Override
		public void onResume() {
			super.onResume();
			mImageLoader.setExitTasksEarly(false);
			mGroupNumberImageGridViewAdapter.notifyDataSetChanged();
		}

		@Override
		public void onPause() {
			super.onPause();
			mImageLoader.setPauseWork(false);
			mImageLoader.setExitTasksEarly(true);
			mImageLoader.flushCache();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mImageLoader.clearMemCache();
			if (ConfigData.IS_USER_DESTROY_BY_BACK_BUTTON) {
				mImageLoader.clearDiskCache();
				ConfigData.IS_USER_DESTROY_BY_BACK_BUTTON = false;
			}
			mImageLoader.closeCache();

		}

	}

	public static class GroupSymbolCardFragment extends
			android.support.v4.app.Fragment {

		public static int cell_width = 0;
		private GridView gridview;
		private GroupSymbolImageGridViewAdapter mGroupSymbolImageGridViewAdapter;
		public static ImageLoaderAsynch mImageLoader;
		public static GroupSymbolCardFragment mInstance;

		public GroupSymbolCardFragment() {
			mInstance = this;
		}

		private void createImageLoader() {
			// TODO INIT FOR IMAGE CACHE
			ImageCacheParams cacheParams = new ImageCacheParams(getActivity(),
					BROWSE_GROUP_IMAGE_CACHE_DIR);

			cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to
														// 25% of app memory

			// The ImageFetcher takes care of loading images into our ImageView
			// children asynchronously
			mImageLoader = new ImageLoaderAsynch(getActivity());
			mImageLoader.setImageSize((ConfigData.SCREEN_WIDTH - 50) / 3);
			mImageLoader.setLoadingImage(null);
			mImageLoader.addImageCache(getActivity()
					.getSupportFragmentManager(), cacheParams);
			mImageLoader.setImageFadeIn(false);
		}

		/**
		 * Reclaim memory and cancel all background task
		 */
		public void restartCacheToClaimMemory() {
			// TODO Auto-generated method stub
			mImageLoader.clearMemCache();
			mImageLoader.setPauseWork(false);
			mImageLoader.setExitTasksEarly(true);
			mImageLoader.flushCache();
			mImageLoader.closeCache();
			createImageLoader();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			createImageLoader();
			

			View view = inflater.inflate(R.layout.fragment_gridcard, container,
					false);

			mGroupSymbolImageGridViewAdapter = new GroupSymbolImageGridViewAdapter(view.getContext());

			gridview = (GridView) view.findViewById(R.id.gridview);
			gridview.setAdapter(mGroupSymbolImageGridViewAdapter);
			cell_width = (ConfigData.SCREEN_WIDTH - 40) / 3;
			gridview.setColumnWidth(cell_width);

			return view;
		}
		@Override
		public void onResume() {
			super.onResume();
			mImageLoader.setExitTasksEarly(false);
			mGroupSymbolImageGridViewAdapter.notifyDataSetChanged();
		}

		@Override
		public void onPause() {
			super.onPause();
			mImageLoader.setPauseWork(false);
			mImageLoader.setExitTasksEarly(true);
			mImageLoader.flushCache();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mImageLoader.clearMemCache();
			if (ConfigData.IS_USER_DESTROY_BY_BACK_BUTTON) {
				mImageLoader.clearDiskCache();
				ConfigData.IS_USER_DESTROY_BY_BACK_BUTTON = false;
			}
			mImageLoader.closeCache();

		}

	}

	public static class GroupStarCardFragment extends
			android.support.v4.app.Fragment {

		public static int cell_width = 0;
		private GridView gridview;
		private GroupStarImageGridViewAdapter mGroupStarImageGridViewAdapter;
		public static ImageLoaderAsynch mImageLoader;
		public static GroupStarCardFragment mInstance;

		public GroupStarCardFragment() {
			mInstance = this;
		}

		private void createImageLoader() {
			// TODO INIT FOR IMAGE CACHE
			ImageCacheParams cacheParams = new ImageCacheParams(getActivity(),
					BROWSE_GROUP_IMAGE_CACHE_DIR);

			cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to
														// 25% of app memory

			// The ImageFetcher takes care of loading images into our ImageView
			// children asynchronously
			mImageLoader = new ImageLoaderAsynch(getActivity());
			mImageLoader.setImageSize((ConfigData.SCREEN_WIDTH - 50) / 3);
			mImageLoader.setLoadingImage(null);
			mImageLoader.addImageCache(getActivity()
					.getSupportFragmentManager(), cacheParams);
			mImageLoader.setImageFadeIn(false);
		}

		/**
		 * Reclaim memory and cancel all background task
		 */
		public void restartCacheToClaimMemory() {
			// TODO Auto-generated method stub
			mImageLoader.clearMemCache();
			mImageLoader.setPauseWork(false);
			mImageLoader.setExitTasksEarly(true);
			mImageLoader.flushCache();
			mImageLoader.closeCache();
			createImageLoader();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			createImageLoader();
			
			View view = inflater.inflate(R.layout.fragment_gridcard, container,
					false);

			mGroupStarImageGridViewAdapter = new GroupStarImageGridViewAdapter(view.getContext());

			gridview = (GridView) view.findViewById(R.id.gridview);
			gridview.setAdapter(mGroupStarImageGridViewAdapter);
			cell_width = (ConfigData.SCREEN_WIDTH - 40) / 3;
			gridview.setColumnWidth(cell_width);

			return view;
		}

		@Override
		public void onResume() {
			super.onResume();
			mImageLoader.setExitTasksEarly(false);
			mGroupStarImageGridViewAdapter.notifyDataSetChanged();
		}

		@Override
		public void onPause() {
			super.onPause();
			mImageLoader.setPauseWork(false);
			mImageLoader.setExitTasksEarly(true);
			mImageLoader.flushCache();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mImageLoader.clearMemCache();
			if (ConfigData.IS_USER_DESTROY_BY_BACK_BUTTON) {
				mImageLoader.clearDiskCache();
				ConfigData.IS_USER_DESTROY_BY_BACK_BUTTON = false;
			}
			mImageLoader.closeCache();

		}

	}
	
	
	
}
