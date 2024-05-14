package com.yandex.app.test.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.app.api.DurationAdapter;
import com.yandex.app.api.HttpTaskServer;
import com.yandex.app.api.LocalDateTimeAdapter;
import com.yandex.app.model.StatusName;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TasksHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer httpTaskServer;
    Gson gson;
    @BeforeEach
    public void beforeEach() throws IOException {
        httpTaskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.intializeGson();
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
        httpTaskServer.start();
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
    }

    @Test
    public void getTasksTest() throws IOException, InterruptedException {
        manager.createTask(new Task("имя1", "описание1", StatusName.NEW, 10,
                "10.01.2024-12:00"));
        manager.createTask(new Task("имя2", "описание2", StatusName.NEW, 600,
                "10.01.2024-12:00"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String bodyString = response.body();

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getAllTasks()), bodyString, "Данные не совпадают");
    }

    @Test
    public void getTaskByIdTest() throws IOException, InterruptedException {
        manager.createTask(new Task("имя1", "описание1", StatusName.NEW, 10,
                "10.01.2024-12:00"));
        manager.createTask(new Task("имя2", "описание2", StatusName.NEW, 600,
                "10.01.2024-12:00"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(1, task.getId(), "Неверный ID задачи");
        assertEquals("имя1", task.getName(), "Неверное имя задачи");
    }

    @Test
    public void createTaskTest() throws IOException, InterruptedException {
        Task task = new Task("имя1", "описание1", StatusName.NEW, 5,
                "10.01.2024-12:00");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("имя1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        manager.createTask(new Task("имя1", "описание1", StatusName.NEW, 10,
                "10.01.2024-12:00"));
        Task task = new Task("new name", "new description", StatusName.DONE, 10,
                "10.01.2024-12:00");
        task.setId(1);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task updatedTask = manager.getTaskById(1);

        assertEquals(201, response.statusCode());
        assertEquals("new name", updatedTask.getName(), "Неверное имя задачи");
        assertEquals(StatusName.DONE, updatedTask.getStatus(), "Неверный статус задачи");
    }

    @Test
    public void deleteTaskTest() throws IOException, InterruptedException {
        manager.createTask(new Task("имя1", "описание1", StatusName.NEW, 10,
                "10.01.2024-12:00"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = manager.getAllTasks();

        assertEquals(200, response.statusCode());
        assertEquals(0, tasks.size(), "Неверный размер");
    }
}
