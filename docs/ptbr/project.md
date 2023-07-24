## Projetos no Pacqit
No Pacqit há algumas peculiaridades relacionadas a organização de projetos e módulos,
adicionando mais pastas que o comum num projeto java convencional, mas calma tudo vai 
se encaixar e em breve você entenderá o uso de cada uma dessas pastas.

### Módulos
No Pacqit módulos se referem a mini-projetos que dependem de um projeto principal, formando
uma árvore de projetos e módulos.

Exemplificando: digamos que seu mais novo projeto seja um gerenciador de arquivos multiplataforma,
cada plataforma tem suas caracteristicas, portanto para organizar melhor você decide criar alguns
módulos, um para conter todas as abstrações e os outros para serem implementações de cada
plataforma, resultando assim em um módulo common e outros três sendo implementações Windows, Linux e MacOS.
Seguindo a estruturação adequada de projetos Pacqit o Common seria um módulo do projeto principal 
e as implementações seriam módulos do Common.
```
ProjetoExploradoDeArquivos:

Common (módulo do Explorador de Arquivos)
- Windows (módulo do Common)
-- src
-- module-config.yml

-- MacOS (módulo do Common)
-- src
-- module-config.yml

- Linux (módulo do Common)
-- src
-- module-config.yml

- src
- module-config.yml

src
project-config.yml
```

Um módulo não tem acesso a outro módulo parente, isso significa que o Windows não tem acesso 
ao módulo Linux e vice versa, apenas o Common e o projeto principal tem. Portanto se fosse criado 
um possível módulo GUI ele não teria acesso ao seu parente Common nem aos filhos do módulo parente.

Os módulos herdam as dependências e configurações de seus pais, portanto dentro dos módulos só é 
possível determinar novas dependências, um nome e uma versão, o resto tudo é definido pelo seus pais.

### Implementações (alpha)
No Pacqit módulos implementadores (ou módulos de implementação) são praticamente módulos independentes
que fazem parte de um mesmo projeto, ou seja, apesar dele fazer parte de um projeto existente tudo nele pode ser personalizado,
desde de compilação até dependências.

Isso significa que cada módulo implementador irá gerar seu próprio output, ou seja cada módulo irá gerar seu próprio JAR
independente dos outros.

Isso porque na verdade ele não é um módulo do projeto principal e sim, um projeto dentro do projeto principal.

Exemplificando:
Digamos que você esteja criando um jogo e quer que ele seja portado para vários dispositivos, portanto
você cria duas implementações, a desktop (para portar o jogo para computadores) e a android (para portar o jogo para android).
Mas obviamente você não quer que toda lógica básica tenha que ser refeita em cada implementação,
portanto você cria um módulo Common e cria toda lógica básica lá. No projeto principal você desativa 
o Project Source, porque você não pretende criar nada dentro do projeto (afinal os implementadores já estão ai para isto).

```
ProjetoJogo:

Common (módulo de Jogo)
- src
- module-config.yml

Desktop (módulo implementador de Jogo)
- src
- implementer-config.yml

Android (módulo implementador de Jogo)
- src
- implementer-config.yml

project-config.yml
```

Os módulos implementadores não tem acesso a outros módulos implementadores parente, porém
eles tem total acesso a módulos parentes normais, isso significa que o Desktop tem total acesso
ao módulo Common mas não ao módulo Android e o mais importante, os módulos implementadores não só
tem acesso a módulos parentes, como também os considera "irmãos" agregando seu código a si próprio.
Ou seja, o Common tem total acesso aos módulos implementadores parente, pois ele considera seu próprio código.

Para o projeto principal o módulo implementador é só mais um módulo filho, ou seja, ele segue as regras
dele e ignora completamente as configurações personalizadas do implementador;