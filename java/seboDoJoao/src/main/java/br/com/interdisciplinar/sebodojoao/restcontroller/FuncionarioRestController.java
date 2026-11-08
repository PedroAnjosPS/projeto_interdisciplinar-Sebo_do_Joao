package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.FuncionarioRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.FuncionarioResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.FuncionarioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// Todas as rotas daqui exigem funcionário logado (ver ProtecaoDasRotas). Além disso:
//   - listar, cadastrar e excluir funcionários: só o administrador;
//   - ver ou alterar uma conta: o próprio funcionário ou o administrador;
//   - situação (ativo/inativo), código e perfil de administrador: só o administrador mexe.
@RestController
@RequestMapping("/api/funcionarios")
@RequiredArgsConstructor
public class FuncionarioRestController {
    private final FuncionarioService funcionarioService;

    @GetMapping
    public List<FuncionarioResponseDTO> listarTodos(HttpSession sessao) {
        ControleDeAcesso.exigirAdministrador(sessao);
        return funcionarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public FuncionarioResponseDTO buscarPorId(@PathVariable Long id, HttpSession sessao) {
        ControleDeAcesso.exigirProprioOuAdministrador(sessao, id);
        return funcionarioService.buscarPorId(id);
    }

    @PostMapping
    public FuncionarioResponseDTO cadastrar(@RequestBody FuncionarioRequestDTO dto, HttpSession sessao) {
        ControleDeAcesso.exigirAdministrador(sessao);
        return funcionarioService.cadastrar(dto);
    }

    @PutMapping("/{id}")
    public FuncionarioResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody FuncionarioRequestDTO dto,
            HttpSession sessao) {
        ControleDeAcesso.exigirProprioOuAdministrador(sessao, id);

        // O funcionário comum não muda a própria situação, o código nem o perfil de administrador
        if (!ControleDeAcesso.ehAdministrador(sessao)) {
            dto = new FuncionarioRequestDTO(
                    dto.nome(), dto.email(), dto.senha(), dto.telefone(), null, dto.fotoPerfil(), null, null
            );
        }
        return funcionarioService.atualizar(id, dto);
    }

    // A tela de perfil envia os dados e a foto em FormData, que usa POST. O Funcionário não
    // tinha esta rota, por isso recebia erro 405 e a foto dele nunca era salva. Ela segue a
    // mesma regra da rota do Cliente. CPF e nascimento não entram porque só existem em Cliente.
    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FuncionarioResponseDTO atualizarPerfil(
            @PathVariable Long id,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "telefone", required = false) String telefone,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            @RequestParam(value = "removerFoto", defaultValue = "false") boolean removerFoto,
            HttpSession sessao) {

        ControleDeAcesso.exigirProprioOuAdministrador(sessao, id);

        // null = manter a foto atual; "" = remover (o service grava NULL no banco)
        String caminhoFoto = null;

        if (removerFoto) {
            FotoPerfilArquivo.apagar(id);
            caminhoFoto = "";
        } else if (foto != null && !foto.isEmpty()) {
            caminhoFoto = FotoPerfilArquivo.guardar(id, foto);
        }

        FuncionarioRequestDTO dto = new FuncionarioRequestDTO(
                nome, email, null, telefone, null, caminhoFoto, null, null
        );

        return funcionarioService.atualizar(id, dto);
    }

    // Excluir de vez só funciona para funcionário que nunca cadastrou produto nem cuidou de pedido.
    // Nos outros casos, use "Inativar" em Gerenciar funcionários.
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id, HttpSession sessao) {
        ControleDeAcesso.exigirAdministrador(sessao);
        funcionarioService.excluir(id);
    }
}
