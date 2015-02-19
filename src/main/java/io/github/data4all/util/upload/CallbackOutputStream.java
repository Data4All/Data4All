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

import java.io.IOException;
import java.io.OutputStream;

/**
 * A custom OutputStream which encapsulates the {@link OutputStream} to provide
 * a callback for the progress of reading.
 * 
 * @author tbrose
 */
public final class CallbackOutputStream extends OutputStream {
    private final OutputStream outstream;
    private final Callback<Integer> callback;
    private int currentRead;

    /**
     * Constructs a OutputStream which pipes the content to write to
     * {@code outstream} and calls the callback {@code callback}.
     * 
     * @param outstream
     *            The stream to write to
     * @param callback
     *            The callback to call
     */
    public CallbackOutputStream(OutputStream outstream,
            Callback<Integer> callback) {
        this.outstream = outstream;
        this.callback = callback;
    }

    /**
     * Gives a callback every time a byte is read.
     */
    @Override
    public void write(int oneByte) throws IOException {
        currentRead++;
        callback.callback(currentRead);
        outstream.write(oneByte);
    }
}