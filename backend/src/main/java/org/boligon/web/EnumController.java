package org.boligon.web;

import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/categorias")
    public List<Map<String, String>> categorias() {
        List<Map<String, String>> lista = new ArrayList<>();
        for (Categoria categoria : Categoria.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("nome", categoria.name());
            item.put("valor", categoria.getValor());
            lista.add(item);
        }
        return lista;
    }

    @GetMapping("/prioridades")
    public List<Map<String, Object>> prioridades() {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Prioridade prioridade : Prioridade.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("nome", prioridade.name());
            item.put("diasSla", prioridade.getDiasSla());
            item.put("impactoSocial", prioridade.getImpactoSocial());
            lista.add(item);
        }
        return lista;
    }

    @GetMapping("/status")
    public List<String> status() {
        List<String> lista = new ArrayList<>();
        for (StatusSolicitacao status : StatusSolicitacao.values()) {
            lista.add(status.name());
        }
        return lista;
    }
}
