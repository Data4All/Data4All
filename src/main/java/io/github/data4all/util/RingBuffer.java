/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.github.data4all.util;

/**
 * Class RingBuffer.
 * 
 * generic RingBuffer for temporary storage of Locations and Devicepositions.
 * 
 * @author konerman
 *
 * @param T
 *          the 
 */
public class RingBuffer<T> {

    /* the buffer that contains the data */
    private T[] buffer;

    /* head of the ringbuffer. the first free position */
    private int head;

    /* the newest entry */
    private int index;

    /* fill level of the buffer */
    private int entries;

    /* the capacity of the buffer */
    private int buffercapacity;

    /**
     * creates the ringpuffer.
     * 
     * @param capacity
     *          the maximum of items in the Ringbuffer
     */
    @SuppressWarnings("unchecked")
    public RingBuffer(int capacity) {

        buffer = (T[]) new Object[capacity];
        head = 0;
        entries = 0;
        setBuffercapacity(capacity);
    }

    /**
     * adds a new Element.
     * 
     * @param value
     *          the element that is added.
     */
    public void put(T value) {
        if (buffer.length > 0) {
            buffer[head] = value;

            index = head;

            head = (head + 1) % buffer.length;

            if (entries < buffer.length) {
                ++entries;
            }
        }

    }

    
    /**
     * get the object of the given position.
     * 
     * @param position
     *              the position of the object you want to get 
     * @return T
     *              the object at the position
     */             

    public T get(int position) {
        if (getSize() > 0) {
            return buffer[position];
        }
        return null;
    }

    /**
     * get an array of all objects in the ringbuffer.
     * 
     * @return the array with all Data.
     */
    public T[] getAll() {

        return buffer;
    }

    /**
     * @return the number of elements in the buffer.
     */
    public int getSize() {
        return entries;
    }

    /**
     * @return the index of the newest entry in the buffer.
     */

    public int getIndex() {
        return index;
    }

    /**
     * get the last object that was added.
     * 
     * @return the last added entry of the buffer.
     */
    public T getLast() {
        if (getSize() > 0) {
            return buffer[index];
        }
        return null;
    }

    public int getBuffercapacity() {
        return buffercapacity;
    }

    public void setBuffercapacity(int buffercapacity) {
        this.buffercapacity = buffercapacity;
    }
}
