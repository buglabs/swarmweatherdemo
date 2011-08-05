package bugswarmtest;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.buglabs.device.IButtonEventProvider;
import com.buglabs.services.ws.IWSResponse;
import com.buglabs.services.ws.PublicWSDefinition;
import com.buglabs.services.ws.PublicWSProvider2;
import com.buglabs.services.ws.WSResponse;

import com.buglabs.application.ServiceTrackerHelper.ManagedInlineRunnable;
import com.buglabs.application.ServiceTrackerHelper.ManagedRunnable;

import com.buglabs.util.SelfReferenceException;
import com.buglabs.util.XmlNode;

/**
 * To be used with the SparkFun USB WeatherBoard. Enjoy
 * @author vish
 */
public class USB_Weather_Module_DemoApplication implements
ManagedInlineRunnable, PublicWSProvider2 {

	private BundleContext theContext ;
	private Timer timer;
	private static int timerPeriod = 5000, i=0;

	private ServiceRegistration webServiceReg ;

	private SerialPort serialPort;
	private InputStream serialIn;
	private OutputStream serialOut;
	private String devNode;

	private BufferedReader bufferedSerialIn;
	private BufferedWriter bufferedSerialOut;

	private boolean serialConnected;
	private boolean isStarted ;

	//	public String currHumidity;
	//	public String currTempC;
	//	public String currTempF;
	//	public String currTempFSCP;
	//	public String currBPressure;
	//	public String currLight;
	//	public String currSample;


	public String currTemp;
	public String currHumid;
	public String currDew;
	public String currBPressure;
	public String currLight;
	public String currWindspd ;
	public String currWinddir;
	public String currRain;
	public String currBatt;

	/*	hardcodevalues!
	public final String currTemp = "70";
	public final String currHumid = "70";
	public final String currDew = "70";
	public final String currBPressure = "70";
	public final String currLight = "70";
	public final String currWindspd = "70" ;
	public final String currWinddir = "70";
	public final String currRain = "70";
	public final String currBatt = "70";
	 */
	public USB_Weather_Module_DemoApplication(BundleContext context) {
		theContext = context ;
	}

	public void run(Map<Object, Object> services) {

		webServiceReg = theContext.registerService(PublicWSProvider2.class.getName(), this, null);

		IButtonEventProvider ibuttoneventprovider = (IButtonEventProvider) services
		.get(IButtonEventProvider.class.getName());

		serialConnected = false;
		isStarted = false ;

		timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {

				try {

					/* Test if USB WeatherBoard is detected */
					Process p;
					p = Runtime.getRuntime().exec(
							new String[] { "/bin/sh", "-c",
							"ls /dev | grep ttyUSB0 " });

					BufferedReader in = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					String line = null;
					line = in.readLine();
					if (line != null) {
						if (!serialConnected) {
							System.out.println("USB Weatherboard Detected!");
							initWeatherBoard(); // if detected, open serial port
							serialConnected = true ;

						}
						if (serialConnected && !isStarted) {
							startWeatherWatcher(); // if serial port
							// successfully attached,
							// start sensor watcher
							// thread
							isStarted = true ;
						}
					}
					else {
						if (isStarted) {
							serialIn.close();
							timer.cancel();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			public void shutdown() {
				timer.cancel();
				try {
					serialIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}, 0, timerPeriod);

	}

	private void initWeatherBoard() {
		System.out.println("Initializing Sparkfun USB Weatherboard");
		this.devNode = "/dev/ttyUSB0";
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier
			.getPortIdentifier(devNode);
			CommPort commPort = portIdentifier.open(this.getClass().getName(),
					2000);
			System.out.println("Opened it.");
			serialPort = (SerialPort) commPort;
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			System.out.println("Configured it");
			serialIn = serialPort.getInputStream();
			System.out.println("Got serialIn");
			this.serialConnected = true;
			// serialOut = serialPort.getOutputStream();
			// System.out.println("Got serialOut") ;

		} catch (UnsupportedCommOperationException e) {
			serialConnected = false;
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			serialConnected = false;
		} catch (NoSuchPortException e) {
			e.printStackTrace();
			serialConnected = false;
		} catch (PortInUseException e) {
			e.printStackTrace();
			serialConnected = false;
		}

	}

	/* Thread to receive weather sensor data from serial port, sampling at 1 Hz */
	private void startWeatherWatcher() {
		this.isStarted = true ;
		timer.schedule(new TimerTask() {


			public void run() {
				try {
					serialIn.skip(serialIn.available());
					String strIn = new String();
					for (int i = 0; i < 70; i++) {
						int numChars = serialIn.available();
						if (numChars > 0) {
							byte[] bb = new byte[numChars];
							serialIn.read(bb, 0, numChars);
							strIn += new String(bb);
							//							System.out.println("reading");
						}

						if (strIn.indexOf("$\r\n") != -1) {
							break;
						}
						Thread.sleep(100);
					}

					//					System.out.println("Unparsed: " + strIn);

					/*Parse raw data from sensor in to human-readable form.
					 *  Unparsed, looks like:
					 *  #18.10,077.20,025.5,077.98,100407,1018,0,015138$ 
					 */
					//					String substring ;
					//					substring = strIn.substring(strIn.indexOf('#') + 1);
					//					String[] dataElements = substring.split(",");
					//					
					//					System.out.println("Relative Humidity: " + dataElements[0] + "%") ;
					//					currHumidity = dataElements[0];
					//					System.out.println("Temp (SHT15 Sensor): " + dataElements[1] + "F");
					//					currTempC = dataElements[1];
					//					System.out.println("Temp (SCP1000 Sensor): " + dataElements[2] + "C");
					//					currTempF = dataElements[2];
					//					System.out.println("Temp (SCP1000 Sensor): " + dataElements[3] + "F");
					//					currTempFSCP = dataElements[3];
					//					System.out.println("Barometric Pressure: " + dataElements[4] + " Pascal") ;
					//					currBPressure = dataElements[4];
					//					System.out.println("Ambient Light: " + dataElements[5]);
					//					currLight = dataElements[5];
					//					System.out.println("sample number - " + dataElements[7].split("$")[0]);
					//					currSample = dataElements[7].split("$")[0] ;


					//  $,27.9,47,15.6,1221.71,0.8,0.0,-1,0.0,0.00,*
					String substring ;
					//					substring = strIn.substring(strIn.indexOf('$') + 1);
					String[] dataElements = strIn.split(",");

					if (dataElements[1] !=null){ //if not null check temp and humidity for spikes

						double temp = Double.parseDouble(dataElements[1]);
						double hum = Double.parseDouble(dataElements[2]);
						String prevTemp = currTemp, prevHumid =currHumid;

						if (temp <1000 && hum<1000 && temp >20 && hum >20){ //if within acceptable range print

							System.out.println("Temperature: " + dataElements[1] + "C") ;
							currTemp = dataElements[1];
							System.out.println("Humidity: " + dataElements[2] + "%");
							currHumid = dataElements[2];
							i=0;
						}

						else { //if not acceptable print previous values
							i++;
							if (i==3){ //if the abnormal spike repeats, print

								System.out.println("Temperature: " + dataElements[1] + "C") ;
								currTemp = dataElements[1];
								System.out.println("Humidity: " + dataElements[2] + "%");
								currHumid = dataElements[2];
								i=0;

							}
							else{
								System.out.println("Temperature: " + prevTemp + "C") ;

								System.out.println("Humidity: " + prevHumid + "%");

							}
						}
					}

					else {
						System.out.println("Temperature: " + dataElements[1] + "C") ;
						currTemp = dataElements[1];
						System.out.println("Humidity: " + dataElements[2] + "%");
						currHumid = dataElements[2];	//if null, print null
					}
					
					System.out.println("Dewpoint: " + dataElements[3] + "C");
					currDew = dataElements[3];
					System.out.println("Barometric pressure: " + dataElements[4] + "Hg");
					currBPressure = dataElements[4];
					System.out.println("Relative Light: " + dataElements[5] + "%") ;
					currLight = dataElements[5];
					System.out.println("Wind Speed: " + dataElements[6] + "mps");
					currWindspd = dataElements[6];
					System.out.println("Wind Direction: " + dataElements[7] + "degrees");
					currWinddir = dataElements[7];
					System.out.println("Cumulative Rainfall: " + dataElements[8] + "mm");
					currRain = dataElements[8];
					System.out.println("Battery Level (0 if USB): " + dataElements[9] + "volts");
					currBatt = dataElements[9] ;



				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException ae) {
					ae.printStackTrace();
				}

			}
			public void shutdown() {
				timer.cancel();
				isStarted = false ;
			}

		}, 0, 1000); 

	}

	public void shutdown() {

	}

	private String serviceName = "WeatherData";

	public void setPublicName(String name) {
		serviceName = name;
	}

	public PublicWSDefinition discover(int operation) {
		if (operation == PublicWSProvider2.GET) {
			return new PublicWSDefinition() {

				public List getParameters() {
					return null;
				}

				public String getReturnType() {
					return "text/xml";
				}
			};
		}

		return null;
	}

	public IWSResponse execute(int operation, String input) {
		if (operation == PublicWSProvider2.GET) {

			return new WSResponse(getWeatherXml(), "text/xml");
		}
		return null;
	}

	@Override
	public String getPublicName() {
		// TODO Auto-generated method stub
		return serviceName;
	}

	public String getDescription() {
		return "Returns data received from USB Serial Port";
	}


	private Object getWeatherXml() {
		XmlNode root = new XmlNode("SerialData");
		//gpio.  style = <GPIO>
		//                <Pin number="0">0</Pin>
		// 			      <Pin number="1">0</Pin> ...
		XmlNode gpio = new XmlNode("WeatherBoard");
		try {

			XmlNode pin0 = new XmlNode("Sensor", currHumid);
			XmlNode pin1 = new XmlNode("Sensor", currTemp);
			XmlNode pin2 = new XmlNode("Sensor", currDew);
			XmlNode pin3 = new XmlNode("Sensor", currBatt);
			XmlNode pin4 = new XmlNode("Sensor", currBPressure);
			XmlNode pin5 = new XmlNode("Sensor", currLight);

			pin0.addAttribute("type", "RelHumidity");
			pin1.addAttribute("type", "Temperature");
			pin2.addAttribute("type", "DewPoint");
			pin3.addAttribute("type", "BattLevel");
			pin4.addAttribute("type", "BaromPressure");
			pin5.addAttribute("type", "AmbientLight");

			pin0.addAttribute("unit", "%");
			pin1.addAttribute("unit", "C");
			pin2.addAttribute("unit", "C");
			pin3.addAttribute("unit", "Volts");
			pin4.addAttribute("unit", "Hg");
			pin5.addAttribute("unit", "%");
			gpio.addChildElement(pin0);
			gpio.addChildElement(pin1);
			gpio.addChildElement(pin2);
			gpio.addChildElement(pin3);
			gpio.addChildElement(pin4);
			gpio.addChildElement(pin5);
			root.addChildElement(gpio);
		} catch (SelfReferenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return root.toString();
	}

}