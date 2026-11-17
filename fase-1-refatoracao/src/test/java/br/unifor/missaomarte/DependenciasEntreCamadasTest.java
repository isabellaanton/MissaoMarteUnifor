package br.unifor.missaomarte;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste de arquitetura sem biblioteca extra: lê o bytecode compilado de cada camada e
 * falha se encontrar uma dependência proibida. Responde, de forma executável, à pergunta
 * "como os testes demonstram que as regras não dependem da interface?".
 *
 * <p>Os nomes de classe aparecem literalmente na tabela de constantes do {@code .class};
 * se o código referenciar {@code Scanner}, {@code System.out} ou uma classe de outra
 * camada, o nome estará lá.</p>
 */
class DependenciasEntreCamadasTest {
    private static final String BASE = "br/unifor/missaomarte/";

    private static final String[] ENTRADA_E_SAIDA = {
            "java/util/Scanner", "java/lang/System", "java/io/", "java/nio/file/"
    };

    private static List<Path> classesDe(String camada) throws IOException, URISyntaxException {
        Path raiz = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path pasta = raiz.resolve(BASE + camada);
        try (Stream<Path> arquivos = Files.list(pasta)) {
            return arquivos.filter(p -> p.toString().endsWith(".class")).toList();
        }
    }

    private static List<String> violacoes(String camada, String... proibidos) throws IOException, URISyntaxException {
        List<String> encontradas = new ArrayList<>();
        for (Path classe : classesDe(camada)) {
            String conteudo = new String(Files.readAllBytes(classe), StandardCharsets.ISO_8859_1);
            for (String proibido : proibidos) {
                if (conteudo.contains(proibido)) {
                    encontradas.add(classe.getFileName() + " referencia " + proibido);
                }
            }
        }
        return encontradas;
    }

    private static String[] somar(String[] a, String... b) {
        String[] r = new String[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    @Test
    @DisplayName("domínio: sem Scanner, System, java.io, java.nio.file nem classes de outras camadas")
    void dominioNaoDependeDeEntradaSaidaNemDeOutrasCamadas() throws Exception {
        assertTrue(classesDe("dominio").size() >= 15, "esperava encontrar as classes do domínio compiladas");

        String[] proibidos = somar(ENTRADA_E_SAIDA,
                BASE + "aplicacao/", BASE + "apresentacao/", BASE + "entrada/", BASE + "infraestrutura/", BASE + "Main");

        List<String> violacoes = violacoes("dominio", proibidos);
        assertTrue(violacoes.isEmpty(), "dependências proibidas no domínio: " + violacoes);
    }

    @Test
    @DisplayName("aplicação: sem Scanner, System, java.io e sem depender dos adaptadores concretos")
    void aplicacaoSoConheceInterfaces() throws Exception {
        assertTrue(classesDe("aplicacao").size() >= 8, "esperava encontrar as classes da aplicação compiladas");

        String[] proibidos = somar(ENTRADA_E_SAIDA,
                BASE + "apresentacao/", BASE + "entrada/", BASE + "infraestrutura/", BASE + "Main");

        List<String> violacoes = violacoes("aplicacao", proibidos);
        assertTrue(violacoes.isEmpty(), "dependências proibidas na aplicação: " + violacoes);
    }

    @Test
    @DisplayName("a camada de entrada não depende da apresentação nem do ranking em disco")
    void entradaIsolada() throws Exception {
        assertTrue(classesDe("entrada").size() >= 2);
        List<String> violacoes = violacoes("entrada", BASE + "apresentacao/", BASE + "infraestrutura/");
        assertTrue(violacoes.isEmpty(), "dependências proibidas na entrada: " + violacoes);
    }
}
