package com.playlist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modelo de datos para representar un video musical en la playlist
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    private String id;
    private String nombre;
    private String link;
    private int likes;
    private boolean favorito;
    private LocalDateTime fechaAgregado;

    /**
     * Constructor para crear un nuevo video
     */
    public Video(String nombre, String link) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.link = convertirAUrlEmbebida(link);
        this.likes = 0;
        this.favorito = false;
        this.fechaAgregado = LocalDateTime.now();
    }

    /**
     * Incrementa los likes del video
     */
    public void agregarLike() {
        this.likes++;
    }

    /**
     * Alterna el estado de favorito
     */
    public void toggleFavorito() {
        this.favorito = !this.favorito;
    }

    /**
     * Convierte una URL de YouTube normal a formato embebido
     * Ejemplo: https://www.youtube.com/watch?v=dQw4w9WgXcQ
     *       -> https://www.youtube.com/embed/dQw4w9WgXcQ
     */
    private String convertirAUrlEmbebida(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        // Si ya es una URL embebida, retornar tal cual
        if (url.contains("/embed/")) {
            return url;
        }

        // Extraer el ID del video de YouTube
        String videoId = extraerVideoId(url);
        if (videoId != null) {
            return "https://www.youtube.com/embed/" + videoId;
        }

        return url;
    }

    /**
     * Extrae el ID del video de diferentes formatos de URL de YouTube
     */
    private String extraerVideoId(String url) {
        // Formato: https://www.youtube.com/watch?v=VIDEO_ID
        if (url.contains("watch?v=")) {
            int startIndex = url.indexOf("watch?v=") + 8;
            int endIndex = url.indexOf("&", startIndex);
            if (endIndex == -1) {
                return url.substring(startIndex);
            }
            return url.substring(startIndex, endIndex);
        }

        // Formato: https://youtu.be/VIDEO_ID
        if (url.contains("youtu.be/")) {
            int startIndex = url.indexOf("youtu.be/") + 9;
            int endIndex = url.indexOf("?", startIndex);
            if (endIndex == -1) {
                return url.substring(startIndex);
            }
            return url.substring(startIndex, endIndex);
        }

        return null;
    }

    /**
     * Obtiene el ID del video para la URL embebida
     */
    public String getVideoIdParaEmbed() {
        if (link != null && link.contains("/embed/")) {
            return link.substring(link.lastIndexOf("/") + 1);
        }
        return null;
    }
}
