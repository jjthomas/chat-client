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
public class ChatWindow extends JFrame {

	public ChatWindow() {
		List<JPanel> chatWindows = new ArrayList<JPanel>();    
		
		String[] people = {"Mike", "George", "Hannah"}; 
		JList inChatList = new JList(people);
		inChatList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		inChatList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		inChatList.setVisibleRowCount(-1);
		JScrollPane buddyScroll = new JScrollPane(inChatList);


		JLabel addUserLabel = new JLabel();
		addUserLabel.setText("Add user:");
		
		JLabel inChatLabel = new JLabel();
		inChatLabel.setText("In this chat:");
		
		JTextField addName = new JTextField();

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

				.addGroup(layout.createParallelGroup()
						.addComponent(testchatwindow)
						
						.addGroup(layout.createSequentialGroup()
								.addComponent(addUserLabel)
								.addComponent(addName))
						
						.addGroup(layout.createSequentialGroup()
								.addComponent(send)
								.addComponent(input)
								)
						)

				.addGroup(layout.createParallelGroup()
						.addComponent(inChatLabel)
						.addComponent(buddyScroll)
						)
				);

		layout.setVerticalGroup(
				layout.createParallelGroup()
				
				.addGroup(layout.createSequentialGroup()
						.addComponent(inChatLabel)
						.addComponent(buddyScroll)
						)
						
				.addGroup(layout.createSequentialGroup()
						
						.addGroup(layout.createParallelGroup()
								.addComponent(addUserLabel)
								.addComponent(addName)
							)
						
						.addComponent(testchatwindow)
						.addGroup(layout.createParallelGroup()
								.addComponent(send)
								.addComponent(input)
								)
						)
								
				
		);



	} 

}