package greendream.ait.tarot.activity;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;

import greendream.ait.tarot.R;
import greendream.ait.tarot.custom.CardViewFlipper;
import greendream.ait.tarot.data.CardsDetailJasonHelper;
import greendream.ait.tarot.data.MapData;
import greendream.ait.tarot.data.ConfigData;
import greendream.ait.tarot.data.SpreadCardJasonHelper;
import greendream.ait.tarot.util.ImageLoaderAsynch;
import greendream.ait.tarot.util.ImageCache.ImageCacheParams;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.animation.LayoutAnimationController;

public class SpreadCardsActivity extends FragmentActivity implements
		OnTouchListener, OnClickListener {
	
	private static final String SPREAD_IMAGE_CACHE_DIR = "spread_image_cache";
	private ImageLoaderAsynch mImageLoader;
	
	public static Context mContext;
	public static SpreadCardsActivity instance;
	private TextView tvTitle;
	private Button btn_card_spread;
	private Button btn_card_list;
	private Button btn_rules;
	private Button btn_flip;

	private int theNummberOfCard;
	public static int spreadId; // current spread id show

	// Main content
	private AbsoluteLayout spread_cards_container;
	private ScrollView svListCards;
	private TextView tvSpreadCardInfo;
	private ScrollView svRules;

	// Startup animation manager 
	private LayoutAnimationController controller;

	private AdView adView;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spread_cards);

		// Reload screen size and background
		ConfigData.reloadScreen(this);

		// Look up the AdView as a resource and load a request.
		adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());
		
		/**
		 * Setup for ImageLoader
		 */
		ImageCacheParams cacheParams = new ImageCacheParams(this,
				SPREAD_IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to
													// 25% of current available
													// memory

		// The ImageLoader takes care of loading images into our ImageView
		// children asynchronously
		mImageLoader = new ImageLoaderAsynch(this);
		mImageLoader.setLoadingImage(null);
		mImageLoader.addImageCache(getSupportFragmentManager(), cacheParams);
		mImageLoader.setImageFadeIn(false);
		
		// Load background
		((ImageView) findViewById(R.id.background)).setBackgroundDrawable(ConfigData.rbdBackground);

		mContext = this.getApplicationContext();

		// get spread which need to show
		spreadId = this.getIntent().getExtras().getInt("spreadId", 0);
		theNummberOfCard = SpreadCardJasonHelper.getStepArray(spreadId).length;

		// Spread card to AbsoluteLayout: spread_cards_container
		spread_cards_container = (AbsoluteLayout) findViewById(R.id.spread_cards_container);
		
		spreadCard();

		// Title
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(ConfigData.UVNCatBien_R);
		tvTitle.setText(SpreadCardJasonHelper.getSpreadName(spreadId));
		tvTitle.setOnClickListener(this);

		// Add card detail into LinearLayout
		LinearLayout listCardContainer = (LinearLayout) findViewById(R.id.listCardContainer);
		listCardContainer.setOnClickListener(this);

		// Main component initial right here !
		svListCards = (ScrollView) findViewById(R.id.svListCards);
		svListCards.setOnClickListener(this);

		tvSpreadCardInfo = (TextView) findViewById(R.id.tvSpreadCardInfo);
		tvSpreadCardInfo.setTextSize(ConfigData.FONT_SIZE);
		tvSpreadCardInfo.setOnClickListener(this);
		tvSpreadCardInfo.setText(SpreadCardJasonHelper.getSpreadInfo(spreadId)
				.split("\n")[0]);

		LayoutInflater inflater = LayoutInflater.from(SpreadCardsActivity.this);
		View v;
		ImageView ivCardIcon;
		TextView tvCardName;
		TextView tvExplainCard;

		// Create View for list Card and Step info
		for (int i = 0; i < theNummberOfCard; i++) {
			v = inflater.inflate(R.layout.card_detail_list, null);
			int cardId = ConfigData.randomCardIdArray[i];
			int width = ConfigData.SCREEN_WIDTH / 4;
			int height = width * 1232 / 710;
			ivCardIcon = (ImageView) v.findViewById(R.id.ivCardIcon);
			ivCardIcon.setLayoutParams(new LinearLayout.LayoutParams(width, height));			
			mImageLoader.loadImage(MapData.arrCardImage_R_Id[cardId] + "_" + width + "_" + height, ivCardIcon);
			
			ivCardIcon.setOnTouchListener(this);
			ivCardIcon.setId(i);
			ivCardIcon.setOnClickListener(this);

			tvCardName = (TextView) v.findViewById(R.id.tvCardName);
			tvCardName.setText(CardsDetailJasonHelper
					.getEnglishCardName(cardId));
			tvCardName.setOnClickListener(this);
			tvCardName.setTextSize(ConfigData.FONT_SIZE + 3);

			tvExplainCard = (TextView) v.findViewById(R.id.tvExplainCard);
			tvExplainCard.setTextSize(ConfigData.FONT_SIZE);
			tvExplainCard
					.setText(SpreadCardJasonHelper.getStepArray(spreadId)[i]);
			tvExplainCard.setOnClickListener(this);

			listCardContainer.addView(v);
		}

		svRules = (ScrollView) findViewById(R.id.svRules);
		svRules.setOnClickListener(this);

		// Bottom bar
		btn_card_spread = (Button) findViewById(R.id.btn_spread_selected);
		btn_card_spread.setOnClickListener(this);

		btn_card_list = (Button) findViewById(R.id.btn_card_list);
		btn_card_list.setOnClickListener(this);

		btn_rules = (Button) findViewById(R.id.btn_rules);
		btn_rules.setVisibility(View.INVISIBLE);
		btn_rules.setOnClickListener(this);

		btn_flip = (Button) findViewById(R.id.btn_flip);
		btn_flip.setOnClickListener(this);

		instance = this;
	}


	@Override
	protected void onStart() {
		super.onStart();
		controller = new LayoutAnimationController(ConfigData.animation_spread_card);
		spread_cards_container.setLayoutAnimation(controller);
		
		EasyTracker.getInstance(this).activityStart(this); // Google Analyze method.
	}
	
	
	// Spread Card via spreadId & random array of card
	private void spreadCard() {
		int w = 0;
		int h = 0;
		int x = 0;
		int y = 0;
		int padding = ConfigData.SCREEN_HEIGHT/32;

		// clear absolute layout before add new card
		spread_cards_container.removeAllViews();

		switch (spreadId) {
		case 0:
			/**
			 * Compute Width & Height of card
			 */
			w = (ConfigData.SCREEN_WIDTH - 3 * padding) / 2;
			h = w * 1232 / 710;

			/**
			 * Compute coordinate of cards in cardIdArrayAdapter one by one
			 */
			// Card 1
			x = padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2;
			// create and add card 1 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 0, false));
			// Card 2
			x += w + padding;
			// create and add card 2 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 1, false));
			break;

		case 1:
			/**
			 * Compute Width & Height of card
			 */
			padding = ConfigData.SCREEN_HEIGHT/64;
			w = (ConfigData.SCREEN_WIDTH - 6 * padding) / 5;
			h = w * 1232 / 710;

			/**
			 * Compute coordinate of cards in cardIdArrayAdapter one by one
			 */
			// Card 1
			x = padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2;
			// create and add card 1 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 0, false));
			// Card 2
			x += w + padding;
			// create and add card 2 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 1, false));
			// Card 3
			x += w + padding;
			// create and add card 3 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 2, false));
			// Card 4
			x += w + padding;
			// create and add card 4 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 3, false));
			// Card 5
			x += w + padding;
			// create and add card 5 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 4, false));
			break;

		case 2:
		case 4:
			/**
			 * Compute Width & Height of card
			 */			
			h = ConfigData.SCREEN_HEIGHT / 4;
			w = h * 701 / 1232;

			/**
			 * Compute coordinate of cards in cardIdArrayAdapter one by one
			 */
			// Card 3
			x = ConfigData.SCREEN_WIDTH / 2 - w / 2;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2;
			// create and add card 3 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 2, false));
			
			// Card 1
			x = x - padding - w;
			y = ConfigData.SCREEN_HEIGHT / 2 + h / 4;
			// create and add card 1 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 0, false));
			// Card 2
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 4 - h;
			// create and add card 2 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 1, false));

			// Card 4
			x = (ConfigData.SCREEN_WIDTH / 2 - w / 2) + padding + w;
			y = ConfigData.SCREEN_HEIGHT / 2 + h / 4;
			// create and add card 4 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 3, false));
			// Card 5
			x = (ConfigData.SCREEN_WIDTH / 2 - w / 2) + padding + w;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 4 - h;
			// create and add card 5 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 4, false));
			break;

		case 3:
			padding = ConfigData.SCREEN_HEIGHT/64;
			/**
			 * Compute Width & Height of card
			 */			
			h = ConfigData.SCREEN_HEIGHT / 5;
			w = h * 710 / 1232;

			int k = (ConfigData.SCREEN_WIDTH - 4*w - 3*padding) / 2;
			/**
			 * Compute coordinate of cards in cardIdArrayAdapter one by one
			 */
			// Card 1
			x = k + padding / 2 + w / 2;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2;
			// create and add card 1 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 0, false));
			// Card 2
			x = k;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2 - padding - h;
			// create and add card 2 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 1, false));
			// Card 3
			x = k;
			y = ConfigData.SCREEN_HEIGHT / 2 + h / 2 + padding;
			// create and add card 3 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 2, false));
			// Card 4
			x = k + w + padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2 - padding - h;
			// create and add card 4 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 3, false));
			// Card 5
			x = k + w + padding;
			y = ConfigData.SCREEN_HEIGHT / 2 + h / 2 + padding;
			// create and add card 5 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 4, false));
			// Card 6
			x = k + w + padding + w + padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2;
			// create and add card 6 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 5, false));
			// Card 7
			x = k + w + padding + w + padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2 - padding - h;
			// create and add card 7 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 6, false));
			// Card 8
			x = k + w + padding + w + padding;
			y = ConfigData.SCREEN_HEIGHT / 2 + h / 2 + padding;
			// create and add card 8 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 7, false));
			// Card 9
			x = k + w + padding + w + padding + padding + w;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2;
			// create and add card 9 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 8, false));
			break;

		case 5:
			/**
			 * Compute Width & Height of card
			 */
			w = (ConfigData.SCREEN_WIDTH - 2 * padding) / 7;
			h = w * 1232 / 710;

			/**
			 * Compute coordinate of cards in cardIdArrayAdapter one by one
			 */
			// Card 1
			x = ConfigData.SCREEN_WIDTH / 2 - w / 2;
			y = ConfigData.SCREEN_HEIGHT / 2 - 2 * h;
			// create and add card 1 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 0, false));
			// Card 2
			x = ConfigData.SCREEN_WIDTH / 2 - w / 2 - w;
			y = ConfigData.SCREEN_HEIGHT / 2 - h;
			// create and add card 2 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 1, false));
			// Card 3
			x = ConfigData.SCREEN_WIDTH / 2 - w / 2 + w;
			y = ConfigData.SCREEN_HEIGHT / 2 - h;
			// create and add card 3 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 2, false));
			// Card 4
			x = ConfigData.SCREEN_WIDTH / 2 - w / 2 - w - w;
			y = ConfigData.SCREEN_HEIGHT / 2;
			// create and add card 4 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 3, false));
			// Card 5
			x = ConfigData.SCREEN_WIDTH / 2 - w / 2 + w + w;
			y = ConfigData.SCREEN_HEIGHT / 2;
			// create and add card 5 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 4, false));
			// Card 6
			x = padding;
			y = ConfigData.SCREEN_HEIGHT / 2 + h;
			// create and add card 6 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 5, false));
			// Card 7
			x = ConfigData.SCREEN_WIDTH / 2 - w / 2 - w;
			y = ConfigData.SCREEN_HEIGHT / 2 + h;
			// create and add card 7 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 6, false));
			// Card 8
			x = ConfigData.SCREEN_WIDTH / 2 - w / 2 + w;
			y = ConfigData.SCREEN_HEIGHT / 2 + h;
			// create and add card 8 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 7, false));
			// Card 9
			x = ConfigData.SCREEN_WIDTH - w - padding;
			y = ConfigData.SCREEN_HEIGHT / 2 + h;
			// create and add card 9 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 8, false));
			break;

		case 6:
			/**
			 * Compute Width & Height of card
			 */
			w = (ConfigData.SCREEN_WIDTH - 4 * padding) / 3;
			h = w * 1232 / 710;

			/**
			 * Compute coordinate of cards in cardIdArrayAdapter one by one
			 */
			// Card 1
			x = padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h;
			// create and add card 1 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 0, false));
			// Card 2
			x = ConfigData.SCREEN_WIDTH - w - padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h;
			// create and add card 2 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 1, false));
			// Card 3
			x = ConfigData.SCREEN_WIDTH / 2 - w / 2;
			y = ConfigData.SCREEN_HEIGHT / 2 + 2;
			// create and add card 3 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 2, false));
			break;
		case 7:
			/**
			 * Compute Width & Height of card
			 */
			w = (ConfigData.SCREEN_WIDTH - 2 * padding) / 7;
			h = w * 1232 / 710;

			/**
			 * Compute coordinate of cards in cardIdArrayAdapter one by one
			 */
			// Card 1
			x = padding;
			y = ConfigData.SCREEN_HEIGHT / 2;
			// create and add card 1 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 0, false));
			// Card 2
			x = padding + w * 2 / 3;
			y = ConfigData.SCREEN_HEIGHT / 2 + h + padding;
			// create and add card 2 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 1, false));
			// Card 3
			x = padding + w + w / 3;
			y = ConfigData.SCREEN_HEIGHT / 2;
			// create and add card 3 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 2, false));
			// Card 4
			x = padding + w * 2 / 3 + w + w / 3;
			y = ConfigData.SCREEN_HEIGHT / 2 + h + padding;
			// create and add card 4 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 3, false));
			// Card 5
			x = padding + w * 2 / 3 + w + w / 3 + w + w / 3;
			y = ConfigData.SCREEN_HEIGHT / 2 + h + padding;
			// create and add card 5 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 4, false));
			// Card 6
			x = padding + w * 2 / 3 + w + w / 3 + w + w / 3 + w + w / 3 - w / 2;
			y = ConfigData.SCREEN_HEIGHT / 2;
			// create and add card 6 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 5, false));
			// Card 7
			x = padding + w * 2 / 3 + w + w / 3 + w + w / 3 + w + w / 3;
			y = ConfigData.SCREEN_HEIGHT / 2 + h + padding;
			// create and add card 7 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 6, false));
			// Card 8
			x = ConfigData.SCREEN_WIDTH - w - padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2 + h + padding;
			// create and add card 8 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 7, false));
			// Card 9
			x = ConfigData.SCREEN_WIDTH - w - padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2;
			// create and add card 9 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 8, false));
			// Card 10
			x = ConfigData.SCREEN_WIDTH - w - padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2 - padding - h;
			// create and add card 9 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 9, false));
			break;

		case 8:
			/**
			 * Compute Width & Height of card
			 */
			w = (ConfigData.SCREEN_WIDTH - 4 * padding) / 3;
			h = w * 1232 / 710;

			/**
			 * Compute coordinate of cards in cardIdArrayAdapter one by one
			 */
			// Card 1
			x = padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2;
			// create and add card 1 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 0, false));
			// Card 2
			x += w + padding;
			// create and add card 2 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 1, false));
			// Card 3
			x += w + padding;
			// create and add card 3 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 2, false));
			break;

		case 9:
			padding = ConfigData.SCREEN_HEIGHT/32;
			/**
			 * Compute Width & Height of card
			 */
			h = ConfigData.SCREEN_HEIGHT*3/4/5;
			w = h * 710 / 1232;

			/**
			 * Compute coordinate of cards in cardIdArrayAdapter one by one
			 */
			// Card 1
			x = padding + w + (padding + h + padding) / 2 - w / 2;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2 - padding / 2;
			// create and add card 1 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 0, false));
			// Card 2
			x = padding + w + padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - w / 2 - padding / 2;
			// create and add card 2 into Absolute layout
			CardViewFlipper cardViewFlipper = createCard(w, h, x, y, 1, true);
			// cardViewFlipper.
			spread_cards_container.addView(cardViewFlipper);
			// Card 3
			x = padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2 - padding / 2;
			// create and add card 3 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 2, false));
			// Card 4
			x = padding + w + padding + h + padding;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2 - padding / 2;
			// create and add card 4 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 3, false));
			// Card 5
			x = padding + w + (padding + h + padding) / 2 - w / 2;
			y = ConfigData.SCREEN_HEIGHT / 2 - h / 2 - padding / 2 - padding
					- h;
			// create and add card 5 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 4, false));
			// Card 6
			x = padding + w + (padding + h + padding) / 2 - w / 2;
			y = ConfigData.SCREEN_HEIGHT / 2 + h / 2 + padding;
			// create and add card 6 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 5, false));
			// Card 7
			x = ConfigData.SCREEN_WIDTH - padding - w;
			y = ConfigData.SCREEN_HEIGHT / 2 + h + padding;
			// create and add card 7 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 6, false));
			// Card 8
			x = ConfigData.SCREEN_WIDTH - padding - w;
			y = ConfigData.SCREEN_HEIGHT / 2;
			// create and add card 8 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 7, false));
			// Card 9
			x = ConfigData.SCREEN_WIDTH - padding - w;
			y = ConfigData.SCREEN_HEIGHT / 2 - padding - h;
			// create and add card 9 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 8, false));
			// Card 10
			x = ConfigData.SCREEN_WIDTH - padding - w;
			y = ConfigData.SCREEN_HEIGHT / 2 - padding - h - padding - h;
			// create and add card 10 into Absolute layout
			spread_cards_container.addView(createCard(w, h, x, y, 9, false));
			break;
		}
	}

	/**
	 * Get Image Loader
	 * @return
	 */
	public ImageLoaderAsynch getImageLoader() {
		return mImageLoader;		
	}
	
	public CardViewFlipper createCard(int w, int h, int x, int y, int cardId,
			boolean isLandscape) {
		CardViewFlipper cardViewFlipper = new CardViewFlipper(this, cardId, w, h, isLandscape);

		// set layout parameter of view when add into absolute layout
		if (isLandscape) {
			AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(h, w, x, y);
			cardViewFlipper.setLayoutParams(layout);
		} else {
			AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(w, h, x, y);
			cardViewFlipper.setLayoutParams(layout);
		}
		
		return cardViewFlipper;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.encyclopedia_menu, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
		
		EasyTracker.getInstance(this).activityStop(this); // Google Analyze method.
	}

	@Override
	public void onClick(View v) {

		if (v.getId() < theNummberOfCard) {
			// Show Activity guide of card
			Intent intentCardViewPager_SpreadCardActivity = new Intent(this,
					CardDetailViewPagerForSpreadCardActivity.class);
			intentCardViewPager_SpreadCardActivity.putExtra("cardClickedIndex",
					v.getId());
			startActivity(intentCardViewPager_SpreadCardActivity);
			return;
		}

		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_spread_selected:
			btn_card_spread
					.setBackgroundResource(R.drawable.btn_card_spread_selected);
			btn_card_list.setBackgroundResource(R.drawable.btn_card_list);
			btn_rules.setBackgroundResource(R.drawable.btn_rules);
			// other process below here
			btn_flip.setVisibility(View.VISIBLE);
			tvTitle.setVisibility(View.INVISIBLE);
			svListCards.setVisibility(View.INVISIBLE);
			svRules.setVisibility(View.INVISIBLE);
			break;

		case R.id.btn_card_list:
			btn_card_spread.setBackgroundResource(R.drawable.btn_card_spread);
			btn_card_list.setBackgroundResource(R.drawable.card_list_selected);
			btn_rules.setBackgroundResource(R.drawable.btn_rules);
			// other process below here
			btn_flip.setVisibility(View.INVISIBLE);
			tvTitle.setVisibility(View.VISIBLE);
			svListCards.setVisibility(View.VISIBLE);
			svRules.setVisibility(View.INVISIBLE);
			break;

		case R.id.btn_rules:
			btn_card_spread.setBackgroundResource(R.drawable.btn_card_spread);
			btn_card_list.setBackgroundResource(R.drawable.btn_card_list);
			btn_rules.setBackgroundResource(R.drawable.rules_selected);
			// other process below here
			btn_flip.setVisibility(View.INVISIBLE);
			tvTitle.setVisibility(View.VISIBLE);
			svListCards.setVisibility(View.INVISIBLE);
			svRules.setVisibility(View.VISIBLE);
			break;

		case R.id.btn_flip: // Flips card
			CardViewFlipper card;
			// Check if has one card is back
			boolean hasCardBack = false;
			for (int i = 0; i < ConfigData.randomCardIdArray.length; i++) {
				card = (CardViewFlipper) spread_cards_container.getChildAt(i);
				if (card.isCardBack() == true) {
					hasCardBack = true;
					break;
				}
			}

			// Flip card
			ConfigData.playSound(R.raw.card_deal);
			for (int i = 0; i < ConfigData.randomCardIdArray.length; i++) {
				card = (CardViewFlipper) spread_cards_container.getChildAt(i);
				// only need one card is back, we will font the others
				if (hasCardBack) {
					// Flip card to font and no sound
					card.flipCardToFont(false);
				} else {
					// Flip card to back and no sound
					card.flipCardToBack(false);
				}
			}

			break;

		default: // Press Spread button
			btn_card_spread
					.setBackgroundResource(R.drawable.btn_card_spread_selected);
			btn_card_list.setBackgroundResource(R.drawable.btn_card_list);
			btn_rules.setBackgroundResource(R.drawable.btn_rules);
			// other process below here
			btn_flip.setVisibility(View.VISIBLE);
			tvTitle.setVisibility(View.INVISIBLE);
			svListCards.setVisibility(View.INVISIBLE);
			svRules.setVisibility(View.INVISIBLE);

			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Start animation
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			v.startAnimation(ConfigData.animation_button_press);
		}

		return false;
	}

	@Override
	public void onBackPressed() {
		(new AlertDialog.Builder(this))
				.setMessage("Bạn có chắc chắn muốn hủy lần trải bài này")
				.setPositiveButton("Có", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SpreadCardsActivity.this.finish();
					}
				}).setNegativeButton("Không", null).create().show();
	}
	
	@Override
	protected void onResume() {
		// Load background
		((ImageView) findViewById(R.id.background))
				.setBackgroundDrawable(ConfigData.rbdBackground);
		super.onResume();
	}
}