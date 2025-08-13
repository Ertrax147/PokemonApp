package com.pokemon.config;

import com.pokemon.model.Pokemon;
import com.pokemon.repository.PokemonRepository;
import com.pokemon.service.PokeApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PokemonRepository pokemonRepository;
    
    @Autowired
    private PokeApiService pokeApiService;

    @Override
    public void run(String... args) throws Exception {
        if (pokemonRepository.count() == 0) {
            cargarPokemonPrimeraGeneracion();
        }
    }

    private void cargarPokemonPrimeraGeneracion() {
        try {
            System.out.println("🔄 Cargando Pokémon de la primera generación desde la API...");
            
            // Verificar conectividad primero
            Boolean conectividad = pokeApiService.verificarConectividad().block();
            if (Boolean.FALSE.equals(conectividad)) {
                System.err.println("❌ No se puede conectar a PokeAPI. Verifica tu conexión a internet.");
                return;
            }
            
            List<Pokemon> pokemons = pokeApiService.obtenerPokemonPrimeraGeneracion()
                    .collectList()
                    .block();

            if (pokemons != null && !pokemons.isEmpty()) {
                pokemonRepository.saveAll(pokemons);
                System.out.println("✅ Se cargaron " + pokemons.size() + " Pokémon exitosamente");
                
                // Verificar que las imágenes estén configuradas
                long pokemonsConImagen = pokemons.stream()
                        .filter(p -> p.getImagenUrl() != null && !p.getImagenUrl().isEmpty())
                        .count();
                System.out.println("🖼️  Pokémon con imagen configurada: " + pokemonsConImagen + "/" + pokemons.size());
            } else {
                System.out.println("❌ No se pudieron cargar los Pokémon desde la API");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al cargar Pokémon desde la API: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
