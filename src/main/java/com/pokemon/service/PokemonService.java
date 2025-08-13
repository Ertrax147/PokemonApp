package com.pokemon.service;

import com.pokemon.model.Pokemon;
import com.pokemon.repository.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<Pokemon> obtenerTodosLosPokemonPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("numero").ascending());
        return pokemonRepository.findAllByOrderByNumeroAsc(pageable);
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

    public Page<Pokemon> obtenerPokemonPorTipoPaginado(String tipo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("numero").ascending());
        return pokemonRepository.findByTipo1IgnoreCaseOrTipo2IgnoreCase(tipo, tipo, pageable);
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

    public boolean existePokemonPorId(Long id) {
        return pokemonRepository.existsById(id);
    }

    public List<Pokemon> buscarPorNombreTipoYGeneraciones(String nombre, String tipo, List<Integer> generaciones) {
        String nombreParam = (nombre == null || nombre.isBlank()) ? null : nombre;
        String tipoParam = (tipo == null || tipo.isBlank()) ? null : tipo;
        if (generaciones == null || generaciones.isEmpty()) {
            return pokemonRepository.searchByNombreTipo(nombreParam, tipoParam);
        }
        return pokemonRepository.searchByNombreTipoGeneraciones(nombreParam, tipoParam, generaciones);
    }

    public Page<Pokemon> buscarPorNombreTipoYGeneracionesPaginado(String nombre, String tipo, List<Integer> generaciones, int page, int size) {
        String nombreParam = (nombre == null || nombre.isBlank()) ? null : nombre;
        String tipoParam = (tipo == null || tipo.isBlank()) ? null : tipo;
        Pageable pageable = PageRequest.of(page, size, Sort.by("numero").ascending());
        if (generaciones == null || generaciones.isEmpty()) {
            return pokemonRepository.searchByNombreTipo(nombreParam, tipoParam, pageable);
        }
        return pokemonRepository.searchByNombreTipoGeneraciones(nombreParam, tipoParam, generaciones, pageable);
    }

    public void eliminarTodos() {
        pokemonRepository.deleteAllInBatch();
    }

    public void guardarTodos(List<Pokemon> pokemons) {
        pokemonRepository.saveAll(pokemons);
    }
}
