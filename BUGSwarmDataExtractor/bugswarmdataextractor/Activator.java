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
	
	private String APIKEY = "cad18f704ecc55eba439121d3046cbcd9a227ba8";
	private String SWARMID = "7de223a52dc1e690883fd6cd7cebe86024db3e46";
	
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
			System.out.println(sb.toString());

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

	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
	}
}