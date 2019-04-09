package comp1206.sushi.common;

public class MessageWithAttachement extends Message {
	Object attachement;
	
	public MessageWithAttachement() {
		super();
		this.attachement = null;
	}
	
	public MessageWithAttachement(String contents, Object attachement) {
		super(contents);
		this.attachement = attachement;
	}
	
	public Object getAttachement() {
		return attachement;
	}
	public void setAttachement (Object attachement){
		this.setAttachement(attachement);
	}

}
