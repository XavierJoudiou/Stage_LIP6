package peersim.solipsis;

public class Message {
	
	public final static int FIND_NEAREST = 0;
	public final static int BEST         = 1;
	public final static int QUERYAROUND  = 2;
	public final static int AROUND       = 3;
	public final static int ALERT		 = 4;
	public final static int HELLO		 = 5;
	public final static int CONNECT	     = 6;
	public final static int CLOSE		 = 7;
	public final static int HEARTBEAT    = 8;
	public final static int DELTA	     = 9;
	public final static int DETECT	     = 10;
	public final static int SEARCH	     = 11;
	public final static int FOUND	     = 12;
	public final static int LOOKUP_REPLY = 15;
	public final static int LOOKED_UP	 = 16;
	
	public final static int PREFETCH	 = 13;
	public final static int SMALLWORLD   = 14;
	public static final int LONG_RANGE_ACK = 17;
	public static final int LONG_RANGE_REQ = 18;
	
	public final static int CACHE_UPD 		= 21;
	public final static int CACHE_UPD_REP 	= 22;
	
	private int messageType;
	private int from;
	private int to;
	private Object body;
	private int ttl;
	private long timestamp;
	
	/*optimization*/
	private int originAddress;
	
	public Message(int messageType, int fromAddress, int from, int to, Object body) {
		this.messageType = messageType;
		this.from = from;
		this.to = to;
		this.body = body;
		this.from = from;
		this.to = to;
		this.body = body;
		this.originAddress = fromAddress;
		this.ttl = Integer.MAX_VALUE;
		this.timestamp = 0;
	}
	
	public void setTimestamp(long value) {
		this.timestamp = value;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
	
	public Message(int messageType, int fromAddress, int from, int to, int ttl, Object body) {
		this(messageType, fromAddress, from, to, body);
		this.ttl = ttl;
	}
	
	@Override
	public Message clone() {
		Message dolly = new Message(this.messageType,this.originAddress,this.from,this.to,this.ttl,this.body);
		return dolly;
	}
	
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	
	public int getTtl() {
		return this.ttl;
	}

	public int getSource() {
		return this.from;
	}
	
	public int getOriginAddress() {
		return this.originAddress;
	}
	
	public int getDestination() {
		return this.to;
	}
	
	public void setDestination(int to) {
		this.to = to;
	}
	
	public int getType() {
		return this.messageType;
	}
	
	public String getTypeString(){
		String res;
		switch(this.messageType){
		case 0:
			res = "FIND_NEAREST";
			break;
		case 1:
			res = "BEST";
			break;
		case 2:
			res = "QUERYAROUND";
			break;
		case 3:
			res = "AROUND";
			break;
		case 4:
			res = "ALERT";
			break;
		case 5:
			res = "HELLO";
			break;
		case 6:
			res = "CONNECT";
			break;
		case 7:
			res = "CLOSE";
			break;
		case 8:
			res = "HEARTBEAT";
			break;
		case 9:
			res = "DELTA";
			break;
		case 10:
			res = "DETECT";
			break;
		case 11:
			res = "SEARCH";
			break;
		case 12:
			res = "FOUND";
			break;
		case 13:
			res = "PREFETCH";
			break;
		case 14:
			res = "SMALLWORLD";
			break;
		case 15:
			res = "LOOKUP_REPLY";
			break;
		case 16:
			res = "LOOKED_UP";
			break;
		case 17:
			res = "LONG_RANGE_ACK";
			break;
		case 18:
			res = "LONG_RANGE_REQ";
			break;
		case 21:
			res = "CACHE_UPD";
			break;
		case 22:
			res = "CACHE_UPD_REP";
			break;
		default:
			res = "" + this.messageType;
			break;
		}
		return res;
	}
	
	public Object getContent() {
		return this.body;
	}
	
	public Object setContent() {
		return this.body;
	}

	@Override
	public String toString() {
		return "Message [body=" + body + ", from=" + from + ", messageType="
				+ messageType + ", originAddress=" + originAddress
				+ ", timestamp=" + timestamp + ", to=" + to + ", ttl=" + ttl
				+ "]";
	}
	
	
	
}
