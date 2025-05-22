package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;
import br.com.alura.bytebank.domain.cliente.Cliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

public class ContaService {

    private ConnectionFactory connection;

    /*private Set<Conta> contas = new HashSet<>();*/

    public ContaService(){
        this.connection = new ConnectionFactory();
    }

    public Set<Conta> listarContasAbertas() {
        Connection conn = connection.recuperarConexao();
        return new ContaDAO(conn).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public void abrir(DadosAberturaConta dadosDaConta) {
        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).salvar(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        /*Conta class*/ var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }

        if(!conta.isEstaAtiva()){
            throw new RegraDeNegocioException("Conta não está ativa");
        }

        BigDecimal novoValor = conta.getSaldo().subtract(valor);

        /*Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).alterar(novoValor, conta.getNumero());*/
        alterarDadosSaldoConta(novoValor, conta);

    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }

        if(!conta.isEstaAtiva()){
            throw new RegraDeNegocioException("Conta não está ativa");
        }

        /*Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).alterar(valor, conta.getNumero());*/

        BigDecimal novoValor = conta.getSaldo().add(valor);
        alterarDadosSaldoConta(novoValor, conta);
    }

    //TRANSFERENCIA
    public void realizarTransferencia (Integer numeroDaContaDestino, Integer numeroDaContaOrigem, BigDecimal valor){


        this.realizarSaque(numeroDaContaOrigem, valor);
        this.realizarSaque(numeroDaContaDestino, valor);
    }

    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).deletar(numeroDaConta);
    }

    public void encerraLogico(Integer numeroDaConta){
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).alterarLogico(numeroDaConta);
    }

    private Conta buscarContaPorNumero(Integer numero) {
        Connection conn = connection.recuperarConexao();
        Conta conta = new ContaDAO(conn).listarPorNumero(numero);
        if(conta != null) {
            return conta;
        } else {
            throw new RegraDeNegocioException("Não existe conta cadastrada com esse número!");
        }
    }

    private void alterarDadosSaldoConta(BigDecimal valor, Conta conta){
        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).alterar(valor, conta.getNumero());
    }

}
