# Parte individual — Pagamento: cartões salvos com Room

**Responsável:** Thyago Silva Guimarães  
**Telas:** Pagamento, Reserva concluída, Meus cartões (nova) e Detalhe do cartão (nova)

## O que foi feito

Para agilizar o pagamento, o usuário pode **salvar cartões** e depois pagar só digitando o CVV. Os cartões ficam no Room e sobrevivem ao fechar o app.

| Onde | O que acontece |
|---|---|
| **Pagamento** | Ao abrir, busca os cartões do usuário no banco (`LaunchedEffect(Unit)`). Mostra a lista com o cartão **padrão já selecionado**. Cartão salvo → só pede o **CVV**. "Usar outro cartão" → formulário completo + caixa **"Salvar este cartão"**. Cartão vencido é bloqueado. |
| **Reserva concluída** | Mostra com o que foi pago ("Pago com Visa •••• 4242" ou "Pix"). |
| **Meus cartões** (botão "Gerenciar") | `LazyColumn` + `Card` lendo do banco. **Adicionar** (formulário), **remover** (lixeira) e clicar no card abre o Detalhe. |
| **Detalhe do cartão** | Recebe só o `id` pela rota e **busca o cartão no banco** (`dao.buscarPorId`). **Edita** apelido, titular, validade e "cartão padrão" (`dao.atualizar`) e **remove** (`dao.deletar`). |

## Room

**Entity `CartaoSalvo`** (tabela `cartoes`): `id` (`@PrimaryKey(autoGenerate = true)`), `usuarioId`, `apelido`, `nomeTitular`, `ultimosDigitos`, `bandeira`, `validade`, `padrao`.

**DAO `CartaoDao`** (todos `suspend`): `inserir`, `buscarTodos`, `atualizar`, `deletar` + 3 consultas extras:
- `buscarPorId(id)` → usada no Detalhe do cartão;
- `buscarPorUsuario(usuarioId)` → cada usuário só vê os próprios cartões (padrão primeiro);
- `limparPadrao(usuarioId)` → garante que só um cartão seja o padrão.

**Database:** `CartaoSalvo` entrou no `AppDatabase` (versão 1 → **2**) com a `MIGRACAO_1_2`, que cria a tabela `cartoes` **sem apagar** carros, reservas e usuários.

**Repositório `CartoesRepositorio`:** mesmo padrão dos colegas. Toda operação roda em `Dispatchers.IO`, com `try/catch` + `Log.e`.

## Decisões

- **Não salvamos o número completo nem o CVV**, só os 4 últimos dígitos e a bandeira (estimada pelo 1º dígito). Guardar dado de cartão completo no aparelho seria inseguro; o CVV é digitado a cada pagamento, como nos apps reais.
- **Primeiro cartão salvo vira o padrão** automaticamente; ao remover o padrão, o próximo assume.
- **Não duplica:** mesmo cartão (4 últimos dígitos + validade + titular) não é salvo duas vezes.
- **Migração em vez de apagar o banco:** assim os dados dos colegas continuam no aparelho.
- **Cartões ligados ao usuário logado** (`usuarioId`), aproveitando a tabela `usuarios` que já existia.

## Arquivos

**Novos (meus):** `model/CartaoSalvo.kt`, `data/CartaoDao.kt`, `data/CartoesRepositorio.kt`, `data/MigracaoCartoes.kt`, `ui/screens/TelaCartoes.kt`, `ui/screens/TelaDetalheCartao.kt`

**Alterados (minhas telas):** `ui/screens/TelaPagamento.kt`, `ui/screens/TelaReservaConcluida.kt`

**Alterados (compartilhados, só acréscimos):**
- `data/AppDatabase.kt` → `CartaoSalvo::class` em `entities`, `version = 2`, `abstract fun cartaoDao()` e `.addMigrations(MIGRACAO_1_2)`.
- `navigation/Rotas.kt` → `CARTOES`, `ARG_CARTAO_ID`, `DETALHE_CARTAO`, `detalheCartao(id)`.
- `navigation/AppNavigation.kt` → 2 `composable` novos (`CARTOES`, `DETALHE_CARTAO`), `onGerenciarCartoes` na `TelaPagamento` e `formaPagamento` na `TelaReservaConcluida`.

Nada em Login, Cadastro, Home, Carros, Agendamento, Favoritos, Perfil ou nos Detalhes de carro/reserva foi alterado.

## ⚠️ Ao juntar com os colegas

- Se alguém mais também mudar o `AppDatabase`, **só pode haver uma `version`**: some as entidades e crie uma migração por salto (ou combine 1 → 2 num só).
- Quem já rodou o app com a versão 1 não perde nada; a migração roda sozinha.

## Roteiro de teste (para o vídeo)

1. Entrar, escolher um carro → diárias → **Pagamento**.
2. Digitar um cartão (16 dígitos), marcar **"Salvar este cartão"** e confirmar.
3. Fazer outra reserva: o cartão aparece **já selecionado**; digitar só o **CVV** e confirmar. Na tela final aparece "Pago com …".
4. **Gerenciar** → **Adicionar cartão**; abrir um cartão → **editar** apelido e salvar; remover outro.
5. **Fechar o app por completo e reabrir** → os cartões continuam lá.
