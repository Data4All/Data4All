package io.github.data4all.util;

/**
 * Class RingBuffer.
 * 
 * generic RingBuffer for temporary storage of Locations and Devicepositions
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

    /** the capacity of the Buffer */
    private int buffercapacity;

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
        setBuffercapacity(capacity);
    }

    /**
     * adds a new Element.
     * 
     * @param the
     *            element that is added.
     */
    public void put(T value) {
        if (buffer.length > 0) {
            buffer[head] = value;
          
            index=head;
            
            head = (head + 1) % buffer.length;
            
            if (entries < buffer.length) {
                ++entries;
            }
        }

    }

    /**
     * @param position
     * @return T
     */

    public T get(int position) {
        if (getSize() > 0) {

            return buffer[position];
        }
        return null;
    }

    /**
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
     * 
     * @return the index of the newest entry in the buffer.
     */

    public int getIndex() {
        return index;
    }

    /**
     * 
     * @return the last added entry of the buffer.
     */
    public T getLast() {
        if(getSize()>0){
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