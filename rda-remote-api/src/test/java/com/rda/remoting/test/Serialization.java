package com.rda.remoting.test;

import com.rda.remoting.api.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lianrao on 2015/8/11.
 */
public class Serialization {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        File file = new File("person.out");

        ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(file));
        List<Person> person = new ArrayList<>();
        Person p1 = new Person("John", 101, "female");
        person.add(p1);
        Response<List<Person>> res = new Response<>();
        res.setCode("abc");
        res.setData(person);
        res.setMsg("msg");
        oout.writeObject(res);
        oout.close();

        ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file));
        Object newPerson = oin.readObject(); // 没有强制转换到Person类型
        oin.close();
        System.out.println(newPerson);
    }

}

