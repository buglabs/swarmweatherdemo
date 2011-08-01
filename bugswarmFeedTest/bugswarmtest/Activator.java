package bugswarmtest;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;


import com.buglabs.application.ServiceTrackerHelper;
import com.buglabs.device.IButtonEventProvider;


/**
 * This is an example application for the new bugswarm-connector client.  This code is not backwards compatible with Swarm-DP.
 * 
 * This is an "API-less interface" in that it only uses OSGi and does not add any additional dependencies, interfaces,
 * or patterns.
 * 
 * The API is whiteboard style.  Any client wishing to contribute a feed simply has to register a java.util.Map 
 * in the OSGi service registry with the following service properties:
 * 
 * SWARM.FEED.NAME		-	The name of the feed.
 * SWARM.FEED.TIMESTAMP	-	The client local time of the last time the map was updated by the client application.  
 * 							This value can be determined easily with System.currentTimeMillis().
 * 
 * If the bugswarm-connector client is active, it will receive updates when the service is updated.  From there,
 * it is up to bugswarm-connector to determine how and when to send the feed updates on to the swarm server.
 * 
 * Clients can be intelligent about when updates are sent to the server by only calling setProperties() on 
 * the ServiceRegistration when they want the server to know that updates have occurred.
 * 
 * In the case of an earthquake detector, one thread could be loading the map with values, and another thread
 * could be analyzing the values to determine if there has been a noteworthy event.
 * The first thread would never cause bugswarm-connector to send a feed update to the server because
 * it does not call ServiceRegistration.setProperties().  Only the second thread would do so, preventing 
 * extraneous data from being sent to the server.
 * 
 * To revoke the feed from bugswarm, the service simply needs to be unregistered.
 * 
 * @author kgilmer
 *
 */
public class Activator implements BundleActivator {
	
	/**
	 * Name of feed as will be presented to other clients from swarm-server.
	 */
	private static final String PROP_SWARM_FEED = "SWARM.FEED.NAME";
	/**
	 * Last time map udpate was registered for bugswarm-connector.
	 */
	private static final String PROP_SWARM_UPDATED = "SWARM.FEED.TIMESTAMP";
	
	private static final String MY_FEED_NAME = "my_test_feed";
	
	private ServiceRegistration sr;
	private ServiceTracker serviceTracker;
	private Timer timer;
	private USB_Weather_Module_DemoApplication app;
	private static final String [] services = {		
		IButtonEventProvider.class.getName(),
	};	
	
	private int i = 0;
	
	public void start(BundleContext context) throws Exception {
		//Create the feed
		final Map<String, String> m = new HashMap<String, String>();
		
		//Register it so it is picked up by bugswarm-connector
		sr = context.registerService(Map.class.getName(), m, createProperties(MY_FEED_NAME));
		app = new USB_Weather_Module_DemoApplication(context);
		serviceTracker = ServiceTrackerHelper.openServiceTracker(context, services, app);
		
		//Do some periodic updates
		timer = new Timer();
		//Update the feed every 5 seconds.
		final Random rnd = new Random();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				i++;
				m.put("currTemp", app.currTemp);
				m.put("currHumid", app.currHumid);
				m.put("currWinddir", app.currWinddir);
				m.put("currWindspd", app.currWindspd);
				m.put("currDew", app.currDew);
				//m.put("currRain", app.currRain);
				m.put("currRain", i + "");
				m.put("currBPressure", app.currBPressure);
				m.put("currBatt",app.currBatt);
				m.put("currTime", Long.toString(System.currentTimeMillis()));
				m.put("UserKey", "" + rnd.nextDouble());
				sr.setProperties(createProperties(MY_FEED_NAME));
			}
		}, 1000, 5000);

		
		
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		serviceTracker.close();
		timer.cancel();
	}
	
	public void enterKeyvalue(String key, String value) {
		//m.put(key, value);
	}
	
	/**
	 * Create the service properties necessary for bugswarm-connector to recognize the service as a swarm feed.
	 * @param name
	 * @return
	 */
	private Dictionary<String, String> createProperties(String name) {
		Dictionary<String, String> d = new Hashtable<String, String>();
		
		d.put(PROP_SWARM_FEED, name);
		d.put(PROP_SWARM_UPDATED, "" + System.currentTimeMillis());
		
		return d;
	}
}