const usuarioProducto = requerirSesion();

if (usuarioProducto) {
  pintarEncabezado('');
  iniciarRelojes();

  const id = parametroUrl('id');
  const $ = (idElemento) => document.getElementById(idElemento);
  const formPuja = $('form-puja');
  const mensajePuja = $('mensaje-puja');
  let temporizador = null;

  async function cargar() {
    try {
      const [subasta, pujas] = await Promise.all([Api.verSubasta(id), Api.verPujas(id)]);
      pintarDetalle(subasta);
      pintarHistorial(pujas);
      $('contenido').hidden = false;
      ocultarMensaje($('mensaje-carga'));

      if (subasta.estado !== 'ACTIVA' && temporizador) {
        clearInterval(temporizador);
        temporizador = null;
      }
    } catch (error) {
      mostrarMensaje($('mensaje-carga'), error.message);
      if (error.estado === 404) $('contenido').hidden = true;
    }
  }

  function nombreDe(persona) {
    if (!persona) return '-';
    return persona.id === usuarioProducto.id ? 'Tu' : escaparHtml(persona.nombre);
  }

  function pintarDetalle(s) {
    document.title = s.nombre + ' - Subastas';
    $('nombre').textContent = s.nombre;
    $('estado').textContent = s.estado;
    $('estado').className = 'insignia ' + s.estado;
    $('descripcion').textContent = s.descripcion;
    $('precio-base').textContent = formatearPrecio(s.precioBase);
    $('puja-mas-alta').textContent = s.pujaMasAlta == null ? 'Sin pujas' : formatearPrecio(s.pujaMasAlta);
    $('ganador').innerHTML = nombreDe(s.ganadorActual);
    $('vendedor').innerHTML = nombreDe(s.vendedor);
    $('fecha-fin').textContent = formatearFecha(s.fechaFin);
    $('titulo-ganador').textContent = s.estado === 'CERRADA' ? 'Ganador' : 'Va ganando';

    const tiempo = $('tiempo');
    if (s.estado === 'ACTIVA') {
      tiempo.dataset.fin = calcularFinLocal(s.segundosRestantes);
      tiempo.textContent = formatearTiempo(s.segundosRestantes);
    } else {
      delete tiempo.dataset.fin;
      tiempo.classList.remove('urgente');
      tiempo.textContent = s.estado === 'CERRADA' ? 'Terminada' : 'Cancelada';
    }

    const minimo = s.pujaMasAlta == null ? s.precioBase : s.pujaMasAlta + 1;
    formPuja.monto.min = minimo;
    $('minimo').textContent = s.pujaMasAlta == null
      ? `Minimo ${formatearPrecio(s.precioBase)} (igual o mayor al precio base)`
      : `Debe ser mayor que ${formatearPrecio(s.pujaMasAlta)}`;

    const activa = s.estado === 'ACTIVA';
    $('zona-puja').hidden = !activa;
    $('zona-resultado').hidden = activa;
    if (!activa) $('zona-resultado').innerHTML = textoResultado(s);
  }

  function textoResultado(s) {
    if (s.estado === 'CANCELADA') {
      return '<h2>Subasta cancelada</h2><p>El administrador cancelo esta subasta.</p>';
    }
    if (!s.ganadorActual) {
      return '<h2>Subasta cerrada</h2><p>Se acabo el tiempo y nadie pujo.</p>';
    }
    const esMio = s.ganadorActual.id === usuarioProducto.id;
    return `<h2>${esMio ? 'Ganaste esta subasta!' : 'Subasta cerrada'}</h2>
            <span class="etiqueta">Precio final</span>
            <span class="precio">${formatearPrecio(s.pujaMasAlta)}</span>
            <p>Ganador: <strong>${nombreDe(s.ganadorActual)}</strong></p>`;
  }

  function pintarHistorial(pujas) {
    $('historial').innerHTML = pujas.map((p, i) => `
      <tr${i === pujas.length - 1 ? ' class="mas-alta"' : ''}>
        <td>${i + 1}</td>
        <td>${escaparHtml(p.usuarioNombre)}</td>
        <td class="numero">${formatearPrecio(p.monto)}</td>
        <td>${formatearFecha(p.fechaHora)}</td>
      </tr>`).join('');
    $('sin-pujas').hidden = pujas.length > 0;
  }

  formPuja.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    ocultarMensaje(mensajePuja);

    const monto = Number(formPuja.monto.value);
    if (!formPuja.monto.value || !(monto > 0)) {
      return mostrarMensaje(mensajePuja, 'Escribe un monto valido');
    }

    const boton = formPuja.querySelector('button');
    boton.disabled = true;
    try {
      await Api.pujar(id, monto);
      formPuja.reset();
      mostrarMensaje(mensajePuja, 'Puja registrada.', 'exito');
      setTimeout(() => ocultarMensaje(mensajePuja), 4000);
      await cargar();
    } catch (error) {
      mostrarMensaje(mensajePuja, error.message);
      cargar();
    } finally {
      boton.disabled = false;
    }
  });

  if (!id) {
    mostrarMensaje($('mensaje-carga'), 'Falta el producto en la direccion (producto.html?id=...)');
  } else {
    cargar();
    temporizador = setInterval(cargar, CONFIG.INTERVALO_ACTUALIZACION_MS);
  }
}
