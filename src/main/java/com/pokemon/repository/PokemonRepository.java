package com.pokemon.repository;

import com.pokemon.model.Pokemon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // Pageable variants
    Page<Pokemon> findAllByOrderByNumeroAsc(Pageable pageable);
    Page<Pokemon> findByTipo1IgnoreCaseOrTipo2IgnoreCase(String tipo1, String tipo2, Pageable pageable);
    
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

    @Query("SELECT p FROM Pokemon p " +
           "WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:tipo IS NULL OR LOWER(p.tipo1) = LOWER(:tipo) OR LOWER(p.tipo2) = LOWER(:tipo)) " +
           "ORDER BY p.numero ASC")
    List<Pokemon> searchByNombreTipo(@Param("nombre") String nombre,
                                     @Param("tipo") String tipo);

    // Pageable variants for custom queries
    @Query("SELECT p FROM Pokemon p " +
           "WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:tipo IS NULL OR LOWER(p.tipo1) = LOWER(:tipo) OR LOWER(p.tipo2) = LOWER(:tipo)) " +
           "AND (:gens IS NULL OR p.generacion IN :gens)")
    Page<Pokemon> searchByNombreTipoGeneraciones(@Param("nombre") String nombre,
                                                 @Param("tipo") String tipo,
                                                 @Param("gens") List<Integer> gens,
                                                 Pageable pageable);

    @Query("SELECT p FROM Pokemon p " +
           "WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:tipo IS NULL OR LOWER(p.tipo1) = LOWER(:tipo) OR LOWER(p.tipo2) = LOWER(:tipo))")
    Page<Pokemon> searchByNombreTipo(@Param("nombre") String nombre,
                                     @Param("tipo") String tipo,
                                     Pageable pageable);
}
