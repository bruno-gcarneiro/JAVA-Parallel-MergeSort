import java.rmi.*;

public interface ServerMaster extends Remote{
//public int [] ordena(int [] aVector, int maxEscravos) throws RemoteException;
	public ObjetoResposta ordena(int [] aVector, int maxEscravos) throws RemoteException, java.lang.InterruptedException ;
	public boolean bindSlave(ServerSlave objeto)throws RemoteException;
	public boolean unbindSlave(ServerSlave objeto)throws RemoteException;
}