package cine.model;

import java.util.Objects;

public class Pelicula {
    private Long id;
    private String titulo;
    private String director;
    private Integer anio;
    private Integer duracionMin;
    private String genero;

    public Pelicula() {
    }

    public Pelicula(String titulo, String director, Integer anio, Integer duracionMin, String genero) {
        this.titulo = titulo;
        this.director = director;
        this.anio = anio;
        this.duracionMin = duracionMin;
        this.genero = genero;
    }

    //GETTERS
    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDirector() {
        return director;
    }

    public Integer getAnio() {
        return anio;
    }

    public Integer getDuracionMin() {
        return duracionMin;
    }

    public String getGenero() {
        return genero;
    }

    //SETTERS
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public void setDuracionMin(Integer duracionMin) {
        this.duracionMin = duracionMin;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    //VALIDACIONES
    public void validar() {
        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("El titulo es obligatorio.");
        if (titulo.length() > 150) throw new IllegalArgumentException("El titulo excede los 150 caracteres.");
        if (director == null || director.isBlank()) throw new IllegalArgumentException("El director es obligatorio.");
        if (director.length() > 50) throw new IllegalArgumentException("El director excede los 50 caracteres.");
        if (anio == null || anio < 1900 || anio > 2100) throw new IllegalArgumentException("El año debe estar entre 1900 y 2100.");
        if (duracionMin == null || duracionMin <= 0 || duracionMin > 500) throw new IllegalArgumentException("La duracion debe estar entre 1 y 500 min.");
        if (genero == null || genero.isBlank()) throw new IllegalArgumentException("El genero es obligatorio.");
        String g = genero.toUpperCase();
        switch (g) {
            case "ACCION", "DRAMA", "COMEDIA", "ANIMACION", "TERROR", "CIENCIA_FICCION" -> {}
            default -> throw new IllegalArgumentException("Genero invalido");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pelicula p)) return false;
        return Objects.equals(id, p.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
