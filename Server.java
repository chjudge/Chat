import java.io.*; 
import java.util.*; 
import java.net.*; 
  
// Server class 
public class Server{ 
  
    // Vector to store active clients 
    static Vector<ClientHandler> ar = new Vector<>(); 
      
    // counter for clients 
    static int i = 0; 
  
    public static void main(String[] args) throws IOException{ 
        // server is listening on port 1234 
        ServerSocket ss = new ServerSocket(1234); 
		
		System.out.println("The server is running");
          
        Socket s; 
          
        // running infinite loop for getting 
        // client request 
        for(i = 0;;i++){ 
            // Accept the incoming request 
            s = ss.accept(); 
  
            System.out.println("New client request received : " + s); 
              
            // obtain input and output streams 
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			
			String username = "client " + i; 
                  
            System.out.println("Client name: " + username); 
            
            // Create a new handler object for handling this request. 
            ClientHandler mtch = new ClientHandler(s,username, dis, dos); 
  
            // Create a new Thread with this object. 
            Thread t = new Thread(mtch); 
            
            // add this client to active clients list 
            ar.add(mtch); 
  
            // start the thread. 
            t.start();
        } 
    } 
} 
  
// ClientHandler class 
class ClientHandler implements Runnable { 
    Scanner scn = new Scanner(System.in); 
    private String name; 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    Socket s; 
    boolean isloggedin; 
      
    // constructor 
    public ClientHandler(Socket s, String name, 
                            DataInputStream dis, DataOutputStream dos) { 
        this.dis = dis; 
        this.dos = dos; 
        this.name = name; 
        this.s = s; 
        this.isloggedin=true; 
    } 
  
    @Override
    public void run() { 
		writeAll(this.name + " has joined");
		
        String received;
        while (true){ 
			// receive the string 
			try{
				received = dis.readUTF(); 
				
				//logs out client
				if(received.equals("logout")){ 
					this.isloggedin=false;
					try { 
						dos.writeUTF("!&&closing**!"); 
					} catch (IOException e) { 
						e.printStackTrace(); 
					}
					System.out.println(name + " has left");
					writeAll(this.name + " has left");
					
					Server.ar.remove(this);
					this.s.close(); 
					break; 
				}
				
				if(received.startsWith("!name ")){
					String newUsername = received.substring(6);
					writeAll(this.name + " changed their name to " + newUsername);
					this.name = newUsername;
				}
				  
				//send message to all clients
				writeAll(this.name + ": " + received);
			} catch(IOException e) { 
				e.printStackTrace(); 
			}
        } 
        try{ 
            // closing resources 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
	
	private void writeAll(String msg){
		try{
			for (ClientHandler mc : Server.ar)
				mc.dos.writeUTF(msg);
		} catch (IOException e) { 
                e.printStackTrace(); 
            } 
	}
} 