package tcc.sensor.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name ="Sensor")
public class SensorModel {

	private String nodeId;
	
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	@Override
	public String toString() {
		return nodeId;
	}
}
