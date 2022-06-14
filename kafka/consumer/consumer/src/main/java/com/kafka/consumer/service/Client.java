package com.kafka.consumer.service;

import javax.print.attribute.HashAttributeSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Client {

    public static void main(String[] args) {
        //Map<Integer,Employee> emp = new HashMap<>();
        Set<Employee> set = new HashSet<>();

        Employee  e1 = new Employee(1,2300);
        Employee e2 = new Employee(2,2311);
        Employee e3 = new Employee(1,7699);


        set.add(e1);
        set.add(e2);
        set.add(e3);

        set.forEach((s)-> System.out.println(s));

    }
}
