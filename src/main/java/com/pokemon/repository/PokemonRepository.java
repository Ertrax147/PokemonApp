package com.pokemon.repository;

import com.pokemon.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    
    Optional<Pokemon> findByNumero(Integer numero);
    
    Optional<Pokemon> findByNombreIgnoreCase(String nombre);
    
    List<Pokemon> findByNombreContainingIgnoreCase(String nombre);
    
    List<Pokemon> findByTipo1IgnoreCase(String tipo);
    
    List<Pokemon> findByTipo1IgnoreCaseOrTipo2IgnoreCase(String tipo1, String tipo2);
    
    @Deprecated
    @Query("SELECT p FROM Pokemon p WHERE p.nombre LIKE %?1%")
    List<Pokemon> findByNombreContaining(String nombre);
    
    List<Pokemon> findAllByOrderByNumeroAsc();

    @Query("SELECT p FROM Pokemon p " +
           "WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:tipo IS NULL OR LOWER(p.tipo1) = LOWER(:tipo) OR LOWER(p.tipo2) = LOWER(:tipo)) " +
           "AND (:gens IS NULL OR p.generacion IN :gens) " +
           "ORDER BY p.numero ASC")
    List<Pokemon> searchByNombreTipoGeneraciones(@Param("nombre") String nombre,
                                                 @Param("tipo") String tipo,
                                                 @Param("gens") List<Integer> gens);
}
