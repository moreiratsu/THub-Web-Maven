package com.alvaro.thub.listener;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alvaro.thub.dao.criteria.EventCriteria;
import com.alvaro.thub.model.EventDTO;
import com.alvaro.thub.model.Status;
import com.alvaro.thub.service.EventService;
import com.alvaro.thub.service.ServiceException;
import com.alvaro.thub.service.StatusService;
import com.alvaro.thub.service.impl.EventServiceImpl;
import com.alvaro.thub.service.impl.StatusServiceImpl;
import com.alvaro.thub.utils.Results;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/*
 * Esta clase se encarga de revisar periodicamente las fechas de los eventos para actualizar en segundo
 * plano los que sus fechas de fin coincidan con la fecha actual y les pone estado = finalizado :D
 */

@WebListener
public class EventStatusScheduler implements ServletContextListener {

    private static final Logger logger =
            LogManager.getLogger(EventStatusScheduler.class);

    private ScheduledExecutorService scheduler;
    private EventService eventService;
    private StatusService statusService;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        eventService = new EventServiceImpl();
        statusService = new StatusServiceImpl();

        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Ejecuta una revisión al arrancar y luego vuelve a hacerlo
        // 24 horas después de terminar cada revisión.
        scheduler.scheduleWithFixedDelay(
                this::actualizarEstadosFinalizados,
                0,
                1,
                TimeUnit.DAYS
        );
    }

    private void actualizarEstadosFinalizados() {
        try {
            List<Status> estadosFinalizados =
                    statusService.findByName("Finalizado");

            Long idEstadoFinalizado = null;

            if (estadosFinalizados != null) {
                for (Status estado : estadosFinalizados) {
                    if (estado.getName() != null
                            && estado.getName().trim()
                                    .equalsIgnoreCase("Finalizado")) {
                        idEstadoFinalizado = estado.getId();
                        break;
                    }
                }
            }

            if (idEstadoFinalizado == null) {
                logger.error("No se encontró el estado 'Finalizado'.");
                return;
            }

            Results<EventDTO> resultado =
                    eventService.findBy(
                            new EventCriteria(),
                            1,
                            Integer.MAX_VALUE
                    );

            if (resultado == null || resultado.getPage() == null) {
                return;
            }

            java.sql.Date fechaDeHoy =
                    java.sql.Date.valueOf(LocalDate.now());

            for (EventDTO evento : resultado.getPage()) {
                if (evento == null
                        || evento.getId() == null
                        || evento.getEndDate() == null) {
                    continue;
                }

                String estadoActual = evento.getStatusName();

                if (estadoActual != null
                        && (estadoActual.trim().equalsIgnoreCase("Cancelado")
                        || estadoActual.trim().equalsIgnoreCase("Finalizado"))) {
                    continue;
                }

                if (!evento.getEndDate().after(fechaDeHoy)) {
                    eventService.updateStatus(
                            evento.getId(),
                            idEstadoFinalizado
                    );
                }
            }

        } catch (ServiceException e) {
            logger.error(
                    "Error al actualizar los estados de los eventos finalizados.",
                    e
            );
        } catch (Exception e) {
            // Evita que un error inesperado detenga las próximas ejecuciones.
            logger.error("Error inesperado en la tarea de eventos.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
}