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
		for (ClientHandler mc : Server.ar) {
			mc.dos.writeUTF(this.name + " has joined");
		}
        String received;
        while (true){ 
            try{ 
                // receive the string 
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
					for (ClientHandler mc : Server.ar) {
						mc.dos.writeUTF(this.name + " has joined");
					}
					Server.ar.remove(this);
                    this.s.close(); 
                    break; 
                } 
                  
				//send message to all clients
				for (ClientHandler mc : Server.ar) {
						mc.dos.writeUTF(this.name + ": " + received);
				}
				
                // search for the recipient in the connected devices list. 
                // ar is the vector storing client of active users 
//                
//				for (ClientHandler mc : Server.ar){ 
					// if the recipient is found, write on its 
					// output stream 
//					if ((mc.name.equals(recipient)) && mc.isloggedin==true){ 
//						mc.dos.writeUTF(this.name+" : "+MsgToSend);
//					} 
//				}
				
            } catch (IOException e) { 
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
} 