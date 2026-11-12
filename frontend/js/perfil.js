const usuarioPerfil = requerirSesion();

if (usuarioPerfil) {
  pintarEncabezado('perfil.html');
  document.getElementById('datos-usuario').textContent = `${usuarioPerfil.nombre} - ${usuarioPerfil.correo}`;

  const enlace = (id, nombre) => `<a href="producto.html?id=${id}">${escaparHtml(nombre)}</a>`;

  async function cargarPerfil() {
    try {
      const [ganadas, vendidas] = await Promise.all([Api.misGanadas(), Api.misVendidas()]);

      document.getElementById('ganadas').innerHTML = ganadas.map(g => `
        <tr>
          <td>${enlace(g.productoId, g.nombre)}</td>
          <td class="numero">${formatearPrecio(g.precioFinal)}</td>
          <td>${escaparHtml(g.vendedorNombre)}</td>
          <td>${formatearFecha(g.fechaCierre)}</td>
        </tr>`).join('');
      document.getElementById('sin-ganadas').hidden = ganadas.length > 0;

      document.getElementById('vendidas').innerHTML = vendidas.map(v => `
        <tr>
          <td>${enlace(v.id, v.nombre)}</td>
          <td><span class="insignia ${v.estado}">${v.estado}</span></td>
          <td class="numero">${v.precioFinal == null ? '-' : formatearPrecio(v.precioFinal)}</td>
          <td>${v.ganadorNombre ? escaparHtml(v.ganadorNombre) : (v.estado === 'CERRADA' ? 'Sin pujas' : '-')}</td>
        </tr>`).join('');
      document.getElementById('sin-vendidas').hidden = vendidas.length > 0;
    } catch (error) {
      mostrarMensaje(document.getElementById('mensaje'), error.message);
    }
  }

  cargarPerfil();
  setInterval(cargarPerfil, CONFIG.INTERVALO_ACTUALIZACION_MS);
}
