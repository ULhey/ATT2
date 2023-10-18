package br.edu.fateczl.AGISSpring.Controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.edu.fateczl.AGISSpring.Model.Aluno;
import br.edu.fateczl.AGISSpring.Model.Curso;
import br.edu.fateczl.AGISSpring.Persistence.AlunoDAO;
import br.edu.fateczl.AGISSpring.Persistence.CursoDAO;
import br.edu.fateczl.AGISSpring.Persistence.GenericDAO;

@Controller
public class AlunoController {

	@Autowired
	GenericDAO gdao;

	@Autowired
	AlunoDAO adao;

	@Autowired
	CursoDAO cdao;

	private List<Aluno> alunos = new ArrayList<>();
	private List<Curso> cursos = new ArrayList<>();
	
	@RequestMapping(name = "aluno", value = "/aluno", method = RequestMethod.GET)
	public ModelAndView alunoget(ModelMap model) {
		if (!cursos.isEmpty()) {
			cursos.removeAll(cursos);
		}
		if (!alunos.isEmpty()) {
			alunos.removeAll(alunos);
		}
		try {
			cursos.addAll(cdao.listar());
			alunos.addAll(adao.listar());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		model.addAttribute("cursos", cursos);
		return new ModelAndView("aluno");
	}

	@RequestMapping(name = "aluno", value = "/aluno", method = RequestMethod.POST)
	public ModelAndView alunopost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
		String RA = allRequestParam.get("RA");
		String CPF = allRequestParam.get("CPF");
		String nome = allRequestParam.get("nome");
		String nomeSocial = allRequestParam.get("nomeSocial");
		String dataNasc = allRequestParam.get("dataNasc");
		String telefone = allRequestParam.get("telefone");
		String email = allRequestParam.get("email");
		String dataMedio = allRequestParam.get("dataMedio");
		String instituicaoMedio = allRequestParam.get("instituicaoMedio");
		String pontVestibular = allRequestParam.get("pontVestibular");
		String posiVestibular = allRequestParam.get("posiVestibular");
		String turno = allRequestParam.get("turno");
		String idCurso = allRequestParam.get("idCurso");

		try {
			switch (allRequestParam.get("botao")) {
			case "Cadastrar":
				inserir(CPF, nome, nomeSocial, dataNasc, telefone, email, dataMedio, instituicaoMedio,
						Double.parseDouble(pontVestibular), Integer.parseInt(posiVestibular), turno,
						Integer.parseInt(idCurso));
				break;
			case "Buscar":
				model.addAttribute("aluno", buscar(RA));
				break;
			case "Alterar":
				atualizar(RA, CPF, nome, nomeSocial, dataNasc, telefone, email, dataMedio, instituicaoMedio,
						Double.parseDouble(pontVestibular), Integer.parseInt(posiVestibular), turno);
				break;
			case "Excluir":
				remover(RA);
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			model.addAttribute("cursos", cursos);
			model.addAttribute("alunos", alunos);
		}

		return new ModelAndView("aluno");
	}

	private void inserir(String CPF, String nome, String nomeSocial, String dataNasc, String telefone, String email,
			String dataMedio, String instituicaoMedio, double pontVestibular, int posiVestibular, String turno,
			int idCurso) throws ClassNotFoundException, SQLException {
		Aluno a = new Aluno();

		a.setRA(adao.getRA());
		a.setCPF(CPF);
		a.setNome(nome);
		a.setNomeSocial(nomeSocial);
		a.setDataNasc(dataNasc);
		a.setTelefone(telefone);
		a.setEmail(email);
		a.setDataMedio(dataMedio);
		a.setInstituicaoMedio(instituicaoMedio);
		a.setPontVestibular(pontVestibular);
		a.setPosiVestibular(posiVestibular);
		a.setTurno(turno);

		for (Curso c : cursos) {
			if (c.getIdCurso() == idCurso) {
				a.setCurso(c);
			}
		}

		if (adao.crudAluno("i", a)) {
			alunos.add(a);
		}
	}

	private String atualizar(String RA, String CPF, String nome, String nomeSocial, String dataNasc, String telefone,
			String email, String dataMedio, String instituicaoMedio, double pontVestibular, int posiVestibular,
			String turno) throws ClassNotFoundException, SQLException {
		for (Aluno a : alunos) {
			if (a.getRA().equals(RA)) {

				a.setNome(nome);
				a.setNomeSocial(nomeSocial);
				a.setDataNasc(dataNasc);
				a.setTelefone(telefone);
				a.setEmail(email);
				a.setDataMedio(dataMedio);
				a.setInstituicaoMedio(instituicaoMedio);
				a.setPontVestibular(pontVestibular);
				a.setPosiVestibular(posiVestibular);
				a.setTurno(instituicaoMedio);

				adao.crudAluno("u", a);
			}
		}
		return null;
	}

	public Aluno buscar(String RA) throws ClassNotFoundException, SQLException {
		for (Aluno a : alunos) {
			if (a.getRA().equals(RA)) {
				return adao.buscar(a);
			}
		}
		return null;
	}

	public String remover(String RA) throws ClassNotFoundException, SQLException {
		for (Aluno a : alunos) {
			if (a.getRA().equals(RA)) {
				alunos.remove(a);
				adao.crudAluno("d", a);
			}
		}
		return null;
	}
}