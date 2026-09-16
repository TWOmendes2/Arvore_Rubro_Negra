# Árvore rubro-negra

Estrutura de conjunto ordenado de inteiros em Java 21 com inserção, remoção e busca
O(log n), acompanhada de visualização Swing. Duplicatas são ignoradas e remover
valor ausente é uma operação sem efeito.

## Executar

```sh
javac -encoding UTF-8 -d out src/tree/domain/RedBlackTree.java RedBlackTreeVisualizer.java tests/TreeTest.java
java -cp out TreeTest
java -cp out RedBlackTreeVisualizer
```

## Arquitetura e evolução

`src/tree/domain` contém apenas a estrutura de dados; a UI consome um snapshot
imutável, sem acesso aos nós internos. Mantidas rotações e correções de inserção e
remoção do projeto original. A sentinela preta agora referencia a si mesma, evitando
acesso nulo em correções de remoção. Mensagens de interface foram profissionalizadas.

`validate()` verifica raiz/folhas pretas, ordenação, pais, ausência de vermelhos
consecutivos e altura preta uniforme. Testes comparam operações contra TreeSet,
incluindo extremos inteiros, duplicatas e esvaziamento. A UI ainda é uma visualização
didática: árvores grandes podem sobrepor nós. O histórico original permanece no Git.
