package ptwop.common.gui;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class Notification {
	public static void displayNotif(String title, String text) {
		if(! SystemTray.isSupported())
			System.err.println("System tray not supported, can't notify");
		
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        
        //Set tooltip text for the tray icon
        // trayIcon.setToolTip("System tray icon demo");
        
        try {
			tray.add(trayIcon);
	        trayIcon.displayMessage(title, text, MessageType.INFO);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}
