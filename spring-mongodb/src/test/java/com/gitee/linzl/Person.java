package com.gitee.linzl;

import java.lang.reflect.TypeVariable;

public class Person<T> {
    public static void main(String[] args) {
        TypeVariable[] parameters = Person.class.getTypeParameters();
        for (TypeVariable typeVariable : parameters) {
            System.out.println(typeVariable);
        }
    }
}