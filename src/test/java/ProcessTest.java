import lombok.SneakyThrows;

public class ProcessTest {

    @SneakyThrows
    public static void main(String[] args) {
        ProcessBuilder builder = new ProcessBuilder(System.getProperty("java.home")+"/bin/javac.exe -d classes -target 20 org/example/Main.java");
        builder.start();
    }
}
