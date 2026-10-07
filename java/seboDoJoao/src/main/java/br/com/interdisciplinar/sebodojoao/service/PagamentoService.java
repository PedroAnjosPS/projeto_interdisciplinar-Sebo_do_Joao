package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.PagamentoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.PagamentoResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Pagamento;
import br.com.interdisciplinar.sebodojoao.model.Pedido;
import br.com.interdisciplinar.sebodojoao.repository.PagamentoRepository;
import br.com.interdisciplinar.sebodojoao.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    public List<PagamentoResponseDTO> listarTodos() {
        return pagamentoRepository.findAll()
                .stream()
                .map(pagamento -> new PagamentoResponseDTO(
                        pagamento.getId(),
                        pagamento.getNumeroParcela(),
                        pagamento.getStatus(),
                        pagamento.getValor(),
                        pagamento.getQuantidadeParcelas(),
                        pagamento.getDataVencimento(),
                        pagamento.getDataPagamento(),
                        pagamento.getPedido().getId()
                ))
                .toList();
    }

    public PagamentoResponseDTO buscarPorId(Long id) {
        Pagamento pagamento = pagamentoRepository
                .findById(id)
                .orElseThrow();

        return new PagamentoResponseDTO(
                pagamento.getId(),
                pagamento.getNumeroParcela(),
                pagamento.getStatus(),
                pagamento.getValor(),
                pagamento.getQuantidadeParcelas(),
                pagamento.getDataVencimento(),
                pagamento.getDataPagamento(),
                pagamento.getPedido().getId()
        );
    }

    public PagamentoResponseDTO cadastrar(PagamentoRequestDTO dto) {
        Pedido pedido = pedidoRepository
                .findById(dto.pedidoId())
                .orElseThrow();

        Pagamento pagamento = new Pagamento();
        pagamento.setNumeroParcela(dto.numeroParcela());
        pagamento.setStatus(dto.status());
        pagamento.setValor(dto.valor());
        pagamento.setQuantidadeParcelas(dto.quantidadeParcelas());
        pagamento.setDataVencimento(dto.dataVencimento());
        pagamento.setDataPagamento(dto.dataPagamento());
        pagamento.setPedido(pedido);

        Pagamento salvo = pagamentoRepository.save(pagamento);

        return new PagamentoResponseDTO(
                salvo.getId(),
                salvo.getNumeroParcela(),
                salvo.getStatus(),
                salvo.getValor(),
                salvo.getQuantidadeParcelas(),
                salvo.getDataVencimento(),
                salvo.getDataPagamento(),
                salvo.getPedido().getId()
        );
    }

    public PagamentoResponseDTO atualizar(Long id, PagamentoRequestDTO dto) {
        Pagamento pagamentoExistente = pagamentoRepository
                .findById(id)
                .orElseThrow();

        Pedido pedido = pedidoRepository
                .findById(dto.pedidoId())
                .orElseThrow();

        pagamentoExistente.setNumeroParcela(dto.numeroParcela());
        pagamentoExistente.setStatus(dto.status());
        pagamentoExistente.setValor(dto.valor());
        pagamentoExistente.setQuantidadeParcelas(dto.quantidadeParcelas());
        pagamentoExistente.setDataVencimento(dto.dataVencimento());
        pagamentoExistente.setDataPagamento(dto.dataPagamento());
        pagamentoExistente.setPedido(pedido);

        Pagamento atualizado = pagamentoRepository.save(pagamentoExistente);

        return new PagamentoResponseDTO(
                atualizado.getId(),
                atualizado.getNumeroParcela(),
                atualizado.getStatus(),
                atualizado.getValor(),
                atualizado.getQuantidadeParcelas(),
                atualizado.getDataVencimento(),
                atualizado.getDataPagamento(),
                atualizado.getPedido().getId()
        );
    }

    public void excluir(Long id) {
        pagamentoRepository.deleteById(id);
    }
}