package com.reservas.hotel.api_gestion_hotelera.service;

import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero;
import java.util.Optional;
import java.util.Set; // Si usas Set para colecciones, como en el ejemplo de las fuentes [6]

public interface PasajeroService {
    
    // 1. Contratos básicos (delegación al Repositorio)
    Optional<Pasajero> buscarPorId(Long id); // Usamos Optional para el manejo de posibles ausencias [6]
    Set<Pasajero> buscarTodos();
    
    // 2. Contratos de Lógica de Negocio (Casos de Uso)
    // CU11: Dar de baja huésped
    void darDeBajaPasajero(Long id); 
    
    // Operación CRUD para el controlador
    Pasajero registrarPasajero(Pasajero pasajero);
}
