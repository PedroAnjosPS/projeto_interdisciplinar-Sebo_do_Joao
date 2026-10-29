// perfil.js - Cuida apenas da interface da tela de perfil

document.getElementById('formPerfil').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    
    // 1. Pega o arquivo de imagem do input HTML
    const inputFoto = document.getElementById('inputFotoPerfil');
    const foto = inputFoto.files[0]; 
    
    // 2. Prepara os dados (usando FormData para suportar envio de arquivo para o Java)
    const formData = new FormData();
    formData.append('nome', document.getElementById('nome').value);
    if (foto) {
        formData.append('foto', foto);
    }

    try {
        // 3. Chama o mensageiro (usuario-store.js) para falar com o back-end
        await UsuarioStore.salvarPerfil(formData); 
        avisar('Perfil atualizado com sucesso!', false); // Usa a função de vitrine.js
        atualizarCabecalhoConta(); 
    } catch (erro) {
        avisar(erro.message, false);
    }
});