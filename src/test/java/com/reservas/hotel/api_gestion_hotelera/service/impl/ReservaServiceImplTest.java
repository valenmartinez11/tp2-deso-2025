package com.reservas.hotel.api_gestion_hotelera.service.impl;

// Importaciones necesarias de JUnit, Mockito y utilidades
import org.junit.jupiter.api.Test; 
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*; 
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional; 
import java.util.Set;
import java.util.List;
import java.util.Arrays; 

// Importa tus clases de la lógica de negocio (entidades, repositorios y otros servicios)
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.TipoFactura;
import com.reservas.hotel.api_gestion_hotelera.repository.PasajeroRepository;
import com.reservas.hotel.api_gestion_hotelera.repository.ReservaRepository;
import com.reservas.hotel.api_gestion_hotelera.service.ContabilidadService; 
import com.reservas.hotel.api_gestion_hotelera.entities.Factura;

// Anotación clave: Extiende el comportamiento de JUnit para usar Mockito
@ExtendWith(MockitoExtension.class) 
public class ReservaServiceImplTest {

    // 1. CLASE A PROBAR (LA UNIDAD): 
    // @InjectMocks inyecta los objetos simulados (@Mock) en la clase real.
    @InjectMocks
    private ReservaServiceImpl reservaService; 

    // 2. DEPENDENCIAS SIMULADAS (MOCKS): 
    // @Mock crea sustitutos simulados para las dependencias (Repositorio, Contabilidad, etc.).
    // Esto asegura que NO tocaremos la base de datos ni llamaremos a la lógica del ContabilidadService.
    @Mock
    private ReservaRepository reservaRepository; 
    
    @Mock
    private ContabilidadService contabilidadService;
    
    @Mock
    private PasajeroRepository pasajeroRepository;
    
    // Aquí pondremos los métodos de prueba (@Test) en el siguiente paso.
    @Test 
    public void buscarPorId_CuandoExiste_DebeRetornarReserva() {
    
        // --- 1. PREPARACIÓN (ARRANGE) ---
        Long idExistente = 10L;
    
        // Creamos un objeto de prueba (Reserva)
        Reserva reservaSimulada = new Reserva(); 
        // OJO: Asumo que tu clase Reserva tiene un método setId(Long id)
        reservaSimulada.setId(idExistente); 
    
        // Instruir al Mock (el repositorio):
        // Cuando el servicio llame a reservaRepository.findById(10L), Mockito debe devolver 
        // un contenedor Optional lleno con nuestra reserva simulada.
        when(reservaRepository.findById(idExistente)).thenReturn(Optional.of(reservaSimulada)); 

        // --- 2. EJECUCIÓN (ACT) ---
        // Llamamos a la función real que queremos probar
        Optional<Reserva> resultado = reservaService.buscarPorId(idExistente);

        // --- 3. VERIFICACIÓN (ASSERT) ---
        // 3.1. JUnit Assertions: Comprobamos el resultado.
        assertTrue(resultado.isPresent()); // Debe existir (no debe ser Optional.empty())
        assertEquals(idExistente, resultado.get().getId()); 
    
        // 3.2. Mockito Verification: Comprobamos que el repositorio FUE LLAMADO.
        verify(reservaRepository, times(1)).findById(idExistente); // Se debe haber llamado exactamente una vez [4].
    }

   @Test
    public void crearReserva_DebeLlamarRepositorioYSIMULARSeguardar() {
    
    // --- 1. PREPARACIÓN (ARRANGE) ---
    // Creamos una nueva reserva que intentamos guardar
    Reserva reservaInicial = new Reserva(); 
    reservaInicial.setFechaIngreso(LocalDate.now()); 
    
    // Creamos la reserva que SIMULAMOS que la BD nos devuelve (con el ID generado)
    Reserva reservaGuardada = new Reserva();
    reservaGuardada.setId(50L); // Le asignamos un ID que la BD generaría
    reservaGuardada.setFechaIngreso(reservaInicial.getFechaIngreso());
    
    // Instruir al Mock:
    // Cuando el servicio llame a reservaRepository.save(cualquier(Reserva.class)),
    // Mockito debe devolver la reservaGuardada simulada.
    when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada); 

    // --- 2. EJECUCIÓN (ACT) ---
    Reserva resultado = reservaService.crearReserva(reservaInicial);

    // --- 3. VERIFICACIÓN (ASSERT) ---
    assertNotNull(resultado);
    assertNotNull(resultado.getId());
    assertEquals(50L, resultado.getId());
    
    // Verificación de Mockito: Aseguramos que el método save() del repositorio FUE invocado.
    verify(reservaRepository, times(1)).save(any(Reserva.class)); 
    } 

    @Test
    public void facturar_CuandoReservaExiste_DebeGenerarFacturaYDevolverla() {
    
    // --- 1. PREPARACIÓN (ARRANGE) ---
    Long idReserva = 20L;
    Reserva reservaExistente = new Reserva(); 
    reservaExistente.setId(idReserva);
    reservaExistente.setFechaIngreso(LocalDate.of(2024, 10, 10)); 
    
    // 1.2 Simulación de la Factura (Salida)
    Factura facturaGenerada = new Factura();
    facturaGenerada.setImporteTotal(1500.0); 
    // CORRECCIÓN: Usamos el Enum TipoFactura.A
    facturaGenerada.setTipo(TipoFactura.A); 
    
    // Instruir a los Mocks:
    when(reservaRepository.findById(idReserva)).thenReturn(Optional.of(reservaExistente)); 
    when(contabilidadService.generarFactura(any(Reserva.class))).thenReturn(facturaGenerada); 

    // --- 2. EJECUCIÓN (ACT) ---
    Factura resultadoFactura = reservaService.facturar(idReserva);

    // --- 3. VERIFICACIÓN (ASSERT) ---
    assertNotNull(resultadoFactura);
    assertEquals(1500.0, resultadoFactura.getImporteTotal());
    // CORRECCIÓN: Comparamos con el Enum TipoFactura.A
    assertEquals(TipoFactura.A, resultadoFactura.getTipo()); 
    
    verify(reservaRepository, times(1)).findById(idReserva); 
    verify(contabilidadService, times(1)).generarFactura(reservaExistente); 
    }

    @Test
    public void facturar_CuandoReservaNoExiste_DebeLanzarExcepcion() {
    
    // --- 1. PREPARACIÓN (ARRANGE) ---
    Long idInexistente = 999L;
    
    // Instruir al Mock: 
    // Cuando el repositorio reciba findById(999L), debe devolver un Optional vacío (no encontrado).
    when(reservaRepository.findById(idInexistente)).thenReturn(Optional.empty());

    // --- 2. VERIFICACIÓN (ASSERT) y EJECUCIÓN (ACT) ---
    // Usamos assertThrows para verificar que el método facturar lanza la RuntimeException 
    // que se debe producir cuando orElseThrow() encuentra Optional.empty().
    assertThrows(RuntimeException.class, () -> {
        reservaService.facturar(idInexistente);
    });
    
    // --- 3. VERIFICACIÓN DE MOCKS ---
    // Aseguramos que la Capa de Persistencia (el repositorio) fue consultada.
    verify(reservaRepository, times(1)).findById(idInexistente);
    
    // Aseguramos que el ContabilidadService NUNCA fue llamado, pues la excepción se lanzó antes.
    verify(contabilidadService, never()).generarFactura(any());
    }

    @Test
    public void cancelarReserva_DebeLlamarDeleteById() {
    
    // --- 1. PREPARACIÓN (ARRANGE) ---
    Long idParaCancelar = 30L;
    
    // Configuración para simular la existencia del ID (opcional, pero buena práctica si el servicio verifica existencia)
    // En este caso, el servicio debe llamar directamente al método deleteById del CrudRepository
    
    // --- 2. EJECUCIÓN (ACT) ---
    reservaService.cancelarReserva(idParaCancelar);

    // --- 3. VERIFICACIÓN (ASSERT) ---
    // Usamos Mockito para verificar que el método deleteById() del repositorio FUE invocado exactamente una vez.
    verify(reservaRepository, times(1)).deleteById(idParaCancelar);
    }

    @Test
    public void buscarTodas_DebeRetornarConjuntoDeReservas() {
    
    // --- 1. PREPARACIÓN (ARRANGE) ---
    // Creamos una lista simulada de reservas
    Reserva r1 = new Reserva(); r1.setId(1L);
    Reserva r2 = new Reserva(); r2.setId(2L);
    List<Reserva> listaSimulada = Arrays.asList(r1, r2);
    
    // Instruir al Mock: Cuando se llame a findAll, devuelve la lista simulada.
    when(reservaRepository.findAll()).thenReturn(listaSimulada); 

    // --- 2. EJECUCIÓN (ACT) ---
    Set<Reserva> resultado = reservaService.buscarTodas();

    // --- 3. VERIFICACIÓN (ASSERT) ---
    assertNotNull(resultado);
    // Verificar que el tamaño del conjunto sea 2
    assertEquals(2, resultado.size()); 
    
    // Verificar que el repositorio fue llamado.
    verify(reservaRepository, times(1)).findAll();
    }

    @Test
    public void modificarReserva_CuandoExiste_DebeActualizarYRetornarla() {
    
    // --- 1. PREPARACIÓN (ARRANGE) ---
    Long idExistente = 1L;
    
    // Reserva original (la que simula estar en BD)
    Reserva reservaOriginal = new Reserva(); 
    reservaOriginal.setId(idExistente);
    reservaOriginal.setFechaEgreso(LocalDate.of(2024, 12, 20)); 
    
    // Datos actualizados (los que se intentan aplicar)
    Reserva datosNuevos = new Reserva();
    // Simulamos que el usuario cambia la fecha de egreso
    datosNuevos.setFechaEgreso(LocalDate.of(2025, 1, 1)); 
    
    // Instruir al Mock 1: Al buscar por ID, devuelve la original
    when(reservaRepository.findById(idExistente)).thenReturn(Optional.of(reservaOriginal)); 
    
    // Instruir al Mock 2: Al guardar (save), devuelve la reserva ya modificada (la original con la nueva fecha)
    // Usamos when(..).thenReturn(..) para simular el comportamiento de save()
    when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaOriginal);

    // --- 2. EJECUCIÓN (ACT) ---
    Reserva resultado = reservaService.modificarReserva(idExistente, datosNuevos);

    // --- 3. VERIFICACIÓN (ASSERT) ---
    // Verificamos que la fecha haya sido actualizada correctamente en el objeto devuelto
    assertNotNull(resultado);
    assertEquals(idExistente, resultado.getId());
    assertEquals(LocalDate.of(2025, 1, 1), resultado.getFechaEgreso()); 
    
    // Verificamos que el repositorio fue llamado para buscar y para guardar
    verify(reservaRepository, times(1)).findById(idExistente);
    verify(reservaRepository, times(1)).save(reservaOriginal);
    }

    @Test
    public void modificarReserva_CuandoNoExiste_DebeLanzarExcepcion() {
    
    // --- 1. PREPARACIÓN (ARRANGE) ---
    Long idInexistente = 999L;
    Reserva datosNuevos = new Reserva();
    
    // Instruir al Mock: el repositorio devuelve un Optional vacío
    when(reservaRepository.findById(idInexistente)).thenReturn(Optional.empty());

    // --- 2. VERIFICACIÓN (ASSERT) y EJECUCIÓN (ACT) combinadas ---
    // Esperamos una RuntimeException porque la reserva no fue encontrada
    assertThrows(RuntimeException.class, () -> {
        reservaService.modificarReserva(idInexistente, datosNuevos);
    });
    
    // --- 3. VERIFICACIÓN DE MOCKS ---
    // Aseguramos que se intentó buscar, pero NO se intentó guardar
    verify(reservaRepository, times(1)).findById(idInexistente);
    verify(reservaRepository, never()).save(any());
    }

    @Test
    public void realizarCheckIn_DebeGuardarReservaActualizada() {
    
    // --- 1. PREPARACIÓN (ARRANGE) ---
    Reserva reservaParaCheckIn = new Reserva(); 
    reservaParaCheckIn.setId(5L);
    // Nota: Asumo que en el servicio, antes de llamar a save, se le asignó un estado de OCUPADA o similar
    
    // Simular que el repositorio devuelve el objeto guardado (con un ID existente)
    when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaParaCheckIn); 

    // --- 2. EJECUCIÓN (ACT) ---
    // Si tu método retorna algo (por ejemplo, la reserva guardada):
    Reserva resultado = reservaService.realizarCheckIn(reservaParaCheckIn);
    
    // Si tu método es void, simplemente ejecuta:
    // reservaService.realizarCheckIn(reservaParaCheckIn); 

    // --- 3. VERIFICACIÓN (ASSERT) ---
    assertNotNull(resultado);
    assertEquals(5L, resultado.getId());
    
    // Verificación de Mockito: Aseguramos que el método save() del repositorio FUE invocado una vez.
    verify(reservaRepository, times(1)).save(reservaParaCheckIn); 
    }

    @Test
    public void darBajaHuesped_DebeLlamarDeleteByIdEnPasajeroRepository() {
    
    // --- 1. PREPARACIÓN (ARRANGE) ---
    Long idHuesped = 100L;
    
    // --- 2. EJECUCIÓN (ACT) ---
    // Llamamos al método que queremos probar
    reservaService.darBajaHuesped(idHuesped); 

    // --- 3. VERIFICACIÓN (ASSERT) ---
    // Verificamos que el PasajeroRepository, que es la dependencia inyectada, 
    // haya recibido la instrucción de eliminar ese ID exactamente una vez.
    verify(pasajeroRepository, times(1)).deleteById(idHuesped);
    
    // Opcional: Si el servicio de reservas tuviera que usar el ReservaRepository
    // para algo, verificaríamos que ese otro repositorio NO FUE LLAMADO:
    verify(reservaRepository, never()).deleteById(any()); 
    }
}


