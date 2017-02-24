package tcc.route.models;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name ="Route")
public class RouteModel {
	private int startNode;
	private int endNode;
	private String wsmlEntity;
	private String wsmlCode;
	
	
	public RouteModel(){}
	
	public int getStartNode() {
		return startNode;
	}
	
	public void setStartNode(int startNode) {
		this.startNode = startNode;
	}
	
	public int getEndNode() {
		return endNode;
	}
	
	public void setEndNode(int endNode) {
		this.endNode = endNode;
	}
	
	public String getWsmlEntity() {
		return wsmlEntity;
	}


	public void setWsmlEntity(String wsmlEntity) {
		this.wsmlEntity = wsmlEntity;
	}


	public String getWsmlCode() {
		return wsmlCode;
	}


	public void setWsmlCode(String wsmlCode) {
		this.wsmlCode = wsmlCode;
	}


	@Override
	public String toString() {
		return wsmlEntity;
	}
}
