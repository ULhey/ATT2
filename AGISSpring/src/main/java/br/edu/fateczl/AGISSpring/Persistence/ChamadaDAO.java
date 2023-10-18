package br.edu.fateczl.AGISSpring.Persistence;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.edu.fateczl.AGISSpring.Model.Aluno;
import br.edu.fateczl.AGISSpring.Model.Chamada;
import br.edu.fateczl.AGISSpring.Model.Curso;
import br.edu.fateczl.AGISSpring.Model.Disciplina;

@Repository
public class ChamadaDAO {
	private Connection c;
	
	private GenericDAO gdao;
	
	public ChamadaDAO(GenericDAO g) throws ClassNotFoundException, SQLException {
		gdao = g;
	}
	
	public boolean crudChamada(String acao, Chamada ch) throws SQLException, ClassNotFoundException {
		c = gdao.conexao();
		
		String sql = "{CALL crudCHAMADA (?,?,?,?,?,?)}";
		CallableStatement cs = c.prepareCall(sql);
		cs.setString(1, acao);
		cs.setInt(2, ch.getIdChamada());
		cs.setInt(3, ch.getFaltas());
		cs.setInt(4, ch.getDisciplina().getIdDisciplina());
		cs.setString(5, ch.getAluno().getRA());;
		cs.registerOutParameter(6, Types.BIT);
		cs.execute();
		boolean validade = cs.getBoolean(6);
		cs.close();
		c.close();

		return validade;
	}

	public List<Chamada> buscaChamada(Disciplina dd, String data) throws SQLException, ClassNotFoundException {
		c = gdao.conexao();

		String sql = "SELECT * FROM Chamada WHERE idDisciplina = ? AND dataChamada = ?";

		List<Chamada> lista = new ArrayList<>();

		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, dd.getIdDisciplina());
		ps.setString(2, data);

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Chamada ch = new Chamada();

	        ch.setFaltas(rs.getInt("faltas"));

			Aluno a = new Aluno();

			a.setRA(rs.getString("RA"));
			a.setCPF(rs.getString("CPF"));
			a.setNome(rs.getString("nome"));
			a.setNomeSocial(rs.getString("nomeSocial"));
			a.setDataNasc(rs.getString("dataNasc"));
			a.setTelefone(rs.getString("telefone"));
			a.setEmail(rs.getString("email"));
			a.setEmailCorp(rs.getString("emailCorp"));
			a.setDataMedio(rs.getString("dataMedio"));
			a.setInstituicaoMedio(rs.getString("instituicaoMedio"));
			a.setPontVestibular(rs.getInt("pontVestibular"));
			a.setPosiVestibular(rs.getInt("posiVestibular"));
			a.setAnoInicio(rs.getInt("anoInicio"));
			a.setSemesInicio(rs.getInt("semesInicio"));
			a.setSemesConclusao(rs.getInt("semesConclusao"));
			a.setAnoLimite(rs.getInt("anoLimite"));

			ch.setAluno(a);

			Curso c = new Curso();

			c.setIdCurso(rs.getInt("idCurso"));
			c.setNomeCurso(rs.getString("nomeCurso"));
			c.setCargaHorario(rs.getString("cargaHorario"));
			c.setSigla(rs.getString("sigla"));
			c.setENADE(rs.getFloat("ENADE"));

			a.setCurso(c);

			Disciplina d = new Disciplina();

			d.setIdDisciplina(rs.getInt("idDisciplina"));
			d.setNomeDisciplina(rs.getString("nomeDisciplina"));
			d.setHorarioSemanal(rs.getString("horarioSemanal"));
			d.setAula(rs.getInt("aula"));
			d.setTipoConteudo(rs.getString("tipoConteudo"));
			d.setSemestreDisciplina(rs.getInt("semestreDisciplina"));
			d.setProfessor(rs.getString("Professor"));

			ch.setDisciplina(d);

			d.setCurso(c);

			lista.add(ch);
		}

		rs.close();
		ps.close();
		c.close();
		return lista;
	}
	
	public List<Chamada> geraChamada(Disciplina d) throws ClassNotFoundException, SQLException {
		c = gdao.conexao();
		
		Date data = new Date(System.currentTimeMillis());

	    String sql = "SELECT * FROM GerarListaChamada(?, ?)";

	    List<Chamada> lista = new ArrayList<>();

	    PreparedStatement ps = c.prepareStatement(sql);
	    ps.setInt(1, d.getIdDisciplina());;
	    ps.setDate(2, data);

	    ResultSet rs = ps.executeQuery();

	    while (rs.next()) {
	        Chamada c = new Chamada();
	        
	        Aluno a = new Aluno();
	        
	        a.setRA(rs.getString("RAAluno"));
	        a.setNome(rs.getString("NomeAluno"));
	        
	        Disciplina dd = new Disciplina(); 
	        dd.setNomeDisciplina(rs.getString("NomeDisciplina"));
	        
	        c.setAluno(a);
	        c.setDisciplina(dd);
	        
	        lista.add(c);
	    }

	    rs.close();
	    ps.close();

	    c.close();

	    return lista;
	}
}
