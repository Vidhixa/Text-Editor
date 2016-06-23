package Main;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import Execution.FileOperations;
import Objects.Actions;
import Objects.UserAction;

/**
 * This is the main class that will initialize singleton object for file, trigger the producer and consumer class 
 * to generate actions(Similar to how user would do in real-time) and invoke corresponding file edit methods.
 * 
 * Here, I am assuming that the code will receive user input details such as like line number, position on line,
 * action performed and text. The code supports file operations like add text, replace(requires both replaced and old text) text
 * and delete text
 * 
 * Few things that would enhance the code will be automatic justification of text, more optimized way for finding edit location 
 * or more advanced features like spellcheck prompt, etc.
 *  
 * @author vidhixa
 *
 */
public class TextEditor {

	public static void main(String[] args) throws IOException {
		FileOperations commonFile = FileOperations.getInstance();
		BlockingQueue<UserAction> queue = new ArrayBlockingQueue<UserAction>(1000);

		ActionProducer p = new ActionProducer(queue);
		ActionConsumer c = new ActionConsumer(queue, commonFile);		
		p.run();
		c.run();
	}
}

/**
 * This class upon thread starting will insert user actions into blocking queue. 
 *
 */
class ActionProducer implements Runnable {

	public BlockingQueue<UserAction> queue;

	public ActionProducer(BlockingQueue<UserAction> queue) {
		this.queue = queue;
	}

	@Override
	public void run() { 
		UserAction ua1 = new UserAction(1, 0,"insert","vidhixa");
		UserAction ua2 = new UserAction(1, 3,"replace","hixa"," Joshi");
		UserAction ua3 = new UserAction(2, 0,"insert","the\ncat");
		UserAction ua4 = new UserAction(2, 3,"replace","\ncat"," happy");
		UserAction ua5 = new UserAction(2, 0, "delete", "the ");
		actionsReceived(ua1);
		actionsReceived(ua2);
		actionsReceived(ua3);
		actionsReceived(ua4);
		actionsReceived(ua5);		   
	}

	public void actionsReceived(UserAction ua) {
		if(ua != null)  {
			queue.add(ua);
		}
	}
}

/**
 * This class upon thread starting will read user actions from blocking queue and call correspoding file edit method 
 *
 */
class ActionConsumer implements Runnable {

	public BlockingQueue<UserAction> queue;
	public FileOperations commonFile;

	public ActionConsumer(BlockingQueue<UserAction> queue, FileOperations commonFile) {
		this.queue = queue;
		this.commonFile = commonFile;
	}

	@Override
	public void run() { 			
		while(!queue.isEmpty()) {
			UserAction ua = queue.poll();
			String action = ua.getAction();

			//Based on action received, one of the three operations will be performed
			if(action.equalsIgnoreCase(Actions.INSERT.toString())){
				commonFile.insertText(ua);

			} else if(action.equalsIgnoreCase(Actions.REPLACE.toString())) {
				commonFile.replaceText(ua);

			} else if(action.equalsIgnoreCase(Actions.DELETE.toString())) {
				commonFile.deleteText(ua);
			} 
			commonFile.viewText();
		}	   
	}
}
