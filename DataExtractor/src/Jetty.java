import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONObject;

public class Jetty extends AbstractHandler
{
	private final String APIKEY = "cad18f704ecc55eba439121d3046cbcd9a227ba8";
	private final String SWARMID = "7de223a52dc1e690883fd6cd7cebe86024db3e46";

	
	private static ArrayList<JSONObject> swarm;
	private static Connection conn;

	private String json_feed;

	private JSONObject rmb;
	private JSONObject ron;
	private JSONObject panda;

	public void handle(String target,
			Request baseRequest,
			HttpServletRequest request,
			HttpServletResponse response) 
	throws IOException, ServletException
	{
		if (target.contains("panda"))
		{
			 swarmConnect();
             System.out.println("panda");

             response.setContentType("application/json");

             //response.setContentType("text/html;charset=utf-8");
             response.setStatus(HttpServletResponse.SC_OK);
             baseRequest.setHandled(true);

             response.getWriter().println(json_feed);
             JSONArray json = null;
             try {
                     json = new JSONArray(json_feed);
             } catch (ParseException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             swarm = new ArrayList<JSONObject>();
             for(int i = 0; i<json.length();i++){
                     swarm.add(json.getJSONObject(i));
                     System.out.println(json.getJSONObject(i).getJSONObject("resource").getString("id").replaceAll(":", ""));
                     try {
                             sqlUpdate(conn,parseFeed(json.getJSONObject(i)),json.getJSONObject(i).getJSONObject("resource").getString("id").replaceAll(":", ""));
                            
                     } catch (NoSuchElementException e) {
                             // TODO Auto-generated catch block
                             e.printStackTrace();
                     } catch (SQLException e) {
                             // TODO Auto-generated catch block
                             e.printStackTrace();
                     }
             }
			     
		}
		else if (target.contains("ui"))
		{
			System.out.println("ui");

			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);

			StringBuilder panda = new StringBuilder();
			ClassLoader cl = getClass().getClassLoader();
			URL url = cl.getResource("index.html");	    
			BufferedReader in;
			try {
				in = new BufferedReader(
						new InputStreamReader(
								url.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null)
				{
					
					panda.append(inputLine);
					panda.append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			response.getWriter().println(panda.toString());

		}
		else if (target.contains("swarm.js"))
		{
			System.out.println("swarm.js");
			response.setContentType("text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			StringBuilder panda = new StringBuilder();
			ClassLoader cl = getClass().getClassLoader();
			URL url = cl.getResource("swarm.js");	    
			BufferedReader in;
			try {
				in = new BufferedReader(
						new InputStreamReader(
								url.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null)
				{
					
					panda.append(inputLine);
					panda.append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			response.getWriter().println(panda.toString());
		}
		else if (target.contains("weather.js"))
		{
			System.out.println("weather.js");
			response.setContentType("text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			StringBuilder panda = new StringBuilder();
			ClassLoader cl = getClass().getClassLoader();
			URL url = cl.getResource("weather.js");	    
			BufferedReader in;
			try {
				in = new BufferedReader(
						new InputStreamReader(
								url.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null)
				{
					
					panda.append(inputLine);
					panda.append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			response.getWriter().println(panda.toString());
		}
		else if (target.contains("google.js"))
		{
			System.out.println("google.js");
			response.setContentType("text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			StringBuilder panda = new StringBuilder();
			ClassLoader cl = getClass().getClassLoader();
			URL url = cl.getResource("google.js");	    
			BufferedReader in;
			try {
				in = new BufferedReader(
						new InputStreamReader(
								url.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null)

				{
					
					panda.append(inputLine);
					panda.append("\n");
				}
				System.out.println("panda");

					panda.append(inputLine);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(panda.toString());
			response.getWriter().println(panda.toString());
		}
		else if (target.contains("image.png"))
		{
			System.out.println("image.png");

			File f = new File("image.png");
			response.setContentType("image/png");
			FileInputStream fis = new FileInputStream(f);

			response.setContentLength(fis.available());

			int i = 0;
			while ((i = fis.read()) != -1) {
				response.getWriter().write(i);
				System.out.println(i);
			}
			
			fis.close();
		}
		
		else if (target.contains("rainbow.jpg"))
		{
			System.out.println("rainbow.jpg");

			File f = new File("rainbow.jpg");
			response.setContentType("image/jpg");
			FileInputStream fis = new FileInputStream(f);

			response.setContentLength(fis.available());

			int i = 0;
			while ((i = fis.read()) != -1) {
				response.getWriter().write(i);
				System.out.println(i);
			}
			
			fis.close();
		}
	}

	public void swarmConnect() {
		HttpURLConnection connection = null;
		BufferedReader rd  = null;
		StringBuilder sb = null;
		String line = null;

		URL serverAddress = null;

		try {
			serverAddress = new URL("http://bugswarm-test/swarms/" + SWARMID + 
			"/feeds/my_test_feed");
			//set up out communications stuff
			connection = null;

			//Set up the initial connection
			connection = (HttpURLConnection)serverAddress.openConnection();
			connection.addRequestProperty("X-BugSwarmApiKey", APIKEY);
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);

			connection.connect();

			//read the result from the server
			rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();

			while ((line = rd.readLine()) != null)
			{
				sb.append(line + '\n');
			}

			json_feed = sb.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			//close the connection, set all objects to null
			connection.disconnect();
			rd = null;
			sb = null;
			connection = null;
		}
	}

	public String parseFeed(JSONObject bug) {
		String name = bug.getJSONObject("resource").getString("id").replaceAll(":", "");
		JSONObject feed = bug.getJSONObject("payload").getJSONObject("my_test_feed");
		String currBatt = feed.getString("currBatt");
		String currTemp = feed.getString("currTemp");
		String currHumid = feed.getString("currHumid");
		String currWinddir = feed.getString("currWinddir");
		String currRain = feed.getString("currRain");
		String currBPressure = feed.getString("currBPressure");
		String currTime = feed.getString("currTime");
		String currWindspd = feed.getString("currWindspd");
		String currDew = feed.getString("currDew");
		String currLight = feed.getString("currLight");

		return "INSERT INTO " + name +" (currTemp, currHumid, currDew, currBPressure, currLight, " +
		"currWindspd, currWinddir, currRain, currBatt, currTime) values ("+currTemp + ", " + currHumid + ", " + currDew + ", " 
		+ currBPressure + ", " + currLight + ", " + currWindspd + ", " +  currWinddir + ", " + currRain + ", " + currBatt+  ", " + currTime+")"; 

	}
	
	public static Connection sqlConnect(){
		System.out.println("MySQL Connect Example.");
		Connection conn = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "weather";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root"; 
		String password = "root";

		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url+dbName,userName,password);

			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	public void sqlUpdate(Connection conn,String insert, String macaddress) throws SQLException{
		Statement stmt = conn.createStatement();
		String create = "CREATE TABLE IF NOT EXISTS "+ macaddress + " (currTemp VARCHAR(50), currHumid VARCHAR(50), " +
		"currDew VARCHAR(50), currBPressure VARCHAR(50), currLight VARCHAR(50), currWindspd VARCHAR(50), " +
		"currWinddir VARCHAR(50), currRain VARCHAR(50), currBatt VARCHAR(50), currTime VARCHAR(50))";
		stmt.executeUpdate(create);
		stmt = conn.createStatement();

		System.out.println(insert);
		stmt.executeUpdate(insert);
	}

	public static void main(String[] args) throws Exception
	{
		Server server = new Server(8080);
		server.setHandler(new Jetty());

		conn = sqlConnect();

		server.start();
		server.join();
	}
}
