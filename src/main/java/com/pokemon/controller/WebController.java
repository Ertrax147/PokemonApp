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
            try {
                java.util.List<com.pokemon.dto.Habilidad> habilidades = pokeApiService.obtenerHabilidades(numero).block();
                model.addAttribute("habilidades", habilidades != null ? habilidades : java.util.Collections.emptyList());
            } catch (Exception e) {
                model.addAttribute("habilidades", java.util.Collections.emptyList());
            }
            try {
                java.util.List<com.pokemon.dto.Movimiento> movimientos = pokeApiService.obtenerMovimientos(numero).block();
                model.addAttribute("movimientos", movimientos != null ? movimientos : java.util.Collections.emptyList());
            } catch (Exception e) {
                model.addAttribute("movimientos", java.util.Collections.emptyList());
            }
            try {
                java.util.List<com.pokemon.dto.TypeEffect> efectividades = pokeApiService.obtenerEfectividades(pokemon.get().getTipo1(), pokemon.get().getTipo2());
                model.addAttribute("efectividades", efectividades);
                java.util.List<com.pokemon.dto.TypeEffect> effX4 = new java.util.ArrayList<>();
                java.util.List<com.pokemon.dto.TypeEffect> effX2 = new java.util.ArrayList<>();
                java.util.List<com.pokemon.dto.TypeEffect> effX1 = new java.util.ArrayList<>();
                java.util.List<com.pokemon.dto.TypeEffect> effX05 = new java.util.ArrayList<>();
                java.util.List<com.pokemon.dto.TypeEffect> effX025 = new java.util.ArrayList<>();
                java.util.List<com.pokemon.dto.TypeEffect> effX0 = new java.util.ArrayList<>();
                for (com.pokemon.dto.TypeEffect e : efectividades) {
                    double m = e.getMultiplicador();
                    if (Math.abs(m - 4.0) < 1e-6) effX4.add(e);
                    else if (Math.abs(m - 2.0) < 1e-6) effX2.add(e);
                    else if (Math.abs(m - 1.0) < 1e-6) effX1.add(e);
                    else if (Math.abs(m - 0.5) < 1e-6) effX05.add(e);
                    else if (Math.abs(m - 0.25) < 1e-6) effX025.add(e);
                    else if (Math.abs(m - 0.0) < 1e-6) effX0.add(e);
                }
                model.addAttribute("effX4", effX4);
                model.addAttribute("effX2", effX2);
                model.addAttribute("effX1", effX1);
                model.addAttribute("effX05", effX05);
                model.addAttribute("effX025", effX025);
                model.addAttribute("effX0", effX0);
            } catch (Exception e) {
                model.addAttribute("efectividades", java.util.Collections.emptyList());
                model.addAttribute("effX4", java.util.Collections.emptyList());
                model.addAttribute("effX2", java.util.Collections.emptyList());
                model.addAttribute("effX1", java.util.Collections.emptyList());
                model.addAttribute("effX05", java.util.Collections.emptyList());
                model.addAttribute("effX025", java.util.Collections.emptyList());
                model.addAttribute("effX0", java.util.Collections.emptyList());
            }
            return "detalle";
        }
        return "redirect:/";
    }
    
    @GetMapping("/buscar")
    public String buscarPokemon(@RequestParam(required = false) String nombre,
                                @RequestParam(required = false) String tipo,
                                @RequestParam(required = false, name = "gen") java.util.List<Integer> generaciones,
                                Model model) {
        List<Pokemon> pokemons = pokemonService.buscarPorNombreTipoYGeneraciones(nombre, tipo, generaciones);
        model.addAttribute("pokemons", pokemons);
        if (nombre != null && !nombre.isBlank()) {
            model.addAttribute("busqueda", nombre);
        }
        if (tipo != null && !tipo.isBlank()) {
            model.addAttribute("tipo", tipo);
        }
        if (generaciones != null && !generaciones.isEmpty()) {
            model.addAttribute("gens", generaciones);
        }
        model.addAttribute("titulo", "Resultados de búsqueda");
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
