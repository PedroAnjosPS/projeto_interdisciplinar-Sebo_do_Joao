package br.com.interdisciplinar.sebodojoao.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.interdisciplinar.sebodojoao.dto.EnderecoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.EnderecoResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Cep;
import br.com.interdisciplinar.sebodojoao.model.Cidade;
import br.com.interdisciplinar.sebodojoao.model.Endereco;
import br.com.interdisciplinar.sebodojoao.model.Uf;
import br.com.interdisciplinar.sebodojoao.repository.CepRepository;
import br.com.interdisciplinar.sebodojoao.repository.CidadeRepository;
import br.com.interdisciplinar.sebodojoao.repository.ClienteRepository;
import br.com.interdisciplinar.sebodojoao.repository.EnderecoRepository;
import br.com.interdisciplinar.sebodojoao.repository.UFRepository;
import lombok.RequiredArgsConstructor;

// As regras dos endereços de entrega do cliente (tela "Meus endereços" e checkout).
//
// Como o endereço é guardado: a tabela enderecos guarda a rua, o número, o bairro etc. e o NÚMERO DO CEP.
// A cidade e a UF ficam nas tabelas ceps -> cidades -> ufs. Por isso, ao salvar um endereço, este serviço:
//   1) procura a UF (ela precisa existir: as 27 já vêm cadastradas);
//   2) procura o CEP; se ele ainda não existe, procura a cidade (pelo nome + UF) e cria o que faltar.
// Em TODAS as operações, quem é o cliente vem da SESSÃO de login (nunca do navegador), e só se enxerga
// ou altera endereço que seja DESTE cliente.
@Service
@RequiredArgsConstructor
public class EnderecoService {
    // Máximo de endereços ativos por cliente (o site mostra o mesmo número)
    public static final int LIMITE = 10;

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;
    private final UFRepository ufRepository;
    private final CidadeRepository cidadeRepository;
    private final CepRepository cepRepository;

    // ---------------------------------------------------------------------------------------
    // CONSULTAR
    // ---------------------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<EnderecoResponseDTO> listar(Long clienteId) {
        // O endereço padrão vem primeiro; os outros, do mais novo para o mais antigo
        return enderecoRepository.findByCliente_IdAndAtivoTrueOrderByPadraoDescIdDesc(clienteId)
                .stream()
                .map(this::paraDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EnderecoResponseDTO buscar(Long clienteId, Long id) {
        return paraDto(buscarDoCliente(clienteId, id));
    }

    // ---------------------------------------------------------------------------------------
    // SALVAR (criar ou editar)
    // ---------------------------------------------------------------------------------------
    // id == null: cria um endereço novo. id preenchido: edita o endereço existente.
    @Transactional
    public EnderecoResponseDTO salvar(Long clienteId, Long id, EnderecoRequestDTO dto) {
        if (dto == null) throw erro(HttpStatus.BAD_REQUEST, "Dados do endereço não informados.");

        // 1) Confere os campos e prepara os valores já "limpos" (sem espaços sobrando)
        boolean semNumero = Boolean.TRUE.equals(dto.semNumero());
        String apelido = texto(dto.apelido());
        String logradouro = obrigatorio(dto.logradouro(), "Informe a rua ou avenida.", 2, 255);
        String numero = semNumero ? "" : obrigatorio(dto.numero(), "Informe o número (ou marque \"Sem número\").", 1, 10);
        String complemento = texto(dto.complemento());
        String bairro = obrigatorio(dto.bairro(), "Informe o bairro.", 2, 100);
        String nomeCidade = obrigatorio(dto.cidade(), "Informe a cidade.", 2, 100);
        String cepFormatado = formatarCep(dto.cep());

        if (apelido.length() > 30) throw erro(HttpStatus.BAD_REQUEST, "O apelido pode ter no máximo 30 caracteres.");
        if (complemento.length() > 100) throw erro(HttpStatus.BAD_REQUEST, "O complemento pode ter no máximo 100 caracteres.");

        // 2) Cria ou carrega o endereço
        Endereco endereco;
        boolean ehNovo = (id == null);
        if (ehNovo) {
            if (enderecoRepository.countByCliente_IdAndAtivoTrue(clienteId) >= LIMITE) {
                throw erro(HttpStatus.CONFLICT, "Você pode guardar até " + LIMITE + " endereços. Exclua algum pra adicionar outro.");
            }
            endereco = new Endereco();
            endereco.setCliente(clienteRepository.getReferenceById(clienteId));
        } else {
            endereco = buscarDoCliente(clienteId, id);
        }

        // 3) Preenche os dados. A cidade/UF viram um CEP cadastrado nas tabelas ceps/cidades/ufs.
        endereco.setApelido(apelido.isEmpty() ? "Endereço" : apelido);
        endereco.setLogradouro(logradouro);
        endereco.setNumero(semNumero ? null : numero);          // null = "Sem número"
        endereco.setComplemento(complemento.isEmpty() ? null : complemento);
        endereco.setBairro(bairro);
        endereco.setCep(procurarOuCriarCep(cepFormatado, nomeCidade, dto.uf()));

        // 4) Endereço padrão: o primeiro endereço do cliente já nasce como padrão. Marcar um como padrão
        //    tira a marca dos outros (só pode existir um padrão por cliente).
        boolean primeiroDaConta = ehNovo && enderecoRepository.countByCliente_IdAndAtivoTrue(clienteId) == 0;
        if (primeiroDaConta || Boolean.TRUE.equals(dto.padrao())) {
            enderecoRepository.limparPadrao(clienteId);
            endereco.setPadrao(true);
        }

        return paraDto(enderecoRepository.save(endereco));
    }

    // ---------------------------------------------------------------------------------------
    // PADRÃO / EXCLUIR
    // ---------------------------------------------------------------------------------------
    @Transactional
    public EnderecoResponseDTO definirPadrao(Long clienteId, Long id) {
        Endereco endereco = buscarDoCliente(clienteId, id);
        enderecoRepository.limparPadrao(clienteId);
        endereco.setPadrao(true);
        return paraDto(enderecoRepository.save(endereco));
    }

    // "Excluir" não apaga a linha: só marca ativo = false (as entregas antigas continuam apontando para ela).
    @Transactional
    public void remover(Long clienteId, Long id) {
        Endereco endereco = buscarDoCliente(clienteId, id);
        boolean eraPadrao = Boolean.TRUE.equals(endereco.getPadrao());
        endereco.setAtivo(false);
        endereco.setPadrao(false);
        enderecoRepository.saveAndFlush(endereco);

        // Se apagou o padrão, o endereço mais novo que sobrou assume o lugar
        if (eraPadrao) {
            enderecoRepository.findByCliente_IdAndAtivoTrueOrderByPadraoDescIdDesc(clienteId)
                    .stream().findFirst().ifPresent(sobra -> sobra.setPadrao(true));
        }
    }

    // ---------------------------------------------------------------------------------------
    // APOIO
    // ---------------------------------------------------------------------------------------
    // Só devolve o endereço se ele for deste cliente e estiver ativo (senão, "não encontrado")
    private Endereco buscarDoCliente(Long clienteId, Long id) {
        return enderecoRepository.findByIdAndCliente_IdAndAtivoTrue(id, clienteId)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Endereço não encontrado."));
    }

    // Procura o CEP no banco. Se ele ainda não existe, cadastra: usa a cidade (pelo nome + UF) que já
    // existir ou cria a cidade. Se o CEP já existia, ele é mantido como está (cada CEP pertence a uma cidade só).
    private Cep procurarOuCriarCep(String cepFormatado, String nomeCidade, String sigla) {
        Cep existente = cepRepository.findById(cepFormatado).orElse(null);
        if (existente != null) return existente;

        Uf uf = ufRepository.findById(texto(sigla).toUpperCase())
                .orElseThrow(() -> erro(HttpStatus.BAD_REQUEST, "Escolha o estado."));

        Cidade cidade = cidadeRepository.findFirstByNomeIgnoreCaseAndUf_Sigla(nomeCidade, uf.getSigla())
                .orElseGet(() -> {
                    Cidade nova = new Cidade();
                    nova.setNome(nomeCidade);
                    nova.setUf(uf);
                    return cidadeRepository.save(nova);
                });

        Cep novo = new Cep();
        novo.setNumeroCep(cepFormatado);
        novo.setCidade(cidade);
        return cepRepository.save(novo);
    }

    // "15010000" ou "15010-000" -> "15010-000" (é assim que os CEPs ficam gravados na tabela ceps)
    private String formatarCep(String cep) {
        String digitos = texto(cep).replaceAll("\\D", "");
        if (digitos.length() != 8 || digitos.matches("(\\d)\\1{7}")) {
            throw erro(HttpStatus.BAD_REQUEST, "Informe um CEP válido (8 números).");
        }
        return digitos.substring(0, 5) + "-" + digitos.substring(5);
    }

    private EnderecoResponseDTO paraDto(Endereco e) {
        Cidade cidade = e.getCep().getCidade();
        return new EnderecoResponseDTO(e.getId(), e.getApelido(),
                e.getCep().getNumeroCep(), e.getLogradouro(), e.getNumero(), e.getComplemento(), e.getBairro(),
                cidade.getNome(), cidade.getUf().getSigla(), Boolean.TRUE.equals(e.getPadrao()));
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    // Campo de texto obrigatório: não pode ficar vazio nem passar do tamanho da coluna
    private static String obrigatorio(String valor, String mensagemSeVazio, int minimo, int maximo) {
        String t = texto(valor);
        if (t.isEmpty()) throw erro(HttpStatus.BAD_REQUEST, mensagemSeVazio);
        if (t.length() < minimo) throw erro(HttpStatus.BAD_REQUEST, mensagemSeVazio);
        if (t.length() > maximo) throw erro(HttpStatus.BAD_REQUEST, "Use no máximo " + maximo + " caracteres.");
        return t;
    }

    private static ResponseStatusException erro(HttpStatus status, String mensagem) {
        return new ResponseStatusException(status, mensagem);
    }
}
