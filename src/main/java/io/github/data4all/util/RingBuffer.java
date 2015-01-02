package io.github.data4all.util;

import java.lang.reflect.Array;


/**
 * Class RingBuffer.
 * 
 * more like FIFO-queue
 * 
 * 
 * @author konerman
 *
 */
public class RingBuffer<T> {

    /** the buffer that contains the data */
    private T[] buffer;

    /** head of the ringbuffer. the first free position */
    private int head;

    /** the newest entry */
    private int index;
    
    /** fill level of the buffer */
    private int entries;

    /**
     * creates the ringpuffer.
     * 
     * @param type
     * @param capacity
     */
    @SuppressWarnings("unchecked")
    public RingBuffer(int capacity) {
        
        buffer = (T[]) new Object[capacity];
        head = 0;
        entries = 0;
    }

     
    /**
     * adds a new Element.
     * 
     * @param the element that is added.
     */
    public void put(T value) {
        if (buffer.length > 0) {
            buffer[head] = value;
            head = (head + 1) % buffer.length;
            if (entries < buffer.length) {
                ++entries;
            }
            if (index < buffer.length-1){
                ++index;
               
            }else{
                index=0;
            }
        }
    }


    /**
     * @param position
     * @return T
     */
    
    public T get(int position) {
        if (size()>0) {
         
            return buffer[position];
        }
        return null;
    }

    /**
     * 
     * @return the array with all Data.
     */
    public T[] getAll(){
        
        return buffer;
    }
    
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