package br.edu.fateczl.AGISSpring.Persistence;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.edu.fateczl.AGISSpring.Model.Curso;
import br.edu.fateczl.AGISSpring.Model.Disciplina;

@Repository
public class DisciplinaDAO {
	private Connection c;
	
	GenericDAO gdao;

	public DisciplinaDAO(GenericDAO g) throws ClassNotFoundException, SQLException {
		gdao = g;
	}

	public void crudDisciplina(String acao, Disciplina d) throws SQLException, ClassNotFoundException {
		c = gdao.conexao();
		
		String sql = "{CALL crudDISCIPLINA (?,?,?,?,?,?,?,?,?,?)}";
		CallableStatement cs = c.prepareCall(sql);
		cs.setString(1, acao);
		cs.setInt(2, d.getIdDisciplina());
		cs.setString(3, d.getNomeDisciplina());
		cs.setString(4, d.getHorarioSemanal());
		cs.setInt(5, d.getAula());
		cs.setString(6, d.getTipoConteudo());
		cs.setString(7, d.getProfessor());
		cs.setInt(8, d.getSemestreDisciplina());
		cs.setInt(9, d.getCurso().getIdCurso());
		cs.registerOutParameter(10, Types.VARCHAR);
		cs.execute();
		cs.close();
		c.close();
	}

	public Disciplina buscar(Disciplina d) throws SQLException, ClassNotFoundException {
		c = gdao.conexao();
		
		String sql = "SELECT * FROM CursoDisciplina WHERE idDisciplina = ?";

		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, d.getIdDisciplina());

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			d.setIdDisciplina(rs.getInt("idDisciplina"));
			d.setNomeDisciplina(rs.getString("nomeDisciplina"));
			d.setHorarioSemanal(rs.getString("horarioSemanal"));
			d.setAula(rs.getInt("aula"));
			d.setTipoConteudo(rs.getString("tipoConteudo"));
			d.setSemestreDisciplina(rs.getInt("semestreDisciplina"));
			d.setProfessor(rs.getString("professor"));

			Curso c = new Curso();

			c.setIdCurso(rs.getInt("idCurso"));
			c.setNomeCurso(rs.getString("nomeCurso"));
			c.setCargaHorario(rs.getString("cargaHorario"));
			c.setSigla(rs.getString("sigla"));
			c.setENADE(rs.getFloat("ENADE"));

			d.setCurso(c);
		}

		rs.close();
		ps.close();
		c.close();

		return d;
	}

	public List<Disciplina> listar() throws SQLException, ClassNotFoundException {
		c = gdao.conexao();
		
		String sql = "SELECT * FROM CursoDisciplina";

		List<Disciplina> disciplinas = new ArrayList<>();

		PreparedStatement ps = c.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Disciplina d = new Disciplina();

			d.setIdDisciplina(rs.getInt("idDisciplina"));
			d.setNomeDisciplina(rs.getString("nomeDisciplina"));
			d.setHorarioSemanal(rs.getString("horarioSemanal"));
			d.setAula(rs.getInt("aula"));
			d.setTipoConteudo(rs.getString("tipoConteudo"));
			d.setSemestreDisciplina(rs.getInt("semestreDisciplina"));
			d.setProfessor(rs.getString("Professor"));

			Curso c = new Curso();

			c.setIdCurso(rs.getInt("idCurso"));
			c.setNomeCurso(rs.getString("nomeCurso"));
			c.setCargaHorario(rs.getString("cargaHorario"));
			c.setSigla(rs.getString("sigla"));
			c.setENADE(rs.getFloat("ENADE"));

			d.setCurso(c);

			disciplinas.add(d);
		}

		rs.close();
		ps.close();
		c.close();

		return disciplinas;
	}
	

}