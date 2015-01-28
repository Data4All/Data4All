/**
 * Copyright (C) Data4All
 */
package io.github.data4all.model.drawing;

/**
 * An interface for analyzer in general purpose.<br/>
 * It provides the analyze method to analyze the (constructor)-given data in his
 * context.
 * 
 * @author tbrose
 *
 */
public interface Analyzer<T> {
    /**
     * Analyze the given data.
     * 
     * @return The result of the analyzing
     */
    T analyze();
}
