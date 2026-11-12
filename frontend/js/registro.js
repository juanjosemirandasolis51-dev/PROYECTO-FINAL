const formRegistro = document.getElementById('form-registro');
const mensajeRegistro = document.getElementById('mensaje');

formRegistro.addEventListener('submit', async (evento) => {
  evento.preventDefault();
  ocultarMensaje(mensajeRegistro);

  const nombre = formRegistro.nombre.value.trim();
  const correo = formRegistro.correo.value.trim();
  const contrasena = formRegistro.contrasena.value;
  const confirmacion = formRegistro.confirmacion.value;

  if (!nombre || !correo || !contrasena) {
    return mostrarMensaje(mensajeRegistro, 'Llena todos los campos');
  }
  if (!formRegistro.correo.checkValidity()) {
    return mostrarMensaje(mensajeRegistro, 'El correo no es valido');
  }
  if (contrasena.length < 6) {
    return mostrarMensaje(mensajeRegistro, 'La contraseña debe tener al menos 6 caracteres');
  }
  if (contrasena !== confirmacion) {
    return mostrarMensaje(mensajeRegistro, 'Las contraseñas no coinciden');
  }

  const boton = formRegistro.querySelector('button');
  boton.disabled = true;
  try {
    await Api.registrar(nombre, correo, contrasena);
    location.href = 'index.html?registrado=1';
  } catch (error) {
    mostrarMensaje(mensajeRegistro, error.message);
  } finally {
    boton.disabled = false;
  }
});
