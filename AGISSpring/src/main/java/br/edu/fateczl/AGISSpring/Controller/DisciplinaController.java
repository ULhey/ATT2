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

import br.edu.fateczl.AGISSpring.Model.Curso;
import br.edu.fateczl.AGISSpring.Model.Disciplina;
import br.edu.fateczl.AGISSpring.Persistence.CursoDAO;
import br.edu.fateczl.AGISSpring.Persistence.DisciplinaDAO;
import br.edu.fateczl.AGISSpring.Persistence.GenericDAO;

@Controller
public class DisciplinaController {
	
	@Autowired
	DisciplinaDAO ddao;
	
	@Autowired
	GenericDAO gdao;

	@Autowired
	CursoDAO cdao;

	private List<Disciplina> disciplinas = new ArrayList<>();
	private List<Curso> cursos = new ArrayList<>();

	@RequestMapping(name = "disciplina", value = "/disciplina", method = RequestMethod.GET)
	public ModelAndView disciplinaget(ModelMap model) {
		if (!disciplinas.isEmpty()) {
			disciplinas.removeAll(disciplinas);
		}
		if (!cursos.isEmpty()) {
			cursos.removeAll(cursos);
		}
		try {
			disciplinas.addAll(ddao.listar());
			cursos.addAll(cdao.listar());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		model.addAttribute("cursos", cursos);
		return new ModelAndView("disciplina");
	}

	@RequestMapping(name = "disciplina", value = "/disciplina", method = RequestMethod.POST)
	public ModelAndView disciplinapost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
		String idDisciplina = allRequestParam.get("idDisciplina");
		String nomeDisciplina = allRequestParam.get("nomeDisciplina");
		String horarioSemanal = allRequestParam.get("horarioSemanal");
		String aula = allRequestParam.get("aula");
		String tipoConteudo = allRequestParam.get("tipoConteudo");
		String professor = allRequestParam.get("professor");
		String semestreDisciplina = allRequestParam.get("semestreDisciplina");
		String idCurso = allRequestParam.get("idCurso");

		try {
			switch (allRequestParam.get("botao")) {
			case "Cadastrar":
				inserir(nomeDisciplina, horarioSemanal, Integer.parseInt(aula), tipoConteudo, professor, Integer.parseInt(semestreDisciplina),
						Integer.parseInt(idCurso));
				break;
			case "Buscar":
				model.addAttribute("disciplina", buscar(Integer.parseInt(idDisciplina)));
				break;
			case "Alterar":
				atualizar(Integer.parseInt(idDisciplina), nomeDisciplina, horarioSemanal, Integer.parseInt(aula),
						tipoConteudo, professor, Integer.parseInt(semestreDisciplina), Integer.parseInt(idCurso));
				break;
			case "Excluir":
				remover(Integer.parseInt(idDisciplina));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			model.addAttribute("cursos", cursos);
			model.addAttribute("disciplinas", disciplinas);
		}
		return new ModelAndView("disciplina");
	}

	private void inserir(String nomeDisciplina, String horarioSemanal, int aula, String tipoConteudo, String professor, int semestreDisciplina, int idCurso)
			throws ClassNotFoundException, SQLException {
		Disciplina d = new Disciplina();

		d.setIdDisciplina(1001 + disciplinas.size());
		d.setNomeDisciplina(nomeDisciplina);
		d.setHorarioSemanal(horarioSemanal);
		d.setAula(aula);
		d.setTipoConteudo(tipoConteudo);
		d.setProfessor(professor);
		d.setSemestreDisciplina(semestreDisciplina);

		for (Curso c : cursos) {
			if (c.getIdCurso() == idCurso) {
				d.setCurso(c);
			}
		}

		ddao.crudDisciplina("i", d);
		disciplinas.add(d);
	}

	private void atualizar(int idDisciplina, String nomeDisciplina, String horarioSemanal, int aula,
			String tipoConteudo, String professor, int semestreDisciplina, int idCurso) throws ClassNotFoundException, SQLException {
		for (Disciplina d : disciplinas) {
			if (d.getIdDisciplina() == idDisciplina) {

				d.setIdDisciplina(idDisciplina);
				d.setNomeDisciplina(nomeDisciplina);
				d.setHorarioSemanal(horarioSemanal);
				d.setAula(aula);
				d.setTipoConteudo(tipoConteudo);
				d.setProfessor(professor);
				d.setSemestreDisciplina(semestreDisciplina);

				for (Curso c : cursos) {
					if (c.getIdCurso() == idCurso) {
						d.setCurso(c);
					}
				}
				ddao.crudDisciplina("u", d);
				break;
			}
		}
	}

	private Disciplina buscar(int idDisciplina) throws ClassNotFoundException, SQLException {
		for (Disciplina d : disciplinas) {
			if (d.getIdDisciplina() == idDisciplina) {
				return ddao.buscar(d);
			}
		}
		return null;
	}

	private void remover(int idDisciplina) throws ClassNotFoundException, SQLException {
		for (Disciplina d : disciplinas) {
			if (d.getIdDisciplina() == idDisciplina) {
				ddao.crudDisciplina("d", d);
				disciplinas.remove(d);
				break;
			}
		}
	}
}