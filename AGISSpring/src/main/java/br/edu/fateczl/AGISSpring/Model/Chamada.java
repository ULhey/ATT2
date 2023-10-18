package br.edu.fateczl.AGISSpring.Model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class Chamada {
	private int idChamada;
	private int faltas;	
	private Aluno aluno;
	private Disciplina disciplina;
}