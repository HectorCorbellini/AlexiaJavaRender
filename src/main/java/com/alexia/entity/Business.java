package com.alexia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un negocio registrado en la plataforma.
 * Permite búsqueda básica por categoría.
 */
@Entity
@Table(name = "businesses", indexes = {
    @Index(name = "idx_businesses_category", columnList = "category"),
    @Index(name = "idx_businesses_is_active", columnList = "is_active"),
    @Index(name = "idx_businesses_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del negocio es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    @Column(nullable = false, length = 255)
    private String name;

    @Size(max = 100, message = "La categoría no puede exceder 100 caracteres")
    @Column(length = 100)
    private String category;

    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    @Column(length = 500)
    private String address;

    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    @Column(length = 50)
    private String phone;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Método ejecutado antes de persistir la entidad.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    /**
     * Método ejecutado antes de actualizar la entidad.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Retorna una representación en texto del negocio.
     * Formato: "Nombre - Dirección - Teléfono"
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        
        if (address != null && !address.isEmpty()) {
            sb.append(" - ").append(address);
        }
        
        if (phone != null && !phone.isEmpty()) {
            sb.append(" - ").append(phone);
        }
        
        return sb.toString();
    }

    /**
     * Verifica si el negocio está activo.
     */
    public boolean isActive() {
        return isActive != null && isActive;
    }
}
