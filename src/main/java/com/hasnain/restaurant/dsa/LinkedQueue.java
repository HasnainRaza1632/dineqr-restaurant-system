package com.hasnain.restaurant.dsa;

import java.util.NoSuchElementException;

public class LinkedQueue<T> {


    private static class Node<T>{
        T value;
        Node<T> next;

        public Node(T value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node<T> head; // front of the queue
    private Node<T> tail; // back of the queue
    private int size;


    public void enqueue(T value){
        Node<T> node = new Node<>(value);
        if(tail == null){
            head = tail = node;
        }else{
            tail.next = node;
            tail = node;
        }
        size++;
    }


    public T dequeue(){
        if(isEmpty()){
            throw new NoSuchElementException("Queue is empty");
        }
        T value = head.value;
        head = head.next;

        if(head == null){
            tail = null;
        }
        size--;
        return value;
    }


    public void clear() {
        head = null;
        tail = null;
        size =0;
    }

    public int size(){
        return size;
    }

    public T peek(){
        if(head == null){
            throw new NoSuchElementException("Queue is empty");
        }
        return head.value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

}

