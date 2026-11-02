package br.com.interdisciplinar.sebodojoao.service;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.interdisciplinar.sebodojoao.dto.CartaoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.CartaoResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Cartao;
import br.com.interdisciplinar.sebodojoao.repository.CartaoRepository;
import br.com.interdisciplinar.sebodojoao.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;

// NOVO: as regras dos cartões salvos do cliente (tela "Meus cartões" e checkout).
//
// SEGURANÇA: o número completo e o CVV nunca chegam aqui nem são guardados. O site manda só a bandeira,
// os 4 últimos dígitos, o nome do titular e a validade. Quem é o cliente vem da SESSÃO de login e
// só se enxerga ou altera cartão que seja DESTE cliente.
@Service
@RequiredArgsConstructor
public class CartaoService {
    // Máximo de cartões por cliente (o site mostra o mesmo número)
    public static final int LIMITE = 6;

    // Bandeiras que o site sabe reconhecer
    private static final Set<String> BANDEIRAS = Set.of("visa", "mastercard", "amex", "elo", "hipercard", "diners");

    private final CartaoRepository cartaoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<CartaoResponseDTO> listar(Long clienteId) {
        // O cartão padrão vem primeiro; os outros, do mais novo para o mais antigo
        return cartaoRepository.findByCliente_IdOrderByPadraoDescIdDesc(clienteId)
                .stream().map(this::paraDto).toList();
    }

    @Transactional
    public CartaoResponseDTO salvar(Long clienteId, CartaoRequestDTO dto) {
        if (dto == null) throw erro(HttpStatus.BAD_REQUEST, "Dados do cartão não informados.");

        // 1) Confere os campos
        String bandeira = texto(dto.bandeira()).toLowerCase();
        String finalNumero = texto(dto.finalNumero());
        String titular = texto(dto.titular()).replaceAll("\\s+", " ").toUpperCase();
        String validade = texto(dto.validade());

        if (!BANDEIRAS.contains(bandeira)) throw erro(HttpStatus.BAD_REQUEST, "Bandeira de cartão não reconhecida.");
        if (!finalNumero.matches("\\d{4}")) throw erro(HttpStatus.BAD_REQUEST, "Informe os 4 últimos dígitos do cartão.");
        if (titular.length() < 2 || titular.length() > 100) throw erro(HttpStatus.BAD_REQUEST, "Informe o nome impresso no cartão.");
        if (!validade.matches("(0[1-9]|1[0-2])/\\d{2}")) throw erro(HttpStatus.BAD_REQUEST, "Informe a validade no formato MM/AA.");
        // O cartão vale até o último dia do mês da validade
        YearMonth venceEm = YearMonth.of(2000 + Integer.parseInt(validade.substring(3)), Integer.parseInt(validade.substring(0, 2)));
        if (venceEm.isBefore(YearMonth.now())) throw erro(HttpStatus.BAD_REQUEST, "Esse cartão está vencido.");

        // 2) Regras de negócio: não repetir o mesmo cartão e respeitar o limite
        if (cartaoRepository.existsByCliente_IdAndBandeiraAndFinalNumeroAndValidade(clienteId, bandeira, finalNumero, validade)) {
            throw erro(HttpStatus.CONFLICT, "Esse cartão já está salvo na sua conta.");
        }
        long quantosJaTem = cartaoRepository.countByCliente_Id(clienteId);
        if (quantosJaTem >= LIMITE) {
            throw erro(HttpStatus.CONFLICT, "Você pode guardar até " + LIMITE + " cartões. Exclua algum pra adicionar outro.");
        }

        // 3) Salva. O primeiro cartão da conta já nasce como padrão; marcar outro como padrão tira a marca dos demais.
        Cartao cartao = new Cartao();
        cartao.setCliente(clienteRepository.getReferenceById(clienteId));
        cartao.setBandeira(bandeira);
        cartao.setFinalNumero(finalNumero);
        cartao.setTitular(titular);
        cartao.setValidade(validade);
        if (quantosJaTem == 0 || Boolean.TRUE.equals(dto.padrao())) {
            cartaoRepository.limparPadrao(clienteId);
            cartao.setPadrao(true);
        }
        return paraDto(cartaoRepository.save(cartao));
    }

    @Transactional
    public CartaoResponseDTO definirPadrao(Long clienteId, Long id) {
        Cartao cartao = buscarDoCliente(clienteId, id);
        cartaoRepository.limparPadrao(clienteId);
        cartao.setPadrao(true);
        return paraDto(cartaoRepository.save(cartao));
    }

    // Aqui a linha é apagada de verdade: nenhuma outra tabela aponta para o cartão
    // (o pagamento guarda só uma cópia da bandeira e do final do cartão usado).
    @Transactional
    public void remover(Long clienteId, Long id) {
        Cartao cartao = buscarDoCliente(clienteId, id);
        boolean eraPadrao = Boolean.TRUE.equals(cartao.getPadrao());
        cartaoRepository.delete(cartao);
        cartaoRepository.flush();

        // Se apagou o padrão, o cartão mais novo que sobrou assume o lugar
        if (eraPadrao) {
            cartaoRepository.findByCliente_IdOrderByPadraoDescIdDesc(clienteId)
                    .stream().findFirst().ifPresent(sobra -> sobra.setPadrao(true));
        }
    }

    private Cartao buscarDoCliente(Long clienteId, Long id) {
        return cartaoRepository.findByIdAndCliente_Id(id, clienteId)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Cartão não encontrado."));
    }

    private CartaoResponseDTO paraDto(Cartao c) {
        return new CartaoResponseDTO(c.getId(), c.getBandeira(), c.getFinalNumero(), c.getTitular(),
                c.getValidade(), Boolean.TRUE.equals(c.getPadrao()));
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static ResponseStatusException erro(HttpStatus status, String mensagem) {
        return new ResponseStatusException(status, mensagem);
    }
}
