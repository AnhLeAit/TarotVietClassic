package greendream.ait.tarot.activity;

import greendream.ait.tarot.R;
import greendream.ait.tarot.data.ConfigData;
import greendream.ait.tarot.data.SpreadCardJasonHelper;
import greendream.ait.tarot.util.Utils;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class ChooseCardActivity extends Activity implements OnClickListener,
		AnimationListener {

	private int padding;
	private int card_width;
	private int card_height;
	private int marginTop;
	private AbsoluteLayout as;
	private Button btnSkip;
	private int cardSelectedCount;
	private int theNumberCardNeedToSelect;
	private int spreadId;
	private ImageView cardNeedRemove;
	private Context mContext;
	private LayoutAnimationController controller;
	private Bitmap cardBack;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_card);

		// Reload screen size and background
		ConfigData.reloadScreen(this);
				
		
		// Load background
		((ImageView) findViewById(R.id.background))
				.setBackgroundDrawable(ConfigData.rbdBackground);

		mContext = this.getApplicationContext();

		if (ConfigData.IS_SOUND_ON) {
			MediaPlayer mPlayer = MediaPlayer.create(mContext,
					R.raw.cards_layout);
			mPlayer.start();
		}

		// get card need to select
		theNumberCardNeedToSelect = this.getIntent().getExtras()
				.getInt("cardSelectInNeed");

		// get spreadId
		spreadId = this.getIntent().getExtras().getInt("spreadId", -1);
		cardSelectedCount = 0;

		as = (AbsoluteLayout) findViewById(R.id.cardDrawArea);

		padding = 10;
		card_width = (ConfigData.SCREEN_WIDTH - padding * 3) / 2;
		card_height = card_width * 710 / 1232;
		marginTop = (ConfigData.SCREEN_HEIGHT - padding - card_height) / 40;
		cardBack = Utils.decodeSampledBitmapFromResource(getResources(),
				R.drawable.card_back, card_width, card_height, 90);

		btnSkip = (Button) findViewById(R.id.btnSkip);
		btnSkip.setOnClickListener(this);
		btnSkip.setTypeface(ConfigData.UVNCatBien_R);

		create78Card();

		ConfigData.animation_select_card.setAnimationListener(this);

	}

	@Override
	protected void onResume() {
		// Load background
		((ImageView) findViewById(R.id.background))
				.setBackgroundDrawable(ConfigData.rbdBackground);
		super.onResume();
	}

	private void create78Card() {

		// Create 39 Cards right
		int xRight = card_width + 2 * padding;
		int i = 0;
		for (i = 0; i < 39; i++) {
			ImageView card_top_right = new ImageView(this);
			card_top_right.setClickable(true);
			card_top_right.setImageBitmap(cardBack);
			card_top_right.setId(i + 1);
			AbsoluteLayout.LayoutParams lpCardRight = new AbsoluteLayout.LayoutParams(
					card_width, card_height, xRight, i * marginTop);
			card_top_right.setLayoutParams(lpCardRight);
			card_top_right.setOnClickListener(this);
			as.addView(card_top_right);
		}

		// Create 39 Cards left
		int xLeft = padding;
		int j = 0;
		for (j = 0; j < 39; j++) {
			ImageView card_top_left = new ImageView(this);
			card_top_left.setClickable(true);
			card_top_left.setImageBitmap(cardBack);
			card_top_left.setId(j + 40);
			AbsoluteLayout.LayoutParams lpCardLeft = new AbsoluteLayout.LayoutParams(
					card_width, card_height, xLeft, j * marginTop);
			card_top_left.setLayoutParams(lpCardLeft);
			card_top_left.setOnClickListener(this);
			as.addView(card_top_left);
		}

	}

	@Override
	protected void onStart() {

		super.onStart();

		if (ConfigData.IS_SOUND_ON) {
			ConfigData.playSound(R.raw.cards_layout);
		}

		controller = new LayoutAnimationController(
				ConfigData.animation_spread_card);

		as.setLayoutAnimation(controller);

		Toast.makeText(this, "Rút " + theNumberCardNeedToSelect + " lá bài",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

		if (v.getId() >= 1 && v.getId() <= 78
				&& cardSelectedCount < theNumberCardNeedToSelect) {
			// move card selected on top
			as.bringChildToFront(v);

			// try to remove previous card still not remove because
			// has new card selected before it end animation
			try {
				as.removeView(cardNeedRemove);
			} catch (Exception e) {

			}

			// run animation
			cardSelectedCount++;
			cardNeedRemove = (ImageView) v;
			cardNeedRemove.startAnimation(ConfigData.animation_select_card);
			ConfigData.playSound(R.raw.single_card);

			return;
		}

		if (v.getId() == R.id.btnSkip) {
			ConfigData.stopSound();
			cardSelectedCount = theNumberCardNeedToSelect;

			if (spreadId == -1) {
				// Random one card to show
				ConfigData.randomOneCard();
				startActivity(new Intent(this,
						SingleCardActivity.class));
			} else {
				// Show Spread card activity of spreadId
				Intent intentSpreadCardsActivity = new Intent(this,
						SpreadCardsActivity.class);
				intentSpreadCardsActivity.putExtra("spreadId", spreadId);
				ConfigData.randomMultipleCards(SpreadCardJasonHelper
						.getStepArray(spreadId).length);
				this.startActivity(intentSpreadCardsActivity);
			}

			this.finish();
			return;
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {

		if (animation == ConfigData.animation_select_card) {
			if (cardSelectedCount == theNumberCardNeedToSelect) {

				if (spreadId == -1) {
					// Random one card to show
					ConfigData.randomOneCard();
					startActivity(new Intent(this,
							SingleCardActivity.class));
				} else {
					// Show Spread card activity of spreadId
					Intent intentSpreadCardsActivity = new Intent(this,
							SpreadCardsActivity.class);
					intentSpreadCardsActivity.putExtra("spreadId", spreadId);
					ConfigData.randomMultipleCards(SpreadCardJasonHelper
							.getStepArray(spreadId).length);
					this.startActivity(intentSpreadCardsActivity);
				}
				this.finish();
			}
		}
		
		try {
			if (cardNeedRemove != null)
				as.removeView(cardNeedRemove);
		} catch (Exception e) {

		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			as.removeAllViews();
			cardBack.recycle();
			System.gc();
		} catch (Exception e) {

		}
	}

}
