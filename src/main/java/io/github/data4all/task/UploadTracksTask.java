package io.github.data4all.task;

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

import io.github.data4all.model.data.User;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Task to upload a gpx file to OpenStreetMap INFO: Request is getting status
 * code 500 if using dev api!
 * 
 * @author sb
 * @author fkirchge
 *
 */
public class UploadTracksTask extends AsyncTask<Void, Void, Integer> {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private User user;
    private String trackXml;
    private String fileName;
    private String description;
    private String tags;
    private String visibility;

    /**
     * HTTP result code or -1 in case of internal error.
     */
    private int statusCode = -1;

    private static final int BUFFER_SIZE = 65535;

    private static final String BOUNDARY =
            "----------------------------d10f7aa230e8";
    private static final String LINE_END = "\r\n";

    private static final String BASE64_ENC =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    /**
     * Uploads the GPX Files to OSM.
     * 
     * @param context
     *            the {@link Context} of the upload
     * @param user
     *            the OAuth Consumer for Authentication
     * @param trackXml
     *            the parsed track that should be uploaded
     * @param description
     *            description what the gpx tracks are
     * @param tags
     *            tags for the tracks
     * @param visibility
     *            visibility of the tracks
     */
    public UploadTracksTask(Context context, User user, String trackXml,
            String fileName, String description, String tags, String visibility) {
        this.context = context;
        this.user = user;
        this.trackXml = trackXml;
        this.fileName = fileName;
        this.description = description;
        this.tags = tags;
        this.visibility = visibility;
    }

    /**
     * Forming the Request.
     */
    @Override
    protected void onPreExecute() {

    }

    /**
     * Displays what happened.
     */
    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
        case -1:
            // Internal error, the request didn't start at all
            Log.d(TAG, "Internal error, the request did not start at all");
            break;
        case HttpStatus.SC_OK:
            // Success ! Update database and close activity
            Log.d(TAG, "Success");
            break;
        case HttpStatus.SC_UNAUTHORIZED:
            // Does not have authorization
            Log.d(TAG, "Does not have authorization");
            break;
        case HttpStatus.SC_INTERNAL_SERVER_ERROR:
            Toast.makeText(context, "INTERNAL SERVER ERROR", Toast.LENGTH_LONG)
                    .show();
            Log.d(TAG, "INTERNAL SERVER ERROR");
            break;
        default:
            // unknown error
            Log.d(TAG, "Unknown error");
        }

    }

    /**
     * Sending the Request.
     */
    @Override
    protected Integer doInBackground(Void... params) {

        String urlDesc = description.replaceAll("\\.;&?,/", "_");
        String urlTags = tags.replaceAll("\\\\.;&?,/", "_");
        try {
            URL url =
                    new URL("http://www.openstreetmap.org/api/0.6/gpx/create");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            // TODO: remove hardcoded account settings
            String username = "Data4All";
            String password = "8zA2E49dpb";

            con.addRequestProperty("Authorization", "Basic "
                    + encodeBase64(username + ":" + password));

            con.addRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            con.addRequestProperty("Connection", "close");
            con.addRequestProperty("Expect", "");

            con.connect();
            DataOutputStream out =
                    new DataOutputStream(new BufferedOutputStream(
                            con.getOutputStream()));

            File gpxFile = createGpxFile(trackXml, fileName);
            writeContentDispositionFile(out, "file", gpxFile);
            writeContentDisposition(out, "description", urlDesc);
            writeContentDisposition(out, "tags", urlTags);
            writeContentDisposition(out, "public", "1");
            writeContentDisposition(out, "visibility", "identifiable");

            out.writeBytes("--" + BOUNDARY + "--" + LINE_END);
            out.flush();

            int retCode = con.getResponseCode();
            String retMsg = con.getResponseMessage();
            Log.d("Upload", "response code: " + retCode + " message: " + retMsg);
            System.err.println("\nreturn code: " + retCode + " " + retMsg);
            if (retCode != 200) {
                // Look for a detailed error message from the server
                if (con.getHeaderField("Error") != null)
                    retMsg += "\n" + con.getHeaderField("Error");
                con.disconnect();
                throw new RuntimeException(retCode + " " + retMsg);
            }
            out.close();
            con.disconnect();
            return retCode;
        } catch (IOException io) {
        } catch (Exception oa) {
        }
        return -1;
    }

    /**
     * Method for reading a track from memory. Return a string representation of
     * a saved track.
     * 
     * @param trackXml
     *            the xml file of the gpx track
     * @param fileName
     *            the filename of the gpx track
     * @return a file object containing the gpx tracks
     */
    private File createGpxFile(String trackXml, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(trackXml);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Test method to debug the upload.
     * 
     * @return xml file with tracks
     */
    public String testTrack() {
        StringBuilder sb = new StringBuilder();
        sb.append("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" version=\"1.1\" creator=\"Data4All - https://data4all.github.io/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\" >");
        sb.append("<trk>");
        sb.append("<name>2015_03_05_04_01_07</name>");
        sb.append("<trkseg>");
        sb.append("<trkpt lat=\"53.09714087006391\" lon=\"8.835233152546115\">");
        sb.append("<time>2015-03-05T02:57:42Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"53.097029161158886\" lon=\"8.835149370872804\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:43Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"53.096913698361774\" lon=\"8.835062773771332\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:44Z</time>");
        sb.append("</trkpt>");
        sb.append("<trkpt lat=\"53.0967978943368\" lon=\"8.834975920752603\">");
        sb.append("<elem>0.0</elem>");
        sb.append("<time>2015-03-05T02:57:45Z</time>");
        sb.append("</trkpt>");
        sb.append("</trkseg>");
        sb.append("</trk>");
        sb.append("</gpx>");
        return sb.toString();
    }

    /**
     * @param out
     * @param string
     * @param gpxFile
     * @throws IOException
     */
    private void writeContentDispositionFile(DataOutputStream out, String name,
            File gpxFile) throws IOException {
        out.writeBytes("--" + BOUNDARY + LINE_END);
        out.writeBytes("Content-Disposition: form-data; name=\"" + name
                + "\"; filename=\"" + gpxFile.getName() + "\"" + LINE_END);
        out.writeBytes("Content-Type: application/octet-stream" + LINE_END);
        out.writeBytes(LINE_END);

        byte[] buffer = new byte[BUFFER_SIZE];
        // int fileLen = (int)gpxFile.length();
        int read;
        int sumread = 0;
        InputStream in = new BufferedInputStream(new FileInputStream(gpxFile));
        Log.i(TAG, "Transferring data to server");
        while ((read = in.read(buffer)) >= 0) {
            out.write(buffer, 0, read);
            out.flush();
            sumread += read;
            // System.out.print("Transferred " + ((1.0 * sumread / fileLen) *
            // 100) + "%                                \r");
        }
        in.close();
        out.writeBytes(LINE_END);
    }

    /**
     * @param string
     * @param urlDesc
     * @throws IOException
     */
    public void writeContentDisposition(DataOutputStream out, String name,
            String value) throws IOException {
        out.writeBytes("--" + BOUNDARY + LINE_END);
        out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\""
                + LINE_END);
        out.writeBytes(LINE_END);
        out.writeBytes(value + LINE_END);
    }

    /**
     * 
     * @param s
     * @return
     */
    public static String encodeBase64(String s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < (s.length() + 2) / 3; ++i) {
            int l = Math.min(3, s.length() - i * 3);
            String buf = s.substring(i * 3, i * 3 + l);
            out.append(BASE64_ENC.charAt(buf.charAt(0) >> 2));
            out.append(BASE64_ENC.charAt((buf.charAt(0) & 0x03) << 4
                    | (l == 1 ? 0 : (buf.charAt(1) & 0xf0) >> 4)));
            out.append(l > 1 ? BASE64_ENC.charAt((buf.charAt(1) & 0x0f) << 2
                    | (l == 2 ? 0 : (buf.charAt(2) & 0xc0) >> 6)) : '=');
            out.append(l > 2 ? BASE64_ENC.charAt(buf.charAt(2) & 0x3f) : '=');
        }
        return out.toString();
    }

}
