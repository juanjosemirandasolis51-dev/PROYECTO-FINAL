if (requerirSesion()) {
  pintarEncabezado('publicar.html');

  const form = document.getElementById('form-publicar');
  const mensaje = document.getElementById('mensaje');

  form.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    ocultarMensaje(mensaje);

    const datos = {
      nombre: form.nombre.value.trim(),
      descripcion: form.descripcion.value.trim(),
      precioBase: Number(form.precioBase.value),
      duracionMinutos: Number(form.duracionMinutos.value)
    };

    if (!datos.nombre || !datos.descripcion) {
      return mostrarMensaje(mensaje, 'El nombre y la descripcion son obligatorios');
    }
    if (!(datos.precioBase > 0)) {
      return mostrarMensaje(mensaje, 'El precio base debe ser mayor que 0');
    }
    if (!Number.isInteger(datos.duracionMinutos) || datos.duracionMinutos < 1) {
      return mostrarMensaje(mensaje, 'La duracion debe ser un numero entero de minutos (minimo 1)');
    }

    const boton = form.querySelector('button');
    boton.disabled = true;
    try {
      const producto = await Api.publicar(datos);
      location.href = 'producto.html?id=' + producto.id;
    } catch (error) {
      mostrarMensaje(mensaje, error.message);
      boton.disabled = false;
    }
  });
}
