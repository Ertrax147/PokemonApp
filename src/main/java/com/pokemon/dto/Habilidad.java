package com.pokemon.dto;

public class Habilidad {
	private String nombre;
	private boolean oculta;
	private String descripcion;

	public Habilidad() {}

	public Habilidad(String nombre, boolean oculta, String descripcion) {
		this.nombre = nombre;
		this.oculta = oculta;
		this.descripcion = descripcion;
	}

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }

	public boolean isOculta() { return oculta; }
	public void setOculta(boolean oculta) { this.oculta = oculta; }

	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
