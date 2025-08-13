package com.pokemon.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pokemon")
public class Pokemon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private Integer numero;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String tipo1;
    
    private String tipo2;
    
    @Column(nullable = false)
    private Integer hp;
    
    @Column(nullable = false)
    private Integer ataque;
    
    @Column(nullable = false)
    private Integer defensa;
    
    @Column(nullable = false)
    private Integer ataqueEspecial;
    
    @Column(nullable = false)
    private Integer defensaEspecial;
    
    @Column(nullable = false)
    private Integer velocidad;
    
    @Column(nullable = false)
    private String descripcion;
    
    @Column(nullable = false)
    private Double altura;
    
    @Column(nullable = false)
    private Double peso;
    
    @Column
    private String imagenUrl;
    
    // Constructores
    public Pokemon() {}
    
    public Pokemon(Integer numero, String nombre, String tipo1, String tipo2, 
                   Integer hp, Integer ataque, Integer defensa, Integer ataqueEspecial, 
                   Integer defensaEspecial, Integer velocidad, String descripcion, 
                   Double altura, Double peso) {
        this.numero = numero;
        this.nombre = nombre;
        this.tipo1 = tipo1;
        this.tipo2 = tipo2;
        this.hp = hp;
        this.ataque = ataque;
        this.defensa = defensa;
        this.ataqueEspecial = ataqueEspecial;
        this.defensaEspecial = defensaEspecial;
        this.velocidad = velocidad;
        this.descripcion = descripcion;
        this.altura = altura;
        this.peso = peso;
        this.imagenUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + numero + ".png";
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipo1() { return tipo1; }
    public void setTipo1(String tipo1) { this.tipo1 = tipo1; }
    
    public String getTipo2() { return tipo2; }
    public void setTipo2(String tipo2) { this.tipo2 = tipo2; }
    
    public Integer getHp() { return hp; }
    public void setHp(Integer hp) { this.hp = hp; }
    
    public Integer getAtaque() { return ataque; }
    public void setAtaque(Integer ataque) { this.ataque = ataque; }
    
    public Integer getDefensa() { return defensa; }
    public void setDefensa(Integer defensa) { this.defensa = defensa; }
    
    public Integer getAtaqueEspecial() { return ataqueEspecial; }
    public void setAtaqueEspecial(Integer ataqueEspecial) { this.ataqueEspecial = ataqueEspecial; }
    
    public Integer getDefensaEspecial() { return defensaEspecial; }
    public void setDefensaEspecial(Integer defensaEspecial) { this.defensaEspecial = defensaEspecial; }
    
    public Integer getVelocidad() { return velocidad; }
    public void setVelocidad(Integer velocidad) { this.velocidad = velocidad; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }
    
    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    public String getTiposCompletos() {
        if (tipo2 != null && !tipo2.isEmpty()) {
            return tipo1 + " / " + tipo2;
        }
        return tipo1;
    }
    
    public Integer getTotalStats() {
        return hp + ataque + defensa + ataqueEspecial + defensaEspecial + velocidad;
    }
}
