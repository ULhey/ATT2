CREATE DATABASE BancoAGIS

USE BancoAGIS

CREATE TABLE Aluno(
	RA VARCHAR(9) NOT NULL,
	CPF VARCHAR(11) NOT NULL,
	nome VARCHAR(100) NOT NULL,
	nomeSocial VARCHAR(100) NOT NULL,
	dataNasc DATE NOT NULL,
	telefone VARCHAR(11) NOT NULL,
	email VARCHAR(100) NOT NULL,
	emailCorp VARCHAR(100) NOT NULL,
	dataMedio DATE NOT NULL, 
	instituicaoMedio VARCHAR(100) NOT NULL,
	pontVestibular DECIMAL(7,2) NOT NULL,
	posiVestibular INT NOT NULL,
	anoInicio INT NOT NULL,
	semesInicio INT NOT NULL, 
	semesConclusao INT NOT NULL, 
	anoLimite INT NOT NULL, 
	PRIMARY KEY (RA)
)

CREATE TABLE Curso(
	idCurso INT CHECK (idCurso >= 1 AND idCurso <= 100) NOT NULL,
	nomeCurso VARCHAR(100) NOT NULL,
	cargaHorario VARCHAR(50) NOT NULL,
	sigla VARCHAR(5) NOT NULL,
	ENADE DECIMAL (7,2) NOT NULL,	
	PRIMARY KEY (idCurso)
)

CREATE TABLE MatriculaAlunoCurso(
	idMatricula INT IDENTITY (101, 1) NOT NULL,
	turno VARCHAR(100) NOT NULL,
	RA VARCHAR(9) NOT NULL,
	idCurso INT CHECK (idCurso >= 1 AND idCurso <= 100) NOT NULL,
	PRIMARY KEY (idMatricula),
	FOREIGN KEY (RA) REFERENCES Aluno(RA),
	FOREIGN KEY (idCurso) REFERENCES Curso(idCurso)
)

CREATE TABLE Disciplina(
	idDisciplina INT IDENTITY (1001, 1) NOT NULL,
	nomeDisciplina VARCHAR (100) NOT NULL,
	horarioSemanal VARCHAR(30) NOT NULL,
	aula INT NOT NULL,
	tipoConteudo VARCHAR (100) NOT NULL,
	semestreDisciplina INT NOT NULL,
	nomeProfessor VARCHAR(100) NOT NULL,
	idCurso INT NOT NULL,
	PRIMARY KEY (idDisciplina),
	FOREIGN KEY (idCurso) REFERENCES Curso(idCurso)
)

CREATE TABLE MatriculaAlunoDisciplina(
	idMatricula INT IDENTITY (201, 1) NOT NULL,
	ano INT NOT NULL,
	semestreMatricula INT NOT NULL,
	dataMatricula DATE NOT NULL,
	situacao VARCHAR (100) NOT NULL,
	notaFinal INT NOT NULL,
	RA VARCHAR(9) NOT NULL,
	idDisciplina INT NOT NULL,
	PRIMARY KEY (idMatricula),
	FOREIGN KEY (RA) REFERENCES Aluno(RA),
	FOREIGN KEY (idDisciplina) REFERENCES Disciplina(idDisciplina)
)

CREATE TABLE ChamadaLista(
	idChamada INT IDENTITY (20, 1) UNIQUE NOT NULL,
	dataChamada DATE UNIQUE NOT NULL,
	faltas INT UNIQUE NOT NULL,
	idDisciplina INT NOT NULL,
	RA VARCHAR(9) NOT NULL,
	PRIMARY KEY (idChamada),
	FOREIGN KEY (idDisciplina) REFERENCES Disciplina(idDisciplina),
	FOREIGN KEY (RA) REFERENCES aluno(RA)
)

-- PROCEDUREs
CREATE PROCEDURE validarCPF(@cpf VARCHAR(11), @valido BIT OUTPUT) AS
BEGIN
    DECLARE @soma1 INT,
					 @soma2 INT,
					 @i INT,
					 @cpfValido BIT

    SET @soma1 = 0
    SET @soma2 = 0
    SET @cpfValido = 1 

    IF (LEN(@cpf) = 11) BEGIN
        SET @i = 1

        WHILE (@i <= 9) BEGIN
            SET @soma1 = @soma1 + CAST(SUBSTRING(@cpf, @i, 1) AS INT) * (11 - @i)
            SET @soma2 = @soma2 + CAST(SUBSTRING(@cpf, @i, 1) AS INT) * (12 - @i)
            SET @i = @i + 1
        END

        SET @soma1 = 11 - (@soma1 % 11)
        IF (@soma1 >= 10)
		SET @soma1 = 0

        SET @soma2 = @soma2 + @soma1 * 2
        SET @soma2 = 11 - (@soma2 % 11)

        IF (@soma2 >= 10)
		SET @soma2 = 0

        IF (@soma1 <> CAST(SUBSTRING(@cpf, 10, 1) AS INT) OR @soma2 <> CAST(SUBSTRING(@cpf, 11, 1) AS INT))
            SET @cpfValido = 0
		END
     ELSE BEGIN
        SET @cpfValido = 0
    END

    SET @valido = @cpfValido
END

CREATE PROCEDURE validaIDADE(@DataNasc DATE, @valido BIT OUTPUT) AS
	DECLARE @i	DATE,
					@idade	 INT

	SET @i = (SELECT GETDATE())
	SET @idade = DATEDIFF(DAY, @DataNasc, @i) / 365

	IF (@idade >= 16)  BEGIN
		SET @valido = 1
	END
	ELSE BEGIN
		SET @valido = 0
	END

CREATE PROCEDURE criarRA(@ra AS VARCHAR(9) OUTPUT) AS
	DECLARE @cont INT =  0

	SET @ra = '' + YEAR(GETDATE())
	
	IF (MONTH(GETDATE()) >= 6) BEGIN
		SET @ra = @ra + '2'
	 END
	 ELSE  BEGIN
		SET @ra = @ra + '1'
	 END

	WHILE(@cont < 4) BEGIN
		SET @ra = @ra +  CAST(CAST(RAND() * 10 AS INT) AS CHAR(1))
		SET @cont = @cont + 1
	END

CREATE PROCEDURE criarEMAILCORP(@nome VARCHAR(100), @email VARCHAR(100) OUTPUT) AS
BEGIN
    DECLARE @pnome VARCHAR(50),
					 @unome VARCHAR(50),
					 @espaco INT,
					 @uespaco INT

    SET @uespaco = CHARINDEX(' ', REVERSE(@nome))

    IF (@uespaco > 0) BEGIN
        SET @unome = RIGHT(@nome, @uespaco - 1)
		END
    ELSE BEGIN
        SET @unome = @nome
    END

    SET @espaco = CHARINDEX(' ', @nome)

    IF (@espaco > 0) BEGIN
        SET @pnome = LEFT(@nome, @espaco - 1)
		END
    ELSE BEGIN
        SET @pnome = @nome
    END

    SET @email = @pnome + '.' + @unome + '@agis.com'
END
	
CREATE PROCEDURE calcularDATACONCLUSAO(@dataConclu DATE OUTPUT) AS
BEGIN
    SET @dataConclu = DATEADD(YEAR, 5,GETDATE())
END

-- CRUDs
CREATE PROCEDURE crudALUNO(@acao AS VARCHAR(1), @RA AS VARCHAR(9), @CPF AS VARCHAR(11), 
@nome AS VARCHAR(100), @nomeSocial AS VARCHAR(100), @dataNasc AS DATE, @telefone AS VARCHAR(9), 
@email AS VARCHAR (100), @dataMedio AS DATE, @instituicaoMedio AS VARCHAR(100), 
@pontVestibular AS DECIMAL(7,2), @posiVestibular AS INT, @idCurso AS INT, @turno AS VARCHAR(100), @Saida AS BIT OUTPUT) AS
	IF(@acao = 'i') BEGIN
		DECLARE @anoInicio INT,
						 @semesInicio INT = 1,
						 @semesConclusao INT = 0,
						 @emailCorp VARCHAR(100),
						 @validaIdade INT = 0,
						 @validaCpf INT = 0,
						 @anoLimite INT =0,
						 @dataConclu AS DATE = GETDATE()

		EXEC validarCPF @cpf, @validaCpf OUTPUT
		
		IF(@validaCpf = 1) BEGIN
			EXEC validaIDADE @DataNasc, @validaIdade OUTPUT

		IF(@validaIdade = 1) BEGIN
			EXEC criarEMAILCORP @nome,@emailCorp OUTPUT
	
			SET @anoInicio = CAST(YEAR(GETDATE()) AS INT)

			EXEC calcularDATACONCLUSAO @dataConclu OUTPUT
				
			SET @anoLimite = CAST(YEAR(@dataConclu) AS INT)

		IF (MONTH(GETDATE()) > 6) BEGIN
			SET @semesInicio =  2
			END
			ELSE BEGIN
			SET @semesInicio =  1
			END

		INSERT INTO Aluno VALUES (@RA, @CPF, @nome, @nomeSocial, @dataNasc, @telefone, @email, @emailCorp, @dataMedio, @instituicaoMedio, 
		@pontVestibular, @posiVestibular,@anoInicio,@semesInicio, @semesInicio, @anolimite) 

		INSERT INTO MatriculaAlunoCurso VALUES (@turno, @RA, @idCurso)
		SET @Saida = 1
		END ELSE BEGIN
		SET @Saida = 0
		END
		END ELSE BEGIN
		SET @Saida = 0
		END
		END

		IF(@acao = 'u') BEGIN
			UPDATE Aluno SET nome = @nome, nomeSocial = @nomeSocial, telefone = @telefone, email = @email WHERE RA = @RA
		END

		IF(@acao  = 'd') BEGIN
			DELETE Aluno WHERE RA = @RA
		END
	
CREATE PROCEDURE crudCURSO(@acao AS VARCHAR(1), @idCurso AS INT, @nomeCurso AS VARCHAR(100), @cargaHorario AS VARCHAR(50),  @sigla AS VARCHAR(5), 
@ENADE AS DECIMAL(7,2), @saida AS VARCHAR(10) OUTPUT) AS
	IF(@acao = 'i') BEGIN
		INSERT INTO Curso VALUES (@idCurso, @nomeCurso, @cargaHorario, @sigla, @ENADE)
	END

	IF(@acao = 'u') BEGIN
		UPDATE Curso SET nomeCurso = @nomeCurso, cargaHorario = @cargaHorario, sigla = @sigla, ENADE = @ENADE WHERE idCurso = @idCurso
	END

	IF(@acao  = 'd') BEGIN
		DELETE Curso WHERE idCurso = @idCurso
	END

CREATE PROCEDURE crudDISCIPLINA(@acao AS VARCHAR(1), @idDisciplina AS INT, @nomeDisciplina AS VARCHAR(100), @horarioSemanal AS VARCHAR(30), 
@aula AS INT, @tipoConteudo AS VARCHAR(100), @nomeProfessor AS VARCHAR(100), @semestreDisciplina AS INT, @idCurso AS INT, @saida AS VARCHAR(10) OUTPUT) AS
	IF(@acao = 'i') BEGIN
			INSERT INTO Disciplina VALUES (@nomeDisciplina, @horarioSemanal, @aula, @tipoConteudo, @semestreDisciplina ,@nomeProfessor, @idCurso)
	END

	IF(@acao = 'u') BEGIN
		UPDATE Disciplina SET nomeDisciplina = @nomeDisciplina, horarioSemanal = @horarioSemanal, aula = @aula,  
		tipoConteudo = @tipoConteudo, idCurso = @idCurso WHERE idDisciplina = @idDisciplina
	END

	IF(@acao  = 'd') BEGIN
		DELETE Disciplina WHERE  idDisciplina = @idDisciplina
	END

CREATE PROCEDURE crudMATRICULA(@acao AS VARCHAR(1), @idMatricula AS INT , @RA AS VARCHAR(9), @situacao AS VARCHAR(100), 
@idDisciplina AS INT, @saida AS VARCHAR(10) OUTPUT) AS
	DECLARE	@anoInicio INT = 0,
					@semesInicio INT = 0

	SET @anoInicio = CAST(YEAR(GETDATE()) AS INT)

	IF (MONTH(GETDATE()) > 6) BEGIN
	 SET @semesInicio =  2
	END
	ELSE BEGIN
	 SET @semesInicio =  1
	END

	IF(@acao = 'i') BEGIN
			INSERT INTO MatriculaAlunoDisciplina VALUES (@anoInicio, @semesInicio, getdate(), 'Cursando', 0, @RA, @idDisciplina)	
	END
	IF(@acao = 'u') BEGIN
		UPDATE MatriculaAlunoDisciplina SET situacao = 'Dispensado' WHERE idDisciplina = @idDisciplina and RA = @RA
	END

CREATE PROCEDURE crudCHAMADA(@acao AS VARCHAR(1), @idChamada AS INT , @faltas AS INT, 
@idDisciplina AS INT, @RA AS VARCHAR(9), @saida AS VARCHAR(10) OUTPUT) AS
	DECLARE	@dataChamada DATE = GETDATE()

	IF(@acao = 'i') BEGIN
			INSERT INTO ChamadaLista VALUES ( getdate(), @faltas, @idDisciplina, @RA)	
	END
	IF(@acao = 'u') BEGIN
		UPDATE ChamadaLista SET faltas = @faltas WHERE  RA = @RA AND idDisciplina = @idDisciplina --filtro com data
	END

--VIEWS SELECTs
CREATE VIEW MatriculaCurso AS
SELECT a.RA AS RA, a.CPF AS CPF, a.nome AS nome, a.nomeSocial AS nomeSocial , a.dataNasc AS dataNasc, a.telefone AS telefone, a.email AS email, a.emailCorp AS emailCorp, 
	a.dataMedio AS dataMedio, a.instituicaoMedio AS instituicaoMedio, 	a.pontVestibular AS pontVestibular, a.posiVestibular AS posiVestibular, 
	a.anoInicio AS anoInicio, a.semesInicio AS semesInicio, a.semesConclusao AS semesConclusao, a.anoLimite AS anoLimite, 
	m.turno AS turno, c.idCurso AS idCurso , c.nomeCurso AS nomeCurso, c.cargaHorario AS cargaHorario, c.sigla AS sigla, c.ENADE AS ENADE
FROM Aluno a INNER JOIN MatriculaAlunoCurso m ON m.RA = a.RA INNER JOIN Curso c ON c.idCurso = m.idCurso 

CREATE VIEW CursoDisciplina AS
SELECT d.idDisciplina AS idDisciplina, d.nomeDisciplina AS nomeDisciplina, d.horarioSemanal AS horarioSemanal, d.aula AS aula, d.tipoConteudo AS tipoConteudo, d.semestreDisciplina AS semestreDisciplina, d.nomeProfessor AS Professor,
		c.idCurso AS idCurso , c.nomeCurso AS nomeCurso, c.cargaHorario AS cargaHorario, c.sigla AS sigla, c.ENADE AS ENADE
FROM Disciplina  d INNER JOIN Curso c ON c.idCurso = d.idCurso

CREATE VIEW MatriculaDisciplina AS
SELECT d.idDisciplina AS idDisciplina, d.nomeDisciplina AS nomeDisciplina, d.horarioSemanal AS horarioSemanal, d.aula AS aula, d.tipoConteudo AS tipoConteudo, d.semestreDisciplina AS semestreDisciplina, d.nomeProfessor AS Professor,
		c.idCurso AS idCurso , c.nomeCurso AS nomeCurso, c.cargaHorario AS cargaHorario, c.sigla AS sigla, c.ENADE AS ENADE,
		MAD.RA AS RA
FROM Disciplina  d 	INNER JOIN Curso c ON c.idCurso = d.idCurso INNER JOIN MatriculaAlunoDisciplina MAD ON MAD.idDisciplina = D.idDisciplina

CREATE VIEW Matricula AS
SELECT a.RA AS RA, a.CPF AS CPF, a.nome AS nome, a.nomeSocial AS nomeSocial , a.dataNasc AS dataNasc, a.telefone AS telefone, a.email AS email, a.emailCorp AS emailCorp, 
	a.dataMedio AS dataMedio, a.instituicaoMedio AS instituicaoMedio, a.pontVestibular AS pontVestibular, a.posiVestibular AS posiVestibular, 
	a.anoInicio AS anoInicio, a.semesInicio AS semesInicio, a.semesConclusao AS semesConclusao, a.anoLimite AS anoLimite, 
    c.idCurso AS idCurso , c.nomeCurso AS nomeCurso, c.cargaHorario AS cargaHorario, c.sigla AS sigla, c.ENADE AS ENADE, 
    d.idDisciplina AS idDisciplina, d.nomeDisciplina AS nomeDisciplina, d.horarioSemanal AS horarioSemanal, d.aula AS aula, d.tipoConteudo AS tipoConteudo, d.semestreDisciplina AS semestreDisciplina, nomeProfessor AS Professor,
    mm.ano AS ano, mm.semestreMatricula AS semestreMatricula, mm.situacao AS situacao, mm.notaFinal AS notafinal, mm.idMatricula AS idMatricula, m.turno AS turno
FROM aluno a INNER JOIN MatriculaAlunoCurso m on m.RA = a.RA INNER JOIN Curso c on c.idCurso = m.idCurso	INNER JOIN MatriculaAlunoDisciplina mm on a.ra = mm.RA INNER JOIN Disciplina d on d.idDisciplina = mm.idDisciplina

CREATE VIEW Chamada AS
SELECT a.RA AS RA, a.CPF AS CPF, a.nome AS nome, a.nomeSocial AS nomeSocial , a.dataNasc AS dataNasc, a.telefone AS telefone, a.email AS email, a.emailCorp AS emailCorp, 
	a.dataMedio AS dataMedio, a.instituicaoMedio AS instituicaoMedio, a.pontVestibular AS pontVestibular, a.posiVestibular AS posiVestibular, 
	a.anoInicio AS anoInicio, a.semesInicio AS semesInicio, a.semesConclusao AS semesConclusao, a.anoLimite AS anoLimite, 
    c.idCurso AS idCurso , c.nomeCurso AS nomeCurso, c.cargaHorario AS cargaHorario, c.sigla AS sigla, c.ENADE AS ENADE, 
    d.idDisciplina AS idDisciplina, d.nomeDisciplina AS nomeDisciplina, d.horarioSemanal AS horarioSemanal, d.aula AS aula, d.tipoConteudo AS tipoConteudo, d.semestreDisciplina AS semestreDisciplina, nomeProfessor AS Professor,
	ch.idChamada AS idChamada, ch.dataChamada AS dataChamada, ch.faltas AS faltas
FROM aluno a INNER JOIN ChamadaLista ch ON ch.Ra = a.RA INNER JOIN Disciplina d ON d.idDisciplina = ch.idDisciplina INNER JOIN Curso c ON c.idCurso = d.idCurso

--TRIGGERs
CREATE TRIGGER MatriculaAuto
ON MatriculaAlunoCurso AFTER INSERT
AS
BEGIN
    INSERT INTO MatriculaAlunoDisciplina (ano, semestreMatricula, dataMatricula, situacao, notaFinal, RA, idDisciplina)
    SELECT
		CAST(YEAR(GETDATE()) AS INT), 
		CASE WHEN MONTH(GETDATE()) > 6 THEN '2' ELSE '1' END AS semestreMatricula,
		GETDATE(),
		'Cursando' AS situacao,
		0 AS notaFinal,
        i.RA AS RA,
        d.idDisciplina AS idDisciplina
    FROM  inserted i INNER JOIN Disciplina d ON  d.idCurso = i.idCurso
	WHERE semestreDisciplina = 1
END

ENABLE TRIGGER MatriculaAuto ON MatriculaAlunoCurso

--UDFs
CREATE FUNCTION GerarListaChamada(
		@DisciplinaID INT,
		@DataChamada DATE )

RETURNS @table TABLE (
		RAAluno INT,
		NomeAluno VARCHAR(100),
		NomeDisciplina VARCHAR(100))
AS
BEGIN
    INSERT INTO @table (RAAluno, NomeAluno, NomeDisciplina)
    SELECT
        A.RA AS RAAluno,
        A.nome AS NomeAluno,
        D.nomeDisciplina AS NomeDisciplina
    FROM MatriculaAlunoCurso MAC
    INNER JOIN Aluno A ON MAC.RA = A.RA
    INNER JOIN MatriculaAlunoDisciplina MAD ON A.RA = MAD.RA
    INNER JOIN Disciplina D ON MAD.idDisciplina = D.idDisciplina
    WHERE D.idDisciplina = @DisciplinaID
    AND MAD.ano <= YEAR(@DataChamada) 
    AND MAD.semestreMatricula <= 
        CASE
            WHEN MONTH(@DataChamada) <= 6 THEN 1
            ELSE 2
        END
    RETURN
END

CREATE  FUNCTION HistoricoAluno(@RA AS VARCHAR(9))
RETURNS @table TABLE ( 
	idDisciplina INT NOT NULL,
	nomeDisciplina VARCHAR (100) NOT NULL,
	nomeProfessor VARCHAR (100) NOT NULL,
	notaFinal VARCHAR (3) NOT NULL,
	quantidadeFaltas INT NOT NULL )
	
AS BEGIN
	INSERT INTO @table (idDisciplina, nomeDisciplina, nomeProfessor, notaFinal, quantidadeFaltas) 
	SELECT 
		d.idDisciplina AS idDisciplina,
		d.nomeDisciplina AS nomeDisciplina,
		d.nomeProfessor AS nomeProfessor,
		CASE WHEN mad.situacao = 'dispensado' THEN 'D' 
        ELSE
            CAST(mad.notaFinal AS VARCHAR(3))
        END AS notaFinal,
			SUM(ch.faltas) AS quantidadeFaltas
	FROM Aluno A
		INNER JOIN MatriculaAlunoCurso MAC ON A.RA = MAC.RA
		INNER JOIN MatriculaAlunoDisciplina MAD ON A.RA = MAD.RA
		INNER JOIN Disciplina D ON MAD.idDisciplina = D.idDisciplina
		INNER JOIN Curso C ON MAC.idCurso = C.idCurso
		INNER JOIN ChamadaLista CH ON CH.RA = MAD.RA
	WHERE MAD.RA = @RA AND CH.idDisciplina = MAD.idDisciplina AND (mad.situacao = 'Aprovado' OR mad.situacao = 'Dispensado')
	GROUP BY d.idDisciplina, d.nomeDisciplina, d.nomeProfessor, mad.situacao, mad.notaFinal
	RETURN
END

CREATE TRIGGER ProibirExclusaoMatricula
ON MatriculaAlunoDisciplina INSTEAD OF DELETE
AS
BEGIN
	ROLLBACK TRANSACTION
    RAISERROR('Não é permitido excluir a matrícula de um aluno em uma disciplina.', 16, 1);
END

ENABLE TRIGGER ProibirExclusaoMatricula ON MatriculaAlunoDisciplina


CREATE TRIGGER ProibirExclusaoChamada
ON ChamadaLista INSTEAD OF DELETE
AS
BEGIN
	ROLLBACK TRANSACTION
    RAISERROR('Não é permitido excluir uma chamada já realizada.', 16, 1);
END

ENABLE TRIGGER ProibirExclusaoChamada ON ChamadaLista

--SELECTs
SELECT * FROM Aluno
SELECT * FROM Curso
SELECT * FROM Disciplina
SELECT * FROM MatriculaAlunoDisciplina
SELECT * FROM MatriculaAlunoCurso
SELECT * FROM ChamadaLista

-- SELECTS VIEWS
SELECT * FROM MatriculaCurso
SELECT * FROM CursoDisciplina
SELECT * FROM MatriculaDisciplina
SELECT * FROM Matricula
SELECT * FROM Chamada
