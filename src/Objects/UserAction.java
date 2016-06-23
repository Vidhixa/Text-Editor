package Objects;

/**
 * This pojo class is used to capture all the details about users file edit action
 * I have intentionally not written setters because I don't want any external class to modify details.
 * Assumption is that in real-time this object will be send from the client side
 * 
 * @author vidhixa
 *
 */
public class UserAction {
	private int line;
	private int position;
	private String action;
	private String text;
	private String replaceText;
	
	public UserAction() {}
	
	public UserAction(int line, int position, String action, String text) {
		this.line = line;
		this.position = position;
		this.action = action;
		this.text = text;
	}
	
	public UserAction(int line, int position, String action, String text, String replaceText) {
		this.line = line;
		this.position = position;
		this.action = action;
		this.text = text;
		this.replaceText = replaceText;
	}
	
	public int getLine(){
		return line;
	}
	
	public int getPosition(){
		return position;
	}
	
	public String getAction(){
		return action;
	}
	
	public String getText(){
		return text;
	}
	
	public String getReplaceText(){
		return replaceText;
	}
}
