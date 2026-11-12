package com.example.subastas.Service;

import com.example.subastas.Excepcion.ErrorNegocio;
import com.example.subastas.Model.Dto.*;
import com.example.subastas.Model.Entity.*;
import com.example.subastas.Model.Subasta;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Service
public class SubastaService {

    private final ProductoRepository productoRepository;
    private final PujaRepository pujaRepository;
    private final GanadorRepository ganadorRepository;
    private final UsuarioService usuarioService;

    private final Map<Long, Subasta> subastas = new HashMap<>();
    private final PriorityQueue<Subasta> cierres = new PriorityQueue<>();
    private final Queue<Puja> colaEntrada = new LinkedList<>();

    public SubastaService(ProductoRepository pr, PujaRepository pjr, GanadorRepository gr, UsuarioService us) {

        productoRepository = pr;
        pujaRepository = pjr;
        ganadorRepository = gr;
        usuarioService = us;

    }

    @PostConstruct
    public void cargar() {

        long inicio = System.currentTimeMillis();

        for ( Producto p : productoRepository.findAll() ) {

            Subasta subasta = new Subasta(p);

            for ( Puja puja : pujaRepository.findByProductoIdOrderByFechaHoraAscIdAsc(p.getId()) ) {
                subasta.agregarPuja(puja);
            }

            subastas.put(p.getId(), subasta);

            if ( subasta.estaActiva() ) {
                cierres.add(subasta);
            }

        }

        log.info("Subastas cargadas en memoria: {} activas: {} en {} ms", subastas.size(), cierres.size(), System.currentTimeMillis() - inicio);

    }

    public synchronized SubastaDetalle publicar(Usuario usuario, PublicarRequest r) {

        if ( r.nombre() == null || r.nombre().trim().isEmpty() || r.descripcion() == null || r.descripcion().trim().isEmpty() ) {
            throw new ErrorNegocio(400, "El nombre y la descripcion son obligatorios");
        }

        if ( r.precioBase() == null || r.precioBase() <= 0 ) {
            throw new ErrorNegocio(400, "El precio base debe ser mayor que 0");
        }

        if ( r.duracionMinutos() == null || r.duracionMinutos() < 1 ) {
            throw new ErrorNegocio(400, "La duracion debe ser minimo de 1 minuto");
        }

        LocalDateTime ahora = LocalDateTime.now();

        Producto producto = new Producto(null, usuario.getId(), r.nombre().trim(), r.descripcion().trim(), r.precioBase(), ahora, ahora.plusMinutes(r.duracionMinutos()), "ACTIVA");
        productoRepository.save(producto);

        Subasta subasta = new Subasta(producto);
        subastas.put(producto.getId(), subasta);
        cierres.add(subasta);

        log.info("Producto publicado {} id {} por {} cierra {}", producto.getNombre(), producto.getId(), usuario.getCorreo(), producto.getFechaFin());

        return detalle(subasta);

    }

    public synchronized List<SubastaResumen> listarActivas(String nombre) {

        long inicio = System.nanoTime();

        List<Subasta> encontradas = new ArrayList<>();

        for ( Subasta s : subastas.values() ) {
            if ( !s.estaActiva() ) { continue; }
            if ( nombre != null && !s.getProducto().getNombre().toLowerCase().contains(nombre.trim().toLowerCase()) ) { continue; }
            encontradas.add(s);
        }

        Collections.sort(encontradas);

        List<SubastaResumen> lista = new ArrayList<>();

        for ( Subasta s : encontradas ) {
            Producto p = s.getProducto();
            lista.add(new SubastaResumen(p.getId(), p.getNombre(), s.getPrecioActual(), s.getSegundosRestantes(), usuarioService.nombreDe(p.getVendedorId()), s.getHistorial().size()));
        }

        log.debug("Busqueda de subastas con filtro {} encontro {} en {} microsegundos", nombre, lista.size(), (System.nanoTime() - inicio) / 1000);

        return lista;

    }

    public synchronized SubastaDetalle verDetalle(Long id) {

        return detalle(buscar(id));

    }

    public synchronized List<PujaResponse> verPujas(Long id) {

        List<PujaResponse> lista = new ArrayList<>();

        for ( Puja p : buscar(id).getHistorial() ) {
            lista.add(new PujaResponse(p.getId(), usuarioService.nombreDe(p.getUsuarioId()), p.getMonto(), p.getFechaHora()));
        }

        return lista;

    }

    public synchronized PujaResponse pujar(Usuario usuario, Long id, PujaRequest r) {

        long inicio = System.nanoTime();

        Subasta subasta = buscar(id);
        Double monto = null;

        if ( r != null ) { monto = r.monto(); }

        subasta.validarPuja(usuario.getId(), monto);

        colaEntrada.add(new Puja(null, id, usuario.getId(), monto, LocalDateTime.now()));

        Puja ultima = null;

        while ( !colaEntrada.isEmpty() ) {
            Puja puja = colaEntrada.poll();
            pujaRepository.save(puja);
            subastas.get(puja.getProductoId()).agregarPuja(puja);
            ultima = puja;
        }

        log.info("Puja de {} por {} en el producto {} procesada en {} ms", usuario.getCorreo(), monto, id, (System.nanoTime() - inicio) / 1000000.0);

        return new PujaResponse(ultima.getId(), usuario.getNombre(), ultima.getMonto(), ultima.getFechaHora());

    }

    @Transactional
    @Scheduled(fixedRate = 1000)
    public synchronized void cerrarVencidas() {

        LocalDateTime ahora = LocalDateTime.now();

        while ( !cierres.isEmpty() && !cierres.peek().getProducto().getFechaFin().isAfter(ahora) ) {

            Subasta subasta = cierres.poll();
            Producto producto = subasta.getProducto();

            if ( !subasta.estaActiva() ) { continue; }

            producto.setEstado("CERRADA");
            productoRepository.save(producto);

            Puja mejor = subasta.getMejorPuja();

            if ( mejor == null ) {
                log.info("Subasta {} cerrada sin pujas", producto.getId());
                continue;
            }

            ganadorRepository.save(new Ganador(producto.getId(), mejor.getUsuarioId(), mejor.getMonto(), ahora));

            log.info("Subasta {} cerrada gano {} con {}", producto.getId(), usuarioService.nombreDe(mejor.getUsuarioId()), mejor.getMonto());

        }

    }

    public synchronized List<GanadaResponse> ganadas(Usuario usuario) {

        List<GanadaResponse> lista = new ArrayList<>();

        for ( Ganador g : ganadorRepository.findByUsuarioId(usuario.getId()) ) {
            Producto p = subastas.get(g.getProductoId()).getProducto();
            lista.add(new GanadaResponse(p.getId(), p.getNombre(), g.getPrecioFinal(), g.getFechaCierre(), usuarioService.nombreDe(p.getVendedorId())));
        }

        return lista;

    }

    public synchronized List<VendidaResponse> vendidas(Usuario usuario) {

        List<VendidaResponse> lista = new ArrayList<>();

        for ( Subasta s : subastas.values() ) {

            Producto p = s.getProducto();

            if ( !p.getVendedorId().equals(usuario.getId()) ) { continue; }

            Double precioFinal = null;
            String ganadorNombre = null;

            if ( p.getEstado().equals("CERRADA") && s.getMejorPuja() != null ) {
                precioFinal = s.getMejorPuja().getMonto();
                ganadorNombre = usuarioService.nombreDe(s.getMejorPuja().getUsuarioId());
            }

            lista.add(new VendidaResponse(p.getId(), p.getNombre(), p.getEstado(), p.getPrecioBase(), precioFinal, ganadorNombre));

        }

        return lista;

    }

    public synchronized List<AdminSubastaResponse> todas(Usuario usuario) {

        revisarAdmin(usuario);

        List<AdminSubastaResponse> lista = new ArrayList<>();

        for ( Subasta s : subastas.values() ) {
            Producto p = s.getProducto();
            lista.add(new AdminSubastaResponse(p.getId(), p.getNombre(), usuarioService.nombreDe(p.getVendedorId()), p.getEstado(), s.getPrecioActual(), s.getSegundosRestantes()));
        }

        return lista;

    }

    public synchronized SubastaDetalle cancelar(Usuario usuario, Long id) {

        revisarAdmin(usuario);

        Subasta subasta = buscar(id);

        if ( !subasta.estaActiva() ) {
            throw new ErrorNegocio(400, "Solo se pueden cancelar subastas activas");
        }

        subasta.getProducto().setEstado("CANCELADA");
        productoRepository.save(subasta.getProducto());

        log.info("El administrador {} cancelo la subasta {}", usuario.getCorreo(), id);

        return detalle(subasta);

    }

    private void revisarAdmin(Usuario usuario) {

        if ( !usuario.getRol().equals("ADMIN") ) {
            throw new ErrorNegocio(403, "Solo el administrador puede hacer esto");
        }

    }

    private Subasta buscar(Long id) {

        Subasta subasta = subastas.get(id);

        if ( subasta == null ) {
            throw new ErrorNegocio(404, "El producto no existe");
        }

        return subasta;

    }

    private SubastaDetalle detalle(Subasta s) {

        Producto p = s.getProducto();
        Puja mejor = s.getMejorPuja();

        Double pujaMasAlta = null;
        PersonaResponse ganador = null;

        if ( mejor != null ) {
            pujaMasAlta = mejor.getMonto();
            ganador = new PersonaResponse(mejor.getUsuarioId(), usuarioService.nombreDe(mejor.getUsuarioId()));
        }

        PersonaResponse vendedor = new PersonaResponse(p.getVendedorId(), usuarioService.nombreDe(p.getVendedorId()));

        return new SubastaDetalle(p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecioBase(), pujaMasAlta, ganador, vendedor, p.getEstado(), p.getFechaFin(), s.getSegundosRestantes());

    }

}
