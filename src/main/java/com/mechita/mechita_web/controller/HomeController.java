package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.service.CategoriaService;
import com.mechita.mechita_web.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // ⚠️ '/' y '/productos' están en CatalogoController

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    // ⭐ NUEVO: categorías del cliente
    @GetMapping("/categorias")
    public String categorias(Model model) {
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("productoService", productoService);
        return "categorias";
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "carrito";
    }
}