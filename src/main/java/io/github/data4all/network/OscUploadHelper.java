package io.github.data4all.network;

/**
 * the OsCUploadHelper handles the whole Uploading Process from 
 * requesting changeSet, parsing into Osc and uploading to the OSM API 
 * 
 */

import java.util.ArrayList;

import android.content.Context;
import android.preference.PreferenceManager;
import io.github.data4all.Constants;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.task.RequestChangesetIDFromOpenStreetMapTask;
import io.github.data4all.task.UploadingOsmChangeFileToOpenStreetMapTask;
import io.github.data4all.util.OsmChangeParser;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

/**
 * @author Richard
 *
 */
public class OscUploadHelper {

	private OAuthConsumer consumer;
	private Context context;
	private ArrayList<AbstractDataElement> elems;

	/**
	 * Constructor of the OscUploadHelper starts with the ChangeSetRequest
	 * 
	 * @param context
	 *            Context of the Application
	 * @param elems
	 *            List of Elements which should be uploaded
	 * @param comment
	 *            Comment of the Upload, should contain what was uploaded
	 */

	public OscUploadHelper(Context context,
			ArrayList<AbstractDataElement> elems, String comment) {
		consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY,
				Constants.CONSUMER_SECRET);
		this.context = context;
		this.elems = elems;
		consumer.setTokenWithSecret(
				PreferenceManager.getDefaultSharedPreferences(context)
						.getString("oauth_token", null),
				PreferenceManager.getDefaultSharedPreferences(context)
						.getString("oauth_token_secret", null));
		new RequestChangesetIDFromOpenStreetMapTask(context, consumer, comment,
				this).execute();
	}

	/**
	 * parseAndUpload is called in RequestChangesetIDFromOpenStreetMapTask with
	 * the requested changeSetID It parses the Osc file and starts the uploading
	 * task.
	 * 
	 * @param changeSetId
	 *            the changeSetId required for parsing and uploading
	 */

	public void parseAndUpload(Integer changeSetId) {
		OsmChangeParser.parseElements(context, elems, changeSetId);
		new UploadingOsmChangeFileToOpenStreetMapTask(context, consumer,
				changeSetId).execute();
	}
}
