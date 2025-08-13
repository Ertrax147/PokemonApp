package com.pokemon.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pokemon.dto.Evolucion;
import com.pokemon.dto.Habilidad;
import com.pokemon.dto.Movimiento;
import com.pokemon.model.Pokemon;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
    
    public Flux<Pokemon> obtenerPokemonGenUnoATres() {
        System.out.println("🚀 Iniciando carga de Pokémon Gen 1-3 (1..386) desde PokeAPI...");
        return Flux.range(1, 386)
                .doOnNext(numero -> System.out.println("📡 Cargando Pokémon #" + numero + "..."))
                .flatMap(this::obtenerPokemonPorNumero, 5)
                .doOnComplete(() -> System.out.println("✅ Carga de Pokémon Gen 1-3 completada"));
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
        
        // Generación (1: 1-151, 2: 152-251, 3: 252-386)
        int id = response.getId();
        int gen = id <= 151 ? 1 : (id <= 251 ? 2 : (id <= 386 ? 3 : 4));
        pokemon.setGeneracion(gen);
        
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
		java.util.List<Evolucion> resultado = new java.util.ArrayList<>();
		if (chainResponse == null || chainResponse.getChain() == null) {
			return resultado;
		}
		EvolutionChainResponse.Chain actual = chainResponse.getChain();
		Integer nivel = null; // nivel para llegar al nodo actual desde el anterior
		String condicion = null;
		while (actual != null) {
			Integer id = extraerIdDesdeUrl(actual.getSpecies().getUrl());
			String nombre = capitalizarPrimeraLetra(actual.getSpecies().getName());
			resultado.add(new Evolucion(id, nombre, nivel, condicion));
			if (actual.getEvolvesTo() == null || actual.getEvolvesTo().isEmpty()) {
				break;
			}
			EvolutionChainResponse.Chain siguiente = actual.getEvolvesTo().get(0);
			nivel = null;
			condicion = null;
			if (siguiente.getEvolutionDetails() != null && !siguiente.getEvolutionDetails().isEmpty()) {
				EvolutionChainResponse.EvolutionDetail det = siguiente.getEvolutionDetails().get(0);
				nivel = det.getMinLevel();
				condicion = describirCondicion(det);
			}
			actual = siguiente;
		}
		return resultado;
	}

	private String describirCondicion(EvolutionChainResponse.EvolutionDetail det) {
		if (det == null) return null;
		if (det.getItem() != null && det.getItem().getName() != null) {
			return describirItem(det.getItem().getName());
		}
		if (det.getTrigger() != null && det.getTrigger().getName() != null) {
			String trigger = det.getTrigger().getName();
			if ("trade".equalsIgnoreCase(trigger)) {
				return "Por intercambio";
			}
			if ("level-up".equalsIgnoreCase(trigger) && det.getTimeOfDay() != null && !det.getTimeOfDay().isEmpty()) {
				return det.getTimeOfDay().equalsIgnoreCase("night") ? "De noche" : "De día";
			}
			if ("use-item".equalsIgnoreCase(trigger) && det.getItem() != null) {
				return describirItem(det.getItem().getName());
			}
		}
		if (det.getMinHappiness() != null) {
			return "Alta amistad";
		}
		return null;
	}

	private String describirItem(String itemSlug) {
		if (itemSlug == null) return null;
		switch (itemSlug) {
			case "thunder-stone": return "Piedra Trueno";
			case "fire-stone": return "Piedra Fuego";
			case "water-stone": return "Piedra Agua";
			case "leaf-stone": return "Piedra Hoja";
			case "moon-stone": return "Piedra Lunar";
			case "sun-stone": return "Piedra Solar";
			case "dawn-stone": return "Piedra Alba";
			case "dusk-stone": return "Piedra Noche";
			case "shiny-stone": return "Piedra Día";
			case "ice-stone": return "Piedra Hielo";
			case "oval-stone": return "Piedra Oval";
			default:
				String pretty = itemSlug.replace('-', ' ');
				return capitalizarPrimeraLetra(pretty);
		}
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
			private NamedResource item;
			private NamedResource trigger;
			@JsonProperty("time_of_day")
			private String time_of_day;
			@JsonProperty("min_happiness")
			private Integer min_happiness;
			public Integer getMinLevel() { return min_level; }
			public void setMinLevel(Integer min_level) { this.min_level = min_level; }
			public NamedResource getItem() { return item; }
			public void setItem(NamedResource item) { this.item = item; }
			public NamedResource getTrigger() { return trigger; }
			public void setTrigger(NamedResource trigger) { this.trigger = trigger; }
			public String getTimeOfDay() { return time_of_day; }
			public void setTimeOfDay(String time_of_day) { this.time_of_day = time_of_day; }
			public Integer getMinHappiness() { return min_happiness; }
			public void setMinHappiness(Integer min_happiness) { this.min_happiness = min_happiness; }
		}
		public static class NamedResource {
			private String name;
			private String url;
			public String getName() { return name; }
			public void setName(String name) { this.name = name; }
			public String getUrl() { return url; }
			public void setUrl(String url) { this.url = url; }
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
        private List<AbilityInfo> abilities;
        private List<MoveInfo> moves;
        
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
        
        public List<AbilityInfo> getAbilities() { return abilities; }
        public void setAbilities(List<AbilityInfo> abilities) { this.abilities = abilities; }
        public List<MoveInfo> getMoves() { return moves; }
        public void setMoves(List<MoveInfo> moves) { this.moves = moves; }
        
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

        public static class AbilityInfo {
            private NamedResource ability;
            @JsonProperty("is_hidden")
            private boolean is_hidden;
            public NamedResource getAbility() { return ability; }
            public void setAbility(NamedResource ability) { this.ability = ability; }
            public boolean isIsHidden() { return is_hidden; }
            public void setIsHidden(boolean is_hidden) { this.is_hidden = is_hidden; }
        }
        public static class MoveInfo {
            private NamedResource move;
            @JsonProperty("version_group_details")
            private List<VersionGroupDetail> version_group_details;
            public NamedResource getMove() { return move; }
            public void setMove(NamedResource move) { this.move = move; }
            public List<VersionGroupDetail> getVersionGroupDetails() { return version_group_details; }
            public void setVersionGroupDetails(List<VersionGroupDetail> version_group_details) { this.version_group_details = version_group_details; }
        }
        public static class VersionGroupDetail {
            @JsonProperty("level_learned_at")
            private Integer level_learned_at;
            @JsonProperty("move_learn_method")
            private NamedResource move_learn_method;
            @JsonProperty("version_group")
            private NamedResource version_group;
            public Integer getLevelLearnedAt() { return level_learned_at; }
            public void setLevelLearnedAt(Integer level_learned_at) { this.level_learned_at = level_learned_at; }
            public NamedResource getMoveLearnMethod() { return move_learn_method; }
            public void setMoveLearnMethod(NamedResource move_learn_method) { this.move_learn_method = move_learn_method; }
            public NamedResource getVersionGroup() { return version_group; }
            public void setVersionGroup(NamedResource version_group) { this.version_group = version_group; }
        }
        public static class NamedResource {
            private String name;
            private String url;
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getUrl() { return url; }
            public void setUrl(String url) { this.url = url; }
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

    public Mono<java.util.List<Habilidad>> obtenerHabilidades(Integer numero) {
        return webClient.get()
                .uri("/pokemon/{id}", numero)
                .retrieve()
                .bodyToMono(PokeApiResponse.class)
                .flatMapMany(resp -> Flux.fromIterable(resp.getAbilities() != null ? resp.getAbilities() : java.util.Collections.emptyList()))
                .flatMap(ab -> {
                    String abilityName = ab.getAbility() != null ? ab.getAbility().getName() : null;
                    boolean isHidden = ab.isIsHidden();
                    if (abilityName == null) {
                        return Mono.empty();
                    }
                    return webClient.get()
                            .uri("/ability/{name}", abilityName)
                            .retrieve()
                            .bodyToMono(AbilityDetailResponse.class)
                            .map(detail -> new Habilidad(capitalizarPrimeraLetra(abilityName), isHidden, extraerDescripcionHabilidad(detail)));
                })
                .collectList();
    }

    private String extraerDescripcionHabilidad(AbilityDetailResponse detail) {
        if (detail == null || detail.getEffectEntries() == null) return "";
        String descEs = detail.getEffectEntries().stream()
                .filter(e -> e.getLanguage() != null && "es".equalsIgnoreCase(e.getLanguage().getName()))
                .map(AbilityDetailResponse.EffectEntry::getShortEffect)
                .findFirst().orElse(null);
        if (descEs != null) return descEs;
        String descEn = detail.getEffectEntries().stream()
                .filter(e -> e.getLanguage() != null && "en".equalsIgnoreCase(e.getLanguage().getName()))
                .map(AbilityDetailResponse.EffectEntry::getShortEffect)
                .findFirst().orElse(null);
        return descEn != null ? descEn : "";
    }

    public Mono<java.util.List<Movimiento>> obtenerMovimientos(Integer numero) {
        return webClient.get()
                .uri("/pokemon/{id}", numero)
                .retrieve()
                .bodyToMono(PokeApiResponse.class)
                .map(resp -> {
                    java.util.List<Movimiento> resultado = new ArrayList<>();
                    if (resp.getMoves() == null) return resultado;
                    Set<String> permitidos = versionGroupsPermitidosGen1a3();
                    for (PokeApiResponse.MoveInfo mi : resp.getMoves()) {
                        String moveName = mi.getMove() != null ? mi.getMove().getName() : null;
                        if (moveName == null || mi.getVersionGroupDetails() == null) continue;
                        // Elegir mejor detalle por prioridad (level-up con menor nivel; si no, cualquiera)
                        Integer mejorNivel = null;
                        String mejorMetodo = null;
                        for (PokeApiResponse.VersionGroupDetail vgd : mi.getVersionGroupDetails()) {
                            if (vgd.getVersionGroup() == null || vgd.getVersionGroup().getName() == null) continue;
                            if (!permitidos.contains(vgd.getVersionGroup().getName())) continue;
                            String metodo = vgd.getMoveLearnMethod() != null ? vgd.getMoveLearnMethod().getName() : null;
                            Integer nivel = vgd.getLevelLearnedAt();
                            if ("level-up".equalsIgnoreCase(metodo)) {
                                if (mejorNivel == null || (nivel != null && nivel < mejorNivel)) {
                                    mejorNivel = nivel;
                                    mejorMetodo = metodo;
                                }
                            } else if (mejorMetodo == null) {
                                mejorMetodo = metodo;
                            }
                        }
                        if (mejorMetodo != null || mejorNivel != null) {
                            resultado.add(new Movimiento(formatearNombreMovimiento(moveName), mejorNivel, traducirMetodoMovimiento(mejorMetodo)));
                        }
                    }
                    resultado.sort(Comparator
                            .comparing((Movimiento m) -> m.getNivel() == null)
                            .thenComparing(m -> m.getNivel() == null ? Integer.MAX_VALUE : m.getNivel())
                            .thenComparing(Movimiento::getNombre));
                    return resultado;
                });
    }

    private Set<String> versionGroupsPermitidosGen1a3() {
        Set<String> set = new HashSet<>();
        // Gen 1
        set.add("red-blue"); set.add("yellow");
        // Gen 2
        set.add("gold-silver"); set.add("crystal");
        // Gen 3
        set.add("ruby-sapphire"); set.add("emerald"); set.add("firered-leafgreen");
        return set;
    }

    private String traducirMetodoMovimiento(String metodo) {
        if (metodo == null) return "";
        switch (metodo) {
            case "level-up": return "Subiendo de nivel";
            case "machine": return "MT/MO";
            case "tutor": return "Tutor";
            case "egg": return "Huevo";
            default: return capitalizarPrimeraLetra(metodo.replace('-', ' '));
        }
    }

    private String formatearNombreMovimiento(String moveSlug) {
        if (moveSlug == null) return "";
        String withSpaces = moveSlug.replace('-', ' ');
        return capitalizarPrimeraLetra(withSpaces);
    }

    // DTO para /ability/{name}
    public static class AbilityDetailResponse {
        @JsonProperty("effect_entries")
        private java.util.List<EffectEntry> effect_entries;
        public java.util.List<EffectEntry> getEffectEntries() { return effect_entries; }
        public void setEffectEntries(java.util.List<EffectEntry> effect_entries) { this.effect_entries = effect_entries; }
        public static class EffectEntry {
            @JsonProperty("short_effect")
            private String short_effect;
            private Language language;
            public String getShortEffect() { return short_effect; }
            public void setShortEffect(String short_effect) { this.short_effect = short_effect; }
            public Language getLanguage() { return language; }
            public void setLanguage(Language language) { this.language = language; }
        }
        public static class Language {
            private String name;
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }
}
