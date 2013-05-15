package client.ui;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import client.Controller;
import client.ConversationListener;
import client.MainListener;

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements MainListener {
    
    private Controller c;
    private JLabel hello;
    private static final String HANDLE_TAKEN = " (previous handle already taken)";
    private static final String WELCOME_TEXT = "Hello, *! Here are your " + 
            "friends online. Click on a friend to chat.";

	public MainWindow() {
		
		String[] buddies = {"Friend1", "Friend2", "Friend3", "Friend4", "Friend5", "Friend6"};
		JList buddyList = new JList(buddies);
		buddyList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		buddyList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		buddyList.setPrototypeCellValue("Index 1234567890");
		buddyList.setVisibleRowCount(-1);
		JScrollPane buddyScroll = new JScrollPane(buddyList);
		
		hello = new JLabel();
		
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}
	
	public void start() throws IOException {
        String hostname = JOptionPane.showInputDialog(
                "Hostname/IP of server (include port number after colon at end): ");
        String[] components = hostname.split(":");
        int port = 5000;
        if (components.length > 1) {
            port = Integer.parseInt(components[1]);
        }
        Socket s = new Socket(components[0], port);
        c.initialize(s, this);
        promptHandle("");
	}
	
	public void promptHandle(String addendum) {
        String handle = JOptionPane.showInputDialog(
                "Enter desired handle" + addendum + ": ");
        c.registerHandle(handle);
	}

    @Override
    public ConversationListener makeConversationListener(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addOnlineUsers(Collection<String> handles) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeOfflineUser(String handle) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void badHandle(String handle) {
        promptHandle(HANDLE_TAKEN);
    }

    @Override
    public void handleClaimed(final String handle) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
                hello.setText(WELCOME_TEXT.replace("*", handle));
            }
        });
    }

    @Override
    public void setController(Controller c) {
        this.c = c;
    }
}
