package com.reservas.hotel.api_gestion_hotelera.entities;

import jakarta.persistence.*; 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ¡CAMBIO CRÍTICO AQUÍ! Usamos LocalDate en lugar de java.util.Date
import java.time.LocalDate; 
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    // Clave primaria e identificador 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    // Atributos base de la reserva
    // MODIFICADO: Uso de LocalDate (java.time) para fechas [2]
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso; 

    // Relaciones (Mapeos básicos asumidos basados en el diagrama)
    
    // Una reserva pertenece a una habitación (Habitacion)
    @ManyToOne 
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion; 

    // Una reserva tiene uno o más pasajeros (Pasajero)
    @ManyToMany
    @JoinTable(
        name = "reserva_pasajero",
        joinColumns = @JoinColumn(name = "reserva_id"),
        inverseJoinColumns = @JoinColumn(name = "pasajero_id")
    )
    private Set<Pasajero> pasajeros;

    // Relación con la persona que paga/hace la reserva (ResponsableReserva)
    // El Modelo mantiene la estructura de datos interna [3]
    @OneToOne(cascade = CascadeType.ALL) 
    @JoinColumn(name = "responsable_id", referencedColumnName = "id")
    private ResponsableReserva responsableReserva; 
}