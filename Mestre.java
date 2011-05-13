import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.lang.*;



public class Mestre implements ServerMaster {

	private static final int PORT = 0;
	private static final int HOST = 0;
	private ArrayList<ServerSlave> escravos = new ArrayList<ServerSlave>();
	private int numEscravos=0;

	private long tempOrdEscravos []; // soma dos tempos de ordenação observado pelos escravos
	private long tempOrdMestre; // soma dos tempos de ordenação nos escravos observado pelo mestre
	private long tempComEscravo [];//tempos de comunicacao mestre-escravo

	private class SlaveCaller extends Thread{
		private ServerSlave slave;
		private int [] interVet;
		private boolean ordenado;
		private ObjetoResposta obj;
		
		//feito por Bruno
		public long tempoOrdEscravo;
		public long tempoOrdThread;
		
		public SlaveCaller(int [] vet,ServerSlave slave){
			this.interVet = vet;
			this.slave = slave;
			this.ordenado = false;
			this.obj = new ObjetoResposta();
		}
		public int [] getVet(){
			return interVet;
		}
		public ObjetoResposta getObj(){
			return this.obj;
		}
		public void run(){
			try{
				//interVet = this.slave.ordena(interVet);
				obj.start();
				obj = this.slave.ordena(interVet);
				obj.tempoResposta = obj.time();
				//System.out.println("Tempo ordenação: " + obj.tempoOrdenacao);
				interVet = obj.retorno;
				this.tempoOrdEscravo = obj.tempoOrdenacao;
				
				//ordenado = true;
			}catch(RemoteException e){
				System.out.println("Escravo "+e+" nao ativo, nao sendo deletado agora.");
			}
		}
	}

	public static void main(String [] args){
		ServerMaster mySelf = new Mestre();
		
		try{
			ServerMaster stubMestre = (ServerMaster) UnicastRemoteObject.exportObject(mySelf, PORT);
			Registry rmiReg = LocateRegistry.getRegistry(); //Registry rmiReg = Registry.getRegistry(HOST);
			rmiReg.rebind("Mestre",stubMestre);
		}catch(RemoteException e){
			System.out.println("exporting object operation not possible");
		}
		
		System.out.println("UP");
	}
	
	public boolean bindSlave(ServerSlave objeto) throws RemoteException{
		if (escravos.contains(objeto)){return false;}
		escravos.add(objeto);
		this.numEscravos++;
		System.out.println("Escravo adicionado - Total:"+this.numEscravos);
		return true;
	}

	public boolean unbindSlave(ServerSlave objeto) throws RemoteException{
		if(escravos.remove(objeto)){
			this.numEscravos--;
			System.out.println("Escravo removido - Total:"+this.numEscravos);
			return true;
		}
		
		return false;
	}
	
	//public int [] ordena(int[] aVector, int maxEscravos) throws RemoteException{
		public ObjetoResposta ordena(int[] aVector, int maxEscravos) throws RemoteException, java.lang.InterruptedException{
		
		if(maxEscravos == 0 ){
			System.out.println("O cliente solicitou 0 escravos. Não é possível continuar.");
			return null;
		}
		
		if(this.numEscravos < maxEscravos){
			System.out.println("Não temos escravos suficientes.");
			return null;
		}

		this.tempOrdEscravos = new long[maxEscravos];
		this.tempComEscravo = new long[maxEscravos];
		
		SlaveCaller [] callers = new SlaveCaller [maxEscravos];
		int sharedVetTam = (aVector.length/maxEscravos);
		int [] auxVet = new int[sharedVetTam + 1];
		int [][] matrix = new int[maxEscravos][];
		int i;
		
		ObjetoResposta obj = new ObjetoResposta();
		SlaveCaller slaveThread;
		
		for ( i = 0 ; i < maxEscravos ; i++ )
		{
			sharedVetTam = (aVector.length/maxEscravos)+(i<(aVector.length%maxEscravos)?1:0);
			callers[i] = new SlaveCaller(copyVector(aVector,sharedVetTam*i,sharedVetTam),escravos.get(i));
		}
		
		obj.start();
		//for(ServerSlave escr:escravos){
		for ( i = 0 ; i < maxEscravos ; i++ )
		{
			callers[i].start();
		}
		
		for ( i = 0 ; i < maxEscravos ; i++ )
		{
			callers[i].join();
		}
		ObjetoResposta aux;
		for(i=0 ; i < maxEscravos ; i++){
			matrix[i] = callers[i].getVet();
			aux = callers[i].getObj();
			obj.addOrdEscravo(aux.tempoOrdenacao, i);
			obj.addRespEscravo(aux.tempoResposta, i);
			//tempOrdEscravos += callers[b].tempoOrdEscravo;
		}
		
		int[] ordenado = merge(matrix, aVector.length);
		long fim =obj.time();
		obj.retorno = ordenado;
		
		long custoTotal = 0;
		for ( i = 0 ; i < maxEscravos ; i++ )
		{
			custoTotal += tempComEscravo[i];
		}
		
		obj.tempoResposta = custoTotal / maxEscravos;
		obj.tempoOrdenacao = fim;
		return obj;
	}

	private int [] copyVector(int [] v,int pv, int tam)
	{
		int [] aux = new int [tam];
		System.arraycopy(v,pv,aux,0,tam);
		return aux;
	}

	private int [] merge(int [][] mat, int tam){
		int [] aux = new int[tam];
		int numSlave = mat.length;
		//System.out.println(numSlave);
		int [] slavesPointer = new int[numSlave];
		int b;
		int menor;
		for(int a=0 ; a<numSlave;a++){
			slavesPointer[a]=0;
		}
		for(int i=0 ; i<tam ; i++){
			b=0;
			while(b<numSlave && slavesPointer[b]==-1) b++;
			menor = b;
			while(b<numSlave){
				if(slavesPointer[b]!=-1){
					if(mat[b][slavesPointer[b]]<mat[menor][slavesPointer[menor]]){
						menor = b;
					}
				}
				b++;
			}
			aux[i] = mat[menor][slavesPointer[menor]];
			slavesPointer[menor]++;
			if(slavesPointer[menor]>=mat[menor].length){
				slavesPointer[menor]=-1;
			}
		}
		return aux;
	}
}
