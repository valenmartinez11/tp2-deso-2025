package com.reservas.hotel.api_gestion_hotelera.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// Asegúrate de importar la Entidad Pasajero
import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero; 

@Repository
// Pasajero es la Entidad, Long es el tipo de su ID
public interface PasajeroRepository extends CrudRepository<Pasajero, Long> {
    
    // Si necesitas buscar por un campo que no sea el ID (ej. CUIT), 
    // podrías añadir métodos aquí, pero por defecto, la interfaz queda vacía:
    // Ejemplo: Optional<Pasajero> findByCUIT(String CUIT);
}
