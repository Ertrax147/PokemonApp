package com.pokemon.dto;

public class TypeEffect {
	private String tipo;
	private double multiplicador;

	public TypeEffect() {}

	public TypeEffect(String tipo, double multiplicador) {
		this.tipo = tipo;
		this.multiplicador = multiplicador;
	}

	public String getTipo() { return tipo; }
	public void setTipo(String tipo) { this.tipo = tipo; }

	public double getMultiplicador() { return multiplicador; }
	public void setMultiplicador(double multiplicador) { this.multiplicador = multiplicador; }
}
