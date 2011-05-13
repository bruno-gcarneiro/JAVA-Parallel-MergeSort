import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.rmi.registry.*;

public class Client{
	
	private static final int vetTam = 10;
	private static final String HOST = "grad25";
	
	public static void main(String [] args){
		
		int[] aVector = criaVetor();
		try{
			//for(int i=0;i<aVector.length;i++){
			//	System.out.println(aVector[i]);
			//    }
			//System.out.println(")");
			Registry myReg = LocateRegistry.getRegistry();
			ServerMaster mestre = (ServerMaster)myReg.lookup("Mestre");
			
			ObjetoResposta obj = new ObjetoResposta();
			obj.start();
			obj = mestre.ordena(aVector , 3);
			obj.tempoResposta = obj.time();
			int [] newVector = obj.retorno;
			
			//      for(int i=0;i<newVector.length;i++){
			//	System.out.println(newVector[i]);
			//      }
			obj.geraRelatorio();
			//System.out.println("Custo médio comunicação com escravos:" + obj.custoComunicacao);
			//System.out.println("Tempo de ordenação no mestre:" + obj.tempoOrdenacao);
			//long tmpOrdCli = fim - ini;
			//System.out.println("Tempo de ordenação no cliente:" + tmpOrdCli );
			//System.out.println("Custo comunicação com mestre:" + ( tmpOrdCli - obj.tempoOrdenacao ) );
			
		}catch(RemoteException e){
			System.out.println("Mestre not found");
		}catch(java.lang.InterruptedException e){
			System.out.println("Mestre not found!");
		}catch(NotBoundException e){
			System.out.println(e);
		}
	}
	
	public static int[]  criaVetor(){
		int[] aVector = new int[vetTam];
		Random r = new Random();
		for(int i=0;i<vetTam;i++){
			aVector[i] = r.nextInt(vetTam);
		}
		return aVector;
	}
}