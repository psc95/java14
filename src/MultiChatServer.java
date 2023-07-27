import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/* 멀티 스레드 적용되어서 다중 사용자 접속을 받을 수 있는 멀티 채팅 서버 프로그램 */

//멀티스레드 클래스
class EchoServerThread implements Runnable{
	Socket child;//소켓은 사용자와 통신하기 위해서 필요하다.
	
	InputStream is;
	ObjectInputStream ois;//입력 스트림
	
	OutputStream os; //바이트 출력 스트림의 최상위 추상클래스
	ObjectOutputStream oos;
	
	String receiveData; //사용자로 부터 전송받은 자료를 저장할 변수
	
	public EchoServerThread(Socket child) {
		this.child = child;
		
		try {
			System.out.println(child.getInetAddress()+"로 부터 연결 요청 받음.");
			
			is = child.getInputStream();
			ois = new ObjectInputStream(is);//입력스트림 생성
			
			os = child.getOutputStream();
			oos = new ObjectOutputStream(os);//출력스트림 생성
		}catch(IOException ie) {
			ie.printStackTrace();
		}
	}//생성자
	
	@Override
	public void run() {
		try {
			while(true) {
				receiveData = (String)ois.readObject();//사용자로 부터 전송된 데이터를 받아서 저장
				System.out.println(child.getInetAddress()+"의 메시지 : "+receiveData);
				oos.writeObject(receiveData);//다시 사용자에게 데이터를 송신함.
				oos.flush();//출력 스트림을 비움
			}//while
		}catch(Exception e) {
			System.out.println("클라이언트 강제 종료~");
		}finally {
			try {
				if(is != null) is.close();
				if(ois != null) ois.close();
				if(os != null) os.close();
				if(oos != null) oos.close();
				if(child != null) child.close();
			}catch(IOException ie) {ie.printStackTrace();}
		}
	}//스레드 문장을 구현
}//EchoServerThread class
public class MultiChatServer {
	ServerSocket server;//서버상에서 사용자 접속을 기다리기 위해서 필요한 클래스
	static final int PORT = 5000;//포트번호
	Socket child;
	
	public MultiChatServer() {
		try {
			server = new ServerSocket(PORT);
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);//프로그램 종료
		}
		
		System.out.println("***채팅 서버 ***");
		System.out.println("서버는 클라이언트 소켓의 접속을 기다리고 있음.");
		
		while(true) {
			try {
				child = server.accept();//사용자 접속이 이루어 지면 서버상에 소켓객체가 생성
				
				EchoServerThread childThread = new EchoServerThread(child);
				Thread th = new Thread(childThread);
				th.start();//멀티 쓰레드 시작
				
			}catch(Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}//무한루프문 =>while
	}//생성자
	
	public static void main(String[] args) {
		new MultiChatServer();//생성자 호출
	}
}
