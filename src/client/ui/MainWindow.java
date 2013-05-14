package client.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;


/**
 * // JottoGUI class is a UI for playing Jotto. 
 * // Remember to name all your components, otherwise autograder will give a zero.
 * // Remember to use the objects newPuzzleButton, newPuzzleNumber, puzzleNumber,
 * // guess, and guessTable in your GUI!
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	public MainWindow() {
		
		String ip;
		ip = JOptionPane.showInputDialog(null, "IP to connect to:");
		String username;
		username = JOptionPane.showInputDialog(null, "Choose a username:");		
		
		String[] buddies = {"Friend1", "Friend2", "Friend3", "Friend4", "Friend5", "Friend6"};
		JList buddyList = new JList(buddies);
		buddyList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		buddyList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		buddyList.setPrototypeCellValue("Index 1234567890");
		buddyList.setVisibleRowCount(-1);
		JScrollPane buddyScroll = new JScrollPane(buddyList);
		
		JLabel hello = new JLabel();
		hello.setText("Hello, "+username + "! Here are your friends online. Click on a friend to chat:");
		
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
				layout.createParallelGroup()
				
				.addComponent(hello)
				.addComponent(buddyScroll)
				
				);

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				
				.addComponent(hello)
				.addComponent(buddyScroll)
				
				);
	} 

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {				
				
				MainWindow main = new MainWindow();
				main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);           
				main.pack();
				main.setVisible(true);
				
				
				ChatWindow chat = new ChatWindow();
				chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);           
				chat.pack();
				chat.setVisible(true);
			}
		});
	}
}
