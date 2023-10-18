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
import br.edu.fateczl.AGISSpring.Model.Disciplina;
import br.edu.fateczl.AGISSpring.Model.Matricula;
import br.edu.fateczl.AGISSpring.Persistence.AlunoDAO;
import br.edu.fateczl.AGISSpring.Persistence.DisciplinaDAO;
import br.edu.fateczl.AGISSpring.Persistence.GenericDAO;
import br.edu.fateczl.AGISSpring.Persistence.MatriculaDAO;

@Controller
public class MatriculaController {
	
	@Autowired
	MatriculaDAO mdao;
	
	@Autowired
	GenericDAO gdao;

	@Autowired
	DisciplinaDAO ddao;

	@Autowired
	AlunoDAO adao;

	private List<Aluno> alunos = new ArrayList<>();
	private List<Disciplina> disciplinas = new ArrayList<>();
	
	private List<Matricula> matriculas = new ArrayList<>();
	private List<Disciplina> disciplinasNmatriculas = new ArrayList<>();
	
	private String RA;

	@RequestMapping(name = "matricula", value = "/matricula", method = RequestMethod.GET)
	public ModelAndView matriculaget(ModelMap model) {
		if (!alunos.isEmpty()) { alunos.removeAll(alunos); }
		if (!disciplinas.isEmpty()) { disciplinas.removeAll(disciplinas); }
		
		try {
			alunos.addAll(adao.listar());
			disciplinas.addAll(ddao.listar());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return new ModelAndView("matricula");
	}

	@RequestMapping(name = "matricula", value = "/matricula", method = RequestMethod.POST)
	public ModelAndView matriculapost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
		if (!matriculas.isEmpty()) { matriculas.removeAll(matriculas); }
		if (!disciplinasNmatriculas.isEmpty()) { disciplinasNmatriculas.removeAll(disciplinasNmatriculas); }
		
		String RA = allRequestParam.get("RA");
		String idDisciplina = allRequestParam.get("idDisciplina");

		try {
			switch (allRequestParam.get("botao")) {
			case "Buscar":
				matriculas.addAll(buscaMatriculas(RA));
				disciplinasNmatriculas.addAll(listarDisciplinasNmatriculadas(RA));
				this.RA = RA;
				break;
			case "Dispensar":
				dispensar(this.RA, Integer.parseInt(idDisciplina));
				break;
			case "Matricular":
				inserir(this.RA, Integer.parseInt(idDisciplina));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("matriculas", matriculas);
		model.addAttribute("disciplinas", disciplinasNmatriculas);
		
		return new ModelAndView("matricula");
	}

	private void dispensar(String RA, int idDisciplina) throws ClassNotFoundException, SQLException {
		Matricula m = new Matricula();
		
		for (Aluno a : alunos) {
			if (a.getRA().equals(RA)) {
				m.setAluno(a);
			} 
		}
		
		for (Disciplina d : disciplinas) {
			if (d.getIdDisciplina() == idDisciplina) {
				m.setDisciplina(d);
			} 
		}	
	
		mdao.crudMATRICULA("u", m);	
	}

	private void inserir(String RA, int idDisciplina) throws ClassNotFoundException, SQLException {
		Matricula m = new Matricula();
		
		for (Aluno a : alunos) {
			if (a.getRA().equals(RA)) {
				m.setAluno(a);
			} 
		}
		
		for (Disciplina d : disciplinas) {
			if (d.getIdDisciplina() == idDisciplina) {
				m.setDisciplina(d);
			} 
		}	
		mdao.crudMATRICULA("i", m);
	}

	private List<Disciplina> listarDisciplinasNmatriculadas(String RA) throws ClassNotFoundException, SQLException {
		for (Aluno a : alunos) {
			if (a.getRA().equals(RA)) {
				return mdao.listarDisciplinasNmatriculadas(a);

			}
		}
		return null;
	}

	private List<Matricula> buscaMatriculas(String RA) throws ClassNotFoundException, SQLException {
		for (Aluno a : alunos) {
			if (a.getRA().equals(RA)) {
				return mdao.buscaMatriculas(a);
			}
		}
		return null;
	}
}