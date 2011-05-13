import java.util.*;

public class ObjetoResposta implements java.io.Serializable
{
	
	public long tempoResposta; // custo de comunicação com outros processos se necessário.
	public long tempoOrdenacao; // custo total do tempo de ordenção do escravo
	public int[] retorno; // vetor ordenado
	
	public long calculaCustoComunicacao(){
		return (tempoResposta-tempoOrdenacao)/2;
	}

	private ArrayList<Long> tempoOrdenacaoEscravo = new ArrayList<Long>();
	public void addOrdEscravo(long tempo,int id){
		tempoOrdenacaoEscravo.add( id,tempo);
	}

	private ArrayList<Long> tempoRespostaEscravo = new ArrayList<Long>();
	public void addRespEscravo(long tempo, int id){
		tempoRespostaEscravo.add( id,tempo);
	}

	private long calculaCustoComunicacaoEscravo(int id){
		return (tempoRespostaEscravo.get(id)-tempoOrdenacaoEscravo.get(id))/2;
	}

	private long temp;
	public void start(){
		temp = System.nanoTime();
	}
	public long time(){
		return System.nanoTime() - temp;
	}

	public void geraRelatorio(){
		System.out.println("Tempo de resposta Total do mestre(do momento em que envia ate a resposta do mestre) :"+tempoResposta);
		System.out.println("Tempo de ordenacao Total do mestre :"+tempoOrdenacao);
		System.out.println("Tempo comunicacao cliente-mestre :"+calculaCustoComunicacao());
		for(int i=0;i<tempoRespostaEscravo.size();i++){
			System.out.println("Tempo de ordenacao do escravo "+(i)+" : "+tempoOrdenacaoEscravo.get(i));
			System.out.println("Tempo de resposta do escravo "+(i)+" : "+tempoRespostaEscravo.get(i));
			System.out.println("Tempo de comunicacao do escravo "+(i)+" : "+calculaCustoComunicacaoEscravo(i));
		}
	}

}