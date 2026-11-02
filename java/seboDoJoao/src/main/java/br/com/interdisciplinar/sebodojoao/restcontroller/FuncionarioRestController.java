package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.FuncionarioRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.FuncionarioResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.FuncionarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
@RequiredArgsConstructor
public class FuncionarioRestController {
    private final FuncionarioService funcionarioService;

    @GetMapping
    public List<FuncionarioResponseDTO> listarTodos() {
        return funcionarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public FuncionarioResponseDTO buscarPorId(@PathVariable Long id) {
        return funcionarioService.buscarPorId(id);
    }

    @PostMapping
    public FuncionarioResponseDTO cadastrar(@RequestBody FuncionarioRequestDTO dto) {
        return funcionarioService.cadastrar(dto);
    }

    @PutMapping("/{id}")
    public FuncionarioResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody FuncionarioRequestDTO dto) {

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
            @RequestParam(value = "removerFoto", defaultValue = "false") boolean removerFoto) {

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

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        funcionarioService.excluir(id);
    }
}