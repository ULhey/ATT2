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
import br.edu.fateczl.AGISSpring.Persistence.CursoDAO;
import br.edu.fateczl.AGISSpring.Persistence.GenericDAO;

@Controller
public class CursoController {
	
	@Autowired
	CursoDAO cdao;

	@Autowired
	GenericDAO gdao;

	private List<Curso> cursos = new ArrayList<>();

	@RequestMapping(name = "curso", value = "/curso", method = RequestMethod.GET)
	public ModelAndView cursoget(ModelMap model) {
		if (!cursos.isEmpty()) {
			cursos.removeAll(cursos);
		}
		try {
			cursos.addAll(cdao.listar());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return new ModelAndView("curso");
	}

	@RequestMapping(name = "curso", value = "/curso", method = RequestMethod.POST)
	public ModelAndView cursopost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
		String idCurso = allRequestParam.get("idCurso");
		String idCurso2 = allRequestParam.get("idCurso2");
		String nomeCurso = allRequestParam.get("nomeCurso");
		String cargaHorario = allRequestParam.get("cargaHorario");
		String sigla = allRequestParam.get("sigla");
		String turno = allRequestParam.get("turno");
		String ENADE = allRequestParam.get("ENADE");

		try {
			switch (allRequestParam.get("botao")) {
			case "Cadastrar":
				inserir(Integer.parseInt(idCurso), nomeCurso, cargaHorario, sigla, turno, Float.parseFloat(ENADE));
				break;
			case "Buscar":
				model.addAttribute("curso", buscar(Integer.parseInt(idCurso2)));
				break;
			case "Alterar":
				atualizar(Integer.parseInt(idCurso), nomeCurso, cargaHorario, sigla, turno, Float.parseFloat(ENADE));
				break;
			case "Excluir":
				remover(Integer.parseInt(idCurso));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			model.addAttribute("cursos", cursos);
		}
		return new ModelAndView("curso");
	}

	private void inserir(int idCurso, String nomeCurso, String cargaHorario, String sigla, String turno, float ENADE)
			throws ClassNotFoundException, SQLException {
		Curso c = new Curso();

		c.setIdCurso(idCurso);
		c.setNomeCurso(nomeCurso);
		c.setCargaHorario(cargaHorario);
		c.setSigla(sigla);
		c.setENADE(ENADE);

		cdao.crudCurso("i", c);
		cursos.add(c);
	}

	private void atualizar(int idCurso, String nomeCurso, String cargaHorario, String sigla, String turno, float ENADE)
			throws ClassNotFoundException, SQLException {
		for (Curso c : cursos) {
			if (c.getIdCurso() == idCurso) {

				c.setIdCurso(idCurso);
				c.setNomeCurso(nomeCurso);
				c.setCargaHorario(cargaHorario);
				c.setSigla(sigla);
				c.setENADE(ENADE);

				cdao.crudCurso("u", c);
				break;
			}
		}
	}

	public Curso buscar(int idCurso) throws ClassNotFoundException, SQLException {
		for (Curso c : cursos) {
			if (c.getIdCurso() == idCurso) {
				return cdao.buscar(c);
			}
		}
		return null;
	}

	private void remover(int idCurso) throws ClassNotFoundException, SQLException {
		for (Curso c : cursos) {
			if (c.getIdCurso() == idCurso) {
				cdao.crudCurso("d", c);
				cursos.remove(c);
				break;
			}
		}
	}
}