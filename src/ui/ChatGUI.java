package ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * // JottoGUI class is a UI for playing Jotto. 
 * // Remember to name all your components, otherwise autograder will give a zero.
 * // Remember to use the objects newPuzzleButton, newPuzzleNumber, puzzleNumber,
 * // guess, and guessTable in your GUI!
 */
@SuppressWarnings("serial")
public class ChatGUI extends JFrame {
	
	private final List<JFrame> chatWindows;
    private final JFrame testchatwindow;
	
    private final JList buddyList;
    

    public ChatGUI() {
    	chatWindows = new ArrayList<JFrame>();    	
    	buddyList = new JList();
    	
    	String ip;
    	ip = JOptionPane.showInputDialog(null, "IP to connect to:");
    	String username;
    	username = JOptionPane.showInputDialog(null, "Choose a username:");
    	
    	testchatwindow = new JFrame();
    	JTextArea display = new JTextArea();
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
        				.addComponent(testchatwindow, 50, 50, 50))
        		.addComponent(buddyList)
        		);
        
        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(testchatwindow, 50, 50, 50)
        				.addComponent(buddyList, 100, 100, 100))
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
