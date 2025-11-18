package com.playlist.service;

import com.playlist.model.Video;
import com.playlist.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la lógica de negocio de videos
 * Aplica el principio de responsabilidad única (SRP)
 */
@Service
public class VideoService {

    private static final Logger log = LoggerFactory.getLogger(VideoService.class);

    private final VideoRepository videoRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    /**
     * Obtiene todos los videos de la playlist
     */
    public List<Video> obtenerTodosLosVideos() {
        log.info("Obteniendo todos los videos");
        return videoRepository.findAll();
    }

    /**
     * Obtiene un video por su ID
     */
    public Optional<Video> obtenerVideoPorId(String id) {
        log.info("Buscando video con ID: {}", id);
        return videoRepository.findById(id);
    }

    /**
     * Agrega un nuevo video a la playlist
     */
    public Video agregarVideo(String nombre, String link) {
        validarDatosVideo(nombre, link);

        Video nuevoVideo = new Video(nombre, link);
        Video videoGuardado = videoRepository.save(nuevoVideo);

        log.info("Video agregado exitosamente: {} - {}", nombre, videoGuardado.getId());
        return videoGuardado;
    }

    /**
     * Elimina un video de la playlist
     */
    public boolean eliminarVideo(String id) {
        log.info("Intentando eliminar video con ID: {}", id);
        boolean eliminado = videoRepository.deleteById(id);

        if (eliminado) {
            log.info("Video eliminado exitosamente: {}", id);
        } else {
            log.warn("No se encontró el video con ID: {}", id);
        }

        return eliminado;
    }

    /**
     * Incrementa los likes de un video
     */
    public Optional<Video> agregarLike(String id) {
        log.info("Agregando like al video: {}", id);

        Optional<Video> videoOpt = videoRepository.findById(id);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            video.agregarLike();
            videoRepository.save(video);
            log.info("Like agregado. Total likes: {}", video.getLikes());
            return Optional.of(video);
        }

        log.warn("No se pudo agregar like. Video no encontrado: {}", id);
        return Optional.empty();
    }

    /**
     * Alterna el estado de favorito de un video
     */
    public Optional<Video> toggleFavorito(String id) {
        log.info("Cambiando estado de favorito del video: {}", id);

        Optional<Video> videoOpt = videoRepository.findById(id);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            video.toggleFavorito();
            videoRepository.save(video);
            log.info("Estado de favorito actualizado: {}", video.isFavorito());
            return Optional.of(video);
        }

        log.warn("No se pudo cambiar favorito. Video no encontrado: {}", id);
        return Optional.empty();
    }

    /**
     * Obtiene solo los videos marcados como favoritos
     */
    public List<Video> obtenerFavoritos() {
        log.info("Obteniendo videos favoritos");
        return videoRepository.findFavoritos();
    }

    /**
     * Obtiene los videos más populares (top N por likes)
     */
    public List<Video> obtenerTopVideos(int cantidad) {
        // Validar que no se pidan demasiados videos
        if (cantidad > 100) {
            cantidad = 100;
        }

        // Validar cantidad mínima
        if (cantidad < 1) {
            cantidad = 1;
        }

        log.info("Obteniendo top {} videos por likes", cantidad);
        return videoRepository.findTopByLikes(cantidad);
    }

    /**
     * Obtiene estadísticas de la playlist
     */
    public PlaylistStats obtenerEstadisticas() {
        List<Video> videos = videoRepository.findAll();
        long totalVideos = videos.size();
        long totalFavoritos = videos.stream().filter(Video::isFavorito).count();
        long totalLikes = videos.stream().mapToLong(Video::getLikes).sum();

        return new PlaylistStats(totalVideos, totalFavoritos, totalLikes);
    }

    /**
     * Valida los datos de un video antes de guardarlo
     */
    private void validarDatosVideo(String nombre, String link) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del video no puede estar vacío");
        }

        if (link == null || link.trim().isEmpty()) {
            throw new IllegalArgumentException("El link del video no puede estar vacío");
        }

        if (!esUrlValida(link)) {
            throw new IllegalArgumentException("El link proporcionado no es una URL válida de YouTube");
        }
    }

    /**
     * Valida si una URL es válida (formato básico de YouTube)
     */
    private boolean esUrlValida(String url) {
        return url.contains("youtube.com") || url.contains("youtu.be");
    }

    /**
     * Clase interna para estadísticas de la playlist
     */
    public static class PlaylistStats {
        private final long totalVideos;
        private final long totalFavoritos;
        private final long totalLikes;

        public PlaylistStats(long totalVideos, long totalFavoritos, long totalLikes) {
            this.totalVideos = totalVideos;
            this.totalFavoritos = totalFavoritos;
            this.totalLikes = totalLikes;
        }

        public long getTotalVideos() {
            return totalVideos;
        }

        public long getTotalFavoritos() {
            return totalFavoritos;
        }

        public long getTotalLikes() {
            return totalLikes;
        }
    }
}
