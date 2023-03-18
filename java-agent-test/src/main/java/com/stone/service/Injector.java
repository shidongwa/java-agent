package com.stone.service;

import java.util.Scanner;

public class Injector {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("classloader:" + sc.getClass().getClassLoader());
        System.out.println("current classloader:" + new Injector().getClass().getClassLoader());
        System.out.println(sc.nextLine());
        sc.close();
    }

    public static void insert() {
        System.out.println("Static method injected.");
    }

}
