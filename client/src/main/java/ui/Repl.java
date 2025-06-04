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
        System.out.print("Welcome to 240 chess. Type Help to get started.\n"+
                EscapeSequences.SET_TEXT_COLOR_GREEN+"[LOGGED OUT]>>> ");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();

            if (status.status.equals("LOGGED_OUT")) {
                try {
                    result = preloginClient.eval(line);
                    if (!result.equals("quit")) {
                        System.out.print(result);
                    }
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_RED +msg +
                            EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED OUT]>>> ");
                }
            }
            if (status.status.equals("LOGGED_IN")){
                try {
                    result = postloginUI.eval(line);
                    if (!result.equals("quit")) {
                        System.out.print(result);
                    }
                } catch (Throwable e){
                    var msg = e.toString();
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_RED +msg +
                            EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> ");
                }
            }
        }
        System.out.println();
    }
}
