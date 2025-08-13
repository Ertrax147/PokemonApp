package com.pokemon.controller;

import com.pokemon.dto.Evolucion;
import com.pokemon.model.Pokemon;
import com.pokemon.service.PokemonService;
import com.pokemon.service.PokeApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class WebController {
    
    @Autowired
    private PokemonService pokemonService;
    
    @Autowired
    private PokeApiService pokeApiService;
    
    @GetMapping("/")
    public String home(Model model) {
        List<Pokemon> pokemons = pokemonService.obtenerTodosLosPokemon();
        model.addAttribute("pokemons", pokemons);
        model.addAttribute("titulo", "Pokédex - Primera Generación");
        return "index";
    }
    
    @GetMapping("/pokemon/{numero}")
    public String detallePokemon(@PathVariable Integer numero, Model model) {
        Optional<Pokemon> pokemon = pokemonService.obtenerPokemonPorNumero(numero);
        if (pokemon.isPresent()) {
            model.addAttribute("pokemon", pokemon.get());
            try {
                List<Evolucion> evoluciones = pokeApiService.obtenerCadenaEvolutiva(numero).block();
                model.addAttribute("evoluciones", evoluciones);
            } catch (Exception e) {
                model.addAttribute("evoluciones", java.util.Collections.emptyList());
            }
            return "detalle";
        }
        return "redirect:/";
    }
    
    @GetMapping("/buscar")
    public String buscarPokemon(@RequestParam String nombre, Model model) {
        List<Pokemon> pokemons = pokemonService.buscarPokemonPorNombre(nombre);
        model.addAttribute("pokemons", pokemons);
        model.addAttribute("busqueda", nombre);
        model.addAttribute("titulo", "Resultados de búsqueda: " + nombre);
        return "index";
    }
    
    @GetMapping("/tipo/{tipo}")
    public String pokemonPorTipo(@PathVariable String tipo, Model model) {
        List<Pokemon> pokemons = pokemonService.obtenerPokemonPorTipo(tipo);
        model.addAttribute("pokemons", pokemons);
        model.addAttribute("tipo", tipo);
        model.addAttribute("titulo", "Pokémon de tipo " + tipo);
        return "index";
    }
    
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("titulo", "Acerca de");
        return "about";
    }
}
