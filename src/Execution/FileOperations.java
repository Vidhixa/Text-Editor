package Execution;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import Objects.UserAction;

/**
 * This class will create commonFile and help implement insert, replace and delete functionality.
 * Typically, every time when user action is received, we will locate the line of change and copy until 
 * that into a temporary file. Changes will be made at given line and written into temporary file. Then remainder of
 * the file will be copied into temporary and this file will renamed to commonfile, deleting the older version.
 * 
 * @author vidhixa
 *
 */
public class FileOperations {

	private static FileOperations fileInstance;
	private File commonFile;
	private File tempFile;
	private BufferedReader reader;
	private BufferedWriter writer;

	/**
	 * Constructor will help in creating a single instance of commonFile
	 * 
	 */
	private FileOperations() {
		try {
			commonFile = new File("commonFile.txt");
			if(!commonFile.exists()) {
				//Creating new file
				commonFile.createNewFile();
			} else {
				//If same name file exists then this will delete and recreate a new file of same name
				//This is can be commented out based on need for feature
				commonFile.delete();
				commonFile.createNewFile();				
			}
		} catch (IOException e) {
			System.err.println("Error occured while creating common file "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Helps implement singleton object creation of FileOperations class
	 * 
	 * @return fileInstance
	 * @throws IOException
	 */
	public static FileOperations getInstance() throws IOException {
		if(fileInstance == null) {
			synchronized (FileOperations.class) {
				if(fileInstance == null) {
					fileInstance = new FileOperations();
				}				
			}		
		}
		return fileInstance;
	}

	
	/**
	 * This method will insert given text on given line at given position 
	 * 
	 * Assumption: If user creates text containing Spaces and new line '\n' then those characters will come as part of text.
	 * Also, the line number and character number are accurate to user activity. Given insert will be for continuous position
	 * 
	 * Cases tested with:
	 * 1. Initial write to file
	 * 2. Writing between words in one line
	 * 3. Appending at end of line
	 * 3. Writing over several lines
	 * 4. Writing in middle of blank file
	 * 
	 * @param action
	 */
	public void insertText(UserAction action) {
		try{
			tempFile = new File("temp.txt");
			writer =  new BufferedWriter(new FileWriter(tempFile));
			reader = new BufferedReader(new FileReader(commonFile));
			String text = action.getText();
			
			OperationsHelper.copyUntilChangeLines(action.getLine(), reader, writer);
			
			String line;
			if((line = reader.readLine()) != null) {
				//Appending/Inserting to given line
				line = line.substring(0, action.getPosition()) + text + line.substring(action.getPosition());
				writer.write(line+"\n");
				OperationsHelper.copyRemainingLines(reader, writer);
				
			} else {
				//Writing on lines beyond end of original file
				/*String blanks = new String(new char[insert.getPosition()]).replace('\0', ' ');
				line = blanks + insert.getText();	*/
				writer.write(action.getText()+"\n");
			}
		
			writer.close();
			reader.close();
			OperationsHelper.replaceFile(commonFile, tempFile);
			
		} catch(Exception e) {
			System.err.println("Exception occured while deleting text file :: "+e.getMessage());
			e.getStackTrace();
		}
	}

	/**
	 * This method will replace text on given line and position by new text 
	 * 
	 * Assumption: If user replaces text containing Spaces and new line '\n' then those characters will come as part of text.
	 * Also, the line number and character number for replace text and old text should be accurate to user activity. Given replace 
	 * text will be written in continuous position. Sizes of old text and replace text can be different but they start at same location.
	 * 
	 * Cases tested with:
	 * 1. Replace text and old text are same length
	 * 2. Both unequal length
	 * 3. Old text to be replaced goes over multiple lines, replace text on just one
	 * 4. Old text to be replaced goes over multiple lines, replace text too
	 * 5. Old text to be replaced is on single line, replace text goes on multiple
	 * 6. Both are on same line
	 * 
	 * @param action
	 */
	public void replaceText(UserAction action) {
		try{
			tempFile = new File("temp.txt");
			writer =  new BufferedWriter(new FileWriter(tempFile));
			reader = new BufferedReader(new FileReader(commonFile));
			String replaceText = action.getReplaceText();
			String oldText = action.getText();

			OperationsHelper.copyUntilChangeLines(action.getLine(), reader, writer);
				
			String line;
			if((line = reader.readLine()) != null) {
				String stringArray[] = oldText.split("\n");
				//If old text is just on one line then simple remove it and insert replace text
				if(stringArray.length == 1) {
					if((action.getPosition()+replaceText.length()) > line.length()){
						//Replace text is longer than old one
						line = line.substring(0, action.getPosition())+ replaceText;
					} else {
						//Replace text is shorter than old one
						line = line.substring(0, action.getPosition())+ replaceText + line.substring(action.getPosition()+replaceText.length());
					}
					writer.write(line+"\n");

				} else {	
					//Old text goes over many lines, they need to be discarded
					line = line.substring(0, action.getPosition())+ replaceText;
					writer.write(line+"\n");
					
					//While deleting last line of old text, we might have to delete only few characters, 
					//thus separate condition inside for loop
					for(int i=1; i < stringArray.length;  i++) {
						line = reader.readLine();
						if(i == stringArray.length-1) {
							line = line.substring(stringArray[i].length());
							writer.write(line+"\n");
						}
					}
				}							
				OperationsHelper.copyRemainingLines(reader, writer);				

			} else {
				throw new Exception("Replace action came for a non-existing line");
			}
			
			writer.close();
			reader.close();
			OperationsHelper.replaceFile(commonFile, tempFile);
			
		} catch(Exception e) {
			System.err.println("Exception occured while deleting text file :: "+e.getMessage());
			e.getStackTrace();
		}
	}
	

	/**
	 * This method will delete text from give line and position 
	 * 
	 * Assumption: If user deletes text containing Spaces and new line '\n' then those characters will come as part of text.
	 * Also, the line number and character number for delete text should be accurate to user activity. Delete text will 
	 * be in continuous position. 
	 * 
	 * Cases tested with:
	 * 1. Deletion from start of file
	 * 2. Deletion of words in line
	 * 3. Deletion of one line
	 * 3. Deletion over several lines
	 * 
	 * @param action
	 */
	public void deleteText(UserAction action) {
		try{
			tempFile = new File("temp.txt");
			writer =  new BufferedWriter(new FileWriter(tempFile));
			reader = new BufferedReader(new FileReader(commonFile));
			String text = action.getText();

			OperationsHelper.copyUntilChangeLines(action.getLine(), reader, writer);
			
			String line;
			if((line = reader.readLine()) != null) {
				String stringArray[] = text.split("\n");				
				
				//Deleting from single line
				if(stringArray.length == 1) {
					line = line.substring(0, action.getPosition())+ line.substring(action.getPosition()+text.length());
					writer.write(line+"\n");
				} else {	
					//Deleting from multiple lines
					//While deleting first and last line, we might have to delete only few characters, thus separate evaluation below
					line = line.substring(0, action.getPosition());
					if(!line.isEmpty()) {
						writer.write(line+"\n");
					}					
					for(int i=1; i < stringArray.length;  i++) {
						line = reader.readLine();
						if(i == stringArray.length-1) {
							line = line.substring(stringArray[i].length());
							writer.write(line+"\n");
						}
					}
				}	
				
			} else {
				throw new Exception("Can't perform Delete operation for a non-existing line");
			}
			OperationsHelper.copyRemainingLines(reader, writer);
			
			writer.close();
			reader.close();
			OperationsHelper.replaceFile(commonFile, tempFile);
			
		} catch(Exception e) {
			System.err.println("Exception occured while deleting text file :: "+e.getMessage());
			e.getStackTrace();
		}
	}
	
	
	/**
	 * This method will display file every time after it is modified
	 * 
	 */
	public void viewText() {
		String line;	
		try {
			reader = new BufferedReader(new FileReader(commonFile));	
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println("*****************End of File**********************");
			reader.close();			
		} catch (Exception e) {
			System.err.println("Exception occured while viewing text file :: "+e.getMessage());
			e.getStackTrace();
		}
	}

}
