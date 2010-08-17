public void processPrefetchMsg(Message msg) {
    ...
    if (!this.protocol.hasNeighbor(prefetch.getSource().getId())) {
         this.sendPrefetchConnectMessage(prefetch.getSource());
    }
    if (ttl > 0) {
      if (this.isFarEnough(prefetch)) { 
          destinations = this.choosePrefetchDestinations(prefetch);
	  size = destinations.size();
	  size = (size > ttl)?ttl:size;
	  msg.setTtl(ttl - size);
	  if (size > 0) {
	    this.protocol.send(msg, this.proxies.get(destinations.get(0)));				
	    for (int i = 1; i < size; i++) {						
	       if (this.protocol.getPrefetch_ameliore() == 1){
	           if (isGoodPrefetch(...) || isGoodDirection(...) ||
		   isMaybeGoodPrefetch(...) || this...getState() != TRAVELLING ){
			   
		        propagateMsg = this.protocol.createFoundMsg(...);
			this.protocol.send(propagateMsg, ...);
		    }
	        }else{

		    propagateMsg = this.protocol.createFoundMsg(...);
		    this.protocol.send(propagateMsg, ...);
		}
             }	
     ...		
}
