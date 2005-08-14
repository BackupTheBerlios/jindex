/*
 * Created on Jul 27, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import javax.swing.JPanel;

import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.ImageIcon;
public class MainContentsGUI extends JPanel {
	private String openAction;
	/**
	 * @return Returns the openAction.
	 */
	public String getOpenAction() {
		return openAction;
	}
	/**
	 * @param openAction The openAction to set.
	 */
	public void setOpenAction(String openAction) {
		this.openAction = openAction;
	}
	private JPanel imagepane = null;
	private JPanel jPanel1 = null;
	private JPanel descriptionpane = null;
	private JTextArea freePane = null;
	private JPanel actionpane = null;
	private JButton openButton = null;
	/**
	 * This method initializes 
	 * 
	 */
	public MainContentsGUI() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        this.setLayout(new BorderLayout());
        this.setName("mainpane");
        this.setSize(400, 100);
        this.setMaximumSize(new Dimension(400,100));
        this.setPreferredSize(new Dimension(400,100));
        this.add(getImagepane(), java.awt.BorderLayout.WEST);
        this.add(getJPanel1(), java.awt.BorderLayout.CENTER);
			
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	public JPanel getImagepane() {
		if (imagepane == null) {
			imagepane = new JPanel();
			imagepane.setName("imagepanel");
			imagepane.setPreferredSize(new java.awt.Dimension(100,50));
			imagepane.setBackground(java.awt.Color.white);
		}
		return imagepane;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BorderLayout());
			jPanel1.setBackground(java.awt.Color.white);
			jPanel1.add(getDescriptionpane(), java.awt.BorderLayout.NORTH);
			jPanel1.add(getFreePane(), java.awt.BorderLayout.CENTER);
			jPanel1.add(getActionpane(), java.awt.BorderLayout.SOUTH);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	public JPanel getDescriptionpane() {
		if (descriptionpane == null) {
			descriptionpane = new JPanel();
			descriptionpane.setLayout(new BoxLayout(descriptionpane, BoxLayout.Y_AXIS));
			descriptionpane.setName("descriptionPane");
			descriptionpane.setPreferredSize(new java.awt.Dimension(100,25));
			descriptionpane.setBackground(java.awt.Color.white);
			descriptionpane.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
		}
		return descriptionpane;
	}
	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	protected JTextArea getFreePane() {
		if (freePane == null) {
			freePane = new JTextArea();
		}
		return freePane;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getActionpane() {
		if (actionpane == null) {
			actionpane = new JPanel();
			actionpane.add(getOpenButton(), null);
		}
		return actionpane;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getOpenButton() {
		if (openButton == null) {
			openButton = new JButton();
			openButton.setIcon(new ImageIcon("/usr/share/icons/hicolor/16x16/stock/io/stock_open.png"));
			openButton.setName("Open");
			openButton.setText("Open");
			openButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
				executeOpenAction();
				}
			});
		}
		return openButton;
	}
	/**
	 * 
	 */
	protected void executeOpenAction() {
		try {
			Runtime.getRuntime().exec("gnome-open "+getOpenAction());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
   }  //  @jve:decl-index=0:visual-constraint="218,59"
