package org.eclipse.cdt.ui.grid;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.cdt.ui.dialogs.PillsControl;

/**
 * @since 5.7
 */
public class GridTest {

	class BreadcrumbsGroupElement extends GridElement {
		
		public BreadcrumbsGroupElement(ICompositePresentationModel model, IViewElementFactory f) {
			this.model = model;
			this.factory = f;
		}
		
		@Override
		public void createImmediateContent(Composite parent) {
			
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
		shell.setText("Foo");
		GridLayout layout = new GridLayout(5, false);
		layout.marginWidth = layout.marginHeight = 12;
		shell.setSize(600, 800);
		shell.setLayout(layout);
		
		/*
		
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
		
		*/
		
		DefaultViewElementFactory factory = new DefaultViewElementFactory() {
		
			@Override
			public GridElement createElement(ISomePresentationModel model) {
				if (model instanceof CompositePresentationModel) {
					//return new HeaderGroupElement((CompositePresentationModel)model, this);
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
		
		buildUI(display, shell);
		
		
		
		
		/*
		IGridElement element = factory.createElement(target);
		element.fillIntoGrid(shell);
		*/
		
        shell.open();
        while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) display.sleep();
        }
	}


	private void buildUI(final Display display, final Shell shell) {
		
		for (Control c: shell.getChildren()) {
			c.dispose();
		}
		
		
		{
			/*
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
		*/
		Label howh2 = new Label(shell, SWT.NONE);
		howh2.setText("New Project");
		howh2.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.HEADER_FONT));
		
		Label howSpacer = new Label(shell, SWT.NONE);
		
		PillsControl pill4 = new PillsControl(shell, SWT.NONE);
		pill4.setItems(new String[]{"Create", "Import", "Checkout"});
		pill4.setSelection(0);
		pill4.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		CDTUITools.getGridLayoutData(pill4).horizontalSpan = 2;
		CDTUITools.grabAllWidth(pill4);
		Label howSpacer2 = new Label(shell, SWT.NONE);
		
		{
		Label separator = new Label(shell, SWT.SEPARATOR|SWT.HORIZONTAL|SWT.BEGINNING);
		CDTUITools.getGridLayoutData(separator).horizontalSpan = 5;
		CDTUITools.grabAllWidth(separator);
		
		/*
		Label separatorSpacer = new Label(shell, SWT.NONE);
		CDTUITools.grabAllWidth(separatorSpacer);
		CDTUITools.getGridLayoutData(separatorSpacer).horizontalSpan = 5;
		CDTUITools.getGridLayoutData(separatorSpacer).heightHint = 24; */
		}
		
		H2Element targeth1 = new H2Element("What is your target?");
		targeth1.create(shell	);
		
		
		Label spacer = new Label(shell, SWT.NONE);
		CDTUITools.getGridLayoutData(spacer).horizontalSpan = GridElement.DEFAULT_WIDTH;
		CDTUITools.getGridLayoutData(spacer).heightHint = 12/2;	
		
		/*
		Label target2 = new Label(shell, SWT.NONE);
		target2.setText("Target");
		target2.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		
		Label targetSpacer = new Label(shell, SWT.NONE);
		
		PillsControl pill3 = new PillsControl(shell, SWT.NONE);
		pill3.setItems(new String[]{"ARM EABI", "ARM Linux"});
		pill3.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		pill3.setSelection(0);
		
		CDTUITools.grabAllWidth(pill3);
		
		CDTUITools.getGridLayoutData(pill3).horizontalSpan = 2;
		Label targetSpacer2 = new Label(shell, SWT.NONE);
		
		*/
		
		PillSelectionViewElement pillElement = new PillSelectionViewElement(
				new SelectionPresentationModel("Target", Arrays.asList("ARM EABI", "ARM Linux"))
		);
		pillElement.create(shell);
		
		PillSelectionViewElement pillElement2 = new PillSelectionViewElement(
				new SelectionPresentationModel("Language", Arrays.asList("C", "C++"))
		);
		pillElement2.create(shell);		
		
		
		{
			Label separatorSpacer = new Label(shell, SWT.NONE);
			CDTUITools.grabAllWidth(separatorSpacer);
			CDTUITools.getGridLayoutData(separatorSpacer).horizontalSpan = 5;
			CDTUITools.getGridLayoutData(separatorSpacer).heightHint = 24;
			
			
		}
		
		H2Element templateh1 = new H2Element("What project do you want to create?");
		templateh1.create(shell);
		
		{
		Label spacer2 = new Label(shell, SWT.NONE);
		CDTUITools.getGridLayoutData(spacer2).horizontalSpan = GridElement.DEFAULT_WIDTH;
		CDTUITools.getGridLayoutData(spacer2).heightHint = 12/2;	
		}
		{
			
			
			
			//Label separator = new Label(shell, SWT.SEPARATOR|SWT.HORIZONTAL|SWT.BEGINNING);
			//CDTUITools.getGridLayoutData(separator).horizontalSpan = 5;
			//CDTUITools.grabAllWidth(separator);
		
			

			}
		
		
		//H2Element executableh2 = new H2Element("Executable");
		//executableh2.fillIntoGrid(shell);
		
		/*
		Label executableh2 = new Label(shell, SWT.NONE);
		executableh2.setText("Executable");
		executableh2.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		
		Label executableSpacer = new Label(shell, SWT.NONE);
		*/
		
		
		
		
		//LinksElement executables = new LinksElement(new String[]{"Threads", "Factorial", "25 more..."});
		//executables.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		//executables.fillIntoGrid(shell);
		
		LinksSelectionViewElement executableLinks = new LinksSelectionViewElement(new SelectionPresentationModel("Executable", 
				Arrays.asList("Threads", "Factorial", "25 more...")));
		executableLinks.create(shell);
		

		//H2Element kernelh2 = new H2Element("Kernel Development");
		//kernelh2.fillIntoGrid(shell);
		
		//LinksElement kernel = new LinksElement(new String[]{"Network", "USB"});
		
	//	kernel.fillIntoGrid(shell);
		
		{
		Label spacer2 = new Label(shell, SWT.NONE);
		CDTUITools.getGridLayoutData(spacer2).horizontalSpan = GridElement.DEFAULT_WIDTH;
		CDTUITools.getGridLayoutData(spacer2).heightHint = 12/2;	
		}
		
		LinksSelectionViewElement kernelLinks = new LinksSelectionViewElement(new SelectionPresentationModel("Kernel Development", 
				Arrays.asList("Network", "USB")));
		kernelLinks.create(shell);

		
		H2Element bs2 = new H2Element("Build System");
		bs2.create(shell);
		
		{
		Label msb_label = new Label(shell, SWT.NONE);
		msb_label.setText("Managed Build");
		
		Label mbs_spacer = new Label(shell, SWT.NONE);
		
		Button mbs_radio = new Button(shell, SWT.RADIO);
		mbs_radio.setText("Managed Build is cool stuff");
		CDTUITools.getGridLayoutData(mbs_radio).horizontalSpan = 2;
		Label mbs_spacer2 = new Label(shell, SWT.NONE);
		}
		
		{
		Label msb_label = new Label(shell, SWT.NONE);
		msb_label.setText("Automake");
		
		Label mbs_spacer = new Label(shell, SWT.NONE);
		
		Button mbs_radio = new Button(shell, SWT.RADIO);
		mbs_radio.setText("Automake is not so cool");
		CDTUITools.getGridLayoutData(mbs_radio).horizontalSpan = 2;
		Label mbs_spacer2 = new Label(shell, SWT.NONE);
		}		
		
		
		
		
		
		
		
		H2Element nameh2 = new H2Element("Name and Location");
		nameh2.create(shell);
		StringViewElement projectName = new StringViewElement(new StringPresentationModel("Name") {
			
		});
		projectName.create(shell);;

		StringViewElement location = new StringViewElement(new StringPresentationModel("Location") {
			
		});
		location.create(shell);;

		
		}
		
		Button b = new Button(shell, SWT.NONE);
		b.setText("Rebuild");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buildUI(display, shell);
				shell.layout();
			}
		});
	}

}
