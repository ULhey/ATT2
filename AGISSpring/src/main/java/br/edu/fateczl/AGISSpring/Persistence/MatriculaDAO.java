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

import br.edu.fateczl.AGISSpring.Model.Aluno;
import br.edu.fateczl.AGISSpring.Model.Curso;
import br.edu.fateczl.AGISSpring.Model.Disciplina;
import br.edu.fateczl.AGISSpring.Model.Matricula;

@Repository
public class MatriculaDAO {
	private Connection c;
	
	GenericDAO gdao;

	public MatriculaDAO(GenericDAO g) throws ClassNotFoundException, SQLException {
		gdao = g;
	}

	public void crudMATRICULA(String acao, Matricula m) throws SQLException, ClassNotFoundException {
		c = gdao.conexao();

		String sql = "{CALL crudMATRICULA (?,?,?,?,?,?)}";
		CallableStatement cs = c.prepareCall(sql);
		cs.setString(1, acao);
		cs.setInt(2, m.getIdMatricula());
		cs.setString(3, m.getAluno().getRA());
		cs.setString(4, m.getSituacao());
		cs.setInt(5, m.getDisciplina().getIdDisciplina());
		cs.registerOutParameter(6, Types.VARCHAR);
		cs.execute();
		cs.close();
		c.close();
	}

	public List<Disciplina> buscaDisciplinas(Aluno a) throws SQLException, ClassNotFoundException {
		c = gdao.conexao();

		String sql = "SELECT * FROM MatriculaDisciplina WHERE ra = ?";

		List<Disciplina> disciplinas = new ArrayList<>();

		PreparedStatement ps = c.prepareStatement(sql);
		ps.setString(1, a.getRA());

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Disciplina d = new Disciplina();

			d.setIdDisciplina(rs.getInt("idDisciplina"));
			d.setNomeDisciplina(rs.getString("nomeDisciplina"));
			d.setHorarioSemanal(rs.getString("horarioSemanal"));
			d.setAula(rs.getInt("aula"));
			d.setTipoConteudo(rs.getString("tipoConteudo"));
			d.setSemestreDisciplina(rs.getInt("Semestre"));
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
	
	public List<Disciplina> listarDisciplinasNmatriculadas(Aluno a) throws SQLException, ClassNotFoundException {
		c = gdao.conexao();
		
		String sql = "SELECT "
				+ "	d.idDisciplina AS idDisciplina, d.nomeDisciplina AS nomeDisciplina, d.horarioSemanal AS horarioSemanal, d.aula AS aula, d.tipoConteudo AS tipoConteudo, d.semestreDisciplina AS semestreDisciplina, d.nomeProfessor AS Professor, "
				+ "	c.idCurso AS idCurso , c.nomeCurso AS nomeCurso, c.cargaHorario AS cargaHorario, c.sigla AS sigla, c.ENADE AS ENADE "
				+ "FROM Disciplina D "
				+ "	INNER JOIN Curso C ON C.idCurso = D.idCurso "
				+ "	LEFT JOIN MatriculaAlunoDisciplina MAD ON MAD.idDisciplina = D.idDisciplina AND MAD.RA = ? "
				+ "WHERE MAD.RA IS NULL";

		List<Disciplina> disciplinas = new ArrayList<>();

		PreparedStatement ps = c.prepareStatement(sql);
		ps.setString(1, a.getRA());
		
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

	public List<Matricula> buscaMatriculas(Aluno aa) throws SQLException, ClassNotFoundException {
		c = gdao.conexao();

		String sql = "SELECT * FROM Matricula WHERE ra = ?";

		List<Matricula> matriculas = new ArrayList<>();

		PreparedStatement ps = c.prepareStatement(sql);
		ps.setString(1, aa.getRA());

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Matricula m = new Matricula();

			m.setIdMatricula(rs.getInt("idMatricula"));
			m.setAno(rs.getInt("ano"));
			m.setSemestreMatricula(rs.getInt("semestreMatricula"));
			m.setSituacao(rs.getString("situacao"));

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
			a.setTurno(rs.getString("turno"));

			m.setAluno(a);

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

			m.setDisciplina(d);

			d.setCurso(c);

			matriculas.add(m);
		}

		rs.close();
		ps.close();
		c.close();
		return matriculas;
	}
}