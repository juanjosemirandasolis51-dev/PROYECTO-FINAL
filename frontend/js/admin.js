if (requerirSesion(true)) {
  pintarEncabezado('admin.html');
  iniciarRelojes();

  const tabla = document.getElementById('tabla');
  const mensaje = document.getElementById('mensaje');

  async function cargarTodas() {
    try {
      const subastas = await Api.todasLasSubastas();
      tabla.innerHTML = subastas.map(s => `
        <tr>
          <td>${s.id}</td>
          <td><a href="producto.html?id=${s.id}">${escaparHtml(s.nombre)}</a></td>
          <td>${escaparHtml(s.vendedorNombre)}</td>
          <td><span class="insignia ${s.estado}">${s.estado}</span></td>
          <td class="numero">${formatearPrecio(s.precioActual)}</td>
          <td>${s.estado === 'ACTIVA'
                ? `<span class="tiempo" data-fin="${calcularFinLocal(s.segundosRestantes)}">${formatearTiempo(s.segundosRestantes)}</span>`
                : '-'}</td>
          <td>${s.estado === 'ACTIVA'
                ? `<button type="button" class="boton peligro pequeno" data-cancelar="${s.id}" data-nombre="${escaparHtml(s.nombre)}">Cancelar</button>`
                : ''}</td>
        </tr>`).join('');
      document.getElementById('vacio').hidden = subastas.length > 0;
    } catch (error) {
      mostrarMensaje(mensaje, error.message);
    }
  }

  tabla.addEventListener('click', async (evento) => {
    const boton = evento.target.closest('[data-cancelar]');
    if (!boton) return;
    if (!confirm(`Cancelar la subasta "${boton.dataset.nombre}"? Ya no recibira pujas.`)) return;

    boton.disabled = true;
    try {
      await Api.cancelarSubasta(boton.dataset.cancelar);
      mostrarMensaje(mensaje, `Subasta "${boton.dataset.nombre}" cancelada.`, 'exito');
      cargarTodas();
    } catch (error) {
      mostrarMensaje(mensaje, error.message);
      boton.disabled = false;
    }
  });

  cargarTodas();
  setInterval(cargarTodas, CONFIG.INTERVALO_ACTUALIZACION_MS);
}
