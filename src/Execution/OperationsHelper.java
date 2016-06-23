package Execution;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

/**
 * This class contains methods commonly used while editing the file
 * 
 * @author vidhixa
 *
 */
public class OperationsHelper {
	
	/**
	 * Replaces outdated commonFile with updated temp file
	 **/
	public static void replaceFile(File commonFile, File tempFile) {		
		commonFile.delete();
		commonFile = new File("commonFile.txt");
		tempFile.renameTo(commonFile);
	}
	
	/**
	 * Copies from starting line until the change line has reached
	 */
	public static void copyUntilChangeLines(int changeLine, BufferedReader reader, BufferedWriter writer) {		
		int lineNo = 1;		
		String line;
		try {
			while(lineNo < changeLine) {
				if((line = reader.readLine()) == null) {
					writer.write("\n");
					++lineNo;
				} else {
					writer.write(line+"\n");
					++lineNo;					
				}
			}
		} catch (IOException e) {
			System.err.println("Error occured while coopying lined until change "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Copies remaining lines of the file after required operation is performed
	 */
	public static void copyRemainingLines(BufferedReader reader, BufferedWriter writer) {		
		String line;
		try {
			while((line = reader.readLine()) != null) {
				writer.write(line+"\n");
			} 
		} catch (IOException e) {
				System.err.println("Error occured while copying remaining lines "+e.getMessage());
				e.printStackTrace();
		}	
	}

}
