package chat;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.Color;
/**
 * @author michael lemberger and fanta mulugeta
 */
public class Server {

  public int port;
  public List<User> clients;
  public ServerSocket server;
  public String test;
  public boolean runner=true;
  public static void main(String[] args) throws IOException {
    new Server(1993).run();
  }
/**
 * a server constructor 
 * gets a port in integer
 * creates a list of all the users that connects
 * @param port integer 
 */
  public Server(int port) {
    this.port = port;
    this.clients = new ArrayList<User>();
  }
/**
 * runs the server
 * opens a new socket with the port id
 * @throws IOException if the server is off
 */
  public void run() throws IOException {
    server = new ServerSocket(port) {
     
    	protected void finalize() throws IOException {
        this.close();
      }
   
    };
    test= ""+this.port;
    System.out.println("Port "+ this.port+" is now open.");

    while (true) {
      // accepts a new client
      Socket client = server.accept();

      // gets nickname of a new user
      String nickname = (new Scanner ( client.getInputStream() )).nextLine();
      nickname = nickname.replace(",", ""); //  ',' use for serialisation
      nickname = nickname.replace(" ", "_");
      System.out.println("New Client: \"" + nickname + "\"\n\t     Host:" + client.getInetAddress().getHostAddress());

      // create new User object
      User newUser = new User(client, nickname);

      // add newUser message to list
      this.clients.add(newUser);

      // Welcome message
      newUser.getOutStream().println(
          "<b>Welcome</b> " + newUser.toString() 
          );

      // create a new thread for handling incoming messages of newUser.
      new Thread(new UserHandler(this, newUser)).start();
    }
    
  }
/**
 * remove a user from the clients list
 * @param user user
 */
  public void removeUser(User user){
    this.clients.remove(user);
  }
/**
 * gets a message and the clients list 
 * takes care that the message will send to the all users
 * @param msg String
 * @param userSender User
 */
  public void broadcastMessages(String msg, User userSender) {
    for (User client : this.clients) {
      client.getOutStream().println(
          userSender.toString() + "<span>: " + msg+"</span>");
    }
  }
  /**
   * sends a message when some one disconnected  to all clients list
   * @param userSender User
   */
  public void disconnect( User userSender) {
	    for (User client : this.clients) {
	      client.getOutStream().println(
	          userSender.toString() + "<span> <b>was disconected</b></span>");
	    }
	  }
  /**
   * sends a message to all clients when some one connected
   * @param userSender User
   */
  public void connect( User userSender) {
	    for (User client : this.clients) {
	      client.getOutStream().println(
	          userSender.toString() + "<span> <b>was connected!!</b></span>");
	    }
	  }
  /**
   * shows the clients list to all the clients
   * @param show boolean
   */
  public void broadcastAllUsers(boolean show){
    if(show==true) {
	  for (User client : this.clients) {
      client.getOutStream().println(this.clients);
    }
  }
  }
/**
 * send a private message to one client
 * @param msg String
 * @param userSender User
 * @param user String
 */
  public void sendMessageToUser(String msg, User userSender, String user){
    boolean find = false;
    for (User client : this.clients) {
      if (client.getNickname().equals(user) && client != userSender) {
        find = true;
        userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() +": " + msg);
        client.getOutStream().println(
            "(<b>Private</b>)" + userSender.toString() + "<span>: " + msg+"</span>");
      }
    }
    if (!find) {
      userSender.getOutStream().println(userSender.toString() + " -> (<b>no one!</b>): " + msg);
    }
  }
}
/**
 * the threads 
 * class that implements Runnable
 * shows what happens while the user is connected or disconnected 
 *
 */
class UserHandler implements Runnable {

  private Server server;
  private User user;

  public UserHandler(Server server, User user) {
    this.server = server;
    this.user = user;
    this.server.broadcastAllUsers(false);
  }

  /**
   * while the clients is connected he can:
   * send an emoji.
   * send a private message to another client 
   * send a broadcast message to all clients
   * when the client disconnects,
   * he is removed from the list of clients.
   */
  public void run() {
    String message;
    server.connect(user);

    // when there is a new message, broadcast to all
    Scanner sc = new Scanner(this.user.getInputStream());
    while (sc.hasNextLine()) {
    	
      message = sc.nextLine();

      // emojis
      message = message.replace(":)", "<img src='http://4.bp.blogspot.com/-ZgtYQpXq0Yo/UZEDl_PJLhI/AAAAAAAADnk/2pgkDG-nlGs/s1600/facebook-smiley-face-for-comments.png'>");
      message = message.replace(":D", "<img src='http://2.bp.blogspot.com/-OsnLCK0vg6Y/UZD8pZha0NI/AAAAAAAADnY/sViYKsYof-w/s1600/big-smile-emoticon-for-facebook.png'>");
      message = message.replace(":d", "<img src='http://2.bp.blogspot.com/-OsnLCK0vg6Y/UZD8pZha0NI/AAAAAAAADnY/sViYKsYof-w/s1600/big-smile-emoticon-for-facebook.png'>");
      message = message.replace(":(", "<img src='http://2.bp.blogspot.com/-rnfZUujszZI/UZEFYJ269-I/AAAAAAAADnw/BbB-v_QWo1w/s1600/facebook-frown-emoticon.png'>");
      message = message.replace("-_-", "<img src='http://3.bp.blogspot.com/-wn2wPLAukW8/U1vy7Ol5aEI/AAAAAAAAGq0/f7C6-otIDY0/s1600/squinting-emoticon.png'>");
      message = message.replace(";)", "<img src='http://1.bp.blogspot.com/-lX5leyrnSb4/Tv5TjIVEKfI/AAAAAAAAAi0/GR6QxObL5kM/s400/wink%2Bemoticon.png'>");
      message = message.replace(":P", "<img src='http://4.bp.blogspot.com/-bTF2qiAqvi0/UZCuIO7xbOI/AAAAAAAADnI/GVx0hhhmM40/s1600/facebook-tongue-out-emoticon.png'>");
      message = message.replace(":p", "<img src='http://4.bp.blogspot.com/-bTF2qiAqvi0/UZCuIO7xbOI/AAAAAAAADnI/GVx0hhhmM40/s1600/facebook-tongue-out-emoticon.png'>");
      message = message.replace(":o", "<img src='http://1.bp.blogspot.com/-MB8OSM9zcmM/TvitChHcRRI/AAAAAAAAAiE/kdA6RbnbzFU/s400/surprised%2Bemoticon.png'>");
      message = message.replace(":O", "<img src='http://1.bp.blogspot.com/-MB8OSM9zcmM/TvitChHcRRI/AAAAAAAAAiE/kdA6RbnbzFU/s400/surprised%2Bemoticon.png'>");

      // private message 
      if (message.charAt(0) == '@'){
        if(message.contains(" ")){
          System.out.println("private msg : " + message);
          int firstSpace = message.indexOf(" ");
          String userPrivate= message.substring(1, firstSpace);
          server.sendMessageToUser(
              message.substring(
                firstSpace+1, message.length()
                ), user, userPrivate
              );
        }

      }
      else if(message.equalsIgnoreCase("get_users")) {
    	  this.server.broadcastAllUsers(true);
      }
      else{
        // update user list
        server.broadcastMessages(message, user);
      }
    }
    // end of Thread
    server.disconnect(user);
    server.removeUser(user);
    this.server.broadcastAllUsers(false);
    sc.close();
  }
}
/**
 * the clients
 * shows how the clients looks like
 *
 */

class User {
  private static int nbUser = 0;
  private int userId;
  private PrintStream streamOut;
  private InputStream streamIn;
  private String nickname;
  private Socket client;
  private String color;

  /**
   * a user constructor 
   * @param client Socket
   * @param name String
   * 
   */
  public User(Socket client, String name) throws IOException {
    this.streamOut = new PrintStream(client.getOutputStream());
    this.streamIn = client.getInputStream();
    this.client = client;
    this.nickname = name;
    this.userId = nbUser;
    this.color = ColorInt.getColor(this.userId);
    nbUser += 1;
  }
/**
 * prints on the gui 
 * @return PrintStream this
 */
  public PrintStream getOutStream(){
    return this.streamOut;
  }
/**
 *gets the message from the client 
 */
  public InputStream getInputStream(){
    return this.streamIn;
  }
  public String getNickname(){
    return this.nickname;
  }

  // print user with a unique color
  public String toString(){

    return "<span style='color:"+ this.color
      +"'>" + this.getNickname() + "</span>";

  }
}
/**
 * the color of the clients
 */
class ColorInt {
	public static String[] mColors = {
            "#3079ab", // dark blue
    };
/**
 * sends the color of the client
 * @param i integer
 * @return a color
 */
    public static String getColor(int i) {
        return mColors[i % mColors.length];
    }
}
