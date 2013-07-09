package me.chatpass.chatpassme;

import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ProfileFragment extends Fragment {

	private Number userId = 257;
	private TabHost tabHost;
	private int whistleCount;
	private int clikCount;
	private int podCount;
	private ArrayList<String> cliks = new ArrayList<String>();
	private ArrayList<String> whistles = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_profile,
				container, false);

		// // Get installation id
		// final ParseInstallation id =
		// ParseInstallation.getCurrentInstallation();
		//
		// // Get user's phoneNumber
		// String phoneNumber = id.getString("phoneNumber");
		//
		// // Create a Parse object query and ask for the Users class
		// ParseQuery<ParseObject> qUsers = ParseQuery.getQuery("Users");
		//
		// // Ask for the specific phone number that is correlated to the user
		// qUsers.whereEqualTo("phoneNumber", phoneNumber);
		//
		// // Find the first object that matches that shows up
		// qUsers.getFirstInBackground(new GetCallback<ParseObject>() {
		// public void done(ParseObject object, ParseException e) {
		// if (e == null) {
		//
		// // Get user's user id from that object
		// Number userId = id.getNumber("userId");
		//
		// // Create a ParseObject query and ask for the VoteQues
		// // class
		// ParseQuery<ParseObject> qVoteQues = ParseQuery
		// .getQuery("VoteQues");
		//
		// // Ask for all the times userId shows up
		// qVoteQues.whereEqualTo("userId", userId);
		//
		// // Count how many times userId has created a whistle
		// qVoteQues.countInBackground(new CountCallback() {
		// public void done(int count, ParseException e) {
		// if (e == null) {
		//
		// // Record the whistle number
		// whistleCount = count;
		// } else {
		// }
		// }
		// });
		// } else {
		// }
		// }
		// });

		tabHost = (TabHost) view.findViewById(R.id.tabhost);
		tabHost.setup();

		TabSpec spec1 = tabHost.newTabSpec("Cliks");
		spec1.setContent(R.id.cliks);
		spec1.setIndicator("Cliks");

		TabSpec spec2 = tabHost.newTabSpec("Whistles");
		spec2.setIndicator("Whistles");
		spec2.setContent(R.id.whistles);

		TabSpec spec3 = tabHost.newTabSpec("Pods");
		spec3.setContent(R.id.pods);
		spec3.setIndicator("Pods");

		TabSpec spec4 = tabHost.newTabSpec("Stats");
		spec4.setContent(R.id.stats);
		spec4.setIndicator("Stats");

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		tabHost.addTab(spec4);

		getWhistleCount(view);

		getClikCount(view);

		getPodCount(view);

		getProfilePicture(view);

		getCliksAnswered(view);

		getWhistlesCreated(view);

		return view;
	}

	private void getWhistlesCreated(View view) {
		final ListView listView = (ListView) view
				.findViewById(R.id.profile_whistles);

		// Create a ParseObject query and ask for the VoteQues class
		ParseQuery<ParseObject> qVoteQues = ParseQuery.getQuery("VoteQues");

		// Ask for all the times userId shows up
		qVoteQues.whereEqualTo("userId", userId);

		// Get it
		qVoteQues.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {

					// Retrieve all whistles created and push into array
					for (int i = 0; i < objects.size(); i++) {

						// Get the string for the quesTxt
						String quesTxt = objects.get(i).getString("quesTxt");

						// Add it to the whistles ArrayList
						whistles.add(quesTxt);
					}

					// Create a new adapter for cliks
					ArrayAdapter<String> whistlesAdapter = new ArrayAdapter<String>(
							getActivity(), android.R.layout.simple_list_item_1,
							android.R.id.text1, whistles);

					// Set the adapter
					listView.setAdapter(whistlesAdapter);
				} else {
				}
			}
		});
	}

	private void getCliksAnswered(View view) {

		final ListView listView = (ListView) view
				.findViewById(R.id.profile_cliks);

		// Create a ParseObject query and ask for the VoteAnswer class
		ParseQuery<ParseObject> qVoteAnswer = ParseQuery.getQuery("voteAnswer");

		// Ask for all the times userId shows up
		qVoteAnswer.whereEqualTo("userId", userId);

		// Get it
		qVoteAnswer.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {

					// Retrieve answered clik questions and push into the array
					for (int i = 0; i < objects.size(); i++) {

						// Get the quesId for the the clik
						Number quesId = objects.get(i).getNumber("quesId");

						// Create a new ParseObject to get the clik question
						ParseQuery<ParseObject> qVoteQues = ParseQuery
								.getQuery("VoteQues");

						// Ask for all the times quesId shows up
						qVoteQues.whereEqualTo("quesId", quesId);
						try {

							// Get the string for the quesTxt
							String quesTxt = qVoteQues.getFirst().getString(
									"quesTxt");

							// Add it to the cliks ArrayList
							cliks.add(quesTxt);

						} catch (ParseException e1) {
						}
					}

					// Create a new adapter for cliks
					ArrayAdapter<String> cliksAdapter = new ArrayAdapter<String>(
							getActivity(), android.R.layout.simple_list_item_1,
							android.R.id.text1, cliks);

					// Set the adapter
					listView.setAdapter(cliksAdapter);
				} else {
				}
			}
		});
	}

	private void getProfilePicture(final View view) {
		// Create a ParseObject query and ask for the Users class
		ParseQuery<ParseObject> qUsers = ParseQuery.getQuery("Users");

		// Find the specific userId
		qUsers.whereEqualTo("userId", userId);

		// Get it
		qUsers.getFirstInBackground(new GetCallback<ParseObject>() {
			public void done(ParseObject Object, ParseException e) {
				if (e == null) {

					// Get the profile picture from the ParseObject and cast it
					// as a ParseFile
					ParseFile profileImage = (ParseFile) Object
							.get("imageFile");

					// Get it and turn the byte[] into a Bitmap
					Bitmap bmp;
					try {
						bmp = BitmapFactory.decodeByteArray(
								profileImage.getData(), 0,
								profileImage.getData().length);
						// Push the profilePicture into the ImageButton
						// view
						ImageButton image = (ImageButton) view
								.findViewById(R.id.profile_picture);
						image.setImageBitmap(bmp);
					} catch (ParseException e1) {
						e1.printStackTrace();
					}

				} else {
				}
			}
		});
	}

	private void getPodCount(View view) {
		// Just set the default podCount to 0
		podCount = 0;
		TextView profilePodCount = (TextView) view
				.findViewById(R.id.profile_pod_count);
		profilePodCount.setText("" + podCount + " pods");
	}

	private void getClikCount(final View view) {
		// Create a ParseObject query and ask for the VoteAnswer class
		ParseQuery<ParseObject> qVoteAnswer = ParseQuery.getQuery("voteAnswer");

		// Ask for all the times userId shows up
		qVoteAnswer.whereEqualTo("userId", userId);

		// Count how many times userId has cliked
		qVoteAnswer.countInBackground(new CountCallback() {
			public void done(int count, ParseException e) {
				if (e == null) {

					// Record the clik number
					clikCount = count;

					// Push the clik number in the TextView
					TextView profileClikCount = (TextView) view
							.findViewById(R.id.profile_clik_count);

					// Make sure that we get the grammar right
					if (clikCount == 1) {
						profileClikCount.setText("" + clikCount + " clik");
					} else {
						profileClikCount.setText("" + clikCount + " cliks");
					}
				} else {
				}
			}
		});
	}

	private void getWhistleCount(final View view) {
		// Create a ParseObject query and ask for the VoteQues
		// class
		ParseQuery<ParseObject> qVoteQues = ParseQuery.getQuery("VoteQues");

		// Ask for all the times userId shows up
		qVoteQues.whereEqualTo("userId", userId);

		// Count how many times userId has created a whistle
		qVoteQues.countInBackground(new CountCallback() {
			public void done(int count, ParseException e) {
				if (e == null) {

					// Record the whistle number
					whistleCount = count;

					// Push the whistle number in the TextView
					TextView profileWhistleCount = (TextView) view
							.findViewById(R.id.profile_whistle_count);

					// Make sure that we get the grammar right
					if (whistleCount == 1) {
						profileWhistleCount.setText("" + whistleCount
								+ " whistle");
					} else {
						profileWhistleCount.setText("" + whistleCount
								+ " whistles");
					}
				} else {
				}
			}
		});
	}
}
