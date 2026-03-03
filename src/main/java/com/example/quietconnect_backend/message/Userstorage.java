package com.example.quietconnect_backend.message;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class Userstorage {

    private static Userstorage userstorage;
    private Set<String> userList;
    

    private Userstorage(){
        userList=new HashSet<>();
    }

    public static synchronized Userstorage getUserstorage(){

        if(userstorage==null){
            userstorage=new Userstorage();
        }

        return userstorage;
    }

    public Set<String> getUsers(){
        return userList;
    }

    public void setUser(String name) throws Exception{
        if(userList.contains(name)){
            throw new Exception("Name already exists");
        }

        userList.add(name);
    }
}
