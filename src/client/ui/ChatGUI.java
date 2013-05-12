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
public class ChatGUI extends JFrame {

	public ChatGUI() {
		List<JPanel> chatWindows = new ArrayList<JPanel>();    
		
		String[] buddies = {"Mike", "George", "Hannah"}; 
		JList buddyList = new JList(buddies);
		buddyList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		buddyList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		buddyList.setVisibleRowCount(-1);
		JScrollPane buddyScroll = new JScrollPane(buddyList);

		String ip;
		ip = JOptionPane.showInputDialog(null, "IP to connect to:");
		String username;
		username = JOptionPane.showInputDialog(null, "Choose a username:");


		JLabel chatLabel = new JLabel();
		chatLabel.setText("In this chat: you");

		JLabel send = new JLabel();
		send.setText("Send:");

		JPanel testchatwindow = new JPanel();
		JTextArea chat = new JTextArea(20, 40);
		JScrollPane display = new JScrollPane(chat);
		chat.setEditable(false);
		JTextField input = new JTextField();
		//display:
		testchatwindow.add(display);
		//textfield:
		testchatwindow.add(input);


		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
				layout.createSequentialGroup()

				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(testchatwindow)
						.addComponent(chatLabel)
						.addGroup(layout.createSequentialGroup()
								.addComponent(send)
								.addComponent(input)
								)
						)

				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(buddyScroll)
						)
				);

		layout.setVerticalGroup(
				layout.createParallelGroup()
				
				.addGroup(layout.createSequentialGroup()
						.addComponent(buddyScroll)
						)
						
				.addGroup(layout.createSequentialGroup()
						.addComponent(chatLabel)
						.addComponent(testchatwindow)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(send)
								.addComponent(input)
								)
						)
								
				
		);



	} 

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ChatGUI main = new ChatGUI();
				main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);           
				main.pack();                
				main.setVisible(true);
			}
		});
	}
}
