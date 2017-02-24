package tcc.models;

import javax.xml.bind.annotation.XmlRootElement;


//@XmlRootElement(name ="Service")
@XmlRootElement(name ="ShoppingResource")
public class ResourceModel {
	private String wsmlEntity;
	private String wsmlCode;
	

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
