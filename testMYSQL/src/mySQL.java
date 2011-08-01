
import java.sql.*;

public class mySQL{
	public static void main(String[] args) {
		System.out.println("MySQL Connect Example.");
		Connection conn = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "weather";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root"; 
		String password = "root";
		String macaddress = "example";
		String currTemp = null, currHumid = null, currDew = null, currBPressure = null, currLight = null, currWindspd = null, currWinddir = null, currRain = null, currBatt = null, currTime = null;
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url+dbName,userName,password);
			Statement stmt = conn.createStatement();
			String create = "CREATE TABLE IF NOT EXISTS "+ macaddress + " (currTemp VARCHAR(50), currHumid VARCHAR(50), " +
			"currDew VARCHAR(50), currBPressure VARCHAR(50), currLight VARCHAR(50), currWindspd VARCHAR(50), " +
			"currWinddir VARCHAR(50), currRain VARCHAR(50), currBatt VARCHAR(50), currTime VARCHAR(50))";
			stmt.executeUpdate(create);
			stmt = conn.createStatement();
			String insert = "INSERT INTO " + macaddress +"(currTemp, currHumid, currDew, currBPressure, currLight, " +
					"currWindspd, currWinddir, currRain, currBatt, currTime) values ("+currTemp + " " + currHumid + " " + currDew + " " 
					+ currBPressure + " " + currLight + " " + currWindspd + " " +  currWinddir + " " + currRain + " " + currBatt+  " " + currTime+")"; 
			System.out.println("Connected to the database");
			conn.close();
			System.out.println("Disconnected from database");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
