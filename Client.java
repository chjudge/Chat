import java.io.*; 
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

  
public class Client{ 
    private final static int ServerPort = 1234;
	private final static String IPAddress = "18.217.206.33";//EC2 IPAddress
	protected static boolean isLoggedIn;
	protected static DataInputStream dis; 
    protected static DataOutputStream dos;
  
    public static void main(String args[]) throws UnknownHostException, IOException{
		// create shutdown hook with anonymous implementation
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Performing shutdown");
				sendMessage("logout");
			}
		});
		//Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
		Chat chat = new Chat();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Chat.createAndShowGUI(chat);
            }
        });
          
        // getting localhost ip 
        //InetAddress ip = InetAddress.getByName("localhost"); 
        InetAddress ip = InetAddress.getByName(IPAddress);
		
        // establish the connection 
        Socket s = new Socket(ip, ServerPort); 
          
        // obtaining input and out streams 
        dis = new DataInputStream(s.getInputStream()); 
        dos = new DataOutputStream(s.getOutputStream()); 
		
		//System.out.println(s.isConnected());
          
        // readMessage thread 
        Thread readMessage = new Thread(new Runnable(){ 
            @Override
            public void run() { 
                while (isLoggedIn) { 
                    try { 
                        // read the message sent to this client 
                        String msg = dis.readUTF();
						if(msg.equals("!&&closing**!")){
							chat.write("Goodbye!");
							System.exit(0);
						}
                        chat.write(msg);
                    } catch (IOException e) { 
                        e.printStackTrace(); 
                    } 
                } 
            } 
        });
		
		isLoggedIn = true;
		System.out.println("Connected to server");
		chat.write("Welcome to the chat");
		
        readMessage.start(); 
    }
	
	// write on the output stream 
	protected static void sendMessage(String msg){
		try { 
			dos.writeUTF(msg);
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
	}
}

class Chat extends JPanel implements ActionListener {
	protected JTextField textField;
    protected JTextArea textArea;
    private final static String newline = "\n";
	
	public Chat(){
		super(new GridBagLayout());
 
        textField = new JTextField(30);
        textField.addActionListener(this);
 
        textArea = new JTextArea(20, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
 
        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
		
        c.fill = GridBagConstraints.BOTH;
        add(textField, c);
	}
	
	public void actionPerformed(ActionEvent evt) {
        String text = textField.getText();
        textField.setText("");
		Client.sendMessage(text);
 
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
	
	// write a message on the chat panel
	protected void write(String msg){
		textArea.append(msg + newline);
	}
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    public static void createAndShowGUI(Chat chat) {
        //Create and set up the window.
        JFrame frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add contents to the window.
        frame.add(chat);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}