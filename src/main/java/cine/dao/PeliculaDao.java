package cine.dao;

import java.sql.Statement;
import java.util.Optional;

import cine.db.ConnectionFactory;
import cine.model.Pelicula;

public class PeliculaDao {
    //SQL
    private static final String SQL_INSERT = """
            INSERT INTO Cartelera (titulo, director, anio, duracion_min, genero)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String SQL_FIND_BY_ID = """
            SELECT id, titulo, director, anio, duracion_min, genero
            FROM Cartelera
            WHERE id = ?
            """;

    private static final String SQL_FIND_BY_TITULO_EXACTO = """
            SELECT id, titulo, director, anio, duracion_min, genero
            FROM Cartelera
            WHERE titulo = ?
            """;

    private static final String SQL_UPDATE = """
            UPDATE Cartelera
            SET titulo = ?, director = ?, anio = ?, duracion_min = ?, genero = ?
            WHERE id = ?
            """;

    private static final String SQL_DELETE_BY_ID = """
            DELETE FROM Cartelera
            WHERE id = ?
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

    public Optional<Pelicula> findById(long id) throws Exception {
        try (var con = ConnectionFactory.getConnection();
             var ps = con.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        }
    }

    public Optional<Pelicula> findByTituloExacto(String titulo) throws Exception {
        if (titulo == null || titulo.isBlank()) return Optional.empty();
        try (var con = ConnectionFactory.getConnection();
             var ps = con.prepareStatement(SQL_FIND_BY_TITULO_EXACTO)) {
            ps.setString(1, titulo.trim());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        }
    }

    public int update(Pelicula p) throws Exception {
        if (p.getId() == null) {
            throw new IllegalArgumentException("ID requerido para actualizar.");
        }
        p.validar();
        try (var con = ConnectionFactory.getConnection();
             var ps = con.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, p.getTitulo().trim());
            ps.setString(2, p.getDirector().trim());
            ps.setInt(3, p.getAnio());
            ps.setInt(4, p.getDuracionMin());
            ps.setString(5, p.getGenero().toUpperCase());
            ps.setLong(6, p.getId());
            return ps.executeUpdate(); // esperado: 1
        }
    }

    public int deleteById(long id) throws Exception {
        try (var con = ConnectionFactory.getConnection();
             var ps = con.prepareStatement(SQL_DELETE_BY_ID)) {
            ps.setLong(1, id);
            return ps.executeUpdate(); // 0 = no existía; 1 = eliminado
        }
    }

    private Pelicula mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        var p = new Pelicula();
        p.setId(rs.getLong("id"));
        p.setTitulo(rs.getString("titulo"));
        p.setDirector(rs.getString("director"));
        p.setAnio(rs.getInt("anio"));
        p.setDuracionMin(rs.getInt("duracion_min"));
        p.setGenero(rs.getString("genero"));
        return p;
    }
}
