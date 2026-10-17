package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    // GET /admin → dashboard del admin
    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");

        // Doble check: si no está logueado o no es admin → fuera
        if (user == null || !user.esAdmin()) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", user);
        model.addAttribute("totalUsuarios", usuarioService.listar().size());

        return "roles/admin";  // → templates/roles/admin.html
    }
}