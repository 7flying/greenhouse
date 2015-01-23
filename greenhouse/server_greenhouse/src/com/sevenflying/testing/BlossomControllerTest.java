package com.sevenflying.testing;

import java.util.Scanner;

import com.sevenflying.server.communicator.BlossomController;


public class BlossomControllerTest {

	public static void main(String[] args) {

		Scanner sca = new Scanner(System.in);
		BlossomController handler = BlossomController.getInstance("COM5");
		handler.setDebugMode(true);
		// Simple echo to see that this works
		String toWrite = sca.nextLine();
		while(!toWrite.equals("E")) {
			System.out.println("Echo: '" + toWrite + "'");
			handler.sendDataTESTINGMETHOD(toWrite);
			//handler.communicator.sendData("helrlo\n");
			toWrite = sca.nextLine();
		}
		sca.close();
		handler.close();
	}

	
}
