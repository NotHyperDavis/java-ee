package com.example;
import java.nio.file.Files;
import java.nio.file.Path;

public class fileManip {
    public static void main(String[] args) throws Exception{ 
        
    
    Path path = Path.of("dados.txt");
    Files.writeString(path, "Dados Interessantes");
    }
}
