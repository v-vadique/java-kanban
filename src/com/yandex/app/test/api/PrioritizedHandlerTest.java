package com.yandex.app.test.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.app.api.DurationAdapter;
import com.yandex.app.api.HttpTaskServer;
import com.yandex.app.api.LocalDateTimeAdapter;
import com.yandex.app.model.Epic;
import com.yandex.app.model.StatusName;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest {
    @Test
    public void prioritizedTest() throws IOException, InterruptedException {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        manager.createTask(new Task("имя1", "описание1", StatusName.NEW, 10,
                "10.01.2024-12:00"));
        manager.createTask(new Task("имя2", "описание2", StatusName.DONE, 180,
                "31.05.2017-23:00"));
        manager.createEpic(new Epic("имя3", "описание3"));
        manager.createSubtask(new Subtask("имя4", "описание4", StatusName.NEW,
                600, "31.12.2023-17:00", 3));

        httpTaskServer.start();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonPrioritized = gson.toJson(manager.getPrioritizedTasks());
        assertEquals(jsonPrioritized, response.body());
        httpTaskServer.stop();
    }
}
