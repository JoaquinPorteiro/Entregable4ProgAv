package com.playlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Mi Playlist
 * Aplicación web para gestionar una playlist de videos musicales
 *
 * @author Entregable 4 - Prog Avanzada 2025
 */
@SpringBootApplication
public class MiPlaylistApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiPlaylistApplication.class, args);
        System.out.println("\n" +
                "=======================================================\n" +
                "  Mi Playlist Musical - Aplicación iniciada\n" +
                "  Accede a: http://localhost:8080\n" +
                "=======================================================\n");
    }
}
