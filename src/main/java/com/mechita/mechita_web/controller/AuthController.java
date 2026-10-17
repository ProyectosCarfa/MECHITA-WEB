package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Rol;
import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.service.RolService;
import com.mechita.mechita_web.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private RolService rolService;

    // ========== LOGIN ==========

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        Optional<Usuario> usuarioOpt = service.buscarPorCorreo(email);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }

        Usuario u = usuarioOpt.get();

        if (!u.getContrasena().equals(password)) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }

        if (!u.getActivo()) {
            model.addAttribute("error", "Tu cuenta está desactivada");
            return "login";
        }

        // Guardar en sesión
        session.setAttribute("usuarioLogueado", u);

        // ⭐ Redirigir según rol
        if (u.esAdmin()) {
            return "redirect:/admin";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ========== REGISTRO ==========

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String password,
            @RequestParam String passwordConfirm,
            Model model) {

        if (!password.equals(passwordConfirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "registro";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            return "registro";
        }

        if (service.existeCorreo(email)) {
            model.addAttribute("error", "Ese correo ya está registrado");
            return "registro";
        }

        // Obtener rol CLIENTE
        Optional<Rol> rolCliente = rolService.buscarPorNombre("CLIENTE");
        if (rolCliente.isEmpty()) {
            model.addAttribute("error", "Error interno: rol CLIENTE no existe");
            return "registro";
        }

        Usuario nuevo = new Usuario();
        nuevo.setRol(rolCliente.get());
        nuevo.setCorreo(email);
        nuevo.setContrasena(password);
        nuevo.setActivo(true);

        service.guardar(nuevo);

        model.addAttribute("success", "¡Cuenta creada! Ya puedes iniciar sesión");
        return "login";
    }
}