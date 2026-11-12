if (requerirSesion()) {
  pintarEncabezado('subastas.html');
  iniciarRelojes();

  const buscador = document.getElementById('buscador');
  const lista = document.getElementById('lista');
  const vacio = document.getElementById('vacio');
  const mensaje = document.getElementById('mensaje');
  let numeroPeticion = 0;

  async function cargarSubastas() {
    const esta = ++numeroPeticion;
    const filtro = buscador.value.trim();
    try {
      const subastas = await Api.listarSubastas(filtro);
      if (esta !== numeroPeticion) return;
      ocultarMensaje(mensaje);
      pintarLista(subastas, filtro);
    } catch (error) {
      mostrarMensaje(mensaje, error.message);
    }
  }

  function pintarLista(subastas, filtro) {
    lista.innerHTML = subastas.map(s => `
      <a class="tarjeta tarjeta-subasta" href="producto.html?id=${s.id}">
        <h3>${escaparHtml(s.nombre)}</h3>
        <p class="vendedor">Vende ${escaparHtml(s.vendedorNombre)}</p>
        <div class="datos-subasta">
          <div>
            <span class="etiqueta">Precio actual</span>
            <span class="precio">${formatearPrecio(s.precioActual)}</span>
          </div>
          <div>
            <span class="etiqueta">Termina en</span>
            <span class="tiempo" data-fin="${calcularFinLocal(s.segundosRestantes)}">${formatearTiempo(s.segundosRestantes)}</span>
          </div>
        </div>
        <p class="cantidad-pujas">${s.cantidadPujas === 0 ? 'Sin pujas todavia' : s.cantidadPujas === 1 ? '1 puja' : s.cantidadPujas + ' pujas'}</p>
      </a>`).join('');

    vacio.hidden = subastas.length > 0;
    vacio.textContent = filtro
      ? `No hay subastas activas que coincidan con "${filtro}".`
      : 'No hay subastas activas en este momento.';
  }

  let espera;
  buscador.addEventListener('input', () => {
    clearTimeout(espera);
    espera = setTimeout(cargarSubastas, 300);
  });

  cargarSubastas();
  setInterval(cargarSubastas, CONFIG.INTERVALO_ACTUALIZACION_MS);
}
