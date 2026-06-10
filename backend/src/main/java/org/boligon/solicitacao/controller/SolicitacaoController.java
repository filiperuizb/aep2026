package org.boligon.solicitacao.controller;

import org.boligon.historico.dto.HistoricoResponse;
import org.boligon.historico.dto.HistoricoStatusDTO;
import org.boligon.historico.service.HistoricoStatusService;
import org.boligon.solicitacao.domain.Solicitacao;
import org.boligon.solicitacao.dto.CriarSolicitacaoRequest;
import org.boligon.solicitacao.dto.FiltroSolicitacaoDTO;
import org.boligon.solicitacao.dto.SolicitacaoDetalheResponse;
import org.boligon.solicitacao.dto.SolicitacaoResponse;
import org.boligon.solicitacao.service.SolicitacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;
    private final HistoricoStatusService historicoStatusService;

    public SolicitacaoController(SolicitacaoService solicitacaoService,
                                 HistoricoStatusService historicoStatusService) {
        this.solicitacaoService = solicitacaoService;
        this.historicoStatusService = historicoStatusService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoResponse criar(@RequestBody CriarSolicitacaoRequest request) {
        return SolicitacaoResponse.de(solicitacaoService.criarSolicitacao(request));
    }

    @GetMapping("/protocolo/{protocolo}")
    public SolicitacaoDetalheResponse buscarPorProtocolo(@PathVariable String protocolo) {
        Solicitacao solicitacao = solicitacaoService.buscarPorProtocolo(protocolo);
        List<HistoricoResponse> historico = historicoStatusService
                .listarPorSolicitacaoId(solicitacao.getId())
                .stream()
                .map(HistoricoResponse::de)
                .toList();
        return SolicitacaoDetalheResponse.de(solicitacao, historico);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<SolicitacaoResponse> listarPorUsuario(@PathVariable Long usuarioId) {
        return solicitacaoService.listarPorUsuarioId(usuarioId)
                .stream()
                .map(SolicitacaoResponse::de)
                .toList();
    }

    @GetMapping("/fila")
    public List<SolicitacaoResponse> obterFila() {
        return solicitacaoService.obterFilaDeAtendimento()
                .getSolicitacoesOrdenadas()
                .stream()
                .map(SolicitacaoResponse::de)
                .toList();
    }

    @PostMapping("/filtro")
    public List<SolicitacaoResponse> filtrar(@RequestBody FiltroSolicitacaoDTO filtro) {
        return solicitacaoService.filtrar(filtro)
                .stream()
                .map(SolicitacaoResponse::de)
                .toList();
    }

    @PutMapping("/status")
    public void atualizarStatus(@RequestBody HistoricoStatusDTO dto) {
        solicitacaoService.atualizarStatus(dto);
    }
}
