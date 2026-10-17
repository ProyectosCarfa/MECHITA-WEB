package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Helper: verifica que sea admin
    private boolean esAdmin(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        return u != null && u.esAdmin();
    }

    // ===== LISTAR =====

    @GetMapping
    public String listar(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String q,
            HttpSession session,
            Model model) {

        if (!esAdmin(session)) return "redirect:/login";

        List<Usuario> usuarios;

        if (q != null && !q.isBlank()) {
            usuarios = usuarioService.buscarPorCorreoParcial(q);
        } else if ("activos".equals(filtro)) {
            usuarios = usuarioService.listarActivos();
        } else if ("inactivos".equals(filtro)) {
            usuarios = usuarioService.listarInactivos();
        } else if ("admins".equals(filtro)) {
            usuarios = usuarioService.listarPorRol("ADMIN");
        } else if ("clientes".equals(filtro)) {
            usuarios = usuarioService.listarPorRol("CLIENTE");
        } else {
            usuarios = usuarioService.listar();
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("total", usuarioService.contarTotal());
        model.addAttribute("activos", usuarioService.contarActivos());
        model.addAttribute("filtroActual", filtro);
        model.addAttribute("busqueda", q);

        return "roles/usuarios";
    }

    // ===== CAMBIAR ESTADO (activar/desactivar) =====

    @GetMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        usuarioService.cambiarEstado(id);
        return "redirect:/admin/usuarios";
    }

    // ===== ELIMINAR =====

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        usuarioService.eliminar(id);
        return "redirect:/admin/usuarios";
    }
}