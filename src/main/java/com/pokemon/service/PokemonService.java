package com.pokemon.service;

import com.pokemon.model.Pokemon;
import com.pokemon.repository.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PokemonService {
    
    @Autowired
    private PokemonRepository pokemonRepository;
    
    public List<Pokemon> obtenerTodosLosPokemon() {
        return pokemonRepository.findAllByOrderByNumeroAsc();
    }
    
    public Optional<Pokemon> obtenerPokemonPorNumero(Integer numero) {
        return pokemonRepository.findByNumero(numero);
    }
    
    public Optional<Pokemon> obtenerPokemonPorNombre(String nombre) {
        return pokemonRepository.findByNombreIgnoreCase(nombre);
    }
    
    public List<Pokemon> buscarPokemonPorNombre(String nombre) {
        return pokemonRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public List<Pokemon> obtenerPokemonPorTipo(String tipo) {
        return pokemonRepository.findByTipo1IgnoreCaseOrTipo2IgnoreCase(tipo, tipo);
    }
    
    public Pokemon guardarPokemon(Pokemon pokemon) {
        return pokemonRepository.save(pokemon);
    }
    
    public void eliminarPokemon(Long id) {
        pokemonRepository.deleteById(id);
    }
    
    public boolean existePokemonPorNumero(Integer numero) {
        return pokemonRepository.findByNumero(numero).isPresent();
    }
}
