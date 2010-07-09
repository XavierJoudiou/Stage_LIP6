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
	
	public Object getContent() {
		return this.body;
	}
	
	public Object setContent() {
		return this.body;
	}
	
}
