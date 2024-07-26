package com.eletrosapplication.controller;

import com.eletrosapplication.domain.Eletro;
import com.eletrosapplication.service.EletroService;
import com.eletrosapplication.service.FileStorageService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class CarrinhoController {

    private final EletroService service;
    private final FileStorageService fileStorageService;

    public CarrinhoController(EletroService service, FileStorageService fileStorageService) {
        this.service = service;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/adicionarCarrinho/{id}")
    public String adicionarCarrinho(@PathVariable String id, HttpSession session, Model model) {

        Optional<Eletro> eletro = service.findById(id);

        if(eletro.isPresent()) {
            List<Eletro> carrinho = (List<Eletro>) session.getAttribute("carrinho");
            if(carrinho == null){
                carrinho = new ArrayList<>();
            }

            carrinho.add(eletro.get());

            session.setAttribute("carrinho", carrinho);
        }

        return "redirect:/admin";
    }

    @GetMapping("/verCarrinho")
    public String verCarrinho(HttpSession session, Model model, HttpServletResponse response) {
        List<Eletro> carrinho = (List<Eletro>) session.getAttribute("carrinho");
        if(carrinho == null || carrinho.isEmpty()) {
            model.addAttribute("msg", "O carrinho está vazio");
            return "redirect:/admin";
        }

        model.addAttribute("itens", carrinho);

        // Criando o cookie de visita
        Cookie cookie_visita  = new Cookie("visita", "true");
        cookie_visita.setMaxAge(24 * 60 * 60); // cookie de 24 horas
        response.addCookie(cookie_visita);

        return "carrinho";
    }

    @GetMapping("/removerCarrinho/{id}")
    public String removerCarrinho(@PathVariable String id, HttpSession session) {
        List<Eletro> carrinho = (List<Eletro>) session.getAttribute("carrinho");
        if(carrinho != null) {
            Eletro removed = null;

            for(Eletro e : carrinho){
                if(e.getId().equals(id)){
                    removed = e;
                    break;
                }
            }

            if(removed != null) {
                carrinho.remove(removed);
            }

            session.setAttribute("carrinho", carrinho);
        }
        return "redirect:/admin";
    }
}
