package com.pokemon.dto;

public class Evolucion {
	private Integer numero;
	private String nombre;
	private Integer nivelMinimo; // puede ser null si es la forma base o no tiene nivel

	public Evolucion() {}

	public Evolucion(Integer numero, String nombre, Integer nivelMinimo) {
		this.numero = numero;
		this.nombre = nombre;
		this.nivelMinimo = nivelMinimo;
	}

	public Integer getNumero() { return numero; }
	public void setNumero(Integer numero) { this.numero = numero; }

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }

	public Integer getNivelMinimo() { return nivelMinimo; }
	public void setNivelMinimo(Integer nivelMinimo) { this.nivelMinimo = nivelMinimo; }
}
