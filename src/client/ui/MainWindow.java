package client.ui;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.Controller;
import client.ConversationListener;
import client.MainListener;

/**
 * See Conversation Design section 5c for high-level documentation.
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame implements MainListener {
    
    public static final String HOSTNAME_INTRO = " (include port number after colon at end)";
    
    private Controller c;
    private JLabel hello;
    private static final String HANDLE_TAKEN = " (previous handle either had invalid chars or was already taken)";
    private static final String HOSTNAME_BAD = " (previous IP/hostname+port was unreachable or improperly formatted)";
    private static final String WELCOME_TEXT = "Hello, *! Here are your " + 
            "friends online. Click on a friend to chat.";
    private List<String> onlinebuddies;
    private JList buddyList;
    // when we try to start a conversation with a user, the user's handle is
    // stored in this queue and a request for a new conversation ID is 
    // immediately sent to the server. when the server returns a new conversation ID,
    // a handle is removed from this queue and a conversation is now begun with this
    // user.
    private Queue<String> waitingConversations = new LinkedBlockingQueue<String>();
    // we need this thread pool to aid in our attempts to open sockets with the
    // hosts the user supplies in the dialog box presented to them when the 
    // client is launched. we want a fairly large number of threads here
    // because the user may theoretically enter many bad hostnames and it
    // sometimes takes a while for the Socket constructor to throw an
    // IOException on a unreachable hostname (and we want these delays to
    // be transparent to the user, so we need to always have available threads
    // to try to create sockets with newly entered hostnames)
    private ExecutorService e = Executors.newFixedThreadPool(5);

	public MainWindow() {
		
		onlinebuddies = new ArrayList<String>();
		
		buddyList = new JList(onlinebuddies.toArray());
		buddyList.addListSelectionListener( 
				new ListSelectionListener(){
				    // kick off the work required to start a conversation
				    // with the selected user
					public void valueChanged(ListSelectionEvent e){
						String selectedItem = (String) buddyList.getSelectedValue();
						if (selectedItem == null)
						    return;
						waitingConversations.add(selectedItem);
						buddyList.clearSelection();
						c.getId();
					}
				}
				);
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
	}
	
	/**
	 * Prompts the user for the IP/hostname of the server and then
	 * their desired handle. Does fairly comprehensive error checking
	 * on the input.
	 * @param addendum text to be added onto the end of the IP/hostname
	 * dialog box's message
	 * @throws IOException if we successfully connect to the server
	 * but then there are IO errors afterwards
	 */
	public void start(String addendum) throws IOException {
        String hostname = JOptionPane.showInputDialog(
                "Hostname/IP of server" + addendum + ": ");
        if (hostname == null || hostname.trim().isEmpty()) {
            start(HOSTNAME_INTRO);
            return;
        }
        
        final String[] components = hostname.trim().split(":");
        if (components.length > 2) {
            start(HOSTNAME_BAD);
            return;
        }
        final int port; 
        try {
            port = components.length == 2 ? Integer.parseInt(components[1].trim()) : 5000;
        } catch (NumberFormatException nfe) {
            start(HOSTNAME_BAD);
            return;
        }
        Future<Socket> f = e.submit(new Callable<Socket>() {
            public Socket call() {
                Socket s = null;
                try {
                    s = new Socket(components[0].trim(), port);
                } catch (IOException ioe) {}
                return s;
            }
        });
        
        Socket s = null;
        try {
            s = f.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {}
        
        if (s == null) {
            start(HOSTNAME_BAD);
            return;
        }
        c.initialize(s, this);
        promptHandle("");
	}
	
	/**
	 * Prompts the user for their desired handle 
	 * and then sends it to the controller.
	 * @param addendum text to be added onto the end of the
	 * handle dialog box's message
	 */
	private void promptHandle(String addendum) {
        String handle = JOptionPane.showInputDialog(
                "Enter desired alphanumeric handle that starts with a letter" + addendum + ": ");
        if (handle == null) {
            promptHandle("");
            return;
        }
        c.registerHandle(handle.trim());
	}
	
	/**
	 * Create a new ConversationListener for the conversation
	 * with given ID.
	 */
    @Override
    public ConversationListener makeConversationListener(long id) {
        ConversationListener cl = new ConversationWindow(id);
        cl.setController(c);
        return cl;
    }
    
    /**
     * Add the given users to the online users list in the view.
     */
    @Override
    public void addOnlineUsers(final Collection<String> handles) {
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
		        for(String s: handles){
		        	if(!onlinebuddies.contains(s)){
		        		onlinebuddies.add(s);
		        	}
		        }
		        
		        buddyList.setListData(onlinebuddies.toArray());
		        pack();
            }
    	});
        
    }
    
    /**
     * Remove the given user from the online users list in the view.
     */
    @Override
    public void removeOfflineUser(final String handle) {
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
		        onlinebuddies.remove(handle);
		        buddyList.setListData(onlinebuddies.toArray());
		        pack();
            }
    	});
        
    }
    
    /**
     * Respond to a notification that the user-submitted handle
     * was invalid or already taken.
     */
    @Override
    public void badHandle(String handle) {
        promptHandle(HANDLE_TAKEN);
    }
    
    /**
     * Respond to a notification that the user-submitted handle
     * was successfully claimed.
     */
    @Override
    public void handleClaimed(final String handle) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                hello.setText(WELCOME_TEXT.replace("*", handle));
                setVisible(true);
                pack();
                c.getUsers(); // we want the controller
                // to ask the server for the list of online users
                // so we can display it here in the view
            }
        });
    }

    @Override
    public void setController(Controller c) {
        this.c = c;
    }
    
    /**
     * Respond to a notification that a new conversation ID
     * was received from the server.
     */
    @Override
    public void newId(long id) {
        ConversationListener cl = new ConversationWindow(id);
        cl.setController(c);
        c.addConversationListener(id, cl);
        c.addUsers(id, Arrays.asList(waitingConversations.remove()));
    }
}
