package com.hasnain.restaurant.dsa;

import java.util.NoSuchElementException;

public class LinkedStack<T>{
    private static class Node<T> {
        T value;
        Node<T> next;

        Node(T value , Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node<T> top;
    private int size;

    public void push(T value){
        top = new Node<>(value,top);
        size++;
    }

    public T pop(){
        if(top == null){
            throw new NoSuchElementException("Stack is empty");
        }
        T value = top.value;
        top = top.next;
        size--;
        return value;
    }

    public T peek(){
        if(top == null){
            throw new NoSuchElementException("Stack is empty");
        }
        return top.value;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){
        return size;
    }

    public void clear(){
        top = null;
        size = 0;
    }

}
