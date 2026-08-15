// Espera o HTML carregar completamente antes de executar o script
document.addEventListener('DOMContentLoaded', () => {

    // 1. Seleciona a div vazia onde os produtos vão entrar
    const productGrid = document.getElementById('product-grid');

    // 2. Busca o arquivo JSON (Simulando uma API)
    // Se o seu JSON estiver em outra pasta, ajuste o caminho aqui!
    fetch('assets/data/produtos.json')
        .then(resposta => resposta.json()) // Transforma a resposta em um objeto JavaScript
        .then(listaDeProdutos => {

            // Limpa qualquer coisa que estivesse na grade (por segurança)
            productGrid.innerHTML = '';

            // 3. Faz um loop (forEach) por cada produto da lista
            listaDeProdutos.forEach(produto => {

                // Formata o preço para o padrão brasileiro (R$ 0,00)
                const precoFormatado = produto.preco.toLocaleString('pt-BR', {
                    style: 'currency',
                    currency: 'BRL'
                });

                // 4. Cria a string com o HTML do Card, injetando as variáveis do objeto 'produto'
                const cardHTML = `
                    <div class="col">
                        <div class="ec-card ec-product-card h-100 d-flex flex-column ec-card-shadow">
                            <div class="ec-product-img-wrapper position-relative">
                                <div class="ec-product-placeholder">
                                    <!-- Injetando o ícone dinamicamente -->
                                    <i class="fas ${produto.icone} fa-4x opacity-50"></i>
                                </div>
                                <!-- Injetando a categoria dinamicamente -->
                                <span class="position-absolute top-0 start-0 m-3 badge bg-dark rounded-pill">${produto.categoria}</span>
                            </div>
                            <div class="ec-product-body d-flex flex-column flex-grow-1 p-4">
                                <!-- Injetando o título dinamicamente -->
                                <h5 class="ec-product-title mb-2">${produto.titulo}</h5>
                                <div class="mt-auto">
                                    <!-- Injetando o preço formatado dinamicamente -->
                                    <p class="ec-product-price mb-3">${precoFormatado}</p>
                                    <button class="btn ec-btn-outline-primary ec-btn-pill w-100">
                                        <i class="fas fa-cart-plus me-2"></i>Comprar
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                `;

                // 5. Adiciona esse HTML recém-criado dentro da div principal
                productGrid.innerHTML += cardHTML;
            });
        })
        .catch(erro => {
            console.error('Erro ao carregar os produtos:', erro);
            productGrid.innerHTML = '<p class="text-center w-100 mt-5">Erro ao carregar a vitrine. Tente novamente mais tarde.</p>';
        });
});
