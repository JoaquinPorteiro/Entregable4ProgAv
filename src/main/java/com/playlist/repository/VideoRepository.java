package com.playlist.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.playlist.model.Video;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repositorio para persistir videos en formato JSON
 * Aplica el patrón Repository para encapsular la lógica de persistencia
 */
@Repository
public class VideoRepository {

    private static final String DATA_FILE = "src/main/resources/data/videos.json";
    private final Gson gson;
    private final Path dataFilePath;

    public VideoRepository() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        this.dataFilePath = Paths.get(DATA_FILE);
        inicializarArchivo();
    }

    /**
     * Inicializa el archivo JSON si no existe
     */
    private void inicializarArchivo() {
        try {
            if (!Files.exists(dataFilePath)) {
                Files.createDirectories(dataFilePath.getParent());
                guardarTodos(new ArrayList<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar archivo de datos", e);
        }
    }

    /**
     * Obtiene todos los videos
     */
    public List<Video> findAll() {
        try (FileReader reader = new FileReader(DATA_FILE)) {
            Type listType = new TypeToken<ArrayList<Video>>(){}.getType();
            List<Video> videos = gson.fromJson(reader, listType);
            return videos != null ? videos : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Busca un video por su ID
     */
    public Optional<Video> findById(String id) {
        return findAll().stream()
                .filter(video -> video.getId().equals(id))
                .findFirst();
    }

    /**
     * Guarda un nuevo video
     */
    public Video save(Video video) {
        List<Video> videos = findAll();

        // Si el video ya existe, actualizarlo
        Optional<Video> existente = videos.stream()
                .filter(v -> v.getId().equals(video.getId()))
                .findFirst();

        if (existente.isPresent()) {
            videos = videos.stream()
                    .map(v -> v.getId().equals(video.getId()) ? video : v)
                    .collect(Collectors.toList());
        } else {
            videos.add(video);
        }

        guardarTodos(videos);
        return video;
    }

    /**
     * Elimina un video por su ID
     */
    public boolean deleteById(String id) {
        List<Video> videos = findAll();
        int sizeBefore = videos.size();

        videos = videos.stream()
                .filter(video -> !video.getId().equals(id))
                .collect(Collectors.toList());

        if (videos.size() < sizeBefore) {
            guardarTodos(videos);
            return true;
        }
        return false;
    }

    /**
     * Cuenta el total de videos
     */
    public long count() {
        return findAll().size();
    }

    /**
     * Obtiene todos los videos favoritos
     */
    public List<Video> findFavoritos() {
        return findAll().stream()
                .filter(Video::isFavorito)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los videos más populares (ordenados por likes)
     */
    public List<Video> findTopByLikes(int limit) {
        return findAll().stream()
                .sorted((v1, v2) -> Integer.compare(v2.getLikes(), v1.getLikes()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Guarda todos los videos en el archivo JSON
     */
    private void guardarTodos(List<Video> videos) {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            gson.toJson(videos, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar videos", e);
        }
    }

    /**
     * Elimina todos los videos (útil para testing)
     */
    public void deleteAll() {
        guardarTodos(new ArrayList<>());
    }
}
