package com.example.allamvizsgateszt2.API

class Applications(var applications: MutableList<String>){
    private var counter=0
    public fun increaseCounter(){
        ++counter;
    }
    public fun getCounter():Int{
        return counter
    }
}