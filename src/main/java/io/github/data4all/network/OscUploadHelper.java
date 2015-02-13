/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.network;

import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.task.RequestChangesetIDFromOsmTask;
import io.github.data4all.task.UploadingOsmChangeFileToOpenStreetMapTask;
import io.github.data4all.util.OsmChangeParser;
import io.github.data4all.util.oauth.parameters.OAuthParameters;

import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.content.Context;
import android.preference.PreferenceManager;

/**
 * the OsCUploadHelper handles the whole Uploading Process from requesting
 * changeSet, parsing into Osc and uploading to the OSM API
 * 
 * @author Richard
 * 
 */
public class OscUploadHelper {

    private OAuthConsumer consumer;
    private Context context;
    private List<AbstractDataElement> elems;

    /**
     * Constructor of the OscUploadHelper starts with the ChangeSetRequest.
     * 
     * @param context
     *            Context of the Application
     * @param elems
     *            List of Elements which should be uploaded
     * @param comment
     *            Comment of the Upload, should contain what was uploaded
     */

    public OscUploadHelper(Context context, List<AbstractDataElement> elems,
            String comment) {
        OAuthParameters params = OAuthParameters.CURRENT;
        consumer =
                new CommonsHttpOAuthConsumer(params.getConsumerKey(),
                        params.getConsumerSecret());
        this.context = context;
        this.elems = elems;
        consumer.setTokenWithSecret(
                PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("oauth_token", null),
                PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("oauth_token_secret", null));
        new RequestChangesetIDFromOsmTask(context, consumer, comment, this)
                .execute();
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
