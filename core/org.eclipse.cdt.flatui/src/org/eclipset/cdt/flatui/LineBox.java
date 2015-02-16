package org.eclipset.cdt.flatui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;

/* Box that contains inline boxes, arranged horizontally. */
public class LineBox extends Box {
	
	public LineBox() {
		
	}
	
	public LineBox add(Box child) {
		children.add(child);
		return this;
	}

	@Override
	Measurements layout(GC gc) {
		
		// FIXME: must be customizable or tied to font.
		int spacing = 10;
		
		int top = 0;
		int x = 0;
		
		rectangles.clear();
		
		for (Box box: children) {
			Measurements m = box.layout(gc);
			
			// For now, ignore baseline, put everything at 0.
			Rectangle r = new Rectangle(x, 0, m.width, m.height);
			rectangles.add(r);
			
			top = Math.max(top,  m.height);
			x += m.width + spacing;
		}
		
		Measurements m = new Measurements();
		m.width = (x > 0) ? (x - spacing) : 0;
		m.height = top;
		m.baseline = 0;
		return m;
	}

	@Override
	void paint(GC gc) {

		for (int i = 0; i < children.size(); ++i) {
		
			Rectangle r = rectangles.get(i);
			
			// FIXME: this appears to clip away more than necessary on
			// subsequent redraws.
			
			//if (gc.getClipping().intersects(r)) {
			
				Transform t = new Transform(gc.getDevice());
				t.translate(r.x, r.y);
				gc.setTransform(t);
			
				children.get(i).paint(gc);
			//}

		}
	}
	
	protected Box getAt(int x, int y) {
		// At least for line boxes, we'll typically have less than 6 elements,
		// so using binary search would not give us much.
		for (int i = 0; i < rectangles.size(); ++i) {
			if (rectangles.get(i).contains(x, y)) {
				return children.get(i);
			}
		}
		return null;
	}
	
	protected List<Box> children = new ArrayList<Box>();
	protected List<Rectangle> rectangles = new ArrayList<Rectangle>();
	
}
