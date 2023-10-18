package br.edu.fateczl.AGISSpring.Persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.edu.fateczl.AGISSpring.Model.Aluno;
import br.edu.fateczl.AGISSpring.Model.Historico;

@Repository
public class HistoricoDAO {
	private Connection c;
	
	GenericDAO gdao;

	public HistoricoDAO(GenericDAO g) throws ClassNotFoundException, SQLException {
		gdao = g;
	}

	public List<Historico> geraHistorico(Aluno a) throws ClassNotFoundException, SQLException {
		c = gdao.conexao();

	    String sql = "SELECT * FROM HistoricoAluno(?)";

	    List<Historico> lista = new ArrayList<>();

	    PreparedStatement ps = c.prepareStatement(sql);
	    ps.setString(1, a.getRA());

	    ResultSet rs = ps.executeQuery();

	    while (rs.next()) {
	        Historico h = new Historico();
	        
	        h.setIdDisciplina(rs.getInt("IdDisciplina"));
	        h.setNomeDisciplina(rs.getString("NomeDisciplina"));
	        h.setNomeProfessor(rs.getString("NomeProfessor"));
	        h.setNotaFinal(rs.getString("NotaFinal"));
	        h.setQuantidadeFaltas(rs.getInt("QuantidadeFaltas"));
	        lista.add(h);
	    }

	    rs.close();
	    ps.close();

	    c.close();

	    return lista;
	}
}