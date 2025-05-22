package br.com.alura.bytebank;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Java Database Connectivity ou JDBC é um conjunto de classes e interfaces (API) escritas em Java que fazem o envio de instruções SQL para qualquer banco de dados relacional

public class ConnectionFactory {
    public Connection recuperarConexao(){
        try{
            return createDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //sem a necessidade de diversas conexoes e fechamento - POOL DE CONEXOES
    //Reciclar um conjunto de conexões de tamanho fixo ou dinâmico.
    //Essa é a abordagem do pool de conexão. Vamos abrir uma quantidade de conexões e reaproveitá-las.
    private HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/byte_bank");
        config.setUsername("root");
        config.setPassword("admin");
        config.setMaximumPoolSize(10);

        return new HikariDataSource(config);
    }

}
