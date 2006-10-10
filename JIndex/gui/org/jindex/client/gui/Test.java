/*
 * Created on Feb 9, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.jindex.client.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * @author sorenm
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Test extends JPanel {

	/**
	 * This is the default constructor
	 */
	public Test() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		this.setSize(300,200);
		this.setLayout(new GridLayout(1,2));
		
	}
 }
