package greendream.ait.tarot.activity;

import greendream.ait.tarot.R;
import greendream.ait.tarot.data.ConfigData;
import greendream.ait.tarot.util.Utils;
import greendream.ait.tarot.view.adapter.SpreadCardNameListViewAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TarotSpreadActivity extends Activity implements
		OnItemClickListener {

	private TextView tvTarotSpreadTitle;
	private ListView lvTarotSpread;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tarot_spread);

		// Reload screen size and background
		ConfigData.reloadScreen(this);
				
		
		// Load background
		((ImageView) findViewById(R.id.background)).setBackgroundDrawable(ConfigData.rbdBackground);

		tvTarotSpreadTitle = (TextView) findViewById(R.id.tvTarotSpreadTitle);
		tvTarotSpreadTitle.setTypeface(ConfigData.UVNCatBien_R);

		SpreadCardNameListViewAdapter adapter = new SpreadCardNameListViewAdapter(
				this.getApplicationContext());
		lvTarotSpread = (ListView) findViewById(R.id.lvTarotSpread);
		lvTarotSpread.setAdapter(adapter);
		lvTarotSpread.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> listAdapter, View arg1,
			int position, long arg3) {
		Intent intent = new Intent(TarotSpreadActivity.this,
				TarotSpreadGuideActivity.class);
		intent.putExtra("spreadId", position);
		this.startActivity(intent);

	}
	
	@Override
	protected void onResume() {
		// Load background
		((ImageView) findViewById(R.id.background))
				.setBackgroundDrawable(ConfigData.rbdBackground);
		super.onResume();
	}

}
