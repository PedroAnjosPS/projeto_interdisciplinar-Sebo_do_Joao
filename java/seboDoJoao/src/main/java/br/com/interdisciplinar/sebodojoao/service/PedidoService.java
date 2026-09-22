package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.model.Pedido;
import br.com.interdisciplinar.sebodojoao.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;

    public List<Pedido> listarTodos() { return pedidoRepository.findAll(); }

    public Pedido buscarPorId(Long id) { return pedidoRepository.findById(id).orElseThrow(); }

    public Pedido cadastrar(Pedido pedido) { return pedidoRepository.save(pedido); }

    public Pedido atualizar(Long id, @NonNull Pedido pedido) {
        Pedido pedidoExistente = pedidoRepository.findById(id).orElseThrow();

        pedidoExistente.setStatus(pedido.getStatus());
        pedidoExistente.setCliente(pedido.getCliente());
        pedidoExistente.setFuncionario(pedido.getFuncionario());

        return pedidoRepository.save(pedidoExistente);
    }

    public void excluir(Long id) { pedidoRepository.deleteById(id); }
}
