package net.java.sip.communicator.gui;
import java.awt.*;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import net.java.sip.communicator.common.*;

//import samples.accessory.StringGridBagLayout;

public class BlockedList extends JDialog implements ListSelectionListener   {

	String userName = null;
	JTextField userNameTextField = null;
	private BlockManager blockman = null;

	private String CMD_CANCEL = "cmd.cancel" /*NOI18N*/;
	private String CMD_SUBMIT = "cmd.submit";
	private String CMD_UNBLOCK = "cmd.unblock";

	private JButton cancelButton = null;

	private JList list;
	private DefaultListModel listUsername;
	private JButton unblockButton = new JButton("Unblock");

	/**
	 * Creates new form BlockAction
	 */
	public BlockedList(Frame parent, boolean modal)
	{
		super(parent, modal);
		initComponents();
		pack();
		centerWindow();
	}

	private void centerWindow()
	{
		Rectangle screen = new Rectangle(
				Toolkit.getDefaultToolkit().getScreenSize());
		Point center = new Point(
				(int) screen.getCenterX(), (int) screen.getCenterY());
		Point newLocation = new Point(
				center.x - this.getWidth() / 2, center.y - this.getHeight() / 2);
		if (screen.contains(newLocation.x, newLocation.y,
				this.getWidth(), this.getHeight())) {
			this.setLocation(newLocation);
		}
	} // centerWindow()

	private void initComponents()
	{
		blockman = new BlockManager();
		String blockee = null;
		ResultSet result = blockman.showBlockedList(Global.currentUser);
		listUsername = new DefaultListModel();
		
		UnblockListener listener = new UnblockListener(unblockButton);
		unblockButton.setActionCommand(CMD_UNBLOCK);
		unblockButton.addActionListener(listener);
		unblockButton.setEnabled(false);
		
		try {
			if (result.isBeforeFirst()){
				while (result.next()){
					blockee = result.getString("BLOCKEE");
					listUsername.addElement(blockee);
				}
				unblockButton.setEnabled(true);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setTitle("Blocked List");
		//Create the list and put it in a scroll pane.
		list = new JList(listUsername);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.addListSelectionListener(this);
		list.setVisibleRowCount(8);
		JScrollPane listScrollPane = new JScrollPane(list);


		

		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		cancelButton.setActionCommand(CMD_CANCEL);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				dialogDone(event);
			}
		});


		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane,
				BoxLayout.LINE_AXIS));
		buttonPane.add(Box.createRigidArea(new Dimension(15, 0)));
		buttonPane.add(unblockButton);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		buttonPane.add(Box.createRigidArea(new Dimension(15, 0)));
		buttonPane.add(cancelButton);


		buttonPane.setPreferredSize(new Dimension(250,100));
		add(listScrollPane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.PAGE_END);
	}



	//This listener is shared by the text field and the hire button.
	class UnblockListener implements ActionListener {


		private JButton button;

		public UnblockListener(JButton button) {
			this.button = button;
		}

		//Required by ActionListener.
		public void actionPerformed(ActionEvent e) {
			//This method can be called only if
			//there's a valid selection
			//so go ahead and remove whatever's selected.
			int index = list.getSelectedIndex();
			String blockee = listUsername.getElementAt(index).toString();
			System.out.println("Removing "+blockee);
			JFrame frame = new JFrame();
			if (blockman.removeBlockFromDB(Global.currentUser,blockee)){
				listUsername.remove(index);
				int size = listUsername.getSize();
				JOptionPane.showMessageDialog(frame, "User "+blockee+" has been unblocked!","Blocking Status", JOptionPane.INFORMATION_MESSAGE);
				//dialogDone(e);
				if (size == 0) { //Nobody's left, disable firing.
					button.setEnabled(false);
					JOptionPane.showMessageDialog(frame, "Your blocking list is empty","Blocking Status", JOptionPane.INFORMATION_MESSAGE);
					dialogDone(e);

				} 
				else { //Select an index.
					if (index == listUsername.getSize()) {
						//removed item in last position
						index--;
					}

					list.setSelectedIndex(index);
					list.ensureIndexIsVisible(index);
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "An unexpected error has occured!Please try again","Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void dialogDone(Object actionCommand)
	{
		String cmd = null;
		if (actionCommand != null) {
			if (actionCommand instanceof ActionEvent) {
				cmd = ( (ActionEvent) actionCommand).getActionCommand();
			}
			else {
				cmd = actionCommand.toString();
			}
		}
		if (cmd == null) {
			// do nothing
		}
		else if (cmd.equals(CMD_CANCEL)) {
			userName = null;
		}
		else if (cmd.equals(CMD_SUBMIT)) {
			userName = userNameTextField.getText();

		}
		setVisible(false);
		dispose();
	} // dialogDone()

	/**
	 * This main() is provided for debugging purposes, to display a
	 * sample dialog.
	 */
	public void main(String args[])
	{
		JFrame frame = new JFrame()
		{
			public Dimension getPreferredSize()
			{
				return new Dimension(300, 400);
			}
		};
		frame.setTitle("Debugging frame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(false);

		JFrame dialog = new JFrame("Blocked List");
		dialog.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				System.exit(0);
			}

			public void windowClosed(WindowEvent event)
			{
				System.exit(0);
			}
		});
		dialog.pack();
		dialog.setVisible(true);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}
} // class LoginSplash


