package org.eclipset.cdt.flatui;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

// Support for FontAwesome iconic font.
//
// To use, create a label, call FA.apply on it, and use appropriate unicode
// characters as the label text. See http://fontawesome.io for the list of
// icons and their unicode values.
public class FA {

	// Load FontAwesore from the font file. Will be called by FlatUIPlugin automatically.
	static void load() throws IOException
	{
		URL url = new URL("platform:/plugin/org.eclipse.cdt.flatui/fontawesome/fontawesome-webfont.ttf");
		url = FileLocator.toFileURL(url);
		String path = new File(url.getPath()).getAbsolutePath();

		IWorkbench wb = PlatformUI.getWorkbench();
		Display display = wb.getDisplay();
		display.loadFont(path);
	}

	// Make the specified control use FontAwesome as font.
	public static void apply(Control control)
	{
		int size = control.getFont().getFontData()[0].getHeight();
		control.setFont(FlatUI.getFont("FontAwesome", size));
	}

}
