package com.example.subastas.Model.Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GanadorRepository extends JpaRepository<Ganador, Long> {

    List<Ganador> findByUsuarioId(Long usuarioId);

}
