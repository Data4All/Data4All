package io.github.data4all.util;

import android.location.Location;


/**
 * Class RingBuffer.
 * 
 * more like FIFO-queue
 * 
 * 
 * @author Koalamann
 *
 */
public class RingBuffer {

    /** the buffer that contains the data */
    private Location[] buffer;

    /** head of the ringbuffer. the first free position */
    private int head;

    /** the newest entry */
    private int index;
    
    /** fill level of the buffer */
    private int entries;

    /**
     * creates the ringpuffer.
     * 
     * @param capacity
     */
    public RingBuffer(int capacity) {
        buffer = new Location[capacity];
        head = 0;
        entries = 0;
    }

    /**
     * adds a new location.
     * 
     * @param location
     *            that is added.
     */
    public void add(Location value) {
        if (buffer.length > 0) {
            buffer[head] = value;
            head = (head + 1) % buffer.length;
            if (entries < buffer.length) {
                ++entries;
            }
            if (index < buffer.length){
                ++index;
               
            }else{
                index=0;
            }
        }
    }

    /**
     * returns the oldest Element
     * not needed
     * @return Location
     */
//    public Location pop() {
//        Location value = peek();
//        --entries;
//        return value;
//    }

    /**
     * @param position
     * @return Location
     */
    
    public Location get(int position) {
        if (size()>0) {
         
            return buffer[position];
        }
        return null;
    }

    /**
     * 
     * @return the array with all Data.
     */
    public Location[] getAll(){
        
        return buffer;
    }
    
    /**
     * returns the oldest Element but it stays in the ringbuffer
     * 
     * @return the oldest location.
     */
//    public Location peek() {
//        return buffer[(head - entries + buffer.length) % buffer.length];
//    }

    /**
     * @return the number of elements in the buffer.
     */
    public int size() {
        return entries;
    }

    /**
     * 
     * @return the index of the newest entry in the buffer.
     */

    public int index(){
        return index;
    }

}