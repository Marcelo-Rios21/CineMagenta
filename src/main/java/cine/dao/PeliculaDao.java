package cine.dao;

import java.sql.Statement;

import cine.db.ConnectionFactory;
import cine.model.Pelicula;

public class PeliculaDao {
    private static final String SQL_INSERT = """
            INSERT INTO Cartelera (titulo, director, anio, duracion_min, genero)
            VALUES (?, ?, ?, ?, ?)
            """;

    public long insertar(Pelicula p) throws Exception {
        p.validar();
        try (var con = ConnectionFactory.getConnection();
        var ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getTitulo().trim());
            ps.setString(2, p.getDirector().trim());
            ps.setInt(3, p.getAnio());
            ps.setInt(4, p.getDuracionMin());
            ps.setString(5, p.getGenero().toUpperCase());
            int filas = ps.executeUpdate();
            if (filas != 1) throw new IllegalStateException("No se insertó la película");
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    p.setId(id);
                    return id;
                } else {
                    throw new IllegalStateException("No se obtuvo el id generado");
                }
            }
        }
    }
}
