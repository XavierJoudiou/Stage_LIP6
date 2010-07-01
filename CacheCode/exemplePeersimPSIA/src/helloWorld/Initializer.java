package helloWorld;

import java.util.Random;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

/*
  Module d'initialisation de helloWorld: 
  Fonctionnement:
    pour chaque noeud, le module fait le lien entre la couche transport et la couche applicative
    ensuite, il fait envoyer au noeud 0 un message "Hello" a tous les autres noeuds
 */
public class Initializer implements peersim.core.Control {
    
    private int helloWorldPid;
    private Random rand;
    
    public Initializer(String prefix) {
	//recuperation du pid de la couche applicative
	this.helloWorldPid = Configuration.getPid(prefix + ".helloWorldProtocolPid");
    }

    public boolean execute() {
	int nodeNb;
	HelloWorld emitter, current;
	Node dest;
	Message helloMsg,mouvMsg;

	//recuperation de la taille du reseau
	nodeNb = Network.size();
	//creation du message
	helloMsg = new Message(Message.HELLOWORLD,"Hello!!", 0);
	if (nodeNb < 1) {
	    System.err.println("Network size is not positive");
	    System.exit(1);
	}
	
	mouvMsg = new Message(Message.MOVE,"Bouge!!",0);

	//recuperation de la couche applicative de l'emetteur (le noeud 0)
	emitter = (HelloWorld)Network.get(0).getProtocol(this.helloWorldPid);
	emitter.setTransportLayer(0);

	//pour chaque noeud, on fait le lien entre la couche applicative et la couche transport
	//puis on fait envoyer au noeud 0 un message "Hello"
	for (int i = 1; i < nodeNb; i++) {
	    dest = Network.get(i);
	    current = (HelloWorld)dest.getProtocol(this.helloWorldPid);
	    current.setTransportLayer(i);
	    emitter.send(helloMsg, dest);
	}
	
	int mouv_node;
	rand = new Random();
	mouv_node = (int) ( 1 + rand.nextInt(Network.size() - 1));
	System.out.println("Le noeud qui va bouger est: " + mouv_node);
	dest = Network.get(mouv_node);
	current = (HelloWorld)dest.getProtocol(this.helloWorldPid);
    current.setTransportLayer(mouv_node);
    emitter.send(mouvMsg, dest);
	
	
	System.out.println("Initialization completed");
	return false;
    }
}