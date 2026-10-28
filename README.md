# RentGo — Aluguel de carros (Trabalho 3: persistência com Room)

App Android de locação de carros feito em **Kotlin + Jetpack Compose**. No Trabalho 3, as listas que antes viviam só na memória (`mutableStateListOf`) passaram a ser **lidas e gravadas num banco de dados local (Room/SQLite)**: adicionar, editar e remover continuam funcionando depois de fechar e reabrir o app.

> **Integrantes:** Christofer Gabriel, Matheus Antoniele de Souza, Thyago Silva Guimarães
> **Disciplina / Professor:** Des. de Aplicativos Móveis / Andre Luis Jeller Selleti
> **Repositório do Trabalho 2 (ponto de partida):** ⚠️ COLAR O LINK

🎥 **Vídeo do teste de persistência:** ⚠️ COLAR O LINK (ou arrastar o .mp4 para cá, ver [seção 6](#6-prints-e-vídeos))

---

## 1. Como rodar o projeto

**Requisitos:** Android Studio (versão recente), JDK 17+ e um emulador ou celular Android (minSdk 24).

1. Clone o repositório:
   ```bash
   git clone <URL-DO-REPOSITORIO>
   ```
2. Abra a pasta do projeto no Android Studio e aguarde o **Gradle Sync** (na primeira vez ele baixa o Room e o KSP).
3. Selecione um emulador/dispositivo e clique em **Run ▶**.
4. Na primeira abertura, toque em **"Criar conta"**. A conta fica salva no aparelho: da próxima vez é só entrar com o mesmo e-mail e senha.
5. O banco já começa com **6 carros** (os mesmos da lista fixa do Trabalho 2). Reservas e favoritos começam vazios.

**Versões principais:** Kotlin 2.2.10 · Compose BOM 2026.02.01 · Navigation Compose 2.8.9 · Material 3 · **Room 2.8.5** · **KSP 2.3.6**.

**Dicas:**
- **Ver o banco por dentro:** com o app rodando, abra *View > Tool Windows > App Inspection > Database Inspector*. Aparecem as tabelas `carros`, `reservas` e `usuarios` mudando em tempo real.
- **Rodar de novo pelo Android Studio não apaga o banco.** Só desinstalar o app ou *Configurações > Apps > RentGo > Armazenamento > Limpar dados* volta ao estado inicial.

---

## 2. Trabalho 2 → Trabalho 3: o que mudou

| | Trabalho 2 | Trabalho 3 |
|---|---|---|
| Lista de carros | `CarrosRepositorio.carros` (`mutableStateListOf` com 6 carros fixos) | Tabela `carros` no Room; os 6 carros entram uma única vez, quando o banco é criado |
| Lista de reservas | `ReservasRepositorio.reservas` (`mutableStateListOf`) | Tabela `reservas` no Room |
| Favoritos | Lista de ids separada, só na memória | Coluna `favorito` da tabela `carros` (gravada com `@Update`) |
| Contas de usuário | `SessaoUsuario.usuarios`, só na memória | Tabela `usuarios` no Room (quem está logado continua na memória) |
| Ids | Calculados à mão (`maxOf + 1`, `proximoId++`) | `@PrimaryKey(autoGenerate = true)`: o banco gera |
| Telas de lista | Liam a lista global direto | Buscam do banco ao abrir: `LaunchedEffect(Unit)` + função `suspend` que usa `dao.buscarTodos()` |
| Adicionar / remover | Mexiam na lista em memória | `dao.inserir(...)` / `dao.deletar(...)` dentro de `CoroutineScope(Dispatchers.IO).launch` |
| Editar | Só as diárias da reserva, em memória | **Editar carro** (lápis no card e no Detalhe), **favoritar**, **editar diárias** e **recuperar senha**: todos com `dao.atualizar(...)` |
| Detalhes | Buscavam o item na lista em memória pelo id | Buscam no banco pelo id da rota: `carroDao.buscarPorId(id)` e `reservaDao.buscarPorId(id)` |
| Fluxo de reserva | O Detalhe do carro já tinha a escolha de diárias e ia direto ao Pagamento | **Detalhe do carro** (layout do Figma: foto, especificações, "Sobre o veículo", preço por dia) → **Escolher diárias** (tela nova) → **Pagamento** |
| Fechar e reabrir o app | Tudo sumia | Tudo continua lá |

---

## 3. Room no projeto

### 3.1 Dependências

Seguindo a ordem vista em aula: primeiro o plugin do KSP (sincroniza), depois as dependências do Room.

`build.gradle.kts` (raiz):
```kotlin
plugins {
    // ...
    id("com.google.devtools.ksp") version "2.3.6" apply false
}
```

`app/build.gradle.kts`:
```kotlin
plugins {
    // ...
    id("com.google.devtools.ksp")
}

dependencies {
    // ...
    val room_version = "2.8.5"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
}
```

### 3.2 Entities (tabelas)

Reaproveitamos as 2 data classes do Trabalho 2 (`Carro` e `Reserva`) e transformamos também a `Usuario` em tabela.

| `@Entity` | Tabela | Campos | Por que esses campos |
|---|---|---|---|
| [`Carro`](app/src/main/java/com/example/rentgo/model/Carro.kt) | `carros` | `id` (autoGenerate), `nome`, `categoria`, `precoDia`, `nota`, `emOferta`, `favorito`, `subtitulo`, `cambio`, `arCondicionado`, `lugares`, `descricao` | São exatamente o que as telas mostram e filtram: nome e categoria (busca e filtros), preço (cálculo do total), nota (ordenar e selo do Detalhe), `emOferta` (Home) e `favorito` (aba Favoritos). `subtitulo` ("Hatchback • Completo"), `cambio`, `arCondicionado`, `lugares` e `descricao` alimentam o Detalhe do carro (especificações técnicas e "Sobre o veículo"); carros novos usam valores padrão e, se o subtítulo ou a descrição estiverem vazios, a tela monta um texto a partir dos outros campos. A `categoria` é um `enum` e o Room já salva o nome dele como texto (`"SEDA"`, `"SUV"`...). O campo `imagemRes` do Trabalho 2 saiu: o número de um `R.drawable` muda a cada build, então não faz sentido guardar no banco. A foto do Ford Focus fica em `res/drawable/ford_focus.jpg` e é escolhida pelo nome do carro em [`FotoCarro.kt`](app/src/main/java/com/example/rentgo/ui/components/FotoCarro.kt); os outros carros mostram o ícone da categoria. |
| [`Reserva`](app/src/main/java/com/example/rentgo/model/Reserva.kt) | `reservas` | `id` (autoGenerate), `carroId`, `nomeCarro`, `precoDia`, `dias` | `nomeCarro` e `precoDia` são uma "foto" do carro no momento da reserva: se o carro mudar de preço ou for removido, a reserva mantém o valor combinado. `carroId` liga a reserva ao carro (o Detalhe da reserva busca categoria e nota com ele). O `total` é calculado (`precoDia * dias`) e não vira coluna. |
| [`Usuario`](app/src/main/java/com/example/rentgo/model/Usuario.kt) | `usuarios` | `id` (autoGenerate), `nome`, `email`, `senha` | O que o cadastro pede. Sem salvar as contas, o teste de persistência não funcionava: ao reabrir o app a conta tinha sumido e não dava para entrar para ver os dados. O login continua 100% local, sem servidor. |

### 3.3 DAOs

Cada DAO tem os 4 métodos básicos (todos `suspend fun`) e consultas extras filtrando por campo:

| DAO | Básicos | Consultas extras |
|---|---|---|
| [`CarroDao`](app/src/main/java/com/example/rentgo/data/CarroDao.kt) | `inserir`, `buscarTodos`, `atualizar`, `deletar` | `buscarPorId(id)` (Detalhe) · `buscarOfertas()` (`WHERE emOferta = 1`, Home) · `buscarFavoritos()` (`WHERE favorito = 1`, aba Favoritos) |
| [`ReservaDao`](app/src/main/java/com/example/rentgo/data/ReservaDao.kt) | `inserir`, `buscarTodos`, `atualizar`, `deletar` | `buscarPorId(id)` (Detalhe da reserva) |
| [`UsuarioDao`](app/src/main/java/com/example/rentgo/data/UsuarioDao.kt) | `inserir`, `buscarTodos`, `atualizar`, `deletar` | `buscarPorEmail(email)` (login, cadastro e recuperar senha) |

Exemplo:
```kotlin
@Query("SELECT * FROM carros WHERE id = :id")
suspend fun buscarPorId(id: Int): Carro?   // null se o carro foi removido
```

### 3.4 AppDatabase (Singleton)

[`AppDatabase.kt`](app/src/main/java/com/example/rentgo/data/AppDatabase.kt): `abstract class AppDatabase : RoomDatabase()` com `@Database(entities = [Carro::class, Reserva::class, Usuario::class], version = 1)`, um método para cada DAO e o Singleton visto em aula (`companion object` + `@Volatile` + `synchronized`), criando o banco com `Room.databaseBuilder(...)`.

Também tem um `RoomDatabase.Callback` com `onCreate`, que coloca os 6 carros iniciais. Ele roda **uma vez só**, quando o arquivo do banco é criado; por isso um carro apagado não "ressuscita" ao reabrir o app.

### 3.5 Onde cada operação acontece

| Operação | Onde o usuário faz | Função chamada | DAO |
|---|---|---|---|
| **Buscar** carros | Abrir a aba Carros | `CarrosRepositorio.buscarTodos` no `LaunchedEffect(Unit)` | `carroDao.buscarTodos()` |
| **Inserir** carro | Carros > "Adicionar carro" | `CarrosRepositorio.inserir` | `carroDao.inserir()` |
| **Atualizar** carro | Lápis no card (Carros) ou no Detalhe | `CarrosRepositorio.atualizar` | `carroDao.atualizar()` |
| Buscar carro pelo id | Abrir o Detalhe do carro ou a tela Escolher diárias | `CarrosRepositorio.buscarPorId` no `LaunchedEffect(carroId)` | `carroDao.buscarPorId()` |
| **Remover** carro | Lixeira no card (Carros) | `CarrosRepositorio.deletar` | `carroDao.deletar()` |
| Favoritar / desfavoritar | Coração no Detalhe ou na aba Favoritos | `CarrosRepositorio.atualizar` | `carroDao.atualizar()` |
| **Buscar** reservas | Abrir a aba Agendamento | `ReservasRepositorio.buscarTodas` no `LaunchedEffect(Unit)` | `reservaDao.buscarTodos()` |
| **Inserir** reserva | Agendamento > "Agendar", ou Detalhe > "Reservar agora" > Escolher diárias > Pagamento | `ReservasRepositorio.inserir` | `reservaDao.inserir()` |
| **Atualizar** reserva | Detalhe da reserva > − / + diárias | `ReservasRepositorio.atualizar` | `reservaDao.atualizar()` |
| **Remover** reserva | Lixeira no card ou "Cancelar reserva" no Detalhe | `ReservasRepositorio.deletar` | `reservaDao.deletar()` |
| Criar conta / entrar / nova senha | Cadastro / Login / "Recuperar senha?" | `SessaoUsuario.cadastrar` / `entrar` / `redefinirSenha` | `usuarioDao.inserir` / `buscarPorEmail` / `atualizar` |

### 3.6 Coroutines e tratamento de erro

Nenhuma função do DAO é chamada na thread principal:

- **Buscar** (assim que a tela abre):
  ```kotlin
  // na tela
  LaunchedEffect(Unit) {
      carrosDoBanco = CarrosRepositorio.buscarTodos(context)
  }

  // no repositório
  suspend fun buscarTodos(context: Context): List<Carro> = withContext(Dispatchers.IO) {
      try {
          dao(context).buscarTodos()
      } catch (e: Exception) {
          Log.e(TAG, "Erro ao buscar os carros", e)
          emptyList()
      }
  }
  ```
- **Inserir / atualizar / deletar**:
  ```kotlin
  fun inserir(context: Context, carro: Carro, aoTerminar: (List<Carro>) -> Unit = {}) {
      CoroutineScope(Dispatchers.IO).launch {
          try {
              val dao = dao(context)
              dao.inserir(carro)
              val listaAtualizada = dao.buscarTodos()                 // relê o banco
              withContext(Dispatchers.Main) { aoTerminar(listaAtualizada) } // a tela se atualiza
          } catch (e: Exception) {
              Log.e(TAG, "Erro ao inserir o carro ${carro.nome}", e)
          }
      }
  }
  ```
  Na tela: `CarrosRepositorio.inserir(context, novo) { listaAtualizada -> carrosDoBanco = listaAtualizada }`.

Se o banco der erro, o app não fecha: a mensagem aparece no **Logcat** (filtre por `CarrosRepositorio`, `ReservasRepositorio` ou `SessaoUsuario`).

### 3.7 Telas de Detalhes lendo do banco

As telas de Detalhes recebem **só o id** pela rota (`detalhe_carro/{carroId}`, `escolher_diarias/{carroId}`, `detalhe_reserva/{reservaId}`) e buscam o item no banco:

- [`TelaDetalheCarro`](app/src/main/java/com/example/rentgo/ui/screens/TelaDetalheCarro.kt): `LaunchedEffect(carroId)` → `carroDao.buscarPorId(id)`. Segue o layout do Figma: foto, nome, subtítulo e nota, especificações técnicas (câmbio, ar-condicionado, lugares), "Sobre o veículo" e a barra com o preço por dia e o botão **RESERVAR AGORA**. Também tem voltar, editar (lápis) e favoritar (coração) sobre a foto.
- [`TelaEscolherDiarias`](app/src/main/java/com/example/rentgo/ui/screens/TelaEscolherDiarias.kt): aberta pelo "Reservar agora". Busca o carro de novo pelo id, deixa escolher de 1 a 30 diárias (− / + ou atalhos 1, 3, 5, 7, 15, 30 dias), calcula o total e o valor no Pix e leva ao **Pagamento**.
- [`TelaDetalheReserva`](app/src/main/java/com/example/rentgo/ui/screens/TelaDetalheReserva.kt): `reservaDao.buscarPorId(id)` e, com o `carroId` da reserva, `carroDao.buscarPorId(...)` (junta as duas tabelas).

Enquanto a busca não termina aparece "Carregando..."; se o id não existir mais (item apagado), aparece uma mensagem em vez de erro.

---

## 4. Estrutura do código

```
app/src/main/java/com/example/rentgo/
├── MainActivity.kt
├── navigation/
│   ├── Rotas.kt              # rotas nomeadas (detalhes recebem só o id)
│   └── AppNavigation.kt      # NavHost + Scaffold + BottomNavigation
├── model/                    # @Entity: Carro, Reserva, Usuario (+ DadosReserva, só para o Pagamento)
├── data/
│   ├── AppDatabase.kt        # @Database + Singleton + carros iniciais
│   ├── CarroDao.kt           # @Dao da tabela carros
│   ├── ReservaDao.kt         # @Dao da tabela reservas
│   ├── UsuarioDao.kt         # @Dao da tabela usuarios
│   ├── CarrosRepositorio.kt  # chama o CarroDao (IO + try/catch)
│   ├── ReservasRepositorio.kt# chama o ReservaDao (IO + try/catch)
│   └── SessaoUsuario.kt      # contas (UsuarioDao) + quem está logado (memória)
└── ui/
    ├── screens/              # uma tela por arquivo (inclui TelaEscolherDiarias)
    ├── components/           # BarraInferior, TopoRentGo, FotoCarro, campos, avatar, logo
    └── theme/
```

Telas: Login, Cadastro, Home, Carros, Detalhe do carro, **Escolher diárias** (nova), Pagamento, Reserva concluída, Agendamento, Detalhe da reserva, Favoritos e Perfil, com `NavHost`, `TopAppBar` com voltar e `NavigationBar` de 5 abas.

Fluxo de reserva: **Carros/Home → Detalhe do carro → "Reservar agora" → Escolher diárias → "Continuar" → Pagamento → Reserva concluída** (a reserva paga é salva no banco e aparece em Agendamento).

---

## 5. Documentação do processo e das decisões

> ⚠️ **Para o trio:** os textos abaixo descrevem o que mudou no código. Revisem e reescrevam com as palavras de vocês, e completem as partes marcadas com ⚠️ (principalmente a 5.6, que é pessoal).

### 5.1 Como estava o projeto no Trabalho 2, e o que mudou para chegar até aqui?

No Trabalho 2 o app já tinha toda a navegação (login, abas, detalhes, pagamento) e duas listas com adicionar e remover: **Carros** e **Agendamento**. Só que tudo morava em `object`s com `mutableStateListOf` (`CarrosRepositorio`, `ReservasRepositorio`, `SessaoUsuario`). Era prático, porque qualquer tela que lia a lista se atualizava sozinha, mas bastava fechar o app para perder tudo: carros adicionados, reservas, favoritos e até a conta criada.

Para o Trabalho 3, mantivemos as mesmas telas, formulários e botões e trocamos o "lugar" onde os dados ficam: agora são três tabelas Room. Os repositórios deixaram de guardar listas e passaram a só chamar os DAOs. Cada tela de lista ganhou um estado próprio que é preenchido pelo banco ao abrir e atualizado depois de cada inserção, edição ou remoção. Também implementamos a **edição de carros** (antes não existia) e a edição de diárias passou a gravar no banco. Por fim, o Detalhe do carro foi refeito seguindo o layout do Figma, e a escolha de diárias virou uma tela própria entre o Detalhe e o Pagamento.

📸 *Print: Carros no Trabalho 2* → `docs/prints/01-trabalho2-carros.png`
📸 *Print: Carros agora (com o lápis de editar)* → `docs/prints/02-trabalho3-carros.png`

### 5.2 Quais Entity, DAO e Database foram criados — e por que esses campos?

- **Entities:** `Carro` (tabela `carros`), `Reserva` (tabela `reservas`) e `Usuario` (tabela `usuarios`). Os motivos de cada campo estão na [tabela da seção 3.2](#32-entities-tabelas). Resumindo: guardamos só o que as telas mostram, filtram ou calculam. Tiramos o `imagemRes` porque não faz sentido gravar um id de recurso, e trocamos a lista separada de favoritos por uma coluna `favorito`, que é mais simples e já fica salva junto com o carro.
- **DAOs:** `CarroDao`, `ReservaDao` e `UsuarioDao`, um por tabela, cada um com os 4 métodos básicos.
- **Database:** `AppDatabase`, Singleton, reunindo as 3 entidades na versão 1.

📸 *Print do Database Inspector com as 3 tabelas* → `docs/prints/03-database-inspector.png`

### 5.3 Como decidimos estruturar as queries do DAO?

Partimos dos 4 básicos (`inserir`, `buscarTodos`, `atualizar`, `deletar`) e criamos uma consulta extra para cada lugar do app que antes filtrava a lista em memória:

- `buscarPorId(id)` em carros e reservas: as telas de Detalhe recebem só o id pela rota e buscam o item certo no banco.
- `buscarOfertas()` e `buscarFavoritos()`: em vez de trazer todos os carros e filtrar no Kotlin, o próprio SQL já devolve só o que a Home e a aba Favoritos precisam (`WHERE emOferta = 1`, `WHERE favorito = 1`).
- `buscarPorEmail(email)`: login, cadastro (não deixa criar duas contas com o mesmo e-mail) e recuperar senha.

Todos os `buscarTodos` usam `ORDER BY id`, para a lista aparecer sempre na ordem em que os itens foram criados.

### 5.4 Qual foi a dificuldade em migrar de `mutableStateListOf` para Room? O que quebrou e como resolvemos?

- **A tela parou de se atualizar sozinha.** Com `mutableStateListOf`, ao adicionar um carro a lista na tela mudava na hora. Com o Room, cada tela tem a sua cópia da lista, então depois de inserir nada acontecia. **Solução:** depois de cada `inserir/atualizar/deletar`, o repositório relê o banco (`buscarTodos()`) e devolve a lista nova para a tela, já na thread principal (`withContext(Dispatchers.Main)`).
- **"Suspend function should be called only from a coroutine".** As funções do DAO são `suspend` e não dá para chamá-las direto no `onClick`. **Solução:** leituras dentro do `LaunchedEffect`; gravações dentro de `CoroutineScope(Dispatchers.IO).launch { ... }`.
- **Ids.** No Trabalho 2 a gente calculava o próximo id na mão. **Solução:** `@PrimaryKey(autoGenerate = true)` com `id = 0` ao criar; o banco gera o número.
- **A aba Carros abria vazia.** O banco novo não tinha os 6 carros que eram fixos na lista. **Solução:** `RoomDatabase.Callback.onCreate`, que insere os carros uma única vez, quando o banco é criado.
- **Favoritos e foto do carro.** Os favoritos eram uma lista de ids separada, e o `imagemRes` não podia ir para o banco. **Solução:** coluna `favorito` (com `@Update`) e remoção do `imagemRes`.
- **Depois de fechar e reabrir, não dava para entrar.** As contas ainda estavam em memória, então o teste de persistência parava na tela de login. **Solução:** `Usuario` virou `@Entity` também.
- **O Detalhe piscava "Carro não encontrado".** A busca leva alguns milissegundos, e nesse meio tempo o carro ainda era `null`. **Solução:** estado `carregando`, que mostra "Carregando..." até a busca terminar.
- **Ordem do KSP.** Se o `ksp(...)` for colocado antes de sincronizar o plugin `com.google.devtools.ksp`, o Gradle não reconhece `ksp`. **Solução:** sincronizar o plugin primeiro, como visto em aula.
- **Cuidado se mudarem uma Entity depois de instalar:** o Room fecha o app avisando que o esquema mudou sem mudar a `version`. Durante o desenvolvimento, basta desinstalar o app (ou limpar os dados) e rodar de novo.

⚠️ Confirmem quais desses aconteceram de fato com vocês e acrescentem o que mais quebrou.

### 5.5 Teste de persistência (adicionar → fechar → reabrir)

🎥 **Vídeo:** ⚠️ COLAR O LINK → `docs/videos/teste-persistencia.mp4`

Roteiro gravado:
1. Entrar com a conta.
2. Aba **Carros** → **Adicionar carro** (ex.: "Fiat Pulse", R$ 130, SUV) → aparece na lista.
3. Fechar o app **de verdade**: botão de apps recentes → arrastar o RentGo para fora (ou *Stop* no Android Studio).
4. Abrir o app de novo → entrar com a mesma conta → aba **Carros**: o "Fiat Pulse" continua lá.

📸 *Antes de fechar* → `docs/prints/04-persistencia-antes.png` · *Depois de reabrir* → `docs/prints/05-persistencia-depois.png`

### 5.6 Algum integrante do trio teve dificuldade em algum ponto? Como resolveram?

⚠️ PREENCHER com o relato real de cada integrante (dificuldade → como resolveram).

- **Christofer:** ⚠️
- **Matheus:** ⚠️
- **Thyago:** ⚠️

---

## 6. Prints e vídeos

Coloquem os arquivos em `docs/prints/` e `docs/videos/`. Para o vídeo tocar dentro do README no GitHub, editem este arquivo pelo site e **arrastem o .mp4** para o editor (o GitHub gera um link que vira player). Também vale subir no YouTube ou Google Drive e colar o link.

| Arquivo | O que mostrar |
|---|---|
| `01-trabalho2-carros.png` | Aba Carros como era no Trabalho 2 |
| `02-trabalho3-carros.png` | Aba Carros agora, com lápis e lixeira |
| `03-database-inspector.png` | Database Inspector com as tabelas `carros`, `reservas`, `usuarios` |
| `04-persistencia-antes.png` / `05-persistencia-depois.png` | Item novo antes de fechar e depois de reabrir |
| `06-editar-carro.png` | Formulário "Editar carro" preenchido |
| `07-detalhe-carro.png` / `08-escolher-diarias.png` / `09-detalhe-reserva.png` | Detalhe do carro (layout do Figma), escolha de diárias e Detalhe da reserva |
| `videos/fluxo-reserva.mp4` | Detalhe → Reservar agora → escolher diárias → Pagamento → reserva aparece em Agendamento |
| `videos/crud-carros.mp4` | Adicionar, editar e remover um carro |
| `videos/crud-reservas.mp4` | Agendar, editar diárias no Detalhe e cancelar uma reserva |
| `videos/teste-persistencia.mp4` | Roteiro da seção 5.5 (o mais importante) |

**Como gravar a tela do emulador:** botão "⋯" (Extended controls) → *Record and Playback*, ou no terminal:
```bash
adb shell screenrecord /sdcard/teste.mp4   # Ctrl+C para parar
adb pull /sdcard/teste.mp4
```

---

## 7. Roteiro da apresentação (ao vivo)

1. **Adicionar:** Carros → "Adicionar carro" → preencher → aparece na lista.
2. **Editar:** lápis no card → mudar o preço → a lista mostra o preço novo. (Opcional: abrir o Detalhe e favoritar.)
3. **Remover:** lixeira em outro carro → some da lista.
4. **Reservar pelo fluxo:** abrir um carro → "Reservar agora" → escolher as diárias → "Continuar" → pagar no Pix → a reserva aparece em Agendamento.
5. **Editar reserva:** Agendamento → abrir a reserva → − / + nas diárias → voltar (o total novo aparece no card).
6. **Fechar e reabrir:** arrastar o app para fora dos recentes → abrir → entrar → mostrar que o carro adicionado, o preço editado, o carro removido, o favorito e a reserva continuam como estavam.
7. (Extra) Mostrar as tabelas no **Database Inspector**.

---

## 8. Checklist da entrega

- [ ] Novo repositório GitHub criado, com README explicando como rodar o projeto ⚠️ *(o README já está pronto; falta criar o repositório e colar o link no topo)*
- [ ] Documentação do processo e das decisões no repositório, com prints/vídeo (seção 5) ⚠️ *(faltam os prints/vídeos e a 5.6)*
- [x] Dependências do Room configuradas (`ksp` + `room-runtime` + `room-ktx` + `room-compiler`)
- [x] No mínimo 2 `@Entity`, cada uma com `@PrimaryKey(autoGenerate = true)` → `Carro`, `Reserva`, `Usuario`
- [x] No mínimo 2 `@Dao` com `@Insert`, `@Query`, `@Update` e `@Delete`, todos `suspend` → `CarroDao`, `ReservaDao`, `UsuarioDao`
- [x] Consulta filtrada além do `buscarTodos()` → `buscarPorId`, `buscarOfertas`, `buscarFavoritos`, `buscarPorEmail`
- [x] Tela de Detalhes busca o item direto do banco pelo id → `TelaDetalheCarro`, `TelaEscolherDiarias` e `TelaDetalheReserva`
- [x] `AppDatabase` em padrão Singleton
- [x] Listas buscam do banco ao abrir a tela (`LaunchedEffect`) → Carros, Agendamento, Favoritos, Home
- [x] Adicionar pela UI salva no banco
- [x] Editar pela UI atualiza no banco (carro, favorito, diárias, senha)
- [x] Remover pela UI apaga do banco
- [ ] Testei: adicionar, fechar, reabrir — o item continua lá ⚠️ *(testar no emulador e gravar o vídeo)*
- [x] Toda operação de banco dentro de Coroutine no `Dispatchers.IO`, com `try/catch` + `Log.e`

---

## 9. Limitações conhecidas

- O banco é **local** (fica só no aparelho): não há servidor, API nem sincronização na nuvem, como pede o trabalho.
- As reservas são do aparelho, não de cada usuário: quem entrar no mesmo celular vê as mesmas reservas.
- A senha é salva como texto no banco local. Serve para o trabalho, mas num app de verdade teria que ser protegida (hash) ou o login seria feito por um servidor.
- Quem está logado fica só na memória: ao reabrir o app é preciso entrar de novo (a conta continua salva).
- Não há migração de esquema (`version = 1`): se mudar alguma Entity, é preciso desinstalar o app antes de rodar de novo.
- O formulário de adicionar/editar carro pede só nome, preço e categoria. Subtítulo, câmbio, ar-condicionado, lugares e descrição dos carros novos usam valores padrão.
- Só o Ford Focus tem foto (`res/drawable/ford_focus.jpg`, recortada do protótipo). Para trocar por uma imagem melhor, exporte do Figma com o mesmo nome; para outros carros, adicione a imagem em `res/drawable` e um caso em `fotoDoCarro` ([`FotoCarro.kt`](app/src/main/java/com/example/rentgo/ui/components/FotoCarro.kt)).
