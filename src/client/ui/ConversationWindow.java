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
 * See Conversation Design section 5d for high-level documentation.
 */
@SuppressWarnings("serial")
public class ConversationWindow extends JFrame implements ConversationListener {
    
    private Controller c;
    private JList communicantsJList;
    private List<String> communicants;
    private JTextField textMessageInput;
    private JTextArea chat;

	public ConversationWindow(final long id) { 
		
	    communicants = new ArrayList<String>();
		
		communicantsJList = new JList(communicants.toArray());
		communicantsJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		communicantsJList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		communicantsJList.setVisibleRowCount(-1);
		JScrollPane buddyScroll = new JScrollPane(communicantsJList);


		JLabel addUserLabel = new JLabel();
		addUserLabel.setText("Add users (comma-delimited list):");
		final JTextField addUsers = new JTextField();
		addUsers.addActionListener(
				new ActionListener(){
				    // if the text field is not empty,
				    // clear it and send the list of 
				    // added users to the controller so that it 
				    // can forward it to the server
					public void actionPerformed(ActionEvent e){
						String text = addUsers.getText().trim();
	                    if (text.isEmpty())
	                        return;
						addUsers.setText("");
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
		
		textMessageInput = new JTextField();
		textMessageInput.addActionListener(
			new ActionListener(){
			    // if the text field is not empty, clear it
			    // and send the typed message to the controller
			    // so that it can forward it to the server
				public void actionPerformed(ActionEvent e){
					String text = textMessageInput.getText().trim();
					if (text.isEmpty())
					    return;
					textMessageInput.setText("");
					c.sendMessage(id, text);
				}
			}
		);
		
		JPanel mainChatPanel = new JPanel();
		chat = new JTextArea(20, 40);
		JScrollPane display = new JScrollPane(chat);
		chat.setEditable(false);
		mainChatPanel.add(display);
		mainChatPanel.add(textMessageInput);


		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
				layout.createSequentialGroup()

				.addGroup(layout.createParallelGroup()
						.addComponent(mainChatPanel)
						
						.addGroup(layout.createSequentialGroup()
								.addComponent(addUserLabel)
								.addComponent(addUsers))
						
						.addGroup(layout.createSequentialGroup()
								.addComponent(send)
								.addComponent(textMessageInput)
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
								.addComponent(addUsers)
							)
						
						.addComponent(mainChatPanel)
						.addGroup(layout.createParallelGroup()
								.addComponent(send)
								.addComponent(textMessageInput)
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
					public void windowActivated(WindowEvent arg0) {}
					@Override
					public void windowClosing(WindowEvent arg0) {}
					@Override
					public void windowDeactivated(WindowEvent arg0) {}
					@Override
					public void windowDeiconified(WindowEvent arg0) {}
					@Override
					public void windowIconified(WindowEvent arg0) {}
					@Override
					public void windowOpened(WindowEvent arg0) {}
				}
				);
		pack();
		setVisible(true);
	}

    @Override
    public void setController(Controller c) {
        this.c = c;
    }
    
    /**
     * Add the given message to the GUI.
     */
    @Override
    public void addMessage(final String senderHandle, final String message) {
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	chat.append(senderHandle+": "+message+"\n");
            }
        });
        
    }
    
    /**
     * Remove the given user from the user list in the view.
     */
    @Override
    public void removeUser(final String handle) {
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chat.append(handle + " left the room.\n");
            	communicants.remove(handle);
            	communicantsJList.setListData(communicants.toArray());
            }
    	});
        
    }
    
    /**
     * Add the given users to the user list in the view.
     */
    @Override
    public void addUsers(final List<String> handles) {
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
		        for(String s : handles){
		        	if (!communicants.contains(s)) {
		        	    communicants.add(s);
		        	    chat.append(s + " entered the room.\n");
		        	}
		        }
		        communicantsJList.setListData(communicants.toArray());
            }
    	});
        
        
    } 

}
