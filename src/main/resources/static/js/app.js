/**
 * JavaScript para Mi Playlist Musical
 * Maneja todas las interacciones del cliente
 */

$(document).ready(function() {
    console.log('Mi Playlist Musical - Aplicación cargada');

    // Configuración inicial
    setupEventListeners();
});

/**
 * Configura todos los event listeners
 */
function setupEventListeners() {
    // Botón de guardar video
    $('#btnGuardarVideo').click(agregarVideo);

    // Submit del formulario con Enter
    $('#agregarVideoForm').submit(function(e) {
        e.preventDefault();
        agregarVideo();
    });

    // Botones de like
    $(document).on('click', '.like-btn', function() {
        const videoId = $(this).data('video-id');
        agregarLike(videoId, $(this));
    });

    // Botones de favorito
    $(document).on('click', '.favorito-btn', function() {
        const videoId = $(this).data('video-id');
        toggleFavorito(videoId, $(this));
    });

    // Botones de eliminar
    $(document).on('click', '.delete-btn', function() {
        const videoId = $(this).data('video-id');
        const videoNombre = $(this).data('video-nombre');
        confirmarEliminarVideo(videoId, videoNombre);
    });
}

/**
 * Agrega un nuevo video a la playlist
 */
function agregarVideo() {
    const nombre = $('#nombreVideo').val().trim();
    const link = $('#linkVideo').val().trim();

    // Validación básica
    if (!nombre || !link) {
        mostrarError('Por favor completa todos los campos');
        return;
    }

    if (!esUrlYouTubeValida(link)) {
        mostrarError('Por favor ingresa una URL válida de YouTube');
        return;
    }

    // Mostrar loading
    const btnGuardar = $('#btnGuardarVideo');
    const textoOriginal = btnGuardar.html();
    btnGuardar.html('<span class="spinner-border spinner-border-sm me-2"></span>Guardando...').prop('disabled', true);

    // Realizar petición AJAX
    $.ajax({
        url: '/api/videos',
        type: 'POST',
        data: {
            nombre: nombre,
            link: link
        },
        success: function(response) {
            if (response.success) {
                // Cerrar modal y limpiar formulario
                $('#agregarVideoModal').modal('hide');
                limpiarFormulario();

                // Mostrar notificación de éxito
                mostrarNotificacion('Video agregado exitosamente', 'success');

                // Recargar página después de un breve delay
                setTimeout(function() {
                    location.reload();
                }, 1000);
            } else {
                mostrarError(response.message);
            }
        },
        error: function(xhr) {
            const errorMsg = xhr.responseJSON?.message || 'Error al agregar el video';
            mostrarError(errorMsg);
        },
        complete: function() {
            btnGuardar.html(textoOriginal).prop('disabled', false);
        }
    });
}

/**
 * Agrega un like a un video
 */
function agregarLike(videoId, boton) {
    $.ajax({
        url: `/api/videos/${videoId}/like`,
        type: 'POST',
        success: function(response) {
            if (response.success) {
                // Actualizar contador
                boton.find('.like-count').text(response.likes);

                // Animación
                boton.find('i').addClass('fa-beat');
                setTimeout(function() {
                    boton.find('i').removeClass('fa-beat');
                }, 500);

                mostrarNotificacion('¡Like agregado!', 'success');
            }
        },
        error: function() {
            mostrarNotificacion('Error al agregar like', 'danger');
        }
    });
}

/**
 * Alterna el estado de favorito de un video
 */
function toggleFavorito(videoId, boton) {
    $.ajax({
        url: `/api/videos/${videoId}/favorito`,
        type: 'POST',
        success: function(response) {
            if (response.success) {
                // Actualizar estado del botón
                if (response.favorito) {
                    boton.removeClass('btn-outline-warning').addClass('btn-warning');
                    mostrarNotificacion('¡Agregado a favoritos!', 'warning');
                } else {
                    boton.removeClass('btn-warning').addClass('btn-outline-warning');
                    mostrarNotificacion('Removido de favoritos', 'info');
                }

                // Animación
                boton.addClass('pulse');
                setTimeout(function() {
                    boton.removeClass('pulse');
                }, 500);
            }
        },
        error: function() {
            mostrarNotificacion('Error al actualizar favorito', 'danger');
        }
    });
}

/**
 * Confirma y elimina un video
 */
function confirmarEliminarVideo(videoId, videoNombre) {
    if (confirm(`¿Estás seguro de que quieres eliminar "${videoNombre}"?`)) {
        eliminarVideo(videoId);
    }
}

/**
 * Elimina un video de la playlist
 */
function eliminarVideo(videoId) {
    $.ajax({
        url: `/api/videos/${videoId}`,
        type: 'DELETE',
        success: function(response) {
            if (response.success) {
                mostrarNotificacion('Video eliminado exitosamente', 'success');

                // Recargar página después de un breve delay
                setTimeout(function() {
                    location.reload();
                }, 1000);
            }
        },
        error: function() {
            mostrarNotificacion('Error al eliminar el video', 'danger');
        }
    });
}

/**
 * Valida si una URL es de YouTube
 */
function esUrlYouTubeValida(url) {
    const regexYouTube = /^(https?:\/\/)?(www\.)?(youtube\.com|youtu\.be)\/.+/;
    return regexYouTube.test(url);
}

/**
 * Muestra un error en el modal
 */
function mostrarError(mensaje) {
    const divError = $('#mensajeError');
    divError.text(mensaje).removeClass('d-none');

    // Ocultar después de 5 segundos
    setTimeout(function() {
        divError.addClass('d-none');
    }, 5000);
}

/**
 * Limpia el formulario de agregar video
 */
function limpiarFormulario() {
    $('#nombreVideo').val('');
    $('#linkVideo').val('');
    $('#mensajeError').addClass('d-none');
}

/**
 * Muestra una notificación temporal
 */
function mostrarNotificacion(mensaje, tipo) {
    // Crear elemento de notificación
    const iconos = {
        'success': 'fa-check-circle',
        'danger': 'fa-times-circle',
        'warning': 'fa-star',
        'info': 'fa-info-circle'
    };

    const toastHtml = `
        <div class="toast align-items-center text-white bg-${tipo} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas ${iconos[tipo]} me-2"></i>
                    ${mensaje}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    // Agregar al DOM
    const toastElement = $(toastHtml).appendTo('body');

    // Mostrar toast
    const toast = new bootstrap.Toast(toastElement[0], {
        autohide: true,
        delay: 3000
    });

    toast.show();

    // Remover del DOM después de ocultarse
    toastElement.on('hidden.bs.toast', function() {
        $(this).remove();
    });
}

/**
 * Resetear el modal cuando se cierra
 */
$('#agregarVideoModal').on('hidden.bs.modal', function() {
    limpiarFormulario();
});
