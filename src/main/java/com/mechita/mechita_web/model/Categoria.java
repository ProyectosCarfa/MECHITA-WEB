package com.mechita.mechita_web.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(name = "nombre", nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    // ⭐ NUEVO
    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}