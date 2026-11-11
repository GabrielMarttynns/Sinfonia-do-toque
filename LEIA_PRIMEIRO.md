# Sinfonia do Toque — abrir e executar no IntelliJ

O projeto que você enviou tinha os códigos, mas faltavam **`build.gradle` e `settings.gradle`**. Agora esses arquivos estão na raiz desta pasta.

## No seu Windows

1. **Extraia** o arquivo ZIP para uma pasta nova. Não abra o ZIP diretamente no IntelliJ.
2. Abra o IntelliJ IDEA → **File → Open** → selecione a pasta **Sinfonia_do_Toque_Corrigido** que contém `settings.gradle`.
3. Se perguntar **Trust Project**, confirme somente se reconhecer esta cópia.
4. Se aparecer a barra do Gradle, clique **Load Gradle Project** / **Sync**. Aguarde baixar bibliotecas (internet necessária).
5. Se houver erros de Java, configure **JDK 17 ou 21** em `File → Project Structure → Project SDK` e em `Settings → Build Tools → Gradle → Gradle JVM`.
6. Para testar rapidamente sem o hardware, dê dois cliques em **`RODAR_TESTE_SEM_ARDUINO.bat`** no Explorador de Arquivos. Os controles serão `Z X C`, `ESPAÇO` e `R`.
7. Para apresentar usando o Arduino de verdade, faça o upload do `.ino` pela Arduino IDE, feche o Monitor Serial e execute **`RODAR_JOGO.bat`**.

**Não use** `gradlew.bat` nesta versão: o Wrapper original não veio no projeto e não foi possível incluí-lo. Os arquivos `RODAR_*.bat` iniciam com Gradle oficial e fazem download seguro com verificação de checksum.

## Se aparecerem erros

- **package com.badlogic.gdx does not exist:** falta sincronizar o Gradle; abra a pasta correta e clique no ícone de sincronização do Gradle.
- **Gradle JVM is invalid / JAVA_HOME:** configure o JDK 17 ou 21 (não apenas o JRE).
- **Could not resolve dependencies:** verifique internet e acesso ao Maven Central.
- **Arduino não encontrado:** verifique o cabo USB, a porta COM, o driver e se o Monitor Serial está fechado.
- **Botões não respondem:** confirme que cada botão está ligado entre D2/D3/D4/D5 e GND (sem 5 V nos botões em `INPUT_PULLUP`).

## O que foi corrigido

- Inclusão dos arquivos Gradle para libGDX e jSerialComm.
- Pastas `core` e `lwjgl3` preservadas, com os pacotes e classes existentes.
- Scripts de inicialização no Windows (normal e teste).
- Modo de teclado desligado por padrão, como exige o professor.
- Melhor filtro contra ruído dos botões no Arduino (debounce).
- Proteção contra mensagens seriais grandes e retorno negativo na leitura.
- Consistência só é calculada com pelo menos duas respostas, e bônus de velocidade reduzido.

**Limitação honesta:** sintaxe Java revisada com uma compilação de substitutos das APIs; execução real do jogo, Gradle e Arduino não testados aqui por falta de acesso às bibliotecas externas e ao dispositivo físico.
