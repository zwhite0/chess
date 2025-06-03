package ui;

import java.util.Scanner;

public class Repl {
    private PreloginUI preloginClient;
    private PostloginUI postloginUI;
    private Status status = new Status();
    private AuthTokenHolder authTokenHolder = new AuthTokenHolder();

    public Repl(String serverURL){
        status.status = "LOGGED_OUT";
        preloginClient = new PreloginUI(serverURL, status, authTokenHolder);
        postloginUI = new PostloginUI(serverURL,status,authTokenHolder);
    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type Help to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();

            if (status.status.equals("LOGGED_OUT")) {
                try {
                    result = preloginClient.eval(line);
                    System.out.print(result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
            if (status.status.equals("LOGGED_IN")){
                try {
                    result = postloginUI.eval(line);
                    System.out.print(result);
                } catch (Throwable e){
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
        }
        System.out.println();
    }
}
