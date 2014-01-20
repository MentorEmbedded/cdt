/*******************************************************************************
 * Copyright (c) 2014 Mentor Graphics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mentor Graphics - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.debug.ui.dialogs;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

/**
 * This control displays a panel of text items.
 * 
 * Example:
 * <code>
 * PillsControl control = new PillsControl(parent, SWT.NONE);
 * control.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
 * control.setAlignment(SWT.CENTER);
 * control.setItems(new String[] { "Item 1", "Item 2", "Item 3", "Item 4"});
 * // Note: This font should be disposed
 * Font font = new Font(area.getDisplay(), "Arial", 20, SWT.NORMAL);
 * control.setFont(font);
 * </code>
 * 
 * @since 7.4
 */
public class PillsControl extends Canvas {
	/** Default spacing between items */
	private int DEFAULT_ITEM_SPACING = 5;
	/** Default horizontal item margin */
	private int DEFAULT_ITEM_HORIZ_MARGIN = 3;
	/** Default vertical item margin */
	private int DEFAULT_ITEM_VERT_MARGIN = 3;

	/** Items */
	private String[] items = null;
	/** Default size */
	private Point defaultSize = null;
	/** Index of selected item */
	private int selectedItem = -1;
	/** Index of tracked item */
	private int trackedItem = -1;
	/** Index of focus item */
	private int focusItem = -1;
	/** Computed item sizes */
	private Point[] itemSizes = null;
	/** Offset for items */
	private int initialOffset = -1;
	/** Margin */
	private Point margin = new Point(DEFAULT_ITEM_HORIZ_MARGIN, DEFAULT_ITEM_VERT_MARGIN);
	/** Item spacing */
	private int itemSpacing = DEFAULT_ITEM_SPACING;
	/** Items alignment */
	private int alignment = SWT.LEFT;
	/** Selection listeners */
	private ListenerList selectionListeners;
	/** Selection foreground color */
	private Color selectionForeground;
	/** Selection background color */
	private Color selectionBackground;

	/**
	 * Constructor
	 * 
	 * @param parent Parent
	 * @param style Style flags, pass SWT.NO_FOCUS to not
	 * take focus (and no focus rectangle).
	 */
	public PillsControl(Composite parent, int style) {
		super(parent, style);
		
		// Add paint listener
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				onPaint(e);
			}
		});
		// Add mouse listener
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (isEnabled()) {
					// Set selected rating
					int item = getItemForPoint(e.x, e.y);
					setSelection(item);
				}
			}
		});
		// Add mouse move listener
		addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				if (isEnabled()) {
					int item = getItemForPoint(e.x, e.y);
					if (item != trackedItem) {
						trackedItem = item;
						redraw();
					}
				}
			}
		});
		// Add mouse track listener
		addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				if (isEnabled()) {
					// Clear tracked rating
					trackedItem = -1;
					redraw();
				}
			}
		});
		// Add focus listener
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (isEnabled()) {
					// Update focus rectangle
					focusItem = ((getStyle() & SWT.NO_FOCUS) == SWT.NO_FOCUS) ? -1 : 0;
					redraw();
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (isEnabled()) {
					// Update focus rectangle
					focusItem = -1;
					redraw();
				}
			}
			
		});
		// Handle focus traversal
		addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				e.doit = true;
			}
		});
		// Add key listener
		addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// Arrow right - increase the focus item
				if (e.keyCode == SWT.ARROW_RIGHT) {
					if (++focusItem >= getItems().length) {
						focusItem = getItems().length - 1;
					}
					redraw();
				}
				// Arrow left - decrease the focus item
				else if (e.keyCode == SWT.ARROW_LEFT) {
					if (--focusItem <= 0) {
						focusItem = 0;
					}
					redraw();
				}
				// Space - select item
				else if (e.keyCode == SWT.SPACE) {
					setSelection(focusItem);
				}
				e.doit = true;
			}

			@Override
			public void keyReleased(KeyEvent e) {
				e.doit = true;
			}
		});
		
		// Initialize foreground color
		setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		// Initialize background color
		setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		// Initialize selection foreground
		setSelectionForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
		// Initialize selection background
		setSelectionBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
	}
	
	/**
	 * Sets the items.
	 * 
	 * @param items Items
	 */
	public void setItems(String[] items) {
		this.items = items;
		itemSizes = null;
		defaultSize = null;
		if (!isDisposed()) {
			redraw();
		}
	}
	
	/**
	 * Returns the items.
	 * 
	 * @return Items
	 */
	public String[] getItems() {
		return items;
	}
	
	/**
	 * Sets the selection foreground color.
	 * 
	 * @param selectionForeground Foreground color
	 */
	public void setSelectionForeground(Color selectionForeground) {
		this.selectionForeground = selectionForeground;
		if (!isDisposed()) {
			redraw();
		}
	}
	
	/**
	 * Returns the selection foreground color.
	 * 
	 * @return Foreground color
	 */
	public Color getSelectionForeground() {
		return selectionForeground;
	}
	
	/**
	 * Sets the selection background color.
	 * 
	 * @param selectionBackground Background color
	 */
	public void setSelectionBackground(Color selectionBackground) {
		this.selectionBackground = selectionBackground;
		if (!isDisposed()) {
			redraw();
		}
	}
	
	/**
	 * Returns the selection background color.
	 * 
	 * @return Background color
	 */
	public Color getSelectionBackground() {
		return selectionBackground;
	}

	/**
	 * Sets the selected item.
	 * 
	 * @param rating Index of item to select or <code>-1</code>
	 * to clear the selection.
	 * @see #getSelection()
	 */
	public void setSelection(int item) {
		if (selectedItem != item) {
			selectedItem = item;
			if (!isDisposed()) {
				redraw();
			}
			fireSelectionChanged(selectedItem);
		}
	}
	
	/**
	 * Returns the index of the selected
	 * item.
	 * 
	 * @return Index or <code>-1</code>
	 * @see #setSelection(int)
	 */
	public int getSelection() {
		return selectedItem;
	}

	/**
	 * Adds a new selection listener.
	 * If the listener has already been added,
	 * this method does nothing.
	 * 
	 * @param listener Listener to add
	 * @see #removeSelectionListener(SelectionListener)
	 */
	public void addSelectionListener(SelectionListener listener) {
		if (selectionListeners == null) {
			selectionListeners = new ListenerList();
		}
		selectionListeners.add(listener);
	}
	
	/**
	 * Removes a selection listener.
	 * 
	 * @param listener Listener to remove
	 * @see #addSelectionListener(SelectionListener)
	 */
	public void removeSelectionListener(SelectionListener listener) {
		if (selectionListeners != null) {
			selectionListeners.remove(listener);
		}
	}
	
	/**
	 * Fires a selection event.
	 * 
	 * @param index Selected index
	 */
	protected void fireSelectionChanged(int index) {
		if (selectionListeners != null) {
			Event event = new Event();
			event.widget = this;
			event.index = getSelection();
			SelectionEvent selectionEvent = new SelectionEvent(event);
			Object[] listeners = selectionListeners.getListeners();
			for (Object listener : listeners) {
				((SelectionListener)listener).widgetSelected(selectionEvent);
			}
		}
	}
	
	/**
	 * Sets the alignment of the items in the control.
	 * <ul>
	 * <li>SWT.LEFT - Align on the left</li>
	 * <li>SWT.CENTER - Align in the center</li>
	 * <li>SWT.RIGHT - Align on the right</li>
	 * </ul>
	 * @param alignment Alignment
	 * @see #getAlignment()
	 */
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	/**
	 * Returns the alignment of the items in the control.
	 * 
	 * @return SWT.LEFT, SWT.CENTER, or SWT.RIGHT
	 * @see #setAlignment(int)
	 */
	public int getAlignment() {
		return alignment;
	}
	
	/**
	 * Sets the margin around items text.
	 * 
	 * @param margin Margin
	 */
	public void setItemMargin(Point margin) {
		this.margin = margin;
	}
	
	/**
	 * Sets the spacing between items.
	 * 
	 * @param itemSpacing Items spacing
	 */
	public void setItemSpacing(int itemSpacing) {
		this.itemSpacing = itemSpacing;
	}
	
	/**
	 * Returns the index of the item for a point.
	 * 
	 * @param x Horizontal coordinate
	 * @param y Vertical coordinate
	 * @return Index of item that corresponds to the point
	 * or <code>-1</code>
	 */
	private int getItemForPoint(int x, int y) {
		int item = -1;
		
		if (initialOffset != -1) {
			x -= initialOffset;
			int offset = 0;
			Point[] sizes = getItemSizes();
			for (int index = 0; index < sizes.length; index ++) {
				if ((x >= offset) && (x <= offset + sizes[index].x)) {
					item = index;
					break;
				}
				offset += sizes[index].x;
			}
		}
		
		return item;
	}

	/**
	 * Returns the sizes for all items.
	 * 
	 * @return Item sizes
	 */
	private Point[] getItemSizes() {
		if (itemSizes == null) {
			GC gc = new GC(this);
			String[] items = getItems();
			itemSizes = new Point[items.length];
			for (int index = 0; index < itemSizes.length; index ++) {
				Point size = gc.textExtent(items[index]);
				itemSizes[index] = new Point(size.x + margin.x + margin.x, size.y + margin.y + margin.y);
			}
			gc.dispose();
		}
		
		return itemSizes;
	}
	
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return computeSize(wHint, hHint);
	}

	@Override
	public Point computeSize(int wHint, int hHint) {
		if (defaultSize == null) {
			Point[] sizes = getItemSizes();
			int width = 0;
			int height = 0;
			for (Point size : sizes) {
				width += size.x;
				if (size.y > height) {
					height = size.y;
				}
			}
			
			defaultSize = new Point(width, height + 2);
		}
		
		return defaultSize;
	}
	
	/**
	 * Called to paint the control.
	 * 
	 * @param event Paint event
	 */
	private void onPaint(PaintEvent event) {
		Rectangle clientArea = getClientArea();
		// Note: Since the painting is not extensive,
		// double buffering is not used for this control.
		GC gc = event.gc;

		// Set font
		gc.setFont(getFont());
		
		// Paint the background
		gc.setBackground(getBackground());
		gc.fillRectangle(clientArea);
		
		String[] items = getItems();
		if ((items != null) && (items.length > 0)) {
			Point[] sizes = getItemSizes();

			// Center offset
			if (getAlignment() == SWT.CENTER) {
				int extent = 0;
				for (Point size : sizes) {
					extent += size.x + itemSpacing;
				}
				initialOffset = clientArea.width / 2 - extent / 2;
			}
			// Right offset
			else if (getAlignment() == SWT.RIGHT) {
				int extent = 0;
				for (Point size : sizes) {
					extent += size.x + itemSpacing;
				}
				initialOffset = clientArea.x + clientArea.width - extent;
			}
			// Left offset
			else {
				initialOffset = clientArea.x;
			}
			if (initialOffset < 0)
				initialOffset = 0;

			int offset = initialOffset;
			// Loop through items
			for (int index = 0; index < items.length; index++) {
				String text = items[index];

				// Draw focus rectangle
				/*
				if (index == focusItem) {
					gc.setLineStyle(SWT.LINE_DOT);
					gc.setForeground(getSelectionBackground());
					gc.drawRoundRectangle(offset, 0, sizes[index].x, sizes[index].y, 10, 10);
				}
				gc.setLineStyle(SWT.LINE_SOLID);
				*/
				
				// Draw selected item
				if (index == selectedItem) {
					gc.setBackground(getSelectionBackground());
					gc.setForeground(getSelectionForeground());
					gc.fillRoundRectangle(offset, 0, sizes[index].x, sizes[index].y, 10, 10);
				}
				// Draw tracked item
				else if (index == trackedItem) {
					gc.setForeground(getSelectionBackground());
					gc.drawRoundRectangle(offset, 0, sizes[index].x, sizes[index].y, 10, 10);
				}
				else {
					gc.setForeground(getForeground());
				}
				// Draw item text
				gc.drawText(text, offset + DEFAULT_ITEM_HORIZ_MARGIN, DEFAULT_ITEM_VERT_MARGIN, true);
				
				// Adjust to next item
				offset += sizes[index].x + itemSpacing;
			}
		}
	}
}
