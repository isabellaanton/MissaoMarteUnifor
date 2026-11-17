package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.EntradaRanking;
import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.suporte.Cenarios;
import br.unifor.missaomarte.suporte.EntradaRoteirizada;
import br.unifor.missaomarte.suporte.RegistroDeApresentacao;
import br.unifor.missaomarte.suporte.RepositorioEmMemoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicoDeRankingTest {
    private final RegistroDeApresentacao tela = new RegistroDeApresentacao();

    private static Ranking rankingCom(int... pontos) {
        Ranking r = Ranking.vazio();
        for (int i = 0; i < pontos.length; i++) r = r.comNovaEntrada(Cenarios.entrada("J" + i, pontos[i]));
        return r;
    }

    private ServicoDeRanking servico(RepositorioEmMemoria repo, String... respostas) {
        return new ServicoDeRanking(new EntradaRoteirizada(respostas), tela, repo);
    }

    @Test
    @DisplayName("resetar com \"s\" apaga o histórico e devolve ranking vazio")
    void resetarConfirmando() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(rankingCom(50));

        Ranking resultado = servico(repo, "s").resetar();

        assertTrue(resultado.estaVazio());
        assertEquals(1, repo.vezesQueLimpou);
        assertEquals(List.of("rankingResetado"), tela.chamadas);
    }

    @Test
    @DisplayName("\"sim\" e maiúsculas também confirmam")
    void confirmacoesAceitas() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(rankingCom(50));
        servico(repo, "  SIM ").resetar();
        assertEquals(1, repo.vezesQueLimpou);
    }

    @Test
    @DisplayName("qualquer outra resposta cancela e mantém o ranking salvo")
    void resetarCancelando() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(rankingCom(50));

        Ranking resultado = servico(repo, "n").resetar();

        assertEquals(1, resultado.getEntradas().size());
        assertEquals(0, repo.vezesQueLimpou);
        assertEquals(List.of("resetCancelado"), tela.chamadas);
    }

    @Test
    @DisplayName("sem resposta (fim da entrada) o reset é cancelado")
    void resetarSemEntrada() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(rankingCom(50));
        servico(repo).resetar();
        assertEquals(0, repo.vezesQueLimpou);
        assertEquals(List.of("resetCancelado"), tela.chamadas);
    }

    @Test
    @DisplayName("se apagar falhar, mostra o erro e mesmo assim devolve ranking vazio (como o original)")
    void resetarComFalha() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(rankingCom(50));
        repo.falharAoLimparCom = "sem permissão";

        Ranking resultado = servico(repo, "s").resetar();

        assertTrue(resultado.estaVazio());
        assertEquals(List.of("erroAoResetar:sem permissão"), tela.chamadas);
    }

    @Test
    @DisplayName("pontuação que entra no Top 5 é gravada e o jogador é parabenizado")
    void registraQuemQualifica() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(Ranking.vazio());
        EntradaRanking nova = Cenarios.entrada("Ana", 74);

        servico(repo).registrarSeQualificar(Ranking.vazio(), nova);

        assertEquals(1, repo.vezesQueSalvou);
        assertEquals(List.of(nova), repo.guardado.getEntradas());
        assertEquals(List.of("novoTop5"), tela.chamadas);
    }

    @Test
    @DisplayName("pontuação que não supera o último colocado não é gravada")
    void ignoraQuemNaoQualifica() {
        Ranking cheio = rankingCom(100, 90, 80, 70, 60);
        RepositorioEmMemoria repo = new RepositorioEmMemoria(cheio);

        servico(repo).registrarSeQualificar(cheio, Cenarios.entrada("Ana", 60));

        assertEquals(0, repo.vezesQueSalvou);
        assertTrue(tela.chamadas.isEmpty());
    }

    @Test
    @DisplayName("pontuação zero ou negativa nunca entra no ranking")
    void ignoraPontuacaoNaoPositiva() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(Ranking.vazio());
        servico(repo).registrarSeQualificar(Ranking.vazio(), Cenarios.entrada("Ana", 0));
        assertEquals(0, repo.vezesQueSalvou);
        assertTrue(tela.chamadas.isEmpty());
    }

    @Test
    @DisplayName("falha ao gravar mostra o erro e ainda parabeniza (comportamento do original)")
    void registrarComFalhaDeGravacao() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(Ranking.vazio());
        repo.falharAoSalvarCom = "disco cheio";

        servico(repo).registrarSeQualificar(Ranking.vazio(), Cenarios.entrada("Ana", 74));

        assertEquals(List.of("erroAoSalvar:disco cheio", "novoTop5"), tela.chamadas);
    }

    @Test
    @DisplayName("o ranking gravado mantém só os 5 melhores")
    void gravaApenasOsCincoMelhores() {
        Ranking cheio = rankingCom(100, 90, 80, 70, 60);
        RepositorioEmMemoria repo = new RepositorioEmMemoria(cheio);

        servico(repo).registrarSeQualificar(cheio, Cenarios.entrada("Ana", 85));

        assertEquals(5, repo.guardado.getEntradas().size());
        assertEquals(60, cheio.getEntradas().get(4).pontuacao());
        assertEquals(70, repo.guardado.getEntradas().get(4).pontuacao());
    }
}
