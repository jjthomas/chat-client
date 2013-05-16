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

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements MainListener {
    
    public static final String HOSTNAME_INTRO = " (include port number after colon at end)";
    
    private Controller c;
    private JLabel hello;
    private static final String HANDLE_TAKEN = " (previous handle either had invalid chars or was already taken)";
    private static final String HOSTNAME_BAD = " (previous IP/hostname+port was unreachable)";
    private static final String WELCOME_TEXT = "Hello, *! Here are your " + 
            "friends online. Click on a friend to chat.";
    private List<String> onlinebuddies;
    private JList buddyList;
    private Queue<String> waitingConversations = new LinkedBlockingQueue<String>();
    private ExecutorService e = Executors.newFixedThreadPool(1); /* to create sockets */

	public MainWindow() {
		
		onlinebuddies = new ArrayList<String>();
		
		buddyList = new JList(onlinebuddies.toArray());
		buddyList.addListSelectionListener( 
				new ListSelectionListener(){
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
	
	public void start(String addendum) throws IOException {
        String hostname = JOptionPane.showInputDialog(
                "Hostname/IP of server" + addendum + ": ");
        if (hostname == null) {
            start(HOSTNAME_INTRO);
            return;
        }
        
        final String[] components = hostname.split(":");
        final int port = components.length > 1 ? Integer.parseInt(components[1]) : 5000;
        Future<Socket> f = e.submit(new Callable<Socket>() {
            public Socket call() {
                Socket s = null;
                try {
                    s = new Socket(components[0], port);
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
	
	public void promptHandle(String addendum) {
        String handle = JOptionPane.showInputDialog(
                "Enter desired handle" + addendum + ": ");
        if (handle == null) {
            promptHandle("");
            return;
        }
        c.registerHandle(handle);
	}

    @Override
    public ConversationListener makeConversationListener(long id) {
        ConversationListener cl = new ChatWindow(id);
        cl.setController(c);
        return cl;
    }

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

    @Override
    public void badHandle(String handle) {
        promptHandle(HANDLE_TAKEN);
    }

    @Override
    public void handleClaimed(final String handle) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                hello.setText(WELCOME_TEXT.replace("*", handle));
                setVisible(true);
                pack();
                c.getUsers();
            }
        });
    }

    @Override
    public void setController(Controller c) {
        this.c = c;
    }

    @Override
    public void newId(long id) {
        ConversationListener cl = new ChatWindow(id);
        cl.setController(c);
        c.addConversationListener(id, cl);
        c.addUsers(id, Arrays.asList(waitingConversations.remove()));
    }
}
