package com.main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Function {

	
	final static String CLOSE="CLOSE",REFRESH="REFRESH",NEXT="NEXT",LAST="LAST",ENTER="ENTER",CHANGE="CHANGE";
	
	static Robot robot = null;
	
	static {
		try {
			robot= new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public static void exec(String cmd) {
		if(cmd.equals(CLOSE)) {
			close();
		}else if(cmd.equals(REFRESH)) {
			refresh();
		}else if(cmd.equals(NEXT)) {
			next();
		}else if(cmd.equals(LAST)) {
			last();
		}else if(cmd.equals(ENTER)) {
			enter();
		}else if(cmd.equals(CHANGE)) {
			change();
		}
	}
	
	static void pressKey(int...args) {
		for(int keyCode:args) {
			robot.keyPress(keyCode);
		}
	}
	
	static void releaseKey(int...args) {
		for(int keyCode:args) {
		robot.keyRelease(keyCode);
		}
	}
	
	public static void pressAndReleaseKey(int...args) {
		for(int keyCode:args) {
			pressKey(keyCode);
		}
		for(int keyCode:args) {
			releaseKey(keyCode);
		}
	}
	
	public static void close() {
		pressAndReleaseKey(KeyEvent.VK_ALT,KeyEvent.VK_F4);
	}
	
	public static void change() {
		pressKey(KeyEvent.VK_ALT);
		pressAndReleaseKey(KeyEvent.VK_TAB);
	}
	
	public static void refresh() {
		pressAndReleaseKey(KeyEvent.VK_F5);
	}
	
	public static void next() {
		pressAndReleaseKey(KeyEvent.VK_RIGHT);
		pressAndReleaseKey(KeyEvent.VK_ALT,KeyEvent.VK_RIGHT);
	}
	
	public static void last() {
		pressAndReleaseKey(KeyEvent.VK_LEFT);
		pressAndReleaseKey(KeyEvent.VK_ALT,KeyEvent.VK_LEFT);
	}
	
	public static void enter() {
		pressAndReleaseKey(KeyEvent.VK_ENTER);
	}
	
	public static void moveMouse(int x,int y) {
		robot.mouseMove(x, y);
	}
	
	public static void mousePress(int buttons) {
		robot.mousePress(buttons);
	}
	
	public static void mouseRelease(int buttons) {
		robot.mouseRelease(buttons);
	}
}
