package helloWorld;

import peersim.edsim.*;

public class Message {

    public final static int HELLOWORLD = 0;
    public final static int MOVE = 1;
    public final static int FORCE = 2;
    public final static int FORCEREP = 3;
    public final static int MAJ = 4;
    public final static int DETEC = 5;

    private int type;
    private String content;
    private int emitter;
    private int info;

    Message(int type, String content,int emitter) {
	this.type = type;
	this.content = content;
	this.emitter = emitter;
    }

    public String getContent() {
	return this.content;
    }

    public int getType() {
	return this.type;
    }

	public void setEmitter(int emitter) {
		this.emitter = emitter;
	}

	public int getEmitter() {
		return emitter;
	}

	public void setInfo(int info) {
		this.info = info;
	}

	public int getInfo() {
		return info;
	}
    
}