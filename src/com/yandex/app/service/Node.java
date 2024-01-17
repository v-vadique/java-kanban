package com.yandex.app.service;

public class Node<T> {
    private T data;
    private Node<T> previousNode;
    private Node<T> nextNode;

    public Node(Node<T> previousNode, T data, Node<T> nextNode) {
        this.data = data;
        this.previousNode = previousNode;
        this.nextNode = nextNode;
    }

    public void setNextNode(Node<T> newNextNode) {
        this.nextNode = newNextNode;
    }

    public void setPreviousNode(Node<T> newPreviousNode) {
        this.previousNode = newPreviousNode;
    }

    public Node<T> getPreviousNode() {
        return previousNode;
    }

    public Node<T> getNextNode() {
        return nextNode;
    }

    public T getData() {
        return data;
    }
}
