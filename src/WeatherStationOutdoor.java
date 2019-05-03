import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.BrickletOutdoorWeather;
import com.tinkerforge.BrickletUVLight;
import com.tinkerforge.BrickletAmbientLightV2;
import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;


class WeatherListener implements IPConnection.EnumerateListener,
                                 IPConnection.ConnectedListener,
                                 BrickletHumidity.HumidityListener,
                                 BrickletTemperature.TemperatureListener,
                                 BrickletOutdoorWeather.StationDataListener,
                                 BrickletUVLight.UVLightListener,
                                 BrickletAmbientLightV2.IlluminanceListener{
	private IPConnection ipcon = null;
	private BrickletHumidity brickletHumidity = null;
	private BrickletTemperature brickletTemperature = null;
	private BrickletOutdoorWeather brickletOutdoorWeather = null;
	private BrickletUVLight brickletUVLight = null;
	private BrickletAmbientLightV2 brickletAmbientLightV2 = null;

	public WeatherListener(IPConnection ipcon) {
		this.ipcon = ipcon;
	}


	public void humidity(int humidity) {
		double humidityValue = humidity/10.0;
		String humi = Double.toString(humidityValue);
		writetoFile("Inside_Humidity", humi);	
	}
	
	
	public void temperature(short temperature) {
		double temp = temperature/100.0;
		String celcius = Double.toString(temp);
		writetoFile("Inside_Temperature" , celcius);	
}
	
	
	public void stationData(int identifier, int temperature, int humidity, long windSpeed,
							long gustSpeed, long rain, int windDirection, boolean batteryLow) {
			// Station Indentifier gebruiken we niet
			// String stationIdentifier = Integer.toString(identifier);
			
			// Write temperature to File
			double temp = temperature/10.0;
			String celcius = Double.toString(temp);
			writetoFile("Station_Temperature" , celcius);	
			
			// Write humidity to File
			String humi = Integer.toString(humidity);
			writetoFile("Station_Humidity", humi);	
			
			// Write WindSpeed to File
			double WindSpeed = windSpeed/10.0;
			String speed = Double.toString(WindSpeed);
			writetoFile("Station_WindSpeed", speed);
			
			// Write GustSpeed to File
			double GustSpeed = gustSpeed/10.0;
			String gust = Double.toString(GustSpeed);
			writetoFile("Station_WindStoot", gust);
			
			// Write rain to File
			double Rain = rain/10.0;
			String Regen = Double.toString(Rain);
			writetoFile("Station_Regen", Regen);

			// Write WindDirection to File
			String direction = Integer.toString(windDirection);
			writetoFile("Station_Windrichting", direction);
			
			// Write BatteryStatus to File
			String BatteryLow = Boolean.toString(batteryLow);
			writetoFile("Station_BatteryLow", BatteryLow);	
	}
	
	
	public void uvLight(long uvLight) {
		double uv = uvLight/10.0;
		String uvlight = Double.toString(uv);
		writetoFile("Outside_UVLight" , uvlight);
	}
	
	
	public void illuminance(long illuminance) {
		double illuminanceValue = illuminance/10.0;
		String illumi = Double.toString(illuminanceValue);
		writetoFile("Outside_AmbientLight", illumi);
	}


	
	public void enumerate(String uid, String connectedUid, char position,
	                      short[] hardwareVersion, short[] firmwareVersion,
	                      int deviceIdentifier, short enumerationType) {
		if(enumerationType == IPConnection.ENUMERATION_TYPE_CONNECTED ||
		   enumerationType == IPConnection.ENUMERATION_TYPE_AVAILABLE) {
			if(deviceIdentifier == BrickletUVLight.DEVICE_IDENTIFIER) {
				try {
					brickletUVLight = new BrickletUVLight(uid, ipcon);
					brickletUVLight.setUVLightCallbackPeriod(30000);
					brickletUVLight.addUVLightListener(this);
					System.out.println("UVLight initialized");
				} catch(com.tinkerforge.TinkerforgeException e) {
					brickletUVLight = null;
					System.out.println("Ambient Light 2.0 init failed: " + e);
				}
			} else if(deviceIdentifier == BrickletHumidity.DEVICE_IDENTIFIER) {
				try {
					brickletHumidity = new BrickletHumidity(uid, ipcon);
					brickletHumidity.setHumidityCallbackPeriod(30000);
					brickletHumidity.addHumidityListener(this);
					System.out.println("Humidity initialized");
				} catch(com.tinkerforge.TinkerforgeException e) {
					brickletHumidity = null;
					System.out.println("Humidity Inside init failed: " + e);
				}	
			} else if(deviceIdentifier == BrickletTemperature.DEVICE_IDENTIFIER) {
				try {
					brickletTemperature = new BrickletTemperature(uid, ipcon);
					brickletTemperature.setTemperatureCallbackPeriod(30000);
					brickletTemperature.addTemperatureListener(this);
					System.out.println("Temperature initialized");
				} catch(com.tinkerforge.TinkerforgeException e) {
					brickletTemperature = null;
					System.out.println("Temperature Inside init failed: " + e);
				}
			} else if(deviceIdentifier == BrickletOutdoorWeather.DEVICE_IDENTIFIER) {
				try {
					brickletOutdoorWeather = new BrickletOutdoorWeather(uid, ipcon);
					brickletOutdoorWeather.setStationCallbackConfiguration(true);
					brickletOutdoorWeather.addStationDataListener(this);
					System.out.println("Outdoor Weather Station initialized");
				} catch(com.tinkerforge.TinkerforgeException e) {
					brickletOutdoorWeather = null;
					System.out.println("Weather Station init failed: " + e);
				}
			} else if(deviceIdentifier == BrickletAmbientLightV2.DEVICE_IDENTIFIER) {
				try {
					brickletAmbientLightV2 = new BrickletAmbientLightV2(uid, ipcon);
					brickletAmbientLightV2.setConfiguration(BrickletAmbientLightV2.ILLUMINANCE_RANGE_64000LUX,
					                                        BrickletAmbientLightV2.INTEGRATION_TIME_200MS);
					brickletAmbientLightV2.setIlluminanceCallbackPeriod(30000);
					brickletAmbientLightV2.addIlluminanceListener(this);
					System.out.println("Ambient Light 2.0 initialized");
				} catch(com.tinkerforge.TinkerforgeException e) {
					brickletAmbientLightV2 = null;
					System.out.println("Ambient Light 2.0 init failed: " + e);
				}
			}
		}
	}

	
	public void connected(short connectedReason) {
		if(connectedReason == IPConnection.CONNECT_REASON_AUTO_RECONNECT) {
			System.out.println("Auto Reconnect");

			while(true) {
				try {
					ipcon.enumerate();
					break;
				} catch(com.tinkerforge.NotConnectedException e) {
				}

				try {
					Thread.sleep(10000);
				} catch(InterruptedException ei) {
				}
			}
		}
	}
	
	
	public void writetoFile( String property, String value ) {
		
		//Prepare log-line
		//String text = property + "," + value;
		String log_text = value + "\n";
				
		//Convert string to byte array
		byte data[] = log_text.getBytes();
		
		//Set log-file name
		//String logfileName = property + ".log";
		String logfileName = property + ".txt";
		
		//Set log-dir name
		String logfileDir = "./data/";
		
		Path p = Paths.get(logfileDir+logfileName);
		try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(p, CREATE, APPEND))) {
				out.write(data, 0, data.length);
		} catch (IOException x) {
			System.err.println(x);
		}
	}
}


public class WeatherStationOutdoor {
	private static final String HOST = "192.168.2.197";
	private static final int PORT = 4223;
	private static IPConnection ipcon = null;
	private static WeatherListener weatherListener = null;
	

	public static void main(String args[]) throws java.net.UnknownHostException, java.io.IOException {
		ipcon = new IPConnection();
		

		while(true) {
			try {
				ipcon.connect(HOST, PORT);
				break;
			} catch(com.tinkerforge.AlreadyConnectedException e) {
			} catch (NetworkException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

			try {
				Thread.sleep(10000);
			} catch(InterruptedException e) {
			}
		}

		weatherListener = new WeatherListener(ipcon);
		ipcon.addEnumerateListener(weatherListener);
		ipcon.addConnectedListener(weatherListener);

		while(true) {
			try {
				ipcon.enumerate();
				break;
			} catch(com.tinkerforge.NotConnectedException e) {
			}

			try {
				Thread.sleep(10000);
			} catch(InterruptedException ei) {
			}
		}

		try {
			while (true){
				Thread.sleep(10000);
			}
		} catch(InterruptedException e) {
		}
 
		try {
			ipcon.disconnect();
		} catch(com.tinkerforge.NotConnectedException e) {
		}
	}
}
