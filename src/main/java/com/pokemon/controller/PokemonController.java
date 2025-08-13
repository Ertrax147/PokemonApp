package com.pokemon.controller;

import com.pokemon.model.Pokemon;
import com.pokemon.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pokemon")
@CrossOrigin(origins = "*")
public class PokemonController {
    
    @Autowired
    private PokemonService pokemonService;
    
    @GetMapping
    public List<Pokemon> obtenerTodosLosPokemon() {
        return pokemonService.obtenerTodosLosPokemon();
    }
    
    @GetMapping("/{numero}")
    public ResponseEntity<Pokemon> obtenerPokemonPorNumero(@PathVariable Integer numero) {
        Optional<Pokemon> pokemon = pokemonService.obtenerPokemonPorNumero(numero);
        return pokemon.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Pokemon> obtenerPokemonPorNombre(@PathVariable String nombre) {
        Optional<Pokemon> pokemon = pokemonService.obtenerPokemonPorNombre(nombre);
        return pokemon.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/buscar/{nombre}")
    public List<Pokemon> buscarPokemonPorNombre(@PathVariable String nombre) {
        return pokemonService.buscarPokemonPorNombre(nombre);
    }
    
    @GetMapping("/tipo/{tipo}")
    public List<Pokemon> obtenerPokemonPorTipo(@PathVariable String tipo) {
        return pokemonService.obtenerPokemonPorTipo(tipo);
    }
    
    @PostMapping
    public Pokemon crearPokemon(@RequestBody Pokemon pokemon) {
        return pokemonService.guardarPokemon(pokemon);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pokemon> actualizarPokemon(@PathVariable Long id, @RequestBody Pokemon pokemon) {
        if (!pokemonService.existePokemonPorId(id)) {
            return ResponseEntity.notFound().build();
        }
        pokemon.setId(id);
        Pokemon pokemonActualizado = pokemonService.guardarPokemon(pokemon);
        return ResponseEntity.ok(pokemonActualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPokemon(@PathVariable Long id) {
        pokemonService.eliminarPokemon(id);
        return ResponseEntity.noContent().build();
    }
}
