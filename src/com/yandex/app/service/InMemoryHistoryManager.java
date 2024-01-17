package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node<Task>> history = new HashMap<>();
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
    public List<Task> getHistory() {                        // Кажется перемудрил
        List<Task> historyCopy = new ArrayList<>();
        Node<Task> task = head;
        historyCopy.add(task.getData());
        while (task.getNextNode() != null) {
            task = task.getNextNode();
            historyCopy.add(task.getData());
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
