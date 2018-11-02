/*******************************************************************************
 * Copyright (c) 2018 oceancerc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * oceancerc - initial API and implementation
*******************************************************************************/
package net.openchrom.xxd.processor.supplier.dalhousie.ui.swt;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import net.openchrom.xxd.processor.supplier.dalhousie.ui.internal.provider.UdpCommandClientRx;

public class CommandConnectionUI
{
	private static final Logger logger = Logger.getLogger(CommandConnectionUI.class);
	
	
	@SuppressWarnings("unused")
	private Button goButton;
	
	private Text responseBox;
	
	@SuppressWarnings("unused")
	private ISupplierEditorSupport supplierEditorSupport = new SupplierEditorSupport(DataType.CSD);
	
	private UdpCommandConnectionObserver commandConnection;

	public CommandConnectionUI(Composite parent)
	{
		initialize(parent);
	}

	private void initialize(Composite parent)
	{
		parent.setLayout(new GridLayout(1, true));
		goButton = createGoButton(parent);
		responseBox = createResponseBox(parent);
		
		/* Start the UDP thread */
		try
		{
			commandConnection = new UdpCommandConnectionObserver(writeText);
			commandConnection.start();
		}
		catch(SocketException | UnknownHostException e)
		{
			logger.warn(e);
		}
	}
	
	private Button createGoButton(Composite parent)
	{
		/* create the button */
		Button button = createStandardButton("Go", parent);
		/* add the button listener */
		button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				commandConnection.sendGoCommand();
			}
		});

		return button;
	}
	
	
	
	private Button createStandardButton(String str, Composite parent)
	{
		Button b = new Button(parent, SWT.PUSH);
		
		b.setText(str);
		
		b.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return b;
	}
	
	

	private Text createResponseBox(Composite parent)
	{
		Text t = new Text(parent, SWT.MULTI | SWT.V_SCROLL);
		
		/* make it read only */
		t.setEditable(false);
		
		/* fill all available space */
		t.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return t;
	}
	
	UdpCommandClientRx.I writeText = (str) -> 
	{
		class OneShotTask implements Runnable {
	        String str;
	        OneShotTask(String s) { str = s; }
	        public void run() {
	        	updateResponseBox(str);
	        }
	    }
		
		Display.getDefault().syncExec(new OneShotTask(str));
		
	};
	
	private void updateResponseBox(String str)
	{
		str.replaceAll("\r\n", responseBox.getLineDelimiter());
		
		responseBox.append(str);
	}
	
}
