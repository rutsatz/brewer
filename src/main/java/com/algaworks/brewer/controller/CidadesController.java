package com.algaworks.brewer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.Cidades;

@Controller
@RequestMapping("/cidades")
public class CidadesController {

    @Autowired
    private Cidades cidades;

    @RequestMapping("/novo")
    public String novo() {
        return "cidade/CadastroCidade";
    }

    public List<Cidade> pesquisarPorCodigoEstado(Long codigoEstado) {
        return cidades.findByEstadoCodigo(codigoEstado); //12:26
    }

}
