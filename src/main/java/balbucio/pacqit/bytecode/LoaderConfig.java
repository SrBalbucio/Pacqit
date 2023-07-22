package balbucio.pacqit.bytecode;

import balbucio.pacqit.Main;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.File;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoaderConfig {

    /**
     * Para projetos menores ou computadores com muita RAM é útil carregar todas as classes de uma vez, evita recursividade
     * e gasto de processamento. Para projetos muito grandes onde o computador tem recursos limitados pode se fazer necessário
     * o processamento classe por classe ao custo de maior tempo de processamento.
     */
    public boolean LOAD_ALL_CLASSES = true;

    /**
     * Deixe ativo para encontrar bugs ou verificar o processo de build.
     */
    public boolean VERBOSE = false;

    public File LOG_PATH = new File("build-logs");
    public boolean GUI = false;
    public Main app;
}
