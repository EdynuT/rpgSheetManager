# Gerenciador de Fichas de RPG

Aplicação desktop em Java Swing para criar e organizar personagens de RPG e suas fichas.

## Funcionalidades

- Cadastro e edição de personagens.
- Gerenciamento de perícias, perícias secundárias e habilidades.
- Catálogo de itens, armas, armaduras e munições.
- Inventário por personagem, com filtro por categoria e indicação de itens equipados.
- Rolagens de dados para perícias, armas e munições.
- Armazenamento local das imagens dos personagens em `data/characterImages/`.

## Tecnologias

- Java (código compilado com compatibilidade Java 8).
- Swing para a interface gráfica.
- PostgreSQL para persistência dos dados.
- Gradle Wrapper para compilar e executar a aplicação.

## Requisitos

- JDK instalado.
- PostgreSQL instalado e em execução.

## Executar

No Linux ou macOS:

```bash
./gradlew run
```

No Windows:

```bat
gradlew.bat run
```

Para compilar e executar as verificações disponíveis:

No Linux ou macOS:

```bash
./gradlew build
```

No Windows:

```bat
gradlew.bat build
```

## Gerar um JAR executável

O comando abaixo gera um JAR com as dependências de execução incluídas e copia as fotos
para uma pasta externa ao lado do arquivo:

```bash
./gradlew jar
```

Os arquivos gerados ficam em `build/libs/`:

- `rpgSheetManager.jar`
- `data/characterImages/`

Execute o JAR com:

```bash
java -jar build/libs/rpgSheetManager.jar
```

No Windows, use `gradlew.bat jar` para gerar o pacote.

## Licença

Este projeto está sob a licença MIT. Consulte o arquivo [`LICENSE`](LICENSE).
