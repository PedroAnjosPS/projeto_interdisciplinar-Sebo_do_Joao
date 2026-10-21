package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.CepRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.CepResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Cep;
import br.com.interdisciplinar.sebodojoao.model.Cidade;
import br.com.interdisciplinar.sebodojoao.repository.CepRepository;
import br.com.interdisciplinar.sebodojoao.repository.CidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CepService {
    private final CepRepository cepRepository;
    private final CidadeRepository cidadeRepository;

    public List<CepResponseDTO> listarTodos() {
        return cepRepository.findAll()
                .stream()
                .map(Cep -> new CepResponseDTO(
                        Cep.getNumeroCep(),
                        Cep.getCidade().getId()
                ))
                .toList();
    }

    public CepResponseDTO buscarPorId(String nr) {
        Cep cep = cepRepository.findById(nr).orElseThrow();

        return new CepResponseDTO(
                cep.getNumeroCep(),
                cep.getCidade().getId()
        );
    }

    public CepResponseDTO cadastrar(CepRequestDTO dto) {
        Cep cep = new Cep();

        Cidade cidade = cidadeRepository.findById(dto.cidadeId()).orElseThrow();

        cep.setNumeroCep(dto.numeroCep());
        cep.setCidade(cidade);

        Cep salvo = cepRepository.save(cep);

        return new CepResponseDTO(
                salvo.getNumeroCep(),
                salvo.getCidade().getId()
        );
    }

    public CepResponseDTO atualizar(String nr, CepRequestDTO dto) {
        Cep cepExistente = cepRepository.findById(nr).orElseThrow();

        Cidade cidade = cidadeRepository.findById(dto.cidadeId()).orElseThrow();

        cepExistente.setNumeroCep(dto.numeroCep());
        cepExistente.setCidade(cidade);

        Cep atualizado = cepRepository.save(cepExistente);

        return new CepResponseDTO(
                atualizado.getNumeroCep(),
                atualizado.getCidade().getId()
        );
    }

    public void excluir(String nr) { cepRepository.deleteById(nr); }
}
