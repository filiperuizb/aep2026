package org.boligon.web;

import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/categorias")
    public List<Map<String, String>> categorias() {
        return Arrays.stream(Categoria.values())
                .map(c -> Map.of("nome", c.name(), "valor", c.getValor()))
                .toList();
    }

    @GetMapping("/prioridades")
    public List<Map<String, Object>> prioridades() {
        return Arrays.stream(Prioridade.values())
                .map(p -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("nome", p.name());
                    item.put("diasSla", p.getDiasSla());
                    item.put("impactoSocial", p.getImpactoSocial());
                    return item;
                })
                .toList();
    }

    @GetMapping("/status")
    public List<String> status() {
        return Arrays.stream(StatusSolicitacao.values())
                .map(Enum::name)
                .toList();
    }
}
