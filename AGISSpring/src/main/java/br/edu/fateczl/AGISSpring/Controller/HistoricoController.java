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
import br.edu.fateczl.AGISSpring.Model.Historico;
import br.edu.fateczl.AGISSpring.Persistence.AlunoDAO;
import br.edu.fateczl.AGISSpring.Persistence.GenericDAO;
import br.edu.fateczl.AGISSpring.Persistence.HistoricoDAO;

@Controller
public class HistoricoController {
	
	@Autowired
	HistoricoDAO hdao;

	@Autowired
	GenericDAO gdao;

	@Autowired
	AlunoDAO adao;	

	private List<Aluno> alunos = new ArrayList<>();

	@RequestMapping(name = "historico", value = "/historico", method = RequestMethod.GET)
	public ModelAndView historicoget(ModelMap model) {
		if (!alunos.isEmpty()) {
			alunos.removeAll(alunos);
		}
		try {
			alunos.addAll(adao.listar());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return new ModelAndView("historico");
	}

	@RequestMapping(name = "historico", value = "/historico", method = RequestMethod.POST)
	public void historicopost(@RequestParam Map<String, String> allRequestParam, ModelMap model) {
		String RA = allRequestParam.get("RA");
		try {
			switch (allRequestParam.get("botao")) {
			case "Buscar":
				model.addAttribute("aluno", buscar(RA));
				model.addAttribute("historicos", chamaHistorico(RA));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Aluno buscar(String RA) throws ClassNotFoundException, SQLException {
		for (Aluno a : alunos) {
			if (a.getRA().equals(RA)) {
				return adao.buscar(a);
			}
		}
		return null;
	}
	
	public List<Historico> chamaHistorico(String RA)throws ClassNotFoundException, SQLException {
		for (Aluno a : alunos) {
			if (a.getRA().equals(RA)) {
				return hdao.geraHistorico(a);
			}
		}
		return null;
	}
}