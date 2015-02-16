package org.eclipset.cdt.flatui;

import java.util.EnumSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipset.cdt.flatui.Box.Measurements;

public class BoxControl extends Canvas {
	
	InlineTextBox inline;
	InlineTextBox inline2;
	InlineTextBox inline3;
	int x;
	int x2;
	
	LineBox line;
	
	public BoxControl(Composite parent, int border)
	{
		super(parent, SWT.NONE);
		
		StyleRule style1 = new StyleRule();
		style1.setBorderWidth(border);
		style1.setBorderRadius(17);
		style1.setPaddingWidthEm(0.75);
		style1.setPaddingHeightEm(0.25);
		style1.setColor(SWT.COLOR_LIST_SELECTION_TEXT);
		style1.setBackgroundColor(SWT.COLOR_LIST_SELECTION);
		style1.setBorderColor(SWT.COLOR_BLACK);

		StyleRule style2 = new StyleRule();
		style2.setBorderWidth(0);
		style2.setBorderRadius(17);
		style2.setPaddingWidthEm(0.75);
		style2.setPaddingHeightEm(0.25);
		style2.setColor(SWT.COLOR_LIST_SELECTION_TEXT);
		style2.setBackgroundColor(SWT.COLOR_BLUE);
		style2.setBorderColor(SWT.COLOR_BLACK);

		StyleRule style3 = new StyleRule();
		style3.setBorderWidth(border);
		style3.setBorderRadius(17);
		style3.setPaddingWidthEm(0.75);
		style3.setPaddingHeightEm(0.25);
		style3.setColor(SWT.COLOR_LIST_FOREGROUND);
		style3.setBackgroundColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		style3.setBorderColor(SWT.COLOR_BLACK);
		
		StyleRule hover = new StyleRule();
		hover.setBorderColor(SWT.COLOR_LIST_SELECTION);
		hover.setBackgroundColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
		
		DeclaredBoxStyle boxStyle = new DeclaredBoxStyle();
		boxStyle.add(EnumSet.noneOf(Box.State.class), style3);
		boxStyle.add(EnumSet.of(Box.State.ACTIVE), style1);
		boxStyle.add(EnumSet.of(Box.State.HOVER), hover);

		inline = new InlineTextBox("First", boxStyle);
		inline2 = new InlineTextBox("Second", boxStyle);
		inline3 = new InlineTextBox("Third", boxStyle);
		
		line = new LineBox() {
			
			Box hover;
			
			@Override
			void mouseDown(MouseEvent e) {
				Box b = getAt(e.x, e.y);
				
				for (Box child: children) {
					child.clearState(Box.State.ACTIVE);
				}
				
				b.setState(Box.State.ACTIVE);
				// should be done by some better method.
				redraw();
			}
			
			void mouseMove(MouseEvent e) {
				Box b = getAt(e.x, e.y);
				
				if (b != hover) {
					
					if (hover != null) {
						
						hover.clearState(Box.State.HOVER);
						
					}
					
					if (b != null) { 
						b.setState(Box.State.HOVER);	
					}
					hover = b;
				}
				
				redraw();			
			}
			
			void mouseExit(MouseEvent e) {
				if (hover != null) {
					hover.clearState(Box.State.HOVER);
					hover = null;
					redraw();
				}				
			}
			
		};
		line.add(inline).add(inline2).add(inline3);
		
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				onPaint(e);
			}
		});	
		
		addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				line.mouseMove(e);
			}
		});
		addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				line.mouseExit(e);
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				line.mouseDown(e);
			}
		});		
		

	}
	
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return computeSize(wHint, hHint);
	}
	
	public Point computeSize(int wHint, int hHint)
	{
		GC gc = new GC(this);
		
		Measurements m = line.layout(gc);
		Point result = new Point(m.width, m.height);
		gc.dispose();
		
		return result;
	}
	
	protected void onPaint(PaintEvent event) {
		
		line.paint(event.gc);
	}

}
