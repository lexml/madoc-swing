package br.gov.lexml.madoc.schema.entity;

import java.io.InputStream;

public class ResourceEntity {

	private final InputStream inputStream;
	private final String mimeType;
	private final String suffix;
	
	public ResourceEntity(InputStream inputStream, String mimeType, String suffix){
		this.inputStream = inputStream;
		this.mimeType = mimeType;
		this.suffix = suffix;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public InputStream getInputStream(){
		return inputStream;
	}

}
