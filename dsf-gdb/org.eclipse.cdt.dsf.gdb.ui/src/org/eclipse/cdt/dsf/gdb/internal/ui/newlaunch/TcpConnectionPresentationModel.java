package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.dsf.gdb.newlaunch.TCPConnectionElement;
import org.eclipse.cdt.ui.grid.CompositePresentationModel;
import org.eclipse.cdt.ui.grid.StringPresentationModel;

public class TcpConnectionPresentationModel extends CompositePresentationModel {
	
	public TcpConnectionPresentationModel(final TCPConnectionElement launchElement) {
		super();
		
		StringPresentationModel host = new StringPresentationModel("Host") {
			
			@Override
			protected String doGetValue() {
				return launchElement.getHostName();
			}
			
			@Override
			protected void doSetValue(String value) {
				launchElement.setHostName(value.trim());
			}
		};
		add(host);
		
		StringPresentationModel port = new StringPresentationModel("Port") {
			
			@Override
			protected String doGetValue() {
				return launchElement.getPortNumber();
			};
			
			@Override
			protected void doSetValue(String value) {
				launchElement.setPortNumber(value.trim());
			};
		};
		add(port);
	}

}
