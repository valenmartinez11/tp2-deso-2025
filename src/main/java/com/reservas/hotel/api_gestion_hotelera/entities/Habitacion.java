package com.reservas.hotel.api_gestion_hotelera.entities;

import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;

import jakarta.persistence.*; // Para @Entity, @Id, @GeneratedValue
import lombok.Data; // Para @Data

@Data
@Entity
public class Habitacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Clave primaria autogenerada

    private String numero;
    private String idHabitacion;
    private EstadoHabitacion estado;


    // (Relaciones con TipoHabitacion y CostoPorNoche se añadirán luego)
}
