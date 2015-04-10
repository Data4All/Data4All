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
package io.github.data4all.util;

import io.github.data4all.Exceptions;
import io.github.data4all.logger.Log;
import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.TransformationParamBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Point;

/**
 * A gallery holds images for being tagged later.
 * 
 * @author tbrose
 */
public class Gallery {
    /**
     * 
     */
    private static final String JSON_DIMENSION = "dimension";

    /**
     * 
     */
    private static final String JSON_ORIENTATION = "orientation";

    /**
     * 
     */
    private static final String JSON_PARAMETER = "parameters";

    /**
     * The name of the gallery folder.
     */
    private static final String DIRECTORY_NAME = "gallery";

    /**
     * The ending of the image file itself.
     */
    private static final String ENDING_JPEG = ".jpeg";

    /**
     * The ending if the information file of an image.
     */
    private static final String ENDING_INFO = ".info";

    private static final String LOG_TAG = Gallery.class.getSimpleName();

    public static final String GALLERY_ID_EXTRA = Gallery.class.getName()
            + ":GALLERY_ID";

    /**
     * The working directory of this gallery.
     */
    private final File workingDirectory;

    /**
     * Constructs a gallery to read from and write to.
     * 
     * @param context
     *            The context of this gallery
     */
    public Gallery(Context context) {
        workingDirectory = new File(context.getFilesDir(), DIRECTORY_NAME);
    }

    /**
     * Saves the given image to this gallery with the parameters, the
     * orientation and the dimension linked to this image.
     * 
     * @param imageData
     *            The raw image data, cannot be {@code null}
     * @param parameters
     *            The camera-parameters, cannot be {@code null}
     * @param orientation
     *            The orientation of the device, cannot be {@code null}
     * @param dimension
     *            The screen dimension, cannot be {@code null}
     * @throws IOException
     *             If the data cannot be stored
     */
    public void addImage(final byte[] imageData,
            TransformationParamBean parameters, DeviceOrientation orientation,
            Point dimension) throws IOException {
        Log.d(LOG_TAG, "adding image");
        if (imageData == null) {
            throw Exceptions.nullArgument("imageData");
        } else if (parameters == null) {
            throw Exceptions.nullArgument(JSON_PARAMETER);
        } else if (orientation == null) {
            throw Exceptions.nullArgument("oriantation");
        } else if (dimension == null) {
            throw Exceptions.nullArgument(JSON_DIMENSION);
        } else {
            final long time = System.currentTimeMillis();
            final JSONObject json =
                    encodeInformations(parameters, orientation, dimension);

            this.save(imageData, time, json);
            Log.d(LOG_TAG, "image added");
        }
    }

    private void save(final byte[] imageData, final long time,
            final JSONObject json) throws IOException {
        try {
            this.saveData(time, ENDING_JPEG, imageData);
        } catch (IOException e) {
            this.deleteData(time, ENDING_JPEG);
            throw e;
        }

        try {
            this.saveData(time, ENDING_INFO, json.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            this.deleteData(time, ENDING_JPEG, ENDING_INFO);
            throw e;
        }
    }

    /**
     * Receives the file of the image at the given timestamp.
     * 
     * @param timestamp
     *            The timestamp of the image
     * @return The file object of the image
     * @throws FileNotFoundException
     *             If the image is not stored in this gallery
     */
    public File getImageFile(long timestamp) throws FileNotFoundException {
        final File result = this.buildPath(timestamp, ENDING_JPEG);
        if (result.exists()) {
            return result;
        } else {
            throw new FileNotFoundException("No image found for timestamp "
                    + timestamp);
        }
    }

    /**
     * Receives the informations of the image at the given timestamp.
     * 
     * @param timestamp
     *            The timestamp of the image
     * @return The information object of the image
     * @throws IOException
     *             If an I/O error occurs
     * @throws FileNotFoundException
     *             If the image is not stored in this gallery
     */
    public Informations getImageInformations(long timestamp) throws IOException {
        final File result = this.buildPath(timestamp, ENDING_INFO);
        if (result.exists()) {
            final FileInputStream stream = new FileInputStream(result);
            final byte[] content = IOUtils.toByteArray(stream);
            IOUtils.closeQuietly(stream);
            try {
                final JSONObject jsonObject =
                        new JSONObject(new String(content, "UTF-8"));
                return decodeInformations(jsonObject);
            } catch (JSONException e) {
                throw new IOException("Content cannot be parsed", e);
            }
        } else {
            throw new FileNotFoundException("No image found for timestamp "
                    + timestamp);
        }
    }

    /**
     * Lists the timestamp of all images in this gallery.
     * 
     * @return An array with timestamps
     */
    public long[] getImages() {
        final File[] files = this.getImageFiles();
        final Long[] timestamps = new Long[files.length];
        for (int i = 0; i < files.length; i++) {
            final File f = files[i];
            timestamps[i] = Long.parseLong(f.getName().replace(ENDING_JPEG, ""));
        }
        final List<Long> asList = Arrays.asList(timestamps);
        Collections.sort(asList, new Comparator<Long>() {
            @Override
            public int compare(Long lhs, Long rhs) {
                return rhs.compareTo(lhs);
            }
        });
        final long[] result = new long[asList.size()];
        for (int i = 0; i < asList.size(); i++) {
            result[i] = asList.get(i);
        }
        return result;
    }

    /**
     * Lists the file-objects of all images in this gallery.
     * 
     * @return An array with files, never {@code null}
     */
    public File[] getImageFiles() {
        final List<File> images = new ArrayList<File>();
        final File[] files = workingDirectory.listFiles();
        if (files != null) {
            for (final File f : files) {
                if (f.getName().endsWith(ENDING_JPEG)) {
                    images.add(f);
                }
            }
        }
        return images.toArray(new File[images.size()]);
    }

    /**
     * Let this gallery forget about the given image. If the picture does not
     * exists, nothing will be deleted.<br/>
     * This operation cannot be undone!
     * 
     * @param timestamp
     *            The timestamp of the image
     */
    public void deleteImage(long timestamp) {
        this.deleteData(timestamp, ENDING_JPEG, ENDING_INFO);
    }

    /**
     * Add a file to the gallery folder.
     * 
     * @param timestamp
     *            The timestamp of the image
     * @param ending
     *            The file ending
     * @param content
     *            The content of the file
     * @throws IOException
     *             If an I/O error occurs
     */
    private void saveData(long timestamp, String ending, byte[] content)
            throws IOException {
        Log.d(LOG_TAG, "saving data " + timestamp + ending);
        if (this.checkOrCreateWorkingDirectory()) {
            FileOutputStream stream = null;
            try {
                final File file = this.buildPath(timestamp, ending);
                stream = new FileOutputStream(file);
                stream.write(content);
            } finally {
                IOUtils.closeQuietly(stream);
            }
        } else {
            throw new IOException("Gallery folder cannot be created.");
        }
    }

    /**
     * Removes a file from the gallery folder.
     * 
     * @param timestamp
     *            The timestamp of the image
     * @param endings
     *            The file ending
     */
    private void deleteData(long timestamp, String... endings) {
        for (String ending : endings) {
            final File file = this.buildPath(timestamp, ending);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * Builds the file object for the given time and ending.
     * 
     * @param timestamp
     *            The timestamp of the image
     * @param ending
     *            The file-ending
     * @return The full qualified file object for the gallery folder
     */
    private File buildPath(long timestamp, String ending) {
        return new File(workingDirectory, timestamp + ending);
    }

    /**
     * Encodes the given informations of an image into a JSON.
     * 
     * @param parameters
     *            The parameters of the image
     * @param orientation
     *            The orientation of the device
     * @param dimension
     *            The screen dimension of the device
     * @return A JSONObject holding the informations
     * @throws IOException
     *             If an JSON error occurs
     */
    private static JSONObject encodeInformations(
            TransformationParamBean parameters, DeviceOrientation orientation,
            Point dimension) throws IOException {
        final JSONObject json = new JSONObject();
        try {
            return json
                    .put(JSON_PARAMETER, parameters.toJSON())
                    .put(JSON_ORIENTATION, orientation.toJSON())
                    .put(JSON_DIMENSION,
                            new JSONArray(Arrays.asList(dimension.x,
                                    dimension.y)));
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Decodes the informations of an image from the given JSON.
     * 
     * @param json
     * @return
     * @throws JSONException
     */
    private static Informations decodeInformations(JSONObject json)
            throws JSONException {
        final TransformationParamBean parameters =
                TransformationParamBean.fromJSON(json
                        .getJSONArray(JSON_PARAMETER));
        final DeviceOrientation oriantation =
                DeviceOrientation.fromJSON((JSONArray) json
                        .getJSONArray(JSON_ORIENTATION));
        final JSONArray dimension = json.getJSONArray(JSON_DIMENSION);

        return new Informations(parameters, oriantation, new Point(
                dimension.getInt(0), dimension.getInt(1)));
    }

    /**
     * Creates the working directory if it currently does not exists.
     * 
     * @return If the working directory exists after the call of this method
     */
    private boolean checkOrCreateWorkingDirectory() {
        return workingDirectory.exists() || workingDirectory.mkdirs();
    }

    /**
     * This class holds the informations of the information file for return
     * purpose.
     * 
     * @author tbrose
     */
    public static final class Informations {
        private final TransformationParamBean parameters;
        private final DeviceOrientation orientation;
        private final Point dimension;

        private Informations(TransformationParamBean parameters,
                DeviceOrientation orientation, Point dimension) {
            this.parameters = parameters;
            this.orientation = orientation;
            this.dimension = dimension;
        }

        public TransformationParamBean getParameters() {
            return parameters;
        }

        public DeviceOrientation getOrientation() {
            return orientation;
        }

        public Point getDimension() {
            return dimension;
        }
    }
}
