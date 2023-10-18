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
import br.edu.fateczl.AGISSpring.Model.Chamada;
import br.edu.fateczl.AGISSpring.Model.Disciplina;
import br.edu.fateczl.AGISSpring.Persistence.AlunoDAO;
import br.edu.fateczl.AGISSpring.Persistence.ChamadaDAO;
import br.edu.fateczl.AGISSpring.Persistence.DisciplinaDAO;
import br.edu.fateczl.AGISSpring.Persistence.GenericDAO;

@Controller
public class ChamadaController {
	private int idDisicplina;
	
	@Autowired
	ChamadaDAO cdao;
	
	@Autowired
	GenericDAO gdao;
	
	@Autowired
	AlunoDAO adao;

	
	@Autowired
	DisciplinaDAO ddao;
	
	private List<Aluno> alunos = new ArrayList<>();
	private List<Chamada> chamada = new ArrayList<>();
	private List<Disciplina> disciplinas = new ArrayList<>();
	
	@RequestMapping(name = "chamada", value = "/chamada", method = RequestMethod.GET)
	public ModelAndView chamadaget(ModelMap model) {
		if (!disciplinas.isEmpty()) { disciplinas.removeAll(disciplinas); }
		if (!alunos.isEmpty()) { alunos.removeAll(alunos); }
		
		try {
			alunos.addAll(adao.listar());
			disciplinas.addAll(ddao.listar());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("disciplinas", disciplinas);
		
		return new ModelAndView("chamada");
	}
	
	@RequestMapping(name = "chamada", value = "/chamada", method = RequestMethod.POST)
	public ModelAndView chamadapost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
		if (!chamada.isEmpty()) { chamada.removeAll(chamada); }
		
		String RA = allRequestParam.get("RA");
		String idDisciplina = allRequestParam.get("idDisciplina");
		String falta = allRequestParam.get("faltas");
		
		try {
			switch (allRequestParam.get("botao")) {
			case "Realizar chamada":
				chamada.addAll(geraChamada(Integer.parseInt(idDisciplina)));
				this.idDisicplina = Integer.parseInt(idDisciplina);
				break;
			case "Salvar":
				inserir(this.idDisicplina, RA, Integer.parseInt(falta));
				break;
			case "Pesquisar":
				String data = allRequestParam.get("data");
				model.addAttribute("chamada2", chamadaHistorico(Integer.parseInt(idDisciplina), data));
				this.idDisicplina = Integer.parseInt(idDisciplina);
				break;
			case "Alterar":
				alterarFalta(this.idDisicplina, RA, Integer.parseInt(falta));
				break;	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("chamadas", chamada);
		model.addAttribute("disciplinas", disciplinas);
		
		return new ModelAndView("chamada");
	}
	
	private void inserir(int idDisicplina, String RA, int falta) throws ClassNotFoundException, SQLException {
		Chamada c = new Chamada();
		
		for (Aluno a : alunos) {
			if (a.getRA().equals(RA)) {
				c.setAluno(a);
			}
		}
		
		for (Disciplina d : disciplinas) {
			if (d.getIdDisciplina() == idDisicplina) {
				c.setDisciplina(d);
			}
		}
		
		c.setFaltas(falta);
		
		cdao.crudChamada("i", c);
	}

	private List<Chamada> geraChamada(int idDisciplina) throws ClassNotFoundException, SQLException {
		for (Disciplina d : disciplinas) {
			if (d.getIdDisciplina() == idDisciplina) {
				return cdao.geraChamada(d);
			} 
		}	
		return null;
	}
	
	private List<Chamada> chamadaHistorico(int idDisciplina, String data) throws ClassNotFoundException, SQLException {
		for (Disciplina d : disciplinas) {
			if (d.getIdDisciplina() == idDisciplina) {
				return cdao.buscaChamada(d, data);
			} 
		}	
		return null;
	}
	
	private void alterarFalta(int idDisicplina, String RA, int falta) throws ClassNotFoundException, SQLException {
		Chamada c = new Chamada();
		
		for (Aluno a : alunos) {
			if (a.getRA().equals(RA)) {
				c.setAluno(a);
			}
		}
		
		for (Disciplina d : disciplinas) {
			if (d.getIdDisciplina() == idDisicplina) {
				c.setDisciplina(d);
			}
		}
		
		c.setFaltas(falta);
		
		cdao.crudChamada("u", c);
	}
}