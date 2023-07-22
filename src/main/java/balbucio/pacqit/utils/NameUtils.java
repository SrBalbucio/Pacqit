package balbucio.pacqit.utils;

import java.util.Random;

public class NameUtils {

    private static final String CARACTERES_VALIDOS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateJavaValidName() {
        Random random = new Random();
        int tamanho = 8 + random.nextInt(24);
        StringBuilder nomeAleatorio = new StringBuilder();

        for (int i = 0; i < tamanho; i++) {
            int indice = random.nextInt(CARACTERES_VALIDOS.length());
            char caractere = CARACTERES_VALIDOS.charAt(indice);
            nomeAleatorio.append(caractere);
        }

        return nomeAleatorio.toString();
    }
}
