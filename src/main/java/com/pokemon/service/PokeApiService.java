package com.pokemon.service;

import com.pokemon.model.Pokemon;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PokeApiService {
    
    private final WebClient webClient;
    private static final String POKEAPI_BASE_URL = "https://pokeapi.co/api/v2";
    
    public PokeApiService() {
        this.webClient = WebClient.builder()
                .baseUrl(POKEAPI_BASE_URL)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB buffer
                .build();
    }
    
    public Mono<Boolean> verificarConectividad() {
        return webClient.get()
                .uri("/pokemon/1")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    System.out.println("✅ Conexión a PokeAPI exitosa");
                    return true;
                })
                .onErrorReturn(false)
                .doOnError(error -> System.err.println("❌ Error de conectividad con PokeAPI: " + error.getMessage()));
    }

    public Flux<Pokemon> obtenerPokemonPrimeraGeneracion() {
        System.out.println("🚀 Iniciando carga de 151 Pokémon desde PokeAPI...");
        return Flux.range(1, 151)
                .doOnNext(numero -> System.out.println("📡 Cargando Pokémon #" + numero + "..."))
                .flatMap(this::obtenerPokemonPorNumero, 5) // Procesar 5 en paralelo
                .doOnComplete(() -> System.out.println("✅ Carga de Pokémon completada"));
    }
    
    private Mono<Pokemon> obtenerPokemonPorNumero(Integer numero) {
        return webClient.get()
                .uri("/pokemon/{id}", numero)
                .retrieve()
                .bodyToMono(PokeApiResponse.class)
                .map(this::convertirAPokemon)
                .doOnError(error -> System.err.println("❌ Error al obtener Pokémon #" + numero + ": " + error.getMessage()))
                .onErrorReturn(crearPokemonDefault(numero));
    }
    
    private Pokemon convertirAPokemon(PokeApiResponse response) {
        Pokemon pokemon = new Pokemon();
        pokemon.setNumero(response.getId());
        pokemon.setNombre(capitalizarPrimeraLetra(response.getName()));
        pokemon.setHp(obtenerStat(response, "hp"));
        pokemon.setAtaque(obtenerStat(response, "attack"));
        pokemon.setDefensa(obtenerStat(response, "defense"));
        pokemon.setAtaqueEspecial(obtenerStat(response, "special-attack"));
        pokemon.setDefensaEspecial(obtenerStat(response, "special-defense"));
        pokemon.setVelocidad(obtenerStat(response, "speed"));
        pokemon.setAltura(response.getHeight() / 10.0); // Convertir de decímetros a metros
        pokemon.setPeso(response.getWeight() / 10.0); // Convertir de hectogramos a kg
        
        // Obtener tipos
        if (response.getTypes() != null && !response.getTypes().isEmpty()) {
            pokemon.setTipo1(capitalizarPrimeraLetra(response.getTypes().get(0).getType().getName()));
            if (response.getTypes().size() > 1) {
                pokemon.setTipo2(capitalizarPrimeraLetra(response.getTypes().get(1).getType().getName()));
            }
        }
        
        // Obtener descripción
        pokemon.setDescripcion(obtenerDescripcion(response.getId()));
        
        // Configurar URL de imagen
        pokemon.setImagenUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + response.getId() + ".png");
        
        return pokemon;
    }
    
    private Integer obtenerStat(PokeApiResponse response, String statName) {
        if (response.getStats() != null) {
            return response.getStats().stream()
                    .filter(stat -> stat.getStat().getName().equals(statName))
                    .findFirst()
                    .map(PokeApiResponse.Stat::getBaseStat)
                    .orElse(50);
        }
        return 50;
    }
    
    private String obtenerDescripcion(Integer pokemonId) {
        // Descripciones basadas en el ID del Pokémon
        switch (pokemonId) {
            case 1: return "Un Pokémon extraño que nace con una semilla en la espalda.";
            case 2: return "Cuando la semilla en su espalda crece, parece que no puede ponerse de pie.";
            case 3: return "La planta florece cuando absorbe energía solar. Permanece en movimiento para buscar luz.";
            case 4: return "Prefiere las cosas calientes. Dicen que cuando llueve sale vapor de la punta de su cola.";
            case 5: return "Le gusta pelear. Se dice que el fuego se intensifica cuando disfruta de una batalla.";
            case 6: return "Escupe fuego tan caliente que funde las rocas. Causa incendios forestales sin querer.";
            case 7: return "Cuando retrae su largo cuello dentro del caparazón, lanza un chorro de agua vigorosa.";
            case 8: return "Se reconoce por las marcas en su caparazón. Con la edad, las marcas se vuelven más oscuras.";
            case 9: return "Aplasta a su enemigo con su peso corporal. Puede retraerse dentro de su caparazón.";
            case 25: return "Cuando varios de estos Pokémon se juntan, sus descargas eléctricas pueden causar tormentas.";
            case 133: return "Un Pokémon raro que puede evolucionar de muchas maneras diferentes.";
            case 150: return "Fue creado por un científico después de años de horribles experimentos de manipulación genética.";
            case 151: return "Un Pokémon legendario que se dice que contiene todos los códigos genéticos de los Pokémon.";
            default: return "Un Pokémon fascinante de la primera generación con habilidades únicas.";
        }
    }
    
    private String capitalizarPrimeraLetra(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    private Pokemon crearPokemonDefault(Integer numero) {
        Pokemon pokemon = new Pokemon();
        pokemon.setNumero(numero);
        pokemon.setNombre("Pokémon " + numero);
        pokemon.setTipo1("Normal");
        pokemon.setHp(50);
        pokemon.setAtaque(50);
        pokemon.setDefensa(50);
        pokemon.setAtaqueEspecial(50);
        pokemon.setDefensaEspecial(50);
        pokemon.setVelocidad(50);
        pokemon.setAltura(1.0);
        pokemon.setPeso(10.0);
        pokemon.setDescripcion("Información no disponible");
        pokemon.setImagenUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + numero + ".png");
        return pokemon;
    }
    
    // Clases internas para mapear la respuesta de la API
    public static class PokeApiResponse {
        private Integer id;
        private String name;
        private Integer height;
        private Integer weight;
        private List<TypeInfo> types;
        private List<Stat> stats;
        
        // Getters y Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }
        
        public Integer getWeight() { return weight; }
        public void setWeight(Integer weight) { this.weight = weight; }
        
        public List<TypeInfo> getTypes() { return types; }
        public void setTypes(List<TypeInfo> types) { this.types = types; }
        
        public List<Stat> getStats() { return stats; }
        public void setStats(List<Stat> stats) { this.stats = stats; }
        
        public static class TypeInfo {
            private Type type;
            
            public Type getType() { return type; }
            public void setType(Type type) { this.type = type; }
            
            public static class Type {
                private String name;
                
                public String getName() { return name; }
                public void setName(String name) { this.name = name; }
            }
        }
        
        public static class Stat {
            private Integer baseStat;
            private StatInfo stat;
            
            public Integer getBaseStat() { return baseStat; }
            public void setBaseStat(Integer baseStat) { this.baseStat = baseStat; }
            
            public StatInfo getStat() { return stat; }
            public void setStat(StatInfo stat) { this.stat = stat; }
            
            public static class StatInfo {
                private String name;
                
                public String getName() { return name; }
                public void setName(String name) { this.name = name; }
            }
        }
    }
}
