package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.EntregaRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.EntregaResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Entrega;
import br.com.interdisciplinar.sebodojoao.repository.EntregaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntregaService {
    private final EntregaRepository entregaRepository;

    public List<EntregaResponseDTO> listarTodos() {
        return entregaRepository.findAll()
                .stream()
                .map(entrega -> new EntregaResponseDTO(
                        entrega.getId(),
                        entrega.getTransportadora(),
                        entrega.getLinkRastreio(),
                        entrega.getCodigoRastreio(),
                        entrega.getDataPostagem(),
                        entrega.getDataPrevisaoEntrega(),
                        entrega.getDataEntrega(),
                        entrega.getStatus()
                ))
                .toList();
    }

    public EntregaResponseDTO buscarPorId(Long id) {
        Entrega entrega = entregaRepository
                .findById(id)
                .orElseThrow();

        return new EntregaResponseDTO(
                entrega.getId(),
                entrega.getTransportadora(),
                entrega.getLinkRastreio(),
                entrega.getCodigoRastreio(),
                entrega.getDataPostagem(),
                entrega.getDataPrevisaoEntrega(),
                entrega.getDataEntrega(),
                entrega.getStatus()
        );
    }

    public EntregaResponseDTO cadastrar(EntregaRequestDTO dto) {
        Entrega entrega = new Entrega();
        entrega.setTransportadora(dto.transportadora());
        entrega.setLinkRastreio(dto.linkRastreio());
        entrega.setCodigoRastreio(dto.codigoRastreio());
        entrega.setDataPostagem(dto.dataPostagem());
        entrega.setDataPrevisaoEntrega(dto.dataPrevisaoEntrega());
        entrega.setDataEntrega(dto.dataEntrega());
        entrega.setStatus(dto.status());

        Entrega salva = entregaRepository.save(entrega);

        return new EntregaResponseDTO(
                salva.getId(),
                salva.getTransportadora(),
                salva.getLinkRastreio(),
                salva.getCodigoRastreio(),
                salva.getDataPostagem(),
                salva.getDataPrevisaoEntrega(),
                salva.getDataEntrega(),
                salva.getStatus()
        );
    }

    public EntregaResponseDTO atualizar(Long id, EntregaRequestDTO dto) {
        Entrega entregaExistente = entregaRepository
                .findById(id)
                .orElseThrow();

        entregaExistente.setTransportadora(dto.transportadora());
        entregaExistente.setLinkRastreio(dto.linkRastreio());
        entregaExistente.setCodigoRastreio(dto.codigoRastreio());
        entregaExistente.setDataPostagem(dto.dataPostagem());
        entregaExistente.setDataPrevisaoEntrega(dto.dataPrevisaoEntrega());
        entregaExistente.setDataEntrega(dto.dataEntrega());
        entregaExistente.setStatus(dto.status());

        Entrega atualizada = entregaRepository.save(entregaExistente);

        return new EntregaResponseDTO(
                atualizada.getId(),
                atualizada.getTransportadora(),
                atualizada.getLinkRastreio(),
                atualizada.getCodigoRastreio(),
                atualizada.getDataPostagem(),
                atualizada.getDataPrevisaoEntrega(),
                atualizada.getDataEntrega(),
                atualizada.getStatus()
        );
    }

    public void excluir(Long id) {
        entregaRepository.deleteById(id);
    }
}