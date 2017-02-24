package tcc.favorite.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name ="Favorite")
public class FavoriteModel {
	private String location;
	private String isFavorite;
	private String wsmlEntity;
	private String wsmlCode;
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getIsFavorite() {
		return isFavorite;
	}
	
	public void setIsFavorite(String isFavorite) {
		this.isFavorite = isFavorite;
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
