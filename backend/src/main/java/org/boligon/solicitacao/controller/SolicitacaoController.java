package org.boligon.solicitacao.controller;

import org.boligon.auth.service.AuthService;
import org.boligon.historico.dto.HistoricoResponse;
import org.boligon.historico.dto.HistoricoStatusDTO;
import org.boligon.historico.service.HistoricoStatusService;
import org.springframework.security.core.Authentication;
import org.boligon.solicitacao.domain.Solicitacao;
import org.boligon.solicitacao.dto.CriarSolicitacaoRequest;
import org.boligon.solicitacao.dto.FiltroSolicitacaoDTO;
import org.boligon.solicitacao.dto.SolicitacaoDetalheResponse;
import org.boligon.solicitacao.dto.SolicitacaoResponse;
import org.boligon.solicitacao.service.SolicitacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final AuthService authService;

    public SolicitacaoController(SolicitacaoService solicitacaoService,
                                 HistoricoStatusService historicoStatusService,
                                 AuthService authService) {
        this.solicitacaoService = solicitacaoService;
        this.historicoStatusService = historicoStatusService;
        this.authService = authService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoResponse criar(@RequestBody CriarSolicitacaoRequest request) {
        Solicitacao criada = solicitacaoService.criarSolicitacao(request);
        return SolicitacaoResponse.converter(criada);
    }

    @GetMapping("/protocolo/{protocolo}/anexo")
    public ResponseEntity<byte[]> baixarAnexo(@PathVariable String protocolo) {
        Solicitacao solicitacao = solicitacaoService.buscarPorProtocolo(protocolo);
        if (!solicitacao.possuiAnexo()) {
            throw new org.boligon.exception.EntidadeNaoEncontradaException("Anexo não encontrado para esta solicitação.");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + solicitacao.getAnexoNome() + "\"")
                .contentType(MediaType.parseMediaType(solicitacao.getAnexoTipo()))
                .body(solicitacao.getAnexoDados());
    }

    @GetMapping("/protocolo/{protocolo}")
    public SolicitacaoDetalheResponse buscarPorProtocolo(@PathVariable String protocolo) {
        Solicitacao solicitacao = solicitacaoService.buscarPorProtocolo(protocolo);
        List<HistoricoResponse> historico = HistoricoResponse.converterLista(
                historicoStatusService.listarPorSolicitacaoId(solicitacao.getId())
        );
        return SolicitacaoDetalheResponse.converter(solicitacao, historico);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<SolicitacaoResponse> listarPorUsuario(@PathVariable Long usuarioId,
                                                      Authentication autenticacao) {
        authService.validarAcessoAsSolicitacoes(usuarioId, autenticacao.getName());
        return SolicitacaoResponse.converterLista(solicitacaoService.listarPorUsuarioId(usuarioId));
    }

    @GetMapping("/fila")
    public List<SolicitacaoResponse> obterFila() {
        return SolicitacaoResponse.converterLista(
                solicitacaoService.obterFilaDeAtendimento().getSolicitacoesOrdenadas()
        );
    }

    @PostMapping("/filtro")
    public List<SolicitacaoResponse> filtrar(@RequestBody FiltroSolicitacaoDTO filtro) {
        return SolicitacaoResponse.converterLista(solicitacaoService.filtrar(filtro));
    }

    @PutMapping("/status")
    public void atualizarStatus(@RequestBody HistoricoStatusDTO dto) {
        solicitacaoService.atualizarStatus(dto);
    }
}
