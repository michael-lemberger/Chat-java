package chat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

class chatest {
	 public class Server_Thread extends Thread {
		 Server server;
		    public void run(){
		    	try {
		    		  server=new Server(1993);
		    		 server.run();
				this.sleep(1000);
		    	
		    	
		    	} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		  }
	 public class client_Thread extends Thread {

		    public void run(){
		    	 try {
					new Client("127.0.0.1", 8080).run();
					try {
						this.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		  }
	 public class gui_Thread extends Thread {
		 public void run(){
			    ClientGui client = new ClientGui();
		 }
	 }

	@Test
	void test() {
		Server_Thread srv= new Server_Thread();
			srv.start();
			
			client_Thread cli= new client_Thread();
			cli.start();

			gui_Thread gui= new gui_Thread();
			gui.run();
	}

}
