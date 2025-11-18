package com.playlist;

import com.playlist.model.Video;
import com.playlist.repository.VideoRepository;
import com.playlist.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para VideoService
 * Aplica principios de testing vistos en clase
 */
@DisplayName("Tests del VideoService")
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService;

    private Video videoEjemplo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        videoEjemplo = new Video("Bohemian Rhapsody", "https://www.youtube.com/watch?v=fJ9rUzIMcZQ");
    }

    @Test
    @DisplayName("Debería obtener todos los videos")
    void testObtenerTodosLosVideos() {
        // Arrange
        List<Video> videosEsperados = Arrays.asList(
                new Video("Video 1", "https://www.youtube.com/watch?v=test1"),
                new Video("Video 2", "https://www.youtube.com/watch?v=test2")
        );
        when(videoRepository.findAll()).thenReturn(videosEsperados);

        // Act
        List<Video> videosObtenidos = videoService.obtenerTodosLosVideos();

        // Assert
        assertNotNull(videosObtenidos);
        assertEquals(2, videosObtenidos.size());
        verify(videoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un video por ID")
    void testObtenerVideoPorId() {
        // Arrange
        String id = videoEjemplo.getId();
        when(videoRepository.findById(id)).thenReturn(Optional.of(videoEjemplo));

        // Act
        Optional<Video> resultado = videoService.obtenerVideoPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(videoEjemplo.getNombre(), resultado.get().getNombre());
        verify(videoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debería retornar vacío cuando no encuentra el video")
    void testObtenerVideoPorIdNoExiste() {
        // Arrange
        when(videoRepository.findById("id-inexistente")).thenReturn(Optional.empty());

        // Act
        Optional<Video> resultado = videoService.obtenerVideoPorId("id-inexistente");

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Debería agregar un video exitosamente")
    void testAgregarVideo() {
        // Arrange
        String nombre = "Test Video";
        String link = "https://www.youtube.com/watch?v=test";
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Video videoGuardado = videoService.agregarVideo(nombre, link);

        // Assert
        assertNotNull(videoGuardado);
        assertEquals(nombre, videoGuardado.getNombre());
        assertEquals(0, videoGuardado.getLikes());
        assertFalse(videoGuardado.isFavorito());
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el nombre está vacío")
    void testAgregarVideoNombreVacio() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            videoService.agregarVideo("", "https://www.youtube.com/watch?v=test");
        });
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el link está vacío")
    void testAgregarVideoLinkVacio() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            videoService.agregarVideo("Test", "");
        });
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el link no es de YouTube")
    void testAgregarVideoLinkInvalido() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            videoService.agregarVideo("Test", "https://www.google.com");
        });
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    @DisplayName("Debería eliminar un video exitosamente")
    void testEliminarVideo() {
        // Arrange
        String id = "test-id";
        when(videoRepository.deleteById(id)).thenReturn(true);

        // Act
        boolean eliminado = videoService.eliminarVideo(id);

        // Assert
        assertTrue(eliminado);
        verify(videoRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Debería agregar like a un video")
    void testAgregarLike() {
        // Arrange
        String id = videoEjemplo.getId();
        when(videoRepository.findById(id)).thenReturn(Optional.of(videoEjemplo));
        when(videoRepository.save(any(Video.class))).thenReturn(videoEjemplo);

        // Act
        Optional<Video> resultado = videoService.agregarLike(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getLikes());
        verify(videoRepository, times(1)).findById(id);
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    @DisplayName("Debería alternar favorito de un video")
    void testToggleFavorito() {
        // Arrange
        String id = videoEjemplo.getId();
        assertFalse(videoEjemplo.isFavorito()); // Inicialmente no es favorito

        when(videoRepository.findById(id)).thenReturn(Optional.of(videoEjemplo));
        when(videoRepository.save(any(Video.class))).thenReturn(videoEjemplo);

        // Act
        Optional<Video> resultado = videoService.toggleFavorito(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().isFavorito()); // Ahora es favorito
        verify(videoRepository, times(1)).findById(id);
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    @DisplayName("Debería obtener videos favoritos")
    void testObtenerFavoritos() {
        // Arrange
        Video video1 = new Video("Video 1", "https://www.youtube.com/watch?v=test1");
        video1.toggleFavorito();
        Video video2 = new Video("Video 2", "https://www.youtube.com/watch?v=test2");

        List<Video> favoritos = Arrays.asList(video1);
        when(videoRepository.findFavoritos()).thenReturn(favoritos);

        // Act
        List<Video> resultado = videoService.obtenerFavoritos();

        // Assert
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isFavorito());
        verify(videoRepository, times(1)).findFavoritos();
    }

    @Test
    @DisplayName("Debería obtener top videos por likes")
    void testObtenerTopVideos() {
        // Arrange
        Video video1 = new Video("Video 1", "https://www.youtube.com/watch?v=test1");
        video1.agregarLike();
        video1.agregarLike();

        Video video2 = new Video("Video 2", "https://www.youtube.com/watch?v=test2");
        video2.agregarLike();

        List<Video> topVideos = Arrays.asList(video1, video2);
        when(videoRepository.findTopByLikes(2)).thenReturn(topVideos);

        // Act
        List<Video> resultado = videoService.obtenerTopVideos(2);

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).getLikes() >= resultado.get(1).getLikes());
        verify(videoRepository, times(1)).findTopByLikes(2);
    }

    @Test
    @DisplayName("Debería obtener estadísticas correctamente")
    void testObtenerEstadisticas() {
        // Arrange
        Video video1 = new Video("Video 1", "https://www.youtube.com/watch?v=test1");
        video1.toggleFavorito();
        video1.agregarLike();

        Video video2 = new Video("Video 2", "https://www.youtube.com/watch?v=test2");
        video2.agregarLike();
        video2.agregarLike();

        List<Video> videos = Arrays.asList(video1, video2);
        when(videoRepository.findAll()).thenReturn(videos);

        // Act
        VideoService.PlaylistStats stats = videoService.obtenerEstadisticas();

        // Assert
        assertEquals(2, stats.getTotalVideos());
        assertEquals(1, stats.getTotalFavoritos());
        assertEquals(3, stats.getTotalLikes());
    }
}
