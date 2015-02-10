package io.github.data4all.network;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.preference.PreferenceManager;
import io.github.data4all.Constants;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.task.RequestChangesetIDFromOpenStreetMapTask;
import io.github.data4all.task.UploadingOsmChangeFileToOpenStreetMapTask;
import io.github.data4all.util.OsmChangeParser;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

public class OscUploadHelper {
    
    private OAuthConsumer consumer;
    private Context context;
    private ArrayList<AbstractDataElement> elems;
    
    public OscUploadHelper(Context context, ArrayList<AbstractDataElement> elems, String comment){
      consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
      this.context = context;
      this.elems = elems;
      consumer.setTokenWithSecret(PreferenceManager
              .getDefaultSharedPreferences(context).getString("oauth_token", null), PreferenceManager
              .getDefaultSharedPreferences(context).getString("oauth_token_secret", null));
      new RequestChangesetIDFromOpenStreetMapTask(context, consumer, comment, this ).execute();
    }

    
    public void parseAndUpload(Integer changeSetId){
        OsmChangeParser.parseElements(context,elems,changeSetId);
        new UploadingOsmChangeFileToOpenStreetMapTask(context,consumer,changeSetId).execute();
    }
}
