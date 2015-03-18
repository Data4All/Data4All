package io.github.data4all.util;

import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.TransformationParamBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
     * The name of the gallery folder.
     */
    private static final String DIRECTORY_NAME = "gallery";

    /**
     * The exception message for null arguments.
     */
    private static final String NULL_ARGUMENT = "Parameter '%s' cannot be null";

    /**
     * The ending of the image file itself.
     */
    private static final String ENDING_JPEG = ".jpeg";

    /**
     * The ending if the information file of an image.
     */
    private static final String ENDING_INFO = ".info";

    /**
     * The working directory of this gallery.
     */
    private final File workingDirectory;

    /**
     * This class holds the informations in the information file for return
     * purpose.
     * 
     * @author tbrose
     */
    public static class Informations {
        private final TransformationParamBean parameters;
        private final DeviceOrientation orientation;
        private final Point dimension;

        private Informations(TransformationParamBean parameters,
                DeviceOrientation orientation, Point dimension) {
            this.parameters = parameters;
            this.orientation = orientation;
            this.dimension = dimension;
        }

        /**
         * @return The camera and device parameters.
         */
        public TransformationParamBean getParameters() {
            return parameters;
        }

        /**
         * @return The device orientation.
         */
        public DeviceOrientation getOrientation() {
            return orientation;
        }

        /**
         * @return The display dimension.
         */
        public Point getDimension() {
            return dimension;
        }
    }

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
        if (imageData == null) {
            throw new IllegalArgumentException(String.format(NULL_ARGUMENT,
                    "imageData"));
        } else if (parameters == null) {
            throw new IllegalArgumentException(String.format(NULL_ARGUMENT,
                    "parameters"));
        } else if (orientation == null) {
            throw new IllegalArgumentException(String.format(NULL_ARGUMENT,
                    "oriantation"));
        } else if (dimension == null) {
            throw new IllegalArgumentException(String.format(NULL_ARGUMENT,
                    "dimension"));
        } else {
            final long time = System.currentTimeMillis();
            final JSONObject json;
            try {
                json = encodeInformations(parameters, orientation, dimension);
            } catch (JSONException e) {
                throw new IOException(e);
            }

            try {
                saveData(time, ENDING_JPEG, imageData);
            } catch (IOException e) {
                deleteData(time, ENDING_JPEG);
                throw e;
            }

            try {
                saveData(time, ENDING_INFO, json.toString().getBytes("UTF-8"));
            } catch (IOException e) {
                deleteData(time, ENDING_JPEG, ENDING_INFO);
                throw e;
            }
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
        final File result = buildPath(timestamp, ENDING_JPEG);
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
        final File result = buildPath(timestamp, ENDING_INFO);
        if (result.exists()) {
            FileInputStream stream = new FileInputStream(result);
            byte[] content = IOUtils.toByteArray(stream);
            IOUtils.closeQuietly(stream);
            try {
                final JSONObject jsonObject = new JSONObject(new String(
                        content, "UTF-8"));
                return decodeInformations(jsonObject);
            } catch (JSONException e) {
                throw new IOException("Content cannot be parsed");
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
        final File[] files = getImageFiles();
        final long[] result = new long[files.length];
        for (int i = 0; i < files.length; i++) {
            final File f = files[i];
            result[i] = Long.parseLong(f.getName().replace(ENDING_JPEG, ""));
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
        for (final File f : workingDirectory.listFiles()) {
            if (f.getName().endsWith(ENDING_JPEG)) {
                images.add(f);
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
        deleteData(timestamp, ENDING_JPEG, ENDING_INFO);
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
        if (checkOrCreateWorkingDirectory()) {
            FileOutputStream stream = null;
            try {
                final File file = buildPath(timestamp, ending);
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
            final File file = buildPath(timestamp, ending);
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
     * @return A JSONObject holding the infornations
     * @throws JSONException
     *             If an JSON error occurs
     */
    private JSONObject encodeInformations(TransformationParamBean parameters,
            DeviceOrientation orientation, Point dimension)
            throws JSONException {
        final JSONObject json = new JSONObject();
        return json
                .put("parameters", parameters.toJSON())
                .put("orentation", orientation.toJSON())
                .put("dimension",
                        new JSONArray(Arrays.asList(dimension.x, dimension.y)));
    }

    /**
     * Decodes the informations of an image from the given JSON.
     * 
     * @param json
     * @return
     * @throws JSONException
     */
    private Informations decodeInformations(JSONObject json)
            throws JSONException {
        TransformationParamBean parameters = TransformationParamBean
                .fromJSON(json.getJSONArray("parameters"));
        DeviceOrientation oriantation = DeviceOrientation
                .fromJSON((JSONArray) json.getJSONArray("orientation"));
        JSONArray dimension = json.getJSONArray("dimension");

        return new Informations(parameters, oriantation, new Point(
                dimension.getInt(0), dimension.getInt(1)));
    }

    /**
     * Creates the working directory if it currently does not exists.
     * 
     * @return If the working directory exists after the call of this method
     */
    private final boolean checkOrCreateWorkingDirectory() {
        return workingDirectory.exists() || workingDirectory.mkdirs();
    }
}
