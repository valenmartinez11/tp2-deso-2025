package com.reservas.hotel.api_gestion_hotelera.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Pasajero {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String apellido;
    private String CUIT;
    private String nroDocumento;
    private java.util.Date fechaDeNacimiento;
    private String nacionalidad;
    private String email;
    private String telefono;
    private String ocupacion;
    
    // (Relaciones con Direccion y TipoDocumento se añadirán luego)
}
