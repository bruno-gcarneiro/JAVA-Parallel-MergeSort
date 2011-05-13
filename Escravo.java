import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;

public class Escravo implements ServerSlave{
	
	private static final int PORT = 0;
	private static final String HOST = "grad25";
	
	private int [] auxVet;
	private int auxTam;
	
	public static void main(String [] args){
		Escravo itSelf = new Escravo();
		try{
			ServerSlave slaveStub = (ServerSlave)UnicastRemoteObject.exportObject(itSelf, PORT);
			Registry myReg = LocateRegistry.getRegistry();
			ServerMaster mestre = (ServerMaster)myReg.lookup("Mestre");
			mestre.bindSlave(slaveStub);
		}catch(RemoteException e){
			System.out.println("Mestre not found");
		}catch(NotBoundException e){
			System.out.println(e);
		}
	}
	
	//public int [] ordena(int [] aVector) throws RemoteException{
	public ObjetoResposta ordena(int [] aVector) throws RemoteException{
		auxVet = aVector;
		auxTam = aVector.length;
		ObjetoResposta obj = new ObjetoResposta();
		
		obj.start();
		mergeSort(0,auxTam-1);
		obj.tempoOrdenacao = obj.time();
		
		obj.retorno = auxVet;
		return obj;
	}
	
	private void mergeSort(int begin, int end){
		if(begin<end){
			int half = (begin+end)/2;
			mergeSort(begin,half);
			mergeSort(half+1,end);
			merge(begin,end, half);
		}
	}
	
	private void merge(int begin, int end, int middle){
		int [] aux = new int[end - begin + 1];
		int j = begin;
		int l = j;
		int k = middle+1;
		
		for(int i = begin; i<=end ; i++){
			aux[i-begin] = auxVet[i];
		}
		
		while ( j<=middle && k<=end){
			if(aux[j-begin] < aux[k-begin]){
				auxVet[l] = aux[j-begin];
				j++;
			}else{
				auxVet[l] = aux[k-begin];
				k++;
			}
			l++;
		}
		
		while(j<=middle){
			auxVet[l] = aux[j-begin];
			l++;
			j++;
		}
		aux = null;
	}
}
