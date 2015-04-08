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
package io.github.data4all;

/**
 * Helper class for simplifying exception throwing.
 * 
 * @author tbrose
 *
 */
public final class Exceptions {
    /**
     * The exception message for null arguments.
     */
    private static final String NULL_ARGUMENT = "Parameter '%s' cannot be null";

    private Exceptions() {
    }

    /**
     * Constructs an exception for the purpose that the parameter with the given
     * name is {@code null}.
     * 
     * @param parameterName
     *            The name of the parameter which is {@code null}
     * @return A new constructed exception
     */
    public static IllegalArgumentException nullArgument(String parameterName) {
        return new IllegalArgumentException(String.format(NULL_ARGUMENT,
                parameterName));
    }

}
