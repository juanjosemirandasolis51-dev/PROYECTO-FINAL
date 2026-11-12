if (Sesion.usuario()) location.replace('subastas.html');

const formLogin = document.getElementById('form-login');
const mensajeLogin = document.getElementById('mensaje');

if (parametroUrl('registrado')) {
  mostrarMensaje(mensajeLogin, 'Cuenta creada. Ya puedes iniciar sesion.', 'exito');
}

formLogin.addEventListener('submit', async (evento) => {
  evento.preventDefault();
  ocultarMensaje(mensajeLogin);

  const correo = formLogin.correo.value.trim();
  const contrasena = formLogin.contrasena.value;
  if (!correo || !contrasena) {
    mostrarMensaje(mensajeLogin, 'Escribe tu correo y tu contraseña');
    return;
  }

  const boton = formLogin.querySelector('button');
  boton.disabled = true;
  try {
    const respuesta = await Api.iniciarSesion(correo, contrasena);
    Sesion.guardar(respuesta.token, respuesta.usuario);
    location.href = 'subastas.html';
  } catch (error) {
    mostrarMensaje(mensajeLogin, error.message);
  } finally {
    boton.disabled = false;
  }
});
