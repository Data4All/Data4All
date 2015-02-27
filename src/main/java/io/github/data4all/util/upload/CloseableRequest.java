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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This class provides the id-request of the changeset. If the user cancels the
 * id-request, this id-request can be simply stopped.
 * 
 * @author tbrose
 */
public class CloseableRequest implements HttpCloseable {

    private final HttpPut httpRequest;
    private final DefaultHttpClient httpClient;
    private boolean isStopped;

    /**
     * Constructs a changeset-id-request with the given request.
     * 
     * @param request
     *            The request to use for the id-request
     */
    public CloseableRequest(HttpPut request) {
        this.httpRequest = request;
        this.httpClient = new DefaultHttpClient();
    }

    /**
     * Starts the upload of the request to OSM.
     * 
     * @return status code of the request
     * @throws OsmException
     *             In case of problems with the upload
     */
    public int request() throws OsmException {
        this.isStopped = false;

        try {
            // Sending the Request
            final HttpResponse response = this.httpClient.execute(httpRequest);

            // Looking if the Request was successful and then starting the
            // Upload Task through the helper
            final int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                final InputStream is = response.getEntity().getContent();
                final BufferedReader in =
                        new BufferedReader(new InputStreamReader(is));
                return Integer.parseInt(in.readLine());
            } else {
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
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.data4all.util.upload.HttpCloseable#stop()
     */
    @Override
    public void stop() {
        Log.d("CloseableRequest", "stopping request");
        this.isStopped = true;
        this.httpClient.getConnectionManager().shutdown();
    }
}
