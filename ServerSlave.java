import java.rmi.*;

public interface ServerSlave extends Remote{
  //public int [] ordena(int [] aVector) throws RemoteException;
	public ObjetoResposta ordena(int [] aVector) throws RemoteException;
}