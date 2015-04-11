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
package io.github.data4all.util.upload;

import io.github.data4all.logger.Log;
import io.github.data4all.util.oauth.exception.OsmException;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This class provides the upload of the changeset. If the user cancels the
 * upload, this upload can be simply stopped.
 * 
 * @author tbrose
 */
public class CloseableUpload implements HttpCloseable {

    private final HttpPost httpRequest;
    private final DefaultHttpClient httpClient;
    private boolean isStopped;

    /**
     * Constructs a changeset-upload with the given request.
     * 
     * @param request
     *            The request to use for the upload
     */
    public CloseableUpload(HttpPost request) {
        this.httpRequest = request;
        this.httpClient = new DefaultHttpClient();
    }

    /**
     * Starts the upload of the request to OSM.
     * 
     * @throws OsmException
     *             In case of problems with the upload
     */
    public void upload() throws OsmException {
        this.isStopped = false;

        try {
            // Sending the Request
            final HttpResponse response = this.httpClient.execute(httpRequest);
            final int code = response.getStatusLine().getStatusCode();
            Log.d("Upload", "status code: " +code);
            if (code != HttpStatus.SC_OK && !this.isStopped) {
                throw new OsmException("Wrong statusCode returned: " + code);
            }
        } catch (ClientProtocolException e) {
            if (!this.isStopped) {
                throw new OsmException(e);
            }
        } catch (IOException e) {
            if (!this.isStopped) {
                throw new OsmException(e);
            }
        } catch (IllegalStateException e) {
            if (!this.isStopped) {
                throw new OsmException(e);
            }
        } finally {
            this.httpClient.getConnectionManager().shutdown();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.util.upload.HttpCloseable#stop()
     */
    @Override
    public void stop() {
        Log.d("CloseableUpload", "stopping upload");
        this.isStopped = true;
        this.httpClient.getConnectionManager().shutdown();
    }
}
