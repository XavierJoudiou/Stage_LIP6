package helloWorld;

import java.util.ArrayList;
import java.util.Random;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

public class HelloWorld implements EDProtocol {
    
	public final static int STOP = 0;
	public final static int E = 1;
	public final static int I = 2;
	public final static int T = 3;
	
    //identifiant de la couche transport
    private int transportPid;

    //objet couche transport
    private HWTransport transport;

    //identifiant de la couche courante (la couche applicative)
    private int mypid;

    //le numero de noeud
    private int nodeId;

    //prefixe de la couche (nom de la variable de protocole du fichier de config)
    private String prefix;
    
    //Un noeud a une liste de voisins (2 voisins pour le moment)
    private ArrayList<Integer> voisins;
    private int nbVoisins;
    
    //Etat du noeud
    private int Etat;
    
    private Random rand;
    
    private HelloWorld current;
    Node dest;
    

    public HelloWorld(String prefix) {
	this.prefix = prefix;
	//initialisation des identifiants a partir du fichier de configuration
	this.transportPid = Configuration.getPid(prefix + ".transport");
	this.mypid = Configuration.getPid(prefix + ".myself");
	this.transport = null;
	this.Etat = STOP;
	this.nbVoisins = 2;
	this.voisins = new ArrayList<Integer>();
    }
    
 

	//methode appelee lorsqu'un message est recu par le protocole HelloWorld du noeud
    public void processEvent( Node node, int pid, Object event ) {
	this.receive((Message)event);
    }
    
    //methode necessaire pour la creation du reseau (qui se fait par clonage d'un prototype)
    public Object clone() {

	HelloWorld dolly = new HelloWorld(this.prefix);

	return dolly;
    }

    //liaison entre un objet de la couche applicative et un 
    //objet de la couche transport situes sur le meme noeud
    public void setTransportLayer(int nodeId) {
    	this.nodeId = nodeId;
    	this.transport = (HWTransport) Network.get(this.nodeId).getProtocol(this.transportPid);
    }

    //envoi d'un message (l'envoi se fait via la couche transport)
    public void send(Message msg, Node dest) {
    	if (msg.getType() == 0){
    		this.transport.send(getMyNode(), dest, msg, this.mypid);
    	}
    	if (msg.getType() == 1){
    		this.transport.send_mouv(getMyNode(), dest, msg, this.mypid);
    	}
    	if (msg.getType() == 2){
    		this.transport.send_mouv(getMyNode(), dest, msg, this.mypid);
    	}
    	if (msg.getType() == 3){
    		this.transport.send_mouv(getMyNode(), dest, msg, this.mypid);
    	}
    }

    //affichage a la reception
    private void receive(Message msg) {
	//System.out.println(this + ": Received " + msg.getContent());
		if ( msg.getType() == 0){
			//System.out.println(this + ": Message initialisation");
			//System.out.println(this + ": Mon nombre de voisins est de: " + this.nbVoisins);
			switch(this.nodeId){
			case 1 : 
				this.voisins.add((Integer) (Network.size() - 1) );
				this.voisins.add((Integer) (this.nodeId + 1) );
				break;
			case 9 : 
				this.voisins.add((Integer) (this.nodeId - 1) );
				this.voisins.add((Integer) (1) );
				break;
			default :
				this.voisins.add((Integer) (this.nodeId - 1) );
				this.voisins.add((Integer) (this.nodeId + 1) );
				break;
			}
			//System.out.println(this + ": Mon Premier voisin est: " + this.voisins.get(0));		
			//System.out.println(this + ": Mon Deuxième voisin est: " + this.voisins.get(1));

		}
		if ( msg.getType() == 1){
			
			Message mouve;
			mouve = new Message(Message.FORCE,"Tu vas bouger enfoiré !!",this.nodeId);
			
			
			System.out.println(this + ": On va bouger !!!");
			this.Etat = I;
			rand = new Random();
			int direction = (int)rand.nextInt(2);
			
			if ( direction == 0){
				mouve.setInfo(this.voisins.get(1));
				System.out.println(this + ": La direction est: " + direction + " On se dirige vers " + this.voisins.get(0));	
				this.send(mouve, Network.get(this.voisins.get(0)));
				
			}else{
				mouve.setInfo(this.voisins.get(0));
				System.out.println(this + ": La direction est: " + direction + " On se dirige vers " + this.voisins.get(1));	
				this.send(mouve, Network.get(this.voisins.get(1)));
			}
			
		}
		if ( msg.getType() == 2){
			System.out.println(this + ": Message de type FORCE reçu de la part de " + msg.getEmitter());
			//System.out.println(this + ": Mon nouveau voisin est " + msg.getInfo());
			this.echange_voisins(msg.getEmitter(),msg.getInfo());
		}
		if ( msg.getType() == 3){
//			System.out.println(this + ": Message de type FORCEREP reçu de la part de " + msg.getEmitter());
//			System.out.println(this + ": Mon nouveau voisin est " + msg.getInfo());
			this.echange_voisins2(msg.getEmitter(),msg.getInfo());
			this.Etat=E;
			System.out.println(this + ": Je suis en état E");
			Message bouge = new Message(Message.MOVE,"Bouge!!",this.nodeId);
			this.send(bouge, Network.get(this.nodeId));
		}
    }

    //retourne le noeud courant
    private Node getMyNode() {
	return Network.get(this.nodeId);
    }
    
    private void echange_voisins(int emetteur, int nouveau){
    	
    int i = 0;
    	for (i=0; i<nbVoisins; i++){
    		if ( this.voisins.get(i) == emetteur ){
    			this.voisins.set(i,nouveau);
    			int index = ((i + 1) % 2);
    			
//    			System.out.println("voisins " + index + " nom: " + this.voisins.get(index));
    			Message mouvMsg;
    			mouvMsg = new Message(Message.FORCEREP,"MAj voisins",this.nodeId);
    			mouvMsg.setInfo(this.voisins.get(index));
    			this.send(mouvMsg, Network.get(emetteur));
    			
    			Message majMsg;
    			majMsg = new Message(Message.MAJ,"Maj",this.nodeId);
    			
    			this.voisins.set( index, emetteur); 
//    			System.out.println("voisins " + index + " nom: " + this.voisins.get(index));
    			System.out.println(this + ": Mes nouveaux voisins assignés sont: " + this.voisins.get(0) + " et " + this.voisins.get(1));
    			break;
    		}
    	}
    	System.out.println("ERROR");
    }
    
    private void echange_voisins2(int emetteur, int nouveau){
    	
        int i = 0;
        	for (i=0; i<nbVoisins; i++){
        		if ( this.voisins.get(i) == emetteur ){
        			this.voisins.set(i,nouveau);
        			int index = ((i + 1) % 2);
        			        			
        			this.voisins.set( index, emetteur); 
//        			System.out.println("voisins " + index + " nom: " + this.voisins.get(index));
        			System.out.println(this + ": Mes nouveaux voisins assignés sont: " + this.voisins.get(0) + " et " + this.voisins.get(1));
        			break;
        		}
        	}
        	
        }

    public String toString() {
	return "Node "+ this.nodeId;
    }
    
    

    public ArrayList<Integer> getVoisins() {
		return voisins;
	}



	public void setVoisins(ArrayList<Integer> voisins) {
		this.voisins = voisins;
	}



	public int getNbVoisins() {
		return nbVoisins;
	}



	public void setNbVoisins(int nbVoisins) {
		this.nbVoisins = nbVoisins;
	}



    
}