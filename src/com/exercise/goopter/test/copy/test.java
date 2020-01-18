package com.exercise.goopter.test.copy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class test {

	public static void main (String args[]) {
		try {
		      File myObj = new File("src/source-reference.jsx");
		      Scanner myReader = new Scanner(myObj,"utf-8");
		      int counter = 1;
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        System.out.println(counter + " " + data);
		        counter++;
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
}
