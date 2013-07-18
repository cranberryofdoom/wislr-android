package me.wislr.wislr;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MainActivity extends Activity {

	private GridView gridView;
	private WhistleGridAdapter gridAdapter;
	private ArrayList<String> objectId = new ArrayList<String>();
	private ArrayList<String> quesTxt = new ArrayList<String>();
	private ArrayList<Bitmap> quesImg = new ArrayList<Bitmap>();
	private ArrayList<byte[]> dataQuesImg = new ArrayList<byte[]>();
	private ArrayList<Bitmap> userImg = new ArrayList<Bitmap>();
	private ArrayList<byte[]> dataUserImg = new ArrayList<byte[]>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize Parse
		Parse.initialize(this, "01tjxzcgenqzGgl4iQCg0Ua9PQi69g4DIlxpPJVC",
				"7pf2c8jNTYzmJQJxvHFUPJH9gPxGtEHKzggrDQ4Y");
		ParseAnalytics.trackAppOpened(getIntent());

		// Get installation id
		final ParseInstallation id = ParseInstallation.getCurrentInstallation();

		// Hide the action bar
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		// Set the view
		setContentView(R.layout.activity_main);
		
		// Identify which gridview we're gonna load data into
		gridView = (GridView) findViewById(R.id.whistle_grid);

		// Set whistle placeholder
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.whistle_placeholder);
		final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		final byte[] bitMapData = stream.toByteArray();

		// Get the questions
		ParseQuery<ParseObject> query = ParseQuery.getQuery("VoteQues");
		query.setLimit(5);
		query.addAscendingOrder("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (int i = 0; i < objects.size(); i++) {

						objectId.add(i, objects.get(i).getObjectId());
						quesTxt.add(i, objects.get(i).getString("quesTxt"));

						ParseFile pQuesImg = objects.get(i).getParseFile(
								"quesImg");
						if (pQuesImg != null) {
							try {
								byte[] data = pQuesImg.getData();
								dataQuesImg.add(data);
								quesImg.add(decodeSampledBitmap(data, 200, 100));
							} catch (ParseException e1) {
								dataQuesImg.add(bitMapData);
								quesImg.add(bitmap);
							}
						} else {
							dataQuesImg.add(bitMapData);
							quesImg.add(bitmap);
						}

						Number userId = objects.get(i).getNumber("userId");
						Log.i("WTF IS WRONG WITH THIS ", "" + userId);
						ParseQuery<ParseObject> user = ParseQuery
								.getQuery("Users");
						user.whereEqualTo("userId", userId);
						try {
							ParseObject pUser = user.getFirst();
							ParseFile pUserImg = pUser
									.getParseFile("imageFile");
							byte[] data = pUserImg.getData();
							dataUserImg.add(data);
							userImg.add(decodeSampledBitmap(data, 50, 50));
						} catch (ParseException e1) {
							Log.i("DAFUCK", "I DIDN'T GET JACK");
						}
					}
					Log.i("SHIT MAN DEBUGGING",
							"size questext" + quesTxt.size() + "size userimg"
									+ userImg.size());
					gridAdapter = new WhistleGridAdapter(MainActivity.this,
							quesTxt, quesImg, userImg);
					gridView.setAdapter(gridAdapter);
				}
			}
		});
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;

		int inSampleSize = 64;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee a final image with both dimensions larger than or equal
			// to the requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmap(byte[] data, int reqWidth,
			int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}
}
