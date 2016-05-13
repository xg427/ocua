/**
 * 
 */
package org.devgateway.toolkit.persistence.mongo.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.index.Indexed;

public abstract class Location<T extends GeoJson<?>> {

	@Id
	private Gazetteer gazetteer = new Gazetteer();

	@Indexed
	private String description;

	private String uri;

	public void setupGazetteer(String id) {
		getGazetteer().getIdentifiers().add(id);
		uri = getGazetteerPrefix() + id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Gazetteer getGazetteer() {
		return gazetteer;
	}

	public void setGazetteer(Gazetteer gazetteer) {
		this.gazetteer = gazetteer;
	}

	public abstract T getGeometry();

	public abstract void setGeometry(T geometry);

	public abstract String getGazetteerPrefix();
}
