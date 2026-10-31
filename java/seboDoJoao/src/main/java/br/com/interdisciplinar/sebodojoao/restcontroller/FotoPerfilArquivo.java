package br.com.interdisciplinar.sebodojoao.restcontroller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

// Cuida do arquivo da foto de perfil. A imagem fica em uma pasta e o banco guarda só o caminho
// dela (foto_perfil). Cliente e Funcionário usam esta mesma classe para a regra não ficar
// repetida nos dois controllers.
final class FotoPerfilArquivo {
    private static final Path PASTA = Paths.get("src/main/resources/static/imagens/usuarios/");

    // Só aceita imagens que o navegador mostra em uma tag <img>.
    private static final Set<String> TIPOS_ACEITOS = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private FotoPerfilArquivo() {
    }

    // Salva a imagem e devolve o caminho que será gravado no banco.
    static String guardar(Long idUsuario, MultipartFile foto) {
        if (foto.getContentType() == null || !TIPOS_ACEITOS.contains(foto.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie uma imagem JPG, PNG, WEBP ou GIF.");
        }

        try {
            Files.createDirectories(PASTA);
            Files.copy(foto.getInputStream(), arquivoDoUsuario(idUsuario), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao guardar a foto.", e);
        }

        // O nome do arquivo é sempre o mesmo para cada usuário, então o navegador acharia que a
        // imagem não mudou e mostraria a antiga. O "?v=" muda a cada envio e evita isso.
        return "/imagens/usuarios/" + idUsuario + "_perfil.jpg?v=" + System.currentTimeMillis();
    }

    // Apaga o arquivo quando a pessoa remove a foto, para a imagem não continuar publicada no site.
    static void apagar(Long idUsuario) {
        try {
            Files.deleteIfExists(arquivoDoUsuario(idUsuario));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao remover a foto.", e);
        }
    }

    private static Path arquivoDoUsuario(Long idUsuario) {
        return PASTA.resolve(idUsuario + "_perfil.jpg");
    }
}
