package cine.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import cine.dao.PeliculaDao;
import cine.model.Pelicula;

public class PeliculaService {

    private static final Set<String> GENEROS_VALIDOS = Set.of(
    "ACCION","DRAMA","COMEDIA","ANIMACION","TERROR","CIENCIA_FICCION");
    private final PeliculaDao dao = new PeliculaDao();

    public long agregar(Pelicula p) throws Exception {
        return dao.insertar(p);
    }

    public Optional<Pelicula> buscarPorId(long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("ID invalido. Debe ser mayor que 0.");
        }
        return dao.findById(id);
    }

    public Optional<Pelicula> buscarPorTituloExacto(String titulo) throws Exception {
        if (titulo == null || titulo.isBlank()) {
            return Optional.empty();
        }
        return dao.findByTituloExacto(titulo);
    }

    public void actualizar(Pelicula p) throws Exception {
        if (p == null) {
            throw new IllegalArgumentException("La película no puede ser nula.");
        }
        if (p.getId() == null || p.getId() <= 0) {
            throw new IllegalArgumentException("ID requerido para actualizar.");
        }

        p.validar();

        int filas = dao.update(p);
        if (filas != 1) {
            // Si filas == 0, no existía ese ID (o fue borrado previamente)
            throw new IllegalStateException("No se encontró la pelicula a actualizar (id=" + p.getId() + ").");
        }
    }

    public void eliminarPorId(long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido. Debe ser mayor que 0.");
        }
        int filas = dao.deleteById(id);
        if (filas != 1) {
            throw new IllegalStateException("No se encontró la pelicula a eliminar (id=" + id + ").");
        }
    }

    public List<Pelicula> listarTodas() throws Exception {
    return dao.findAll();  
    }

    public List<Pelicula> listarPorFiltros(String genero, Integer anioDesde, Integer anioHasta) throws Exception {
        Integer desde = anioDesde;
        Integer hasta = anioHasta;

        if (desde != null && (desde < 1900 || desde > 2100)) {
            throw new IllegalArgumentException("El año 'desde' debe estar entre 1900 y 2100.");
        }
        if (hasta != null && (hasta < 1900 || hasta > 2100)) {
            throw new IllegalArgumentException("El año 'hasta' debe estar entre 1900 y 2100.");
        }
        if (desde != null && hasta != null && desde > hasta) {
            throw new IllegalArgumentException("El año 'desde' no puede ser mayor que 'hasta'.");
        }

        String g = (genero == null) ? null : genero.trim();
        if (g == null || g.isEmpty() || "TODOS".equalsIgnoreCase(g)) {
            g = null;
        } else {
            g = g.toUpperCase();
            if (!GENEROS_VALIDOS.contains(g)) {
                throw new IllegalArgumentException("Genero invalido. Valores permitidos: " + GENEROS_VALIDOS);
            }
        }
        return dao.findByCriteria(g, desde, hasta);
    }
}
