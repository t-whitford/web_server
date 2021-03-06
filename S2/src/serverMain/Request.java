package serverMain;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class Request  {
	
	private Socket socket;
	private String query;
	private int requestType;
	private String body = null;
	private File file;
	private String ver;	
	private BufferedReader clientIn;
	private Reply reply;

	public Request(Socket socket, Reply reply) {
		this.socket = socket;
		this.reply = reply;
		this.reply.attachRequest(this);
	}
	
	public void getRequest() throws Exception
	{
		//Get first line
		query = getQueryFromSocket();



		try{
			//Split the query into parts and set appropriate parameters 
			String[] splitQuery = query.split("\\s");

			setFile(splitQuery[1]);
			setVer(splitQuery[2]);
			setRequestType(splitQuery[0]);

			if(requestType == 3) //If type is POST need to get body
				body = getBodyFromSocket();
		} catch (NullPointerException e){
			System.out.println("Null Pointer e");
		}
	}
	
	private String getQueryFromSocket() throws IOException{

		clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line1 = clientIn.readLine();
		return line1;

	}
	
	private void setRequestType(String type) throws Exception{
		
		switch(type) {
		case "GET" : 
			requestType = 1;
			break;
		case "HEAD" :
			requestType = 2;
			break;
		case "POST" :
			requestType = 3;
			break;
		default :
			throw new Exception("400\nBad Request");
		}
	}
	
	private String getBodyFromSocket() throws IOException{
		
		boolean cont = true;
		
		String line = null;
		
		int contentLength = 0;
		
		//Skip through to body. Get content length
		while(cont)
		{
			
			line = clientIn.readLine();
			if(line.contains("Content-Length"))
				contentLength = (new Integer(line.split("\\s")[1])).intValue();
				
			if(line.compareTo("") == 0)
				cont = false;
		}
		
		String body = "";
		for(int i = 0; i < contentLength; i++)
		{
			body += (char)clientIn.read();
		}
		return body;
	}

	public String getQuery() {
		return query;
	}
	
	public int getRequestType() {
		return requestType;
	}

	public String getBody() {
		return body;
	}
	
	public String getVer() {
		return ver;
	}

	private void setVer(String ver) {
		this.ver = ver;
	}

	public void setFile(String fileName){
		
		if(fileName.equalsIgnoreCase("/"))
			fileName = "index.html";
		
		file = new File(Server.getRootDirectory(), fileName);
	}
	
	public File getFileName()
	{
		return file;
	}

	public void checkFile() {
		System.out.println(getFileName());
		//TODO: this
	}
	
	public Reply getReply()
	{
		return reply;
	}
	
	public void setReply(Reply newReply)
	{
		reply = newReply;
	}
	

}
