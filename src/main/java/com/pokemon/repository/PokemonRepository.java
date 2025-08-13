package com.pokemon.repository;

import com.pokemon.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    
    Optional<Pokemon> findByNumero(Integer numero);
    
    Optional<Pokemon> findByNombreIgnoreCase(String nombre);
    
    List<Pokemon> findByTipo1(String tipo);
    
    List<Pokemon> findByTipo1OrTipo2(String tipo1, String tipo2);
    
    @Query("SELECT p FROM Pokemon p WHERE p.nombre LIKE %?1%")
    List<Pokemon> findByNombreContaining(String nombre);
    
    List<Pokemon> findAllByOrderByNumeroAsc();
}
