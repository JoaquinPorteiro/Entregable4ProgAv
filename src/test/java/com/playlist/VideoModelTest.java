package com.playlist;

import com.playlist.model.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el modelo Video
 */
@DisplayName("Tests del modelo Video")
class VideoModelTest {

    private Video video;

    @BeforeEach
    void setUp() {
        video = new Video("Test Video", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
    }

    @Test
    @DisplayName("Debería crear un video con valores por defecto")
    void testCrearVideoValoresPorDefecto() {
        assertNotNull(video.getId());
        assertEquals("Test Video", video.getNombre());
        assertEquals(0, video.getLikes());
        assertFalse(video.isFavorito());
        assertNotNull(video.getFechaAgregado());
    }

    @Test
    @DisplayName("Debería incrementar likes correctamente")
    void testAgregarLike() {
        assertEquals(0, video.getLikes());

        video.agregarLike();
        assertEquals(1, video.getLikes());

        video.agregarLike();
        assertEquals(2, video.getLikes());
    }

    @Test
    @DisplayName("Debería alternar favorito correctamente")
    void testToggleFavorito() {
        assertFalse(video.isFavorito());

        video.toggleFavorito();
        assertTrue(video.isFavorito());

        video.toggleFavorito();
        assertFalse(video.isFavorito());
    }

    @Test
    @DisplayName("Debería convertir URL normal de YouTube a embebida")
    void testConvertirUrlNormalAEmbebida() {
        Video video1 = new Video("Video 1", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        assertTrue(video1.getLink().contains("/embed/"));
        assertTrue(video1.getLink().contains("dQw4w9WgXcQ"));
    }

    @Test
    @DisplayName("Debería convertir URL corta de YouTube a embebida")
    void testConvertirUrlCortaAEmbebida() {
        Video video1 = new Video("Video 1", "https://youtu.be/dQw4w9WgXcQ");
        assertTrue(video1.getLink().contains("/embed/"));
        assertTrue(video1.getLink().contains("dQw4w9WgXcQ"));
    }

    @Test
    @DisplayName("Debería mantener URL embebida si ya lo es")
    void testMantenerUrlEmbebida() {
        String urlEmbebida = "https://www.youtube.com/embed/dQw4w9WgXcQ";
        Video video1 = new Video("Video 1", urlEmbebida);
        assertEquals(urlEmbebida, video1.getLink());
    }

    @Test
    @DisplayName("Debería extraer ID de video correctamente")
    void testExtraerVideoId() {
        Video video1 = new Video("Video 1", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        String videoId = video1.getVideoIdParaEmbed();
        assertEquals("dQw4w9WgXcQ", videoId);
    }

    @Test
    @DisplayName("Debería manejar parámetros adicionales en URL")
    void testUrlConParametrosAdicionales() {
        Video video1 = new Video("Video 1", "https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=10s");
        assertTrue(video1.getLink().contains("/embed/"));
        assertTrue(video1.getLink().contains("dQw4w9WgXcQ"));
    }

    @Test
    @DisplayName("Debería generar IDs únicos para cada video")
    void testIdsUnicos() {
        Video video1 = new Video("Video 1", "https://www.youtube.com/watch?v=test1");
        Video video2 = new Video("Video 2", "https://www.youtube.com/watch?v=test2");

        assertNotNull(video1.getId());
        assertNotNull(video2.getId());
        assertNotEquals(video1.getId(), video2.getId());
    }

    @Test
    @DisplayName("Debería tener fecha de agregado")
    void testFechaAgregado() {
        assertNotNull(video.getFechaAgregado());
    }
}
