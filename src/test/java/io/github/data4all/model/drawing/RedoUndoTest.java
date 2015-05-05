/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.data4all.model.drawing;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test cases for the RedoUndo class
 * 
 * @author vkochno
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class RedoUndoTest extends MotionInterpreterTest {

	private RedoUndo redoUndo;
	private List<Point> polygon;
	
    private Point point1;
    private Point point2;
    private Point point3;
    private Point point4;
    private Point point5;
    
    @Before
    public void setUp() throws Exception {

        point1 = new Point(2, 2);
        point2 = new Point(40, 40);
        point3 = new Point(43, 43);
        point4 = new Point(33, 33);
        point5 = new Point(1, 1);

        polygon = new ArrayList<Point>();

        polygon.add(point1);
        polygon.add(point2);
        polygon.add(point3);

        redoUndo = new RedoUndo(polygon);
    }
    
    @Test
    public void getSizeTest(){
    	assertEquals(redoUndo.getMax(),3);
    }
    
    @Test
    public void redoUndoTest(){
    	assertEquals(redoUndo.undo(),point3);
    	assertEquals(redoUndo.undo(),point2);
    	assertEquals(redoUndo.undo(),point1);
    	assertEquals(redoUndo.redo(),point1);
    	assertEquals(redoUndo.redo(),point2);
    	assertEquals(redoUndo.redo(),point3);
    }
    
    @Test
    public void addTest(){
    	redoUndo.add(point2, "MOVE_FROM", polygon.indexOf(point2));
    	redoUndo.add(point4, "MOVE_TO", polygon.indexOf(point2));
    	redoUndo.add(point3, "DELET", polygon.indexOf(point3));
    	redoUndo.undo();
    	assertEquals(redoUndo.getAction(),"DELET");
    	redoUndo.undo();
    	assertEquals(redoUndo.getAction(),"MOVE_TO");
    	redoUndo.undo();
    	assertEquals(redoUndo.getAction(),"MOVE_FROM");
    	redoUndo.undo();
    	assertEquals(redoUndo.getAction(),"ADD");
    	redoUndo.undo();
    	assertEquals(redoUndo.getAction(),"ADD");
    	redoUndo.undo();
    	assertEquals(redoUndo.getAction(),"ADD");
    }
    
    @Test
    public void moveTest(){
    	redoUndo.add(point2, "MOVE_FROM", polygon.indexOf(point2));
    	redoUndo.add(point1, "MOVE_TO", polygon.indexOf(point2));
    	redoUndo.add(point3, "MOVE_TO", polygon.indexOf(point2));
    	redoUndo.add(point4, "MOVE_TO", polygon.indexOf(point2));
    	Point point = redoUndo.undo();
    	assertEquals(redoUndo.getAction(),"MOVE_TO");
    	assertEquals(point,point4);
    	point = redoUndo.undo();
    	assertEquals(redoUndo.getAction(),"MOVE_FROM");
    	assertEquals(point,point2);
    }

    
}
