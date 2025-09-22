package cine.service;

import cine.dao.PeliculaDao;
import cine.model.Pelicula;

public class PeliculaService {
    private final PeliculaDao dao = new PeliculaDao();

    public long agregar(Pelicula p) throws Exception {
        return dao.insertar(p);
    }
}
