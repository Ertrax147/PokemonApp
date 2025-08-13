package com.pokemon.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pokemon.dto.Evolucion;
import com.pokemon.model.Pokemon;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            pokemon.setTipo1(traducirTipo(response.getTypes().get(0).getType().getName()));
            if (response.getTypes().size() > 1) {
                pokemon.setTipo2(traducirTipo(response.getTypes().get(1).getType().getName()));
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
    
    public Mono<java.util.List<Evolucion>> obtenerCadenaEvolutiva(Integer numero) {
		return webClient.get()
				.uri("/pokemon-species/{id}", numero)
				.retrieve()
				.bodyToMono(SpeciesResponse.class)
				.flatMap(species -> {
					String chainUrl = species.getEvolutionChain() != null ? species.getEvolutionChain().getUrl() : null;
					if (chainUrl == null) {
						return Mono.just(java.util.Collections.emptyList());
					}
					Integer chainId = extraerIdDesdeUrl(chainUrl);
					return webClient.get()
							.uri("/evolution-chain/{id}", chainId)
							.retrieve()
							.bodyToMono(EvolutionChainResponse.class)
							.map(this::construirCadenaLineal);
				});
	}

	private java.util.List<Evolucion> construirCadenaLineal(EvolutionChainResponse chainResponse) {
		java.util.List<Evolucion> resultado = new ArrayList<>();
		if (chainResponse == null || chainResponse.getChain() == null) {
			return resultado;
		}
		EvolutionChainResponse.Chain actual = chainResponse.getChain();
		Integer nivel = null; // nivel para llegar al nodo actual desde el anterior
		while (actual != null) {
			Integer id = extraerIdDesdeUrl(actual.getSpecies().getUrl());
			String nombre = capitalizarPrimeraLetra(actual.getSpecies().getName());
			resultado.add(new Evolucion(id, nombre, nivel));
			if (actual.getEvolvesTo() == null || actual.getEvolvesTo().isEmpty()) {
				break;
			}
			EvolutionChainResponse.Chain siguiente = actual.getEvolvesTo().get(0);
			nivel = null;
			if (siguiente.getEvolutionDetails() != null && !siguiente.getEvolutionDetails().isEmpty()) {
				EvolutionChainResponse.EvolutionDetail det = siguiente.getEvolutionDetails().get(0);
				nivel = det.getMinLevel();
			}
			actual = siguiente;
		}
		return resultado;
	}

	private Integer extraerIdDesdeUrl(String url) {
		if (url == null) return null;
		Pattern p = Pattern.compile("/([0-9]+)/?$");
		Matcher m = p.matcher(url);
		if (m.find()) {
			return Integer.parseInt(m.group(1));
		}
		return null;
	}

	// DTOs para species y evolution-chain
	public static class SpeciesResponse {
		@JsonProperty("evolution_chain")
		private EvolutionChainRef evolution_chain;
		public EvolutionChainRef getEvolutionChain() { return evolution_chain; }
		public void setEvolutionChain(EvolutionChainRef evolution_chain) { this.evolution_chain = evolution_chain; }
		public static class EvolutionChainRef {
			private String url;
			public String getUrl() { return url; }
			public void setUrl(String url) { this.url = url; }
		}
	}

	public static class EvolutionChainResponse {
		private Chain chain;
		public Chain getChain() { return chain; }
		public void setChain(Chain chain) { this.chain = chain; }

		public static class Chain {
			private Species species;
			@JsonProperty("evolves_to")
			private java.util.List<Chain> evolves_to;
			@JsonProperty("evolution_details")
			private java.util.List<EvolutionDetail> evolution_details;
			public Species getSpecies() { return species; }
			public void setSpecies(Species species) { this.species = species; }
			public java.util.List<Chain> getEvolvesTo() { return evolves_to; }
			public void setEvolvesTo(java.util.List<Chain> evolves_to) { this.evolves_to = evolves_to; }
			public java.util.List<EvolutionDetail> getEvolutionDetails() { return evolution_details; }
			public void setEvolutionDetails(java.util.List<EvolutionDetail> evolution_details) { this.evolution_details = evolution_details; }
		}
		public static class Species {
			private String name;
			private String url;
			public String getName() { return name; }
			public void setName(String name) { this.name = name; }
			public String getUrl() { return url; }
			public void setUrl(String url) { this.url = url; }
		}
		public static class EvolutionDetail {
			@JsonProperty("min_level")
			private Integer min_level;
			public Integer getMinLevel() { return min_level; }
			public void setMinLevel(Integer min_level) { this.min_level = min_level; }
		}
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
            @JsonProperty("base_stat")
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

    private String traducirTipo(String typeEn) {
        if (typeEn == null) return null;
        switch (typeEn.toLowerCase()) {
            case "normal": return "Normal";
            case "fire": return "Fuego";
            case "water": return "Agua";
            case "grass": return "Planta";
            case "electric": return "Eléctrico";
            case "ice": return "Hielo";
            case "fighting": return "Lucha";
            case "poison": return "Veneno";
            case "ground": return "Tierra";
            case "flying": return "Volador";
            case "psychic": return "Psíquico";
            case "bug": return "Bicho";
            case "rock": return "Roca";
            case "ghost": return "Fantasma";
            case "dragon": return "Dragón";
            case "dark": return "Siniestro";
            case "steel": return "Acero";
            case "fairy": return "Hada";
            default: return capitalizarPrimeraLetra(typeEn);
        }
    }
}
