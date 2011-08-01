import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Jetty extends AbstractHandler
{
	private final String APIKEY = "cad18f704ecc55eba439121d3046cbcd9a227ba8";
	private final String SWARMID = "7de223a52dc1e690883fd6cd7cebe86024db3e46";

	private final String key1 = "00:50:c2:69:c8:29"; //rmb
	private final String key2 = "00:50:c2:69:c8:2a"; //panda

	private String temperature;
	private String humidity;
	private String windDirection;
	private String windSpeed;
	private String dewpoint;
	private String rainfall;
	private String pressure;
	private String batlevel;

	private String json_feed;

	private String display1;
	private String display2;
	
	private String ui_html;
	
	public void handle(String target,
			Request baseRequest,
			HttpServletRequest request,
			HttpServletResponse response) 
	throws IOException, ServletException
	{
		if (target.contains("panda"))
		{
			doStuff();
			System.out.println("panda");

			response.setContentType("application/json");

			//response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);

			response.getWriter().println(json_feed);
			/*        response.getWriter().println("<h1>panda's weather</h1>");
        response.getWriter().println("<p>" + display1 + "</p>");
        response.getWriter().println("<img src=\"http://farm1.static.flickr.com/45/151498777_2af8148a1f.jpg\">");
        response.getWriter().println("<h1>rmb's weather</h1>");
        response.getWriter().println("<p>" + display2 + "</p>");*/
		}
		else if (target.contains("ui"))
		{
			System.out.println("ui");

			//response.setContentType("application/json");

			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);

			//response.getWriter().println(ui_html);
			
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
				panda.append(inputLine);
			} catch (IOException e) {
				e.printStackTrace();
			}
			response.getWriter().println(panda.toString());
			
		}
		else if (target.contains("helloworld.js"))
		{
			System.out.println("helloworld.js");
			response.setContentType("text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			StringBuilder panda = new StringBuilder();
			ClassLoader cl = getClass().getClassLoader();
			URL url = cl.getResource("helloworld.js");	    
			BufferedReader in;
			try {
				in = new BufferedReader(
						new InputStreamReader(
								url.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null)
				panda.append(inputLine);
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
				panda.append(inputLine);
			} catch (IOException e) {
				e.printStackTrace();
			}
			response.getWriter().println(panda.toString());
		}
	}

	public void doStuff() {
		HttpURLConnection connection = null;
		OutputStreamWriter wr = null;
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

			//get the output stream writer and write the output to the server
			//not needed in this example
			//wr = new OutputStreamWriter(connection.getOutputStream());
			//wr.write("");
			//wr.flush();

			//read the result from the server
			rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();

			while ((line = rd.readLine()) != null)
			{
				sb.append(line + '\n');
			}
			//System.out.println(sb.toString());
			//parseFeed2(sb.toString());
			/*System.out.println("Temperature: " + temperature);
			System.out.println("Wind Direction: " + windDirection);
			System.out.println("Humidity: " + humidity);*/
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
			wr = null;
			connection = null;
		}
	}

	public String parseFeed(String feed) {
		String temp = feed.substring(feed.indexOf("Wind Direction"));
		temp = temp.substring(17, temp.indexOf("\","));
		windDirection = temp;
		//System.out.println(windDirection);

		temp = feed.substring(feed.indexOf("Temperature"));
		temp = temp.substring(14, temp.indexOf("\","));
		temperature = temp;
		//System.out.println(temperature);

		temp = feed.substring(feed.indexOf("Humidity"));
		temp = temp.substring(11, temp.indexOf("\","));
		humidity = temp;
		//System.out.println(humidity);

		temp = feed.substring(feed.indexOf("Wind Speed"));
		temp = temp.substring(13, temp.indexOf("\","));
		windSpeed = temp;
		//System.out.println(windSpeed);

		temp = feed.substring(feed.indexOf("Dewpoint"));
		temp = temp.substring(11, temp.indexOf("\","));
		dewpoint = temp;
		//System.out.println(dewpoint);

		temp = feed.substring(feed.indexOf("Cumulative rainfall"));
		temp = temp.substring(22, temp.indexOf("\","));
		rainfall = temp;
		//System.out.println(rainfall);

		temp = feed.substring(feed.indexOf("Barometric pressure"));
		temp = temp.substring(22, temp.indexOf("\","));
		pressure = temp;
		//System.out.println(pressure);

		temp = feed.substring(feed.indexOf("Battery level"));
		temp = temp.substring(16, temp.indexOf("\","));
		batlevel = temp;
		//System.out.println(dewpoint);

		StringBuilder sb = new StringBuilder();
		sb.append("Wind Direction: " + windDirection + '\n');
		sb.append("Temperature: " + temperature + '\n');
		sb.append("Humidity: " + humidity + '\n');
		sb.append("Wind Speed: " + windSpeed + '\n');
		sb.append("Dewpoint: " + dewpoint + '\n');
		sb.append("Cumulative rainfall: " + rainfall + '\n');
		sb.append("Barometric pressure: " + pressure + '\n');
		sb.append("Battery level: " + batlevel + '\n');
		return sb.toString();
	}

	public void parseFeed2(String feed) {
		String one = feed.substring(0, feed.indexOf("UserKey"));
		String two = feed.substring(feed.indexOf("UserKey"));

		if (one.indexOf(key1) == -1)
		{
			display1 = parseFeed(one);
			display2 = parseFeed(two);
		}
		else
		{
			display1 = parseFeed(two);
			display2 = parseFeed(one);
		}
	}

	public static void main(String[] args) throws Exception
	{
		Server server = new Server(8080);
		server.setHandler(new Jetty());

		server.start();
		server.join();
	}
}
