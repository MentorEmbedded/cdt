package org.eclipset.cdt.flatui;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;

/* Rectangular object that can draw itself on a GC. 
 * 
 * The client should call the 'layout' method to compute
 * measurements, and then can repeatedly call 'paint'.
 */
abstract class Box {
	
	enum State {
		DISABLED,
		HOVER,
		ACTIVE
	};
	
	class Measurements {
		int width;
		int height;
		int baseline;
	};
	
	abstract Measurements layout(GC gc);
	abstract void paint(GC gc);
	
	Box setState(State state) { return this; }
	Box clearState(State state) { return this; }
	
	void mouseDown(MouseEvent e) {}
	void mouseMove(MouseEvent e) {}
	void mouseExit(MouseEvent e) {}

}
