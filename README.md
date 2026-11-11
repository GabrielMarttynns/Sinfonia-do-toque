# 🎵 Sinfonia do Toque

## 📌 Sobre o Projeto

O **Sinfonia do Toque** é um jogo sério desenvolvido como projeto acadêmico, com foco no estímulo da coordenação motora fina, da movimentação individual dos dedos e da precisão dos movimentos por meio de desafios musicais.

Utilizando **Java, libGDX e Arduino**, o jogo transforma o acionamento de botões físicos em notas musicais. O jogador ajuda **Lumi**, um pequeno maestro, a recuperar as melodias de seu mundo, completando sequências de notas com os dedos.

## 🎯 Objetivo

Criar uma experiência gamificada de apoio a exercícios de destreza manual, incentivando movimentos repetidos e controlados dos dedos.

O jogo também busca registrar indicadores como acertos, tempo de resposta e regularidade dos movimentos para acompanhar a evolução do jogador ao longo das fases.

## 🕹️ Como Funciona o Jogo?

1. Uma nota musical é apresentada na tela.
2. O jogador identifica a nota e pressiona o botão físico correspondente com o dedo indicado.
3. O Arduino detecta o acionamento e envia a informação ao jogo.
4. O sistema verifica a resposta e apresenta um retorno visual e musical.
5. Os acertos, erros e tempos de resposta são registrados para compor a pontuação.
6. Conforme avança, o jogador precisa reproduzir sequências de notas e manter um ritmo regular.

O controle do jogo será realizado **exclusivamente pelos dispositivos físicos conectados ao Arduino**, sem depender de teclado ou mouse durante a partida.

## 🎹 Sistema de Notas e Controles

Cada nota corresponde a um botão físico:

- 🎵 **DÓ:** botão do dedo indicador.
- 🎵 **RÉ:** botão do dedo médio.
- 🎵 **MI:** botão do dedo anelar.

O projeto prevê três botões para as notas e um quarto botão para iniciar, pausar e reiniciar o jogo.

LEDs também poderão ser utilizados como retorno visual complementar.

## 🏆 Fases do Jogo

O jogo contará com três fases de dificuldade progressiva:

**Fase 1 – Primeiras Notas (Fácil):**

O jogador pressiona uma nota de cada vez, com maior tempo de resposta. O objetivo é estimular movimentos individuais dos dedos e a precisão.

**Fase 2 – Ritmo da Harmonia (Intermediária):**

O jogador reproduz pequenas sequências musicais alternando os dedos. O desafio estimula a coordenação, a precisão e a regularidade dos movimentos.

**Fase 3 – A Grande Melodia (Avançada):**

O jogador deve completar sequências mais longas, seguindo um ritmo definido e confortável. O desafio exige maior concentração e consistência.

A dificuldade poderá ser adaptada às necessidades e aos limites de cada jogador.

## 📊 Sistema de Pontuação e Avaliação

O desempenho será avaliado considerando:

- **Acertos:** quantidade de notas executadas corretamente.
- **Erros:** quantidade de notas pressionadas incorretamente.
- **Precisão:** porcentagem de respostas corretas.
- **Tempo de reação:** intervalo entre a apresentação da nota e o acionamento do botão.
- **Consistência:** regularidade dos movimentos durante as sequências.
- **Evolução:** comparação do desempenho entre as fases e sessões.

A pontuação priorizará a execução correta e regular dos movimentos, evitando valorizar apenas a velocidade.

## ⚙️ Tecnologias Utilizadas

- **Java:** linguagem de programação utilizada no desenvolvimento do jogo.
- **libGDX:** framework responsável pela interface gráfica e pelas mecânicas.
- **Arduino:** responsável pela leitura dos botões físicos.
- **Comunicação serial:** transmissão dos dados do Arduino para o jogo.
- **Botões físicos:** dispositivos utilizados para os exercícios dos dedos.
- **Git e GitHub:** armazenamento e controle de versão do projeto.

## 🎓 Finalidade Acadêmica

O **Sinfonia do Toque** faz parte de um projeto acadêmico que busca demonstrar como a tecnologia e a gamificação podem ser utilizadas como ferramentas de apoio à reabilitação física.

O jogo tem como proposta estimular a coordenação motora fina, a movimentação individual dos dedos, a precisão e a regularidade dos movimentos.

Por meio da integração com o Arduino, será possível monitorar o desempenho do jogador e acompanhar sua evolução durante os exercícios.

A proposta busca tornar as atividades motoras mais interativas e motivadoras, utilizando a música como elemento de incentivo.

O jogo não substitui o acompanhamento de profissionais de saúde, e os exercícios devem respeitar as capacidades e limitações de cada usuário.
