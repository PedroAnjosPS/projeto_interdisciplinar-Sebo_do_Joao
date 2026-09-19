package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.model.Usuario;
import br.com.interdisciplinar.sebodojoao.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow();
    }

    public Usuario cadastrar(Usuario Usuario) {
        return usuarioRepository.save(Usuario);
    }

    public Usuario atualizar(Long id, @NonNull Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(id).orElseThrow();

        usuarioExistente.setNome(usuario.getNome());

        return usuarioRepository.save(usuarioExistente);
    }

    public void excluir(Long id) {
        usuarioRepository.deleteById(id);
    }
}
