# QRCodeGenerator

Aplicação web desenvolvida em Java para gerar QR Codes de links (URLs) e redes Wi-Fi. O sistema disponibiliza uma interface no navegador, recebe os dados por uma API REST, gera uma imagem PNG e salva uma cópia no diretório configurado.

## Funcionalidades

- Gerar QR Codes para endereços web.
- Adicionar automaticamente `https://` quando o endereço não possui protocolo.
- Validar URLs antes de gerar o código.
- Gerar QR Codes de Wi-Fi com nome da rede, senha e tipo de segurança.
- Suportar redes WPA, WEP e redes sem senha.
- Exibir a imagem gerada diretamente no navegador.
- Permitir baixar o QR Code em formato PNG.
- Salvar automaticamente uma cópia na pasta `Downloads`.
- Criar o diretório de saída caso ele não exista.
- Compatibilidade com Linux e Windows por meio de caminhos configuráveis.

## Como funciona

O funcionamento da aplicação segue estas etapas:

1. O usuário acessa a interface web `index.html`.
2. Seleciona o tipo de QR Code que deseja gerar.
3. Preenche os campos necessários.
4. O JavaScript envia os dados em formato JSON para o endpoint `/api/qrcode`.
5. O `QRCodeController` recebe a requisição HTTP.
6. O `QRCodeRequest` representa os dados recebidos.
7. O `QRCodeGeneratorService` valida os campos e prepara o conteúdo do QR Code.
8. A biblioteca ZXing converte o conteúdo em uma matriz QR e gera uma imagem PNG de 400 × 400 pixels.
9. O backend devolve a imagem ao navegador.
10. A interface exibe o resultado e disponibiliza o download.
11. O serviço salva uma cópia do arquivo no diretório configurado.

## Tipos de QR Code

### QR Code de link

Armazena um endereço web. Ao escanear o código, o celular reconhece a URL e pode oferecer a abertura no navegador.

Exemplo:

```text
https://github.com/Jbosettidev/QRCodeGenerator
```

O projeto não hospeda o site de destino. Ele apenas codifica o endereço informado.

### QR Code de Wi-Fi

Armazena as informações necessárias para identificar uma rede e solicitar a conexão.

Exemplo do formato utilizado:

```text
WIFI:T:WPA;S:MinhaRede;P:MinhaSenha;;
```

Os campos representam:

- `T`: tipo de segurança.
- `S`: nome da rede (SSID).
- `P`: senha da rede.

Celulares compatíveis podem reconhecer essas informações e oferecer a opção de conectar à rede.

**Segurança:** um QR Code de Wi-Fi pode revelar a senha da rede a quem tiver acesso à imagem. Evite compartilhar códigos de redes privadas publicamente.

## Tecnologias e dependências

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java JDK | 21, se configurado no projeto | Linguagem e ambiente de execução |
| Spring Boot | 3.5.6, se declarado no `pom.xml` | Inicialização da aplicação e gerenciamento dos componentes |
| Spring Web | Gerenciada pelo Spring Boot | API REST e servidor web |
| ZXing Core | Conferir no `pom.xml` | Codificação dos dados em QR Code |
| ZXing Java SE | Conferir no `pom.xml` | Conversão da matriz QR em imagem PNG |
| Maven Wrapper | Conforme configuração do projeto | Compilação e execução das dependências |
| HTML, CSS e JavaScript | Recursos nativos do navegador | Interface e comunicação com o backend |

As versões exatas das dependências devem ser confirmadas no `pom.xml`. Não declare versões diferentes sem verificar as configurações atuais do projeto.

### Como verificar as versões

No Linux:

```bash
java -version
./mvnw dependency:tree
```

No Windows PowerShell:

```powershell
java -version
.\mvnw.cmd dependency:tree
```

No arquivo `pom.xml`, confira também as propriedades `java.version` e a versão do Spring Boot.

## Pré-requisitos

- JDK compatível com o projeto (Java 21, se essa for a versão configurada).
- Maven Wrapper incluído no repositório.
- Conexão com a internet na primeira execução, para baixar dependências.
- Permissão de escrita no diretório de saída.

## Como executar no Linux

1. Abra o terminal na raiz do repositório, onde está o arquivo `pom.xml`.
2. Se necessário, dê permissão de execução ao Maven Wrapper:

```bash
chmod +x mvnw
```

3. Inicie a aplicação:

```bash
./mvnw spring-boot:run
```

4. Abra no navegador:

http://localhost:8080

## Como executar no Windows

1. Abra o PowerShell na raiz do repositório.
2. Execute:

```powershell
.\mvnw.cmd spring-boot:run
```

3. Abra no navegador:

http://localhost:8080

## Configuração do diretório de saída

O arquivo `src/main/resources/application.properties` deve conter:

```properties
qrcode.output.directory=${user.home}/Downloads
```

A propriedade `${user.home}` é resolvida pelo Java de acordo com o usuário que executa a aplicação.

Exemplos:

- Linux: `/home/usuario/Downloads`
- Windows: `C:\Users\Usuario\Downloads`

O serviço utiliza `Files.createDirectories()` para criar a pasta caso ela não exista.

**Importante:** os arquivos são salvos no computador ou servidor em que o backend está executando. O download pelo navegador é independente desse salvamento.

## Endpoint da API

### `POST /api/qrcode`

Recebe dados em JSON e responde com uma imagem PNG (`image/png`).

#### Exemplo: gerar QR Code de link

```json
{
  "type": "url",
  "url": "https://github.com/Jbosettidev/QRCodeGenerator"
}
```

#### Exemplo: gerar QR Code de Wi-Fi

```json
{
  "type": "wifi",
  "ssid": "MinhaRede",
  "password": "MinhaSenha",
  "security": "WPA"
}
```

Para redes sem senha, utilize `"security": "nopass"` e deixe o campo `password` vazio.

## Estrutura do projeto

```text
QRCodeGenerator/
├── src/
│   ├── main/
│   │   ├── java/com/bosetti/qrcode/generator/
│   │   │   ├── QRCodeGeneratorApplication.java
│   │   │   ├── QRCodeController.java
│   │   │   ├── QRCodeGeneratorService.java
│   │   │   └── QRCodeRequest.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           └── index.html
│   └── test/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

### Responsabilidade de cada arquivo

- `QRCodeGeneratorApplication.java`: inicializa a aplicação Spring Boot.
- `QRCodeController.java`: recebe as requisições HTTP e devolve a imagem PNG.
- `QRCodeGeneratorService.java`: valida os dados, gera a imagem e salva o arquivo.
- `QRCodeRequest.java`: representa os dados enviados pelo formulário.
- `application.properties`: configura o diretório de saída.
- `index.html`: contém a interface, os campos e as ações de geração e download.
- `pom.xml`: define as dependências e configurações de compilação.
- `mvnw` e `mvnw.cmd`: permitem executar o Maven no Linux/macOS e Windows, respectivamente.

## Compilar e testar

Para executar os testes existentes:

Linux:

```bash
./mvnw clean test
```

Windows:

```powershell
.\mvnw.cmd clean test
```

Para gerar o pacote da aplicação:

Linux:

```bash
./mvnw clean package
```

Windows:

```powershell
.\mvnw.cmd clean package
```

O arquivo JAR gerado normalmente ficará no diretório `target/`. Caso o projeto produza um JAR executável do Spring Boot, execute-o com:

```bash
java -jar target/NOME-DO-ARQUIVO.jar
```

Substitua `NOME-DO-ARQUIVO.jar` pelo nome real do arquivo gerado.

## Problemas comuns

### Erro de permissão no Linux

Execute:

```bash
chmod +x mvnw
```

Depois tente iniciar a aplicação novamente.

### Erro de versão do Java

Confira `java -version` e a propriedade `java.version` no `pom.xml`. Configure um JDK compatível.

### Erro nos imports do ZXing

Confirme se as dependências `com.google.zxing:core` e `com.google.zxing:javase` estão presentes no `pom.xml`.

### A página não abre

Verifique se a aplicação iniciou corretamente e se o arquivo `index.html` está em `src/main/resources/static/`.

### O arquivo não é salvo

Verifique a propriedade `qrcode.output.directory`, o diretório resultante e as permissões de escrita.

### O celular não conecta ao Wi-Fi

Confira o nome da rede, a senha e o tipo de segurança. A conexão depende do suporte do celular e pode exigir confirmação do usuário.

## Limitações

- Os QR Codes são estáticos. Para alterar um destino depois da impressão, é necessário que o código aponte para um serviço de redirecionamento controlado.
- O sistema não hospeda os sites informados.
- O QR Code de Wi-Fi não ativa o Wi-Fi nem força uma conexão sem a confirmação do usuário.
- O salvamento no backend e o download pelo navegador são operações distintas.

## Repositório

https://github.com/Jbosettidev/QRCodeGenerator
