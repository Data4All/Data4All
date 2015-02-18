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
package io.github.data4all.util.oauth.exception;

/**
 * Indicates an failure in a osm progress.
 * 
 * @author tbrose
 */
public class OsmException extends Exception {
    private static final long serialVersionUID = 8058911944806018283L;

    /**
     * Constructs a new exception with null as its detail message.<br/>
     * The cause is not initialized, and may subsequently be initialized by a
     * call to Throwable.initCause(java.lang.Throwable).
     */
    public OsmException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.<br/>
     * The cause is not initialized, and may subsequently be initialized by a
     * call to Throwable.initCause(java.lang.Throwable).
     * 
     * @param message
     *            The detail message. The detail message is saved for later
     *            retrieval by the Throwable.getMessage() method.
     */
    public OsmException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message
     * of (cause==null ? null : cause.toString()) (which typically contains the
     * class and detail message of cause). This constructor is useful for
     * exceptions that are little more than wrappers for other throwables (for
     * example, PrivilegedActionException).
     * 
     * @param cause
     *            The cause (which is saved for later retrieval by the
     *            Throwable.getCause() method). (A null value is permitted, and
     *            indicates that the cause is nonexistent or unknown.)
     */
    public OsmException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * <br/>
     * Note that the detail message associated with cause is not automatically
     * incorporated in this exception's detail message.
     * 
     * @param message
     *            The detail message (which is saved for later retrieval by the
     *            Throwable.getMessage() method).
     * @param cause
     *            The cause (which is saved for later retrieval by the
     *            Throwable.getCause() method). (A null value is permitted, and
     *            indicates that the cause is nonexistent or unknown.)
     */
    public OsmException(String message, Throwable cause) {
        super(message, cause);
    }
}
