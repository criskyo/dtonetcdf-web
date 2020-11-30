package com.dtonetcdf.entity;

import java.io.Serializable;

public class MyVariableDTO implements Serializable{
	
	private String name;
	private String descripcion;
	private String dataType;
	private String dimensions;
	private String shape;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDimensions() {
		return dimensions;
	}
	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}
	public String getShape() {
		return shape;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
	
	
	
	@Override
	public String toString() {
		return "MyVariableDTO [name=" + name + ", descripcion=" + descripcion + ", dataType=" + dataType
				+ ", dimensions=" + dimensions + ", shape=" + shape + "]";
	}
	
	
	

}
