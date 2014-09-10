package org.eclipse.cdt.ui;

import java.io.File;
import java.net.URL;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class handle loading and creating of Font objects.
 * @since 6.0
 */
public class FontUtil {

	private static Exception exp = null;

	/**
	 * Loads Font file after identifying the current display automatically.
	 * 
	 * @throws Exception
	 */
	public static synchronized void loadFontFile(final String URLSpec) throws Exception {
		exp = null;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					IWorkbench workbench = PlatformUI.getWorkbench();
					Display display = workbench.getDisplay();
					URL fontURL = new URL(URLSpec);
					URL fileURL = org.eclipse.core.runtime.FileLocator.toFileURL(fontURL);
					String fontFilePath = new File(fileURL.getPath()).getAbsolutePath();
					display.loadFont(fontFilePath);
				} catch (Exception e) {
					exp = e;
				}
			}
		});
		if (exp != null) {
			throw new Exception(exp);
		}
	}

	/**
	 * This the font's symbolic name that will be used in the font registry.
	 * 
	 * @param name
	 *            : the name of the font.
	 * @param fontSize
	 *            : the size of the font to be used.
	 * @return: return the symbolic name.
	 */
	public static String makeSymbolicFontName(String name, int fontSize) {
		return name + "-" + fontSize;
	}

	/**
	 * <p>
	 * Try to get an existing font or create a new one. The fonts are managed automatically by
	 * {@link org.eclipse.jface.resource.FontRegistry FontRegistry}.
	 * </p>
	 * 
	 * <p>
	 * WARNING: The font returned by this function must not be disposed by the caller as it is managed automatically by
	 * {@link org.eclipse.jface.resource.FontRegistry FontRegistry}.
	 * </p>
	 * 
	 * <p>
	 * For details on various parameters see {@link org.eclipse.swt.graphics.Font#Font(Device, String, int, int) Font}.
	 * </p>
	 */
	public static Font getFont(Shell shell, String name, int fontSize, int style) {
		Font font = null;
		String symbolicFontName = makeSymbolicFontName(name, fontSize);

		if (JFaceResources.getFontRegistry().hasValueFor(symbolicFontName) == false) {
			font = new Font(shell.getDisplay(), name, fontSize, style);
			JFaceResources.getFontRegistry().put(symbolicFontName, font.getFontData());
		}

		switch (style) {

		case SWT.NORMAL:
			font = JFaceResources.getFontRegistry().get(symbolicFontName);
			break;
		case SWT.BOLD:
			font = JFaceResources.getFontRegistry().getBold(symbolicFontName);
			break;
		case SWT.ITALIC:
			font = JFaceResources.getFontRegistry().getItalic(symbolicFontName);
			break;
		}

		return font;
	}

}
