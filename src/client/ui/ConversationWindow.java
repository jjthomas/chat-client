package client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.SwingUtilities;

import client.Controller;
import client.ConversationListener;

/**
 * // JottoGUI class is a UI for playing Jotto. 
 * // Remember to name all your components, otherwise autograder will give a zero.
 * // Remember to use the objects newPuzzleButton, newPuzzleNumber, puzzleNumber,
 * // guess, and guessTable in your GUI!
 */
@SuppressWarnings("serial")
public class ConversationWindow extends JFrame implements ConversationListener {
    
    private Controller c;
    private JList inChatList;
    private List<String> people;
    private JTextField input;
    private JTextArea chat;

	public ConversationWindow(final long id) { 
		
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
		addUserLabel.setText("Add users (comma-delimited list):");
		final JTextField addName = new JTextField();
		addName.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						String text = addName.getText().trim();
	                    if (text.isEmpty())
	                        return;
						addName.setText("");
						List<String> users = new ArrayList<String>();
						for (String handle : text.split(",")) {
						    users.add(handle.trim());
						}
						c.addUsers(id, users);
					}
				}
			);
		
		JLabel inChatLabel = new JLabel();
		inChatLabel.setText("In this chat:");
				
		
		JLabel send = new JLabel();
		send.setText("Send:");
		
		input = new JTextField();
		input.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String text = input.getText().trim();
					if (text.isEmpty())
					    return;
					input.setText("");
					c.sendMessage(id, text);
				}
			}
		);
		
		JPanel testchatwindow = new JPanel();
		chat = new JTextArea(20, 40);
		JScrollPane display = new JScrollPane(chat);
		chat.setEditable(false);
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

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(
				new WindowListener(){
					public void windowClosed(WindowEvent e) {
				        c.exitConversation(id);
				    }

					@Override
					public void windowActivated(WindowEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowClosing(WindowEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowDeactivated(WindowEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowDeiconified(WindowEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowIconified(WindowEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowOpened(WindowEvent arg0) {
						// TODO Auto-generated method stub
						
					}
				}
				);
		pack();
		setVisible(true);
	}

    @Override
    public void setController(Controller c) {
        this.c = c;
    }

    @Override
    public void addMessage(final String senderHandle, final String message) {
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	chat.append(senderHandle+": "+message+"\n");
            }
        });
        
    }

    @Override
    public void removeUser(final String handle) {
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	people.remove(handle);
            	inChatList.setListData(people.toArray());
            }
    	});
        
    }

    @Override
    public void addUsers(final List<String> handles) {
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
		        for(String s:handles){
		        	if(!people.contains(s))
		        	{
		        	people.add(s);
		        	}
		        }
		        
		        inChatList.setListData(people.toArray());
            }
    	});
        
        
    } 

}