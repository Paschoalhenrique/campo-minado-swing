package br.com.aula.minado.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Tabuleiro implements CampoObservador {

	private int linhas;
	private int colunas;
	private static  int minas;

	private final static List<Campo> campos = new ArrayList<>();
	private final  List<Consumer<ResultadoEvento>> observadores =
			new ArrayList<>();

	@SuppressWarnings("static-access")
	public Tabuleiro(int linhas, int colunas, int minas) {
		this.linhas = linhas;
		this.colunas = colunas;
		this.minas = minas;

		gerarCampos();
		associarOsVizinhos();
		sortearMinas();
	}
	public void paraCadaCampo(Consumer<Campo>funcao) {
		campos.forEach(funcao);
	}
	
	
	public void registrarObservador(Consumer<ResultadoEvento> observador) {
		observadores.add(observador);
	}
	
	private void notificarObservador(boolean resultado) {
		observadores.stream()
		.forEach(o -> o.accept(new ResultadoEvento(resultado)));
	}
	//implementa��o do campo com try catch
	public void abrir(int linha, int coluna) {
		campos.parallelStream()
				.filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
				.findFirst()
				.ifPresent(c -> c.abrir());
	}
	
	
	
	
	public void alterarMarcacao(int linha, int coluna) {
		campos.parallelStream()
			.filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
			.findFirst()
			.ifPresent(c -> c.alternarMarcacao());
	}
	

	public int getLinhas() {
		return linhas;
	}

	public void setLinhas(int linhas) {
		this.linhas = linhas;
	}

	public int getColunas() {
		return colunas;
	}

	public void setColunas(int colunas) {
		this.colunas = colunas;
	}

	public int getMinas() {
		return minas;
	}

	public void setMinas(int minas) {
		Tabuleiro.minas = minas;
	}

	public List<Campo> getCampos() {
		return campos;
	}

	private void gerarCampos() {
		for (int linha = 0; linha < linhas; ++linha) {
			for (int coluna = 0; coluna < colunas; ++coluna) {
				Campo campo = new Campo(linha, coluna);
				campo.registrarObservadores(this);
				campos.add(campo);
			}
		}
	}

	private void associarOsVizinhos() {
		for (Campo c1 : campos) {
			for (Campo c2 : campos) {
				c1.adicionarVizinho(c2);
			}
		}
	}
	//corre��o da distribui��o das minas
	private static void sortearMinas() {
		long minasArmadas = 0;
		Predicate<Campo> minado = c -> c.isMinado();
		do {
			int aleatorio = (int) (Math.random() * campos.size());
			campos.get(aleatorio).minar();
			minasArmadas = campos.stream().filter(minado).count();
		} while (minasArmadas < minas);
	}

	public boolean objetivoAlcancado() {
		return campos.stream().allMatch(c -> c.objetivoAlcancado());
	}

	public void reiniciar() {
		campos.stream().forEach(c -> c.reiniciar());
		sortearMinas();
	}
	 
	@Override
	public void eventoOcorreu(Campo campo, CampoEvento evento) {
		
		if(evento == CampoEvento.EXPLODIR) {
			monstraMinas();
			notificarObservador(false);
		}else if(objetivoAlcancado()) {
			notificarObservador(true);
		}			
	}
	private void monstraMinas() {
		campos.stream()
		.filter(c-> c.isMinado())
		.filter(c -> !c.isMarcado())
		.forEach(c -> c.setAberto(true));
	}
	
	
	//Melhotando metodo toString
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append("  ");
//		for(int c = 0; c < colunas; c++) {
//			sb.append(" ");
//			sb.append(c);
//			sb.append(" ");
//		}
//		
//		sb.append("\n");
//		
//		int i = 0;
//		for(int l = 0; l < linhas; ++l) {
//			sb.append(l);
//			sb.append(" ");
//			for(int c = 0; c < colunas; ++c) {
//				sb.append(" ");
//				sb.append(campos.get(i));
//				sb.append(" ");
//				++i;
//			}
//			sb.append("\n");
//		}
//		return sb.toString();
	}
