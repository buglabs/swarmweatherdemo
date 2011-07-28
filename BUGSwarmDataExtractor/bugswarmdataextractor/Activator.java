package bugswarmdataextractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {
	
	private final String APIKEY = "cad18f704ecc55eba439121d3046cbcd9a227ba8";
	private final String SWARMID = "7de223a52dc1e690883fd6cd7cebe86024db3e46";
	
	private String temperature;
	private String humidity;
	private String windDirection;
	
	public void start(BundleContext context) throws Exception {

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

			System.out.println();
			System.out.println();
			System.out.println();
			//System.out.println(sb.toString());
			parseFeed(sb.toString());
			System.out.println("Temperature: " + temperature);
			System.out.println("Wind Direction: " + windDirection);
			System.out.println("Humidity: " + humidity);
			

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
	
	public void parseFeed(String feed) {
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
	}

	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
	}
}