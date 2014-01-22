package org.eclipse.cdt.ui.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

import org.eclipse.cdt.ui.CDTUITools;

public class BreadcrumbsContainer extends Composite {

	private Link navigator;
	private Composite content;
	private StackLayout contentLayout;

	private List<Composite> breadcrumbsParts = new ArrayList<Composite>();
	
	public BreadcrumbsContainer(Composite parent, int style) {
		super(parent, style);
		
		GridLayout topLayout = new GridLayout(1, false);
		topLayout.marginWidth = topLayout.marginHeight = 0;
		setLayout(topLayout);
		
		navigator = new Link(this, SWT.NONE);
		FontData fd = getFont().getFontData()[0];
		// FIXME: this +2 adds underlying to links, for some reason. 
		Font boldFont = new Font(getFont().getDevice(), fd.getName(), (int)fd.height + 2, SWT.BOLD);
		CDTUITools.grabAllWidth(navigator);
		
		navigator.setFont(boldFont);
		
		navigator.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				int index = Integer.parseInt(e.text);
				activatePart(breadcrumbsParts.get(index));
				
			}
		});
		
		CDTUITools.addHorizontalSeparatorToGrid(this);
		
		content = new Composite(this, SWT.NONE);
		CDTUITools.grabAllWidth(content);
		contentLayout = new StackLayout();
		contentLayout.marginHeight = contentLayout.marginWidth = 0;
		content.setLayout(contentLayout);
	}
	
	public Composite getContent()
	{
		return content;
	}
	
	public void addPart(Composite part, String title, Composite parent)
	{
		part.setParent(content);
				
		ComponentData d = new ComponentData(title, part, parent);
		components.put(part, d);		
	}
	
	public void activatePart(Composite part)
	{
		ComponentData current = components.get(part);
		assert current != null;
		
		String text = current.title;
		
		breadcrumbsParts.clear();
		while (current.parent != null)
		{
			current = components.get(current.parent);
			text = "<a href=\"" + breadcrumbsParts.size() + "\">" + current.title + "</a> / " + text;
			breadcrumbsParts.add(current.composite);
		}
		
		navigator.setText(text);
		
		contentLayout.topControl = part;
		content.layout();
	}
	
	private class ComponentData
	{
		public ComponentData(String title, Composite composite, Composite parent) 
		{
			this.title = title;
			this.composite = composite;
			this.parent = parent;
		}
		
		String title;
		Composite composite; 
		Composite parent;
	}
	
	private Map<Composite, ComponentData> components = new HashMap<Composite, ComponentData>();
}
