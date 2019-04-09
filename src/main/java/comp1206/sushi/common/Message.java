package comp1206.sushi.common;

public class Message {
	private String contents;
	
	public Message() {
		this.contents="";
	}
	
	public Message(String contents) {
		this.contents = contents;
	}
	
	public String toString() {
		return contents;
	}
	
	public void setContents(String contents) {
		this.contents = contents;
	}
}
