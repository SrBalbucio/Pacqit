package balbucio.pacqit.bytecode;

public class LoaderConfig {

    /**
     * Para projetos menores ou computadores com muita RAM é útil carregar todas as classes de uma vez, evita recursividade
     * e gasto de processamento. Para projetos muito grandes onde o computador tem recursos limitados pode se fazer necessário
     * o processamento classe por classe ao custo de maior tempo de processamento.
     */
    public boolean LOAD_ALL_CLASSES = true;
}
