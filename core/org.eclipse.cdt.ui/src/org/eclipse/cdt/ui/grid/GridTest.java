package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

public class GridTest {

	class BreadcrumbsGroupElement implements IGridElement {
		
		public BreadcrumbsGroupElement(ICompositePresentationModel model, IViewElementFactory f) {
			this.model = model;
			this.factory = f;
		}
		
		@Override
		public void fillIntoGrid(Composite parent) {
			
			// Uhm, this kinda sucks really.
			BreadcrumbsContainer container = new BreadcrumbsContainer(parent, SWT.NONE);
			
			
			// TODO Auto-generated method stub
			
		}
		
		ICompositePresentationModel model;
		IViewElementFactory factory;
	};
	
	
	@Test
	public void test() {
		
		Display display = PlatformUI.getWorkbench().getDisplay();
		
		Shell shell = new Shell(display);
		
		GridLayout layout = new GridLayout(5, false);
		layout.marginWidth = layout.marginHeight = 12;
		shell.setSize(600, 800);
		shell.setLayout(layout);
		
		
		
		CompositePresentationModel target = new CompositePresentationModel("Overview");
		
		
		CompositePresentationModel basics = new CompositePresentationModel("Basics");
		
		StringPresentationModel name = new StringPresentationModel("Name");
		StringPresentationModel description = new StringPresentationModel("Description");
		
		CompositePresentationModel connections = new CompositePresentationModel("Connections");
		
		StringPresentationModel ip = new StringPresentationModel("IP");
		StringPresentationModel serial = new StringPresentationModel("Serial");
		connections.add(ip);
		connections.add(serial);
		
		
		basics.add(name);
		basics.add(description);
		
		target.add(basics);
		target.add(connections);
		
		DefaultViewElementFactory factory = new DefaultViewElementFactory() {
		
			@Override
			public IGridElement createElement(IPresentationModel model) {
				if (model instanceof CompositePresentationModel) {
					return new HeaderGroupElement((CompositePresentationModel)model, this);
				}
				return super.createElement(model);
			}
		};
		
		/*
		x = new DelegatingViewElementFactory(factory) {
			@Override
			public IViewElement createCustom(IPresentationModel model) {
				if (model instanceof CompositePresentationModel) {
					return new BreadcrumbsGroupElement((CompositePresentationModel)model, this);
				}
				return null;
			}
		};*/
		
		H1Element h1 = new H1Element("New Sourcery CodeBench Project");
		h1.fillIntoGrid(shell);
		
		PillElement pill = new PillElement(new String[]{"Create", "Import", "Checkout"});
		pill.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		pill.fillIntoGrid(shell);
		
		H1Element targeth1 = new H1Element("What is your target?");
		targeth1.fillIntoGrid(shell	);
		
		PillElement pill2 = new PillElement(new String[]{"ARM EABI", "ARM Linux"});
		pill2.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		pill2.fillIntoGrid(shell);
		
		
		H1Element templateh1 = new H1Element("What project do you want to create?");
		templateh1.fillIntoGrid(shell);
		
		H2Element executableh2 = new H2Element("Executable");
		executableh2.fillIntoGrid(shell);
		
		LinksElement executables = new LinksElement(new String[]{"Threads", "Factorial", "25 more..."});
		//executables.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		executables.fillIntoGrid(shell);
		
		H2Element kernelh2 = new H2Element("Kernel Development");
		kernelh2.fillIntoGrid(shell);
		
		LinksElement kernel = new LinksElement(new String[]{"Network", "USB"});
		//kernel.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		kernel.fillIntoGrid(shell);		
		
		
		
		
		
		/*
		IGridElement element = factory.createElement(target);
		element.fillIntoGrid(shell);
		*/
		
        shell.open();
        while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) display.sleep();
        }
	}

}
