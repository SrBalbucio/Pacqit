package org.example;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    public Main(){
        System.out.println("Estou funcionando!");
        Form form = new Form(this);
        form.open();
    }

    public void chamadaTeste(String teste){
        System.out.println(teste);
    }
}
