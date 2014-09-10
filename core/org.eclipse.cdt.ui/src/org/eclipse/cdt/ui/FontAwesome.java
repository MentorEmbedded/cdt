package org.eclipse.cdt.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;

/**
 * "Font Awesome gives you scalable vector icons that can instantly be customized: size, color, drop shadow, and anything that can be done with the power of CSS."
 * Ref: http://fontawesome.io/
 * 
 * This class contains FontAwesome related data like its URL specification and the unicodes for various font Awesome
 * icons. The list of the icons and their corresponding unicodes can be seen at
 * http://fortawesome.github.io/Font-Awesome/icons/
 * @since 6.0
 */
public class FontAwesome {

	/* The specification of the font awesome font file. */
	public static final String URL_SPECIFICATION = "platform:/plugin/org.eclipse.cdt.ui/fonts/fontawesome-webfont.ttf"; //$NON-NLS-1$
	/* The font name for FontAwesome */
	public static final String FONT_NAME = "FontAwesome"; //$NON-NLS-1$

	/******************************* Icon Unicodes ************************************/
	public static final String FA_TRASH_O = "\uf014"; //$NON-NLS-1$
	public static final String FA_ARROW_UP = "\uf176"; //$NON-NLS-1$
	public static final String FA_ARROW_DOWN = "\uf175"; //$NON-NLS-1$

	/**
	 * Set the font awesome to the control.
	 * 
	 * @param control
	 *            : The control to which the font has to be set.
	 * @return: the created and used Font, caller has to dispose it explicitly.
	 */
	public static Font setFontAwesomeToControl(Control control) {
		try {
			int fontSize = control.getFont().getFontData()[0].getHeight();
			Font awesomeFont = FontUtil.getFont(control.getShell(), FontAwesome.FONT_NAME, fontSize, SWT.NORMAL);
			control.setFont(awesomeFont);
			return awesomeFont;
		} catch (Exception e) {
			throw new RuntimeException("Failed to set given font: ", e);
		}
	}

}
