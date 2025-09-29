package cine.service;

import java.util.Optional;

import cine.dao.PeliculaDao;
import cine.model.Pelicula;

public class PeliculaService {
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
}
