package io.github.data4all.util;

import java.lang.reflect.Array;


/**
 * Class RingBuffer.
 * 
 * more like FIFO-queue
 * 
 * 
 * @author Koalamann
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
    public RingBuffer(Class<T> type,int capacity) {
        
        buffer = (T[]) Array.newInstance(type,capacity);
        head = 0;
        entries = 0;
    }

    @SuppressWarnings("hiding")
    public <T> T[] getArray(Class<T> clazz, int size) {
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) Array.newInstance(clazz, size);

        return arr;
    }
    
    /**
     * adds a new T.
     * 
     * @param T
     *            that is added.
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
     * returns the oldest Element
     * not needed
     * @return T
     */
//    public T pop() {
//        T value = peek();
//        --entries;
//        return value;
//    }

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
     * returns the oldest Element but it stays in the ringbuffer
     * 
     * @return the oldest T.
     */
//    public T peek() {
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