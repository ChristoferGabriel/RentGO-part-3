# RentGo — Aluguel de carros (Trabalho 3: persistência com Room)

App Android de locação de carros feito em **Kotlin + Jetpack Compose**. No Trabalho 3, as listas que antes viviam só na memória (`mutableStateListOf`) passaram a ser **lidas e gravadas num banco de dados local (Room/SQLite)**: adicionar, editar e remover continuam funcionando depois de fechar e reabrir o app.

> **Integrantes:** Christofer Gabriel, Matheus Antoniele de Souza, Thyago Silva Guimarães
> **Disciplina / Professor:** Des. de Aplicativos Móveis / Andre Luis Jeller Selleti
> **Repositório do Trabalho:** https://github.com/ChristoferGabriel/RentGO-part-3.git

🎥 **Vídeo do teste** 
https://youtu.be/ssEIMvnJI6I
---

## 1. Como rodar o projeto

**Requisitos:** Android Studio (versão recente), JDK 17+ e um emulador ou celular Android (minSdk 24).

1. Clone o repositório:
   ```bash
   git clone https://github.com/ChristoferGabriel/RentGO-part-3.git
   ```
2. Abra a pasta do projeto no Android Studio e aguarde o **Gradle Sync** (na primeira vez ele baixa o Room e o KSP).
3. Selecione um emulador/dispositivo e clique em **Run ▶**.
4. Na primeira abertura, toque em **"Criar conta"**. A conta fica salva no aparelho: da próxima vez é só entrar com o mesmo e-mail e senha.
5. O banco já começa com **6 carros** (os mesmos da lista fixa do Trabalho 2). Reservas e favoritos começam vazios.

**Versões principais:** Kotlin 2.2.10 · Compose BOM 2026.02.01 · Navigation Compose 2.8.9 · Material 3 · **Room 2.8.5** · **KSP 2.3.6**.



## 2. Limitações conhecidas

- O banco é **local** (fica só no aparelho): não há servidor, API nem sincronização na nuvem, como pede o trabalho.
- As reservas são do aparelho, não de cada usuário: quem entrar no mesmo celular vê as mesmas reservas.
- A senha é salva como texto no banco local. Serve para o trabalho, mas num app de verdade teria que ser protegida (hash) ou o login seria feito por um servidor.
- Quem está logado fica só na memória: ao reabrir o app é preciso entrar de novo (a conta continua salva).
- Não há migração de esquema (`version = 1`): se mudar alguma Entity, é preciso desinstalar o app antes de rodar de novo.
- O formulário de adicionar/editar carro pede só nome, preço e categoria. Subtítulo, câmbio, ar-condicionado, lugares e descrição dos carros novos usam valores padrão.
- Só o Ford Focus tem foto (`res/drawable/ford_focus.jpg`, recortada do protótipo). Para trocar por uma imagem melhor, exporte do Figma com o mesmo nome; para outros carros, adicione a imagem em `res/drawable` e um caso em `fotoDoCarro` ([`FotoCarro.kt`](app/src/main/java/com/example/rentgo/ui/components/FotoCarro.kt)).
