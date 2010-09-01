public int maintainCacheTopology() {
  ...
  if ( this.mainVirtualEntity.getStateMachine().getState() == WANDERING ) {
    switch (this.strategieCache) {
    case SolipsisProtocol.FIFO:
      neighbor = cache.searchCacheNeighborKnoledgeRay(...,this.knowledgeRay);
    if (neighbor != null){
      if ( neighbor.getTime() + time_limite > CommonState.getIntTime()){
        cache.RmCache(neighbor);
	addLocalView(neighbor);
	return 1;
      }
      if (contact_node == 1){
	...
	cache_request = new CacheRequest(neigh,source);
	cache_test = new Message(Message.CACHE_UPD, this.getPeersimNodeId(), 
	  this.mainVirtualEntity.getId(), neighbor.getId(), cache_request);
	neighbor.setQuality(NeighborProxy.CACHED);
	this.send(cache_test,neighbor);
	return 0;
      }else{
	return 0;
      }
    }else{
      return 0;
    }
    case SolipsisProtocol.FIFOMULT:
      ...			
  }
}
