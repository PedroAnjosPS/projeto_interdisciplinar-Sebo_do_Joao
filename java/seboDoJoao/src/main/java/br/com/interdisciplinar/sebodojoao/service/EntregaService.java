package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.EntregaRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.EntregaResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Endereco;
import br.com.interdisciplinar.sebodojoao.model.Entrega;
import br.com.interdisciplinar.sebodojoao.repository.EnderecoRepository;
import br.com.interdisciplinar.sebodojoao.repository.EntregaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntregaService {
    private final EntregaRepository entregaRepository;
    // NOVO: usado para achar o endereço de destino (a entrega agora aponta para a tabela enderecos)
    private final EnderecoRepository enderecoRepository;

    public List<EntregaResponseDTO> listarTodos() {
        return entregaRepository.findAll()
                .stream()
                .map(this::paraDto)
                .toList();
    }

    public EntregaResponseDTO buscarPorId(Long id) {
        Entrega entrega = entregaRepository
                .findById(id)
                .orElseThrow();

        return paraDto(entrega);
    }

    public EntregaResponseDTO cadastrar(EntregaRequestDTO dto) {
        Entrega entrega = new Entrega();
        preencher(entrega, dto);

        return paraDto(entregaRepository.save(entrega));
    }

    public EntregaResponseDTO atualizar(Long id, EntregaRequestDTO dto) {
        Entrega entregaExistente = entregaRepository
                .findById(id)
                .orElseThrow();

        preencher(entregaExistente, dto);

        return paraDto(entregaRepository.save(entregaExistente));
    }

    public void excluir(Long id) {
        entregaRepository.deleteById(id);
    }

    // ALTERADO: o preenchimento da entrega e a montagem da resposta ficavam repetidos em cada método.
    // Agora estão em dois métodos (preencher e paraDto), e as colunas novas (valor_frete e endereco_id)
    // só precisaram ser acrescentadas aqui.
    private void preencher(Entrega entrega, EntregaRequestDTO dto) {
        entrega.setTransportadora(dto.transportadora());
        entrega.setLinkRastreio(dto.linkRastreio());
        entrega.setCodigoRastreio(dto.codigoRastreio());
        entrega.setDataPostagem(dto.dataPostagem());
        entrega.setDataPrevisaoEntrega(dto.dataPrevisaoEntrega());
        entrega.setDataEntrega(dto.dataEntrega());
        entrega.setStatus(dto.status());
        entrega.setValorFrete(dto.valorFrete() == null ? BigDecimal.ZERO : dto.valorFrete());
        Endereco endereco = enderecoRepository.findById(dto.enderecoId()).orElseThrow();
        entrega.setEndereco(endereco);
    }

    private EntregaResponseDTO paraDto(Entrega entrega) {
        return new EntregaResponseDTO(
                entrega.getId(),
                entrega.getTransportadora(),
                entrega.getLinkRastreio(),
                entrega.getCodigoRastreio(),
                entrega.getDataPostagem(),
                entrega.getDataPrevisaoEntrega(),
                entrega.getDataEntrega(),
                entrega.getStatus(),
                entrega.getValorFrete(),
                entrega.getEndereco() != null ? entrega.getEndereco().getId() : null
        );
    }
}
