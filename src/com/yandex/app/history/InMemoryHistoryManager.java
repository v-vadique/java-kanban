package com.yandex.app.history;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> tail;
    private Node<Task> head;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        } else if (history.containsKey(task.getId())) {
            remove(task.getId());
        }
        history.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {                        // спасибо за подсказку. Так и знал, что можно упростить
        List<Task> historyCopy = new ArrayList<>();
        Node<Task> task = head;
        while (task != null) {
            historyCopy.add(task.getData());
            task = task.getNextNode();
        }
        return historyCopy;
    }

    public Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.setNextNode(newNode);
        return newNode;
    }

    public void removeNode(Node<Task> node) {
        Node<Task> previousNode = node.getPreviousNode();
        Node<Task> nextNode = node.getNextNode();
        if (nextNode != null && previousNode != null) {
            previousNode.setNextNode(nextNode);
            nextNode.setPreviousNode(previousNode);
        }
        if (nextNode == null) {
            tail = previousNode;
            previousNode.setNextNode(null);
        }
        if (previousNode == null) {
            head = nextNode;
            nextNode.setPreviousNode(null);
        }
        history.remove(node.getData().getId());
    }
}
