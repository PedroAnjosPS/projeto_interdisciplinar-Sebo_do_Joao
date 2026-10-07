package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.PedidoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.PedidoResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Cliente;
import br.com.interdisciplinar.sebodojoao.model.Entrega;
import br.com.interdisciplinar.sebodojoao.model.Funcionario;
import br.com.interdisciplinar.sebodojoao.model.Pedido;
import br.com.interdisciplinar.sebodojoao.repository.ClienteRepository;
import br.com.interdisciplinar.sebodojoao.repository.EntregaRepository;
import br.com.interdisciplinar.sebodojoao.repository.FuncionarioRepository;
import br.com.interdisciplinar.sebodojoao.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final EntregaRepository entregaRepository;

    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedido -> new PedidoResponseDTO(
                        pedido.getId(),
                        pedido.getData(),
                        pedido.getTotal(),
                        pedido.getStatus(),
                        pedido.getCliente().getId(),
                        pedido.getFuncionario().getId(),
                        pedido.getEntrega().getId()
                ))
                .toList();
    }

    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository
                .findById(id)
                .orElseThrow();

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getData(),
                pedido.getTotal(),
                pedido.getStatus(),
                pedido.getCliente().getId(),
                pedido.getFuncionario().getId(),
                pedido.getEntrega().getId()
        );
    }

    public PedidoResponseDTO cadastrar(PedidoRequestDTO dto) {
        Cliente cliente = clienteRepository
                .findById(dto.clienteId())
                .orElseThrow();

        Funcionario funcionario = funcionarioRepository
                .findById(dto.funcionarioId())
                .orElseThrow();

        Entrega entrega = entregaRepository
                .findById(dto.entregaId())
                .orElseThrow();

        Pedido pedido = new Pedido();
        pedido.setData(dto.data());
        pedido.setTotal(dto.total());
        pedido.setStatus(dto.status());
        pedido.setCliente(cliente);
        pedido.setFuncionario(funcionario);
        pedido.setEntrega(entrega);

        Pedido salvo = pedidoRepository.save(pedido);

        return new PedidoResponseDTO(
                salvo.getId(),
                salvo.getData(),
                salvo.getTotal(),
                salvo.getStatus(),
                salvo.getCliente().getId(),
                salvo.getFuncionario().getId(),
                salvo.getEntrega().getId()
        );
    }

    public PedidoResponseDTO atualizar(Long id, PedidoRequestDTO dto) {
        Pedido pedidoExistente = pedidoRepository
                .findById(id)
                .orElseThrow();

        Cliente cliente = clienteRepository
                .findById(dto.clienteId())
                .orElseThrow();

        Funcionario funcionario = funcionarioRepository
                .findById(dto.funcionarioId())
                .orElseThrow();

        Entrega entrega = entregaRepository
                .findById(dto.entregaId())
                .orElseThrow();

        pedidoExistente.setData(dto.data());
        pedidoExistente.setTotal(dto.total());
        pedidoExistente.setStatus(dto.status());
        pedidoExistente.setCliente(cliente);
        pedidoExistente.setFuncionario(funcionario);
        pedidoExistente.setEntrega(entrega);

        Pedido atualizado = pedidoRepository.save(pedidoExistente);

        return new PedidoResponseDTO(
                atualizado.getId(),
                atualizado.getData(),
                atualizado.getTotal(),
                atualizado.getStatus(),
                atualizado.getCliente().getId(),
                atualizado.getFuncionario().getId(),
                atualizado.getEntrega().getId()
        );
    }

    public void excluir(Long id) {
        pedidoRepository.deleteById(id);
    }
}