class ErrorApi extends Error {
  constructor(estado, mensaje) {
    super(mensaje);
    this.estado = estado;
  }
}

const Sesion = {
  guardar(token, usuario) {
    sessionStorage.setItem('token', token);
    sessionStorage.setItem('usuario', JSON.stringify(usuario));
  },
  token() {
    return sessionStorage.getItem('token');
  },
  usuario() {
    const texto = sessionStorage.getItem('usuario');
    return texto ? JSON.parse(texto) : null;
  },
  cerrar() {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('usuario');
  }
};

function requerirSesion(soloAdmin = false) {
  const usuario = Sesion.usuario();
  if (!usuario || !Sesion.token()) {
    location.replace('index.html');
    return null;
  }
  if (soloAdmin && usuario.rol !== 'ADMIN') {
    location.replace('subastas.html');
    return null;
  }
  return usuario;
}

function pintarEncabezado(paginaActual) {
  const usuario = Sesion.usuario();
  const enlaces = [
    ['subastas.html', 'Subastas'],
    ['publicar.html', 'Publicar'],
    ['perfil.html', 'Mi perfil']
  ];
  if (usuario.rol === 'ADMIN') enlaces.push(['admin.html', 'Administracion']);

  const encabezado = document.getElementById('encabezado');
  encabezado.innerHTML = `
    <div class="encabezado-contenido">
      <a class="marca" href="subastas.html">Subastas</a>
      <nav class="menu">
        ${enlaces.map(([href, texto]) =>
          `<a href="${href}"${href === paginaActual ? ' class="activo" aria-current="page"' : ''}>${texto}</a>`
        ).join('')}
      </nav>
      <div class="usuario-actual">
        <span>${escaparHtml(usuario.nombre)}</span>
        <button type="button" class="boton secundario pequeno" id="boton-salir">Salir</button>
      </div>
    </div>`;

  document.getElementById('boton-salir').addEventListener('click', async () => {
    try { await Api.cerrarSesion(); } catch (e) { }
    Sesion.cerrar();
    location.href = 'index.html';
  });
}

const formatoPesos = new Intl.NumberFormat('es-CO', {
  style: 'currency', currency: 'COP', maximumFractionDigits: 0
});

function formatearPrecio(valor) {
  return valor == null ? '-' : formatoPesos.format(valor);
}

function formatearFecha(textoIso) {
  return new Date(textoIso).toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'medium' });
}

function formatearTiempo(segundos) {
  if (segundos <= 0) return 'Cerrando...';
  const h = Math.floor(segundos / 3600);
  const m = Math.floor((segundos % 3600) / 60);
  const s = Math.floor(segundos % 60);
  const ss = String(s).padStart(2, '0');
  return h > 0 ? `${h}h ${m}m ${ss}s` : `${m}m ${ss}s`;
}

function escaparHtml(texto) {
  return String(texto).replace(/[&<>"']/g, c => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[c]));
}

function calcularFinLocal(segundosRestantes) {
  return Date.now() + segundosRestantes * 1000;
}

function segundosHasta(finLocal) {
  return Math.max(0, Math.round((finLocal - Date.now()) / 1000));
}

function iniciarRelojes() {
  setInterval(() => {
    document.querySelectorAll('[data-fin]').forEach(el => {
      const seg = segundosHasta(Number(el.dataset.fin));
      el.textContent = formatearTiempo(seg);
      el.classList.toggle('urgente', seg <= 60);
    });
  }, 1000);
}

function mostrarMensaje(elemento, texto, tipo = 'error') {
  elemento.textContent = texto;
  elemento.className = 'mensaje ' + tipo;
  elemento.hidden = false;
}

function ocultarMensaje(elemento) {
  elemento.hidden = true;
}

function parametroUrl(nombre) {
  return new URLSearchParams(location.search).get(nombre);
}
