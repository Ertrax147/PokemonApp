package com.pokemon.dto;

public class Movimiento {
	private String nombre;
	private Integer nivel;
	private String metodo;

	public Movimiento() {}

	public Movimiento(String nombre, Integer nivel, String metodo) {
		this.nombre = nombre;
		this.nivel = nivel;
		this.metodo = metodo;
	}

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }

	public Integer getNivel() { return nivel; }
	public void setNivel(Integer nivel) { this.nivel = nivel; }

	public String getMetodo() { return metodo; }
	public void setMetodo(String metodo) { this.metodo = metodo; }
}
