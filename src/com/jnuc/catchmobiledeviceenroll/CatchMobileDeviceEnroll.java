package com.jnuc.catchmobiledeviceenroll;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;

import com.jamfsoftware.eventnotifications.JAMFEventNotificationMonitor;
import com.jamfsoftware.eventnotifications.JAMFEventNotificationMonitorResponse;
import com.jamfsoftware.eventnotifications.JAMFEventNotificationParameter;
import com.jamfsoftware.eventnotifications.JAMFEventNotificationParameter.InfoMapKeys;
import com.jamfsoftware.eventnotifications.events.EventType;
import com.jamfsoftware.eventnotifications.events.EventType.EventTypeIdentifier;
import com.jamfsoftware.eventnotifications.shellobjects.MobileDeviceEventShell;

public class CatchMobileDeviceEnroll implements JAMFEventNotificationMonitor {

	@Override
	public JAMFEventNotificationMonitorResponse eventOccurred(JAMFEventNotificationParameter param) {
		JAMFEventNotificationMonitorResponse response = new JAMFEventNotificationMonitorResponse(this);

		executeScript(param);
		
		return response;
	}
	
	@Override
	 public boolean isRegisteredForEvent(EventTypeIdentifier e) {
		if(e == EventType.EventTypeIdentifier.MobileDeviceEnrolled) {
			return true;
		}else{
			return true;
		}
	}
	
	public void executeScript(JAMFEventNotificationParameter event) {
		
		//	Write to log any event

		writeEventToLog(event.getEventType(), event.getInfoMap());
		
		if ( event.getEventType().getIdentifier() == EventType.EventTypeIdentifier.MobileDeviceEnrolled ) {
			
			MobileDeviceEventShell enrollmentObject = (MobileDeviceEventShell)event.getEventObject();
			
							
			//	Get the info we need from the Events API

			String deviceUDID = enrollmentObject.getUdid();
			String deviceSerial = enrollmentObject.getSerialNumber();
			String userDirectoryID = enrollmentObject.getUserDirectoryID();
			
			writeToLog("So i am gonna call script with this\n");
			writeToLog("device UDID: "+ deviceUDID+ "\n");
			writeToLog("deviceSerial: "+ deviceSerial + "\n");
			writeToLog("userDirectoryID: "+ userDirectoryID+ "\n");
			
			ProcessBuilder pb = new ProcessBuilder("/usr/bin/python", "/opt/scripts/mobile_device_enrolled.py", deviceUDID, deviceSerial, userDirectoryID );
			
			pb.directory(new File("/usr/bin/"));
			try {
				Process p = pb.start();
			} catch (IOException e) {
				writeToLog(e.getMessage());
			}
		
		}
		
		
		
	}
	
	public void writeEventToLog(EventType e, HashMap<Object, Object> hm){

		writeToLog("\nNew Event - " + new Date() + "\n----------\nEvent Type:\n" + e + "\n----------\nEvent Type Identifier:\n" + e.getIdentifier() + "\nEventObject:\n" +
				   e.getEventObject() + "\nJSSObject:\n" + hm.get(InfoMapKeys.JSSObject) + "\n----------\n");
	
	}
	
	private void writeToLog(String s) {
		try{
			FileOutputStream fstream = new FileOutputStream("/var/log/AllJAMFEvents.log", true);
			fstream.write(s.getBytes(Charset.forName("UTF-8")));
			fstream.close();
		}catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

	}
}
