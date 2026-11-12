package com.example.subastas.Model.Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PujaRepository extends JpaRepository<Puja, Long> {

    List<Puja> findByProductoIdOrderByFechaHoraAscIdAsc(Long productoId);

}
