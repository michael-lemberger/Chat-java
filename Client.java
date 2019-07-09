package chat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.StringReader;
/**
 * @author michael lemberger and fanta mulugeta
 *
 * simulate a client host server connection.
 *  
 */
public class Client {

  private String host;
  private int port;

  public static void main(String[] args) throws UnknownHostException, IOException {
    new Client("127.0.0.1", 1993).run();
  }
/**
 * client constructor 
 * gets port and host to connect
 * @param host String
 * @param port Integer
 */
  public Client(String host, int port) {
    this.host = host;
    this.port = port;
  }
/**
 * threads:
 * opens a sockets while running. 
 * console prints the results
 * @throws UnknownHostException IOException exceptions 
 */
  public void run() throws UnknownHostException, IOException {
    // connect client to server
    Socket client = new Socket(host, port);
    System.out.println("Client successfully connected to server!");

    // Get Socket output stream (where the client send her mesg)
    PrintStream output = new PrintStream(client.getOutputStream());

    // ask for a nickname
    Scanner sc = new Scanner(System.in);
    System.out.print("Enter a nickname: ");
    String nickname = sc.nextLine();

    // choosing nickname and sending to server.
    output.println(nickname);

    // new thread - server messages handling
    new Thread(new ReceivedMessagesHandler(client.getInputStream())).start();

    // reading input and sending messages to server.
    System.out.println("Messages: \n");

    // open conversation.
    while (sc.hasNextLine()) {
      output.println(sc.nextLine());
    }

    // end ctrl D
    output.close();
    sc.close();
    client.close();
  }
}

class ReceivedMessagesHandler implements Runnable {

  private InputStream server;

  public ReceivedMessagesHandler(InputStream server) {
    this.server = server;
  }
/**
 * run function for threads. 
 * simple parsing of messages and printing them.
 */
  public void run() {
    Scanner s = new Scanner(server);
    String tmp = "";
    while (s.hasNextLine()) {
      tmp = s.nextLine();
      if (tmp.charAt(0) == '[') {
        tmp = tmp.substring(1, tmp.length()-1);
        System.out.println(
            "\nUSERS LIST: " +
            new ArrayList<String>(Arrays.asList(tmp.split(","))) + "\n"
            );
      }else{
        try {
          System.out.println("\n" + getTagValue(tmp));
        } catch(Exception ignore){}
      }
    }
    s.close();
  }

  
  public static String getTagValue(String xml){
    return  xml.split(">")[2].split("<")[0] + xml.split("<span>")[1].split("</span>")[0];
  }

}
