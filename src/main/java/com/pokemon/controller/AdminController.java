package com.pokemon.controller;

import com.pokemon.model.Pokemon;
import com.pokemon.service.PokeApiService;
import com.pokemon.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private PokeApiService pokeApiService;
    
    @Autowired
    private PokemonService pokemonService;
    
    @GetMapping
    public String adminPanel(Model model) {
        model.addAttribute("titulo", "Panel de Administración");
        model.addAttribute("totalPokemon", pokemonService.obtenerTodosLosPokemon().size());
        return "admin";
    }
    
    @PostMapping("/reload")
    public String recargarPokemon(Model model) {
        try {
            // Limpiar base de datos existente
            pokemonService.obtenerTodosLosPokemon().forEach(pokemon -> 
                pokemonService.eliminarPokemon(pokemon.getId()));
            
            // Cargar desde la API (Gen 1-3)
            List<Pokemon> pokemons = pokeApiService.obtenerPokemonGenUnoATres()
                    .collectList()
                    .block();
            
            if (pokemons != null && !pokemons.isEmpty()) {
                pokemons.forEach(pokemonService::guardarPokemon);
                model.addAttribute("mensaje", "✅ Se recargaron " + pokemons.size() + " Pokémon exitosamente");
                model.addAttribute("tipoMensaje", "success");
            } else {
                model.addAttribute("mensaje", "❌ No se pudieron cargar los Pokémon desde la API");
                model.addAttribute("tipoMensaje", "danger");
            }
        } catch (Exception e) {
            model.addAttribute("mensaje", "❌ Error al recargar: " + e.getMessage());
            model.addAttribute("tipoMensaje", "danger");
        }
        
        return adminPanel(model);
    }

    @GetMapping("/diagnostico")
    public String diagnostico(Model model) {
        try {
            // Verificar conectividad con PokeAPI
            Boolean conectividad = pokeApiService.verificarConectividad().block();
            model.addAttribute("conectividad", conectividad);
            
            // Obtener estadísticas de Pokémon
            List<Pokemon> todosLosPokemon = pokemonService.obtenerTodosLosPokemon();
            long totalPokemon = todosLosPokemon.size();
            long pokemonsConImagen = todosLosPokemon.stream()
                    .filter(p -> p.getImagenUrl() != null && !p.getImagenUrl().isEmpty())
                    .count();
            long pokemonsSinImagen = totalPokemon - pokemonsConImagen;
            
            model.addAttribute("totalPokemon", totalPokemon);
            model.addAttribute("pokemonsConImagen", pokemonsConImagen);
            model.addAttribute("pokemonsSinImagen", pokemonsSinImagen);
            model.addAttribute("porcentajeImagenes", totalPokemon > 0 ? (pokemonsConImagen * 100.0 / totalPokemon) : 0.0);
            
            // Verificar algunos Pokémon específicos
            List<Pokemon> pokemonsProblema = todosLosPokemon.stream()
                    .filter(p -> p.getImagenUrl() == null || p.getImagenUrl().isEmpty() || 
                                p.getNombre() == null || p.getNombre().isEmpty())
                    .limit(10)
                    .collect(Collectors.toList());
            
            model.addAttribute("pokemonsProblema", pokemonsProblema);
            
        } catch (Exception e) {
            model.addAttribute("error", "Error en diagnóstico: " + e.getMessage());
        }
        
        model.addAttribute("titulo", "Diagnóstico del Sistema");
        return "diagnostico";
    }
}
