package br.unifor.missaomarte.infraestrutura;

import br.unifor.missaomarte.aplicacao.FalhaDePersistencia;
import br.unifor.missaomarte.aplicacao.RepositorioRanking;
import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.EntradaRanking;
import br.unifor.missaomarte.dominio.Ranking;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistência do ranking em {@code ranking.json}. O formato do arquivo e o leitor
 * manual de JSON são os da versão original, apenas movidos para cá: arquivos antigos
 * continuam compatíveis.
 *
 * <p><b>Limitação conhecida (herdada):</b> o leitor divide o texto por vírgula e por
 * {@code ':'} e corta o objeto no primeiro {@code '}'}; nomes de piloto contendo
 * vírgula, dois-pontos ou chaves não sobrevivem à leitura, e barras invertidas não são
 * escapadas ao gravar. Trocar por uma biblioteca JSON é a correção natural na fase do
 * servidor.</p>
 */
public class RankingJsonRepository implements RepositorioRanking {
    private final Path caminho;

    public RankingJsonRepository(Path caminho) {
        this.caminho = caminho;
    }

    @Override
    public Ranking carregar() {
        if (!Files.exists(caminho)) {
            return Ranking.vazio();
        }
        try {
            String json = new String(Files.readAllBytes(caminho), StandardCharsets.UTF_8).trim();
            return Ranking.ordenado(interpretar(json));
        } catch (IOException e) {
            return Ranking.vazio();
        }
    }

    @Override
    public void salvar(Ranking ranking) {
        try {
            Files.write(caminho, serializar(ranking).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new FalhaDePersistencia(e.getMessage(), e);
        }
    }

    @Override
    public void limpar() {
        try {
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            throw new FalhaDePersistencia(e.getMessage(), e);
        }
    }

    static String serializar(Ranking ranking) {
        List<EntradaRanking> entradas = ranking.getEntradas();
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < entradas.size(); i++) {
            EntradaRanking entry = entradas.get(i);
            builder.append("{\"name\":\"")
                    .append(entry.nome().replace("\"", "\\\""))
                    .append("\",\"score\":")
                    .append(entry.pontuacao())
                    .append(",\"dificuldade\":\"")
                    .append(entry.dificuldade().name())
                    .append("\",\"passageirosColetados\":")
                    .append(entry.passageirosColetados())
                    .append(",\"dataHora\":\"")
                    .append(entry.dataHora())
                    .append("\",\"tempoJogo\":")
                    .append(entry.tempoJogo())
                    .append("}");
            if (i < entradas.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    static List<EntradaRanking> interpretar(String json) {
        List<EntradaRanking> ranking = new ArrayList<>();
        if (json.isEmpty() || json.equals("[]")) {
            return ranking;
        }
        json = json.trim();
        if (json.startsWith("[")) {
            json = json.substring(1);
        }
        if (json.endsWith("]")) {
            json = json.substring(0, json.length() - 1);
        }

        int index = 0;
        while (index < json.length()) {
            int start = json.indexOf('{', index);
            if (start < 0) break;
            int end = json.indexOf('}', start);
            if (end < 0) break;
            String object = json.substring(start + 1, end);
            String name = null;
            Integer score = null;
            Dificuldade dificuldade = Dificuldade.MEDIO;
            int passageirosColetados = 0;
            String dataHora = "";
            long tempoJogo = 0;

            for (String part : object.split(",")) {
                String[] pair = part.split(":", 2);
                if (pair.length != 2) continue;
                String key = pair[0].trim().replaceAll("\"", "");
                String value = pair[1].trim();

                if (key.equals("name")) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        name = value.substring(1, value.length() - 1).replace("\\\"", "\"");
                    }
                } else if (key.equals("score")) {
                    try {
                        score = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                        // campo inválido: a entrada será descartada por não ter pontuação
                    }
                } else if (key.equals("dificuldade")) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        dificuldade = Dificuldade.deString(value.substring(1, value.length() - 1));
                    }
                } else if (key.equals("passageirosColetados")) {
                    try {
                        passageirosColetados = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                        // mantém o valor padrão (0)
                    }
                } else if (key.equals("dataHora")) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        dataHora = value.substring(1, value.length() - 1);
                    }
                } else if (key.equals("tempoJogo")) {
                    try {
                        tempoJogo = Long.parseLong(value);
                    } catch (NumberFormatException ignored) {
                        // mantém o valor padrão (0)
                    }
                }
            }
            if (name != null && score != null) {
                ranking.add(new EntradaRanking(name, score, dificuldade, passageirosColetados, dataHora, tempoJogo));
            }
            index = end + 1;
        }
        return ranking;
    }
}
