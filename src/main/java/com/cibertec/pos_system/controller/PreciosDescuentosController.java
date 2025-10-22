package com.cibertec.pos_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PreciosDescuentosController {
    
    @GetMapping("/preciosDescuentos")
    public String mostrarPreciosDescuentos() {
        return "preciosDescuentos"; //aqui tenemos el nombre del archivo HTML
    }
}
