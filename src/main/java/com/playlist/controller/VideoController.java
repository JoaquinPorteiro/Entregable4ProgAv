package com.playlist.controller;

import com.playlist.model.Video;
import com.playlist.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador principal para manejar las peticiones relacionadas con videos
 * Combina endpoints REST y vistas web
 */
@Controller
@Slf4j
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * Página principal - muestra todos los videos
     */
    @GetMapping("/")
    public String index(Model model) {
        log.info("Accediendo a la página principal");
        List<Video> videos = videoService.obtenerTodosLosVideos();
        VideoService.PlaylistStats stats = videoService.obtenerEstadisticas();

        model.addAttribute("videos", videos);
        model.addAttribute("stats", stats);
        model.addAttribute("titulo", "Mi Playlist Musical");

        return "index";
    }

    /**
     * Página de favoritos
     */
    @GetMapping("/favoritos")
    public String favoritos(Model model) {
        log.info("Accediendo a la página de favoritos");
        List<Video> favoritos = videoService.obtenerFavoritos();

        model.addAttribute("videos", favoritos);
        model.addAttribute("titulo", "Videos Favoritos");
        model.addAttribute("esFavoritos", true);

        return "index";
    }

    /**
     * API REST: Obtener todos los videos
     */
    @GetMapping("/api/videos")
    @ResponseBody
    public ResponseEntity<List<Video>> obtenerVideos() {
        List<Video> videos = videoService.obtenerTodosLosVideos();
        return ResponseEntity.ok(videos);
    }

    /**
     * API REST: Obtener un video por ID
     */
    @GetMapping("/api/videos/{id}")
    @ResponseBody
    public ResponseEntity<Video> obtenerVideo(@PathVariable String id) {
        Optional<Video> video = videoService.obtenerVideoPorId(id);
        return video.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * API REST: Agregar un nuevo video
     */
    @PostMapping("/api/videos")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> agregarVideo(
            @RequestParam String nombre,
            @RequestParam String link) {

        Map<String, Object> response = new HashMap<>();

        try {
            Video nuevoVideo = videoService.agregarVideo(nombre, link);
            response.put("success", true);
            response.put("message", "Video agregado exitosamente");
            response.put("video", nuevoVideo);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Error al agregar video: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * API REST: Eliminar un video
     */
    @DeleteMapping("/api/videos/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarVideo(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        boolean eliminado = videoService.eliminarVideo(id);

        if (eliminado) {
            response.put("success", true);
            response.put("message", "Video eliminado exitosamente");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Video no encontrado");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * API REST: Agregar like a un video
     */
    @PostMapping("/api/videos/{id}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> agregarLike(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Video> video = videoService.agregarLike(id);

        if (video.isPresent()) {
            response.put("success", true);
            response.put("likes", video.get().getLikes());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Video no encontrado");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * API REST: Toggle favorito de un video
     */
    @PostMapping("/api/videos/{id}/favorito")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleFavorito(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Video> video = videoService.toggleFavorito(id);

        if (video.isPresent()) {
            response.put("success", true);
            response.put("favorito", video.get().isFavorito());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Video no encontrado");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * API REST: Obtener estadísticas
     */
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<VideoService.PlaylistStats> obtenerEstadisticas() {
        return ResponseEntity.ok(videoService.obtenerEstadisticas());
    }

    /**
     * API REST: Obtener top videos por likes
     */
    @GetMapping("/api/videos/top/{cantidad}")
    @ResponseBody
    public ResponseEntity<List<Video>> obtenerTopVideos(@PathVariable int cantidad) {
        List<Video> topVideos = videoService.obtenerTopVideos(cantidad);
        return ResponseEntity.ok(topVideos);
    }

    /**
     * Manejador de errores genérico
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("Error en el controlador: ", e);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Error interno del servidor: " + e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
