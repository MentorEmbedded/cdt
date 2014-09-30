package org.eclipset.cdt.flatui;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;

public class FlatUI {

	// Convenience version of getFont, below.
	public static Font getFont(String name, int size)
	{
		return getFont(name, size, SWT.NORMAL);
	}

	// Obtain a font with specified name, size and style.
	// The font will be stored in a FontRegistry instance,
	// and reused for future calls. It shall not be disposed
	// by clients.
	// The method does not accept Display, since actual font
	// creation is handled by FontRegistry, which basically uses
	// Display.getCurrent().
	public static Font getFont(String name, int size, int style)
	{
		String key = name + "/" + size;

		if (!fonts.hasValueFor(key)) {
			FontData fd[] = {new FontData(name, size, SWT.NORMAL)};
			fonts.put(key, fd);
		}

		switch(style) {
		case SWT.NORMAL: return fonts.get(key);
		case SWT.BOLD: return fonts.getBold(key);
		case SWT.ITALIC: return fonts.getItalic(key);
		default: return fonts.get(key);
		}
	}

	// Get the font that is the same as 'c' already use, but bold.
	public static Font getBold(Control c)
	{
		FontData fd = c.getFont().getFontData()[0];
		return getFont(fd.name, fd.getHeight(), SWT.BOLD);
	}

	private static FontRegistry fonts = new FontRegistry();

}
