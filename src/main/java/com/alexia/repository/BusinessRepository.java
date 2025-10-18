package com.alexia.repository;

import com.alexia.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para operaciones de base de datos con negocios.
 */
@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    /**
     * Encuentra negocios por categoría (case-insensitive).
     * Solo retorna negocios activos.
     */
    @Query("SELECT b FROM Business b WHERE LOWER(b.category) = LOWER(:category) AND b.isActive = true ORDER BY b.name")
    List<Business> findByCategoryIgnoreCase(@Param("category") String category);

    /**
     * Encuentra negocios cuya categoría contenga el texto especificado (case-insensitive).
     * Solo retorna negocios activos.
     */
    @Query("SELECT b FROM Business b WHERE LOWER(b.category) LIKE LOWER(CONCAT('%', :category, '%')) AND b.isActive = true ORDER BY b.name")
    List<Business> findByCategoryContainingIgnoreCase(@Param("category") String category);

    /**
     * Encuentra todos los negocios activos ordenados por nombre.
     */
    List<Business> findByIsActiveTrueOrderByName();

    /**
     * Encuentra negocios por nombre (case-insensitive).
     */
    @Query("SELECT b FROM Business b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) AND b.isActive = true ORDER BY b.name")
    List<Business> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Cuenta negocios activos por categoría.
     */
    @Query("SELECT COUNT(b) FROM Business b WHERE LOWER(b.category) = LOWER(:category) AND b.isActive = true")
    long countByCategoryIgnoreCase(@Param("category") String category);

    /**
     * Cuenta todos los negocios activos.
     */
    long countByIsActiveTrue();

    /**
     * Obtiene todas las categorías únicas de negocios activos.
     */
    @Query("SELECT DISTINCT b.category FROM Business b WHERE b.isActive = true AND b.category IS NOT NULL ORDER BY b.category")
    List<String> findDistinctCategories();
}
