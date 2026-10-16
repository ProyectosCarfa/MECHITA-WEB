package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Categoria;
import com.mechita.mechita_web.model.Producto;
import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.service.CategoriaService;
import com.mechita.mechita_web.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    private boolean esAdmin(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        return u != null && u.esAdmin();
    }

    // ===== LISTAR =====

    @GetMapping
    public String listar(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long cat,
            HttpSession session,
            Model model) {

        if (!esAdmin(session)) return "redirect:/login";

        List<Producto> productos;

        if (q != null && !q.isBlank()) {
            productos = productoService.buscarPorNombre(q);
        } else if (cat != null) {
            productos = productoService.listarPorCategoria(cat);
        } else if ("activos".equals(filtro)) {
            productos = productoService.listarActivos();
        } else if ("inactivos".equals(filtro)) {
            productos = productoService.listarInactivos();
        } else if ("stock-bajo".equals(filtro)) {
            productos = productoService.listarStockBajo();
        } else {
            productos = productoService.listar();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("total", productoService.contarTotal());
        model.addAttribute("activos", productoService.contarActivos());
        model.addAttribute("filtroActual", filtro);
        model.addAttribute("busqueda", q);
        model.addAttribute("categoriaSeleccionada", cat);

        return "roles/productos";
    }

    // ===== FORM NUEVO =====

    @GetMapping("/nuevo")
    public String nuevoForm(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";

        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("modo", "crear");

        return "roles/productos-form";
    }

    // ===== FORM EDITAR =====

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";

        Optional<Producto> p = productoService.buscarPorId(id);
        if (p.isEmpty()) return "redirect:/admin/productos";

        model.addAttribute("producto", p.get());
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("modo", "editar");

        return "roles/productos-form";
    }

    // ===== GUARDAR (crear o actualizar) =====

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Long idProducto,
            @RequestParam Long idCategoria,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam java.math.BigDecimal precio,
            @RequestParam Integer stock,
            @RequestParam(required = false) String imagenUrl,
            HttpSession session) {

        if (!esAdmin(session)) return "redirect:/login";

        Optional<Categoria> cat = categoriaService.buscarPorId(idCategoria);
        if (cat.isEmpty()) return "redirect:/admin/productos";

        Producto p;
        if (idProducto != null) {
            // Editar existente
            p = productoService.buscarPorId(idProducto).orElse(new Producto());
        } else {
            // Nuevo
            p = new Producto();
        }

        p.setCategoria(cat.get());
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setImagenUrl(imagenUrl);

        productoService.guardar(p);

        return "redirect:/admin/productos";
    }

    // ===== CAMBIAR ESTADO =====

    @GetMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        productoService.cambiarEstado(id);
        return "redirect:/admin/productos";
    }

    // ===== ELIMINAR =====

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        productoService.eliminar(id);
        return "redirect:/admin/productos";
    }
}