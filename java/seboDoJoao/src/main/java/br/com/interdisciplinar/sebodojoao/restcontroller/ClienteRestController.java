package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.ClienteRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ClienteResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.ClienteService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteRestController {
    private final ClienteService clienteService;

    @GetMapping
    public List<ClienteResponseDTO> listarTodos() {
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ClienteResponseDTO buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @PostMapping
    public ClienteResponseDTO cadastrar(@RequestBody ClienteRequestDTO dto) {
        return clienteService.cadastrar(dto);
    }

    // MUDANÇA CRÍTICA: Usar POST porque o Tomcat barra pacotes multipart no PUT
    @PostMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "telefone", required = false) String telefone,
            @RequestParam(value = "dataNascimento", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataNascimento,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {

        String caminhoFoto = null;
        
        if (foto != null && !foto.isEmpty()) {
            try {
                String diretorio = "src/main/resources/static/imagens/usuarios/";
                java.nio.file.Path caminhoDiretorio = java.nio.file.Paths.get(diretorio);
                
                if (!java.nio.file.Files.exists(caminhoDiretorio)) {
                    java.nio.file.Files.createDirectories(caminhoDiretorio);
                }

                String nomeArquivo = id + "_perfil.jpg";
                java.nio.file.Path caminhoArquivo = caminhoDiretorio.resolve(nomeArquivo);
                java.nio.file.Files.copy(foto.getInputStream(), caminhoArquivo, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                caminhoFoto = "/imagens/usuarios/" + nomeArquivo;
            } catch (java.io.IOException e) {
                throw new RuntimeException("Erro ao guardar a foto.", e);
            }
        } else if (foto != null) {
            caminhoFoto = ""; 
        }

        ClienteRequestDTO dto = new ClienteRequestDTO(
                nome, email, null, telefone, null, null, null, null, caminhoFoto, cpf, dataNascimento
        );

        return ResponseEntity.ok(clienteService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        clienteService.excluir(id);
    }
}