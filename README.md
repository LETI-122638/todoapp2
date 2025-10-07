# Vídeo de Demonstração da Aplicação
https://youtu.be/HhxRMc92h4g


# Projeto de Engenharia de Software

- António Moura - 122622
- Gonçalo Batista - 122623
- Afonso Lopes- 122631
- Rafael Silva - 122638




## D.6 - Pipeline de Integração Contínua (GitHub Actions)

Para automatizar o processo de compilação e empacotamento da aplicação, foi criada uma pipeline de Integração Contínua utilizando o GitHub Actions.  
Esta pipeline permite gerar automaticamente o ficheiro `.jar` do projeto sempre que são realizadas alterações no ramo principal (`main`).

### Funcionalidade da pipeline

O workflow executa-se automaticamente sempre que ocorre um `push` para a branch `main`.  
As suas principais etapas são as seguintes:

1. **Checkout do código** – obtém o código-fonte do repositório através da ação `actions/checkout@v4`.
2. **Configuração do ambiente Java** – instala o JDK 21 (Temurin) e ativa a cache de dependências Maven com `actions/setup-java@v4`.
3. **Compilação e empacotamento** – executa o comando `mvn clean package`, que compila o projeto e gera o ficheiro `.jar` na pasta `target/`.
4. **Publicação do artefacto** – utiliza `actions/upload-artifact@v4` para disponibilizar o `.jar` gerado como artefacto de build, acessível na interface do GitHub em Actions -> Artifacts.

### Excerto do ficheiro `build.yml`
```yaml
name: Build and Package Java Application

on:
  push:
    branches:
      - main

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout do código
        uses: actions/checkout@v4

      - name: Configurar Java 21
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
          cache: 'maven'

      - name: Compilar e empacotar o projeto
        run: mvn -B -V clean package

      - name: Publicar artefacto .jar
        uses: actions/upload-artifact@v4
        with:
          name: app-jar
          path: target/**/*.jar
```




## Project Structure

The sources of your Todoapp have the following structure:

```
src
├── main/frontend
│   └── themes  
│       └── default
│           ├── styles.css
│           └── theme.json
├── main/java
│   └── [application package]
│       ├── base
│       │   └── ui
│       │       ├── component
│       │       │   └── ViewToolbar.java
│       │       ├── MainErrorHandler.java
│       │       └── MainLayout.java
│       ├── examplefeature
│       │   ├── ui
│       │   │   └── TaskListView.java
│       │   ├── Task.java
│       │   ├── TaskRepository.java
│       │   └── TaskService.java                
│       └── Application.java       
└── test/java
    └── [application package]
        └── examplefeature
           └── TaskServiceTest.java                 
```

The main entry point into the application is `Application.java`. This class contains the `main()` method that start up 
the Spring Boot application.

The skeleton follows a *feature-based package structure*, organizing code by *functional units* rather than traditional 
architectural layers. It includes two feature packages: `base` and `examplefeature`.

* The `base` package contains classes meant for reuse across different features, either through composition or 
  inheritance. You can use them as-is, tweak them to your needs, or remove them.
* The `examplefeature` package is an example feature package that demonstrates the structure. It represents a 
  *self-contained unit of functionality*, including UI components, business logic, data access, and an integration test.
  Once you create your own features, *you'll remove this package*.

The `src/main/frontend` directory contains an empty theme called `default`, based on the Lumo theme. It is activated in
the `Application` class, using the `@Theme` annotation.

## Starting in Development Mode

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

## Building for Production

To build the application in production mode, run:

```bash
./mvnw -Pproduction package
```

To build a Docker image, run:

```bash
docker build -t my-application:latest .
```

If you use commercial components, pass the license key as a build secret:

```bash
docker build --secret id=proKey,src=$HOME/.vaadin/proKey .
```

## Getting Started

The [Getting Started](https://vaadin.com/docs/latest/getting-started) guide will quickly familiarize you with your new
Todoapp implementation. You'll learn how to set up your development environment, understand the project 
structure, and find resources to help you add muscles to your skeleton — transforming it into a fully-featured 
application.
