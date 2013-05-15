package client.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import client.Controller;
import client.ConversationListener;

/**
 * // JottoGUI class is a UI for playing Jotto. 
 * // Remember to name all your components, otherwise autograder will give a zero.
 * // Remember to use the objects newPuzzleButton, newPuzzleNumber, puzzleNumber,
 * // guess, and guessTable in your GUI!
 */
@SuppressWarnings("serial")
public class ChatWindow extends JFrame implements ConversationListener {
    
    private Controller c;
    private JList inChatList;
    private List<String> people;
    private JTextField input;
    private long id;
    private JTextArea chat;

	public ChatWindow(long id) {
	    this.id = id; 
		
	    people = new ArrayList<String>();
		/*people.add("Mike");
		people.add("George");
		people.add("Hannah");
		*/
		
		inChatList = new JList(people.toArray());
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
		chat = new JTextArea(20, 40);
		JScrollPane display = new JScrollPane(chat);
		chat.setEditable(false);
		
		
		input = new JTextField();
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

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

    @Override
    public void setController(Controller c) {
        this.c = c;
    }

    @Override
    public void addMessage(String senderHandle, String message) {
    	chat.append(senderHandle+": "+message+"\n");
        
    }

    @Override
    public void removeUser(String handle) {
        people.remove(handle);
        inChatList.setListData(people.toArray());
        
    }

    @Override
    public void addUsers(List<String> handles) {
    	
        for(String s:handles){
        	if(!people.contains(s))
        	{
        	people.add(s);
        	}
        }
        
        inChatList.setListData(people.toArray());
        
        
    } 

}
