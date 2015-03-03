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

/**
 * Interface to handle a generic Callback.
 * 
 * @author tbrose
 *
 * @param <T>
 *            The argument type which should be delivered by the callback.
 */
public interface Callback<T> {
    /**
     * This method handles the callback (execute it).
     * 
     * @param t
     *            The argument which should be delivered through the callback.
     */
    void callback(T t);

    /**
     * To prevent a callback flood the callback can specify an interval for the
     * callback.<br/>
     * If a callback returns {@code n}, only every {@code n}-th callback-event
     * will call the callback-method.
     * 
     * @return The requested interval of the callback.
     */
    int interval();
}
