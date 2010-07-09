package peersim.solipsis;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import peersim.solipsis.Globals;
import peersim.solipsis.NeighborProxy;
import peersim.solipsis.VirtualEntity;
import peersim.solipsis.VirtualWorldDistribution;

public class VirtualWorldRecorder {
	
	private FileOutputStream file;
	private String filename;
	
	VirtualWorldRecorder() {
		this("defaultRecord.txt");
	}
	
	VirtualWorldRecorder(String filename) {
		try {
			this.filename = filename;
			this.file = new FileOutputStream(this.filename);
			this.write(""+Globals.distribution.getMapSize());
			this.writeEndTick();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void record() {
		VirtualWorldDistribution distribution = Globals.distribution;
		HashMap<Integer,VirtualEntity> map = distribution.getDistribution();
		Iterator it = map.entrySet().iterator();
		VirtualEntity entity;
		
		try {
//			this.writeBeginTick();
			while(it.hasNext()) {
				entity = (VirtualEntity)((Map.Entry)it.next()).getValue();
				this.write(this.virtualEntityToLine(entity)+"\n");
			}
			this.writeEndTick();
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private String virtualEntityToLine(VirtualEntity entity) {
		String line = entity.getId()+
					":"+entity.getKnowledgeRay() +
					":"+entity.getCoord()[0] +
					":"+entity.getCoord()[1] + 
					":"+((entity.isStabilized())?1:0) + ":";
		Iterator neighbors = entity.getProtocol().getNeighbors();
		NeighborProxy current;
		
		while(neighbors.hasNext()) {
			current = (NeighborProxy)((Map.Entry)neighbors.next()).getValue();
			line += current.getId() + ":" + current.getQuality() + ":"; 
		}
		return line;
	}
	
	private void write(String line) throws IOException {
		file.write(line.getBytes());
	}
	
	private void writeBeginTick() throws IOException {
		file.write("*".getBytes());		
	}
	
	private void writeEndTick() throws IOException {
		file.write("*".getBytes());		
	}
}
