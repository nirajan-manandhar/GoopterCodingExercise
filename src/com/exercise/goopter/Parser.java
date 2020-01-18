package com.exercise.goopter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.*;
import java.util.HashMap;

public class Parser {

	/**
	 * Main Method
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<String, HashMap<String, String>> input = MapMaker("src/input-source.jsx");
		HashMap<String, HashMap<String, String>> source = MapMaker("src/source-reference.jsx");
	
		Compare(input, source);

		WriteFile(input);
		
		System.out.println("Success");
	}
	
	/**
	 * Compare method, takes two HashMaps (input, source), and replaces the existing input values with existing source values
	 * @param input HashMap that stores the nested key/value pairs from input-source.jsx file
	 * @param source  HashMap that stores the nested key/value pairs from source-reference.jsx file
	 */
	public static void Compare(HashMap<String, HashMap<String, String>> input, HashMap<String, HashMap<String, String>> source) {
		for (String category : input.keySet()) {
			HashMap<String, String> categoryData = input.get(category);
			
			for (String key : categoryData.keySet()) {
				if(source.containsKey(category)) {
					if(source.get(category).containsKey(key)) {
						String newValue = source.get(category).get(key);
						input.get(category).replace(key, newValue);
					}
				}
			}
		}
	}
	
	
	/**
	 * Writes the final formatted HashMap into the same format as the input files
	 * @param map The HashMap to be written in JSON format
	 */
	public static void WriteFile(HashMap<String, HashMap<String, String>> map) {
	
		String filename = "input-source-consolidated.jsx";
				
		try {
			FileWriter writer = new FileWriter("src/" + filename);
			writer.write("export default {\n");
			int keyCount = 0;
			int size = map.size();
			
			for (String category : map.keySet()) {
				keyCount++;
				writer.write("\t\"" + category + "\" : {\n");
				HashMap<String, String> categoryData = map.get(category);
				for (String key : categoryData.keySet()) {
					String value = categoryData.get(key);
					writer.write("\t\t\"" + key + "\" : \"" + value + "\",\n");
				}
				
				String end = keyCount == size ? "\t}\n": "\t},\n";
				writer.write(end);
				
			}
			writer.write("}");

			writer.close();
			System.out.println("Successfully wrote to the file.");
	    } catch (IOException e) {
	    	System.out.println("An error occurred.");
	    	e.printStackTrace();
	    }
		
	}
	
	/**
	 * MapMaker, reads a JSON file and converts it into a nested key/value HashMap structure
	 * @param location Location of the file to be read
	 * @return HashMap created from from input file
	 */
	public static HashMap<String, HashMap<String, String>> MapMaker(String location) {
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		try {
			File input = new File(location);
			Scanner scan = new Scanner(input);
			map = ParseData(scan);
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("The file cannot be found");
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**
	 * ParseData, method that uses Regex to convert JSON into Key/Value pairs, put into a HashMap structure
	 * @param scan Scanner for an input file
	 * @return HashMap is the converted structure from JSON file
	 */
	public static HashMap<String, HashMap<String, String>> ParseData(Scanner scan) {
		
		HashMap<String, HashMap<String, String>> nest0 = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> nest1 = null;
		String rootKey = null;
		String startPattern = ": \\{";
		String endPattern = " \\}";
		String eofPattern = "\\}$";
		
		Pattern pStart = Pattern.compile(startPattern);
		Pattern pEnd = Pattern.compile(endPattern);
		Pattern pEof = Pattern.compile(eofPattern);
		Pattern pKey = Pattern.compile("\"([^\"]*)\":");
		Pattern pValue = Pattern.compile(": \"([^\"]*)\"");
		
		
		while (scan.hasNextLine()) {
			String data = scan.nextLine();
	        
	        Matcher mStart = pStart.matcher(data);
			Matcher mEnd = pEnd.matcher(data);
			Matcher mEof = pEof.matcher(data);
			Matcher mKey = pKey.matcher(data);;
			Matcher mValue = pValue.matcher(data);
			
			//Conditional for start of a category
			if (mStart.find() && mKey.find()) {
				nest1 = new HashMap<String, String>();
				rootKey = mKey.group(1);
			} 
			
			//Conditional for if key/value pair found
			if (mKey.find() && mValue.find()) {
				nest1.put(mKey.group(1), mValue.group(1));
			}
			
			//Conditional for EOF found
			if (mEof.find() && rootKey == null) {
				return nest0;
			} 
			
			//Conditional for end of a category
			if (mEnd.find()) {
				nest0.put(rootKey, nest1);
				rootKey = null;
			} 
		}
		return null;
	}

}
