package io.github.data4all.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RingBufferTest {


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testPut() {
        RingBuffer ringBuffer = new RingBuffer(5); 
        ringBuffer.put(1);
        ringBuffer.put(2);
        ringBuffer.put(3);
        assertEquals(3,ringBuffer.getSize());
        ringBuffer.put(4);
        ringBuffer.put(5);
        assertEquals(5,ringBuffer.getSize());
        //ringBuffer is full, size should stay the same
        ringBuffer.put(6);
        assertEquals(5,ringBuffer.getSize());
        ringBuffer.put(7);
        assertEquals(5,ringBuffer.getSize());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testGet() {
      
        RingBuffer ringBuffer = new RingBuffer(5); 
        ringBuffer.put(1);
        ringBuffer.put(2);
        ringBuffer.put(3);
        ringBuffer.put(4);
        ringBuffer.put(5);
        assertEquals(1, ringBuffer.get(0));
        assertEquals(3, ringBuffer.get(2));
        assertEquals(5, ringBuffer.get(4));
        //ringBuffer is full, should overwrite the oldest position
        ringBuffer.put(6);
        assertEquals(6, ringBuffer.get(0));
    }
        
    
    /**
     * length of the returned array should always be the same.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testGetAll() {
        RingBuffer ringBuffer = new RingBuffer(4); 
        ringBuffer.put(1);
        ringBuffer.put(2);
        assertEquals(4,ringBuffer.getAll().length);
        ringBuffer.put(3);
        ringBuffer.put(4); 
        assertEquals(4,ringBuffer.getAll().length);
        ringBuffer.put(5);
        assertEquals("blub",4,ringBuffer.getAll().length);
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGetLast() {
        RingBuffer ringBuffer = new RingBuffer(3); 
    
       
        ringBuffer.put(1);
        assertEquals(1,ringBuffer.get(0));
        assertEquals(1,ringBuffer.getLast());
        ringBuffer.put(2);
        assertEquals(2,ringBuffer.getLast());
        ringBuffer.put(3);
        assertEquals(3,ringBuffer.getLast());
        ringBuffer.put(4);
        assertEquals(4,ringBuffer.getLast());
        ringBuffer.put(5);
        assertEquals(5,ringBuffer.getLast());
    }

}
