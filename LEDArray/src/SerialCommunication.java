import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;
/**
 * @author DAflamingFOX
 * read README.md for info
 */
public class SerialCommunication {
	public static final int MAX_BYTES=64;
	private static SerialPort port;
	private static boolean portOpen;

	/**
	 * This method will open a serial port for communication
	 * @param choice Which port number you want to open
	 * @throws Exception
	 */
	public static void begin(int choice) throws Exception {
		portOpen=false;
		SerialPort[] ports = SerialPort.getCommPorts();

		// attempt to open port,
		port = ports[choice];
		while(!portOpen) {
			if (port.openPort()) {
				System.out.println("port opened:");
				portOpen = true;
			} else {
				System.out.println("Unable to open port:");
				Scanner retry = new Scanner(System.in);
				System.out.println("Retry? [Y/N]");
				if (retry.nextLine().toLowerCase() == "y") {
					portOpen = false;
				}
				else {
					System.exit(1);
				}
			}
		}


		// sets up the port
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		port.setBaudRate(ports[choice].getBaudRate()); // should autoatically set baud rate, if it doesnt, just set it manually
	}
	/**
	 * Send string data out through the port
	 * @param data
	 */
	public static void sendData(String data) {
		//Only send data if the port is open
		if(portOpen) {
			// sets up the data scanner
			Scanner dataOut = new Scanner(data);

			// takes input and sends it 64 bytes at a time
			System.out.print("> ");
			do {
				byte output[]=new byte[MAX_BYTES];
				int curBytes=0;
				while(dataOut.hasNextByte()&&curBytes<MAX_BYTES) {
					output[curBytes]=dataOut.nextByte();
					curBytes++;
				}
				System.out.print(new String(output, StandardCharsets.UTF_8));
				port.writeBytes(output, MAX_BYTES);            
			} while (dataOut.hasNext());
		}
	}

	public static void end() {
		port.closePort();
		portOpen=false;
	}
	public static void showPorts() {
		System.out.println("Available ports: ");
		int i = 0;
		for (SerialPort port : SerialPort.getCommPorts()) {
			System.out.println(i++ + ": " + port.getDescriptivePortName());
		}
	}
	
	public static boolean isOpen() {
		return portOpen;
	}
}
