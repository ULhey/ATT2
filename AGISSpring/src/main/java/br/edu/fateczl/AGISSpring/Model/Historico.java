package br.edu.fateczl.AGISSpring.Model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class Historico {
	private int idDisciplina;
	private String nomeDisciplina;
	private String nomeProfessor;
	private String notaFinal; 
	private int quantidadeFaltas;
}