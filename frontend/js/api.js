async function llamarApi(metodo, ruta, cuerpo) {
  try {
    return await llamarServidor(metodo, ruta, cuerpo);
  } catch (error) {
    if (error instanceof ErrorApi && error.estado === 401 && Sesion.token()) {
      Sesion.cerrar();
      location.replace('index.html');
    }
    throw error;
  }
}

async function llamarServidor(metodo, ruta, cuerpo) {
  const opciones = { method: metodo, headers: {} };

  if (cuerpo !== undefined) {
    opciones.headers['Content-Type'] = 'application/json';
    opciones.body = JSON.stringify(cuerpo);
  }
  const token = Sesion.token();
  if (token) {
    opciones.headers['Authorization'] = 'Bearer ' + token;
  }

  let respuesta;
  try {
    respuesta = await fetch(CONFIG.API_BASE + ruta, opciones);
  } catch (e) {
    throw new ErrorApi(0, 'No se pudo conectar con el servidor');
  }

  const texto = await respuesta.text();
  let datos = null;
  if (texto) {
    try { datos = JSON.parse(texto); }
    catch (e) { throw new ErrorApi(respuesta.status, 'El servidor respondio algo que no es JSON'); }
  }

  if (!respuesta.ok) {
    throw new ErrorApi(respuesta.status, (datos && datos.error) || 'Error ' + respuesta.status);
  }
  return datos;
}

const Api = {
  registrar: (nombre, correo, contrasena) =>
    llamarApi('POST', '/api/registro', { nombre, correo, contrasena }),
  iniciarSesion: (correo, contrasena) =>
    llamarApi('POST', '/api/login', { correo, contrasena }),
  cerrarSesion: () =>
    llamarApi('POST', '/api/logout'),

  listarSubastas: (nombre = '') =>
    llamarApi('GET', '/api/subastas' + (nombre ? '?nombre=' + encodeURIComponent(nombre) : '')),
  publicar: (datos) =>
    llamarApi('POST', '/api/subastas', datos),
  verSubasta: (id) =>
    llamarApi('GET', `/api/subastas/${id}`),
  verPujas: (id) =>
    llamarApi('GET', `/api/subastas/${id}/pujas`),
  pujar: (id, monto) =>
    llamarApi('POST', `/api/subastas/${id}/pujas`, { monto }),

  misGanadas: () =>
    llamarApi('GET', '/api/usuarios/me/ganadas'),
  misVendidas: () =>
    llamarApi('GET', '/api/usuarios/me/vendidas'),

  todasLasSubastas: () =>
    llamarApi('GET', '/api/admin/subastas'),
  cancelarSubasta: (id) =>
    llamarApi('POST', `/api/admin/subastas/${id}/cancelar`)
};
